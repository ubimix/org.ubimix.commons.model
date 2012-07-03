/**
 * 
 */
package org.ubimix.model;

import org.ubimix.model.ValueFactory.IValueList;
import org.ubimix.model.ValueFactory.IValueMap;
import org.webreformatter.commons.json.AbstractObjectBuilder;

/**
 * This class is used to convert JSON to Java objects and vice versa.
 * 
 * @author kotelnikov
 */
public class JsonBuilder extends AbstractObjectBuilder {

    private ValueFactory fValueFactory;

    public JsonBuilder() {
        this(ValueFactory.get());
    }

    public JsonBuilder(ValueFactory valueFactory) {
        fValueFactory = valueFactory;
    }

    @Override
    protected void addObjectValue(Object obj, String property, Object value) {
        ((IValueMap) obj).putValue(property, value);
    }

    @Override
    protected void addToArray(Object array, Object value) {
        IValueList list = (IValueList) array;
        int size = list.getSize();
        list.setValue(size, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JsonBuilder)) {
            return false;
        }
        JsonBuilder o = (JsonBuilder) obj;
        return fValueFactory.equals(o.fValueFactory) && super.equals(o);
    }

    @Override
    protected Object newArray() {
        return fValueFactory.newList();
    }

    @Override
    protected Object newObject() {
        return fValueFactory.newMap();
    }

    @Override
    protected String toString(Object top) {
        return top.toString();
    }

}
