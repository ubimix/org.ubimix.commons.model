/**
 * 
 */
package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class StructuredTree extends StructuredNode.StructuredNodeContainer {

    public static final IValueFactory<StructuredTree> FACTORY = newStructuredTreeFactory(Value.FACTORY);

    protected static List<XmlElement> getLists(XmlElement element) {
        List<XmlElement> result = new ArrayList<XmlElement>();
        String name = element.getName();
        if (HtmlTagDictionary.isList(name)) {
            result.add(element);
        } else {
            for (XmlNode node : element) {
                if (node instanceof XmlElement) {
                    XmlElement e = (XmlElement) node;
                    name = e.getName();
                    if (HtmlTagDictionary.isList(name)) {
                        result.add(e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * @param factory
     * @return a new {@link IValueFactory} used to create new
     *         {@link StructuredTree} instances
     */
    public static <T extends Value> IValueFactory<StructuredTree> newStructuredTreeFactory(
        final IValueFactory<T> factory) {
        return new IValueFactory<StructuredTree>() {
            @Override
            public StructuredTree newValue(Object object) {
                return new StructuredTree((XmlElement) object, factory);
            }
        };
    }

    /**
     * This method searches a list item (UL or OL) in the given collection of
     * XML nodes; if a list is found then this method returns a
     * {@link StructuredTree} wrapper around it.
     * 
     * @param list a list of elements where a list should be found and wrapperd
     * @return a {@link StructuredNode} wrapper for the first list found in the
     *         specified list of XML nodes
     */
    public static StructuredTree search(Iterable<XmlNode> list) {
        return search(list, Value.FACTORY);
    }

    /**
     * This method searches a list item (UL or OL) in the given collection of
     * XML nodes; if a list is found then this method returns a
     * {@link StructuredTree} wrapper around it.
     * 
     * @param list a list of elements where a list should be found and wrapperd
     * @param factory a factory used to create values
     * @return a {@link StructuredNode} wrapper for the first list found in the
     *         specified list of XML nodes
     */
    public static <T extends Value> StructuredTree search(
        Iterable<XmlNode> list,
        IValueFactory<T> factory) {
        return wrapFirstElement(
            list,
            newStructuredTreeFactory(factory),
            HtmlTagDictionary.UL,
            HtmlTagDictionary.OL);
    }

    /**
     * Recursively search a list in the specified node and if it is found then
     * returns a {@link StructuredTree} wrapper around it. This method returns
     * <code>null</code> if no lists were found.
     * 
     * @param content the list of XML nodes (or an {@link XmlElement})
     * @param valueFactory a factory used to create values
     * @return a {@link StructuredTree} wrapper around a list
     */
    public static <T extends Value> StructuredTree searchTreeRecursively(
        Iterable<XmlNode> content,
        IValueFactory<T> valueFactory) {
        StructuredTree tree = StructuredTree.search(content, valueFactory);
        if (tree == null) {
            for (XmlNode node : content) {
                if (node instanceof XmlElement) {
                    XmlElement e = (XmlElement) node;
                    tree = searchTreeRecursively(e, valueFactory);
                    if (tree != null) {
                        break;
                    }
                }
            }
        }
        return tree;
    }

    /**
     * Cached list of child sub-nodes. This field should never be used directly;
     * use the {@link #getSubtrees()} method instead.
     */
    private List<StructuredTree> fChildren;

    /**
     * A parent of this tree node
     */
    private StructuredTree fParent;

    /**
     * Value object associated with this tree item
     */
    private Value fValue;

    /**
     * Creates a new tree node.
     * 
     * @param parent a parent node; could be <code>null</code>..
     * @param element the element corresponding to this tree item
     * @param factory a factory used to create values
     */
    public StructuredTree(
        StructuredTree parent,
        XmlElement element,
        IValueFactory<? extends Value> factory) {
        super(element, factory);
        fParent = parent;
    }

    /**
     * Creates a new tree node.
     * 
     * @param element the element corresponding to this tree item
     * @param factory a factory used to create values
     */
    public StructuredTree(
        XmlElement element,
        IValueFactory<? extends Value> factory) {
        this(null, element, factory);
    }

    /**
     * Returns the index (position) of this tree item in the parent tree. If
     * this item does not have a parent then this method returns -1.
     * 
     * @return the index (position) of this tree item in the parent tree
     */
    public int getIndex() {
        int result = -1;
        if (fParent != null) {
            List<StructuredTree> children = fParent.getSubtrees();
            result = children.indexOf(this);
        }
        return result;
    }

    /**
     * Returns the next sibling node in this tree structure.
     * 
     * @return the next sibling node in this tree structure
     */
    public StructuredTree getNextSibling() {
        return getSibling(+1);
    }

    public StructuredTree getParent() {
        return fParent;
    }

    /**
     * Returns the previous sibling node in this tree structure.
     * 
     * @return the previous sibling node in this tree structure
     */
    public StructuredTree getPreviousSibling() {
        return getSibling(-1);
    }

    private StructuredTree getSibling(int delta) {
        StructuredTree result = null;
        if (fParent != null) {
            List<StructuredTree> children = fParent.getSubtrees();
            int idx = children.indexOf(this);
            if (idx >= 0) {
                idx += delta;
                if (idx >= 0 && idx < children.size()) {
                    result = children.get(idx);
                }
            }
        }
        return result;
    }

    public int getSize() {
        List<StructuredTree> children = getSubtrees();
        return children.size();
    }

    public StructuredTree getSubtree(int pos) {
        List<StructuredTree> children = getSubtrees();
        StructuredTree result = null;
        if (pos >= 0 && pos <= children.size()) {
            result = children.get(pos);
        }
        return result;
    }

    /**
     * @return a list of all children of this node
     */
    public List<StructuredTree> getSubtrees() {
        if (fChildren == null) {
            fChildren = new ArrayList<StructuredTree>();
            List<XmlElement> lists = getLists(fElement);
            for (XmlElement list : lists) {
                for (XmlNode node : list) {
                    if (node instanceof XmlElement) {
                        XmlElement e = (XmlElement) node;
                        String name = e.getName();
                        if (HtmlTagDictionary.isListItem(name)) {
                            StructuredTree child = newChildTree(e);
                            if (child != null) {
                                fChildren.add(child);
                            }
                        }
                    }
                }
            }
        }
        return fChildren;
    }

    /**
     * @return structured representation for the content of this node
     */
    public <T extends Value> T getValue() {
        if (fValue == null) {
            fValue = newValue(fElement);
        }
        return cast(fValue);
    }

    @Override
    protected boolean isExcludedElement(XmlElement e) {
        String name = e.getName();
        return HtmlTagDictionary.isList(name)
            || HtmlTagDictionary.isListItem(name);
    }

    protected StructuredTree newChildTree(XmlElement e) {
        return new StructuredTree(this, e, getValueFactory());
    }

    public void setParent(StructuredTree parent) {
        fParent = parent;
    }

}
