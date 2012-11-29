package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.ModelObject;
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

    public static List<String> getImageUrls(XmlElement element) {
        return getLinks(element, "img", "src");
    }

    public static List<String> getLinks(
        XmlElement element,
        String tagName,
        final String attr) {
        List<String> result = new ArrayList<String>();
        List<XmlElement> references = element.getAllChildrenByName(
            tagName,
            new IValueFactory<XmlElement>() {
                @Override
                public XmlElement newValue(Object object) {
                    XmlElement e = (XmlElement) object;
                    String value = e.getAttribute(attr);
                    if (value == null) {
                        return null;
                    }
                    return e;
                }
            });
        for (XmlElement reference : references) {
            String href = reference.getAttribute(attr);
            if (href != null) {
                result.add(href);
            }
        }
        return result;
    }

    public static List<String> getReferences(XmlElement element) {
        return getLinks(element, "a", "href");
    }

    public HtmlArticle() {
        super(HtmlTagDictionary.ARTICLE);
        // XmlElement hgroup = new XmlElement(HtmlTagDictionary.HGROUP);
        // addChild(hgroup);
        // XmlElement h1 = new XmlElement(HtmlTagDictionary.H1);
        // hgroup.addChild(h1);
        // addChild(new XmlElement(HtmlTagDictionary.SECTION));
        // addChild(new XmlElement(HtmlTagDictionary.FOOTER));
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

    public List<String> getImageUrls() {
        return getImageUrls(this);
    }

    public <T> List<T> getImageUrlsAs(IValueFactory<T> factory) {
        List<String> refs = getImageUrls();
        return getLinksAs(refs, factory);
    }

    private <T> List<T> getLinksAs(List<String> refs, IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        if (refs != null && !refs.isEmpty()) {
            for (String ref : refs) {
                T value = factory.newValue(ref);
                if (value != null) {
                    result.add(value);
                }
            }
        }
        return result;
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

    public ModelObject getProperties() {
        XmlElement hgroup = getOrCreate(this, HtmlTagDictionary.HGROUP);
        XmlElement e = hgroup.select("properties");
        return e != null ? ModelObject.FACTORY.newValue(e) : new ModelObject();
    }

    public List<String> getReferences() {
        return getReferences(this);
    }

    public <T> List<T> getReferencesAs(IValueFactory<T> factory) {
        List<String> refs = getReferences();
        return getLinksAs(refs, factory);
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

    public void setContent(XmlElement contentXml) {
        XmlElement section = getSection();
        section.removeChildren();
        section.addChild(contentXml);
    }

    public void setProperties(ModelObject properties) {
        XmlElement hgroup = getOrCreate(this, HtmlTagDictionary.HGROUP);
        XmlElement e = new XmlElement(properties);
        e.setName("properties");
        hgroup.addChild(e);
    }

    public void setTitle(String title) {
        XmlElement e = getTitleElement();
        e.setChildren(new XmlText(title));
    }

}