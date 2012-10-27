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
public class NullProcessor extends AbstractTagListProcessor
    implements
    ITagListProcessor {

    /**
     * 
     */
    public NullProcessor() {
    }

    /**
     * @see org.ubimix.model.cleaner.ITagListProcessor#handle(org.ubimix.model.xml.XmlElement,
     *      boolean)
     */
    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        return Arrays.<XmlNode> asList(element);
    }

}
