/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.TreePresenter;

/**
 * @author kotelnikov
 */
public class XmlElement extends XmlNode
    implements
    IHasValueMap,
    IXmlElement,
    IXmlJson {

    public static TreePresenter TREE_ACCESSOR = new TreePresenter(KEY_CHILDREN);

    @SuppressWarnings("unchecked")
    protected static <T> T cast(Object value) {
        return (T) value;
    }

    private static boolean isExcludedAttributeName(String key) {
        boolean excluded = KEY_NAME.equals(key)
            || KEY_CHILDREN.equals(key)
            || NS.equals(key);
        if (!excluded) {
            excluded = key.startsWith(NS_PREFIX);
        }
        return excluded;
    }

    private static boolean isExcludedAttributeValue(Object value) {
        return (value instanceof Map<?, ?>) || (value instanceof List<?>);
    }

    protected XmlElement(
        IXmlFactory factory,
        IXmlElement parent,
        Map<Object, Object> map) {
        super(factory, parent, map);
    }

    protected XmlElement(IXmlFactory xmlFactory, String name) {
        super(xmlFactory, null, null);
        if (!"umx:object".equals(name)) {
            setName(name);
        }
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
        int len = getChildCount();
        addChild(node, len);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addChild(IXmlNode, int)
     */
    @Override
    public boolean addChild(IXmlNode node, int pos) {
        boolean result = addChildObject(getNodeObject(node), pos);
        if (result) {
            ((XmlNode) node).setParent(this);
        }
        return result;
    }

    private boolean addChildObject(Object object, int pos) {
        boolean result = TREE_ACCESSOR.addChild(getMap(), pos, object);
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#addChildren(Iterable)
     */
    @Override
    public void addChildren(Iterable<? extends IXmlNode> children) {
        for (IXmlNode child : children) {
            if (child != null) {
                addChild(child);
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

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String key) {
        Map<Object, Object> map = getMap();
        Object value = map.get(key);
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            return null;
        }
        return TreePresenter.toString(value);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttributeNames()
     */
    @Override
    public Set<String> getAttributeNames() {
        return getAttributes().keySet();
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getAttributes()
     */
    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        Map<Object, Object> map = getMap();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object attr = entry.getKey();
            String key = TreePresenter.toString(attr);
            if (!isExcludedAttributeName(key)) {
                Object value = entry.getValue();
                if (!isExcludedAttributeValue(value)) {
                    String str = TreePresenter.toString(value);
                    result.put(key, str);
                }
            }
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChild(int)
     */
    @Override
    public IXmlNode getChild(int pos) {
        IXmlNode result = null;
        Object object = getChildObject(pos);
        if (object != null) {
            result = newChild(object);
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildCount()
     */
    @Override
    public int getChildCount() {
        return TREE_ACCESSOR.getChildCount(getMap());
    }

    protected Object getChildObject(int pos) {
        return TREE_ACCESSOR.getChildObject(getMap(), pos);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildPosition(org.ubimix.model.xml.XmlNode)
     */
    @Override
    public int getChildPosition(IXmlNode node) {
        return TREE_ACCESSOR.getChildPosition(getMap(), getNodeObject(node));
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getChildren()
     */
    @Override
    public List<IXmlNode> getChildren() {
        return TREE_ACCESSOR.getChildren(getMap(), getNodeFactory());
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getDeclaredNamespaces()
     */
    @Override
    public Map<String, String> getDeclaredNamespaces() {
        Map<String, String> result = null;
        Map<Object, Object> map = getMap();
        Set<Object> attrs = map.keySet();
        for (Object attr : attrs) {
            String str = TreePresenter.toString(attr);
            String prefix = null;
            if (str.startsWith(NS_PREFIX)) {
                prefix = str.substring(NS_PREFIX.length());
            } else if (str.equals(NS)) {
                prefix = "";
            }
            if (prefix != null) {
                if (result == null) {
                    result = new LinkedHashMap<String, String>();
                }
                Object value = map.get(attr);
                String ns = TreePresenter.toString(value);
                result.put(prefix, ns);
            }
        }
        if (result == null) {
            result = Collections.emptyMap();
        }
        return result;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getMap()
     */
    @Override
    public Map<Object, Object> getMap() {
        return cast(getObject());
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#getName()
     */
    @Override
    public String getName() {
        Map<Object, Object> map = getMap();
        Object value = map.get(KEY_NAME);
        String name = value != null && !"".equals(value) ? TreePresenter
            .toString(value) : "umx:object";
        return name;
    }

    public IValueFactory<IXmlNode> getNodeFactory() {
        return new IValueFactory<IXmlNode>() {
            @Override
            public IXmlNode newValue(Object object) {
                return newChild(object);
            }
        };
    }

    public Object getNodeObject(IXmlNode child) {
        return ((XmlNode) child).getObject();
    }

    private boolean isEmpty(Object container) {
        return container == null || "".equals(container);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#iterator()
     */
    @Override
    public Iterator<IXmlNode> iterator() {
        List<IXmlNode> list = getChildren();
        return list.iterator();
    }

    private IXmlCDATA newCDATA(String object) {
        IXmlFactory factory = getFactory();
        IXmlCDATA cdata = factory.newCDATA(object);
        setParent(cdata);
        return cdata;
    }

    private IXmlNode newChild(Object o) {
        IXmlNode result = null;
        if (o instanceof List<?>) {
            IXmlElement e = newElement(newObject());
            e.setName("umx:list");
            ((XmlElement) e).addChildObject(o, 0);
            result = e;
        } else if (o instanceof Map<?, ?>) {
            Map<Object, Object> map = cast(o);
            result = newElement(map);
        } else if (!isEmpty(o)) {
            String str = TreePresenter.toString(o);
            if (XmlCDATA.isCDATA(str)) {
                result = newCDATA(str);
            } else {
                result = newText(str);
            }
        }
        return result;
    }

    private IXmlElement newElement(Map<Object, Object> obj) {
        if (obj == null) {
            obj = newObject();
        }
        IXmlElement result = FACTORY.newValue(obj);
        setParent(result);
        return result;
    }

    @Override
    protected Map<Object, Object> newObject() {
        return new LinkedHashMap<Object, Object>();
    }

    private IXmlText newText(String content) {
        IXmlFactory factory = getFactory();
        IXmlText text = factory.newText(content);
        setParent(text);
        return text;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeAttribute(java.lang.String)
     */
    @Override
    public IXmlElement removeAttribute(String key) {
        if (isExcludedAttributeName(key)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = getMap();
        map.remove(key);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChild(int)
     */
    @Override
    public boolean removeChild(int pos) {
        return TREE_ACCESSOR.removeChild(getMap(), pos);
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChild(org.ubimix.model.xml.XmlNode)
     */
    @Override
    public boolean removeChild(IXmlNode child) {
        return TREE_ACCESSOR.removeChild(getMap(), getNodeObject(child));
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#removeChildren()
     */
    @Override
    public void removeChildren() {
        TREE_ACCESSOR.removeChildren(getMap());
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setAttribute(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public IXmlElement setAttribute(String key, String value) {
        if (isExcludedAttributeName(key)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = getMap();
        map.put(key, value);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setAttributes(java.util.Map)
     */
    @Override
    public IXmlElement setAttributes(Map<String, String> attributes) {
        setValues(attributes, null);
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
        Map<Object, Object> map = getMap();
        if (tagName != null) {
            map.put(KEY_NAME, tagName);
        } else {
            map.remove(KEY_NAME);
        }
        return this;
    }

    @Override
    public IXmlElement setNamespace(String nsPrefix, String nsUrl) {
        String key = "".equals(nsPrefix) ? "xmlns" : "xmlns:" + nsPrefix;
        Map<Object, Object> map = getMap();
        map.put(key, nsUrl);
        return this;
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setNamespaces(java.util.Map)
     */
    @Override
    public IXmlElement setNamespaces(Map<String, String> attributes) {
        setValues(attributes, "xmlns");
        return this;
    }

    private void setParent(IXmlNode node) {
        if (node instanceof XmlNode) {
            ((XmlNode) node).setParent(this);
        }
    }

    /**
     * @see org.ubimix.model.xml.IXmlElement#setText(java.lang.String)
     */
    @Override
    public IXmlElement setText(String str) {
        IXmlText text = getFactory().newText(str);
        setChildren(text);
        return this;
    }

    private void setValues(Map<String, String> attributes, String prefix) {
        Map<Object, Object> map = getMap();
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            String key = attr.getKey();
            if (prefix != null
                && !key.startsWith(prefix)
                && !key.equals(prefix)) {
                if ("".equals(key)) {
                    key = prefix;
                } else {
                    key = prefix + ":" + key;
                }
            }
            String value = attr.getValue();
            map.put(key, value);
        }
    }

}
