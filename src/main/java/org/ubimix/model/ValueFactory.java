/**
 * 
 */
package org.ubimix.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.json.JsonObject;
import org.ubimix.commons.json.JsonObjectBuilder;
import org.ubimix.commons.json.JsonParser;
import org.ubimix.commons.json.JsonSerializer;

/**
 * @author kotelnikov
 */
public abstract class ValueFactory {

    public interface IHasValueMap {
        IValueMap getValueMap();
    }

    /**
     * Instances of this type are used to create wrappers of a specific type for
     * JSON objects.
     * 
     * @author kotelnikov
     * @param <W> the type of created wrappers
     */
    public interface IJsonValueFactory<W> {
        /**
         * Transforms the specified JSON value (string, boolean, number, JSON
         * array or JSON object) into the target object.
         * 
         * @return a newly created target for the specified JSON value
         * @param object the JSON value to wrap
         */
        W newValue(Object object);
    }

    public interface IValueList {

        void addValue(int pos, Object value);

        int getSize();

        Object getValue(int pos);

        void removeValue(int pos);

        void setValue(int pos, Object value);
    }

    public interface IValueMap {

        Set<String> getKeys();

        Object getValue(String name);

        void putValue(String name, Object value);

        void removeValue(String name);
    }

    /**
     * 
     */
    public static class SimpleValueFactory extends ValueFactory {

        public static class ValueList extends ArrayList<Object>
            implements
            IValueList {
            private static final long serialVersionUID = 5517666313440347482L;

            public void addValue(int pos, Object value) {
                add(pos, value);
            }

            public int getSize() {
                return size();
            }

            public Object getValue(int pos) {
                return get(pos);
            }

            public void removeValue(int pos) {
                remove(pos);
            }

            public void setValue(int pos, Object v) {
                if (pos == size()) {
                    add(v);
                } else {
                    set(pos, v);
                }
            }

        }

        public static class ValueMap extends LinkedHashMap<String, Object>
            implements
            IValueMap {
            private static final long serialVersionUID = -2415154452289492331L;

            public Set<String> getKeys() {
                return super.keySet();
            }

            public Object getValue(String name) {
                return super.get(name);
            }

            public void putValue(String name, Object value) {
                put(name, value);
            }

            public void removeValue(String name) {
                remove(name);
            }

        }

        @Override
        protected Object doParseJson(String str) {
            try {
                JsonParser parser = new JsonParser();
                JsonObjectBuilder util = new JsonObjectBuilder();
                parser.parse(str, util);
                Object obj = util.getTop();
                return toModelValue(obj);
            } catch (Throwable e) {
                throw handleError("Can not parse the given string. String: "
                    + str, e);
            }
        }

        @Override
        protected String doSerializeJson(Object obj) {
            final StringBuilder buf = new StringBuilder();
            JsonSerializer serializer = new JsonSerializer(2) {
                @Override
                protected void print(String string) {
                    buf.append(string);
                }
            };
            new JsonVisitor().visit(obj, serializer);
            return buf.toString();
        }

        private RuntimeException handleError(String msg, Throwable t) {
            if (t instanceof RuntimeException) {
                return (RuntimeException) t;
            }
            return new RuntimeException(t);
        }

        @Override
        public IValueList newList() {
            return new ValueList();
        }

        @Override
        public IValueMap newMap() {
            return new ValueMap();
        }

    }

    private static ValueFactory fValueFactory = new SimpleValueFactory();

    /**
     * This "factory" transforms the given value in a JSON internal value and
     * returns it.
     */
    public static IJsonValueFactory<Object> NULL_FACTORY = new IJsonValueFactory<Object>() {
        public Object newValue(Object object) {
            return ValueFactory.get().toModelValue(object);
        }
    };

    /**
     * Transforms values from an internal array into objects using the specified
     * factory and puts them in the collection.
     * 
     * @param <W>
     * @param <C>
     * @param array the internal array object
     * @param collection the collection to fill with values
     * @param factory the factory of values
     * @return the collection
     */
    public static <W, C extends Collection<? super W>> C addValues(
        IValueList array,
        C collection,
        IJsonValueFactory<W> factory) {
        if (array != null) {
            int len = array.getSize();
            for (int i = 0; i < len; i++) {
                Object value = array.getValue(i);
                W wrapper = factory.newValue(value);
                collection.add(wrapper);
            }
        }
        return collection;
    }

    public static ValueFactory get() {
        return fValueFactory;
    }

    protected static void set(ValueFactory valueFactory) {
        fValueFactory = valueFactory;
    }

    /**
     * Adds values from the given array to the specified {@link JsonObject}. All
     * impair objects are used as keys and pair objects are used as the
     * corresponding values.
     * 
     * @param values key/value pairs; impair objects are keys, pair objects are
     *        values
     */
    public void addValues(IValueMap obj, Object... values) {
        for (int i = 0; i < values.length;) {
            Object str = values[i++];
            String key = toString(str);
            Object value = i < values.length ? values[i++] : null;
            value = toModelValue(value);
            obj.putValue(key, value);
        }
    }

    protected abstract Object doParseJson(String str);

    protected abstract String doSerializeJson(Object obj);

