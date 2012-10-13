/**
 * 
 */
package org.ubimix.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.json.IJsonParser;
import org.ubimix.commons.parser.json.JsonParser;
import org.ubimix.commons.parser.json.JsonSerializer;
import org.ubimix.commons.parser.json.utils.JavaObjectBuilder;
import org.ubimix.commons.parser.json.utils.JavaObjectVisitor;

/**
 * @author kotelnikov
 */
public class ModelObject implements IHasValueMap {

    /**
     * Creates and returns {@link ModelObject} instance wrapping the specified
     * java value.
     */
    public static final IValueFactory<ModelObject> FACTORY = new IValueFactory<ModelObject>() {
        @Override
        public ModelObject newValue(Object object) {
            return new ModelObject(object);
        }
    };

    private static IJsonParser fJsonParser;

    @SuppressWarnings("unchecked")
    protected static <T> T cast(Object value) {
        return (T) value;
    }

    public static ModelObject from(IHasValueMap object) {
        if (object instanceof ModelObject) {
            return cast(object);
        }
        return new ModelObject(object);
    }

    public static IJsonParser getJsonParser() {
        if (fJsonParser == null) {
            fJsonParser = new JsonParser();
        }
        return fJsonParser;
    }

    public static ModelObject parse(String json) {
        return parse(json, ModelObject.FACTORY);
    }

    public static <T> T parse(String json, IValueFactory<T> factory) {
        Object innerObject = parseToInnerObject(json);
        return innerObject != null ? factory.newValue(innerObject) : null;
    }

    public static Object parseToInnerObject(String json) {
        JavaObjectBuilder builder = new JavaObjectBuilder();
        IJsonParser parser = getJsonParser();
        parser.parse(json, builder);
        return builder.getTop();
    }

    public static void setJsonParser(IJsonParser jsonParser) {
        fJsonParser = jsonParser;
    }

    public static String toJSON(Object obj) {
        return toJSON(obj, false, 2);
    }

    public static String toJSON(Object obj, boolean sort, int indent) {
        JsonSerializer serializer = new JsonSerializer(indent);
        JavaObjectVisitor visitor = new JavaObjectVisitor();
        visitor.visit(obj, sort, serializer);
        return serializer.toString();
    }

    protected Map<Object, Object> fMap;

    public ModelObject() {
        setInnerMap(null);
    }

    private ModelObject(IHasValueMap obj) {
        setInnerMap(obj);
    }

    private ModelObject(Object obj) {
        setInnerMap(obj);
    }

    /**
     * Creates an fills values from the internal array in the given collection.
     * 
     * @param <W>
     * @param name the name of the property
     * @param collection the result collection where all values from the
     *        original internal array should be added
     * @param factory the factory used to create JSON wrappers for each array
     *        value
     */
    private <W, C extends Collection<? super W>> C addValues(
        String name,
        C collection,
        IValueFactory<W> factory) {
        List<Object> list = getInnerList(name, false);
        if (list != null) {
            for (Object obj : list) {
                W w = factory.newValue(obj);
                collection.add(w);
            }
        }
        return collection;
    }

