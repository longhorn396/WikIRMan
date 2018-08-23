import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TestRandomWordExtractor {

    private static final String BODY = "Industry\n fretful, cross acidic separate.";

    private static final String INFO = "thirsty. Redundant twig\n elegant, harass";

    private static String HTML = String.format("<html><body>%s<div class=\"infobox\">%s</div></body></html>", BODY, INFO);

    @Test
    public void testRandomExtractFromBody() {
        String word = testRandomExtract(HTML, ExtractorOptions.BODY, "body");
        Assert.assertTrue("Wrong word extracted from body", BODY.toLowerCase().contains(word) || INFO.toLowerCase().contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        Assert.assertEquals("Not lower case", word, word.toLowerCase());
    }

    @Test
    public void testRandomExtractFromInfo() {
        String word = testRandomExtract(HTML, ExtractorOptions.INFO_TABLE, "info");
        Assert.assertTrue("Wrong word extracted from info", INFO.toLowerCase().contains(word) && !BODY.toLowerCase().contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        Assert.assertEquals("Not lower case", word, word.toLowerCase());
    }

    @Test
    public void testRandomExtractIsTypable() {
        String others = "<html><body>célèbre c’est chef-d’œuvre grâce doppelgänger résistance</body></html>";
        String word = testRandomExtract(others, ExtractorOptions.BODY, "typable");
        Assert.assertNull("", word);
    }

    private String testRandomExtract(String from, ExtractorOptions eo, String msg) {
        String word = null;
        InputStream stream = null;
        try {
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(from.getBytes(StandardCharsets.UTF_8.name()))), "", eo);
        } catch (IOException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        closeStream(stream, String.format("Failed to close stream from %s", msg));
        return word;
    }

    private void closeStream(InputStream stream, String msg) {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(msg);
        }
    }

}
