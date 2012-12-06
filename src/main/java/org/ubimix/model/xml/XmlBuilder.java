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

    protected class Context {

        private Map<String, String> fAttributes;

        private IXmlElement fElement;

        private String fName;

        private Map<String, String> fNamespaces;

        private Context fParent;

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

        public void appendChild(IXmlNode node) {
            IXmlElement containerElement = getActiveElement();
            if (containerElement != null) {
                containerElement.addChild(node);
            }
        }

        public IXmlElement getActiveElement() {
            IXmlElement result = fElement;
            if (result == null) {
                result = fParent != null ? fParent.getActiveElement() : null;
            }
            return result;
        }

        protected void init() {
            fElement = newXmlElement(fName)
                .setAttributes(fAttributes)
                .setNamespaces(fNamespaces);
            if (fParent != null) {
                fParent.appendChild(fElement);
            }
        }

        public Context pop() {
            return fParent;
        }

    }

    protected Context fContext;

    private IXmlFactory fFactory;

    protected IXmlElement fTopElement;

    public XmlBuilder() {
        this(XmlFactory.getInstance());
    }

    /**
     * 
     */
    public XmlBuilder(IXmlFactory factory) {
        fFactory = factory;
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

    public IXmlElement getResult() {
        return fTopElement;
    }

    protected boolean isList(String name) {
        return "umx:list".equals(name);
    }

    protected IXmlElement newXmlElement(String name) {
        return fFactory.newElement(name);
    }

    @Override
    public void onCDATA(String content) {
        if (fContext != null) {
            IXmlCDATA node = fFactory.newCDATA(content);
            fContext.appendChild(node);
        }
    }

    @Override
    public void onEntity(Entity entity) {
        if (fContext != null) {
            String text = entity.getChars();
            IXmlText node = fFactory.newText(text);
            fContext.appendChild(node);
        }
    }

    @Override
    public void onText(String text) {
        if (fContext != null) {
            IXmlText node = fFactory.newText(text);
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
