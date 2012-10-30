/**
 * 
 */
package org.ubimix.model.selector.utils;

/**
 * @author kotelnikov
 */
public class ORSelector extends CompositeSelector {

    @Override
    protected Boolean getStopResult() {
        return Boolean.TRUE;
    }

    @Override
    protected Boolean process(Boolean a, Boolean b) {
        if (Boolean.TRUE.equals(a) || Boolean.TRUE.equals(b)) {
            return Boolean.TRUE;
        }
        if (Boolean.FALSE.equals(a) && Boolean.FALSE.equals(b)) {
            return Boolean.FALSE;
        }
        return null;
    }

}
