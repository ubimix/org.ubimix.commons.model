/**
 * 
 */
package org.ubimix.model.path.xml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.model.path.INodeSelector;
import org.ubimix.model.path.PathProcessor;
import org.ubimix.model.path.PathSelectorBuilder;

/**
 * @author kotelnikov
 */
public class XmlSelectorBuilder extends PathSelectorBuilder {

    /**
     * This map contains namespace prefixes and the corresponding namespace
     * URLs.
     */
    private Map<String, String> fNsPrefixes = new HashMap<String, String>();

    @Override
    public XmlSelectorBuilder add(INodeSelector selector) {
        super.add(selector);
        return this;
    }

    public XmlSelectorBuilder attrs(Map<String, String> map) {
        Map<String, INodeSelector> m = new LinkedHashMap<String, INodeSelector>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String name = entry.getKey();
            String mask = entry.getValue();

            INodeSelector selector = mask != null
                ? new TextSelector(mask)
                : null;
            m.put(name, selector);
        }
        return attrSelectors(m);
    }

    public XmlSelectorBuilder attrs(String... attributes) {
        Map<String, String> map = toMap(attributes);
        return attrs(map);
    }

    public XmlSelectorBuilder attrSelectors(Map<String, INodeSelector> map) {
        int len = fList.size();
        XmlElementSelector elementFilter = null;
        INodeSelector filter = len > 0 ? fList.get(len - 1) : null;
        if (filter instanceof XmlElementSelector) {
            elementFilter = (XmlElementSelector) filter;
        }
        XmlElementSelector newFilter = new XmlElementSelector(
            elementFilter,
            map);
        if (elementFilter != null) {
            fList.set(len - 1, newFilter);
        } else {
            fList.add(newFilter);
        }
        return this;
    }

    public PathProcessor buildPath() {
        return buildPath(new XmlNodeProvider());
    }

    @Override
    public XmlSelectorBuilder clear() {
        super.clear();
        return this;
    }

    public XmlSelectorBuilder element(String name) {
        name = getName(name);
        return element(name, false);
    }

    private XmlSelectorBuilder element(String name, boolean deep) {
        name = getName(name);
        INodeSelector.SelectionResult defaultSelectResult = deep
            ? INodeSelector.SelectionResult.MAYBE
            : INodeSelector.SelectionResult.NO;
        XmlElementSelector selector = new XmlElementSelector(
            name,
            defaultSelectResult);
        return add(selector);
    }

    private String getName(String name) {
        return name;
    }

    @Override
    public XmlSelectorBuilder node() {
        super.node();
        return this;
    }

    /**
     * Sets a new prefixes and the corresponding namespaces.
     * 
     * @param prefixes a dictionary of prefixes and the corresponding namespaces
     *        to set
     */
    public void setNamespacePrefixes(Map<String, String> prefixes) {
        fNsPrefixes.clear();
        fNsPrefixes.putAll(prefixes);
    }

    public XmlSelectorBuilder text(String mask) {
        XmlTextSelector selector = new XmlTextSelector(mask);
        return add(selector);
    }

    private Map<String, String> toMap(String... attributes) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (int i = 0; i < attributes.length; i++) {
            String name = attributes[i];
            i++;
            String value = i < attributes.length ? attributes[i] : "";
            String n = getName(name);
            result.put(n, value);
        }
        return result;
    }

}