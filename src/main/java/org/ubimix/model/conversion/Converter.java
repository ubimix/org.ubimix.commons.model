/**
 * 
 */
package org.ubimix.model.conversion;

import org.ubimix.commons.parser.json.utils.JavaObjectBuilder;
import org.ubimix.commons.parser.json.utils.JavaObjectVisitor;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;

/**
 * @author kotelnikov
 */
public class Converter {

    public static IXmlElement convertJavaToXml(Object java, IXmlFactory factory) {
        JsonToXml jsonToXml = new JsonToXml(factory);
        JavaObjectVisitor visitor = new JavaObjectVisitor();
        visitor.visit(java, jsonToXml);
        IXmlElement testElement = jsonToXml.getResultElement();
        return testElement;
    }

    public static IXmlElement convertJsonToXml(
        IHasValueMap json,
        IXmlFactory factory) {
        return convertJavaToXml(json.getMap(), factory);
    }

    public static ModelObject convertXmlToJson(IXmlElement e) {
        JavaObjectBuilder jsonBuilder = new JavaObjectBuilder();
        XmlToJson xmlToJson = new XmlToJson(jsonBuilder);
        e.accept(xmlToJson);
        ModelObject testObject = ModelObject.FACTORY.newValue(jsonBuilder
            .getTop());
        return testObject;
    }

}
