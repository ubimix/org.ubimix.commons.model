package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

public class Burner {

    public static class Postprocessing {

    }

    /**
     * @author kotelnikov
     */
    public static class TagStat {

        private int fBlockElements;

        private int fInlineElements;

        private int fLineBreakElements;

        private int fOtherXmlNodes;

        private int fSpaceBlocks;

        private int fTextBlocks;

        private int fXmlElements;

        public void clear() {
            fBlockElements = 0;
            fInlineElements = 0;
            fLineBreakElements = 0;
            fOtherXmlNodes = 0;
            fSpaceBlocks = 0;
            fTextBlocks = 0;
            fXmlElements = 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Burner.TagStat)) {
                return false;
            }
            Burner.TagStat o = (Burner.TagStat) obj;
            int[] array1 = toArray();
            int[] array2 = o.toArray();
            for (int i = 0; i < array1.length; i++) {
                if (array1[i] != array2[i]) {
                    return false;
                }
            }
            return true;
        }

        public int getAllElements() {
            return fBlockElements + fInlineElements + fXmlElements;
        }

        public int getAllInlineElements() {
            return fInlineElements + fTextBlocks + fSpaceBlocks;
        }

        public int getAllMeaningfulNodes() {
            return getAllElements() + fOtherXmlNodes + fTextBlocks;
        }

        public int getAllTextBlocks() {
            return fTextBlocks + fSpaceBlocks;
        }

        protected int getBlockElements() {
            return fBlockElements;
        }

        protected int getInlineElements() {
            return fInlineElements;
        }

        protected int getLineBreakElements() {
            return fLineBreakElements;
        }

        protected int getOtherXmlNodes() {
            return fOtherXmlNodes;
        }

        protected int getSpaceBlocks() {
            return fSpaceBlocks;
        }

        protected int getTextBlocks() {
            return fTextBlocks;
        }

        protected int getXmlElements() {
            return fXmlElements;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            int[] array = toArray();
            for (int value : array) {
                hashCode = 31 * hashCode + value;
            }
            return hashCode;
        }

        public void incBlockElements() {
            fBlockElements++;
        }

        public void incInlineElements() {
            fInlineElements++;
        }

        public void incLineBreakElements() {
            fLineBreakElements++;
        }

        public void incOtherXmlNodes() {
            fOtherXmlNodes++;
        }

        public void incSpaceBlocks() {
            fSpaceBlocks++;
        }

        public void incTextBlocks() {
            fTextBlocks++;
        }

        public void incXmlElements() {
            fXmlElements++;
        }

        private int[] toArray() {
            int[] array = {
                fBlockElements,
                fInlineElements,
                fLineBreakElements,
                fOtherXmlNodes,
                fSpaceBlocks,
                fTextBlocks,
                fXmlElements };
            return array;
        }

        @Override
        public String toString() {
            return "{\n"
                + "  \"blockElements\":"
                + fBlockElements
                + ",\n"
                + "  \"inlineElements\":"
                + fInlineElements
                + ",\n"
                + "  \"lineBreakElements\":"
                + fLineBreakElements
                + ",\n"
                + "  \"otherXmlNodes\":"
                + fOtherXmlNodes
                + ",\n"
                + "  \"spaceBlocks\":"
                + fSpaceBlocks
                + ",\n"
                + "  \"textBlocks\":"
                + fTextBlocks
                + ",\n"
                + "  \"xmlElements\":"
                + fXmlElements
                + "\n"
                + "}";
        }

    }

    private static boolean isEmpty(String str) {
        return "".equals(str);
    }

    private static String reduceText(String txtStr, boolean keepSpaces) {
        if (keepSpaces) {
            return txtStr;
        }
        return txtStr.replaceAll("\\s+", " ");
    }

    private static String reduceText(StringBuilder buf, boolean keepSpaces) {
        String result = buf.toString();
        result = reduceText(result, keepSpaces);
        buf.delete(0, buf.length());
        return result;
    }

    protected boolean allowedEmptyNode(XmlElement node) {
        String name = node.getName();
        return !(HtmlTagDictionary.DIV.equals(name)
            || HtmlTagDictionary.SPAN.equals(name)
            || HtmlTagDictionary.P.equals(name)
            || HtmlTagDictionary.OL.equals(name)
            || HtmlTagDictionary.UL.equals(name)
            || HtmlTagDictionary.LI.equals(name)
            || HtmlTagDictionary.DL.equals(name)
            || HtmlTagDictionary.DD.equals(name) || HtmlTagDictionary.DT
                .equals(name));
    }

    protected void appendToNodeList(
        List<XmlNode> newList,
        XmlNode node,
        boolean keepSpaces,
        StringBuilder buf) {
        if (node instanceof XmlText) {
            String str = ((XmlText) node).getContent();
            buf.append(str);
        } else {
            String str = reduceText(buf, keepSpaces);
            if (!isEmpty(str)) {
                XmlText text = new XmlText(str);
                newList.add(text);
            }
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                if (canReplace(e)) {
                    List<XmlNode> subnodes = e.getChildren();
                    for (XmlNode subnode : subnodes) {
                        appendToNodeList(newList, subnode, keepSpaces, buf);
                    }
                } else {
                    newList.add(e);
                }
            } else {
                newList.add(node);
            }
        }
    }

    public boolean burn(XmlNode node, boolean keepNodeIntact) {
        boolean result = false;
        if (node instanceof XmlElement) {
            XmlElement element = (XmlElement) node;
            Burner.TagStat stat = burnChildNodes(element);

            String tagName = element.getName();
            if (stat.getBlockElements() > 0 && stat.getAllInlineElements() > 0) {
                stat = wrapInlineElementsInParagraphs(element);
            }
            if (!keepNodeIntact) {
                removeUnusedAttributes(element);
                if (stat.getAllMeaningfulNodes() == 0
                    && node instanceof XmlElement
                    && !allowedEmptyNode((XmlElement) node)) {
                    result = true;
                } else if (stat.getBlockElements() > 0
                    && !HtmlTagDictionary.isBlockContainerElement(tagName)) {
                    element.setName(HtmlTagDictionary.DIV);
                } else if (stat.getAllInlineElements() > 0
                    && stat.getBlockElements() == 0) {
                    if (HtmlTagDictionary.DIV.equals(tagName)) {
                        // FIXME:
                        element.setName(HtmlTagDictionary.P);
                    }
                }
            }
        }
        return result;
    }

    protected Burner.TagStat burnChildNodes(XmlElement element) {
        List<XmlNode> list = element.getChildren();
        element.removeChildren();
        boolean keepSpaces = false;
        StringBuilder buf = new StringBuilder();
        List<XmlNode> newList = new ArrayList<XmlNode>();
        for (XmlNode child : list) {
            if (!burn(child, false)) {
                appendToNodeList(newList, child, keepSpaces, buf);
            }
        }
        String str = reduceText(buf, keepSpaces);
        if (!isEmpty(str)) {
            XmlText text = new XmlText(str);
            newList.add(text);
        }

        Burner.TagStat stat = new TagStat();
        for (int i = 0; i < newList.size(); i++) {
            XmlNode n = newList.get(i);
            int brCounter = 0;
            while (i + brCounter < newList.size()
                && isLineBreak(newList.get(i + brCounter))) {
                brCounter++;
            }
            if (brCounter > 1) {
                XmlElement el = (XmlElement) n;
                el.setName(HtmlTagDictionary.P);
                i += brCounter - 1;
                for (int j = 1; j < brCounter; j++) {
                    newList.remove(i--);
                }
            }
            element.addChild(n);
            updateStat(n, stat);
        }
        return stat;
    }

    /**
     * @param e
     * @return <code>true</code> if the specified node could be removed from the
     *         tree
     */
    private boolean canReplace(XmlElement e) {
        String name = e.getName();
        return (HtmlTagDictionary.DIV.equals(name) || HtmlTagDictionary.SPAN
            .equals(name));
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

    private boolean isLineBreak(XmlNode node) {
        if (!(node instanceof XmlElement)) {
            return false;
        }
        XmlElement e = (XmlElement) node;
        return HtmlTagDictionary.isLineBreak(e.getName());
    }

    private boolean isTextflowNode(XmlNode node) {
        boolean result = false;
        if (node instanceof XmlText) {
            result = true;
        } else if (node instanceof XmlElement) {
            XmlElement e = (XmlElement) node;
            String name = e.getName();
            if (!HtmlTagDictionary.isHtmlElement(name)) {
                // No HTML name means that it is a simple XML node.
                // A simple XML node can appear between in-line elements.
                result = true;
            } else {
                result = HtmlTagDictionary.isInlineElement(name);
            }
        }
        return result;
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

    private void updateStat(XmlNode node, Burner.TagStat stat) {
        if (node instanceof XmlText) {
            updateTextStat((XmlText) node, stat);
        } else if (node instanceof XmlElement) {
            updateXmlStat((XmlElement) node, stat);
        }
    }

    protected void updateTextStat(String str, Burner.TagStat stat) {
        str = str.trim();
        if (isEmpty(str)) {
            stat.incSpaceBlocks();
        } else {
            stat.incTextBlocks();
        }
    }

    private void updateTextStat(XmlText text, Burner.TagStat stat) {
        String str = text.getContent();
        str = reduceText(str, false);
        updateTextStat(str, stat);
    }

    private void updateXmlStat(XmlElement e, Burner.TagStat stat) {
        String name = e.getName();
        if (HtmlTagDictionary.isBlockElement(name)) {
            stat.incBlockElements();
        } else if (HtmlTagDictionary.isInlineElement(name)) {
            if (HtmlTagDictionary.isLineBreak(name)) {
                stat.incLineBreakElements();
            } else {
                stat.incInlineElements();
            }
        } else {
            stat.incXmlElements();
        }
    }

    private Burner.TagStat wrapInlineElementsInParagraphs(XmlElement e) {
        List<XmlNode> children = e.getChildren();
        e.removeChildren();
        Burner.TagStat stat = new TagStat();
        Burner.TagStat paragraphStat = new TagStat();
        int brCounter = 0;
        XmlElement paragraph = null;
        for (XmlNode node : children) {
            boolean inline = isTextflowNode(node);
            if (node instanceof XmlElement) {
                XmlElement eNode = (XmlElement) node;
                if (HtmlTagDictionary.isLineBreak(eNode.getName())) {
                    brCounter++;
                    continue;
                }
            }
            if (brCounter > 0) {
                if (brCounter == 0) {

                }
            }
            if (inline) {
                if (paragraph == null) {
                    paragraph = new XmlElement(HtmlTagDictionary.P);
                    paragraphStat.clear();
                }
                updateStat(node, paragraphStat);
                paragraph.addChild(node);
            } else {
                if (paragraph != null) {
                    if (paragraphStat.getAllMeaningfulNodes() > 0) {
                        updateStat(paragraph, stat);
                        e.addChild(paragraph);
                    }
                    paragraph = null;
                }
                e.addChild(node);
            }
        }
        if (paragraph != null) {
            if (paragraphStat.getAllMeaningfulNodes() > 0) {
                updateStat(paragraph, stat);
                e.addChild(paragraph);
            }
            paragraph = null;
        }
        return stat;
    }
}