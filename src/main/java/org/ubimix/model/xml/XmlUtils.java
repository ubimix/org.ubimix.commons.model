package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.xml.IXmlListener;
import org.ubimix.commons.parser.xml.utils.TextSerializer;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.XmlNode.XmlVisitorWithListener;

/**
 * @author kotelnikov
 */
public class XmlUtils {

    public static void accept(IXmlNode node, IXmlListener listener, boolean deep) {
        XmlVisitorWithListener visitor = new XmlVisitorWithListener(
            listener,
            deep);
        node.accept(visitor);
    }

    public static <T> List<T> getAllChildrenByName(
        IXmlElement e,
        String tagName,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        loadChildrenByName(e, tagName, result, factory, true);
        return result;
    }

    public static IXmlElement getChildByName(IXmlElement element, String name) {
        IXmlElement result = null;
        for (IXmlNode node : element) {
            if (!(node instanceof IXmlElement)) {
                continue;
            }
            IXmlElement e = (IXmlElement) node;
            if (name.equals(e.getName())) {
                result = e;
            }
        }
        return result;
    }

    public static IXmlElement getChildByPath(
        IXmlElement e,
        IValueFactory<IXmlElement> factory,
        String... path) {
        IXmlElement result = getChildByPath(e, path, 0, factory);
        return result;
    }

    public static IXmlElement getChildByPath(IXmlElement e, String... path) {
        return getChildByPath(e, IXmlElement.FACTORY, path);
    }

    private static <T> T getChildByPath(
        IXmlElement element,
        String[] path,
        int pos,
        IValueFactory<T> factory) {
        if (pos >= path.length) {
            return null;
        }
        T result = null;
        String tagName = path[pos];
        for (IXmlNode node : element) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                if (tagName.equals(e.getName())) {
                    if (pos == path.length - 1) {
                        result = factory.newValue(e);
                    } else {
                        result = getChildByPath(element, path, pos + 1, factory);
                    }
                }
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    public static List<IXmlElement> getChildrenByName(
        IXmlElement e,
        String tagName) {
        return getChildrenByName(e, tagName, IXmlElement.FACTORY);
    }

    public static <T> List<T> getChildrenByName(
        IXmlElement e,
        String tagName,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        loadChildrenByName(e, tagName, result, factory, false);
        return result;
    }

    public static List<IXmlElement> getChildrenByNames(
        IXmlElement e,
        Collection<String> tagNames) {
        return getChildrenByNames(e, tagNames, IXmlElement.FACTORY);
    }

    public static <T extends IXmlElement> List<T> getChildrenByNames(
        IXmlElement element,
        Collection<String> tagNames,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        for (IXmlNode node : element) {
            if (!(node instanceof IXmlElement)) {
                continue;
            }
            IXmlElement e = (IXmlElement) node;
            if (tagNames.contains(e.getName())) {
                T resultNode = factory.newValue(e);
                result.add(resultNode);
            }
        }
        return result;
    }

    public static IXmlElement getFirstChildElement(IXmlElement e) {
        IXmlElement result = null;
        for (IXmlNode node : e) {
            if (node instanceof IXmlElement) {
                result = (IXmlElement) node;
                break;
            }
        }
        return result;
    }

    public static Map<String, String> getNamespaces(IXmlElement e) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        while (e != null) {
            Map<String, String> declaredNamespaces = e.getDeclaredNamespaces();
            for (String prefix : declaredNamespaces.keySet()) {
                if (!result.containsKey(prefix)) {
                    String ns = declaredNamespaces.get(prefix);
                    if (!result.containsValue(ns)) {
                        result.put(prefix, ns);
                    }
                }
            }
            e = e.getParent();
        }
        return result;
    }

    public static IXmlElement getOrCreateElement(IXmlElement e, String name) {
        IXmlElement child = getChildByName(e, name);
        if (child == null) {
            IXmlFactory factory = e.getFactory();
            child = factory.newElement(name);
            e.addChild(child);
        }
        return child;
    }

    public static <T> T getOrCreateElement(
        IXmlElement e,
        String name,
        IValueFactory<T> factory) {
        IXmlElement r = getOrCreateElement(e, name);
        return r != null ? factory.newValue(r) : null;
    }

    private static <T> void loadChildrenByName(
        IXmlElement element,
        String tagName,
        List<T> result,
        IValueFactory<T> factory,
        boolean recursive) {
        for (IXmlNode node : element) {
            if (!(node instanceof IXmlElement)) {
                continue;
            }
            IXmlElement e = (IXmlElement) node;
            if (tagName.equals(e.getName())) {
                T resultNode = factory.newValue(e);
                if (resultNode != null) {
                    result.add(resultNode);
                }
            }
            if (recursive) {
                loadChildrenByName(e, tagName, result, factory, recursive);
            }
        }
    }

