import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class TestRandomWordExtractor {

    private static final String BODY = "industry\n fretful, cross acidic separate.";

    private static final String INFO = "thirsty. redundant twig\n elegant, harass";

    private static String html = String.format("<html><body>%s<div class=\"infobox\">%s</div></body></html>", BODY, INFO);

    @Test
    public void testRandomExtractFromBody() {
        String word = null;
        InputStream stream = null;
        try {
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8.name()))), "", ExtractorOptions.BODY);
        } catch (IOException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        Assert.assertTrue("Wrong word extracted from body", BODY.contains(word) || INFO.contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        closeStream(stream, "Failed to close stream from body");
    }

    @Test
    public void testRandomExtractFromInfo() {
        String word = null;
        InputStream stream = null;
        try {
            word = RandomWordExtractor.extract((stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8.name()))), "", ExtractorOptions.INFO_TABLE);
        } catch (UnsupportedEncodingException e) {
            Assert.fail("UnsupportedEncodingException thrown");
        }
        Assert.assertTrue("Wrong word extracted from info", INFO.contains(word) && !BODY.contains(word));
        Assert.assertTrue("Word too long", word.length() > 5);
        closeStream(stream, "Failed to close stream from info");
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
