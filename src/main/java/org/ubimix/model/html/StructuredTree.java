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

    public static final IValueFactory<StructuredTree> FACTORY = new IValueFactory<StructuredTree>() {
        @Override
        public StructuredTree newValue(Object object) {
            return new StructuredTree((XmlElement) object);
        }
    };

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

    public static StructuredTree search(Iterable<XmlNode> list) {
        return wrapFirstElement(
            list,
            FACTORY,
            HtmlTagDictionary.UL,
            HtmlTagDictionary.OL);
    }

    public static StructuredTree search(
        Iterable<XmlNode> list,
        IValueFactory<? extends Value> valueFactory) {
        StructuredTree tree = search(list);
        if (tree != null) {
            tree.setValueFactory(valueFactory);
        }
        return tree;
    }

    private List<StructuredTree> fChildren;

    private Value fValue;

    public StructuredTree(XmlElement element) {
        this(element, Value.FACTORY);
    }

    public StructuredTree(
        XmlElement element,
        IValueFactory<? extends Value> factory) {
        super(element, factory);
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
    public Value getValue() {
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

    protected StructuredTree newChildTree(XmlElement e) {
        return new StructuredTree(e, getValueFactory());
    }

}
