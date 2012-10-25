/**
 * 
 */
package org.ubimix.model.path.utils;

import java.util.List;

import org.ubimix.model.path.INodeSelector;

/**
 * @author kotelnikov
 */
public class ANDSelector extends CompositeSelector {

    public static Boolean and(Boolean a, Boolean b) {
        if (Boolean.FALSE.equals(a) || Boolean.FALSE.equals(b)) {
            return Boolean.FALSE;
        }
        if (Boolean.TRUE.equals(a) && Boolean.TRUE.equals(b)) {
            return Boolean.TRUE;
        }
        return null;
    }

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
     * @see org.ubimix.model.path.utils.CompositeSelector#getStopResult()
     */
    @Override
    protected Boolean getStopResult() {
        return Boolean.FALSE;
    }

    /**
     * @see org.ubimix.model.path.utils.CompositeSelector#process(org.ubimix.model.path.INodeSelector.Boolean,
     *      org.ubimix.model.path.INodeSelector.Boolean)
     */
    @Override
    protected Boolean process(Boolean a, Boolean b) {
        return and(a, b);
    }

}
