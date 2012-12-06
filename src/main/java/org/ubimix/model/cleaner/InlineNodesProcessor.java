package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;

/**
 * This node processor splits a sequence of inline tags separated by line breaks
 * to multiple paragraphs and wraps all inline nodes in paragraphs.
 * 
 * @author kotelnikov
 */
public class InlineNodesProcessor extends AbstractTagProcessor {

    private List<IXmlNode> fInlineNodes = new ArrayList<IXmlNode>();

    private List<IXmlNode> fLineBreaks = new ArrayList<IXmlNode>();

    private List<IXmlNode> fResult = new ArrayList<IXmlNode>();

    private List<IXmlNode> fSpaces = new ArrayList<IXmlNode>();

    private boolean fTransformBrToParagraph;

    public InlineNodesProcessor(boolean transformBrToParagraphs) {
        fTransformBrToParagraph = transformBrToParagraphs;
    }

    private void clear() {
        fInlineNodes.clear();
        fLineBreaks.clear();
        fResult.clear();
        fSpaces.clear();
    }

    private void flushLineBreaks() {
        int number = fLineBreaks.size();
        if (number > 0) {
            if (number > 1) {
                wrapInlineNodesInParagraph();
            } else if (hasNodesBefore()) {
                if (fTransformBrToParagraph) {
                    wrapInlineNodesInParagraph();
                } else {
                    fInlineNodes.add(fLineBreaks.get(0));
                }
            }
            fLineBreaks.clear();
        }
    }

    private void flushSpaces() {
        if (!fSpaces.isEmpty()) {
            if (fLineBreaks.isEmpty() && hasNodesBefore()) {
                fInlineNodes.addAll(fSpaces);
            }
            fSpaces.clear();
        }
    }

    @Override
    public List<IXmlNode> handle(IXmlElement element, boolean keepSpaces1) {
        try {
            List<IXmlNode> nodes = element.getChildren();
            int len = nodes.size();
            for (int i = 0; i < len; i++) {
                IXmlNode node = nodes.get(i);
                if (node instanceof IXmlText) {
                    IXmlText text = (IXmlText) node;
                    String str = reduceText(text.getContent(), keepSpaces1);
                    if (str.length() == 0) {
                        // Skip empty text nodes
                    } else if (" ".equals(str)) {
                        // Add spaces only if there is no already empty spaces
                        // and
                        // no line breaks just before this space.
                        if (fSpaces.isEmpty() && fLineBreaks.isEmpty()) {
                            fSpaces.add(text);
                        }
                    } else {
                        flushSpaces();
                        flushLineBreaks();
                        fInlineNodes.add(text);
                    }
                } else if (node instanceof IXmlElement) {
                    IXmlElement e = (IXmlElement) node;
                    String name = getHtmlName(e);
                    if (HtmlTagDictionary.isLineBreak(name)) {
                        fLineBreaks.add(e);
                    } else {
                        if (HtmlTagDictionary.isInlineElement(name)) {
                            flushSpaces();
                            flushLineBreaks();
                            fInlineNodes.add(node);
                        } else {
                            wrapInlineNodesInParagraph();
                            fResult.add(e);
                        }
                    }
                }
            }
            if (fResult.isEmpty()) {
                if (fInlineNodes.isEmpty()) {
                    fInlineNodes.addAll(fSpaces);
                }
                fResult.addAll(fInlineNodes);
            } else {
                wrapInlineNodesInParagraph();
            }
            element.setChildren(fResult);
            return Arrays.<IXmlNode> asList(element);
        } finally {
            clear();
        }
    }

    /**
     * Returns <code>true</code> if there is already an inline or a block node.
     * 
     * @return
     */
    private boolean hasNodesBefore() {
        return !fInlineNodes.isEmpty(); // || !fResult.isEmpty();
    }

    private void wrapInlineNodesInParagraph() {
        if (fInlineNodes.size() > 0) {
            IXmlFactory factory = fInlineNodes.get(0).getFactory();
            IXmlElement paragraph = factory.newElement(HtmlTagDictionary.P);
            fResult.add(paragraph);
            paragraph.addChildren(fInlineNodes);
            fInlineNodes.clear();
        }
    }

}