/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Map;

import junit.framework.TestCase;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlListener;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;

/**
 * @author kotelnikov
 */
public class HtmlParserTest extends TestCase {

    /**
     * @param name
     */
    public HtmlParserTest(String name) {
        super(name);
    }

    protected ICharStream newStream(String str) {
        return new CharStream(str);
        // return new StringBufferCharStream(str);
    }

    protected IXmlParser newXmlParser() {
        return new HtmlParser();
    }

    private String parseAndSerialize(String str) {
        XmlSerializer listener = new XmlSerializer();
        listener.setSortAttributes(false);
        IXmlParser parser = newXmlParser();
        ICharStream stream = newStream(str);
        parser.parse(stream, listener);
        return listener.toString();
    }

    public void test() {
        testParser("cd", "<html><body><p>cd</p></body></html>");
        testParser("<i>b</i>c", "<html><body><p><i>b</i>c</p></body></html>");
        testParser("a<s>b</s>c", "<html><body><p>a<s>b</s>c</p></body></html>");
    }

    public void testAttributes() {
        testAttributeWithEntities("a&#x27;b", "a'b");
        testAttributeWithEntities("a'b", "a'b");
        testParser(
            "<div prop=\"value\"/>",
            "<html><body><div prop='value'></div></body></html>");
        testParser(
            "<div prop=\"a'b\"/>",
            "<html><body><div prop='a&#x27;b'></div></body></html>");
    }

    private void testAttributeWithEntities(String attr, final String control) {
        IXmlParser parser = newXmlParser();
        parser.parse(
            newStream("<div prop=\"" + attr + "\"/>"),
            new XmlListener() {
                @Override
                public void beginElement(
                    String tagName,
                    Map<String, String> attributes,
                    Map<String, String> namespaces) {
                    if ("div".equals(tagName)) {
                        String value = attributes.get("prop");
                        assertEquals(control, value);
                    }
                }
            });
    }

    public void testExamplesFromJsoup() {
        // Tests
        testParser(
            "<body><p><textarea>one<p>two",
            "<html><body><p><textarea>one</textarea></p><p>two</p></body></html>");
        testParser(
            "<div > <a name=\"top\"></a ><p id=1 >Hello</p></div>",
            "<html><body><div><a name='top'></a><p id='1'>Hello</p></div></body></html>");
        testParser(
            "foo <b>bar</b> baz",
            "<html><body><p>foo <b>bar</b> baz</p></body></html>");
        testParser(
            "<div title='Surf &amp; Turf'>Reef &amp; Beef</div>",
            "<html><body><div title='Surf &#x26; Turf'>Reef &#x26; Beef</div></body></html>");
        testParser(
            "<meta name=keywords /><link rel=stylesheet /><title>jsoup</title><p>Hello world</p>",
            ""
                + "<html>"
                + "<head>"
                + "<meta name='keywords'></meta>"
                + "<link rel='stylesheet'></link>"
                + "<title>jsoup</title>"
                + "<body><p>Hello world</p></body>"
                + "</head>"
                + "</html>");
        testParser(""
            + "<meta name=keywords>"
            + "<link rel=stylesheet>"
            + "<title>jsoup"
            + "<p>Hello world", ""
            + "<html>"
            + "<head>"
            + "<meta name='keywords'></meta>"
            + "<link rel='stylesheet'></link>"
            + "<title>jsoup</title>"
            + "<body><p>Hello world</p></body>"
            + "</head>"
            + "</html>");
        // FIXME:
        testParser(
            "<body><p><select><option>One<option>Two</p><p>Three</p>",
            "<html><body><p><select><option>One<option>Two</option></option></select></p><p>Three</p></body></html>");
    }

    private void testParser(String str) {
        testParser(str, str);
    }

    private void testParser(String str, String control) {
        String test1 = parseAndSerialize(str);
        assertEquals(control, test1);
        String test2 = parseAndSerialize(test1);
        assertEquals(control, test2);
    }

    public void testScriptsAndStyles() {
        testParser(
            "a<script>b</script>c",
            "<html><body><p>a<script>b</script>c</p></body></html>");
        testParser(
            "before<script>toto<a href=''>it is not a tag <>> titi",
            "<html><body><p>before<script>toto&#x3c;a href=''&#x3e;it is not a tag &#x3c;&#x3e;&#x3e; titi</script></p></body></html>");

        testParser(
            "<script><a>text",
            "<html><head><script>&#x3c;a&#x3e;text</script></head></html>");
        testParser(
            "before<script><a>text",
            "<html><body><p>before<script>&#x3c;a&#x3e;text</script></p></body></html>");
        testParser(
            "<script>toto",
            "<html><head><script>toto</script></head></html>");
        testParser(
            "before<script>toto<a href=''>it is not a tag <>> titi",
            "<html><body><p>before<script>toto&#x3c;a href=''&#x3e;it is not a tag &#x3c;&#x3e;&#x3e; titi</script></p></body></html>");

        testParser(
            "before<script>toto[<!-- This is a comment -->] <a href=''>it is not a tag <>> titi",
            "<html><body><p>before<script>toto[] &#x3c;a href=''&#x3e;it is not a tag &#x3c;&#x3e;&#x3e; titi</script></p></body></html>");
    }

    public void testSerializeDeserialize() {
        testParser(
            "<p>Lorem <p>Ipsum ",
            "<html><body><p>Lorem </p><p>Ipsum </p></body></html>");
        testParser(
            "<li>Lorem <li>Ipsum ",
            "<html><body><ul><li>Lorem </li><li>Ipsum </li></ul></body></html>");
        testParser(
            "<div><li>Lorem <li>Ipsum ",
            "<html><body><div><ul><li>Lorem </li><li>Ipsum </li></ul></div></body></html>");
        testParser(
            "<li>Lorem <li>Ipsum ",
            "<html><body><ul><li>Lorem </li><li>Ipsum </li></ul></body></html>");

        testParser("<div />", "<html><body><div></div></body></html>");
        testParser(
            "<div>This is a text</div>",
            "<html><body><div>This is a text</div></body></html>");
        testParser(
            "<div>This is a text",
            "<html><body><div>This is a text</div></body></html>");
        testParser("", "");
        testParser("<a/>", "<html><body><p><a></a></p></body></html>");
        testParser("    <div />   ", "<html><body><div></div></body></html>");
        testParser("<root>"
            + "<a xmlns='foo'><x></x><y></y></a>"
            + "<a xmlns:n='bar'><n:x></n:x><n:y></n:y></a>"
            + "</root>");
        testParser(
            "<feed xmlns='http://www.w3.org/2005/Atom' />",
            "<feed xmlns='http://www.w3.org/2005/Atom'></feed>");
        testParser(
            "<a><b><c><d><e><f>Text</f></e></d></c></b></a>",
            "<html><body><p><a><b><c><d><e><f>Text</f></e></d></c></b></a></p></body></html>");
        testParser(
            "<a><b>Text</b><c>Text</c><d>Text</d><e>Text</e><f>Text</f></a>",
            "<html><body><p><a><b>Text</b><c>Text</c><d>Text</d><e>Text</e><f>Text</f></a></p></body></html>");
        testParser(""
            + "<html>"
            + "<head>"
            + "<title>Hello, world</title>"
            + "</head>"
            + "<body>"
            + "<p class='first'>A new paragraph</p>"
            + "</body>"
            + "</html>");
    }

}
