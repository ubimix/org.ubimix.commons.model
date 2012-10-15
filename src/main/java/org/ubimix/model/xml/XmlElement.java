/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.html.XHTMLEntities;
import org.ubimix.commons.parser.xml.EntityFactory;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XMLTokenizer;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.TreePresenter;

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

    public static TreePresenter TREE_ACCESSOR = new TreePresenter(KEY_CHILDREN1);

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
            EntityFactory entityFactory = new EntityFactory();
            new XHTMLEntities(entityFactory);
            ITokenizer tokenizer = XMLTokenizer
                .getFullXMLTokenizer(entityFactory);
            fParser = new XmlParser(tokenizer);
        }
        return fParser;
    }

    private static boolean isExcludedAttributeName(String key) {
        boolean excluded = KEY_NAME.equals(key)
            || KEY_CHILDREN1.equals(key)
            || NS.equals(key);
        if (!excluded) {
            excluded = key.startsWith(NS_PREFIX);
        }
        return excluded;
    }

    private static boolean isExcludedAttributeValue(Object value) {
        return (value instanceof Map<?, ?>) || (value instanceof List<?>);
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
        if (!"umx:object".equals(name)) {
            setName(name);
        }
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
        boolean result = addChildObject(node.getObject(), pos);
        if (result) {
            node.setParent(this);
        }
        return result;
    }

    private boolean addChildObject(Object object, int pos) {
        boolean result = TREE_ACCESSOR.addChild(getMap(), pos, object);
        return result;
    }

    public void addPropertyField(String name, XmlNode value) {
        TreePresenter p = new TreePresenter(name);
        Map<Object, Object> map = getMap();
        int len = p.getChildCount(map);
        p.addChild(map, len, value.getObject());
    }

    public String getAttribute(String key) {
        Map<Object, Object> map = getMap();
        Object value = map.get(key);
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            return null;
        }
        return TreePresenter.toString(value);
    }

    public Set<String> getAttributeNames() {
        return getAttributes().keySet();
    }

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
        String name = value != null
            ? TreePresenter.toString(value)
            : "umx:object";
        return name;
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

    public Map<String, XmlNode> getPropertyFields() {
        Map<String, XmlNode> result = new LinkedHashMap<String, XmlNode>();
        Map<Object, Object> map = getMap();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object attr = entry.getKey();
            String key = TreePresenter.toString(attr);
            if (!isExcludedAttributeName(key)) {
                Object value = entry.getValue();
                if (isExcludedAttributeValue(value)) {
                    XmlNode node = newChild(value);
                    if (node instanceof XmlElement) {
                        ((XmlElement) node).setParent(this);
                    }
                    result.put(key, node);
                }
            }
        }
        return result;
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
            Map<String, String> attrs = e.getAttributes();
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String attr = entry.getKey();
                String value = entry.getValue();
                if (attr.startsWith(NS_PREFIX)) {
                    if (namespace.equals(value)) {
                        result = attr.substring(NS_PREFIX.length());
                    }
                } else if (attr.equals(NS)) {
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
            XmlElement e = newElement(newObject());
            e.setName("umx:list");
            e.addChildObject(o, 0);
            result = e;
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
        if (obj == null) {
            obj = newObject();
        }
        return new XmlElement(this, obj);
    }

    public XmlElement newElement(String name) {
        return newElement(newObject()).setName(name);
    }

    @Override
    protected Map<Object, Object> newObject() {
        return new LinkedHashMap<Object, Object>();
    }

    public XmlText newText(String content) {
        XmlText text = new XmlText(this, content);
        return text;
    }

    public XmlElement removeAttribute(String key) {
        if (isExcludedAttributeName(key)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = getMap();
        map.remove(key);
        return this;
    }

    public boolean removeChild(int pos) {
        return TREE_ACCESSOR.removeChild(getMap(), pos);
    }

    public boolean removeChild(XmlNode child) {
        return TREE_ACCESSOR.removeChild(getMap(), child.getObject());
    }

    public void removeChildren() {
        TREE_ACCESSOR.removeChildren(getMap());
    }

    public XmlElement setAttribute(String key, String value) {
        if (isExcludedAttributeName(key)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = getMap();
        map.put(key, value);
        return this;
    }

    public XmlElement setAttributes(Map<String, String> attributes) {
        setValues(attributes, null);
        return this;
    }

    public XmlElement setName(String tagName) {
        Map<Object, Object> map = getMap();
        if (tagName != null) {
            map.put(KEY_NAME, tagName);
        } else {
            map.remove(KEY_NAME);
        }
        return this;
    }

    public XmlElement setNamespaces(Map<String, String> attributes) {
        setValues(attributes, "xmlns");
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
