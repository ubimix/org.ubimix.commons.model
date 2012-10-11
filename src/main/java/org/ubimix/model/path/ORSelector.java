/**
 * 
 */
package org.ubimix.model.path;

/**
 * @author kotelnikov
 */
public class ORSelector extends CompositeSelector {

    @Override
    protected SelectionResult getStopResult() {
        return SelectionResult.YES;
    }

    @Override
    protected SelectionResult process(
        SelectionResult previousResult,
        SelectionResult currentResult) {
        return previousResult.or(currentResult);
    }

}
