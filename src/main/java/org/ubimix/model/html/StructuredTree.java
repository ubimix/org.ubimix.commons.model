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
public class StructuredTree<T extends StructuredNode.Value>
    extends
    StructuredNode.StructuredNodeContainer<T> {

    public static final IValueFactory<StructuredTree<Value>> FACTORY = newStructuredTreeFactory(Value.FACTORY);

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

    public static <T extends Value> IValueFactory<StructuredTree<T>> newStructuredTreeFactory(
        final IValueFactory<T> factory) {
        return new IValueFactory<StructuredTree<T>>() {
            @Override
            public StructuredTree<T> newValue(Object object) {
                return new StructuredTree<T>((XmlElement) object, factory);
            }
        };
    }

    public static StructuredTree<Value> search(Iterable<XmlNode> list) {
        return search(list, Value.FACTORY);
    }

    public static <T extends Value> StructuredTree<T> search(
        Iterable<XmlNode> list,
        IValueFactory<T> factory) {
        return wrapFirstElement(
            list,
            newStructuredTreeFactory(factory),
            HtmlTagDictionary.UL,
            HtmlTagDictionary.OL);
    }

    private List<StructuredTree<T>> fChildren;

    private T fValue;

    public StructuredTree(XmlElement element, IValueFactory<T> factory) {
        super(element, factory);
    }

    public int getSize() {
        List<StructuredTree<T>> children = getSubtrees();
        return children.size();
    }

    public StructuredTree<T> getSubtree(int pos) {
        List<StructuredTree<T>> children = getSubtrees();
        StructuredTree<T> result = null;
        if (pos >= 0 && pos <= children.size()) {
            result = children.get(pos);
        }
        return result;
    }

    /**
     * @return a list of all children of this node
     */
    public List<StructuredTree<T>> getSubtrees() {
        if (fChildren == null) {
            fChildren = new ArrayList<StructuredTree<T>>();
            List<XmlElement> lists = getLists(fElement);
            for (XmlElement list : lists) {
                for (XmlNode node : list) {
                    if (node instanceof XmlElement) {
                        XmlElement e = (XmlElement) node;
                        String name = e.getName();
                        if (HtmlTagDictionary.isListItem(name)) {
                            StructuredTree<T> child = newChildTree(e);
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
    public T getValue() {
        if (fValue == null) {
            fValue = newValue(fElement);
        }
        return fValue;
    }

    @Override
    protected boolean isExcludedElement(XmlElement e) {
        String name = e.getName();
        return HtmlTagDictionary.isList(name)
            || HtmlTagDictionary.isListItem(name);
    }

    protected StructuredTree<T> newChildTree(XmlElement e) {
        return new StructuredTree<T>(e, getValueFactory());
    }

}