    public boolean isArray(Object value) {
        return value instanceof IValueList;
    }

    public boolean isBoolean(Object value) {
        return value instanceof Boolean;
    }

    public boolean isDouble(Object value) {
        return value instanceof Double;
    }

    public boolean isInteger(Object value) {
        return value instanceof Integer;
    }

    public boolean isLong(Object value) {
        return value instanceof Long;
    }

    public boolean isMap(Object value) {
        return value instanceof IValueMap;
    }

    public boolean isString(Object value) {
        return value instanceof String;
    }

    public abstract IValueList newList();

    public abstract IValueMap newMap();

    public Object parseJson(String str) {
        Object result = doParseJson(str);
        return toModelValue(result);
    }

    public String serializeJson(Object obj) {
        obj = toModelValue(obj);
        return doSerializeJson(obj);
    }

    public IValueList toArray(Object value) {
        return (IValueList) (value instanceof IValueList ? value : null);
    }

    public boolean toBoolean(Object value, boolean defaultValue) {
        boolean result = defaultValue;
        if (value != null) {
            if (isBoolean(value)) {
                result = (Boolean) value;
            } else {
                String str = toString(value).toLowerCase();
                result = "true".equals(str)
                    || "1".equals(str)
                    || "ok".equals(str);
            }
        }
        return result;
    }

    public double toDouble(Object value, double defaultValue) {
        double result = defaultValue;
        if (value != null) {
            if (isDouble(value)) {
                result = (Double) value;
            } else if (isLong(value)) {
                result = (Long) value;
            } else if (isInteger(value)) {
                result = (Integer) value;
            } else {
                String str = toString(value);
                try {
                    result = Double.parseDouble(str);
                } catch (Throwable t) {
                }
            }
        }
        return result;
    }

    public int toInteger(Object value, int defaultValue) {
        int result = defaultValue;
        if (value != null) {
            if (isInteger(value)) {
                result = (Integer) value;
            }
            if (isLong(value)) {
                long l = (Long) value;
                result = (int) l;
            } else if (isDouble(value)) {
                double d = (Double) value;
                result = (int) d;
            } else {
                String str = toString(value);
                try {
                    result = Integer.parseInt(str);
                } catch (Throwable t) {
                }
            }
        }
        return result;
    }

    public long toLong(Object value, long defaultValue) {
        long result = defaultValue;
        if (value != null) {
            ValueFactory f = ValueFactory.get();
            if (f.isLong(value)) {
                result = (Long) value;
            } else if (f.isInteger(value)) {
                result = (Integer) value;
            } else if (f.isDouble(value)) {
                double d = (Double) value;
                result = (int) d;
            } else {
                String str = f.toString(value);
                try {
                    result = Long.parseLong(str);
                } catch (Throwable t) {
                }
            }
        }
        return result;
    }

    public IValueMap toMap(Object value) {
        return (IValueMap) (value instanceof IValueMap ? value : null);
    }

    /**
     * Transforms the given java object into a JSON object.
     * 
     * @param value the java object to transform into a JSON instance
     * @return a newly created JSON object
     */
    public Object toModelValue(Object value) {
        return toModelValue(value, null);
    }

    /**
     * Transforms the given java object into a JSON object.
     * 
     * @param value the java object to transform into a JSON instance
     * @return a newly created JSON object
     */
    private Object toModelValue(Object value, Set<Object> stack) {
        if (value == null) {
            return null;
        }
        Set<Object> s = stack;
        if (s != null) {
            if (s.contains(value)) {
                return null;
            }
            s.add(value);
        }
        try {
            Object val = value;
            if (value instanceof IHasValueMap) {
                val = ((IHasValueMap) value).getValueMap();
            } else if ((value instanceof Integer)
                || (value instanceof Boolean)
                || (value instanceof Long)
                || (value instanceof Short)
                || (value instanceof Double)
                || (value instanceof Float)
                || (value instanceof String)
                || (value instanceof IValueMap)
                || (value instanceof IValueList)) {
                // do nothing
            } else if (value instanceof Map<?, ?>) {
                IValueMap obj = newMap();
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    String name = entry.getKey() + "";
                    if (stack == null) {
                        stack = new HashSet<Object>();
                    }
                    Object v = toModelValue(entry.getValue(), stack);
                    obj.putValue(name, v);
                }
                val = obj;
            } else if (value instanceof Iterable<?>) {
                IValueList array = newList();
                int pos = 0;
                for (Object o : (Iterable<?>) value) {
                    if (stack == null) {
                        stack = new HashSet<Object>();
                    }
                    Object v = toModelValue(o, stack);
                    array.setValue(pos, v);
                    pos++;
                }
                val = array;
            } else if (value.getClass().isArray()) {
                IValueList array = newList();
                int pos = 0;
                for (Object o : (Object[]) value) {
                    if (stack == null) {
                        stack = new HashSet<Object>();
                    }
                    Object v = toModelValue(o, stack);
                    array.setValue(pos, v);
                    pos++;
                }
                val = array;
            } else {
                val = value.toString();
            }
            return val;
        } finally {
            if (s != null) {
                s.remove(value);
            }
        }
    }

    public String toString(Object value) {
        return value != null ? value.toString() : null;
    }

}
