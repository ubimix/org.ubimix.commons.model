package org.ubimix.model.conversion;

import org.ubimix.commons.parser.json.JsonListener;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class JsonToXml extends JsonListener {

    private static class ElementContext {

        private XmlElement fElement;

        private JsonToXml.ElementContext fParent;

        private String fProperty;

        public ElementContext(JsonToXml.ElementContext parent, XmlElement e) {
            fParent = parent;
            fElement = e;
        }

        public void addContent(XmlNode e) {
            fElement.addChild(e);
        }

        public void addProperty(XmlNode e) {
            fElement.addPropertyField(fProperty, e);
        }

        public XmlElement getXmlElement() {
            return fElement;
        }

        public boolean isContentProperty() {
            return XmlElement.KEY_CHILDREN.equals(fProperty);
        }

        public boolean isNameProperty() {
            return XmlElement.KEY_NAME.equals(fProperty);
        }

        public JsonToXml.ElementContext pop() {
            return fParent;
        }

        public void setAttributeName(String property) {
            fProperty = property;
        }

        public void setXmlName(String value) {
            fElement.setName(value);
        }
    }

    private StringBuilder fBuf;

    private XmlFactory fFactory;

    private XmlElement fResult;

    private JsonToXml.ElementContext fTop;

    public JsonToXml(XmlFactory factory) {
        fFactory = factory;
    }

    @Override
    public void beginArrayElement() {
        flushText();
    }

    @Override
    public void beginObject() {
        flushText();
        XmlElement e = fFactory.newElement(null);
        fTop = new ElementContext(fTop, e);
    }

    @Override
    public void beginObjectProperty(String property) {
        flushText();
        fTop.setAttributeName(property);
    }

    @Override
    public void endObject() {
        flushText();
        JsonToXml.ElementContext prev = fTop;
        fTop = fTop.pop();
        XmlElement e = prev.getXmlElement();
        if (fTop != null) {
            if (fTop.isContentProperty()) {
                fTop.addContent(e);
            } else {
                fTop.addProperty(e);
            }
        } else {
            fResult = e;
        }
    }

    @Override
    public void endObjectProperty(String property) {
        flushText();
    }

    private void flushText() {
        if (fBuf != null) {
            String value = fBuf.toString();
            fBuf = null;
            if (fTop.isNameProperty()) {
                fTop.setXmlName(value);
            } else {
                XmlText e = fFactory.newText(value);
                if (fTop.isContentProperty()) {
                    fTop.addContent(e);
                } else {
                    fTop.addProperty(e);
                }
            }
        }
    }

    public XmlElement getResultElement() {
        return fResult;
    }

    @Override
    public void onValue(boolean value) {
        onValue(value + "");
    }

    @Override
    public void onValue(double value) {
        onValue(value + "");
    }

    @Override
    public void onValue(int value) {
        onValue(value + "");
    }

    @Override
    public void onValue(long value) {
        onValue(value + "");
    }

    @Override
    public void onValue(String value) {
        if (fBuf == null) {
            fBuf = new StringBuilder();
        }
        fBuf.append(value);
    }

    public void reset() {
        fTop = null;
        fResult = null;
        fBuf = null;
    }

}