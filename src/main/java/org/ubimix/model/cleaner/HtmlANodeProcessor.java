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
public class HtmlANodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlANodeProcessor() {
    }

    private XmlElement getNextBlockElement(XmlElement element) {
        XmlElement result = null;
        XmlElement parent = element.getParent();
        if (parent != null) {
            List<XmlNode> children = parent.getChildren();
            int count = children.size();
            int startPos = -1;
            for (int i = 0; i < count; i++) {
                XmlNode child = children.get(i);
                if (child instanceof XmlElement) {
                    XmlElement e = (XmlElement) child;
                    if (startPos < 0) {
                        if (element.getMap() == e.getMap()) {
                            startPos = i;
                        }
                    } else {
                        String name = e.getName();
                        if (hasId(e)) {
                            break;
                        }
                        if (HtmlTagDictionary.isBlockElement(name)) {
                            result = e;
                        } else {
                            result = getNextBlockElement(e);
                        }
                        if (result != null) {
                            break;
                        }
                    }

                }
            }
        }
        return result;
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        String href = element.getAttribute("href");
        List<XmlNode> result = null;
        if (href == null || "".equals(href) || "#".equals(href)) {
            String id = element.getAttribute("id");
            if (id == null) {
                id = element.getAttribute("name");
            }
            if (id != null) {
                XmlElement block = getNextBlockElement(element);
                boolean ok = false;
                if (block != null && !hasId(block)) {
                    block.setAttribute("id", id);
                    result = element.getChildren();
                    ok = true;
                }
                if (!ok) {
                    element.setName("span");
                }
            } else {
                result = element.getChildren();
            }
        }
        if (result == null) {
            result = Arrays.<XmlNode> asList(element);
        }
        return result;
    }

}
