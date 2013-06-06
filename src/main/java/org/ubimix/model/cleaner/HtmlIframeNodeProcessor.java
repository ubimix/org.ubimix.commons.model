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
public class HtmlIframeNodeProcessor extends GenericTagProcessor {

    public HtmlIframeNodeProcessor() {
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> result = Arrays.<IXmlNode> asList(element);
        String url = element.getAttribute("src");
        if (url != null && url.contains("vimeo.fr")) {
            // TODO : check the URL of the iframe
        }
        return result;
    }

}
