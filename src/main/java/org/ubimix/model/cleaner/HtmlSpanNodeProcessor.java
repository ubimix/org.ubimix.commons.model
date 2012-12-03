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
public class HtmlSpanNodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlSpanNodeProcessor() {
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
