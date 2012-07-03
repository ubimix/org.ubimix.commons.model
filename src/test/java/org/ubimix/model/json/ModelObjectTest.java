/**
 * 
 */
package org.ubimix.model.json;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.ValueFactory.IJsonValueFactory;

/**
 * @author kotelnikov
 */
// getArray(String, boolean)
// getDouble(String, double)
// getInteger(String, int)
// getKeys()
// getList(String, IJsonValueFactory<W>)
// getLong(String, long)
// getSet(String, IJsonValueFactory<W>)
// getString(String)
// getObject(String, IJsonValueFactory<W>)
// removeValue(String)
// setValue(String, Object)
// setValues(String, Iterable<?>)
// setValues(String, T...)
public class ModelObjectTest extends TestCase {

    public static class MyList extends ModelObject {
        public List<MyValue> getList() {
            return getList("list", MyValue.FACTORY);
        }

        public String getTitle() {
            return getString("title");
        }

        public MyList setList(List<MyValue> values) {
            setValue("list", values);
            return this;
        }

        public MyList setTitle(String title) {
            setValue("title", title);
            return this;
        }
    }

    public static class MyValue extends ModelObject {
        public static IJsonValueFactory<MyValue> FACTORY = new IJsonValueFactory<ModelObjectTest.MyValue>() {
            @Override
            public MyValue newValue(Object object) {
                return new MyValue().setInternalMap(object);
            }
        };

        protected MyValue() {
        }

        public MyValue(int id) {
            setValue("id", id);
        }

        public int getId() {
            return getInteger("id", -1);
        }
    }

    /**
     * @param name
     */
    public ModelObjectTest(String name) {
        super(name);
    }

    public void testBoolean() {
        ModelObject o = new ModelObject();
        o.setValue("a", true);
        assertEquals(true, o.getBoolean("a", false));
        o.setValue("a", false);
        assertEquals(false, o.getBoolean("a", true));
    }

    public void testDouble() {
        ModelObject o = new ModelObject();
        Double value = 0.3;
        o.setValue("a", value);
        assertEquals(value, o.getDouble("a", -123));
        value = -0.3;
        o.setValue("a", value);
        assertEquals(value, o.getDouble("a", 123));
    }

    public void testInteger() {
        ModelObject o = new ModelObject();
        Integer value = 345;
        o.setValue("a", value);
        assertEquals((int) value, o.getInteger("a", -123));
        value = -345;
        o.setValue("a", value);
        assertEquals((int) value, o.getInteger("a", 123));
    }

    public void testList() throws Exception {
        List<MyValue> list = new ArrayList<ModelObjectTest.MyValue>();
        int i = 0;
        list.add(new MyValue(i++));
        list.add(new MyValue(i++));
        list.add(new MyValue(i++));
        list.add(new MyValue(i++));
        list.add(new MyValue(i++));
        MyList obj = new MyList().setTitle("This is a title").setList(list);
        assertEquals("This is a title", obj.getTitle());
        assertEquals(list, obj.getList());
    }

    public void testLong() {
        ModelObject o = new ModelObject();
        Long value = ((long) Integer.MAX_VALUE) + 10000;
        o.setValue("a", value);
        assertEquals((long) value, o.getLong("a", -123));
        value = ((long) Integer.MIN_VALUE) - 10000;
        o.setValue("a", value);
        assertEquals((long) value, o.getLong("a", 123));
    }

    public void testParseSerialize() {
        ModelObject o = new ModelObject("{"
            + "id: '98979879', "
            + "firstName: 'John', "
            + "lastName: 'Smith', "
            + "address: "
            + "{ "
            + "city: Paris, "
            + "street: 'Rue Rivoli', "
            + "building: 123 "
            + "} "
            + "}");
        assertEquals("98979879", o.getString("id"));
        assertEquals(98979879, o.getInteger("id", -1));
        assertEquals("John", o.getString("firstName"));
        assertEquals("Smith", o.getString("lastName"));
        ModelObject address = o.getObject("address");
        assertNotNull(address);
        assertEquals("Paris", address.getString("city"));
        assertEquals("Rue Rivoli", address.getString("street"));
        assertEquals("123", address.getString("building"));
        assertEquals(123, address.getInteger("building", -1));

        String str = o.toString();
        assertEquals("{\n"
            + "  \"id\":\"98979879\",\n"
            + "  \"firstName\":\"John\",\n"
            + "  \"lastName\":\"Smith\",\n"
            + "  \"address\":{\n"
            + "    \"city\":\"Paris\",\n"
            + "    \"street\":\"Rue Rivoli\",\n"
            + "    \"building\":123\n"
            + "  }\n"
            + "}", str);
        ModelObject test = new ModelObject(str);
        assertEquals(o, test);
    }

}
