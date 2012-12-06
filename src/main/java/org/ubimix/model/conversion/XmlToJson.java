package org.ubimix.model.conversion;

import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.json.IJsonListener;
import org.ubimix.model.xml.IXmlCDATA;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlJson;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;
import org.ubimix.model.xml.IXmlVisitor;

/**
 * @author kotelnikov
 */
public class XmlToJson implements IXmlVisitor, IXmlJson {

    private IJsonListener fJsonListener;

    public XmlToJson(IJsonListener listener) {
        fJsonListener = listener;
    }

    public void notifyPropertyValues(IXmlElement element, String propertyName) {
        List<IXmlNode> children = element.getChildren();
        int len = children.size();
        if (len > 0) {
            fJsonListener.beginObjectProperty(propertyName);
            if (len == 1) {
                IXmlNode node = children.get(0);
                node.accept(this);
            } else {
                fJsonListener.beginArray();
                for (IXmlNode node : children) {
                    fJsonListener.beginArrayElement();
                    node.accept(this);
                    fJsonListener.endArrayElement();
                }
                fJsonListener.endArray();
            }
            fJsonListener.endObjectProperty(propertyName);
        }
    }

    @Override
    public void visit(IXmlCDATA cdata) {
        fJsonListener.onValue(cdata.getContent());
    }

    @Override
    public void visit(IXmlElement element) {
        String name = element.getName();
        if ("umx:property".equals(name)) {
            String propertyName = element.getAttribute("name");
            notifyPropertyValues(element, propertyName);
        } else {
            fJsonListener.beginObject();
            fJsonListener.beginObjectProperty(KEY_NAME);
            fJsonListener.onValue(name);
            fJsonListener.endObjectProperty(KEY_NAME);
            Map<String, String> attrs = element.getAttributes();
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                fJsonListener.beginObjectProperty(key);
                fJsonListener.onValue(value);
                fJsonListener.endObjectProperty(key);
            }
            Map<String, String> namespaces = element.getDeclaredNamespaces();
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!"".equals(key)) {
                    key = IXmlElement.NS_PREFIX + key;
                } else {
                    key = IXmlElement.NS;
                }
                fJsonListener.beginObjectProperty(key);
                fJsonListener.onValue(value);
                fJsonListener.endObjectProperty(key);
            }
            notifyPropertyValues(element, KEY_CHILDREN);
            fJsonListener.endObject();
        }
    }

    @Override
    public void visit(IXmlText text) {
        fJsonListener.onValue(text.getContent());
    }

}