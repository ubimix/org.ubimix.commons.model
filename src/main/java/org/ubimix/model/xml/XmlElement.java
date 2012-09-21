/**
 * 
 */
package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.model.ValueFactory;
import org.ubimix.model.ValueFactory.IHasValueMap;
import org.ubimix.model.ValueFactory.IJsonValueFactory;
import org.ubimix.model.ValueFactory.IValueList;
import org.ubimix.model.ValueFactory.IValueMap;
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
    public static final IJsonValueFactory<XmlElement> FACTORY = new IJsonValueFactory<XmlElement>() {
        @Override
        public XmlElement newValue(Object object) {
            ValueFactory f = ValueFactory.get();
            if (!f.isMap(object)) {
                return null;
            }
            IValueMap map = f.toMap(object);
            return new XmlElement(null, map);
        }
    };

    private static IXmlParser fParser;

    private static final String NAME_PREFIX = "!";

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

    private static String getName(IValueMap object) {
        String name = getNameKey(object);
        if (name != null && name.startsWith(NAME_PREFIX)) {
            name = name.substring(NAME_PREFIX.length());
        }
        return name;
    }

    private static String getNameKey(IValueMap object) {
        Set<String> attrs = object.getKeys();
        String result = null;
        for (String attr : attrs) {
            if (result == null) {
                result = attr;
            }
            if (attr.startsWith(NAME_PREFIX)) {
                result = attr;
                break;
            }
        }
        return result;
    }

    private static String getNameKey(String tagName) {
        return NAME_PREFIX + tagName;
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
        this(null, object.getValueMap());
    }

    public XmlElement(String name) {
        this(null, null);
        setName(name);
    }

    public XmlElement(XmlElement parent, IValueMap object) {
        super(parent, object);
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
        IValueList container = getChildContainer(true);
        int len = container.getSize();
        if (pos >= 0 && pos <= len) {
            Object nodeObject = node.getObject();
            container.addValue(pos, nodeObject);
            result = true;
        }
        if (result) {
            node.setParent(this);
        }
        return result;
    }

    public String getAttribute(String key) {
        IValueMap object = getObject();
        Object value = object.getValue(key);
        return ValueFactory.get().toString(value);
    }

    public Set<String> getAttributeNames() {
        IValueMap obj = getObject();
        Set<String> set = obj.getKeys();
        Set<String> result = new HashSet<String>(set);
        boolean hasExplicitName = false;
        String firstAttr = null;
        for (String attr : set) {
            if (firstAttr == null) {
                firstAttr = attr;
            }
            if (attr.startsWith(NAME_PREFIX)) {
                hasExplicitName = true;
                result.remove(attr);
            } else if (attr.startsWith(NS_PREFIX)) {
                result.remove(attr);
            } else if (attr.equals(NS)) {
                result.remove(attr);
            }
        }
        if (!hasExplicitName && firstAttr != null) {
            result.remove(firstAttr);
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

    private IValueList getChildContainer(boolean create) {
        IValueMap object = getObject();
        String nameKey = getNameKey(object);
        Object container = nameKey != null ? object.getValue(nameKey) : null;
        ValueFactory f = ValueFactory.get();
        IValueList list = f.isArray(container) ? f.toArray(container) : null;
        if (list == null && create) {
            list = f.newList();
            object.putValue(nameKey, list);
        }
        return list;
    }

    public int getChildCount() {
        IValueList container = getChildContainer(false);
        int result = container != null ? container.getSize() : 0;
        return result;
    }

    protected Object getChildObject(int pos) {
        IValueList container = getChildContainer(false);
        int size = container != null ? container.getSize() : 0;
        return pos >= 0 && pos < size ? container.getValue(pos) : null;
    }

    protected int getChildPosition(XmlNode node) {
        Object nodeObject = node.getObject();
        IValueList container = getChildContainer(false);
        int result = -1;
        int len = container != null ? container.getSize() : 0;
        for (int i = 0; result < 0 && i < len; i++) {
            Object childObject = container.getValue(i);
            if (equals(childObject, nodeObject)) {
                result = i;
            }
        }
        return result;
    }

    public List<XmlNode> getChildren() {
        List<XmlNode> list = new ArrayList<XmlNode>();
        IValueList container = getChildContainer(false);
        int len = container != null ? container.getSize() : 0;
        for (int i = 0; i < len; i++) {
            Object c = container.getValue(i);
            XmlNode node = newChild(c);
            if (node != null) {
                list.add(node);
            }
        }
        return list;
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
        IValueMap object = getObject();
        Set<String> attrs = object.getKeys();
        ValueFactory f = ValueFactory.get();
        for (String attr : attrs) {
            String prefix = null;
            if (attr.startsWith(NS_PREFIX)) {
                prefix = attr.substring(NS_PREFIX.length());
            } else if (attr.equals(NS)) {
                prefix = "";
            }
            if (prefix != null) {
                Object value = object.getValue(attr);
                String str = f.toString(value);
                if (result == null) {
                    result = new LinkedHashMap<String, String>();
                }
                result.put(prefix, str);
            }
        }
        if (result == null) {
            result = Collections.emptyMap();
        }
        return result;
    }

    public String getName() {
        IValueMap object = getObject();
        return getName(object);
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

    @Override
    public IValueMap getObject() {
        IValueMap object = (IValueMap) super.getObject();
        return object;
    }

    @Override
    public IValueMap getValueMap() {
        return getObject();
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
        ValueFactory f = ValueFactory.get();
        if (f.isArray(o)) {
            // FIXME: ???
        } else if (f.isMap(o)) {
            IValueMap map = f.toMap(o);
            result = newElement(map);
        } else if (!isEmpty(o)) {
            String str = f.toString(o);
            if (str.startsWith(XmlCDATA.CDATA_PREFIX)) {
                result = newCDATA(str);
            } else {
                result = newText(str);
            }
        }
        return result;
    }

    private XmlElement newElement(IValueMap obj) {
        return new XmlElement(this, obj);
    }

    @Override
    protected Object newObject() {
        return ValueFactory.get().newMap();
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
        IValueList container = getChildContainer(false);
        int len = container != null ? container.getSize() : 0;
        if (pos >= 0 && pos < len) {
            container.removeValue(pos);
            result = true;
        }
        return result;
    }

    public void removeChildren() {
        IValueMap object = getObject();
        String nameKey = getNameKey(object);
        object.removeValue(nameKey);
    }

    public void setAttribute(String key, String value) {
        if (key.startsWith(NAME_PREFIX)) {
            throw new IllegalArgumentException("Invalid attribute name.");
        }
        IValueMap object = getObject();
        object.putValue(key, value);
    }

    public void setAttributes(Map<String, String> attributes) {
        setValues(attributes, null);
    }

    public void setName(String tagName) {
        IValueMap object = getObject();
        String nameKey = getNameKey(object);
        Object value = "";
        if (nameKey != null && nameKey.startsWith(NAME_PREFIX)) {
            value = object.getValue(nameKey);
            object.removeValue(nameKey);
        }
        nameKey = getNameKey(tagName);
        object.putValue(nameKey, value);
    }

    public void setNamespaces(Map<String, String> attributes) {
        setValues(attributes, "xmlns");
    }

    private void setValues(Map<String, String> attributes, String prefix) {
        IValueMap object = getObject();
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
            object.putValue(key, value);
        }
    }

}
