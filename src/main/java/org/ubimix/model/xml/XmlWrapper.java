package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ubimix.model.IValueFactory;

/**
 * @author kotelnikov
 */
public class XmlWrapper extends XmlUtils {

    public static final IValueFactory<XmlWrapper> FACTORY = new IValueFactory<XmlWrapper>() {
        public XmlWrapper newValue(Object object) {
            if (!(object instanceof IXmlElement)) {
                return null;
            }
            return new XmlWrapper((IXmlElement) object);
        }
    };

    protected IXmlElement fElement;

    public XmlWrapper(IXmlElement e) {
        fElement = e;
    }

    public XmlWrapper(String name) {
        this(XmlFactory.getInstance().newElement(name));
    }

    @SuppressWarnings("unchecked")
    protected <T extends XmlWrapper> T cast() {
        return (T) this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XmlWrapper)) {
            return false;
        }
        XmlWrapper o = (XmlWrapper) obj;
        return fElement.equals(o.fElement);
    }

    public String getAsText() {
        return getSerializedContent(false, getElement());
    }

    public String getAsXml() {
        return getSerializedContent(true, getElement());
    }

    public List<XmlWrapper> getByName(String tagName) {
        return getChildrenByName(fElement, tagName, XmlWrapper.FACTORY);
    }

    public List<XmlWrapper> getByPath(String... path) {
        return getChildrenByPath(XmlWrapper.FACTORY, path);

    }

    public IXmlElement getChildByName(String name) {
        return getChildByName(fElement, name);
    }

    public IXmlElement getChildByPath(String... path) {
        return getChildByPath(fElement, path);
    }

    public List<IXmlElement> getChildrenByName(String tagName) {
        return getChildrenByName(fElement, tagName, IXmlElement.FACTORY);
    }

    public <T> List<T> getChildrenByName(
        String tagName,
        IValueFactory<T> factory) {
        return getChildrenByName(fElement, tagName, factory);
    }

    public List<IXmlElement> getChildrenByNames(Collection<String> tagNames) {
        return getChildrenByNames(tagNames, IXmlElement.FACTORY);
    }

    public <T extends IXmlElement> List<T> getChildrenByNames(
        Collection<String> tagNames,
        IValueFactory<T> factory) {
        List<T> result = new ArrayList<T>();
        for (IXmlNode node : fElement) {
            if (!(node instanceof IXmlElement)) {
                continue;
            }
            IXmlElement e = (IXmlElement) node;
            if (tagNames.contains(e.getName())) {
                T resultNode = factory.newValue(e);
                result.add(resultNode);
            }
        }
        return result;
    }

    public <T> List<T> getChildrenByPath(
        IValueFactory<T> factory,
        String... path) {
        List<T> result = new ArrayList<T>();
        getChildrenByPath(result, path, 0, factory);
        return result;
    }

    private <T> void getChildrenByPath(
        List<T> result,
        String[] path,
        int pos,
        IValueFactory<T> factory) {
        if (pos >= path.length) {
            return;
        }
        String tagName = path[pos];
        for (IXmlNode node : fElement) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                if (tagName.equals(e.getName())) {
                    if (pos == path.length - 1) {
                        T resultNode = factory.newValue(e);
                        result.add(resultNode);
                    } else {
                        getChildrenByPath(result, path, pos + 1, factory);
                    }
                }
            }
        }
    }

    public List<IXmlElement> getChildrenByPath(String... path) {
        return getChildrenByPath(IXmlElement.FACTORY, path);
    }

    public IXmlElement getElement() {
        return fElement;
    }

    public IXmlFactory getFactory() {
        return fElement != null ? fElement.getFactory() : null;
    }

    public IXmlElement getOrCreateElement(String name) {
        return getOrCreateElement(fElement, name);
    }

    public <T> T getOrCreateElement(String name, IValueFactory<T> factory) {
        return getOrCreateElement(fElement, name, factory);
    }

    @Override
    public int hashCode() {
        return fElement != null ? fElement.hashCode() : 0;
    }

    public IXmlElement select(String cssSelector) {
        return select(fElement, cssSelector);
    }

    public <T extends IXmlNode> T select(
        String cssSelector,
        final IValueFactory<T> factory) {
        return select(fElement, cssSelector, factory);
    }

    public List<IXmlElement> selectAll(String cssSelector) {
        return selectAll(fElement, cssSelector);
    }

    public <T extends IXmlNode> List<T> selectAll(
        String cssSelector,
        IValueFactory<T> factory) {
        return selectAll(fElement, cssSelector, factory);
    }

    @Override
    public String toString() {
        return fElement != null ? fElement.toString() : null;
    }

}