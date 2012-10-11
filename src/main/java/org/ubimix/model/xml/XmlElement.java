/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.TreePresenter;
import org.ubimix.model.xml.listeners.XmlBuilder;

/**
 * @author kotelnikov
 */
public class XmlElement extends XmlNode
    implements
    Iterable<XmlNode>,
    IHasValueMap {

    /**
     * Creates and returns {@link XmlElement} instance wrapping the specified
     * java value.
     */
    public static final IValueFactory<XmlElement> FACTORY = new IValueFactory<XmlElement>() {
        @Override
        public XmlElement newValue(Object object) {
            Map<Object, Object> map = null;
            if (object instanceof Map<?, ?>) {
                map = cast(object);
            }
            return map != null ? new XmlElement(null, map) : null;
        }
    };

    private static IXmlParser fParser;

    private static final String KEY_CHILDREN1 = "~";

    private static final String KEY_NAME = "!";

    public static final String NS = "xmlns";

    public static final String NS_PREFIX = "xmlns:";

    public static TreePresenter TREE_ACCESSOR = new TreePresenter(KEY_CHILDREN1)
        .setExcludedAttributePrefixes(NS_PREFIX)
        .setExcludedAttributes(KEY_NAME, KEY_CHILDREN1, NS);

    protected static void addDeclaredNamespaces(
        Map<String, String> namespaces,
        Map<String, String> declaredNamespaces) {
        for (String prefix : declaredNamespaces.keySet()) {
            if (!namespaces.containsKey(prefix)) {
                String ns = declaredNamespaces.get(prefix);
                if (!namespaces.containsValue(ns)) {
                    namespaces.put(prefix, ns);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T cast(Object value) {
        return (T) value;
    }

    public static XmlElement from(IHasValueMap object) {
        if (object instanceof XmlElement) {
            return cast(object);
        }
        return new XmlElement(object);
    }

    public static IXmlParser getParser() {
        if (fParser == null) {
            fParser = new XmlParser();
        }
        return fParser;
    }

    public static XmlElement parse(String xml) {
        XmlBuilder builder = new XmlBuilder();
        IXmlParser parser = getParser();
        parser.parse(xml, builder);
        return builder.getResult();
    }

    public static void setParser(IXmlParser parser) {
        fParser = parser;
    }

    public XmlElement(IHasValueMap object) {
        this(null, object.getMap());
    }

    public XmlElement(String name) {
        this(null, null);
        setName(name);
    }

    public XmlElement(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    public void addChild(XmlNode node) {
        int len = getChildCount();
        addChild(node, len);
    }

    public boolean addChild(XmlNode node, int pos) {
        boolean result = TREE_ACCESSOR
            .addChild(getMap(), pos, node.getObject());
        if (result) {
            node.setParent(this);
        }
        return result;
    }

    public String getAttribute(String key) {
        Map<Object, Object> map = getMap();
        Object value = map.get(key);
        return TreePresenter.toString(value);
    }

    public Set<String> getAttributeNames() {
        return TREE_ACCESSOR.getAttributeNames(getMap());
    }

    public Map<String, String> getAttributes() {
        Set<String> keys = getAttributeNames();
        Map<String, String> result = new HashMap<String, String>();
        for (String key : keys) {
            String value = getAttribute(key);
            result.put(key, value);
        }
        return result;
    }

    public XmlNode getChild(int pos) {
        XmlNode result = null;
        Object object = getChildObject(pos);
        if (object != null) {
            result = newChild(object);
        }
        return result;
    }

    public int getChildCount() {
        return TREE_ACCESSOR.getChildCount(getMap());
    }

    protected Object getChildObject(int pos) {
        return TREE_ACCESSOR.getChildObject(getMap(), pos);
    }

    protected int getChildPosition(XmlNode node) {
        return TREE_ACCESSOR.getChildPosition(getMap(), node.getObject());
    }

    public List<XmlNode> getChildren() {
        return TREE_ACCESSOR.getChildren(getMap(), getNodeFactory());
    }

    /**
     * Returns a map with prefixes and the corresponding namespaces defined
     * directly in this element. The default namespace corresponds to an empty
     * prefix ("").
     * 
     * @return a map with prefixes and the corresponding namespaces
     */
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

    @Override
    public Map<Object, Object> getMap() {
        return cast(getObject());
    }

    public String getName() {
        Map<Object, Object> map = getMap();
        Object value = map.get(KEY_NAME);
        return TreePresenter.toString(value);
    }

    /**
     * Returns a map with prefixes and the corresponding namespaces valid for
     * this element. The default namespace corresponds to an empty prefix ("").
     * 
     * @return a map with prefixes and the corresponding namespaces
     */
    public Map<String, String> getNamespaces() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        XmlElement e = this;
        while (e != null) {
            Map<String, String> declaredNamespaces = getDeclaredNamespaces();
            addDeclaredNamespaces(result, declaredNamespaces);
            e = e.getParent();
        }
        return result;
    }

    public IValueFactory<XmlNode> getNodeFactory() {
        return new IValueFactory<XmlNode>() {
            @Override
            public XmlNode newValue(Object object) {
                return newChild(object);
            }
        };
    }

    private boolean isEmpty(Object container) {
        return container == null || "".equals(container);
    }

    @Override
    public Iterator<XmlNode> iterator() {
        List<XmlNode> list = getChildren();
        return list.iterator();
    }

    public String lookupNs(String prefix) {
        String key = "".equals(prefix) ? NS : NS_PREFIX + prefix;
        XmlElement e = this;
        String result = null;
        while (e != null && result == null) {
            result = e.getAttribute(key);
            e = e.getParent();
        }
        return result;
    }

    public String lookupNsPrefix(String namespace) {
        XmlElement e = this;
        String result = null;
        while (e != null && result == null) {
            Set<String> attrs = e.getAttributeNames();
            for (String attr : attrs) {
                if (attr.startsWith(NS_PREFIX)) {
                    String value = e.getAttribute(attr);
                    if (namespace.equals(value)) {
                        result = attr.substring(NS_PREFIX.length());
                    }
                } else if (attr.equals(NS)) {
                    String value = e.getAttribute(attr);
                    if (namespace.equals(value)) {
                        result = value;
                    }
                }
            }
            e = e.getParent();
        }
        return result;
    }

    public XmlCDATA newCDATA(String object) {
        XmlCDATA cdata = new XmlCDATA(this, object);
        return cdata;
    }

    private XmlNode newChild(Object o) {
        XmlNode result = null;
        if (o instanceof List<?>) {
            // FIXME: ???
        } else if (o instanceof Map<?, ?>) {
            Map<Object, Object> map = cast(o);
            result = newElement(map);
        } else if (!isEmpty(o)) {
            String str = TreePresenter.toString(o);
            if (str.startsWith(XmlCDATA.CDATA_PREFIX)) {
                result = newCDATA(str);
            } else {
                result = newText(str);
            }
        }
        return result;
    }

    private XmlElement newElement(Map<Object, Object> obj) {
        return new XmlElement(this, obj);
    }

    @Override
    protected Map<Object, Object> newObject() {
        return new LinkedHashMap<Object, Object>();
    }

    public XmlText newText(String content) {
        XmlText text = new XmlText(this, content);
        return text;
    }

    public boolean removeChild(int pos) {
        return TREE_ACCESSOR.removeChild(getMap(), pos);
    }

    public void removeChildren() {
        TREE_ACCESSOR.removeChildren(getMap());
    }

    public void setAttribute(String key, String value) {
        TREE_ACCESSOR.setAttribute(getMap(), key, value);
    }

    public void setAttributes(Map<String, String> attributes) {
        setValues(attributes, null);
    }

    public void setName(String tagName) {
        Map<Object, Object> map = getMap();
        if (tagName != null) {
            map.put(KEY_NAME, tagName);
        } else {
            map.remove(KEY_NAME);
        }
    }

    public void setNamespaces(Map<String, String> attributes) {
        setValues(attributes, "xmlns");
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
