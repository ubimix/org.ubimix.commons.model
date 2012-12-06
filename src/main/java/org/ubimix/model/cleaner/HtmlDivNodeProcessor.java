/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

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
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> children = element.getChildren();
        int len = children.size();
        List<IXmlNode> result = null;
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
                    IXmlElement child = (IXmlElement) children.get(0);
                    if (hasId(element)) {
                        if (!hasId(child)) {
                            // Transfert the ID from the parent to the child
                            String id = element
                                .getAttribute(HtmlTagDictionary.ATTR_ID);
                            child.setAttribute(HtmlTagDictionary.ATTR_ID, id);
                            result = Arrays.<IXmlNode> asList(child);
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
            result = Arrays.<IXmlNode> asList(element);
        }
        return result;
    }

    private boolean inlineOnlyNodes(List<IXmlNode> children) {
        boolean result = true;
        for (IXmlNode child : children) {
            if (child instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) child;
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
