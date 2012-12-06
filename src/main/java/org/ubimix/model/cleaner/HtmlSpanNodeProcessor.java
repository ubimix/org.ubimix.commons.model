/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

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
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> result;
        if (hasId(element)) {
            result = Arrays.<IXmlNode> asList(element);
        } else {
            result = element.getChildren();
        }
        return result;
    }

}
