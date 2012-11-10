/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class HtmlSpanProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlSpanProcessor() {
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> result;
        if (hasId(element)) {
            result = Arrays.<XmlNode> asList(element);
        } else {
            result = element.getChildren();
        }
        return result;
    }

}
