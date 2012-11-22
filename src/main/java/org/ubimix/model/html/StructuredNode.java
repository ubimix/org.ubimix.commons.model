package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * A common superclass for all objects providing structured access to XML
 * elements.
 * 
 * @author kotelnikov
 */
public class StructuredNode {

    /**
     * @author kotelnikov
     */
    public static class StructuredNodeContainer extends StructuredNode {

        private IValueFactory<? extends Value> fValueFactory;

        public StructuredNodeContainer(
            XmlElement element,
            IValueFactory<? extends Value> factory) {
            super(element);
            fValueFactory = factory;
        }

        @SuppressWarnings("unchecked")
        protected <T extends Value> T cast(Value value) {
            return (T) value;
        }

        public IValueFactory<? extends Value> getValueFactory() {
            return fValueFactory;
        }

        protected <T extends Value> T newValue(XmlElement e) {
            Value value = fValueFactory.newValue(e);
            value.setContainer(this);
            return cast(value);
        }

        public void setValueFactory(IValueFactory<? extends Value> valueFactory) {
            fValueFactory = valueFactory;
        }

    }

    /**
     * @author kotelnikov
     */
    public static class Value extends StructuredNode {

        public static final IValueFactory<Value> FACTORY = new IValueFactory<Value>() {
            @Override
            public Value newValue(Object object) {
                return new Value((XmlElement) object);
            }
        };

        private StructuredNodeContainer fContainer;

        private boolean fTrim = true;

        public Value(XmlElement element) {
            super(element);
        }

        protected void addInnerNodes(
            List<XmlNode> list,
            boolean includeText,
            boolean includeInlineElements,
            boolean includeBlockElements) {
            if (fElement == null) {
                return;
            }
            for (XmlNode node : fElement) {
                boolean include = includeText;
                if (node instanceof XmlElement) {
                    XmlElement e = (XmlElement) node;
                    if (isExcludedElement(e)) {
                        continue;
                    }
                    String name = e.getName();
                    boolean inline = HtmlTagDictionary.isInlineElement(name);
                    include = inline
                        ? includeInlineElements
                        : includeBlockElements;
                }
                if (include) {
                    list.add(node);
                }
            }
        }

        @SuppressWarnings("unchecked")
        protected <T extends Value> T cast(Value value) {
            return (T) value;
        }

        public String getAsString() {
            return trim(XmlNode.toString(getNodes(), true));
        }

        public String getAsText() {
            return trim(XmlNode.toText(getNodes()));
        }

        /**
         * @return a list of inner block elements; inline elements and text
         *         nodes are ignored
         */
        public List<XmlNode> getBlockElements() {
            List<XmlNode> result = new ArrayList<XmlNode>();
            addInnerNodes(result, false, false, true);
            return result;
        }

        public String getBlockElementsAsString() {
            return trim(XmlNode.toString(getBlockElements(), true));
        }

        public String getBlockElementsAsText() {
            return trim(XmlNode.toText(getBlockElements()));
        }

        public StructuredNodeContainer getContainer() {
            return fContainer;
        }

        /**
         * @return a list of all elements (block and inline elements); text
         *         nodes are ignored
         */
        public List<XmlElement> getElements() {
            List<XmlElement> result = new ArrayList<XmlElement>();
            Object o = result;
            @SuppressWarnings("unchecked")
            List<XmlNode> list = (List<XmlNode>) o;
            addInnerNodes(list, false, true, true);
            return result;
        }

        public String getElementsAsString() {
            return trim(XmlNode.toString(getElements(), true));
        }

        public String getElementsAsText() {
            return trim(XmlNode.toText(getElements()));
        }

        /**
         * @return a list of inner inline elements; block elements and text
         *         nodes are ignored
         */
        public List<XmlNode> getInlineElements() {
            List<XmlNode> result = new ArrayList<XmlNode>();
            addInnerNodes(result, false, true, false);
            return result;
        }

        public String getInlineElementsAsString() {
            return trim(XmlNode.toString(getInlineElements(), true));
        }

        public String getInlineElementsAsText() {
            return trim(XmlNode.toText(getInlineElements()));
        }

        /**
         * @return a list of all inner nodes (text, inline elements, block
         *         elements...)
         */
        public List<XmlNode> getNodes() {
            List<XmlNode> result = new ArrayList<XmlNode>();
            addInnerNodes(result, true, true, true);
            return result;
        }

        public XmlElement getReferenceElement() {
            if (fElement == null) {
                return null;
            }
            XmlElement element = fElement.select("a");
            return element;
        }

        @Override
        protected boolean isExcludedElement(XmlElement e) {
            if (fContainer != null) {
                return fContainer.isExcludedElement(e);
            }
            return false;
        }

        public void setContainer(StructuredNodeContainer container) {
            fContainer = container;
        }

        public <T extends Value> T setTrim(boolean trim) {
            fTrim = trim;
            return cast(this);
        }

        protected String trim(String text) {
            if (text == null) {
                text = "";
            }
            if (fTrim) {
                text = text.trim();
            }
            return text;
        }

    }

    protected static <T> T wrapFirstElement(
        Iterable<XmlNode> list,
        IValueFactory<T> factory,
        String... acceptedNames) {
        T result = null;
        for (XmlNode node : list) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                String name = e.getName();
                for (int i = 0; i < acceptedNames.length; i++) {
                    String acceptedName = acceptedNames[i];
                    if (acceptedName.equals(name)) {
                        result = factory.newValue(e);
                        break;
                    }
                }
            }
        }
        return result;
    }

    protected XmlElement fElement;

    /**
     * This constructor sets the content binding and the XML element associated
     * with this object.
     * 
     * @param element the XML element corresponding to this object
     */
    public StructuredNode(XmlElement element) {
        fElement = element;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StructuredNode)) {
            return false;
        }
        StructuredNode o = (StructuredNode) obj;
        XmlElement e = o.getElement();
        return fElement == null || e == null ? fElement == e : fElement
            .equals(e);
    }

    public XmlElement getElement() {
        return fElement;
    }

    @Override
    public int hashCode() {
        XmlElement e = getElement();
        return e != null ? e.hashCode() : 0;
    }

    protected boolean isExcludedElement(XmlElement e) {
        return false;
    }

    @Override
    public String toString() {
        XmlElement e = getElement();
        return e != null ? e.toString() : null;
    }

}