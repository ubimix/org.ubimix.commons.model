/**
 * 
 */
package org.ubimix.model.path.utils;

import org.ubimix.model.path.INodeSelector;

/**
 * Selectors of this type are used to skip the current node and continue to make
 * search on the
 * 
 * @author kotelnikov
 */
public class SkipSelector implements INodeSelector {

    private INodeSelector fSelector;

    public SkipSelector(INodeSelector selector) {
        fSelector = selector;
    }

    @Override
    public INodeSelector.SelectionResult accept(Object node) {
        SelectionResult result = fSelector.accept(node);
        return (result == SelectionResult.YES)
            ? SelectionResult.YES
            : SelectionResult.MAYBE;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "SkipSelector[" + fSelector + "]";
    }
}