/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Map;

import org.ubimix.commons.parser.xml.IXmlListener;

/**
 * @author kotelnikov
 */
public abstract class XmlNode implements IXmlNode {

    /**
     * This visitor notifies an internal {@link IXmlListener} instance about
     * visited nodes.
     * 
     * @author kotelnikov
     */
    public static class XmlVisitorWithListener implements IXmlVisitor {

        private boolean fDeep;

        private IXmlListener fListener;

        /**
         * 
         */
        public XmlVisitorWithListener(IXmlListener listener, boolean deep) {
            fListener = listener;
            fDeep = deep;
        }

        @Override
        public void visit(IXmlCDATA cdata) {
            String str = cdata.getContent();
            fListener.onCDATA(str);
        }

        @Override
        public void visit(IXmlElement element) {
            Map<String, String> declaredNamespaces = element
                .getDeclaredNamespaces();
            String name = element.getName();
            Map<String, String> attributes = element.getAttributes();
            fListener.beginElement(name, attributes, declaredNamespaces);
            if (fDeep) {
                for (IXmlNode child : element) {
                    child.accept(this);
                }
            }
            fListener.endElement(name, attributes, declaredNamespaces);
        }

        @Override
        public void visit(IXmlText text) {
            String str = text.getContent();
            fListener.onText(str);
        }

    }

    private Object fObject;

    private IXmlElement fParent;

    private IXmlFactory fXmlFactory;

    protected XmlNode(IXmlFactory factory, IXmlElement parent, Object object) {
        fXmlFactory = factory;
        fParent = parent;
        fObject = object;
    }

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
     * @see org.ubimix.model.xml.IXmlNode#getFactory()
     */
    @Override
    public IXmlFactory getFactory() {
        return fXmlFactory;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getNextSibling()
     */
    @Override
    public IXmlNode getNextSibling() {
        if (fParent == null) {
            return null;
        }
        int parentSize = fParent.getChildCount();
        int pos = fParent.getChildPosition(this);
        return pos >= 0 && pos < parentSize - 1
            ? fParent.getChild(pos + 1)
            : null;
    }

    protected Object getObject() {
        if (fObject == null) {
            fObject = newObject();
        }
        return fObject;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getParent()
     */
    @Override
    public IXmlElement getParent() {
        return fParent;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getPreviousSibling()
     */
    @Override
    public IXmlNode getPreviousSibling() {
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

    /**
     * @see org.ubimix.model.xml.IXmlNode#remove()
     */
    @Override
    public void remove() {
        IXmlElement parent = getParent();
        if (parent != null) {
            parent.removeChild(this);
        }
    }

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

    protected void setObject(Object object) {
        fObject = object;
    }

    protected void setParent(IXmlElement parent) {
        fParent = parent;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#toString()
     */
    @Override
    public String toString() {
        return XmlUtils.toStringRecursively(this, false);
    }

}
