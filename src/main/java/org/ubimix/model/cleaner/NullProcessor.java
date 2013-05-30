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
public class NullProcessor extends AbstractTagProcessor
    implements
    ITagProcessor {

    /**
     * 
     */
    public NullProcessor() {
    }

    /**
     * @see org.ubimix.model.cleaner.ITagProcessor#handle(IXmlElement, boolean)
     */
    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> result;
        if (element.getChildCount() == 0
            && !HtmlTagDictionary.isEmptyElement(element.getName())) {
            result = Arrays.<IXmlNode> asList();
        } else {
            result = Arrays.<IXmlNode> asList(element);
        }
        return result;
    }

}
