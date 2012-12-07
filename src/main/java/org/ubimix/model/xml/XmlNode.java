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

    protected IXmlNode fNextSibling = this;

    private IXmlElement fParent;

    protected IXmlNode fPrevSibling = this;

    private IXmlFactory fXmlFactory;

    protected XmlNode(IXmlFactory factory, IXmlElement parent) {
        fXmlFactory = factory;
        fParent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XmlNode)) {
            return false;
        }
        return false;
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

    private IXmlNode getFirstNode() {
        IXmlNode result = null;
        XmlElement parent = getParent();
        if (parent != null) {
            result = parent.fFirstChild;
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getNextSibling()
     */
    @Override
    public IXmlNode getNextSibling() {
        if (fParent == null) {
            return null;
        }
        return !same(fNextSibling, this) && !same(fNextSibling, getFirstNode())
            ? fNextSibling
            : null;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getParent()
     */
    @Override
    public XmlElement getParent() {
        return (XmlElement) fParent;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#getPreviousSibling()
     */
    @Override
    public IXmlNode getPreviousSibling() {
        if (fParent == null) {
            return null;
        }
        return !same(fPrevSibling, this) && !same(getFirstNode(), this)
            ? fPrevSibling
            : null;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void insertBefore(IXmlNode nextNode) {
        insertBefore(nextNode, true);
    }

    protected void insertBefore(IXmlNode nextNode, boolean resetFirstParentNode) {
        XmlNode n = (XmlNode) nextNode;
        XmlElement parent = n.getParent();
        boolean first = false;
        if (n.sameAs(parent.fFirstChild)) {
            first = true;
        }
        XmlNode prev = (XmlNode) n.fPrevSibling;
        remove();
        n.fPrevSibling = this;
        prev.fNextSibling = this;
        fPrevSibling = prev;
        fNextSibling = n;
        if (first && resetFirstParentNode) {
            parent.fFirstChild = this;
        }
        fParent = parent;
    }

    /**
     * @see org.ubimix.model.xml.IXmlNode#remove()
     */
    @Override
    public void remove() {
        XmlElement parent = getParent();
        if (parent != null) {
            parent.removeChild(this);
        } else {
            unlink();
        }
        fParent = null;
    }

    protected boolean same(IXmlNode node, IXmlNode child) {
        return child == node;
    }

    @Override
    public boolean sameAs(IXmlNode node) {
        return same(node, this);
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

    protected void unlink() {
        XmlNode prev = (XmlNode) fPrevSibling;
        XmlNode next = (XmlNode) fNextSibling;
        prev.fNextSibling = next;
        next.fPrevSibling = prev;
        fPrevSibling = this;
        fNextSibling = this;
        fParent = null;
    }

}
