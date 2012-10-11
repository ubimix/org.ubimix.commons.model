/**
 * 
 */
package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
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

    private static final String KEY_CHILDREN = "~";

    private static final String KEY_NAME = "!";

    public static final String NS = "xmlns";

    public static final String NS_PREFIX = "xmlns:";

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
        boolean result = false;
        if (pos < 0) {
            return result;
        }
        Object innerObject = node.getObject();
        Map<Object, Object> map = getMap();
        Object value = map.get(KEY_CHILDREN);
        if (value == null) {
            map.put(KEY_CHILDREN, innerObject);
            result = true;
        } else {
            if (value instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                if (pos >= 0 && pos <= list.size()) {
                    list.add(pos, innerObject);
                    result = true;
                }
            } else {
                if (pos == 0 || pos == 1) {
                    List<Object> list = new ArrayList<Object>();
                    map.put(KEY_CHILDREN, list);
                    list.add(value);
                    list.add(pos, innerObject);
                    result = true;
                }
            }
        }
        if (result) {
            node.setParent(this);
        }
        return result;
    }

    public String getAttribute(String key) {
        Map<Object, Object> map = getMap();
        Object value = map.get(key);
        return toString(value);
    }

    public Set<String> getAttributeNames() {
        Map<Object, Object> map = getMap();
        Set<Object> set = map.keySet();
        Set<String> result = new LinkedHashSet<String>();
        for (Object attr : set) {
            String key = toString(attr);
            boolean exclude = (KEY_NAME.equals(key)
                || KEY_CHILDREN.equals(key)
                || key.startsWith(NS_PREFIX) || NS.equals(key));
            if (!exclude) {
                result.add(key);
            }
        }
        return result;
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

    private List<Object> getChildContainer1(boolean create) {
        Map<Object, Object> map = getMap();
        List<Object> result = null;
        Object value = map.get(KEY_CHILDREN);
        if (value instanceof List<?>) {
            result = cast(value);
        }
        if (result == null && create) {
            result = new ArrayList<Object>();
            map.put(KEY_CHILDREN, result);
        }
        return result;
    }

    public int getChildCount() {
        Object value = getMap().get(KEY_CHILDREN);
        int result = 0;
        if (value != null) {
            if (value instanceof List<?>) {
                result = ((List<?>) value).size();
            } else {
                result = 1;
            }
        }
        return result;
    }

    protected Object getChildObject(int pos) {
        Object value = getMap().get(KEY_CHILDREN);
        Object result = null;
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = ((List<?>) value);
                result = pos >= 0 && pos < list.size() ? list.get(pos) : null;
            } else {
                result = pos == 0 ? value : null;
            }
        }
        return result;
    }

    protected int getChildPosition(XmlNode node) {
        Object nodeObject = node.getObject();
        int result = -1;
        Object value = getMap().get(KEY_CHILDREN);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                result = list.indexOf(nodeObject);
            } else if (nodeObject.equals(value)) {
                result = 0;
            }
        }
        return result;
    }

    public List<XmlNode> getChildren() {
        List<XmlNode> result = new ArrayList<XmlNode>();
        Map<Object, Object> map = getMap();
        Object value = map.get(KEY_CHILDREN);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                for (Object c : list) {
                    XmlNode node = newChild(c);
                    if (node != null) {
                        result.add(node);
                    }
                }
            } else {
                XmlNode node = newChild(value);
                if (node != null) {
                    result.add(node);
                }
            }
        }
        return result;
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
            String str = toString(attr);
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
                String ns = toString(value);
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
        return toString(value);
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
            String str = toString(o);
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
        boolean result = false;
        if (pos < 0) {
            return result;
        }
        Map<Object, Object> map = getMap();
        Object value = map.get(KEY_CHILDREN);
        if (value != null) {
            if (value instanceof List<?>) {
                List<?> list = (List<?>) value;
                result = pos >= 0
                    && pos < list.size()
                    && list.remove(pos) != null;
                if (list.isEmpty()) {
                    map.remove(KEY_CHILDREN);
                } else if (list.size() == 1) {
                    map.put(KEY_CHILDREN, list.get(0));
                }
            } else if (pos == 0) {
                result = map.remove(KEY_CHILDREN) != null;
            }
        }
        return result;
    }

    public void removeChildren() {
        Map<Object, Object> map = getMap();
        map.remove(KEY_CHILDREN);
    }

    public void setAttribute(String key, String value) {
        if (KEY_NAME.equals(key) || KEY_CHILDREN.equals(key)) {
            throw new IllegalArgumentException();
        }
        Map<Object, Object> map = getMap();
        map.put(key, value);
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

    protected String toString(Object value) {
        return value != null ? value.toString() : null;
    }

}
