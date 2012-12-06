package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.IXmlCDATA;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;

/**
 * This handler removes excessive empty spaces from text blocks.
 * 
 * @author kotelnikov
 */
public class TextNodeReducer extends AbstractTagProcessor {

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> nodes = element.getChildren();
        List<IXmlNode> result = new ArrayList<IXmlNode>();
        StringBuilder buf = new StringBuilder();
        IXmlFactory factory = element.getFactory();
        int len = nodes.size();
        for (int i = 0; i < len; i++) {
            IXmlNode node = nodes.get(i);
            if ((node instanceof IXmlText) && !(node instanceof IXmlCDATA)) {
                IXmlText text = (IXmlText) node;
                buf.append(text.getContent());
            } else {
                if (buf.length() > 0) {
                    String str = reduceText(buf.toString(), keepSpaces);
                    buf.delete(0, buf.length());
                    IXmlText text = factory.newText(str);
                    result.add(text);
                }
                result.add(node);
            }
        }
        if (buf.length() > 0) {
            String str = reduceText(buf.toString(), keepSpaces);
            buf.delete(0, buf.length());
            IXmlText text = factory.newText(str);
            result.add(text);
        }
        element.setChildren(result);
        return Arrays.<IXmlNode> asList(element);
    }

}