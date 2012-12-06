/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;

/**
 * @author kotelnikov
 */
public class HtmlListNodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlListNodeProcessor() {
    }

    /**
     * @see org.ubimix.model.cleaner.ITagProcessor#handle(IXmlElement, boolean)
     */
    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> children = element.getChildren();
        children = wrapInListItems(children);
        int len = children.size();
        List<IXmlNode> result;
        if (len == 0) {
            result = children;
        } else {
            element.setChildren(children);
            result = Arrays.<IXmlNode> asList(element);
        }
        return result;
    }

    private List<IXmlNode> wrapInListItems(List<IXmlNode> children) {
        List<IXmlNode> result = new ArrayList<IXmlNode>();
        List<IXmlNode> nodes = new ArrayList<IXmlNode>();
        for (IXmlNode child : children) {
            if (child instanceof IXmlText) {
                IXmlText text = (IXmlText) child;
                String content = text.getContent();
                if (!isEmpty(content)) {
                    nodes.add(child);
                }
            } else if (child instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) child;
                String name = getHtmlName(e);
                if (HtmlTagDictionary.LI.equals(name)
                    || !HtmlTagDictionary.isHtmlElement(name)) {
                    wrapNodesInListItem(result, nodes);
                    result.add(child);
                } else {
                    nodes.add(child);
                }
            }
        }
        wrapNodesInListItem(result, nodes);
        return result;
    }

    private void wrapNodesInListItem(List<IXmlNode> result, List<IXmlNode> nodes) {
        if (!nodes.isEmpty()) {
            IXmlNode node = nodes.get(0);
            IXmlFactory factory = node.getFactory();
            IXmlElement li = factory.newElement(HtmlTagDictionary.LI);
            li.setChildren(nodes);
            result.add(li);
            nodes.clear();
        }
    }

}
