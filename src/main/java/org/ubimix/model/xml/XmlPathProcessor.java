/**
 * 
 */
package org.ubimix.model.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.selector.INodeProvider;
import org.ubimix.model.selector.IPathNodeCollector;
import org.ubimix.model.selector.IPathSelector;
import org.ubimix.model.selector.PathProcessor;
import org.ubimix.model.selector.utils.CssPathSelectorBuilder;

/**
 * @author kotelnikov
 */
public class XmlPathProcessor extends PathProcessor {

    private static final INodeProvider XML_TREE_NODE_PROVIDER = new INodeProvider() {
        @Override
        public Iterator<?> getChildren(Object object) {
            Iterator<?> result = null;
            if (object instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) object;
                result = e.iterator();
            }
            return result;
        }
    };

    /**
     * @param nodeProvider
     * @param filters
     */
    public XmlPathProcessor(IPathSelector filters) {
        super(XML_TREE_NODE_PROVIDER, filters);
    }

    public XmlPathProcessor(String cssSelector) {
        this(CssPathSelectorBuilder.INSTANCE.build(cssSelector));
    }

    public IXmlElement select(IXmlElement element) {
        return select(element, IXmlElement.FACTORY);
    }

    @SuppressWarnings("unchecked")
    public <T extends IXmlNode> T select(
        IXmlElement element,
        final IValueFactory<T> factory) {
        final Object[] results = { null };
        select(element, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                results[0] = factory.newValue(node);
                return false;
            }
        });
        return (T) results[0];
    }

    public List<IXmlElement> selectAll(IXmlElement element) {
        return selectAll(element, IXmlElement.FACTORY);
    }

    public <T extends IXmlNode> List<T> selectAll(
        IXmlElement element,
        final IValueFactory<T> factory) {
        final List<T> results = new ArrayList<T>();
        select(element, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                T value = factory.newValue(node);
                results.add(value);
                return true;
            }
        });
        return results;
    }

}
