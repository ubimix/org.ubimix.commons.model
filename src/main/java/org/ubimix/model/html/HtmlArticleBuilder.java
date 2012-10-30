package org.ubimix.model.html;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ubimix.commons.graph.TreeBuilder;
import org.ubimix.commons.graph.WalkerListener;
import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class HtmlArticleBuilder {

    private static class NodeContext {

        private static Set<String> fExcludedTags = new HashSet<String>();

        static {
            fExcludedTags.add(HtmlTagDictionary.UL);
            fExcludedTags.add(HtmlTagDictionary.OL);
            fExcludedTags.add(HtmlTagDictionary.DL);
            fExcludedTags.add(HtmlTagDictionary.LI);
            fExcludedTags.add(HtmlTagDictionary.DD);
            fExcludedTags.add(HtmlTagDictionary.DT);
            fExcludedTags.add(HtmlTagDictionary.TABLE);
            fExcludedTags.add(HtmlTagDictionary.TBODY);
            fExcludedTags.add(HtmlTagDictionary.THEAD);
            fExcludedTags.add(HtmlTagDictionary.COLGROUP);
            // fExcludedTags.add(HtmlTagDictionary.TROW);
            fExcludedTags.add(HtmlTagDictionary.COL);
            fExcludedTags.add(HtmlTagDictionary.TR);
            fExcludedTags.add(HtmlTagDictionary.TD);
            fExcludedTags.add(HtmlTagDictionary.TH);
        }

        private HtmlArticle fArticle;

        private XmlElement fNode;

        private NodeContext fParent;

        public NodeContext(
            NodeContext parent,
            HtmlArticle article,
            XmlElement node) {
            fNode = node;
            fArticle = article;
            fParent = parent;
        }

        public boolean acceptTitles() {
            if (fParent != null && !fParent.acceptTitles()) {
                return false;
            }
            String name = getHTMLName(fNode);
            return !fExcludedTags.contains(name);
        }

        public HtmlArticle getCurrentArticle() {
            return fArticle;
        }

        public NodeContext getParent() {
            return fParent;
        }

        public XmlElement getXmlElement() {
            return fNode;
        }

    }

    private final static Logger log = Logger.getLogger(HtmlArticleBuilder.class
        .getName());

    private static String getHTMLName(XmlElement e) {
        return e.getName();
    }

    private Comparator<XmlElement> fComparator = new Comparator<XmlElement>() {
        @Override
        public int compare(XmlElement o1, XmlElement o2) {
            String name1 = getHTMLName(o1);
            String name2 = getHTMLName(o2);
            return -name1.compareTo(name2);
        }
    };

    private NodeContext fCurrentContext;

    private WalkerListener<XmlElement> fListener = new WalkerListener<XmlElement>() {

        @Override
        public void onBegin(XmlElement parent, XmlElement node) {
            try {
                HtmlArticle parentArticle = fSectionStack.peek();
                HtmlArticle article = parentArticle.addArticle();
                article.getTitleElement().addChildren(node);
                fSectionStack.push(article);
            } catch (Throwable t) {
                throw handleError("Can not create a new XML node", t);
            }
        }

        @Override
        public void onEnd(XmlElement parent, XmlElement node) {
            fSectionStack.pop();
        }
    };

    private Stack<HtmlArticle> fSectionStack = new Stack<HtmlArticle>();

    private TreeBuilder<XmlElement> fTreeBuilder = new TreeBuilder<XmlElement>(
        fListener);

    protected <T extends XmlNode> T append(XmlElement element, T child) {
        @SuppressWarnings("unchecked")
        T clone = (T) child.newCopy(false);
        element.addChild(clone);
        return clone;
    }

    private void doVisit(XmlElement e) {
        if ((fCurrentContext == null || fCurrentContext.acceptTitles())
            && isTitle(e)) {
            fTreeBuilder.align(e, fComparator);
        } else {
            HtmlArticle currentArticle = fSectionStack.peek();
            XmlElement parent = null;
            if (fCurrentContext == null
                || !currentArticle.equals(fCurrentContext.getCurrentArticle())) {
                parent = currentArticle.getSection();
            } else {
                parent = fCurrentContext.getXmlElement();
            }
            XmlElement clone = append(parent, e);
            fCurrentContext = new NodeContext(
                fCurrentContext,
                currentArticle,
                clone);
            try {
                for (XmlNode child : e) {
                    if (child instanceof XmlElement) {
                        doVisit((XmlElement) child);
                    } else {
                        append(clone, child);
                    }
                }
            } finally {
                fCurrentContext = fCurrentContext.getParent();
            }
        }
    }

    private RuntimeException handleError(String msg, Throwable t) {
        if (t instanceof RuntimeException) {
            return (RuntimeException) t;
        }
        log.log(Level.FINE, msg, t);
        return new RuntimeException(msg, t);
    }

    protected boolean isTitle(XmlElement e) {
        String name = getHTMLName(e);
        boolean isTitle = "h1".equals(name)
            || "h2".equals(name)
            || "h3".equals(name)
            || "h4".equals(name)
            || "h5".equals(name)
            || "h6".equals(name);
        return isTitle;
    }

    public HtmlArticle visit(XmlElement e) {
        HtmlArticle result = new HtmlArticle();
        visit(e, result);
        return result;
    }

    public void visit(XmlElement element, HtmlArticle article) {
        fSectionStack.push(article);
        doVisit(element);
        fTreeBuilder.close();
    }
}