/**
 * 
 */
package org.ubimix.model.path;

import java.util.Iterator;

/**
 * @author kotelnikov
 */
public interface INodeSelector {

    public static enum SelectionResult {

        MAYBE, NO, YES;

        /**
         * Using this method the {@link #AND(SelectionResult, SelectionResult)} operation could be
         * represented as the <code>_DO(NOT, a, b)</code> call. The
         * {@link #OR(SelectionResult, SelectionResult)} operation corresponds to the
         * <code>_DO(AND, a, b)</code> method call.
         * 
         * @param r
         * @param a
         * @param b
         * @return
         */
        public static SelectionResult _DO(SelectionResult r, SelectionResult a, SelectionResult b) {
            if (a == r || b == r)
                return r;
            r = r.not();
            if (a == r && b == r)
                return r;
            return MAYBE;
        }

        public static SelectionResult AND(SelectionResult a, SelectionResult b) {
            if (a == NO || b == NO)
                return NO;
            if (a == YES && b == YES)
                return YES;
            return MAYBE;
        }

        public static SelectionResult NOT(SelectionResult a) {
            if (a == NO)
                return YES;
            if (a == YES)
                return NO;
            return MAYBE;
        }

        public static SelectionResult OR(SelectionResult a, SelectionResult b) {
            if (a == YES || b == YES)
                return YES;
            if (a == NO && b == NO)
                return NO;
            return MAYBE;
        }

        public static SelectionResult XOR(SelectionResult a, SelectionResult b) {
            if (a == b) {
                if (a == MAYBE) {
                    return MAYBE;
                } else {
                    return NO;
                }
            }
            if (a == YES || b == YES)
                return YES;
            return MAYBE;
        }

        public SelectionResult and(SelectionResult... values) {
            SelectionResult result = this;
            for (int i = 0; result != NO && i < values.length; i++) {
                result = AND(result, values[i]);
            }
            return result;
        }

        public SelectionResult and(Iterable<SelectionResult> values) {
            SelectionResult result = this;
            if (result != NO) {
                Iterator<SelectionResult> iterator = values.iterator();
                while (result != NO && iterator.hasNext()) {
                    result = AND(result, iterator.next());
                }
            }
            return result;
        }

        public SelectionResult not() {
            return NOT(this);
        }

        public SelectionResult or(SelectionResult... values) {
            SelectionResult result = this;
            for (int i = 0; result != YES && i < values.length; i++) {
                result = OR(result, values[i]);
            }
            return result;
        }

        public SelectionResult or(Iterable<SelectionResult> values) {
            SelectionResult result = this;
            if (result != YES) {
                Iterator<SelectionResult> iterator = values.iterator();
                while (result != YES && iterator.hasNext()) {
                    result = OR(result, iterator.next());
                }
            }
            return result;
        }
    }

    SelectionResult accept(Object node);
}