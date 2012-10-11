/**
 * 
 */
package org.ubimix.model.path.utils;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.model.path.INodeSelector;

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
     * @see org.ubimix.model.path.INodeSelector#accept(java.lang.Object)
     */
    @Override
    public SelectionResult accept(Object node) {
        SelectionResult result = null;
        int size = fSelectors.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                INodeSelector selector = fSelectors.get(i);
                SelectionResult selectionResult = selector.accept(node);
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

    protected SelectionResult getDefaultValue() {
        return SelectionResult.NO;
    }

    protected abstract SelectionResult getStopResult();

    protected abstract SelectionResult process(
        SelectionResult previousResult,
        SelectionResult currentResult);
}
