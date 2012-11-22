package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class StructuredTable extends StructuredNode.StructuredNodeContainer {

    private static Set<String> CELL_NAMES = new HashSet<String>();

    public static final IValueFactory<StructuredTable> FACTORY = newStructuredTableFactory(Value.FACTORY);

    static {
        CELL_NAMES.add(HtmlTagDictionary.TD);
        CELL_NAMES.add(HtmlTagDictionary.TH);
    }

    public static <T extends Value> IValueFactory<StructuredTable> newStructuredTableFactory(
        final IValueFactory<? extends Value> valueFactory) {
        return new IValueFactory<StructuredTable>() {
            @Override
            public StructuredTable newValue(Object object) {
                return new StructuredTable((XmlElement) object, valueFactory);
            }
        };
    }

    public static StructuredTable search(Iterable<XmlNode> list) {
        return search(list, Value.FACTORY);
    }

    public static <T extends Value> StructuredTable search(
        Iterable<XmlNode> list,
        final IValueFactory<? extends Value> valueFactory) {
        StructuredTable table = wrapFirstElement(
            list,
            newStructuredTableFactory(valueFactory),
            HtmlTagDictionary.TABLE);
        if (table != null) {
            table.setValueFactory(valueFactory);
        }
        return table;
    }

    public static <T extends Value> StructuredTable searchTableRecursively(
        Iterable<XmlNode> content,
        IValueFactory<? extends Value> valueFactory,
        String... headers) {
        StructuredTable result = null;
        for (XmlNode node : content) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                String name = e.getName();
                if (HtmlTagDictionary.isTableElement(name)) {
                    StructuredTable table = new StructuredTable(e, valueFactory);
                    if (table.checkColumnNames(headers)) {
                        result = table;
                    }
                } else {
                    result = searchTableRecursively(e, valueFactory, headers);
                }
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    private List<List<XmlElement>> fCells;

    private Map<Integer, String> fColumnIndexToName;

    private List<String> fColumnNames;

    private Map<String, Integer> fColumnNameToIndex;

    private int fTableWidth = -1;

    public StructuredTable(
        XmlElement element,
        IValueFactory<? extends Value> factory) {
        super(element, factory);
    }

    private void checkColumnNames() {
        if (fColumnNames == null) {
            fColumnNames = new ArrayList<String>();
            fColumnNameToIndex = new HashMap<String, Integer>();
            fColumnIndexToName = new HashMap<Integer, String>();
            List<List<XmlElement>> cells = getCells();
            if (!cells.isEmpty()) {
                List<XmlElement> firstLine = cells.get(0);
                for (int i = 0; i < firstLine.size(); i++) {
                    XmlElement e = firstLine.get(i);
                    String name = e.getName();
                    if (HtmlTagDictionary.TH.equals(name)) {
                        String str = cleanColumnName(newValue(e).getAsText());
                        fColumnNames.add(str);
                        str = cleanColumnName(str);
                        fColumnNameToIndex.put(str, i);
                        fColumnIndexToName.put(i, str);
                    }
                }
            }
        }
    }

    /**
     * This method checks if this table has the specified column names. If a
     * name is not specified in a position then it is considered that any header
     * name is acceptable.
     * 
     * @param columnNames name of columns
     * @return <code>true</code> if this table has the specified column names
     */
    public boolean checkColumnNames(String... columnNames) {
        boolean result = true;
        List<String> list = getColumnNames();
        for (int i = 0; result && i < columnNames.length; i++) {
            String name = columnNames[i];
            if (name != null) {
                name = cleanColumnName(name);
                String realName = list.get(i);
                if (!name.equals(realName)) {
                    result = false;
                }
            }
        }
        return result;
    }

    protected String cleanColumnName(String columnName) {
        return columnName.toLowerCase().trim();
    }

    public <T extends Value> T getCell(int row, int col) {
        XmlElement e = getCellElement(row, col);
        return newValue(e);
    }

    public <T extends Value> T getCell(
        int keyColumn,
        String key,
        int valueColumn) {
        int row = getRowIndex(keyColumn, key);
        return getCell(row, valueColumn);
    }

    public <T extends Value> T getCell(String columnName, int row) {
        int col = getColumnIndex(columnName);
        return getCell(row, col);
    }

    public <T extends Value> T getCell(
        String keyColumnName,
        String key,
        int valueColumn) {
        int keyColumn = getColumnIndex(keyColumnName);
        return getCell(keyColumn, key, valueColumn);
    }

    public <T extends Value> T getCell(
        String keyColumnName,
        String key,
        String valueColumnName) {
        int keyColumn = getColumnIndex(keyColumnName);
        int valueColumn = getColumnIndex(valueColumnName);
        T value = getCell(keyColumn, key, valueColumn);
        return value;
    }

    public XmlElement getCellElement(int row, int col) {
        if (row < 0 || col < 0) {
            return null;
        }
        XmlElement result = null;
        List<XmlElement> line = getRowElements(row);
        if (line != null) {
            result = col < line.size() ? line.get(col) : null;
        }
        return result;
    }

    private List<List<XmlElement>> getCells() {
        if (fCells == null) {
            fCells = new ArrayList<List<XmlElement>>();
            List<XmlElement> rows = fElement
                .getChildrenByName(HtmlTagDictionary.TR);
            if (rows.isEmpty()) {
                XmlElement tbody = fElement
                    .getChildByName(HtmlTagDictionary.TBODY);
                if (tbody != null) {
                    rows = tbody.getChildrenByName(HtmlTagDictionary.TR);
                }
            }
            for (XmlElement row : rows) {
                List<XmlElement> elements = row.getChildrenByNames(CELL_NAMES);
                fCells.add(elements);
            }
        }
        return fCells;
    }

    public <T extends Value> List<T> getColumn(int columnIndex) {
        List<T> result = new ArrayList<T>();
        int width = getWidth();
        if (columnIndex >= 0 && columnIndex < width) {
            int height = getHeight();
            for (int i = 0; i < height; i++) {
                T cell = getCell(i, columnIndex);
                result.add(cell);
            }
        }
        return result;
    }

    public <T extends Value> List<T> getColumn(String columnName) {
        int columnIndex = getColumnIndex(columnName);
        return getColumn(columnIndex);
    }

    public List<XmlElement> getColumnElements(int column) {
        if (column < 0) {
            return null;
        }
        int width = getWidth();
        if (column >= width) {
            return null;
        }
        List<XmlElement> result = new ArrayList<XmlElement>();
        List<List<XmlElement>> cells = getCells();
        for (List<XmlElement> row : cells) {
            XmlElement cell = column < row.size() ? row.get(column) : null;
            result.add(cell);
        }
        return result;
    }

    public int getColumnIndex(String columnName) {
        checkColumnNames();
        columnName = cleanColumnName(columnName);
        Integer value = fColumnNameToIndex.get(columnName);
        return value != null ? value.intValue() : -1;
    }

    public List<String> getColumnNames() {
        checkColumnNames();
        return fColumnNames;
    }

    public int getHeight() {
        List<List<XmlElement>> cells = getCells();
        return cells.size();
    }

    public <T extends Value> List<T> getRow(int row) {
        List<T> result = new ArrayList<T>();
        List<XmlElement> elements = getRowElements(row);
        if (elements != null) {
            for (XmlElement e : elements) {
                T value = newValue(e);
                result.add(value);
            }
        }
        return result;
    }

    /**
     * @param columnName the name of the column
     * @param columnValue the value of the cell in the specified column
     * @return a row values which contains the specified value of the column
     *         with the given name
     */
    public <T extends Value> List<T> getRow(
        String columnName,
        String columnValue) {
        int column = getColumnIndex(columnName);
        int row = getRowIndex(column, columnValue);
        return getRow(row);
    }

    /**
     * @param row the row number
     * @return a list of all elements of the specified row
     */
    public List<XmlElement> getRowElements(int row) {
        if (row < 0) {
            return null;
        }
        List<List<XmlElement>> cells = getCells();
        return row >= cells.size() ? null : cells.get(row);
    }

    /**
     * @param column the column containing the specified value
     * @param columnValue the value to seek
     * @return the index of the row containing the specified value in the column
     */
    public int getRowIndex(int column, String columnValue) {
        int result = -1;
        if (column >= 0) {
            List<List<XmlElement>> cells = getCells();
            for (int row = 0; row < cells.size(); row++) {
                List<XmlElement> line = cells.get(row);
                XmlElement e = column < line.size() ? line.get(column) : null;
                Value value = newValue(e);
                String textValue = value.getAsText();
                if (columnValue.equals(textValue)) {
                    result = row;
                    break;
                }
            }
        }
        return result;
    }

    public int getWidth() {
        if (fTableWidth < 0) {
            List<List<XmlElement>> cells = getCells();
            fTableWidth = 0;
            for (List<XmlElement> row : cells) {
                int count = row.size();
                if (count > fTableWidth) {
                    fTableWidth = count;
                }
            }
        }
        return fTableWidth;
    }

}