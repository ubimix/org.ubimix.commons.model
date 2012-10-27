/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class HtmlDivNodeProcessor extends AbstractTagListProcessor {

    /**
     * 
     */
    public HtmlDivNodeProcessor() {
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> children = element.getChildren();
        int len = children.size();
        List<XmlNode> result;
        if (len == 0) {
            result = children;
        } else {
            boolean inlineOnlyNodes = inlineOnlyNodes(children);
            if (inlineOnlyNodes) {
                element.setName(HtmlTagDictionary.P);
            } else {
                len = children.size();
                if (len == 1) {
                    element = (XmlElement) children.get(0);
                } else {
                    element.setChildren(children);
                }
            }
            result = Arrays.<XmlNode> asList(element);
        }
        return result;
    }

    private boolean inlineOnlyNodes(List<XmlNode> children) {
        boolean result = true;
        for (XmlNode child : children) {
            if (child instanceof XmlElement) {
                XmlElement e = (XmlElement) child;
                String name = getHtmlName(e);
                result = HtmlTagDictionary.isInlineElement(name);
                if (!result) {
                    break;
                }
            }
        }
        return result;
    }

}
