package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;
import org.ubimix.model.xml.XmlWrapper;

/**
 * @author kotelnikov
 */
public class HtmlArticle extends XmlWrapper {

    public static final IValueFactory<HtmlArticle> FACTORY = new IValueFactory<HtmlArticle>() {
        @Override
        public HtmlArticle newValue(Object object) {
            XmlElement e = null;
            if (object instanceof XmlElement) {
                e = (XmlElement) object;
            }
            return e != null ? new HtmlArticle(e) : null;
        }
    };

    public static List<String> getImageUrls(XmlElement element) {
        return getLinks(element, "img", "src");
    }

    public static List<String> getLinks(
        XmlElement element,
        String tagName,
        final String attr) {
        List<String> references = element.getAllChildrenByName(
            tagName,
            new IValueFactory<String>() {
                @Override
                public String newValue(Object object) {
                    XmlElement e = (XmlElement) object;
                    String value = e.getAttribute(attr);
                    if (value == null) {
                        return null;
                    }
                    String href = e.getAttribute(attr);
                    return href;
                }
            });
        return references;
    }

    public static List<String> getReferences(XmlElement element) {
        return getLinks(element, "a", "href");
    }

    public HtmlArticle(XmlElement e) {
        super(e);
        // XmlElement hgroup = new XmlElement(HtmlTagDictionary.HGROUP);
        // addChild(hgroup);
        // XmlElement h1 = new XmlElement(HtmlTagDictionary.H1);
        // hgroup.addChild(h1);
        // addChild(new XmlElement(HtmlTagDictionary.SECTION));
        // addChild(new XmlElement(HtmlTagDictionary.FOOTER));
    }

    public HtmlArticle(XmlFactory factory) {
        this(factory.newElement(HtmlTagDictionary.ARTICLE));
    }

    public HtmlArticle addArticle() {
        HtmlArticle article = new HtmlArticle(getFactory());
        addArticle(article);
        return article;
    }

    public void addArticle(HtmlArticle article) {
        fElement.addChild(article.fElement);
    }

    public List<HtmlArticle> getArticles() {
        List<HtmlArticle> result = fElement.getChildrenByName(
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
        return getImageUrls(fElement);
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

    protected <T extends XmlElement> T getOrCreate(
        XmlElement e,
        String childName,
        IValueFactory<T> factory) {
        T result = e.getChildByName(childName, factory);
        if (result == null) {
            XmlFactory xmlFactory = e.getFactory();
            XmlElement child = xmlFactory.newElement(childName);
            e.addChild(child);
            result = factory.newValue(child);
        }
        return result;
    }

    protected XmlElement getOrCreate(XmlWrapper e, String childName) {
        return fElement.getOrCreateElement(childName, XmlElement.FACTORY);
    }

    public ModelObject getProperties() {
        XmlElement hgroup = getOrCreate(this, HtmlTagDictionary.HGROUP);
        XmlElement e = hgroup.select("properties");
        return e != null ? ModelObject.FACTORY.newValue(e) : new ModelObject();
    }

    public List<String> getReferences() {
        return getReferences(fElement);
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
        return XmlElement.getOrCreateElement(hgroup, HtmlTagDictionary.H1);
    }

    public void setContent(XmlElement contentXml) {
        XmlElement section = getSection();
        section.removeChildren();
        section.addChild(contentXml);
    }

    public void setTitle(String title) {
        XmlElement e = getTitleElement();
        XmlFactory factory = getFactory();
        XmlText child = factory.newText(title);
        e.setChildren(child);
    }

}