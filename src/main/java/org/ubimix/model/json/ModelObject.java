/**
 * 
 */
package org.ubimix.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.ubimix.model.ValueFactory;
import org.ubimix.model.ValueFactory.IHasValueMap;
import org.ubimix.model.ValueFactory.IJsonValueFactory;
import org.ubimix.model.ValueFactory.IValueList;
import org.ubimix.model.ValueFactory.IValueMap;

/**
 * @author kotelnikov
 */
public class ModelObject implements IHasValueMap {

    /**
     * Creates and returns {@link ModelObject} instance wrapping the specified
     * java value.
     */
    public static final IJsonValueFactory<ModelObject> FACTORY = new IJsonValueFactory<ModelObject>() {
        public ModelObject newValue(Object object) {
            return new ModelObject(object);
        }
    };

    private IValueMap fMap;

    public ModelObject() {
        this(ValueFactory.get().newMap());
    }

    public ModelObject(IHasValueMap object) {
        this(object.getValueMap());
    }

    public ModelObject(IValueMap map) {
        fMap = map;
    }

    public ModelObject(Object json) {
        setInternalMap(json);
    }

    public ModelObject(String json) {
        setInternalMap(json);
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
        IJsonValueFactory<W> factory) {
        IValueList array = getArrayObject(name, false);
        return ValueFactory.addValues(array, collection, factory);
    }

    @SuppressWarnings("unchecked")
    protected <T extends ModelObject> T cast() {
        return (T) this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ModelObject)) {
            return false;
        }
        ModelObject o = (ModelObject) obj;
        return fMap.equals(o.fMap);
    }

    /**
     * Returns an array corresponding to the specified property name.
     * 
     * @param name the name of the property
     * @param create if this flag is <code>true</code> and the specified
     *        property does not contain an array then a new one will be created
     *        and initialized
     * @return the array corresponding to the specified property name
     */
    private IValueList getArrayObject(String name, boolean create) {
        IValueList result = null;
        Object value = fMap.getValue(name);
        ValueFactory f = ValueFactory.get();
        if (f.isArray(value)) {
            result = (IValueList) value;
        } else if (create) {
            result = f.newList();
            fMap.putValue(name, result);
        }
        return result;
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
        Object value = fMap.getValue(name);
        return ValueFactory.get().toBoolean(value, defaultValue);
    }

    /**
     * Returns the specified property as a float value.
     * 
     * @param name the name of the property
     * @param defaultValue the default value; used if the property is not
     *        defined
     * @return the specified property as a float value.
     */
    public double getDouble(String name, double defaultValue) {
        Object value = fMap.getValue(name);
        return ValueFactory.get().toDouble(value, defaultValue);
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
        Object value = fMap.getValue(name);
        return ValueFactory.get().toInteger(value, defaultValue);
    }

    /**
     * Returns a set of property names.
     * 
     * @return a set of property names
     */
    public Set<String> getKeys() {
        Set<String> result = fMap.getKeys();
        return result;
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
    public <W> ArrayList<W> getList(String name, IJsonValueFactory<W> factory) {
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
        Object value = fMap.getValue(name);
        return ValueFactory.get().toLong(value, defaultValue);
    }

    public ModelObject getObject(String name) {
        Object value = fMap.getValue(name);
        ValueFactory f = ValueFactory.get();
        ModelObject result = null;
        if (f.isMap(value)) {
            result = new ModelObject(f.toMap(value));
        }
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
    public <W> W getObject(String name, IJsonValueFactory<W> factory) {
        return getValue(name, factory);
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
    public <W> Set<W> getSet(String name, IJsonValueFactory<W> factory) {
        return addValues(name, new LinkedHashSet<W>(), factory);
    }

    /**
     * Returns the specified property as a string.
     * 
     * @param name the name of the property
     * @return the specified property as a string.
     */
    public String getString(String name) {
        Object value = fMap.getValue(name);
        return ValueFactory.get().toString(value);
    }

    /**
     * Returns a JSON value corresponding to the specified key.
     * 
     * @param name the key of the property
     * @return a JSON value corresponding to the specified key
     */
    public Object getValue(String name) {
        return getValue(name, ValueFactory.NULL_FACTORY);
    }

    /**
     * Returns the specified property as a wrapper of the a specific type.
     * 
     * @param <W> the type of the returned wrapper
     * @param name the name of the property
     * @param factory the factory used to create new wrappers
     * @return the specified property as a wrapper of the a specific type.
     */
    public <W> W getValue(String name, IJsonValueFactory<W> factory) {
        Object value = fMap.getValue(name);
        return value != null ? factory.newValue(value) : null;
    }

    public IValueMap getValueMap() {
        return fMap;
    }

    @Override
    public int hashCode() {
        return fMap.hashCode();
    }

    /**
     * Removes the property with the specified name;
     * 
     * @param name the name of the property to remove
     */
    public ModelObject removeValue(String name) {
        fMap.removeValue(name);
        return this;
    }

    /**
     * Replaces the old JSON internal object by the given one.
     * 
     * @param json the JSON object to set.
     */
    public <T extends ModelObject> T setInternalMap(Object json) {
        ValueFactory f = ValueFactory.get();
        IValueMap map = null;
        if (json instanceof ModelObject) {
            map = ((ModelObject) json).fMap;
        } else if (json instanceof String) {
            String str = (String) json;
            str = str.trim();
            if (!"".equals(str)) {
                Object result = f.parseJson(str);
                map = f.toMap(result);
            }
        } else {
            Object result = f.toModelValue(json);
            map = f.toMap(result);
        }
        if (map == null) {
            map = f.newMap();
        }
        fMap = map;
        return cast();
    }

    /**
     * Replaces an existing value (if any) of the property with the givene name
     * by a new value.
     * 
     * @param name the property name
     * @param value the value of to set
     */
    public ModelObject setValue(String name, Object value) {
        Object val = ValueFactory.get().toModelValue(value);
        fMap.putValue(name, val);
        return this;
    }

    /**
     * Replaces the property array by the given values
     * 
     * @param name the name of the property
     * @param values the values to set
     */
    public ModelObject setValues(String name, Iterable<?> values) {
        removeValue(name);
        IValueList array = getArrayObject(name, true);
        ValueFactory f = ValueFactory.get();
        int i = 0;
        for (Object value : values) {
            Object val = f.toModelValue(value);
            array.setValue(i, val);
            i++;
        }
        return this;
    }

    /**
     * Replaces the property array by the given values
     * 
     * @param <T> the type of values to set
     * @param name the name of the property
     * @param values the values to set
     */
    public <T> void setValues(String name, T... values) {
        removeValue(name);
        ValueFactory f = ValueFactory.get();
        IValueList array = getArrayObject(name, true);
        int i = 0;
        for (Object value : values) {
            Object val = f.toModelValue(value);
            array.setValue(i, val);
            i++;
        }
    }

    @Override
    public String toString() {
        return ValueFactory.get().serializeJson(fMap);
    }
}
