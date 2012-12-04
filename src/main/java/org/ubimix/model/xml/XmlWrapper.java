package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlWrapper {

    protected XmlElement fElement;

    public XmlWrapper(String name) {
        this(new XmlFactory().newElement(name));
    }

    public XmlWrapper(XmlElement e) {
        fElement = e;
    }

    @SuppressWarnings("unchecked")
    protected <T extends XmlWrapper> T cast() {
        return (T) this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XmlWrapper)) {
            return false;
        }
        XmlWrapper o = (XmlWrapper) obj;
        return fElement.equals(o.fElement);
    }

    public XmlElement getElement() {
        return fElement;
    }

    public XmlFactory getFactory() {
        return fElement.getFactory();
    }

    @Override
    public int hashCode() {
        return fElement.hashCode();
    }

    @Override
    public String toString() {
        return fElement.toString();
    }

}