    public static String lookupNs(IXmlElement e, String prefix) {
        String key = "".equals(prefix) ? IXmlElement.NS : IXmlElement.NS_PREFIX
            + prefix;
        String result = null;
        while (e != null && result == null) {
            result = e.getAttribute(key);
            e = e.getParent();
        }
        return result;
    }

    public static String lookupNsPrefix(IXmlElement e, String namespace) {
        String result = null;
        while (e != null && result == null) {
            Map<String, String> attrs = e.getAttributes();
            for (Map.Entry<String, String> entry : attrs.entrySet()) {
                String attr = entry.getKey();
                String value = entry.getValue();
                if (attr.startsWith(IXmlElement.NS_PREFIX)) {
                    if (namespace.equals(value)) {
                        result = attr.substring(IXmlElement.NS_PREFIX.length());
                    }
                } else if (attr.equals(IXmlElement.NS)) {
                    if (namespace.equals(value)) {
                        result = value;
                    }
                }
            }
            e = e.getParent();
        }
        return result;
    }

    public static <T extends IXmlNode> T newCopy(T node) {
        return newCopy(node, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IXmlNode> T newCopy(T node, boolean deep) {
        IXmlFactory factory = node.getFactory();
        IXmlNode result = null;
        if (node instanceof IXmlElement) {
            XmlBuilder builder = new XmlBuilder(factory);
            accept(node, builder, deep);
            result = builder.getResult();
        } else if (node instanceof IXmlCDATA) {
            result = factory.newCDATA(((IXmlCDATA) node).getContent());
        } else if (node instanceof IXmlText) {
            result = factory.newText(((IXmlText) node).getContent());
        }
        return (T) result;
    }

    protected static XmlPathProcessor newXmlPathProcessor(String cssSelector) {
        return new XmlPathProcessor(cssSelector);
    }

    public static IXmlElement select(IXmlElement element, String cssSelector) {
        return select(element, cssSelector, IXmlElement.FACTORY);
    }

    public static <T extends IXmlNode> T select(
        IXmlElement element,
        String cssSelector,
        final IValueFactory<T> factory) {
        return newXmlPathProcessor(cssSelector).select(element, factory);
    }

    public static List<IXmlElement> selectAll(
        IXmlElement element,
        String cssSelector) {
        return newXmlPathProcessor(cssSelector).selectAll(element);
    }

    public static <T extends IXmlNode> List<T> selectAll(
        IXmlElement element,
        String cssSelector,
        IValueFactory<T> factory) {
        return newXmlPathProcessor(cssSelector).selectAll(element, factory);
    }

    public static <T extends IXmlNode> String toString(
        boolean sortAttributes,
        T... nodes) {
        XmlSerializer listener = new XmlSerializer();
        listener.setSortAttributes(sortAttributes);
        for (IXmlNode node : nodes) {
            accept(node, listener, true);
        }
        return listener.toString();
    }

    public static <T extends IXmlNode> String toString(
        Iterable<T> nodes,
        boolean sortAttributes) {
        XmlSerializer listener = new XmlSerializer();
        listener.setSortAttributes(sortAttributes);
        for (IXmlNode node : nodes) {
            accept(node, listener, true);
        }
        return listener.toString();
    }

    public static String toString(
        IXmlElement e,
        boolean sortAttributes,
        boolean includeElement) {
        if (includeElement) {
            return toString(sortAttributes, e);
        } else {
            Iterable<IXmlNode> nodes = e;
            return toString(nodes, sortAttributes);
        }
    }

    public static String toStringRecursively(
        IXmlNode node,
        boolean sortAttributes) {
        XmlSerializer listener = new XmlSerializer();
        listener.setSortAttributes(sortAttributes);
        accept(node, listener, true);
        return listener.toString();
    }

    public static <T extends IXmlNode> String toText(Iterable<T> nodes) {
        if (nodes == null) {
            return null;
        }
        TextSerializer listener = new TextSerializer();
        for (IXmlNode node : nodes) {
            accept(node, listener, true);
        }
        return listener.toString();
    }

    public static <T extends IXmlNode> String toText(T... nodes) {
        TextSerializer listener = new TextSerializer();
        for (IXmlNode node : nodes) {
            if (node != null) {
                accept(node, listener, true);
            }
        }
        return listener.toString();
    }

}