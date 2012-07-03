/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Map;

import org.ubimix.model.xml.listeners.XmlSerializer;

/**
 * @author kotelnikov
 */
public abstract class XmlNode {

    /**
     * This visitor notifies an internal {@link IXmlListener} instance about
     * visited nodes.
     * 
     * @author kotelnikov
     */
    public static class XmlVisitorWithListener implements IXmlVisitor {

        private IXmlListener fListener;

        /**
         * 
         */
        public XmlVisitorWithListener(IXmlListener listener) {
            fListener = listener;
        }

        public void visit(XmlCDATA cdata) {
            String str = cdata.getContent();
            fListener.onCDATA(str);
        }

        public void visit(XmlElement element) {
            Map<String, String> declaredNamespaces = element
                .getDeclaredNamespaces();
            String name = element.getName();
            Map<String, String> attributes = element.getAttributes();
            fListener.beginElement(name, attributes, declaredNamespaces);
            for (XmlNode child : element) {
                child.accept(this);
            }
            fListener.endElement(name, attributes, declaredNamespaces);
        }

        public void visit(XmlText text) {
            String str = text.getContent();
            fListener.onText(str);
        }
    }

    private Object fObject;

    private XmlElement fParent;

    protected XmlNode(XmlElement parent, Object object) {
        fParent = parent;
        fObject = object;
    }

    public void accept(IXmlListener listener) {
        XmlVisitorWithListener visitor = new XmlVisitorWithListener(listener);
        accept(visitor);
    }

    public abstract void accept(IXmlVisitor visitor);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XmlNode)) {
            return false;
        }
        XmlNode o = (XmlNode) obj;
        return equals(fObject, o.fObject);
    }

    protected boolean equals(Object first, Object second) {
        return first == second || (first != null && first.equals(second));
    }

    /**
     * @return
     */
    public XmlNode getNextSibling() {
        if (fParent == null) {
            return null;
        }
        int parentSize = fParent.getChildCount();
        int pos = fParent.getChildPosition(this);
        return pos < parentSize - 1 ? fParent.getChild(pos + 1) : null;
    }

    public Object getObject() {
        if (fObject == null) {
            fObject = newObject();
        }
        return fObject;
    }

    public XmlElement getParent() {
        return fParent;
    }

    /**
     * @return
     */
    public XmlNode getPreviousSibling() {
        if (fParent == null) {
            return null;
        }
        int parentSize = fParent.getChildCount();
        int pos = fParent.getChildPosition(this);
        return pos > 0 && pos < parentSize ? fParent.getChild(pos - 1) : null;
    }

    @Override
    public int hashCode() {
        return fObject.hashCode();
    }

    protected abstract Object newObject();

    protected void removeFromParent() {
        if (fParent != null) {
            int parentSize = fParent.getChildCount();
            int pos = fParent.getChildPosition(this);
            if (pos >= 0 && pos < parentSize) {
                fParent.removeChild(pos);
            }
            fParent = null;
        }
    }

    protected void setParent(XmlElement parent) {
        fParent = parent;
    }

    @Override
    public String toString() {
        IXmlListener listener = new XmlSerializer();
        accept(listener);
        return listener.toString();
    }

}
