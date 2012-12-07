package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.XmlUtils;
import org.ubimix.model.xml.XmlWrapper;

/**
 * @author kotelnikov
 */
public class HtmlArticle extends XmlWrapper {

    public static final IValueFactory<HtmlArticle> FACTORY = new IValueFactory<HtmlArticle>() {
        @Override
        public HtmlArticle newValue(Object object) {
            IXmlElement e = null;
            if (object instanceof IXmlElement) {
                e = (IXmlElement) object;
            }
            return e != null ? new HtmlArticle(e) : null;
        }
    };

    public static List<String> getImageUrls(IXmlElement element) {
        return getLinks(element, "img", "src");
    }

    public static List<String> getLinks(
        IXmlElement element,
        String tagName,
        final String attr) {
        List<String> references = XmlUtils.getAllChildrenByName(
            element,
            tagName,
            new IValueFactory<String>() {
                @Override
                public String newValue(Object object) {
                    IXmlElement e = (IXmlElement) object;
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

    public static List<String> getReferences(IXmlElement element) {
        return getLinks(element, "a", "href");
    }

    public HtmlArticle(IXmlElement e) {
        super(e);
        // XmlElement hgroup = new XmlElement(HtmlTagDictionary.HGROUP);
        // addChild(hgroup);
        // XmlElement h1 = new XmlElement(HtmlTagDictionary.H1);
        // hgroup.addChild(h1);
        // addChild(new XmlElement(HtmlTagDictionary.SECTION));
        // addChild(new XmlElement(HtmlTagDictionary.FOOTER));
    }

    public HtmlArticle(IXmlFactory factory) {
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
        List<HtmlArticle> result = XmlUtils.getChildrenByName(
            fElement,
            HtmlTagDictionary.ARTICLE,
            HtmlArticle.FACTORY);
        result.remove(this);
        return result;
    }

    public List<IXmlNode> getContent() {
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

    public ModelObject getProperties() {
        IXmlElement hgroup = getOrCreateElement(HtmlTagDictionary.HGROUP);
        IXmlElement e = select(hgroup, "properties");
        return e != null ? ModelObject.FACTORY.newValue(e) : new ModelObject();
    }

    public List<String> getReferences() {
        return getReferences(fElement);
    }

    public <T> List<T> getReferencesAs(IValueFactory<T> factory) {
        List<String> refs = getReferences();
        return getLinksAs(refs, factory);
    }

    public IXmlElement getSection() {
        return getOrCreateElement(HtmlTagDictionary.SECTION);
    }

    private String getSerializedContent(boolean html) {
        StringBuilder buf = new StringBuilder();
        for (IXmlNode node : getSection()) {
            String str;
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                if (html) {
                    str = e.toString();
                } else {
                    str = XmlUtils.toText(e);
                }
            } else {
                str = node.toString();
            }
            buf.append(str);
        }
        return buf.toString();
    }

    public String getTitle() {
        IXmlElement title = getTitleElement();
        return XmlUtils.toText(title);
    }

    public IXmlElement getTitleElement() {
        IXmlElement hgroup = getOrCreateElement(HtmlTagDictionary.HGROUP);
        return getOrCreateElement(hgroup, HtmlTagDictionary.H1);
    }

    public void setContent(IXmlElement contentXml) {
        IXmlElement section = getSection();
        section.removeChildren();
        section.addChild(contentXml);
    }

    public void setTitle(String title) {
        IXmlElement e = getTitleElement();
        e.setText(title);
    }

}