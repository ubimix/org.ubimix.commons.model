/**
 * 
 */
package org.ubimix.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ubimix.commons.json.AbstractObjectVisitor;
import org.ubimix.commons.json.IJsonListener;
import org.ubimix.model.ValueFactory.IValueList;
import org.ubimix.model.ValueFactory.IValueMap;

/**
 * @author kotelnikov
 */
public class JsonVisitor extends AbstractObjectVisitor {

    /**
     * Transforms the given java object in a sequence of JSON listener calls.
     * 
     * @param value the java object to transform in JSON calls
     */
    @Override
    protected void visit(
        Object value,
        boolean sort,
        IJsonListener listener,
        Set<Object> stack,
        boolean acceptNull) {
        if (value == null && !acceptNull) {
            return;
        }
        if (stack.contains(value)) {
            return;
        }
        stack.add(value);
        try {
            ValueFactory f = ValueFactory.get();
            if (f.isArray(value)) {
                listener.beginArray();
                IValueList array = f.toArray(value);
                int len = array.getSize();
                for (int i = 0; i < len; i++) {
                    Object o = array.getValue(i);
                    listener.beginArrayElement();
                    visit(o, sort, listener, stack, true);
                    listener.endArrayElement();
                }
                listener.endArray();
            } else if (f.isBoolean(value)) {
                listener.onValue(f.toBoolean(value, false));
            } else if (f.isDouble(value)) {
                listener.onValue(f.toDouble(value, 0));
            } else if (f.isInteger(value)) {
                listener.onValue(f.toInteger(value, 0));
            } else if (f.isLong(value)) {
                listener.onValue(f.toLong(value, 0));
            } else if (f.isMap(value)) {
                IValueMap map = f.toMap(value);
                Collection<String> keys = map.getKeys();
                if (sort) {
                    List<String> list = new ArrayList<String>(keys);
                    Collections.sort(list);
                    keys = list;
                }
                listener.beginObject();
                for (String key : keys) {
                    listener.beginObjectProperty(key);
                    Object v = map.getValue(key);
                    visit(v, sort, listener, stack, true);
                    listener.endObjectProperty(key);
                }
                listener.endObject();
            } else if (f.isString(value)) {
                listener.onValue(f.toString(value));
            }
        } finally {
            stack.remove(value);
        }
    }

}
