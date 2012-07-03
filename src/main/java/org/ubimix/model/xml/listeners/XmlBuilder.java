/**
 * 
 */
package org.ubimix.model.xml.listeners;

import java.util.Map;
import java.util.Stack;

import org.ubimix.model.xml.IXmlListener;
import org.ubimix.model.xml.XmlCDATA;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class XmlBuilder implements IXmlListener {

    private Stack<XmlElement> fStack = new Stack<XmlElement>();

    public XmlElement fTopElement;

    /**
     * 
     */
    public XmlBuilder() {
    }

    public void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        XmlElement element = new XmlElement(name);
        element.setAttributes(attributes);
        element.setNamespaces(namespaces);
        XmlElement parent = getParent();
        if (parent != null) {
            parent.addChild(element);
        }
        fStack.push(element);
        if (fTopElement == null) {
            fTopElement = element;
        }
    }

    public void endElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        fStack.pop();
    }

    protected XmlElement getParent() {
        XmlElement parent = !fStack.isEmpty() ? fStack.peek() : null;
        return parent;
    }

    public XmlElement getResult() {
        return fTopElement;
    }

    public void onCDATA(String content) {
        XmlElement parent = getParent();
        if (parent != null) {
            XmlCDATA node = new XmlCDATA(content);
            parent.addChild(node);
        }
    }

    public void onText(String text) {
        XmlElement parent = getParent();
        if (parent != null) {
            XmlText node = new XmlText(text);
            parent.addChild(node);
        }
    }

    public void reset() {
        fTopElement = null;
        fStack.clear();
    }

    @Override
    public String toString() {
        return fTopElement == null ? null : fTopElement.toString();
    }

}
