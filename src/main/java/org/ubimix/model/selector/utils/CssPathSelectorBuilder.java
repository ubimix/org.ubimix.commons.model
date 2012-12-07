/**
 * 
 */
package org.ubimix.model.selector.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.css.CssSelectorListener;
import org.ubimix.commons.parser.css.CssSelectorParser;
import org.ubimix.commons.parser.css.ICssSelectorParser;
import org.ubimix.model.IHasValueMap;
import org.ubimix.model.selector.INodeProvider;
import org.ubimix.model.selector.INodeSelector;
import org.ubimix.model.selector.IPathSelector;
import org.ubimix.model.selector.PathProcessor;

/**
 * @author kotelnikov
 */
public class CssPathSelectorBuilder {

    /**
     * @author kotelnikov
     */
    public static class MapNodeSelector implements INodeSelector {

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
                    new SkipSelector(TextSelector.selector(
                        tagNameMatch,
                        tagName)));
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

    /**
     * @author kotelnikov
     */
    public static class PathSelectorBuilder {

        private Map<String, List<INodeSelector>> fAttributeSelectors = new LinkedHashMap<String, List<INodeSelector>>();

        protected Boolean fDefaultSelectResult;

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
            TextSelector selector = TextSelector
                .selector(matchType, matchValue);
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

    public static CssPathSelectorBuilder INSTANCE = new CssPathSelectorBuilder();

    private String fNameProperty;

    public CssPathSelectorBuilder() {
        this("!");
    }

    public CssPathSelectorBuilder(String nameProperty) {
        fNameProperty = nameProperty;
    }

    public IPathSelector build(String cssSelector) {
        ICssSelectorParser parser = new CssSelectorParser();
        ICharStream stream = new CharStream(cssSelector);
        final PathSelectorBuilder builder = new PathSelectorBuilder();
        parser.parse(stream, new CssSelectorListener() {

            @Override
            public void endElement(String elementMask) {
                if (elementMask != null) {
                    if ("*".equals(elementMask)) {
                        builder.addSelector(fNameProperty, new ThisSelector());
                    } else {
                        builder.addSelector(fNameProperty, "=", elementMask);
                    }
                }
                builder.addNode().skip(true);
            }

            @Override
            public void onAttribute(
                String attributeName,
                String matchType,
                String matchValue) {
                if ("umx|tag".equals(attributeName)) {
                    attributeName = fNameProperty;
                }
                builder.addSelector(attributeName, matchType, matchValue);
            }

            @Override
            public void onElementCombinator(char combinator) {
                builder.skip(combinator != '>');
            }

        });
        IPathSelector selector = builder.build();
        return selector;
    }
}
