/**
 * 
 */
package org.ubimix.model.path.xml;

import java.util.LinkedHashMap;
import java.util.Map;

import org.ubimix.model.path.INodeSelector;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class XmlElementSelector implements INodeSelector {

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

    private Map<String, INodeSelector> fAttributeSelectors;

    private INodeSelector.SelectionResult fDefaultSelectResult;

    private String fName;

    public XmlElementSelector(
        String name,
        INodeSelector.SelectionResult defaultSelectResult,
        Map<String, INodeSelector> attributeSelectors) {
        fName = name;
        fDefaultSelectResult = defaultSelectResult;
        fAttributeSelectors = attributeSelectors;
    }

    public XmlElementSelector(
        String name,
        INodeSelector.SelectionResult defaultSelectResult,
        String... attributes) {
        this(name, defaultSelectResult, toAttributeSelectors(attributes));
    }

    /**
     * A copy constructor used to change the attribute values.
     * 
     * @param filter the filter used as a template
     * @param attributeSelectors attribute filter values
     */
    public XmlElementSelector(
        XmlElementSelector filter,
        Map<String, INodeSelector> attributeSelectors) {
        fName = filter != null ? filter.fName : null;
        fDefaultSelectResult = filter != null
            ? filter.fDefaultSelectResult
            : INodeSelector.SelectionResult.NO;
        fAttributeSelectors = attributeSelectors;
    }

    @Override
    public INodeSelector.SelectionResult accept(Object node) {
        INodeSelector.SelectionResult result = INodeSelector.SelectionResult.NO;
        if (node instanceof XmlElement) {
            XmlElement e = (XmlElement) node;
            if (fName != null) {
                if (fName.equals(e.getName())) {
                    result = checkAttributes(
                        e,
                        INodeSelector.SelectionResult.YES,
                        INodeSelector.SelectionResult.NO);
                } else {
                    result = fDefaultSelectResult;
                }
            } else {
                result = checkAttributes(
                    e,
                    fDefaultSelectResult,
                    fDefaultSelectResult);
            }
        }
        return result;
    }

    private INodeSelector.SelectionResult checkAttributes(
        XmlElement e,
        INodeSelector.SelectionResult defaultResult,
        INodeSelector.SelectionResult negativeResult) {
        INodeSelector.SelectionResult result = defaultResult;
        if (fAttributeSelectors != null) {
            result = INodeSelector.SelectionResult.YES;
            for (Map.Entry<String, INodeSelector> entry : fAttributeSelectors
                .entrySet()) {
                String attrName = entry.getKey();

                String value = e.getAttribute(attrName);
                if (value == null) {
                    result = negativeResult;
                    break;
                }

                INodeSelector selector = entry.getValue();
                if (selector != null) {
                    SelectionResult attrResult = selector.accept(value);
                    result = sum(result, attrResult);
                    if (result == INodeSelector.SelectionResult.NO) {
                        result = negativeResult;
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
        if (!(obj instanceof XmlElementSelector)) {
            return false;
        }
        XmlElementSelector o = (XmlElementSelector) obj;
        return equals(fName, o.fName)
            && equals(fAttributeSelectors, o.fAttributeSelectors);
    }

    private boolean equals(Object a, Object b) {
        return a != null && b != null ? a.equals(b) : a == b;
    }

    @Override
    public int hashCode() {
        int a = fName != null ? fName.hashCode() : 0;
        int b = fAttributeSelectors != null
            ? fAttributeSelectors.hashCode()
            : 0;
        return a ^ b;
    }

    protected SelectionResult sum(SelectionResult first, SelectionResult second) {
        return first.and(second);
    }

    @Override
    public String toString() {
        return "ElementFilter[" + fName + ":" + fAttributeSelectors + "]";
    }

}