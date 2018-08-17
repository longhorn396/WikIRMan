import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class TestRandomWiki {

    @Test()
    public void testRandomWiki() {
        RandomWiki rw = getNewRandomWiki();
        Assert.assertNotNull(rw);
        Assert.assertNotNull(rw.getPage());
        Assert.assertNotNull(rw.getConn());
        Assert.assertEquals(rw.getURI(), rw.getConn().getURL().toString());
        Assert.assertNotEquals(RandomWiki.RANDOM_URL, rw.getURI());
        closeRandomWiki(rw);
    }

    @Test
    public void testRandomWikiRefresh() {
        RandomWiki rw = getNewRandomWiki();
        InputStream page = rw.getPage();
        HttpURLConnection conn = rw.getConn();
        String url = rw.getConn().getURL().toString();
        try {
            rw.refresh();
        } catch (IOException e) {
            closeRandomWiki(rw);
        }
        Assert.assertNotEquals(page, rw.getPage());
        Assert.assertNotEquals(conn, rw.getConn());
        Assert.assertNotEquals(url, rw.getConn().getURL().toString());
        closeRandomWiki(rw);
    }

    private RandomWiki getNewRandomWiki() {
        RandomWiki rw = null;
        try {
            rw = new RandomWiki();
        } catch (IOException ioe) {
            closeRandomWiki(rw);
            ioe.printStackTrace();
            Assert.fail("RandomWiki constructor threw IOException");
        }
        return rw;
    }

    private void closeRandomWiki(RandomWiki rw) {
        try {
            rw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Closing RandomWiki failed");
        }
    }

}
