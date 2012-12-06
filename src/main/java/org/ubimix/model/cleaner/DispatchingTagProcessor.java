package org.ubimix.model.cleaner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public class DispatchingTagProcessor extends AbstractTagProcessor {

    private ITagProcessor fDefaultProcessor;

    private Map<String, ITagProcessor> fProcessors = new HashMap<String, ITagProcessor>();

    public ITagProcessor getProcessor(String elementName) {
        ITagProcessor result = fProcessors.get(elementName);
        if (result == null) {
            result = fDefaultProcessor;
        }
        return result;
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces) {
        String name = getHtmlName(element);
        ITagProcessor processor = getProcessor(name);
        List<IXmlNode> list = processor.handle(element, keepSpaces);
        return list;
    }

    public DispatchingTagProcessor register(
        ITagProcessor processor,
        String... tags) {
        for (String tag : tags) {
            fProcessors.put(tag, processor);
        }
        processor.setParent(this);
        return this;
    }

    public DispatchingTagProcessor register(String tag, ITagProcessor processor) {
        fProcessors.put(tag, processor);
        processor.setParent(this);
        return this;
    }

    public DispatchingTagProcessor setDefaultProcessor(
        ITagProcessor defaultProcessor) {
        defaultProcessor.setParent(this);
        fDefaultProcessor = defaultProcessor;
        return this;
    }

}