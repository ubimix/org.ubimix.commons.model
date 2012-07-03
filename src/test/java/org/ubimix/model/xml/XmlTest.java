/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.xml.listeners.XmlBuilder;
import org.ubimix.model.xml.listeners.XmlSerializer;

/**
 * @author kotelnikov
 */
public class XmlTest extends TestCase {

    /**
     * @param name
     */
    public XmlTest(String name) {
        super(name);
    }

    public void testElement() throws Exception {
        XmlElement e = new XmlElement("div");
        assertEquals("div", e.getName());
    }

    public void testElementAttributes() {
        XmlElement e = new XmlElement("div");
        assertNull(e.getAttribute("toto"));
        e.setAttribute("toto", "abc");
        assertEquals("abc", e.getAttribute("toto"));
    }

    public void testElementChildren() {
        XmlElement e = new XmlElement("div");
        List<XmlNode> children = e.getChildren();
        assertNotNull(children);
        assertTrue(children.isEmpty());
        assertEquals(0, e.getChildCount());

        Iterator<XmlNode> iterator = e.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());

        XmlElement c = new XmlElement("p");
        assertNull(c.getParent());
        e.addChild(c);
        assertEquals(e, c.getParent());

        XmlText t = new XmlText("Hello");
        assertNull(t.getParent());
        e.addChild(t, 0);
        assertEquals(e, t.getParent());

        XmlNode next = c.getNextSibling();
        assertNull(next);
        XmlNode prev = c.getPreviousSibling();
        assertEquals(t, prev);
    }

    public void testElementSerialization() {
        XmlElement html = new XmlElement("html");
        XmlElement head = new XmlElement("head");
        XmlElement body = new XmlElement("body");
        html.addChild(head);
        html.addChild(body);
        XmlElement p = new XmlElement("p");
        body.addChild(p);
        XmlText text = new XmlText("Hello, there!");
        p.addChild(text);

        testElementSerialization(html, "<html>"
            + "<head></head>"
            + "<body><p>Hello, there!</p></body>"
            + "</html>");

        p.setAttribute("class", "main");
        p.setName("div");
        testElementSerialization(html, "<html>"
            + "<head></head>"
            + "<body><div class='main'>Hello, there!</div></body>"
            + "</html>");
    }

    private void testElementSerialization(XmlElement html, String control) {
        XmlSerializer serializer = new XmlSerializer();
        html.accept(serializer);
        String str = serializer.toString();
        assertEquals(control, str);
    }

    public void testEntitiesInXml() {
        testEntitiesInXml("&#946;", "&#946;");
        testEntitiesInXml("abc&#946;xyz", "abc&#946;xyz");
        testEntitiesInXml("abc&#toto;xyz", "abc&#x26;#toto;xyz");
    }

    private void testEntitiesInXml(String xml, String control) {
        XmlText node = new XmlText(xml);
        String str = node.toString();
        assertEquals(control, str);
    }

    public void testParser() {
        testParser("", "");
        testParser("<a/>", "<a></a>");
        testParser("    <div />   ", "<div></div>");
        testParser("<root>"
            + "<a xmlns='foo'><x></x><y></y></a>"
            + "<a xmlns:n='bar'><n:x></n:x><n:y></n:y></a>"
            + "</root>");
        testParser(
            "<feed xmlns='http://www.w3.org/2005/Atom' />",
            "<feed xmlns='http://www.w3.org/2005/Atom'></feed>");
        testParser("<a><b><c><d><e><f>Text</f></e></d></c></b></a>");
        testParser("<a><b>Text</b><c>Text</c><d>Text</d><e>Text</e><f>Text</f></a>");
        testParser(""
            + "<html>"
            + "<head>"
            + "<title>Hello, world</title>"
            + "</head>"
            + "<body>"
            + "<p class='first'>A new paragraph</p>"
            + "</body>"
            + "</html>");
        testParser(
            ""
                + "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
                + " \n"
                + "    <title>Example Feed</title>\n"
                + "    <subtitle>A subtitle.</subtitle>\n"
                + "    <link href='http://example.org/feed/' rel='self' />\n"
                + "    <link href='http://example.org/' />\n"
                + "    <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>\n"
                + "    <updated>2003-12-13T18:30:02Z</updated>\n"
                + "    <author>\n"
                + "        <name>John Doe</name>\n"
                + "        <email>johndoe@example.com</email>\n"
                + "    </author>\n"
                + " \n"
                + "    <entry>\n"
                + "        <title>Atom-Powered Robots Run Amok</title>\n"
                + "        <link href='http://example.org/2003/12/13/atom03' />\n"
                + "        <link rel='alternate' type='text/html' href='http://example.org/2003/12/13/atom03.html'/>\n"
                + "        <link rel='edit' href='http://example.org/2003/12/13/atom03/edit'/>\n"
                + "        <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n"
                + "        <updated>2003-12-13T18:30:02Z</updated>\n"
                + "        <summary>Some text.</summary>\n"
                + "        <category term='robots'/>\n"
                + "        <category term='test' label='Test Label' scheme='http://example.org/ns/tags/'/>\n"
                + "        <content type='html' xmlns='http://www.w3.org/1999/xhtml'>Robot-generated content.</content>\n"
                + "    </entry>\n"
                + " \n"
                + "</feed>",
            ""
                + "<feed xmlns='http://www.w3.org/2005/Atom'>\n"
                + " \n"
                + "    <title>Example Feed</title>\n"
                + "    <subtitle>A subtitle.</subtitle>\n"
                + "    <link href='http://example.org/feed/' rel='self'></link>\n"
                + "    <link href='http://example.org/'></link>\n"
                + "    <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>\n"
                + "    <updated>2003-12-13T18:30:02Z</updated>\n"
                + "    <author>\n"
                + "        <name>John Doe</name>\n"
                + "        <email>johndoe@example.com</email>\n"
                + "    </author>\n"
                + " \n"
                + "    <entry>\n"
                + "        <title>Atom-Powered Robots Run Amok</title>\n"
                + "        <link href='http://example.org/2003/12/13/atom03'></link>\n"
                + "        <link href='http://example.org/2003/12/13/atom03.html' rel='alternate' type='text/html'></link>\n"
                + "        <link href='http://example.org/2003/12/13/atom03/edit' rel='edit'></link>\n"
                + "        <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n"
                + "        <updated>2003-12-13T18:30:02Z</updated>\n"
                + "        <summary>Some text.</summary>\n"
                + "        <category term='robots'></category>\n"
                + "        <category label='Test Label' scheme='http://example.org/ns/tags/' term='test'></category>\n"
                + "        <content type='html' xmlns='http://www.w3.org/1999/xhtml'>Robot-generated content.</content>\n"
                + "    </entry>\n"
                + " \n"
                + "</feed>");
    }

    private void testParser(String xml) {
        testParser(xml, xml);
    }

    private void testParser(String xml, String control) {
        XmlBuilder builder = new XmlBuilder();
        IXmlParser parser = new XmlParser(); // new SaxXmlParser();
        parser.parse(xml, builder);
        XmlElement element = builder.getResult();
        if ("".equals(control)) {
            assertNull(element);
        } else {
            assertNotNull(element);
            String str = element.toString();
            assertEquals(control, str);

            builder.reset();
            parser.parse(str, builder);
            XmlElement newElement = builder.getResult();
            assertEquals(control, newElement.toString());
        }
    }

}
