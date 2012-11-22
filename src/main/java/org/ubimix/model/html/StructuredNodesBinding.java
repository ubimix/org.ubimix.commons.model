package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.html.StructuredNode.StructuredNodeContainer;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * This class is used to associate "structured nodes" with XML elements and
 * manage them. These nodes could be used to give typed access to individual XML
 * elements. For example it could be used to represent an HTML table as a
 * sequence of key/value pairs. Or a set of <code>div</code> tags as an image
 * with a description etc.
 * 
 * @author kotelnikov
 */
public class StructuredNodesBinding {

    /**
     * This implementation of the {@link IStructuredNodesBinder} interface is
     * used to associate individual binder instances with XML tag names.
     * 
     * @author kotelnikov
     */
    public static class DispatchingStructureBinder implements IStructureBinder {

        /**
         * This map contains tag names with a list of binders used to create
         * widgets for XML elements with this name.
         */
        private Map<String, List<IStructureBinder>> fMap = new HashMap<String, List<IStructureBinder>>();

        /**
         * Associates a binder with specified tags.
         * 
         * @param binder a binder to add
         * @param tagNames an array of tag names to associate with the binder
         */
        public DispatchingStructureBinder addBinder(
            IStructureBinder binder,
            String... tagNames) {
            for (String tagName : tagNames) {
                tagName = checkTagName(tagName);
                List<IStructureBinder> list = fMap.get(tagName);
                if (list == null) {
                    list = new ArrayList<IStructureBinder>();
                    fMap.put(tagName, list);
                }
                list.add(binder);
            }
            return this;
        }

        /**
         * Associates a binder with the specified tag.
         * 
         * @param tagName the name of the tag
         * @param binder the binder to add name to associate with the binder
         */
        public DispatchingStructureBinder addBinder(
            String tagName,
            IStructureBinder binder) {
            return addBinder(binder, tagName);
        }

        @Override
        public StructuredNodeContainer bind(
            StructuredNodesBinding binding,
            XmlElement e) {
            StructuredNodeContainer result = null;
            String name = checkTagName(e.getName());
            List<IStructureBinder> list = fMap.get(name);
            if (list != null) {
                for (IStructureBinder binder : list) {
                    result = binder.bind(binding, e);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }

        /**
         * Returns the normalized tag name.
         * 
         * @param tagName the tag name to normalize
         * @return the normalized tag name
         */
        private String checkTagName(String tagName) {
            tagName = tagName.toLowerCase();
            return tagName;
        }
    }

    /**
     * Instances of this type are used by the {@link StructuredNodesBinding}
     * class to associate structured nodes with individual XML elements.
     * 
     * @author kotelnikov
     */
    public interface IStructureBinder {

        /**
         * Creates and returns a structured node associated with the specified
         * XML element; this method could return <code>null</code> if the
         * element could/should not be bind with a structured node.
         * 
         * @param binding the {@link StructuredNodesBinding} instance calling
         *        this method; this object is used as a context for structured
         *        nodes
         * @param e an XML element
         * @return a structured node corresponding to the specified XML element
         */
        StructuredNodeContainer bind(
            StructuredNodesBinding binding,
            XmlElement e);

    }

    /**
     * The name of an XML element attribute used to keep a structured node
     * identifier in XML elements. This class adds this attribute to all XML
     * elements associated with structured nodes.
     */
    public static String ATTR_BINDING_ID = "data-binding-id";

    /**
     * The root binder used as a factory for structured nodes.
     */
    private IStructureBinder fBinder;

    /**
     * Internal counter for structured node identifiers
     */
    private int fIdCounter;

    /**
     * This map is used to keep identifiers and the corresponding structured
     * nodes.
     */
    private Map<String, StructuredNodeContainer> fMap = new HashMap<String, StructuredNodeContainer>();

    /**
     * The value factory used to create {@link Value} instances
     */
    private IValueFactory<? extends Value> fValueFactory;

    /**
     * The main constructor. Initializes internal fields and sets the given
     * binder used as a factory for structured nodes.
     * 
     * @param factory the value factory used to create {@link Value} instances
     * @param binder the factory for structured nodes
     */
    public StructuredNodesBinding(
        IValueFactory<? extends Value> factory,
        IStructureBinder binder) {
        fValueFactory = factory;
        fBinder = binder;
    }

    /**
     * Initiales the internal fields and binds structured nodes to the specified
     * XML element and to its children.
     * 
     * @param factory the value factory used to create {@link Value} instances
     * @param binder the binder used to create structured nodes
     * @param element the XML element to bind with the structured nodes
     */
    public StructuredNodesBinding(
        IValueFactory<? extends Value> factory,
        IStructureBinder binder,
        XmlElement element) {
        this(factory, binder);
        bindStructuredNodes(element);
    }

    /**
     * Recursively binds structured nodes to XML elements. All bound structured
     * nodes are stored in an internal map and could be retrieved using the
     * {@link #getStructuredNode(XmlElement)} or
     * {@link #getStructuredNode(XmlElement, Class)} methods.
     * 
     * @param content container of XML nodes to associate with structured nodes
     */
    public void bindStructuredNodes(Iterable<XmlNode> content) {
        if (fBinder == null) {
            return;
        }
        for (XmlNode node : content) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            StructuredNodeContainer s = fBinder.bind(this, e);
            if (s != null) {
                String id = newId();
                e.setAttribute(ATTR_BINDING_ID, id);
                fMap.put(id, s);
            } else {
                bindStructuredNodes(e);
            }
        }
    }

    /**
     * Searches and returns the first structured node of the specified type.
     * 
     * @param type the type of the structured node
     * @return the structured node of the specified type
     */
    @SuppressWarnings("unchecked")
    public <N extends StructuredNodeContainer> N getStructuredNode(Class<N> type) {
        N result = null;
        for (StructuredNodeContainer w : fMap.values()) {
            if (type.isInstance(w)) {
                result = (N) w;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a content structured node associated with the specified XML
     * element (or <code>null</code> if this element has no corresponding
     * structured nodes).
     * 
     * @param e an XML element to check
     * @return a structured node associated with the given element
     */
    public StructuredNodeContainer getStructuredNode(XmlElement e) {
        StructuredNodeContainer result = null;
        String id = getStructuredNodeId(e);
        if (id != null) {
            result = fMap.get(id);
        }
        return result;
    }

    /**
     * Returns an identifier of a structured node associated with the specified
     * XML element.
     * 
     * @param e an XML element to check
     * @return an identifier of a structured node
     */
    public String getStructuredNodeId(XmlElement e) {
        String id = e.getAttribute(ATTR_BINDING_ID);
        return id;
    }

    /**
     * Returns a list of all structured nodes of the specified type.
     * 
     * @param type the type of structured nodes to return
     * @return a list of all structured nodes of the specified type
     */
    @SuppressWarnings("unchecked")
    public <N extends StructuredNodeContainer> List<N> getStructuredNodes(
        Class<?> type) {
        List<N> result = new ArrayList<N>();
        for (StructuredNodeContainer w : fMap.values()) {
            if (type.isInstance(w)) {
                result.add((N) w);
            }
        }
        return result;
    }

    /**
     * @return a value factory used to create {@link Value} instances
     */
    public IValueFactory<? extends Value> getValueFactory() {
        return fValueFactory;
    }

    /**
     * @return a newly create identifier
     */
    private String newId() {
        return "binding-" + (fIdCounter++);
    }

    /**
     * Sets a new factory for {@link Value} instances
     * 
     * @param valueFactory the factory to set
     */
    public void setValueFactory(IValueFactory<? extends Value> valueFactory) {
        fValueFactory = valueFactory;
    }
}