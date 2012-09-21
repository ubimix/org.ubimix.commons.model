/**
 * 
 */
package org.ubimix.model.path.xml;

import org.ubimix.model.path.INodeSelector;

/**
 * @author kotelnikov
 */
public class TextSelector implements INodeSelector {

    private String fMask;

    public TextSelector(String mask) {
        fMask = mask;
    }

    public Accept accept(Object node) {
        INodeSelector.Accept result = INodeSelector.Accept.MAYBE;
        String value = getTextValue(node);
        if (value != null) {
            result = INodeSelector.Accept.YES;
            if (fMask != null) {
                if (value.indexOf(fMask) < 0) {
                    result = INodeSelector.Accept.NO;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TextSelector)) {
            return false;
        }
        TextSelector o = (TextSelector) obj;
        return fMask == null || o.fMask == null ? fMask == o.fMask : fMask
            .equals(o.fMask);
    }

    protected String getTextValue(Object node) {
        return node != null ? node.toString() : null;
    }

    @Override
    public int hashCode() {
        return fMask != null ? fMask.hashCode() : 0;
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        return name + "[" + fMask + "]";
    }
}
