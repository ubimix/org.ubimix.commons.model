/**
 * 
 */
package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.html.XHTMLEntities;
import org.ubimix.commons.parser.xml.EntityFactory;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XMLTokenizer;
import org.ubimix.commons.parser.xml.XmlParser;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.TreePresenter;
import org.ubimix.model.selector.IPathNodeCollector;
import org.ubimix.model.selector.IPathSelector;
import org.ubimix.model.selector.PathProcessor;
import org.ubimix.model.selector.utils.CssPathSelectorBuilder;
import org.ubimix.model.selector.utils.TreeNodeProvider;

/**
 * @author kotelnikov
 */
public class XmlElement extends XmlNode
    implements
    Iterable<XmlNode>,
    IHasValueMap {

    public static abstract class AbstractXmlElementFactory<T extends XmlElement>
        implements
        IValueFactory<T> {
        @Override
        public T newValue(Object object) {
            XmlElement parent = null;
            Map<Object, Object> map = null;
            if (object instanceof IHasValueMap) {
                map = ((IHasValueMap) object).getMap();
                if (object instanceof XmlNode) {
                    parent = ((XmlNode) object).getParent();
                }
            } else if (object instanceof Map<?, ?>) {
                map = cast(object);
            }
            return map != null ? newXmlElement(parent, map) : null;
        }

        protected abstract T newXmlElement(
            XmlElement parent,
            Map<Object, Object> map);
    }

    /**
     * Creates and returns {@link XmlElement} instance wrapping the specified
     * java value.
     */
    public static final IValueFactory<XmlElement> FACTORY = new AbstractXmlElementFactory<XmlElement>() {
        @Override
        protected XmlElement newXmlElement(
            XmlElement parent,
            Map<Object, Object> map) {
            return new XmlElement(parent, map);
        }
    };

    private static IXmlParser fParser;

    private static final String KEY_CHILDREN = "~";

    private static final String KEY_NAME = "!";

    public static final String NS = "xmlns";

    public static final String NS_PREFIX = "xmlns:";

    public static TreePresenter TREE_ACCESSOR = new TreePresenter(KEY_CHILDREN);

    public static TreeNodeProvider XML_TREE_NODE_PROVIDER = new TreeNodeProvider(
        XmlElement.TREE_ACCESSOR) {
        @Override
        protected IValueFactory<?> getChildNodeFactory(IHasValueMap element) {
            return ((XmlElement) element).getNodeFactory();
        }
    };

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

    public static XmlElement parse(ICharStream stream) {
        XmlBuilder builder = new XmlBuilder();
        IXmlParser parser = getParser();
        parser.parse(stream, builder);
        return builder.getResult();
    }

    public static XmlElement parse(String xml) {
        ICharStream stream = new CharStream(xml);
        return parse(stream);
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

    public void addChildren(Iterable<XmlNode> children) {
        for (XmlNode child : children) {
            addChild(child);
        }
    }

    public void addPropertyField(String name, XmlNode value) {
        TreePresenter p = new TreePresenter(name);
        Map<Object, Object> map = getMap();
        int len = p.getChildCount(map);
        p.addChild(map, len, value.getObject());
    }

    public XmlElement addText(String str) {
        addChild(new XmlText(str));
        return this;
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

    public XmlElement getChildByName(String name) {
        XmlElement result = null;
        for (XmlNode node : this) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            if (name.equals(e.getName())) {
                result = e;
            }
        }
        return result;
    }

    public <T extends XmlElement> T getChildByName(
        String tagName,
        IValueFactory<T> factory) {
        T result = null;
        for (XmlNode node : this) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            if (tagName.equals(e.getName())) {
                result = factory.newValue(e);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public XmlElement getChildByPath(
        IValueFactory<XmlElement> factory,
        String... path) {
        XmlElement result = getChildByPath(path, 0, factory);
        return result;
    }

    public XmlElement getChildByPath(String... path) {
        return getChildByPath(XmlElement.FACTORY, path);
    }

    private <T> T getChildByPath(
        String[] path,
        int pos,
        IValueFactory<T> factory) {
        if (pos >= path.length) {
            return null;
        }
        T result = null;
        String tagName = path[pos];
        for (XmlNode node : this) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                if (tagName.equals(e.getName())) {
                    if (pos == path.length - 1) {
                        result = factory.newValue(e);
                    } else {
                        result = getChildByPath(path, pos + 1, factory);
                    }
                }
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public int getChildCount() {
        return TREE_ACCESSOR.getChildCount(getMap());
    }

    public XmlElement getChildElement() {
        XmlElement result = null;
        for (XmlNode node : this) {
            if (node instanceof XmlElement) {
                result = (XmlElement) node;
                break;
            }
        }
        return result;
    }

    public List<XmlElement> getChildElements() {
        List<XmlElement> result = new ArrayList<XmlElement>();
        for (XmlNode node : this) {
            if (node instanceof XmlElement) {
                result.add((XmlElement) node);
            }
        }
        return result;
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

    public List<XmlElement> getChildrenByName(String tagName) {
        return getChildrenByName(tagName, XmlElement.FACTORY);
    }

    public <T extends XmlElement> List<T> getChildrenByName(
        String tagName,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        for (XmlNode node : this) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            if (tagName.equals(e.getName())) {
                T resultNode = factory.newValue(e);
                result.add(resultNode);
            }
        }
        return result;
    }

    public List<XmlElement> getChildrenByNames(Collection<String> tagNames) {
        return getChildrenByNames(tagNames, XmlElement.FACTORY);
    }

    public <T extends XmlElement> List<T> getChildrenByNames(
        Collection<String> tagNames,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        for (XmlNode node : this) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            if (tagNames.contains(e.getName())) {
                T resultNode = factory.newValue(e);
                result.add(resultNode);
            }
        }
        return result;
    }

    public <T extends XmlElement> List<T> getChildrenByPath(
        IValueFactory<T> factory,
        String... path) {
        List<T> result = new ArrayList<T>();
        getChildrenByPath(result, path, 0, factory);
        return result;
    }

    private <T> void getChildrenByPath(
        List<T> result,
        String[] path,
        int pos,
        IValueFactory<T> factory) {
        if (pos >= path.length) {
            return;
        }
        String tagName = path[pos];
        for (XmlNode node : this) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                if (tagName.equals(e.getName())) {
                    if (pos == path.length - 1) {
                        T resultNode = factory.newValue(e);
                        result.add(resultNode);
                    } else {
                        getChildrenByPath(result, path, pos + 1, factory);
                    }
                }
            }
        }
    }

    public List<XmlElement> getChildrenByPath(String... path) {
        return getChildrenByPath(XmlElement.FACTORY, path);
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

    public XmlElement getOrCreateElement(String name) {
        return getOrCreateElement(this, name);
    }

    public XmlElement getOrCreateElement(XmlElement e, String name) {
        XmlElement child = e.getChildByName(name);
        if (child == null) {
            child = new XmlElement(name);
            addChild(child);
        }
        return child;
    }

    protected PathProcessor getPathProcessor(String cssSelector) {
        IPathSelector selector = CssPathSelectorBuilder.INSTANCE
            .build(cssSelector);
        PathProcessor pathProcessor = new PathProcessor(
            XML_TREE_NODE_PROVIDER,
            selector);
        return pathProcessor;
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
            if (XmlCDATA.isCDATA(str)) {
                result = newCDATA(str);
            } else {
                result = newText(str);
            }
        }
        return result;
    }

    @Override
    public XmlElement newCopy(boolean depth) {
        Map<Object, Object> thisMap = getMap();
        Map<Object, Object> map;
        if (depth) {
            map = TreePresenter.copy(thisMap);
        } else {
            map = new LinkedHashMap<Object, Object>();
            for (Map.Entry<Object, Object> entry : thisMap.entrySet()) {
                Object key = entry.getKey();
                if (KEY_CHILDREN.equals(key)) {
                    continue;
                }
                // Copy all non-object fields
                Object value = entry.getValue();
                if (!isExcludedAttributeValue(value)) {
                    map.put(key, value);
                }
            }
        }
        XmlElement result = new XmlElement(null, map);
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

    public void remove() {
        XmlElement parent = getParent();
        if (parent != null) {
            parent.removeChild(this);
        }
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

    public XmlElement select(String cssSelector) {
        return select(cssSelector, XmlElement.FACTORY);
    }

    @SuppressWarnings("unchecked")
    public <T extends XmlNode> T select(
        String cssSelector,
        final IValueFactory<T> factory) {
        final Object[] results = { null };
        PathProcessor processor = getPathProcessor(cssSelector);
        processor.select(this, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                results[0] = factory.newValue(node);
                return false;
            }
        });
        return (T) results[0];
    }

    public List<XmlElement> selectAll(String cssSelector) {
        return selectAll(cssSelector, XmlElement.FACTORY);
    }

    public <T extends XmlNode> List<T> selectAll(
        String cssSelector,
        final IValueFactory<T> factory) {
        final List<T> results = new ArrayList<T>();
        PathProcessor processor = getPathProcessor(cssSelector);
        processor.select(this, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                T value = factory.newValue(node);
                results.add(value);
                return true;
            }
        });
        return results;
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

    public XmlElement setChildren(Iterable<XmlNode> children) {
        removeChildren();
        addChildren(children);
        return this;
    }

    public XmlElement setChildren(XmlNode... children) {
        return setChildren(Arrays.asList(children));
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

    public XmlElement setText(String str) {
        setChildren(new XmlText(str));
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

    public String toString(boolean sortAttributes, boolean includeElement) {
        if (includeElement) {
            return super.toString(sortAttributes);
        } else {
            return toString(this, sortAttributes);
        }
    }

}
