/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;

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

    protected IXmlFactory newXmlFactory() {
        return XmlFactory.getInstance();
    }

    protected IXmlParser newXmlParser() {
        // return new SaxXmlParser();
        return new XmlParser();
    }

    public void test() throws Exception {
        IXmlFactory f = XmlFactory.getInstance();
        IXmlElement div = f.newElement("div");
        assertEquals("div", div.getName());

        IXmlElement a = f.newElement("a");
        assertEquals("a", a.getName());
        div.addChild(a);

        test(div, a);

        IXmlText txt = f.newText("hello");
        assertEquals("hello", txt.getContent());
        div.addChild(txt);

        test(div, a, txt);

        IXmlElement b = f.newElement("b");
        assertEquals("b", b.getName());
        div.addChild(b);

        test(div, a, txt, b);
        test(a);

        // Move the text node in the a
        a.addChild(txt);
        test(div, a, b);
        test(a, txt);

        IXmlElement xx = f.newElement("xx");
        IXmlElement yy = f.newElement("yy");
        a.addChild(xx, 0);
        a.addChild(yy);
        test(a, xx, txt, yy);
        a.flatten();
        test(a);
        test(div, xx, txt, yy, b);
    }

    private void test(IXmlElement div, IXmlNode... children) {
        assertEquals(children.length, div.getChildCount());
        for (int i = 0; i < children.length; i++) {
            IXmlNode child = children[i];
            IXmlNode test = div.getChild(i);
            assertEquals(child, test);
            assertTrue(child.sameAs(test));
        }

        IXmlNode test = div.getChild(0);
        if (children.length == 0) {
            assertNull(test);
        } else {
            for (int i = 0; i < children.length; i++) {
                IXmlNode child = children[i];
                assertNotNull(test);
                assertEquals(child, test);
                assertTrue(child.sameAs(test));
                test = test.getNextSibling();
            }
        }

        Iterator<IXmlNode> iterator = div.iterator();
        for (int i = 0; i < children.length; i++) {
            IXmlNode child = children[i];
            assertTrue(iterator.hasNext());
            test = iterator.next();
            assertEquals(child, test);
            assertTrue(child.sameAs(test));
        }

    }

    public void testElement() throws Exception {
        IXmlFactory doc = newXmlFactory();
        IXmlElement e;

        e = doc.newElement("div");
        assertEquals("div", e.getName());
        assertEquals("<div></div>", e.toString());
    }

    public void testElementAttributes() {
        IXmlFactory doc = newXmlFactory();
        IXmlElement e = doc.newElement("div");
        assertNull(e.getAttribute("toto"));
        e.setAttribute("toto", "abc");
        assertEquals("abc", e.getAttribute("toto"));
    }

    public void testElementChildren() {
        IXmlFactory doc = newXmlFactory();
        IXmlElement e = doc.newElement("div");
        List<IXmlNode> children = e.getChildren();
        assertNotNull(children);
        assertTrue(children.isEmpty());
        assertEquals(0, e.getChildCount());

        Iterator<IXmlNode> iterator = e.iterator();
        assertNotNull(iterator);
        assertFalse(iterator.hasNext());

        IXmlElement c = doc.newElement("p");
        assertNull(c.getParent());
        e.addChild(c);
        assertEquals(e, c.getParent());

        IXmlText t = doc.newText("Hello");
        assertNull(t.getParent());
        e.addChild(t, 0);
        assertEquals(e, t.getParent());

        IXmlNode next = c.getNextSibling();
        assertNull(next);
        IXmlNode prev = c.getPreviousSibling();
        assertEquals(t, prev);
    }

    public void testElementSerialization() {
        IXmlFactory doc = newXmlFactory();
        IXmlElement html = doc.newElement("html");
        IXmlElement head = doc.newElement("head");
        IXmlElement body = doc.newElement("body");
        html.addChild(head);
        html.addChild(body);
        IXmlElement p = doc.newElement("p");
        body.addChild(p);
        IXmlText text = doc.newText("Hello, there!");
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

    private void testElementSerialization(IXmlElement html, String control) {
        XmlSerializer serializer = new XmlSerializer();
        XmlUtils.accept(html, serializer, true);
        String str = serializer.toString();
        assertEquals(control, str);
    }

    public void testEntitiesInXml() {
        testEntitiesInXml("&#946;", "&#946;");
        testEntitiesInXml("abc&#946;xyz", "abc&#946;xyz");
        testEntitiesInXml("abc&#toto;xyz", "abc&#x26;#toto;xyz");
    }

    private void testEntitiesInXml(String xml, String control) {
        IXmlFactory doc = newXmlFactory();
        IXmlText node = doc.newText(xml);
        String str = node.toString();
        assertEquals(control, str);
    }

}
