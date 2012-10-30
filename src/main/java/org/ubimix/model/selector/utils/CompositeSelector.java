/**
 * 
 */
package org.ubimix.model.selector.utils;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.model.selector.INodeSelector;

/**
 * @author kotelnikov
 */
public abstract class CompositeSelector implements INodeSelector {

    private List<INodeSelector> fSelectors = new ArrayList<INodeSelector>();

    /**
     * 
     */
    public CompositeSelector(INodeSelector... selectors) {
        for (INodeSelector selector : selectors) {
            fSelectors.add(selector);
        }
    }

    /**
     * 
     */
    public CompositeSelector(List<INodeSelector> selectors) {
        fSelectors.addAll(selectors);
    }

    /**
     * @see org.ubimix.model.selector.INodeSelector#accept(java.lang.Object)
     */
    @Override
    public Boolean accept(Object node) {
        Boolean result = null;
        int size = fSelectors.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                INodeSelector selector = fSelectors.get(i);
                Boolean selectionResult = selector.accept(node);
                if (result == null) {
                    result = selectionResult;
                } else {
                    result = process(result, selectionResult);
                }
                if (result == getStopResult()) {
                    break;
                }
            }
        }
        if (result == null) {
            result = getDefaultValue();
        }
        return result;
    }

    protected Boolean getDefaultValue() {
        return Boolean.FALSE;
    }

    protected abstract Boolean getStopResult();

    protected abstract Boolean process(
        Boolean previousResult,
        Boolean currentResult);
}
