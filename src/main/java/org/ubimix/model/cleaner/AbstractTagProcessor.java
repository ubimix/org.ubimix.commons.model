package org.ubimix.model.cleaner;

import java.util.Map;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlElement;

public abstract class AbstractTagProcessor implements ITagProcessor {

    protected ITagProcessor fParentProcessor;

    protected String getHtmlName(XmlElement e) {
        return e.getName().toLowerCase();
    }

    @Override
    public ITagProcessor getParentProcessor() {
        return fParentProcessor;
    }

    public ITagProcessor getRootProcessor() {
        ITagProcessor processor = this;
        ITagProcessor parent = processor.getParentProcessor();
        while (parent != null) {
            processor = parent;
            parent = processor.getParentProcessor();
        }
        return processor;
    }

    protected boolean isEmpty(String content) {
        content = reduceText(content, false);
        return "".equals(content) || " ".equals(content);
    }

    protected boolean isExcludedAttribute(String name, String attr) {
        name = name.toLowerCase();
        if (name.startsWith("on")) {
            // Remove all handlers
            return true;
        }
        if (HtmlTagDictionary.isImportantAttribute(name)) {
            // Keep important HTML attributes
            return false;
        }
        if (HtmlTagDictionary.isHtmlAttribute(name)) {
            // Remove all non-important HTML attributes
            return true;
        }
        // Remove other attributes
        return true;
    }

    protected String reduceText(String txtStr, boolean keepSpaces) {
        if (keepSpaces) {
            return txtStr;
        }
        return txtStr.replaceAll("\\s+", " ");
    }

    protected void removeUnusedAttributes(XmlElement e) {
        Map<String, String> attributes = e.getAttributes();
        for (Map.Entry<String, String> attr : attributes.entrySet()) {
            String name = attr.getKey();
            String value = attr.getValue();
            if (isExcludedAttribute(name, value)) {
                e.removeAttribute(name);
            }
        }
    }

    @Override
    public void setParent(ITagProcessor parentProcessor) {
        fParentProcessor = parentProcessor;
    }
}