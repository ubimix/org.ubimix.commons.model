/**
 * 
 */
package org.ubimix.model.path;

import java.util.List;

/**
 * @author kotelnikov
 */
public class ANDSelector extends CompositeSelector {

    /**
     * @param selectors
     */
    public ANDSelector(INodeSelector... selectors) {
        super(selectors);
    }

    /**
     * @param selectors
     */
    public ANDSelector(List<INodeSelector> selectors) {
        super(selectors);
    }

    /**
     * @see org.ubimix.model.path.CompositeSelector#getStopResult()
     */
    @Override
    protected SelectionResult getStopResult() {
        return SelectionResult.NO;
    }

    /**
     * @see org.ubimix.model.path.CompositeSelector#process(org.ubimix.model.path.INodeSelector.SelectionResult,
     *      org.ubimix.model.path.INodeSelector.SelectionResult)
     */
    @Override
    protected SelectionResult process(
        SelectionResult previousResult,
        SelectionResult currentResult) {
        return previousResult.and(currentResult);
    }

}
