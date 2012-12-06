package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

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
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        List<IXmlNode> list = Arrays.<IXmlNode> asList(element);
        for (ITagProcessor processor : fProcessors) {
            if (list.isEmpty()) {
                break;
            }
            List<IXmlNode> result = new ArrayList<IXmlNode>();
            for (IXmlNode node : list) {
                if (node instanceof IXmlElement) {
                    IXmlElement e = (IXmlElement) node;
                    List<IXmlNode> r = processor.handle(e, keepSpaces);
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