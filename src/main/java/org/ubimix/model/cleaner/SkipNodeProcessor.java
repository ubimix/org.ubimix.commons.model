/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.List;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class SkipNodeProcessor extends AbstractTagProcessor {

    /**
     * 
     */
    public SkipNodeProcessor() {
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        return element.getChildren();
    }

}
