package org.ubimix.model.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ubimix.commons.parser.html.HtmlTagDictionary;
import org.ubimix.model.xml.XmlElement;

public class StructuredTable extends StructuredNode {

    private static Set<String> CELL_NAMES = new HashSet<String>();

    static {
        CELL_NAMES.add(HtmlTagDictionary.TD);
        CELL_NAMES.add(HtmlTagDictionary.TH);
    }

    private List<List<XmlElement>> fCells;

    private Map<Integer, String> fColumnIndexToName;

    private List<String> fColumnNames;

    private Map<String, Integer> fColumnNameToIndex;

    private int fTableWidth = -1;

    public StructuredTable(XmlElement element) {
        super(element);
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
                        String str = newValue(e).getAsText().trim();
                        fColumnNames.add(str);
                        str = str.toLowerCase();
                        fColumnNameToIndex.put(str, i);
                        fColumnIndexToName.put(i, str);
                    }
                }
            }
        }
    }

    public Value getCell(int row, int col) {
        XmlElement e = getCellElement(row, col);
        return newValue(e);
    }

    public Value getCell(int keyColumn, String key, int valueColumn) {
        int row = getRowIndex(keyColumn, key);
        return getCell(row, valueColumn);
    }

    public Value getCell(String columnName, int row) {
        int col = getColumnIndex(columnName);
        return getCell(row, col);
    }

    public Value getCell(String keyColumnName, String key, int valueColumn) {
        int keyColumn = getColumnIndex(keyColumnName);
        return getCell(keyColumn, key, valueColumn);
    }

    public Value getCell(
        String keyColumnName,
        String key,
        String valueColumnName) {
        int keyColumn = getColumnIndex(keyColumnName);
        int valueColumn = getColumnIndex(valueColumnName);
        Value value = getCell(keyColumn, key, valueColumn);
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

    public List<Value> getColumn(int columnIndex) {
        List<Value> result = new ArrayList<Value>();
        int width = getWidth();
        if (columnIndex >= 0 && columnIndex < width) {
            int height = getHeight();
            for (int i = 0; i < height; i++) {
                Value cell = getCell(i, columnIndex);
                result.add(cell);
            }
        }
        return result;
    }

    public List<Value> getColumn(String columnName) {
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
        columnName = columnName.toLowerCase();
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

    public List<Value> getRow(int row) {
        List<Value> result = new ArrayList<Value>();
        List<XmlElement> elements = getRowElements(row);
        if (elements != null) {
            for (XmlElement e : elements) {
                result.add(newValue(e));
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
    public List<Value> getRow(String columnName, String columnValue) {
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

    protected Value newValue(XmlElement e) {
        return new Value(e);
    }
}