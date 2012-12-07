/**
 * 
 */
package org.ubimix.model.selector.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.css.CssSelectorListener;
import org.ubimix.commons.parser.css.CssSelectorParser;
import org.ubimix.commons.parser.css.ICssSelectorParser;
import org.ubimix.model.selector.INodeSelector;
import org.ubimix.model.selector.IPathSelector;
import org.ubimix.model.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class HtmlCssPathSelectorBuilder {

    public static class XmlElementSelector implements INodeSelector {

        private Map<String, INodeSelector> fAttributeSelectors = new LinkedHashMap<String, INodeSelector>();

        private INodeSelector fNameSelector;

        public XmlElementSelector(
            INodeSelector nameSelector,
            Map<String, List<INodeSelector>> attributeSelectors) {
            fNameSelector = nameSelector;
            for (Map.Entry<String, List<INodeSelector>> entry : attributeSelectors
                .entrySet()) {
                String attr = entry.getKey();
                List<INodeSelector> selectors = entry.getValue();
                INodeSelector selector;
                if (selectors.size() == 1) {
                    selector = selectors.get(0);
                } else {
                    selector = new ANDSelector(selectors);
                }
                fAttributeSelectors.put(attr, selector);
            }
        }

        @Override
        public Boolean accept(Object node) {
            if (!(node instanceof IXmlElement)) {
                return false;
            }
            IXmlElement e = (IXmlElement) node;
            Boolean result = null;
            if (fNameSelector != null) {
                String name = e.getName();
                result = fNameSelector.accept(name);
            }
            if (!Boolean.FALSE.equals(result) && !fAttributeSelectors.isEmpty()) {
                result = Boolean.TRUE;
                for (Map.Entry<String, INodeSelector> entry : fAttributeSelectors
                    .entrySet()) {
                    if (Boolean.FALSE.equals(result)) {
                        break;
                    }
                    String attr = entry.getKey();
                    INodeSelector selector = entry.getValue();
                    String value = e.getAttribute(attr);
                    result = value != null ? ANDSelector.and(
                        result,
                        selector.accept(value)) : Boolean.FALSE;
                }
            }
            return result;
        }

    }

    public static HtmlCssPathSelectorBuilder INSTANCE = new HtmlCssPathSelectorBuilder();

    public HtmlCssPathSelectorBuilder() {
    }

    public IPathSelector build(String cssSelector) {
        return build(cssSelector, false);
    }

    public IPathSelector build(
        String cssSelector,
        final boolean defaultSelectResult) {
        ICssSelectorParser parser = new CssSelectorParser();
        ICharStream stream = new CharStream(cssSelector);
        final ArrayList<INodeSelector> list = new ArrayList<INodeSelector>();
        parser.parse(stream, new CssSelectorListener() {

            private Map<String, List<INodeSelector>> fAttributeSelectors = new LinkedHashMap<String, List<INodeSelector>>();

            private INodeSelector fNameSelector;

            private boolean fSkip = true;

            @Override
            public void endElement(String elementMask) {
                if (elementMask != null) {
                    if ("*".equals(elementMask)) {
                        fNameSelector = new ThisSelector();
                    } else {
                        fNameSelector = TextSelector.selector("=", elementMask);
                    }
                }
                if (!fAttributeSelectors.isEmpty() || fNameSelector != null) {
                    INodeSelector selector = new XmlElementSelector(
                        fNameSelector,
                        fAttributeSelectors);
                    if (fSkip) {
                        selector = new SkipSelector(selector);
                    }
                    list.add(selector);
                }
                fAttributeSelectors.clear();
                fNameSelector = null;
                fSkip = true;
            }

            @Override
            public void onAttribute(
                String attributeName,
                String matchType,
                String matchValue) {
                TextSelector selector = TextSelector.selector(
                    matchType,
                    matchValue);
                List<INodeSelector> selectors = fAttributeSelectors
                    .get(attributeName);
                if (selectors == null) {
                    selectors = new ArrayList<INodeSelector>();
                    fAttributeSelectors.put(attributeName, selectors);
                }
                selectors.add(selector);
            }

            @Override
            public void onElementCombinator(char combinator) {
                fSkip = combinator != '>';
            }

        });
        IPathSelector selector = new PathSelector(list);
        return selector;
    }
}
