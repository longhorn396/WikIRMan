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
        String word = null;
        InputStream stream = null;
        try {
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(HTML.getBytes(StandardCharsets.UTF_8.name()))), "", ExtractorOptions.BODY);
        } catch (IOException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        Assert.assertTrue("Wrong word extracted from body", BODY.toLowerCase().contains(word) || INFO.toLowerCase().contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        Assert.assertEquals("Not lower case", word, word.toLowerCase());
        closeStream(stream, "Failed to close stream from body");
    }

    @Test
    public void testRandomExtractFromInfo() {
        String word = null;
        InputStream stream = null;
        try {
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(HTML.getBytes(StandardCharsets.UTF_8.name()))), "", ExtractorOptions.INFO_TABLE);
        } catch (UnsupportedEncodingException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        Assert.assertTrue("Wrong word extracted from info", INFO.toLowerCase().contains(word) && !BODY.toLowerCase().contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        Assert.assertEquals("Not lower case", word, word.toLowerCase());
        closeStream(stream, "Failed to close stream from info");
    }

    @Test
    public void testRandomExtractIsTypable() {
        String word = null;
        InputStream stream = null;
        try {
            String others = "<html><body>célèbre c’est chef-d’œuvre grâce doppelgänger résistance</body></html>";
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(others.getBytes(StandardCharsets.UTF_8.name()))), "", ExtractorOptions.BODY);
        } catch (IOException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        Assert.assertNull("", word);
        closeStream(stream, "Failed to close stream from Typable");
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
