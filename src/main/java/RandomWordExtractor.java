import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to extract a random word from a document using Jsoup.
 */
public class RandomWordExtractor {

    /**
     * Utility method to extract a random word from a document using Jsoup.
     *
     * @param in      the InputStream for the document
     * @param baseURI URI used by Jsoup to resolve links
     * @param eo      ExtractorOptions enum
     * @return random string of alphabetic characters from the document or null
     */
    public static String extract(InputStream in, String baseURI, ExtractorOptions eo) {
        Document doc = null;
        try {
            doc = Jsoup.parse(in, null, baseURI);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Elements terms = null;
        switch (eo) {
            case BODY:
                terms = doc.getElementsByTag("body");
                break;
            case INFO_TABLE:
                terms = doc.getElementsByClass("infobox");
                break;
        }
        List<String> words = Arrays.asList(terms.text().split("\\b"));
        words = words.stream().map(String::toLowerCase).filter(word -> word.length() > 5 && word.matches("[a-z]+?")).collect(Collectors.toList());
        if (words.size() > 0) {
            return words.get((int) Math.floor(Math.random() * words.size()));
        }
        return null;
    }

}

