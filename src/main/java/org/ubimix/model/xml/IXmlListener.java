package org.ubimix.model.xml;

import java.util.Map;

/**
 * @author kotelnikov
 */
public interface IXmlListener {

    /**
     * Default "do nothing" implementation of the {@link IXmlListener}
     * interface.
     * 
     * @author kotelnikov
     */
    public static class XmlListener implements IXmlListener {

        public void beginElement(
            String name,
            Map<String, String> attributes,
            Map<String, String> namespaces) {
        }

        public void endElement(
            String name,
            Map<String, String> attributes,
            Map<String, String> namespaces) {
        }

        public void onCDATA(String content) {
        }

        public void onText(String text) {
        }

    }

    /**
     * This method is called to notify about the beginning of a new element
     * 
     * @param name the qualified name of this element
     * @param attributes element attributes
     * @param namespaces namespaces declared in this element
     */
    void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces);

    /**
     * This method is called to notify about the end of an element
     * 
     * @param name the qualified name of this element
     * @param attributes element attributes
     * @param namespaces namespaces declared in this element
     */
    void endElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces);

    /**
     * This method is called to notify about CDATA blocks
     * 
     * @param content the content of the CDATA block
     */
    void onCDATA(String content);

    /**
     * Text blocks
     * 
     * @param text the content of this text block
     */
    void onText(String text);
}