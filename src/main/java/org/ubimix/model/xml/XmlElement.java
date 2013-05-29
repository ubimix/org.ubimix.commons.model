/**
 * 
 */
package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kotelnikov
 */
public class XmlElement extends XmlNode implements IXmlElement {

    @SuppressWarnings("unchecked")
    protected static <T> T cast(Object value) {
        return (T) value;
    }

    private final Map<String, String> fAttributes = new LinkedHashMap<String, String>();

    protected IXmlNode fFirstChild;

    private String fName;

    private Map<String, String> fNamespaces;

    protected XmlElement(IXmlFactory xmlFactory, String name) {
        super(xmlFactory, null);
        setName(name);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#accept(org.ubimix.model.xml.IXmlVisitor)
     */
    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addChild(IXmlNode)
     */
    @Override
    public void addChild(IXmlNode node) {
        node.remove();
        XmlNode n = (XmlNode) node;
        if (fFirstChild == null) {
            fFirstChild = n;
            n.setParent(this);
        } else {
            n.insertBefore(fFirstChild, false);
        }
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addChild(IXmlNode, int)
     */
    @Override
    public boolean addChild(IXmlNode node, int pos) {
        XmlNode n = (XmlNode) node;
        IXmlNode nextNode = getChild(pos);
        if (nextNode != null) {
            n.insertBefore(nextNode, true);
        } else if (fFirstChild != null) {
            n.insertBefore(fFirstChild);
        }
        return true;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addChildren(Iterable)
     */
    @Override
    public void addChildren(Iterable<? extends IXmlNode> children) {
        if (children instanceof IXmlElement) {
            IXmlElement e = (IXmlElement) children;
            children = e.getChildren();
        }
        if (children != null) {
            for (IXmlNode child : children) {
                if (child != null) {
                    addChild(child);
                }
            }
        }
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addText(java.lang.String)
     */
    @Override
    public IXmlElement addText(String str) {
        IXmlText text = getFactory().newText(str);
        addChild(text);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XmlElement)) {
            return false;
        }
        XmlElement e = (XmlElement) obj;
        return equals(fName, e.fName)
            && equals(fAttributes, e.fAttributes)
            && equals(fName, e.fNamespaces);
    }

    @Override
    public void flatten() {
        XmlElement parent = getParent();
        if (parent != null) {
            List<IXmlNode> children = getChildren();
            for (IXmlNode node : children) {
                XmlNode n = (XmlNode) node;
                n.insertBefore(this, true);
            }
            remove();
        }
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        return fAttributes.get(key);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttributeNames()
     */
    @Override
    public Set<String> getAttributeNames() {
        return fAttributes.keySet();
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttributes()
     */
    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> result = new LinkedHashMap<String, String>(
            fAttributes);
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChild(int)
     */
    @Override
    public IXmlNode getChild(int pos) {
        if (pos < 0) {
            return null;
        }
        IXmlNode child = fFirstChild;
        for (int i = 0; child != null && i < pos; i++) {
            child = child.getNextSibling();
        }
        return child;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildCount()
     */
    @Override
    public int getChildCount() {
        int result;
        IXmlNode child = fFirstChild;
        for (result = 0; child != null; result++) {
            child = child.getNextSibling();
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildPosition(org.ubimix.model.xml.XmlNode)
     */
    @Override
    public int getChildPosition(IXmlNode node) {
        int result = -1;
        IXmlNode child = fFirstChild;
        for (int i = 0; child != null; i++) {
            if (same(node, child)) {
                result = i;
                break;
            }
            child = child.getNextSibling();
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildren()
     */
    @Override
    public List<IXmlNode> getChildren() {
        List<IXmlNode> result = new ArrayList<IXmlNode>();
        IXmlNode child = fFirstChild;
        while (child != null) {
            result.add(child);
            child = child.getNextSibling();
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getDeclaredNamespaces()
     */
    @Override
    public Map<String, String> getDeclaredNamespaces() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (fNamespaces != null) {
            result.putAll(fNamespaces);
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getName()
     */
    @Override
    public String getName() {
        return fName != null ? fName : "umx:object";
    }

    @Override
    public int hashCode() {
        int a = getName().hashCode();
        int b = fAttributes.hashCode();
        int c = fNamespaces != null ? fNamespaces.hashCode() : 0;
        return a ^ b ^ c;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#iterator()
     */
    @Override
    public Iterator<IXmlNode> iterator() {
        Iterator<IXmlNode> result = new Iterator<IXmlNode>() {

            private IXmlNode fNode;

            private boolean fStarted;

            @Override
            public boolean hasNext() {
                if (!fStarted) {
                    return fFirstChild != null;
                } else {
                    return fNode != null && fNode.getNextSibling() != null;
                }
            }

            @Override
            public IXmlNode next() {
                if (!fStarted) {
                    fNode = fFirstChild;
                    fStarted = true;
                } else if (fNode != null) {
                    fNode = fNode.getNextSibling();
                }
                return fNode;
            }

            @Override
            public void remove() {
                if (!fStarted) {
                    throw new IllegalStateException();
                } else if (fNode != null) {
                    IXmlNode next = fNode.getNextSibling();
                    fNode.remove();
                    fNode = next;
                }
            }
        };
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeAttribute(java.lang.String)
     */
    @Override
    public IXmlElement removeAttribute(String key) {
        fAttributes.remove(key);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChild(int)
     */
    @Override
    public boolean removeChild(int pos) {
        boolean result = false;
        IXmlNode child = getChild(pos);
        if (child != null) {
            child.remove();
            result = true;
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChild(org.ubimix.model.xml.XmlNode)
     */
    @Override
    public boolean removeChild(IXmlNode child) {
        if (!sameAs(child.getParent())) {
            return false;
        }

        if (same(fFirstChild, child)) {
            fFirstChild = child.getNextSibling();
            if (same(fFirstChild, child)) {
                fFirstChild = null;
            }
        }
        XmlNode n = (XmlNode) child;
        n.unlink();
        return true;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChildren()
     */
    @Override
    public void removeChildren() {
        IXmlNode child = fFirstChild;
        while (child != null) {
            IXmlNode next = child.getNextSibling();
            ((XmlNode) child).unlink();
            child = next;
        }
        fFirstChild = null;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setAttribute(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public IXmlElement setAttribute(String key, String value) {
        fAttributes.put(key, value);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setAttributes(java.util.Map)
     */
    @Override
    public IXmlElement setAttributes(Map<String, String> attributes) {
        fAttributes.clear();
        fAttributes.putAll(attributes);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setChildren(Iterable)
     */
    @Override
    public IXmlElement setChildren(Iterable<IXmlNode> children) {
        removeChildren();
        addChildren(children);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setChildren(IXmlNode[])
     */
    @Override
    public IXmlElement setChildren(IXmlNode... children) {
        return setChildren(Arrays.asList(children));
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setName(java.lang.String)
     */
    @Override
    public IXmlElement setName(String tagName) {
        fName = !"umx:object".equals(tagName) ? tagName : null;
        return this;
    }

    @Override
    public IXmlElement setNamespace(String nsPrefix, String nsUrl) {
        if (fNamespaces == null) {
            fNamespaces = new LinkedHashMap<String, String>();
        }
        fNamespaces.put(nsPrefix, nsUrl);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setNamespaces(java.util.Map)
     */
    @Override
    public IXmlElement setNamespaces(Map<String, String> namespaces) {
        if (fNamespaces == null) {
            fNamespaces = new LinkedHashMap<String, String>();
        } else {
            fNamespaces.clear();
        }
        fNamespaces.putAll(namespaces);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setText(java.lang.String)
     */
    @Override
    public IXmlElement setText(String str) {
        if (str != null && !"".equals(str)) {
            IXmlText text = getFactory().newText(str);
            setChildren(text);
        } else {
            removeChildren();
        }
        return this;
    }

}
