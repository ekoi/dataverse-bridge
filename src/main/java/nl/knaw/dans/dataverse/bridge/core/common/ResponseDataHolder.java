package nl.knaw.dans.dataverse.bridge.core.common;

import nl.knaw.dans.dataverse.bridge.core.util.StateEnum;
import nl.knaw.dans.dataverse.bridge.exception.BridgeException;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/*
    @author Eko Indarto
 */
public class ResponseDataHolder {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseDataHolder.class);
    private String state;
    private String pid;
    private String landingPage;
    private String feedXml;

    private static Abdera abdera = null;

    public ResponseDataHolder(){}

    public ResponseDataHolder(InputStream content) throws BridgeException {
        init(content);
    }
    public static synchronized Abdera getInstance() {
        if (abdera == null) {
            abdera = new Abdera();
        }
        return abdera;
    }

    private void init(InputStream content) throws BridgeException{
        try {
            feedXml = IOUtils.toString(content, "UTF-8");
            LOG.info(feedXml);
        } catch (IOException e) {
            throw new BridgeException(e.getMessage(), e, this.getClass());
        }
        Parser parser = getInstance().getParser();
        Document<Feed> doc = parser.parse(new ByteArrayInputStream(feedXml.getBytes()));
        Feed feed = doc.getRoot();
        List<Category> categories = feed.getCategories("http://purl.org/net/sword/terms/state");
        if (categories.size() != 1)
            throw new BridgeException("Zero or multiples categories. Catagories size:  " + categories.size(), this.getClass());
        else {
            Category category = categories.get(0);
            state = category.getTerm();
            if (state.equals(StateEnum.ARCHIVED.toString())){
                List<Entry> entries = feed.getEntries();
                if (entries.size() != 1) {
                    throw new BridgeException("Categories size is not equals 1. Size: " + categories.size(), this.getClass());
                } else {
                    Entry entry = entries.get(0);
                    landingPage = entry.getLink("self").getHref().toString();
                    pid = entry.getLink("self").getHref().getPath().replaceFirst("/", "");
                }
            } else {
                    String msg = "State is : " + state + " feed: " + feedXml;
                    LOG.debug(msg);
            }
        }
    }

    public String getState() {
        return state;
    }

    public String getPid() {
        return pid;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public String getFeedXml() {
        return feedXml;
    }

}
