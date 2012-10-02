/**
 * 
 */
package org.ubimix.model.path;

public class ThisSelector implements INodeSelector {
    public INodeSelector.SelectionResult accept(Object node) {
        return INodeSelector.SelectionResult.YES;
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
        return "ThisSelector";
    }
}