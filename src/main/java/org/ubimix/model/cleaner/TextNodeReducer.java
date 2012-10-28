package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.XmlCDATA;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

/**
 * This handler removes excessive empty spaces from text blocks.
 * 
 * @author kotelnikov
 */
public class TextNodeReducer extends AbstractTagProcessor {

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> nodes = element.getChildren();
        List<XmlNode> result = new ArrayList<XmlNode>();
        StringBuilder buf = new StringBuilder();
        int len = nodes.size();
        for (int i = 0; i < len; i++) {
            XmlNode node = nodes.get(i);
            if ((node instanceof XmlText) && !(node instanceof XmlCDATA)) {
                XmlText text = (XmlText) node;
                buf.append(text.getContent());
            } else {
                if (buf.length() > 0) {
                    String str = reduceText(buf.toString(), keepSpaces);
                    buf.delete(0, buf.length());
                    XmlText text = new XmlText(str);
                    result.add(text);
                }
                result.add(node);
            }
        }
        if (buf.length() > 0) {
            String str = reduceText(buf.toString(), keepSpaces);
            buf.delete(0, buf.length());
            XmlText text = new XmlText(str);
            result.add(text);
        }
        element.setChildren(result);
        return Arrays.<XmlNode> asList(element);
    }

}