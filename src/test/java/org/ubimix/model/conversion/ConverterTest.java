/**
 * 
 */
package org.ubimix.model.conversion;

import java.util.Map;

import junit.framework.TestCase;

import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlFactory;

/**
 * @author kotelnikov
 */
public class ConverterTest extends TestCase {

    public ConverterTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        test("<div></div>", "{'!': 'div'}");
        test("<div><a/><b/>text<c/></div>", "{\n"
            + "  '!':'div',\n"
            + "  '~':[\n"
            + "    {'!':'a'},\n"
            + "    {'!':'b'},\n"
            + "    'text',\n"
            + "    {'!':'c'}\n"
            + "  ]\n"
            + "}");
        test(""
            + "<div xmlns='http://www.w3.org/1999/xhtml/'>"
            + "<p>First paragraph.</p>"
            + "<p>This is the second paragraph.</p>"
            + "</div>", "{\n"
            + "  '!':'div',\n"
            + "  'xmlns':'http://www.w3.org/1999/xhtml/',\n"
            + "  '~':[\n"
            + "     {'!':'p','~':'First paragraph.'},\n"
            + "     {'!':'p','~':'This is the second paragraph.'}\n"
            + "  ]\n"
            + "}");
    }

    private void test(String xml, String json) {
        XmlFactory factory = new XmlFactory();
        XmlElement e = factory.parse(xml);
        ModelObject o = ModelObject.parse(json);

        // XmlToJson
        ModelObject testObject = Converter.convertXmlToJson(e);
        assertEquals(o + "", testObject + "");

        // JsonToXml
        XmlElement testElement = Converter.convertJsonToXml(o, factory);
        assertEquals(e, testElement);
    }

    public void testConvertion() {
        String json = "{\n"
            + "  \"title\":\"\",\n"
            + "  \"content\":\"<div></div>\",\n"
            + "  \"children\":[\n"
            + "    {\n"
            + "      \"title\":\"Title 1\",\n"
            + "      \"content\":\"<p>First paragraph</p><div><p>Second paragraph</p><div><div></div></div></div>\",\n"
            + "      \"children\":[\n"
            + "        {\n"
            + "          \"title\":\"Title 1.1\",\n"
            + "          \"content\":\"<p>Third paragraph</p><p>Fourth paragraph</p>\"\n"
            + "        },\n"
            + "        {\n"
            + "          \"title\":\"Title 1.2\",\n"
            + "          \"content\":\"<p>Fifth paragraph</p><div><div></div></div>\",\n"
            + "          \"children\":[\n"
            + "            {\n"
            + "              \"title\":\"Title 1.2.3\",\n"
            + "              \"content\":\"<div><div><p>Sixth paragraph</p></div></div>\"\n"
            + "            }\n"
            + "          ]\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";
        ModelObject obj = ModelObject.parse(json);
        Map<Object, Object> map = obj.getMap();
        XmlFactory xmlFactory = new XmlFactory();

        XmlElement first = Converter.convertJsonToXml(obj, xmlFactory);
        XmlElement second = Converter.convertJavaToXml(map, xmlFactory);
        assertEquals(first, second);
    }

}
