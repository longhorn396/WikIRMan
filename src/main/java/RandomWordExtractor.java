import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RandomWordExtractor {

    public static String extract(InputStream in, String baseURI, ExtractorOptions eo) {
        Document doc = null;
        try {
            doc = Jsoup.parse(in, null, baseURI);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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
        words = words.stream().filter(word -> word.length() > 5).collect(Collectors.toList());
        if (words.size() > 0) {
            return words.get((int) Math.floor(Math.random() * words.size())).toLowerCase();
        }
        return null;
    }

}

