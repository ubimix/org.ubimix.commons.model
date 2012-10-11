/**
 * 
 */
package org.ubimix.model.xml;

import junit.framework.TestCase;

import org.ubimix.model.ModelObject;

/**
 * @author kotelnikov
 */
public class MixedJsonXmlModelTest extends TestCase {

    public static class MyDocument extends ModelObject {

        public XmlElement getContent() {
            return getValue("content", XmlElement.FACTORY);
        }

        public String getTitle() {
            return getString("title");
        }

        public MyDocument setContent(String content) {
            XmlElement element = XmlElement.parse(content);
            return setContent(element);
        }

        public MyDocument setContent(XmlElement content) {
            setValue("content", content);
            return cast();
        }

        public MyDocument setTitle(String title) {
            setValue("title", title);
            return cast();
        }
    }

    /**
     * @param name
     */
    public MixedJsonXmlModelTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        MyDocument doc = new MyDocument();
        doc.setTitle("Hello, world");
        String str = ""
            + "<div xmlns='http://www.w3.org/1999/xhtml/'>"
            + "<p>First paragraph.</p>"
            + "<p>This is the second paragraph.</p>"
            + "</div>";
        doc.setContent(str);
        assertEquals("Hello, world", doc.getTitle());
        XmlElement content = doc.getContent();
        assertNotNull(content);
        assertEquals(str, content.toString());

        doc.setContent(""
            + "<div my:userid=\"123\">"
            + "<p>first paragraph</p>"
            + "<p>second paragraph</p>"
            + "</div>");
        XmlElement e = XmlElement.from(doc);
        System.out.println(e);

        content = doc.getContent();
        assertEquals(""
            + "<div my:userid='123'>"
            + "<p>first paragraph</p>"
            + "<p>second paragraph</p>"
            + "</div>", content.toString());
    }
}
