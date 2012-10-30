package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class CompositeTagProcessor extends AbstractTagProcessor {

    private List<ITagProcessor> fProcessors = new ArrayList<ITagProcessor>();

    public CompositeTagProcessor addProcessor(ITagProcessor processor) {
        fProcessors.add(processor);
        processor.setParent(this);
        return this;
    }

    @Override
    public List<XmlNode> handle(XmlElement element, boolean keepSpaces) {
        List<XmlNode> list = Arrays.<XmlNode> asList(element);
        for (ITagProcessor processor : fProcessors) {
            if (list.isEmpty()) {
                break;
            }
            List<XmlNode> result = new ArrayList<XmlNode>();
            for (XmlNode node : list) {
                if (node instanceof XmlElement) {
                    XmlElement e = (XmlElement) node;
                    List<XmlNode> r = processor.handle(e, keepSpaces);
                    result.addAll(r);
                } else {
                    result.add(node);
                }
            }
            list = result;
        }
        return list;
    }

    public CompositeTagProcessor removeProcessor(ITagProcessor processor) {
        fProcessors.remove(processor);
        return this;
    }

}