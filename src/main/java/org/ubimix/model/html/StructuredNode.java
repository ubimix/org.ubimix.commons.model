package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class StructuredNode {

    /**
     * @author kotelnikov
     */
    public static class Value extends StructuredNode {

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
                    String name = e.getName();
                    boolean inline = HtmlTagDictionary
                        .isInlineElement(name);
                    include = inline
                        ? includeInlineElements
                        : includeBlockElements;
                }
                if (include) {
                    list.add(node);
                }
            }
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

        public StructuredNode.Value setTrim(boolean trim) {
            fTrim = trim;
            return this;
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

    protected XmlElement fElement;

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
        return fElement == null || o.fElement == null
            ? fElement == o.fElement
            : fElement.equals(o.fElement);
    }

    public XmlElement getElement() {
        return fElement;
    }

    @Override
    public int hashCode() {
        return fElement != null ? fElement.hashCode() : 0;
    }

    @Override
    public String toString() {
        return fElement != null ? fElement.toString() : null;
    }

}