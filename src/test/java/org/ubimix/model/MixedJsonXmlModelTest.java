/**
 * 
 */
package org.ubimix.model;

import junit.framework.TestCase;

import org.ubimix.model.xml.XmlElement;

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

        content = doc.getContent();
        assertEquals(""
            + "<div my:userid='123'>"
            + "<p>first paragraph</p>"
            + "<p>second paragraph</p>"
            + "</div>", content.toString());
    }

    public void test1() throws Exception {
        String str = ""
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
            + "        <link rel='alternate' type='text/html' href='http://example.org/2003/12/13/atom03.html'></link>\n"
            + "        <link rel='edit' href='http://example.org/2003/12/13/atom03/edit'></link>\n"
            + "        <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>\n"
            + "        <updated>2003-12-13T18:30:02Z</updated>\n"
            + "        <summary>Some text.</summary>\n"
            + "        <category term='robots'></category>\n"
            + "        <category term='test' label='Test Label' scheme='http://example.org/ns/tags/'></category>\n"
            + "        <content type='html' xmlns='http://www.w3.org/1999/xhtml'>Robot-generated content.</content>\n"
            + "    </entry>\n"
            + " \n"
            + "</feed>";
        String objStr = testXmlToObj(str);
        String xmlStr = testObjToXml(objStr);
        assertEquals(str, xmlStr);
    }

    public void test2() {
        ModelTestFeed f = new ModelTestFeed();
        ModelObject obj = f.getFeed();
        String xmlStr = testObjToXml(obj.toString());
        String objStr = testXmlToObj(xmlStr);

        String first = obj.toString(true, 2);
        String second = ModelObject.parse(objStr).toString(true, 2);
        assertEquals(first, second);
    }

    protected void testConversion(XmlElement xml, ModelObject obj) {
        String xmlStr = xml.toString();
        String objStr = obj.toString();
        XmlElement xmlTest = XmlElement.parse(xmlStr);
        ModelObject objTest = ModelObject.parse(objStr);
        assertEquals(xml, xmlTest);
        assertEquals(obj, objTest);
        assertEquals(xmlStr, xmlTest.toString());
        assertEquals(objStr, objTest.toString());
        assertEquals(xmlTest, XmlElement.from(objTest));
        assertEquals(objTest, ModelObject.from(xmlTest));
    }

    private String testObjToXml(String str) {
        ModelObject obj = ModelObject.parse(str);
        XmlElement xml = new XmlElement(obj);
        testConversion(xml, obj);
        return xml.toString();
    }

    private String testXmlToObj(String str) {
        XmlElement xml = XmlElement.parse(str);
        ModelObject obj = ModelObject.from(xml);
        testConversion(xml, obj);
        return obj.toString();
    }
}
