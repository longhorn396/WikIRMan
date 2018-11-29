import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API for getting random webpages from Wikipedia
 */
public class RandomWiki implements Closeable {

    /**
     * The URL of the "Random Article" link for Wikipedia
     */
    public static final String RANDOM_URL = "https://en.wikipedia.org/wiki/Special:Random";

    private InputStream page;
    private HttpURLConnection conn;

    /**
     * Loads a random Wikipedia webpage
     */
    public RandomWiki() {
        try {
            loadRandomPage();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Closes the current connection and gets a new webpage
     *
     * @throws IOException if the connection fails to close
     */
    public void refresh() throws IOException {
        this.close();
        this.loadRandomPage();
    }

    /**
     * Closes the current connection
     * @throws IOException if the connection fails to close
     */
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

    /**
     * Getter method for the webpage's InputStream
     * @return the InputStream for the webpage
     */
    public InputStream getPage() {
        return this.page;
    }

    /**
     * Getter method for the current webpage's location
     * @return the current webpage's URI
     */
    public String getURI() {
        return this.conn.getURL().toString();
    }

    HttpURLConnection getConn() {
        return this.conn;
    }

}
