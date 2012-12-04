/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

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
     * @see org.ubimix.model.cleaner.ITagProcessor#handle(org.ubimix.model.xml.XmlElement,
     *      boolean)
     */
    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> children = element.getChildren();
        children = wrapInListItems(children);
        int len = children.size();
        List<XmlNode> result;
        if (len == 0) {
            result = children;
        } else {
            element.setChildren(children);
            result = Arrays.<XmlNode> asList(element);
        }
        return result;
    }

    private List<XmlNode> wrapInListItems(List<XmlNode> children) {
        List<XmlNode> result = new ArrayList<XmlNode>();
        List<XmlNode> nodes = new ArrayList<XmlNode>();
        for (XmlNode child : children) {
            if (child instanceof XmlText) {
                XmlText text = (XmlText) child;
                String content = text.getContent();
                if (!isEmpty(content)) {
                    nodes.add(child);
                }
            } else if (child instanceof XmlElement) {
                XmlElement e = (XmlElement) child;
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

    private void wrapNodesInListItem(List<XmlNode> result, List<XmlNode> nodes) {
        if (!nodes.isEmpty()) {
            XmlNode node = nodes.get(0);
            XmlFactory factory = node.getFactory();
            XmlElement li = factory.newElement(HtmlTagDictionary.LI);
            li.setChildren(nodes);
            result.add(li);
            nodes.clear();
        }
    }

}
