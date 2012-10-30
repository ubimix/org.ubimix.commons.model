/**
 * 
 */
package org.ubimix.model.selector.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.model.IHasValueMap;
import org.ubimix.model.selector.INodeSelector;

/**
 * @author kotelnikov
 */
public class MapNodeSelector implements INodeSelector {

    public static SkipSelector getDefaultTagSelector(
        String tagName,
        String... attrs) {
        return getTagSelector("!", "~", tagName, attrs);
    }

    public static SkipSelector getTagSelector(
        String tagNameField,
        String tagNameMatch,
        String tagName,
        String... attrs) {
        Map<String, INodeSelector> map = MapNodeSelector
            .toAttributeSelectors(attrs);
        if (tagName != null) {
            if (map == null) {
                map = new HashMap<String, INodeSelector>();
            }
            map.put(
                tagNameField,
                new SkipSelector(TextSelector.selector(tagNameMatch, tagName)));
        }
        SkipSelector selector = new SkipSelector(new MapNodeSelector(
            Boolean.FALSE,
            map));
        return selector;
    }

    public static Map<String, INodeSelector> toAttributeSelectors(
        String... attributes) {
        if (attributes == null || attributes.length == 0) {
            return null;
        }
        Map<String, INodeSelector> result = new LinkedHashMap<String, INodeSelector>();
        for (int i = 0; i < attributes.length; i++) {
            String name = attributes[i];
            i++;
            String value = i < attributes.length ? attributes[i] : "";
            INodeSelector selector = value != null
                ? new TextSelector(value)
                : null;
            result.put(name, selector);
        }
        return result;
    }

    private Boolean fDefaultSelectResult;

    private Map<String, INodeSelector> fSelectors;

    public MapNodeSelector(
        Boolean defaultSelectResult,
        Map<String, INodeSelector> selectors) {
        fDefaultSelectResult = defaultSelectResult;
        fSelectors = selectors;
    }

    @Override
    public Boolean accept(Object node) {
        Boolean result = Boolean.FALSE;
        if (node instanceof IHasValueMap) {
            Map<?, ?> map = ((IHasValueMap) node).getMap();
            result = fDefaultSelectResult;
            if (fSelectors != null && !fSelectors.isEmpty()) {
                result = Boolean.TRUE;
                for (Map.Entry<String, INodeSelector> entry : fSelectors
                    .entrySet()) {
                    String attrName = entry.getKey();
                    Object value = map.get(attrName);
                    INodeSelector selector = entry.getValue();
                    Boolean attrResult = selector.accept(value);
                    result = ANDSelector.and(result, attrResult);
                    if (result == Boolean.FALSE) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MapNodeSelector)) {
            return false;
        }
        MapNodeSelector o = (MapNodeSelector) obj;
        return equals(fSelectors, o.fSelectors);
    }

    private boolean equals(Object a, Object b) {
        return a != null && b != null ? a.equals(b) : a == b;
    }

    @Override
    public int hashCode() {
        int a = fSelectors != null ? fSelectors.hashCode() : 0;
        return a;
    }

    @Override
    public String toString() {
        return "ElementFilter[" + fSelectors + "]";
    }
}