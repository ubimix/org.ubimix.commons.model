/**
 * 
 */
package org.ubimix.model.path.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.path.INodeProvider;
import org.ubimix.model.path.INodeSelector;
import org.ubimix.model.path.IPathSelector;
import org.ubimix.model.path.PathProcessor;

/**
 * @author kotelnikov
 */
public class PathSelectorBuilder {

    private Map<String, List<INodeSelector>> fAttributeSelectors = new LinkedHashMap<String, List<INodeSelector>>();

    private Boolean fDefaultSelectResult;

    protected List<INodeSelector> fList = new ArrayList<INodeSelector>();

    private boolean fSkip = true;

    public PathSelectorBuilder() {
        this(Boolean.FALSE);
    }

    public PathSelectorBuilder(Boolean defaultSelectResult) {
        fDefaultSelectResult = defaultSelectResult;
    }

    public PathSelectorBuilder addNode() {
        Map<String, INodeSelector> map = new HashMap<String, INodeSelector>();
        if (fAttributeSelectors != null && !fAttributeSelectors.isEmpty()) {
            for (Map.Entry<String, List<INodeSelector>> entry : fAttributeSelectors
                .entrySet()) {
                String attr = entry.getKey();
                List<INodeSelector> selectors = entry.getValue();
                INodeSelector selector;
                if (selectors.size() > 1) {
                    selector = new ANDSelector(selectors);
                } else {
                    selector = selectors.get(0);
                }
                map.put(attr, selector);
            }
        }
        INodeSelector nodeSelector = newNodeSelector(map);
        if (nodeSelector != null) {
            if (fSkip) {
                nodeSelector = new SkipSelector(nodeSelector);
                fList.add(nodeSelector);
            } else {
                fList.add(nodeSelector);
            }
        }
        if (!fAttributeSelectors.isEmpty()) {
            fAttributeSelectors = new LinkedHashMap<String, List<INodeSelector>>();
        }
        skip(true);
        return this;
    }

    public PathSelectorBuilder addSelector(
        String attributeName,
        INodeSelector selector) {
        List<INodeSelector> list = fAttributeSelectors.get(attributeName);
        if (list == null) {
            list = new ArrayList<INodeSelector>();
            fAttributeSelectors.put(attributeName, list);
        }
        list.add(selector);
        return this;
    }

    public PathSelectorBuilder addSelector(
        String attributeName,
        String matchType,
        String matchValue) {
        TextSelector selector = TextSelector.selector(matchType, matchValue);
        return addSelector(attributeName, selector);
    }

    public IPathSelector build() {
        addNode();
        final ArrayList<INodeSelector> list = new ArrayList<INodeSelector>(
            fList);
        return new PathSelector(list);
    }

    public PathProcessor buildPath(INodeProvider manager) {
        IPathSelector filters = build();
        return new PathProcessor(manager, filters);
    }

    public PathSelectorBuilder clear() {
        fList.clear();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PathSelectorBuilder)) {
            return false;
        }
        PathSelectorBuilder o = (PathSelectorBuilder) obj;
        return fList.equals(o.fList);
    }

    @Override
    public int hashCode() {
        return fList.hashCode();
    }

    protected INodeSelector newNodeSelector(Map<String, INodeSelector> map) {
        if (map.isEmpty()) {
            return null;
        }
        return new MapNodeSelector(fDefaultSelectResult, map);
    }

    public PathSelectorBuilder skip() {
        return skip(true);
    }

    public PathSelectorBuilder skip(boolean b) {
        fSkip = b;
        return this;
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        int idx = name.lastIndexOf(".");
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        return name + "( " + fList + " )";
    }
}
