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
public class HtmlDivNodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlDivNodeProcessor() {
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> children = element.getChildren();
        int len = children.size();
        List<XmlNode> result = null;
        if (len == 0) {
            if (!hasId(element)) {
                result = children;
            }
        } else {
            boolean inlineOnlyNodes = inlineOnlyNodes(children);
            if (inlineOnlyNodes) {
                element.setName(HtmlTagDictionary.P);
            } else {
                len = children.size();
                if (len == 1) {
                    XmlElement child = (XmlElement) children.get(0);
                    if (hasId(element)) {
                        if (!hasId(child)) {
                            // Transfert the ID from the parent to the child
                            String id = element
                                .getAttribute(HtmlTagDictionary.ATTR_ID);
                            child.setAttribute(HtmlTagDictionary.ATTR_ID, id);
                            result = Arrays.<XmlNode> asList(child);
                        }
                    } else {
                        result = children;
                    }
                } else if (!hasId(element)) {
                    // element.setChildren(children);
                    result = children;
                }
            }
        }
        if (result == null) {
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
