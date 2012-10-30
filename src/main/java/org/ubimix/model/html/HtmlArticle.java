package org.ubimix.model.html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.selector.PathProcessor;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class HtmlArticle extends XmlElement {

    public static final IValueFactory<HtmlArticle> FACTORY = new AbstractXmlElementFactory<HtmlArticle>() {
        @Override
        protected HtmlArticle newXmlElement(
            XmlElement parent,
            Map<Object, Object> map) {
            return new HtmlArticle(parent, map);
        }
    };

    private static Map<String, PathProcessor> fPathProcessorCache = new HashMap<String, PathProcessor>();

    public HtmlArticle() {
        super(HtmlTagDictionary.ARTICLE);
        XmlElement hgroup = new XmlElement(HtmlTagDictionary.HGROUP);
        addChild(hgroup);
        XmlElement h1 = new XmlElement(HtmlTagDictionary.H1);
        hgroup.addChild(h1);
        addChild(new XmlElement(HtmlTagDictionary.SECTION));
        addChild(new XmlElement(HtmlTagDictionary.FOOTER));
    }

    public HtmlArticle(IHasValueMap object) {
        super(object);
    }

    public HtmlArticle(XmlElement parent) {
        this();
        setParent(parent);
    }

    public HtmlArticle(XmlElement parent, Map<Object, Object> map) {
        super(parent, map);
    }

    public HtmlArticle addArticle() {
        HtmlArticle article = new HtmlArticle(this);
        addArticle(article);
        return article;
    }

    public void addArticle(HtmlArticle article) {
        this.addChild(article);
    }

    public List<HtmlArticle> getArticles() {
        List<HtmlArticle> result = getChildrenByName(
            HtmlTagDictionary.ARTICLE,
            HtmlArticle.FACTORY);
        result.remove(this);
        return result;
    }

    public List<XmlNode> getContent() {
        return getSection().getChildren();
    }

    public String getContentAsHtml() {
        return getSerializedContent(true);
    }

    public String getContentAsText() {
        return getSerializedContent(false);
    }

    protected XmlElement getOrCreate(XmlElement e, String childName) {
        return getOrCreate(e, childName, XmlElement.FACTORY);
    }

    protected <T extends XmlElement> T getOrCreate(
        XmlElement e,
        String childName,
        IValueFactory<T> factory) {
        T result = e.getChildByName(childName, factory);
        if (result == null) {
            XmlElement child = new XmlElement(childName);
            e.addChild(child);
            result = factory.newValue(child);
        }
        return result;
    }

    @Override
    protected PathProcessor getPathProcessor(String cssSelector) {
        PathProcessor processor = fPathProcessorCache.get(cssSelector);
        if (processor == null) {
            processor = super.getPathProcessor(cssSelector);
            fPathProcessorCache.put(cssSelector, processor);
        }
        return processor;
    }

    public XmlElement getSection() {
        return getOrCreate(this, HtmlTagDictionary.SECTION);
    }

    private String getSerializedContent(boolean html) {
        StringBuilder buf = new StringBuilder();
        for (XmlNode node : getSection()) {
            String str;
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                if (html) {
                    str = e.toString();
                } else {
                    str = e.toText();
                }
            } else {
                str = node.toString();
            }
            buf.append(str);
        }
        return buf.toString();
    }

    public String getTitle() {
        XmlElement title = getTitleElement();
        return title.toText();
    }

    public XmlElement getTitleElement() {
        XmlElement hgroup = getOrCreate(this, HtmlTagDictionary.HGROUP);
        return getOrCreate(hgroup, HtmlTagDictionary.H1);
    }

    public void setTitle(String title) {
        XmlElement e = getTitleElement();
        e.setChildren(new XmlText(title));
    }

}