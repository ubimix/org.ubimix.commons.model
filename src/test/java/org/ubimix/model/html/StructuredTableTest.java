/**
 * 
 */
package org.ubimix.model.html;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.XmlFactory;

/**
 * @author kotelnikov
 */
public class StructuredTableTest extends TestCase {

    private IXmlFactory fFactory = newXmlFactory();

    /**
     * @param name
     */
    public StructuredTableTest(String name) {
        super(name);
    }

    private StructuredPropertiesTable getProperties(String html) {
        IXmlElement e = HtmlDocument.parseFragment(html);
        return new StructuredPropertiesTable(e, Value.FACTORY);
    }

    private StructuredTable getTable(String html) {
        IXmlElement e = HtmlDocument.parseFragment(html);
        return new StructuredTable(e, Value.FACTORY);
    }

    protected IXmlFactory newXmlFactory() {
        return XmlFactory.getInstance();
    }

    private IXmlElement tdRow(String... values) {
        return toLine(HtmlTagDictionary.TD, values);
    }

    public void testProperties() throws Exception {
        StructuredPropertiesTable table = getProperties(""
            + "<table>"
            + "<tr><th>  Property  <th>  Value  "
            + "<tr><td>firstName<td>John"
            + "<tr><td>lastName<td>Smith");
        testTableColumns(table, "Property", "Value");
        testTableColumnValues(table, "Property", "firstName", "lastName");
        testTableColumnValues(table, "Value", "John", "Smith");
        assertEquals("John", table.getProperty("firstName").getAsText());
        assertEquals("Smith", table.getProperty("lastName").getAsText());
    }

    public void testTable() throws Exception {
        StructuredTable table = getTable(""
            + "<table>"
            + "<tr><th>  Property  <th>  Value  "
            + "<tr><td>firstName<td>John"
            + "<tr><td>lastName<td>Smith");
        testTableColumns(table, "Property", "Value");
        testTableColumnValues(table, "Property", "firstName", "lastName");
        testTableColumnValues(table, "Value", "John", "Smith");
        assertEquals("John", table
            .getCell("Property", "firstName", "Value")
            .getAsText());
        assertEquals("Smith", table
            .getCell("Property", "lastName", "Value")
            .getAsText());

        assertEquals("firstName", table
            .getCell("Property", "firstName", 0)
            .getAsText());
        assertEquals("John", table
            .getCell("Property", "firstName", 1)
            .getAsText());
        assertEquals("lastName", table
            .getCell("Property", "lastName", 0)
            .getAsText());
        assertEquals("Smith", table
            .getCell("Property", "lastName", 1)
            .getAsText());

    }

    private void testTableColumns(StructuredTable table, String... columnNames) {
        List<String> list = table.getColumnNames();
        assertNotNull(list);
        assertEquals(columnNames.length, list.size());
        int index = 0;
        for (String columnName : columnNames) {
            int testIndex = table.getColumnIndex(columnName);
            assertEquals(index, testIndex);
            testIndex = table.getColumnIndex(columnName.toLowerCase());
            assertEquals(index, testIndex);
            testIndex = table.getColumnIndex(columnName.toUpperCase());
            assertEquals(index, testIndex);
            index++;
        }
    }

    private void testTableColumnValues(
        StructuredTable table,
        String columnName,
        String... controls) {
        int index = table.getColumnIndex(columnName);
        List<Value> first = table.getColumn(columnName);
        List<Value> second = table.getColumn(index);
        assertEquals(first, second);
        assertEquals(controls.length + 1, first.size());
        int i = 1;
        assertEquals(columnName, first.get(0).getAsText());
        for (String control : controls) {
            Value value = first.get(i++);
            assertNotNull(value);
            assertEquals(control, value.getAsText());
        }

        i = 1;
        assertEquals(columnName, table.getCell(columnName, 0).getAsText());
        for (String control : controls) {
            Value value = table.getCell(columnName, i++);
            assertNotNull(value);
            assertEquals(control, value.getAsText());
        }
    }

    public void testTableDimentions() throws Exception {
        testTableDimentions(0);
        testTableDimentions(1, tdRow("a"));
        testTableDimentions(1, thRow("a"));
        testTableDimentions(2, tdRow("a", "A"));
        testTableDimentions(2, thRow("a", "A"));
        testTableDimentions(2, tdRow("a", "A"), tdRow("b", "B"));
        testTableDimentions(
            3,
            thRow("a"),
            tdRow("a", "b", "c"),
            tdRow("a", "b"));
    }

    private void testTableDimentions(int cols, IXmlElement... rows) {
        IXmlElement table = toTable(rows);
        StructuredTable sTable = new StructuredTable(table, Value.FACTORY);
        assertEquals(cols, sTable.getWidth());
        assertEquals(rows.length, sTable.getHeight());
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows.length; j++) {
                IXmlElement row = rows[j];
                IXmlNode col = row.getChild(i);
                Value testCell = sTable.getCell(j, i);
                assertNotNull(testCell);
                assertEquals(col + "", testCell + "");
                assertEquals(col, testCell.getElement());
            }
        }
    }

    private IXmlElement thRow(String... values) {
        return toLine("th", values);
    }

    private IXmlElement toLine(String cellName, String... values) {
        IXmlElement row = fFactory.newElement("tr");
        for (String value : values) {
            IXmlElement cell = fFactory.newElement(cellName);
            row.addChild(cell);
            IXmlNode node = toNode(value);
            cell.addChild(node);
        }
        return row;
    }

    private IXmlNode toNode(String value) {
        IXmlNode result = null;
        if (value == null) {
            value = "";
        }
        if (value.startsWith("<")) {
            result = HtmlDocument.parseFragment(value);
        } else {
            result = fFactory.newText(value);
        }
        return result;
    }

    private IXmlElement toTable(IXmlElement... rows) {
        IXmlElement table = fFactory.newElement("table");
        table.addChildren(Arrays.<IXmlNode> asList(rows));
        return table;
    }

}
