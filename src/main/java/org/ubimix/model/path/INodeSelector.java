/**
 * 
 */
package org.ubimix.model.path;

import java.util.Iterator;

/**
 * @author kotelnikov
 */
public interface INodeSelector {

    public static enum Accept {

        MAYBE, NO, YES;

        /**
         * Using this method the {@link #AND(Accept, Accept)} operation could be
         * represented as the <code>_DO(NOT, a, b)</code> call. The
         * {@link #OR(Accept, Accept)} operation corresponds to the
         * <code>_DO(AND, a, b)</code> method call.
         * 
         * @param r
         * @param a
         * @param b
         * @return
         */
        public static Accept _DO(Accept r, Accept a, Accept b) {
            if (a == r || b == r)
                return r;
            r = r.not();
            if (a == r && b == r)
                return r;
            return MAYBE;
        }

        public static Accept AND(Accept a, Accept b) {
            if (a == NO || b == NO)
                return NO;
            if (a == YES && b == YES)
                return YES;
            return MAYBE;
        }

        public static Accept NOT(Accept a) {
            if (a == NO)
                return YES;
            if (a == YES)
                return NO;
            return MAYBE;
        }

        public static Accept OR(Accept a, Accept b) {
            if (a == YES || b == YES)
                return YES;
            if (a == NO && b == NO)
                return NO;
            return MAYBE;
        }

        public static Accept XOR(Accept a, Accept b) {
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

        public Accept and(Accept... values) {
            Accept result = this;
            for (int i = 0; result != NO && i < values.length; i++) {
                result = AND(result, values[i]);
            }
            return result;
        }

        public Accept and(Iterable<Accept> values) {
            Accept result = this;
            if (result != NO) {
                Iterator<Accept> iterator = values.iterator();
                while (result != NO && iterator.hasNext()) {
                    result = AND(result, iterator.next());
                }
            }
            return result;
        }

        public Accept not() {
            return NOT(this);
        }

        public Accept or(Accept... values) {
            Accept result = this;
            for (int i = 0; result != YES && i < values.length; i++) {
                result = OR(result, values[i]);
            }
            return result;
        }

        public Accept or(Iterable<Accept> values) {
            Accept result = this;
            if (result != YES) {
                Iterator<Accept> iterator = values.iterator();
                while (result != YES && iterator.hasNext()) {
                    result = OR(result, iterator.next());
                }
            }
            return result;
        }
    }

    Accept accept(Object node);
}