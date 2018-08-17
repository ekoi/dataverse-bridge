package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.ingest.ArchivedObject;
import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by akmi on 04/05/17.
 */
public class IngestToEasy implements IDataverseIngest {
    private String landingPage;
    private String doi;
    private static final Logger LOG = LoggerFactory.getLogger(IngestToEasy.class);
    private int timeout = 60000;
    int chunkSize = 262144000;


    @Override
    public ArchivedObject execute(File bagitZipFile, IRI colIri, String uid, String pw) {
        long checkingTimePeriod = 5000;
        ArchivedObject archivedObject = new ArchivedObject();
        StringBuffer sb = new StringBuffer("");
        String state = "";

        try {
            // 1. Set up stream for calculating MD5
            DigestInputStream dis = getDigestInputStream(bagitZipFile);

            // 2. Post first chunk bag to Col-IRI
            CloseableHttpClient http = BridgeHelper.createHttpClient(colIri.toURI(), uid, pw, getTimeout());
            CloseableHttpResponse response = BridgeHelper.sendChunk(dis, getChunkSize(), "POST", colIri.toURI(), "bag.zip.1", "application/octet-stream", http,
                    getChunkSize() < bagitZipFile.length());

            // 3. Check the response. If transfer corrupt (MD5 doesn't check out), report and exit.
            String bodyText = BridgeHelper.readEntityAsString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 201) {
                LOG.error("FAILED. Status = " + response.getStatusLine());
                LOG.error("Response body follows:");
                LOG.error(bodyText);
                //System.exit(2);
            }
            LOG.info("SUCCESS. Deposit receipt follows:");
            LOG.info(bodyText);

            Entry receipt = BridgeHelper.parse(bodyText);
            Link seIriLink = receipt.getLink("edit");
            URI seIri = seIriLink.getHref().toURI();

            int remaining = (int) bagitZipFile.length() - chunkSize;
            int count = 2;
            while (remaining > 0) {
                checkingTimePeriod += 1000;
                LOG.info(String.format("POST-ing chunk of %d bytes to SE-IRI (remaining: %d) ... ", chunkSize, remaining));
                response = BridgeHelper.sendChunk(dis, chunkSize, "POST", seIri, "bag.zip." + count++, "application/octet-stream", http, remaining > chunkSize);
                remaining -= chunkSize;
                bodyText = BridgeHelper.readEntityAsString(response.getEntity());
                if (response.getStatusLine().getStatusCode() != 200) {
                    LOG.error("FAILED. Status = " + response.getStatusLine());
                    LOG.error("Response body follows:");
                    LOG.error(bodyText);
                }
                LOG.info("SUCCESS.");
            }

            LOG.info("SUCCESS. Deposit receipt follows:");
            sb.append("<bodyText>");
            sb.append(bodyText);
            sb.append("</bodyText>");
            LOG.info(bodyText);

            // 4. Get the statement URL. This is the URL from which to retrieve the current status of the deposit.
            LOG.info("Retrieving Statement IRI (Stat-IRI) from deposit receipt ...");
            receipt = BridgeHelper.parse(bodyText);
            Link statLink = receipt.getLink("http://purl.org/net/sword/terms/statement");
            IRI statIri = statLink.getHref();
            LOG.info("Stat-IRI = " + statIri);
            state = trackDeposit(http, statIri.toURI(), checkingTimePeriod);
            // 5. Check statement every ten seconds (a bit too frantic, but okay for this test). If status changes:
            // report new status. If status is an error (INVALID, REJECTED, FAILED) or ARCHIVED: exit.
            LOG.info(state);
        } catch (Exception e) {
            LOG.error("ERROR: " + e.getMessage());
            sb.append("\nERROR: " + e.getMessage() + "\n");
            state = StateEnum.ERROR.toString();
            //send mail
        }
        archivedObject.setLandingPage(getLandingPage());
        archivedObject.setPid(getPid());
        archivedObject.setStatus(state);
        archivedObject.setAuditLogResponse(sb.toString());
        LOG.info("state: " + state);
        return archivedObject;
    }

    private DigestInputStream getDigestInputStream(File bagitZipFile) throws FileNotFoundException, NoSuchAlgorithmException {
        FileInputStream fis = new FileInputStream(bagitZipFile);
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new DigestInputStream(fis, md);
    }

    private String trackDeposit(CloseableHttpClient http, URI statUri, long checkingTimePeriod) throws Exception {
        CloseableHttpResponse response;
        String bodyText;
        LOG.info("Checking Time Period: " + checkingTimePeriod + " milliseconds.");
        LOG.info("Start polling Stat-IRI for the current status of the deposit, waiting {} seconds before every request ...", checkingTimePeriod);
        while (true) {
            Thread.sleep(checkingTimePeriod);
            LOG.info("Checking deposit status ... ");
            response = http.execute(new HttpGet(statUri));
            bodyText = BridgeHelper.readEntityAsString(response.getEntity());
            Feed statement = BridgeHelper.parse(bodyText);
            List<Category> states = statement.getCategories("http://purl.org/net/sword/terms/state");
            if (states.isEmpty()) {
                bodyText = "ERROR: NO STATE FOUND";
                LOG.error(bodyText);
                return bodyText;
            } else if (states.size() > 1) {
                bodyText = "ERROR: FOUND TOO MANY STATES (" + states.size() + "). CAN ONLY HANDLE ONE";
                LOG.error(bodyText);
                return (bodyText);
            } else {
                String state = states.get(0).getTerm();
                LOG.info(state);
                String doiNumber = "";
                if (state.equals(StateEnum.INVALID.toString()) || state.equals(StateEnum.REJECTED.toString()) || state.equals(StateEnum.FAILED.toString())) {
                    LOG.error("FAILURE. Complete statement follows:");
                    LOG.error(bodyText);
                    return (state);
                } else if (state.equals(StateEnum.ARCHIVED.toString())) {
                    List<Entry> entries = statement.getEntries();
                    LOG.info("SUCCESS. ");
                    if (entries.size() == 1) {
                        LOG.info("Deposit has been archived at: [" + entries.get(0).getId() + "]. ");

                        List<String> dois = getDois(entries.get(0));
                        int numDois = dois.size();
                        switch (numDois) {
                            case 1:
                                LOG.info(" With DOI: [" + dois.get(0) + "]. ");
                                setDoi(dois.get(0));
                                break;
                            case 0:
                                LOG.info("WARNING: No DOI found");
                                break;

                            default:
                                LOG.info("WARNING: More than one DOI found (" + numDois + "): ");
                                for (String doi : dois) {
                                    LOG.info(" [" + doi + "]");
                                }

                                break;
                        }
                        if (entries.size() == 1) {
                            LOG.info("Deposit has been archived at: <" + entries.get(0).getId() + ">. ");
                        }
                        String stateText = states.get(0).getText();
                        if (stateText != null && !stateText.isEmpty())
                            stateText = stateText.replace("ui/datasets/easy", "ui/datasets/id/easy");

                        LOG.info("DvBridgeDataset landing page will be located at: " + stateText);
                        LOG.info("Complete statement follows:");
                        LOG.info(bodyText);
                        setLandingPage(stateText);
                        return state;
                    }
                }
            }
        }
    }

    private String getLandingPage() {
        return landingPage;
    }

    private String getPid() {
        return doi;
    }

    private void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    private void setDoi(String doi) {
        this.doi = doi;
    }

    private List<String> getDois(Entry entry) {
        List<String> dois = new ArrayList<String>();

        List<Link> links = entry.getLinks("self");
        for (Link link : links) {
            IRI href = link.getHref();
            if (href.getHost().equals("doi.org")) {
                String path = href.getPath();
                String doi = path.substring(1); // skip leading '/'
                dois.add(doi);
            }
        }
        return dois;
    }

    private int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
