package org.ubimix.model.conversion;

import org.ubimix.commons.parser.json.JsonListener;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlJson;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;

/**
 * @author kotelnikov
 */
public class JsonToXml extends JsonListener implements IXmlJson {

    private static class ElementContext {

        private IXmlElement fElement;

        private JsonToXml.ElementContext fParent;

        private String fProperty;

        public ElementContext(JsonToXml.ElementContext parent, IXmlElement e) {
            fParent = parent;
            fElement = e;
        }

        public void addAttribute(String value) {
            if (fProperty != null) {
                String nsPrefix = null;
                if (fProperty.startsWith(IXmlElement.NS_PREFIX)) {
                    nsPrefix = fProperty.substring(IXmlElement.NS_PREFIX
                        .length());
                } else if (IXmlElement.NS.equals(fProperty)) {
                    nsPrefix = "";
                }
                if (nsPrefix != null) {
                    fElement.setNamespace(nsPrefix, value);
                } else {
                    fElement.setAttribute(fProperty, value);
                }
            }
        }

        public void addContent(IXmlNode e) {
            fElement.addChild(e);
        }

        public void addProperty(IXmlNode e) {
            IXmlElement property = e.getFactory().newElement("umx:property");
            property.setAttribute("name", fProperty);
            property.addChild(e);
            fElement.addChild(property);
        }

        public IXmlElement getXmlElement() {
            return fElement;
        }

        public boolean isContentProperty() {
            return KEY_CHILDREN.equals(fProperty);
        }

        public boolean isNameProperty() {
            return KEY_NAME.equals(fProperty);
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

    private IXmlFactory fFactory;

    private IXmlElement fResult;

    private JsonToXml.ElementContext fTop;

    public JsonToXml(IXmlFactory factory) {
        fFactory = factory;
    }

    @Override
    public void beginArrayElement() {
        flushText();
    }

    @Override
    public void beginObject() {
        flushText();
        IXmlElement e = fFactory.newElement(null);
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
        IXmlElement e = prev.getXmlElement();
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
                if (fTop.isContentProperty()) {
                    IXmlText e = fFactory.newText(value);
                    fTop.addContent(e);
                } else {
                    fTop.addAttribute(value);
                }
            }
        }
    }

    public IXmlElement getResultElement() {
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