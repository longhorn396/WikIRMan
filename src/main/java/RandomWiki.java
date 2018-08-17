import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RandomWiki implements Closeable {

    public static final String RANDOM_URL = "https://en.wikipedia.org/wiki/Special:Random";

    private InputStream page;
    private HttpURLConnection conn;

    public RandomWiki() throws IOException {
        loadRandomPage();
    }

    public void refresh() throws IOException {
        this.close();
        this.loadRandomPage();
    }

    public void close() throws IOException {
        page.close();
        page = null;
        conn.disconnect();
        conn = null;
    }

    private void loadRandomPage() throws IOException {
        assert page == null;
        assert conn == null;
        conn = (HttpURLConnection) new URL(RANDOM_URL).openConnection();
        conn.setInstanceFollowRedirects(true);//Ensures that it follows the redirection
        conn.connect();
        page = conn.getInputStream();
    }

    public InputStream getPage() {
        return this.page;
    }

    public String getURI() {
        return this.conn.getURL().toString();
    }

    HttpURLConnection getConn() {
        return this.conn;
    }

}
