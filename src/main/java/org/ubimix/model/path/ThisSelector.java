/**
 * 
 */
package org.ubimix.model.path;

public class ThisSelector implements INodeSelector {
    public INodeSelector.Accept accept(Object node) {
        return INodeSelector.Accept.YES;
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