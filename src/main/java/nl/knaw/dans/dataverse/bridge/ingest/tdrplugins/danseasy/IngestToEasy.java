package nl.knaw.dans.dataverse.bridge.ingest.tdrplugins.danseasy;

import nl.knaw.dans.dataverse.bridge.core.util.BridgeHelper;
import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import nl.knaw.dans.dataverse.bridge.ingest.IDataverseIngest;
import nl.knaw.dans.dataverse.bridge.ingest.ResponseDataHolder;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
    public ResponseDataHolder execute(File bagitZipFile, IRI colIri, String uid, String pw) throws BridgeException{
        long checkingTimePeriod = 5000;
        ResponseDataHolder responseDataHolder = null;
        try {
            DigestInputStream dis = getDigestInputStream(bagitZipFile);

            CloseableHttpClient http = BridgeHelper.createHttpClient(colIri.toURI(), uid, pw, getTimeout());
            CloseableHttpResponse response = BridgeHelper.sendChunk(dis, getChunkSize(), "POST", colIri.toURI(), "bag.zip.1", "application/octet-stream", http,
                    getChunkSize() < bagitZipFile.length());

            String bodyText = BridgeHelper.readEntityAsString(response.getEntity());
            if (response.getStatusLine().getStatusCode() != 201) {
                LOG.error("FAILED. Status = " + response.getStatusLine());
                LOG.error("Response body follows:");
                LOG.error(bodyText);
                throw new BridgeException("Status = " + response.getStatusLine() + ". Response body follows:" + bodyText, this.getClass());
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
            LOG.info(bodyText);

            LOG.info("Retrieving Statement IRI (Stat-IRI) from deposit receipt ...");
            receipt = BridgeHelper.parse(bodyText);
            Link statLink = receipt.getLink("http://purl.org/net/sword/terms/statement");
            IRI statIri = statLink.getHref();
            LOG.info("Stat-IRI = " + statIri);
            responseDataHolder = trackDeposit(http, statIri.toURI(), checkingTimePeriod);
            LOG.info(responseDataHolder.getState());
        } catch (FileNotFoundException e) {
            LOG.error("FileNotFoundException: " + e.getMessage());
            new BridgeException("execute - FileNotFoundException, msg: " + e.getMessage(), e, this.getClass());
        } catch (NoSuchAlgorithmException e) {
            LOG.error("NoSuchAlgorithmException: " + e.getMessage());
            new BridgeException("execute - NoSuchAlgorithmException, msg: " + e.getMessage(), e, this.getClass());
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException: " + e.getMessage());
            new BridgeException("execute - URISyntaxException, msg: " + e.getMessage(), e, this.getClass());
        } catch (IOException e) {
            new BridgeException("execute - IOException, msg: " + e.getMessage(), e, this.getClass());
        }
        return responseDataHolder;
    }

    private DigestInputStream getDigestInputStream(File bagitZipFile) throws FileNotFoundException, NoSuchAlgorithmException {
        FileInputStream fis = new FileInputStream(bagitZipFile);
        MessageDigest md = MessageDigest.getInstance("MD5");
        return new DigestInputStream(fis, md);
    }

    private ResponseDataHolder trackDeposit(CloseableHttpClient http, URI statUri, long checkingTimePeriod) throws BridgeException {
        ResponseDataHolder responseDataHolder;
        CloseableHttpResponse response;
        LOG.info("Checking Time Period: " + checkingTimePeriod + " milliseconds.");
        LOG.info("Start polling Stat-IRI for the current status of the deposit, waiting {} seconds before every request ...", checkingTimePeriod);
        while (true) {
            try {
                Thread.sleep(checkingTimePeriod);
                LOG.info("Checking deposit status ... ");
                response = http.execute(new HttpGet(statUri));
                responseDataHolder = new ResponseDataHolder(response.getEntity().getContent());
                String state = responseDataHolder.getState();
                LOG.info("State: " + state);
                if (state.equals(StateEnum.ARCHIVED.toString()) || state.equals(StateEnum.INVALID.toString())
                        || state.equals(StateEnum.REJECTED.toString()) || state.equals(StateEnum.FAILED.toString()))
                    return responseDataHolder;
            } catch (InterruptedException e) {
                throw new BridgeException("InterruptedException ", e, this.getClass());
            } catch (ClientProtocolException e) {
                throw new BridgeException("ClientProtocolException ", e, this.getClass());
            } catch (IOException e) {
                throw new BridgeException("IOException ", e, this.getClass());
            }
        }
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
