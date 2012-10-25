/**
 * 
 */
package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.path.INodeProvider;
import org.ubimix.model.path.IPathNodeCollector;
import org.ubimix.model.path.IPathSelector;
import org.ubimix.model.path.PathProcessor;
import org.ubimix.model.path.utils.CssPathSelectorBuilder;
import org.ubimix.model.path.utils.TreeNodeProvider;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class XmlSelect {

    public static XmlSelect on(XmlElement e) {
        return new XmlSelect(e);
    }

    private XmlElement fElement;

    public XmlSelect(XmlElement element) {
        fElement = element;
    }

    protected PathProcessor getProcessor(String cssSelector) {
        PathProcessor pathProcessor = null;
        if (fElement != null) {
            CssPathSelectorBuilder builder = new CssPathSelectorBuilder();
            IPathSelector selector = builder.build(cssSelector);
            INodeProvider provider = new TreeNodeProvider(
                XmlElement.TREE_ACCESSOR) {
                @Override
                protected IValueFactory<?> getChildNodeFactory(
                    IHasValueMap element) {
                    IValueFactory<XmlNode> result = null;
                    if (element instanceof XmlElement) {
                        result = ((XmlElement) element).getNodeFactory();
                    }
                    return result;
                }
            };
            pathProcessor = new PathProcessor(provider, selector);
        }
        return pathProcessor;
    }

    public XmlElement select(String cssSelector) {
        final XmlElement[] results = { null };
        if (fElement != null) {
            PathProcessor processor = getProcessor(cssSelector);
            processor.select(fElement, new IPathNodeCollector() {
                @Override
                public boolean setResult(Object node) {
                    results[0] = ((XmlElement) node);
                    return false;
                }
            });
        }
        return results[0];
    }

    public List<XmlElement> selectAll(String cssSelector) {
        final List<XmlElement> results = new ArrayList<XmlElement>();
        if (fElement != null) {
            PathProcessor processor = getProcessor(cssSelector);
            processor.select(fElement, new IPathNodeCollector() {
                @Override
                public boolean setResult(Object node) {
                    results.add((XmlElement) node);
                    return true;
                }
            });
        }
        return results;
    }
}
