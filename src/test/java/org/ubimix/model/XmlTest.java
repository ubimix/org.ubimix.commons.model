/**
 * 
 */
package org.ubimix.model;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

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

    protected IXmlParser newXmlParser() {
        // return new SaxXmlParser();
        return new XmlParser();
    }

    public void testElement() throws Exception {
        XmlFactory doc = new XmlFactory();
        XmlElement e;

        e = doc.newElement("div");
        assertEquals("div", e.getName());
        assertEquals("<div></div>", e.toString());

        e = doc.newElement((String) null);
        assertEquals("umx:object", e.getName());
        assertEquals("<umx:object></umx:object>", e.toString());
    }

    public void testElementAttributes() {
        XmlFactory doc = new XmlFactory();
        XmlElement e = doc.newElement("div");
        assertNull(e.getAttribute("toto"));
        e.setAttribute("toto", "abc");
        assertEquals("abc", e.getAttribute("toto"));
    }

    public void testElementChildren() {
        XmlFactory doc = new XmlFactory();
        XmlElement e = doc.newElement("div");
        List<XmlNode> children = e.getChildren();
        assertNotNull(children);
        assertTrue(children.isEmpty());
        assertEquals(0, e.getChildCount());

        Iterator<XmlNode> iterator = e.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());

        XmlElement c = doc.newElement("p");
        assertNull(c.getParent());
        e.addChild(c);
        assertEquals(e, c.getParent());

        XmlText t = doc.newText("Hello");
        assertNull(t.getParent());
        e.addChild(t, 0);
        assertEquals(e, t.getParent());

        XmlNode next = c.getNextSibling();
        assertNull(next);
        XmlNode prev = c.getPreviousSibling();
        assertEquals(t, prev);
    }

    public void testElementSerialization() {
        XmlFactory doc = new XmlFactory();
        XmlElement html = doc.newElement("html");
        XmlElement head = doc.newElement("head");
        XmlElement body = doc.newElement("body");
        html.addChild(head);
        html.addChild(body);
        XmlElement p = doc.newElement("p");
        body.addChild(p);
        XmlText text = doc.newText("Hello, there!");
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
        XmlFactory doc = new XmlFactory();
        XmlText node = doc.newText(xml);
        String str = node.toString();
        assertEquals(control, str);
    }

}
