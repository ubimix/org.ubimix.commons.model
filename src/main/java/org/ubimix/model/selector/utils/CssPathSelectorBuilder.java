/**
 * 
 */
package org.ubimix.model.selector.utils;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.css.CssSelectorListener;
import org.ubimix.commons.parser.css.CssSelectorParser;
import org.ubimix.commons.parser.css.ICssSelectorParser;
import org.ubimix.model.selector.IPathSelector;

/**
 * @author kotelnikov
 */
public class CssPathSelectorBuilder {

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
        final PathSelectorBuilder fBuilder = new PathSelectorBuilder();
        parser.parse(stream, new CssSelectorListener() {

            @Override
            public void endElement(String elementMask) {
                if (elementMask != null) {
                    if ("*".equals(elementMask)) {
                        fBuilder.addSelector(fNameProperty, new ThisSelector());
                    } else {
                        fBuilder.addSelector(fNameProperty, "=", elementMask);
                    }
                }
                fBuilder.addNode().skip(true);
            }

            @Override
            public void onAttribute(
                String attributeName,
                String matchType,
                String matchValue) {
                if ("umx|tag".equals(attributeName)) {
                    attributeName = fNameProperty;
                }
                fBuilder.addSelector(attributeName, matchType, matchValue);
            }

            @Override
            public void onElementCombinator(char combinator) {
                fBuilder.skip(combinator != '>');
            }

        });
        IPathSelector selector = fBuilder.build();
        return selector;
    }
}
