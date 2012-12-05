package org.ubimix.model.conversion;

import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.json.IJsonListener;
import org.ubimix.model.xml.IXmlVisitor;
import org.ubimix.model.xml.XmlCDATA;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class XmlToJson implements IXmlVisitor {

    static final String KEY_CHILDREN = "~";

    static final String KEY_NAME = "!";

    private IJsonListener fJsonListener;

    public XmlToJson(IJsonListener listener) {
        fJsonListener = listener;
    }

    @Override
    public void visit(XmlCDATA cdata) {
        fJsonListener.onValue(cdata.getContent());
    }

    @Override
    public void visit(XmlElement element) {
        fJsonListener.beginObject();
        String name = element.getName();
        fJsonListener.beginObjectProperty(XmlElement.KEY_NAME);
        fJsonListener.onValue(name);
        fJsonListener.endObjectProperty(XmlElement.KEY_NAME);
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
                key = "xmlns:" + key;
            } else {
                key = "xmlns";
            }
            fJsonListener.beginObjectProperty(key);
            fJsonListener.onValue(value);
            fJsonListener.endObjectProperty(key);
        }
        List<XmlNode> children = element.getChildren();
        int len = children.size();
        if (len > 0) {
            fJsonListener.beginObjectProperty(XmlElement.KEY_CHILDREN);
            if (len == 1) {
                XmlNode node = children.get(0);
                node.accept(this);
            } else {
                fJsonListener.beginArray();
                for (XmlNode node : children) {
                    fJsonListener.beginArrayElement();
                    node.accept(this);
                    fJsonListener.endArrayElement();
                }
                fJsonListener.endArray();
            }
            fJsonListener.endObjectProperty(XmlElement.KEY_CHILDREN);
        }
        fJsonListener.endObject();
    }

    @Override
    public void visit(XmlText text) {
        fJsonListener.onValue(text.getContent());
    }

}