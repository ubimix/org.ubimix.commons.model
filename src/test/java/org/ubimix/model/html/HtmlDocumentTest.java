/**
 * 
 */
package org.ubimix.model.html;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.XmlPathProcessor;

/**
 * @author kotelnikov
 */
public class HtmlDocumentTest extends TestCase {

    /**
     * @param name
     */
    public HtmlDocumentTest(String name) {
        super(name);
    }

    private void test(String str, String control) {
        IXmlElement xml = HtmlDocument.parse(str);
        assertEquals(control, xml.toString());
    }

    public void testParse() throws Exception {
        test("Hello, world", "<html><body>Hello, world</body></html>");
        test(
            "<a href='#'>Hello, world",
            "<html><body><a href='#'>Hello, world</a></body></html>");
        test(
            "<body><a href='#'>a",
            "<html><body><a href='#'>a</a></body></html>");
        test(
            "<a href='#'>a</p>b",
            "<html><body><a href='#'>ab</a></body></html>");
        test("<li>first<li>second<p>para1<p>para2", ""
            + "<html><body>"
            + "<ul>"
            + "<li>first</li>"
            + "<li>second"
            + "<p>para1</p>"
            + "<p>para2</p>"
            + "</li>"
            + "</ul>"
            + "</body></html>");

    }

    public void testSelect() {
        testSelect(
            "<p>Hello, <strong>world</strong>!",
            "strong",
            "<strong>world</strong>");
        testSelect(
            "before <a>b</a> <a href='#'>b</a> <a href='toto'>c</a> after",
            "a",
            "<a>b</a>",
            "<a href='#'>b</a>",
            "<a href='toto'>c</a>");
        testSelect(
            "before <a>b</a> <a href='#'>b</a> <a href='toto'>c</a> after",
            "a[href]",
            "<a href='#'>b</a>",
            "<a href='toto'>c</a>");
        testSelect(
            "before <a>b</a> <a href='#'>b</a> <a href='toto'>c</a> after",
            "a[href=toto]",
            "<a href='toto'>c</a>");
        testSelect(
            "before <a>b</a> <a href='#'>b</a> <a href='toto'>c</a> after",
            "a[href='#']",
            "<a href='#'>b</a>");
    }

    private void testSelect(String html, String select, String... controls) {
        IXmlElement e = HtmlDocument.parse(html);
        List<IXmlElement> list = new XmlPathProcessor(select).selectAll(e);
        assertEquals(controls.length, list.size());
        int i = 0;
        for (String control : controls) {
            IXmlElement s = list.get(i++);
            assertNotNull(s);
            assertEquals(control, s.toString());
        }
    }
}
