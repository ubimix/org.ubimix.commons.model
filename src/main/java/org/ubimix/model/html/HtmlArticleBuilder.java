package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ubimix.commons.graph.TreeBuilder;
import org.ubimix.commons.graph.WalkerListener;
import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.cleaner.TagBurner;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.XmlUtils;

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

        private IXmlElement fNode;

        private NodeContext fParent;

        public NodeContext(
            NodeContext parent,
            HtmlArticle article,
            IXmlElement node) {
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

        public IXmlElement getXmlElement() {
            return fNode;
        }

    }

    private final static Logger log = Logger.getLogger(HtmlArticleBuilder.class
        .getName());

    public static void burnContent(TagBurner burner, HtmlArticle article) {
        IXmlElement section = article.getSection();
        List<IXmlNode> newContent = new ArrayList<IXmlNode>();
        for (IXmlNode node : section) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                List<IXmlNode> list = burner.handle(e, false);
                newContent.addAll(list);
            } else {
                newContent.add(node);
            }
        }
        section.setChildren(newContent);

        for (HtmlArticle a : article.getArticles()) {
            burnContent(burner, a);
        }
    }

    private static String getHTMLName(IXmlElement e) {
        return e.getName();
    }

    private Comparator<IXmlElement> fComparator = new Comparator<IXmlElement>() {
        @Override
        public int compare(IXmlElement o1, IXmlElement o2) {
            String name1 = getHTMLName(o1);
            String name2 = getHTMLName(o2);
            return -name1.compareTo(name2);
        }
    };

    private NodeContext fCurrentContext;

    private WalkerListener<IXmlElement> fListener = new WalkerListener<IXmlElement>() {

        @Override
        public void onBegin(IXmlElement parent, IXmlElement node) {
            try {
                HtmlArticle parentArticle = fSectionStack.peek();
                HtmlArticle article = parentArticle.addArticle();
                IXmlElement titleElement = article.getTitleElement();
                titleElement.addChildren(node);
                String id = node.getAttribute("id");
                if (id != null) {
                    titleElement.setAttribute("id", id);
                }
                fSectionStack.push(article);
            } catch (Throwable t) {
                throw handleError("Can not create a new XML node", t);
            }
        }

        @Override
        public void onEnd(IXmlElement parent, IXmlElement node) {
            fSectionStack.pop();
        }
    };

    private Stack<HtmlArticle> fSectionStack = new Stack<HtmlArticle>();

    private TreeBuilder<IXmlElement> fTreeBuilder = new TreeBuilder<IXmlElement>(
        fListener);

    protected <T extends IXmlNode> T append(IXmlElement element, T child) {
        T clone = XmlUtils.newCopy(child, false);
        element.addChild(clone);
        return clone;
    }

    public HtmlArticle buildArticle(IXmlElement e) {
        IXmlFactory factory = e.getFactory();
        HtmlArticle result = new HtmlArticle(factory);
        buildArticle(e, result);
        return result;
    }

    public HtmlArticle buildArticle(IXmlElement element, HtmlArticle article) {
        fSectionStack.push(article);
        doVisit(element);
        fTreeBuilder.close();
        return article;
    }

    public HtmlArticle buildArticle(
        IXmlElement element,
        HtmlArticle article,
        TagBurner burner) {
        buildArticle(element, article);
        if (burner != null) {
            burnContent(burner, article);
        }
        return article;
    }

    private void doVisit(IXmlElement e) {
        if ((fCurrentContext == null || fCurrentContext.acceptTitles())
            && isTitle(e)) {
            fTreeBuilder.align(e, fComparator);
        } else {
            HtmlArticle currentArticle = fSectionStack.peek();
            IXmlElement parent = null;
            if (fCurrentContext == null
                || !currentArticle.equals(fCurrentContext.getCurrentArticle())) {
                parent = currentArticle.getSection();
            } else {
                parent = fCurrentContext.getXmlElement();
            }
            IXmlElement clone = append(parent, e);
            fCurrentContext = new NodeContext(
                fCurrentContext,
                currentArticle,
                clone);
            try {
                for (IXmlNode child : e) {
                    if (child instanceof IXmlElement) {
                        doVisit((IXmlElement) child);
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

    protected boolean isTitle(IXmlElement e) {
        String name = getHTMLName(e);
        boolean isTitle = "h1".equals(name)
            || "h2".equals(name)
            || "h3".equals(name)
            || "h4".equals(name)
            || "h5".equals(name)
            || "h6".equals(name);
        return isTitle;
    }
}