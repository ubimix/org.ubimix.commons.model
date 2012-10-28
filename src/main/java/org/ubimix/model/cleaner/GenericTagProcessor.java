package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class GenericTagProcessor extends AbstractTagProcessor {

    protected List<XmlNode> cleanupChildren(
        boolean keepSpaces,
        List<XmlNode> children) {
        List<XmlNode> result = new ArrayList<XmlNode>();
        for (XmlNode node : children) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                ITagListProcessor rootProcessor = getRootProcessor();
                List<XmlNode> list = rootProcessor.handle(e, keepSpaces);
                result.addAll(list);
            } else {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        removeUnusedAttributes(element);
        List<XmlNode> children = element.getChildren();
        List<XmlNode> result = cleanupChildren(keepSpaces, children);
        element.setChildren(result);
        return Arrays.<XmlNode> asList(element);
    }

}