package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public class GenericTagProcessor extends AbstractTagProcessor {

    protected List<IXmlNode> cleanupChildren(
        boolean keepSpaces,
        List<IXmlNode> children) {
        List<IXmlNode> result = new ArrayList<IXmlNode>();
        for (IXmlNode node : children) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                ITagProcessor rootProcessor = getRootProcessor();
                List<IXmlNode> list = rootProcessor.handle(e, keepSpaces);
                result.addAll(list);
            } else {
                result.add(node);
            }
        }
        return result;
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        removeUnusedAttributes(element);
        List<IXmlNode> children = element.getChildren();
        List<IXmlNode> result = cleanupChildren(keepSpaces, children);
        element.setChildren(result);
        return Arrays.<IXmlNode> asList(element);
    }

}