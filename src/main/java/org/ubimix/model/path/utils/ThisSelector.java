/**
 * 
 */
package org.ubimix.model.path.utils;

import org.ubimix.model.path.INodeSelector;

/**
 * @author kotelnikov
 */
public class ThisSelector implements INodeSelector {
    @Override
    public Boolean accept(Object node) {
        return Boolean.TRUE;
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