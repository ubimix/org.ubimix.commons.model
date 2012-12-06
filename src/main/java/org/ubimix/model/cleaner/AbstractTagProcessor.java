package org.ubimix.model.cleaner;

import java.util.Map;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.IXmlElement;

public abstract class AbstractTagProcessor implements ITagProcessor {

    /**
     * Returns <code>true</code> if the given character is a space character
     * 
     * @param ch the character to check
     * @return <code>true</code> if the given character is a space character
     */
    private static boolean isSpaceChar(char ch) {
        return ch == 160 /* &nbsp; */
            || ch == '\t'
            || ch == '\r'
            || ch == '\n'
            || Character.isSpaceChar(ch);
    }

    protected ITagProcessor fParentProcessor;

    protected String getHtmlName(IXmlElement e) {
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

    protected boolean hasId(IXmlElement element) {
        String id = element.getAttribute(HtmlTagDictionary.ATTR_ID);
        return id != null;
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
        if (txtStr == null) {
            return "";
        }
        char[] array = txtStr.toCharArray();
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        int spaceCount = 0;
        for (int i = 0; i < array.length; i++) {
            char ch = array[i];
            if (isSpaceChar(ch)) {
                if (keepSpaces) {
                    buf.append(ch);
                } else if (spaceCount == 0) {
                    buf.append(' ');
                }
                spaceCount++;
            } else {
                buf.append(ch);
                spaceCount = 0;
            }
        }
        return buf.toString();
    }

    protected void removeUnusedAttributes(IXmlElement e) {
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