    protected <T extends ModelObject> T cast() {
        return cast(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IHasValueMap)) {
            return false;
        }
        IHasValueMap o = (IHasValueMap) obj;
        return getMap().equals(o.getMap());
    }

    /**
     * Returns the specified property as a boolean value.
     * 
     * @param name the name of the property
     * @param defaultValue the default value; used if the property is not
     *        defined
     * @return the specified property as a boolean value.
     */
    public boolean getBoolean(String name, boolean defaultValue) {
        boolean result = defaultValue;
        Object value = fMap.get(name);
        if (value instanceof Boolean) {
            result = (Boolean) cast(value);
        } else if (value != null) {
            String str = value + "";
            result = "1".equals(str) || "true".equals(str);
        }
        return result;
    }

    public double getDouble(String name, double defaultValue) {
        double result = defaultValue;
        Object value = fMap.get(name);
        if (value instanceof Double) {
            result = ((Double) cast(value)).doubleValue();
        } else if (value instanceof Integer) {
            result = ((Integer) cast(value)).doubleValue();
        } else if (value instanceof Long) {
            result = ((Long) cast(value)).doubleValue();
        } else if (value instanceof String) {
            try {
                result = Double.parseDouble((String) cast(value));
            } catch (Throwable t) {
            }
        }
        return result;
    }

    protected List<Object> getInnerList(String name, boolean create) {
        List<Object> result = null;
        Object value = fMap.get(name);
        if (value instanceof List<?>) {
            result = cast(value);
        } else if (create) {
            result = newList();
            fMap.put(name, result);
        }
        return result;
    }

    protected Map<Object, Object> getInnerMap(String name, boolean create) {
        Map<Object, Object> result = null;
        Object value = fMap.get(name);
        if (value instanceof Map<?, ?>) {
            result = cast(value);
        } else if (create) {
            result = newMap();
            fMap.put(name, result);
        }
        return result;
    }

    /**
     * Returns the specified property as a integer value.
     * 
     * @param name the name of the property
     * @param defaultValue the default value; used if the property is not
     *        defined
     * @return the specified property as a integer value.
     */
    public int getInteger(String name, int defaultValue) {
        int result = defaultValue;
        Object value = fMap.get(name);
        if (value instanceof Double) {
            result = ((Double) cast(value)).intValue();
        } else if (value instanceof Integer) {
            result = ((Integer) cast(value)).intValue();
        } else if (value instanceof Long) {
            result = ((Long) cast(value)).intValue();
        } else if (value instanceof String) {
            try {
                result = Integer.parseInt((String) cast(value));
            } catch (Throwable t) {
            }
        }
        return result;
    }

    /**
     * Returns a set of property names.
     * 
     * @return a set of property names
     */
    public Set<String> getKeys() {
        Set<String> result = new HashSet<String>();
        Set<Object> keys = fMap.keySet();
        for (Object key : keys) {
            if (key instanceof String) {
                result.add((String) key);
            }
        }
        return result;
    }

    public List<ModelObject> getList(String name) {
        return getList(name, FACTORY);
    }

    /**
     * Creates and returns an array of JSON wrappers corresponding to the
     * specified property name.
     * 
     * @param <W> the type of the wrapper
     * @param name the name of the property
     * @param factory the factory used to create new wrappers
     * @return an array of JsonObject instances
     */
    public <W> List<W> getList(String name, IValueFactory<W> factory) {
        return addValues(name, new ArrayList<W>(), factory);
    }

    /**
     * Returns the specified property as a long value.
     * 
     * @param name the name of the property
     * @param defaultValue the default value; used if the property is not
     *        defined
     * @return the specified property as a long value.
     */
    public long getLong(String name, long defaultValue) {
        long result = defaultValue;
        Object value = fMap.get(name);
        if (value instanceof Double) {
            result = ((Double) cast(value)).longValue();
        } else if (value instanceof Integer) {
            result = ((Integer) cast(value)).longValue();
        } else if (value instanceof Long) {
            result = ((Long) cast(value)).longValue();
        } else if (value instanceof String) {
            try {
                result = Long.parseLong((String) cast(value));
            } catch (Throwable t) {
            }
        }
        return result;
    }

    @Override
    public Map<Object, Object> getMap() {
        return fMap;
    }

    public ModelObject getObject(String name) {
        return getObject(name, false);
    }

    public ModelObject getObject(String name, boolean create) {
        Object value = fMap.get(name);
        Map<Object, Object> map = null;
        if (value instanceof Map<?, ?>) {
            map = cast(value);
        } else if (create) {
            map = new HashMap<Object, Object>();
            fMap.put(name, map);
        }
        ModelObject result = map != null ? new ModelObject(map) : null;
        return result;
    }

    /**
     * Returns the specified property as a wrapper of the a specific type.
     * 
     * @param <W> the type of the returned wrapper
     * @param name the name of the property
     * @param factory the factory used to create new wrappers
     * @return the specified property as a wrapper of the a specific type.
     */
    public <W> W getObject(String name, IValueFactory<W> factory) {
        Object value = fMap.get(name);
        W result = null;
        if (value != null) {
            result = factory.newValue(value);
        }
        return result;
    }

    /**
     * Creates and returns a set of JSON wrappers corresponding to the specified
     * property name.
     * 
     * @param <W> the type of the wrapper
     * @param name the name of the property
     * @param factory the factory used to create new wrappers
     * @return a set of JsonObject instances
     */
    public <W> Set<W> getSet(String name, IValueFactory<W> factory) {
        return addValues(name, new LinkedHashSet<W>(), factory);
    }

    /**
     * Returns the specified property as a string.
     * 
     * @param name the name of the property
     * @return the specified property as a string.
     */
    public String getString(String name) {
        String result = null;
        Object value = fMap.get(name);
        if (value instanceof String) {
            result = cast(value);
        } else if (value != null) {
            result = value.toString();
        }
        return result;
    }

    public String getType() {
        return getString("!");
    }

    /**
     * Returns the specified property as a wrapper of the a specific type.
     * 
     * @param <W> the type of the returned wrapper
     * @param name the name of the property
     * @param factory the factory used to create new wrappers
     * @return the specified property as a wrapper of the a specific type.
     */
    public <W> W getValue(String name, IValueFactory<W> factory) {
        Object value = fMap.get(name);
        return value != null ? factory.newValue(value) : null;
    }

    @Override
    public int hashCode() {
        return getMap().hashCode();
    }

    protected Object importValue(Object object) {
        if (object == null) {
            return null;
        }
        Object result = null;
        if ((object instanceof Boolean)
            || (object instanceof Integer)
            || (object instanceof Long)
            || (object instanceof Double)
            || (object instanceof String)) {
            result = object;
        } else if (object instanceof IHasValueMap) {
            result = ((IHasValueMap) object).getMap();
        } else {
            JavaObjectBuilder builder = new JavaObjectBuilder();
            JavaObjectVisitor visitor = new JavaObjectVisitor() {
                @Override
                protected Map<?, ?> getMap(Object value) {
                    Map<?, ?> map = null;
                    if (value instanceof IHasValueMap) {
                        map = ((IHasValueMap) value).getMap();
                    } else {
                        map = super.getMap(value);
                    }
                    return map;
                }
            };
            visitor.visit(object, builder);
            result = builder.getTop();
        }
        return result;
    }

    protected ArrayList<Object> newList() {
        return new ArrayList<Object>();
    }

    protected Map<Object, Object> newMap() {
        return new LinkedHashMap<Object, Object>();
    }

    /**
     * Removes the property with the specified name;
     * 
     * @param name the name of the property to remove
     */
    public ModelObject removeValue(String name) {
        fMap.remove(name);
        return this;
    }

    public <T extends ModelObject> T setInnerMap(Object obj) {
        Map<Object, Object> map = null;
        obj = importValue(obj);
        if (obj instanceof Map<?, ?>) {
            map = cast(obj);
        } else if (obj instanceof List<?>) {
            map = newMap();
            map.put("~", obj);
        } else if (obj != null) {
            String str = obj.toString();
            Object object = parseToInnerObject(str);
            if (object instanceof Map<?, ?>) {
                map = cast(object);
            }
        }
        if (map == null) {
            map = newMap();
        }
        fMap = map;
        return cast(this);
    }

    public ModelObject setType(String type) {
        return setValue("!", type);
    }

    /**
     * Replaces an existing value (if any) of the property with the givene name
     * by a new value.
     * 
     * @param name the property name
     * @param value the value of to set
     */
    public ModelObject setValue(String name, Object value) {
        value = importValue(value);
        fMap.put(name, value);
        return this;
    }

    /**
     * Replaces the property array by the given values
     * 
     * @param name the name of the property
     * @param values the values to set
     */
    public ModelObject setValues(String name, Iterable<?> values) {
        List<Object> list = newList();
        for (Object object : values) {
            object = importValue(object);
            list.add(object);
        }
        fMap.put(name, list);
        return this;
    }

    /**
     * Replaces the property array by the given values
     * 
     * @param <T> the type of values to set
     * @param name the name of the property
     * @param values the values to set
     */
    public <T> ModelObject setValues(String name, T... values) {
        List<Object> list = cast(Arrays.asList(values));
        setValues(name, list);
        return this;
    }

    @Override
    public String toString() {
        return toJSON(getMap());
    }

    public String toString(boolean sort, int indent) {
        return toJSON(getMap(), sort, indent);
    }

}
