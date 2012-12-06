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
public class HtmlPreformattedNodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public HtmlPreformattedNodeProcessor() {
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        return Arrays.<IXmlNode> asList(element);
    }

}
