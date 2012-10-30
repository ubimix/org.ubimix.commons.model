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
public class NullProcessor extends AbstractTagProcessor
    implements
    ITagProcessor {

    /**
     * 
     */
    public NullProcessor() {
    }

    /**
     * @see org.ubimix.model.cleaner.ITagProcessor#handle(org.ubimix.model.xml.XmlElement,
     *      boolean)
     */
    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        return Arrays.<XmlNode> asList(element);
    }

}
