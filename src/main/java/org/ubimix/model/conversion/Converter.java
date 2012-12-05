/**
 * 
 */
package org.ubimix.model.conversion;

import org.ubimix.commons.parser.json.utils.JavaObjectBuilder;
import org.ubimix.commons.parser.json.utils.JavaObjectVisitor;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlFactory;

/**
 * @author kotelnikov
 */
public class Converter {

    public static XmlElement convertJavaToXml(Object java, XmlFactory factory) {
        JsonToXml jsonToXml = new JsonToXml(factory);
        JavaObjectVisitor visitor = new JavaObjectVisitor();
        visitor.visit(java, jsonToXml);
        XmlElement testElement = jsonToXml.getResultElement();
        return testElement;
    }

    public static XmlElement convertJsonToXml(
        IHasValueMap json,
        XmlFactory factory) {
        return convertJavaToXml(json.getMap(), factory);
    }

    public static ModelObject convertXmlToJson(XmlElement e) {
        JavaObjectBuilder jsonBuilder = new JavaObjectBuilder();
        XmlToJson xmlToJson = new XmlToJson(jsonBuilder);
        e.accept(xmlToJson);
        ModelObject testObject = ModelObject.FACTORY.newValue(jsonBuilder
            .getTop());
        return testObject;
    }

}
