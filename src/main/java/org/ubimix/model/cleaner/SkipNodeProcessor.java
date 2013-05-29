/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.List;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public class SkipNodeProcessor extends AbstractTagProcessor {

    public SkipNodeProcessor() {
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        return element.getChildren();
    }

}
