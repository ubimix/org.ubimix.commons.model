package org.ubimix.model.path.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.css.CssSelectorParser;
import org.ubimix.model.path.ANDSelector;
import org.ubimix.model.path.INodeSelector;
import org.ubimix.model.path.INodeSelector.SelectionResult;
import org.ubimix.model.path.PathNodeSelector;
import org.ubimix.model.path.SkipSelector;

/**
 * @author kotelnikov
 */
public class CssPathSelector extends PathNodeSelector {

    public CssPathSelector(String cssSelector) {
        CssSelectorParser parser = new CssSelectorParser();
        ICharStream stream = new CharStream(cssSelector);
        final List<INodeSelector> list = new ArrayList<INodeSelector>();
        parser.parse(stream, new CssSelectorParser.CssSelectorListener() {
            private Map<String, List<INodeSelector>> fAttributeSelectors;

            private boolean fSkip = true;

            @Override
            public void beginElement(String elementMask) {
                fAttributeSelectors = new LinkedHashMap<String, List<INodeSelector>>();
            }

            @Override
            public void endElement(String elementMask) {
                String tagName = !"".equals(elementMask) ? elementMask : null;
                Map<String, INodeSelector> map = new HashMap<String, INodeSelector>();
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
                XmlElementSelector elementSelector = new XmlElementSelector(
                    tagName,
                    SelectionResult.NO,
                    map);
                if (fSkip) {
                    SkipSelector selector = new SkipSelector(elementSelector);
                    list.add(selector);
                } else {
                    list.add(elementSelector);
                }
                fSkip = true;
            }

            @Override
            public void onAttribute(
                String attributeName,
                String matchType,
                String matchValue) {
                INodeSelector attributeSelector = null;
                if (matchValue != null) {
                    char match = 0;
                    if (matchType.length() > 0) {
                        match = matchType.charAt(0);
                    }
                    if (matchValue.startsWith("\'")
                        || matchValue.startsWith("\"")) {
                        matchValue = matchValue.substring(1);
                    }
                    if (matchValue.endsWith("\'") || matchValue.endsWith("\"")) {
                        matchValue = matchValue.substring(
                            0,
                            matchValue.length() - 1);
                    }
                    switch (match) {
                        case '=':
                            attributeSelector = new TextSelector(matchValue) {
                                @Override
                                protected boolean match(
                                    String value,
                                    String mask) {
                                    return value.equals(mask);
                                }
                            };
                            break;
                        case '~':
                            attributeSelector = new TextSelector(matchValue) {
                                @Override
                                protected boolean match(
                                    String value,
                                    String mask) {
                                    return value.indexOf(mask) >= 0;
                                }
                            };
                            break;
                        case '^':
                            attributeSelector = new TextSelector(matchValue) {
                                @Override
                                protected boolean match(
                                    String value,
                                    String mask) {
                                    return value.startsWith(mask);
                                }
                            };
                            break;
                        case '$':
                            attributeSelector = new TextSelector(matchValue) {
                                @Override
                                protected boolean match(
                                    String value,
                                    String mask) {
                                    return value.endsWith(mask);
                                }
                            };
                            break;
                    }
                }
                List<INodeSelector> list = fAttributeSelectors
                    .get(attributeName);
                if (list == null) {
                    list = new ArrayList<INodeSelector>();
                    fAttributeSelectors.put(attributeName, list);
                }
                list.add(attributeSelector);
            }

            @Override
            public void onElementCombinator(char combinator) {
                fSkip = combinator == '>';
                if (combinator == '>') {
                    fSkip = false;
                } else if (combinator == ',') {

                }
            }

        });
        setNodeSelectors(list);
    }

}