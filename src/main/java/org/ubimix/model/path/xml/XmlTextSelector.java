/**
 * 
 */
package org.ubimix.model.path.xml;

import org.ubimix.model.xml.XmlText;

public class XmlTextSelector extends TextSelector {

    public XmlTextSelector(String mask) {
        super(mask);
    }

    @Override
    protected String getTextValue(Object node) {
        String result = null;
        if (node instanceof XmlText) {
            XmlText text = (XmlText) node;
            result = text.getContent();
        }
        return result;
    }

}