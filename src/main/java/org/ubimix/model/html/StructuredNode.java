package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.XmlUtils;
import org.ubimix.model.xml.XmlWrapper;

/**
 * A common superclass for all objects providing structured access to XML
 * elements.
 * 
 * @author kotelnikov
 */
public class StructuredNode extends XmlWrapper {

    /**
     * @author kotelnikov
     */
    public static class StructuredNodeContainer extends StructuredNode {

        private IValueFactory<? extends Value> fValueFactory;

        public StructuredNodeContainer(
            IXmlElement element,
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

        protected <T extends Value> T newValue(IXmlElement e) {
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
                return new Value((IXmlElement) object);
            }
        };

        private StructuredNodeContainer fContainer;

        private boolean fTrim = true;

        public Value(IXmlElement element) {
            super(element);
        }

        protected void addInnerNodes(
            List<IXmlNode> list,
            boolean includeText,
            boolean includeInlineElements,
            boolean includeBlockElements) {
            addInnerNodes(
                list,
                fElement,
                includeText,
                includeInlineElements,
                includeBlockElements);
        }

        protected void addInnerNodes(
            List<IXmlNode> list,
            IXmlElement element,
            boolean includeText,
            boolean includeInlineElements,
            boolean includeBlockElements) {
            if (element == null) {
                return;
            }
            for (IXmlNode node : element) {
                boolean include = includeText;
                if (node instanceof IXmlElement) {
                    IXmlElement e = (IXmlElement) node;
                    if (isExcludedElement(e)) {
                        continue;
                    }
                    String name = e.getName();
                    boolean inline = HtmlTagDictionary.isInlineElement(name);
                    include = inline
                        ? includeInlineElements
                        : includeBlockElements;
                    if (!include) {
                        addInnerNodes(
                            list,
                            e,
                            includeText,
                            includeInlineElements,
                            includeBlockElements);
                    }
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
            return toTrimmedString(getNodes(), true);
        }

        public String getAsText() {
            return trim(XmlUtils.toText(getNodes()));
        }

        /**
         * @return a list of inner block elements; inline elements and text
         *         nodes are ignored
         */
        public List<IXmlNode> getBlockElements() {
            List<IXmlNode> result = new ArrayList<IXmlNode>();
            addInnerNodes(result, false, false, true);
            return result;
        }

        public String getBlockElementsAsString() {
            return toTrimmedString(getBlockElements(), true);
        }

        public String getBlockElementsAsText() {
            return toTrimmedText(getBlockElements());
        }

        public StructuredNodeContainer getContainer() {
            return fContainer;
        }

        /**
         * @return a list of all elements (block and inline elements); text
         *         nodes are ignored
         */
        public List<IXmlElement> getElements() {
            List<IXmlElement> result = new ArrayList<IXmlElement>();
            Object o = result;
            @SuppressWarnings("unchecked")
            List<IXmlNode> list = (List<IXmlNode>) o;
            addInnerNodes(list, false, true, true);
            return result;
        }

        public String getElementsAsString() {
            return toTrimmedString(getElements(), true);
        }

        public String getElementsAsText() {
            return toTrimmedText(getElements());
        }

        /**
         * @return a list of inner inline elements; block elements and text
         *         nodes are ignored
         */
        public List<IXmlNode> getInlineElements() {
            List<IXmlNode> result = new ArrayList<IXmlNode>();
            addInnerNodes(result, false, true, false);
            return result;
        }

        public String getInlineElementsAsString() {
            return toTrimmedString(getInlineElements(), true);
        }

        public String getInlineElementsAsText() {
            return toTrimmedText(getInlineElements());
        }

        /**
         * @return a list of inner inline elements; block elements and text
         *         nodes are ignored
         */
        public List<IXmlNode> getInlineNodes() {
            List<IXmlNode> result = new ArrayList<IXmlNode>();
            addInnerNodes(result, true, true, false);
            return result;
        }

        /**
         * @return a list of all inner nodes (text, inline elements, block
         *         elements...)
         */
        public List<IXmlNode> getNodes() {
            List<IXmlNode> result = new ArrayList<IXmlNode>();
            addInnerNodes(result, true, true, true);
            return result;
        }

        public IXmlElement getReferenceElement() {
            if (fElement == null) {
                return null;
            }
            IXmlElement element = select("a");
            return element;
        }

        @Override
        protected boolean isExcludedElement(IXmlElement e) {
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

        private String toTrimmedString(
            List<? extends IXmlNode> list,
            boolean sort) {
            return trim(XmlUtils.toString(list, sort));
        }

        private String toTrimmedText(List<? extends IXmlNode> nodes) {
            return trim(XmlUtils.toText(nodes));
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
        Iterable<IXmlNode> list,
        IValueFactory<T> factory,
        String... acceptedNames) {
        T result = null;
        for (IXmlNode node : list) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
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

    /**
     * This constructor sets the content binding and the XML element associated
     * with this object.
     * 
     * @param element the XML element corresponding to this object
     */
    public StructuredNode(IXmlElement element) {
        super(element);
    }

    protected boolean isExcludedElement(IXmlElement e) {
        return false;
    }

}