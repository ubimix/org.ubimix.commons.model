/**
 * 
 */
package org.ubimix.model.xml;

import java.util.Map;

import org.ubimix.commons.parser.xml.Entity;
import org.ubimix.commons.parser.xml.XmlListener;

/**
 * @author kotelnikov
 */
public class XmlBuilder extends XmlListener {

    private class Context {

        private Map<String, String> fAttributes;

        private XmlElement fElement;

        private String fName;

        private Map<String, String> fNamespaces;

        private Context fParent;

        private String fPropertyName;

        public Context(
            Context parent,
            String name,
            Map<String, String> attributes,
            Map<String, String> namespaces) {
            fName = name;
            fAttributes = attributes;
            fNamespaces = namespaces;
            fParent = parent;
            init();
        }

        public void appendChild(XmlNode node) {
            XmlElement containerElement = getActiveElement();
            if (containerElement != null) {
                String propertyName = getPropertyName();
                if (propertyName == null) {
                    containerElement.addChild(node);
                } else {
                    containerElement.addPropertyField(propertyName, node);
                }
            }
        }

        public XmlElement getActiveElement() {
            XmlElement result = fElement;
            if (result == null) {
                result = fParent != null ? fParent.getActiveElement() : null;
            }
            return result;
        }

        private String getPropertyName() {
            return fPropertyName;
        }

        protected void init() {
            if (isPropertyContext()) {
                fPropertyName = fAttributes.get("name");
            } else if (!isListContext()) {
                fElement = newXmlElement(fName)
                    .setAttributes(fAttributes)
                    .setNamespaces(fNamespaces);
                if (fParent != null) {
                    fParent.appendChild(fElement);
                }
            }
            if (fPropertyName == null && fParent != null) {
                fPropertyName = fParent.getPropertyName();
            }
        }

        protected boolean isListContext() {
            return "umx:list".equals(fName);
        }

        protected boolean isPropertyContext() {
            return "umx:property".equals(fName);
        }

        public Context pop() {
            return fParent;
        }

    }

    private Context fContext;

    public XmlElement fTopElement;

    /**
     * 
     */
    public XmlBuilder() {
    }

    @Override
    public void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        fContext = new Context(fContext, name, attributes, namespaces);
        if (fTopElement == null) {
            fTopElement = fContext.getActiveElement();
        }
    }

    @Override
    public void endElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        if (fContext != null) {
            Context parentContext = fContext.pop();
            if (parentContext == null) {
                fTopElement = fContext.getActiveElement();
            }
            fContext = parentContext;
        }
    }

    public XmlElement getResult() {
        return fTopElement;
    }

    protected boolean isList(String name) {
        return "umx:list".equals(name);
    }

    protected XmlElement newXmlElement(String name) {
        return new XmlElement(name);
    }

    @Override
    public void onCDATA(String content) {
        if (fContext != null) {
            XmlCDATA node = new XmlCDATA(content);
            fContext.appendChild(node);
        }
    }

    @Override
    public void onEntity(Entity entity) {
        if (fContext != null) {
            String text = entity.getChars();
            XmlText node = new XmlText(text);
            fContext.appendChild(node);
        }
    }

    @Override
    public void onText(String text) {
        if (fContext != null) {
            XmlText node = new XmlText(text);
            fContext.appendChild(node);
        }
    }

    public void reset() {
        fContext = null;
        fTopElement = null;
    }

    @Override
    public String toString() {
        return fTopElement == null ? null : fTopElement.toString();
    }

}
