/**
 * 
 */
package org.ubimix.model.html;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class StructuredPropertiesTable extends StructuredTable {

    private int fPropertyColumnId = 0;

    private int fValueColumnId = 1;

    public StructuredPropertiesTable(
        IXmlElement element,
        IValueFactory<? extends Value> factory) {
        super(element, factory);
    }

    public <T extends Value> T getProperty(String name) {
        T result = getCell(fPropertyColumnId, name, fValueColumnId);
        return result;
    }

    public int getPropertyColumnId() {
        return fPropertyColumnId;
    }

    public Set<String> getPropertyNames() {
        Set<String> result = new LinkedHashSet<String>();
        List<Value> nameColumn = getColumn(fPropertyColumnId);
        for (int i = 1; i < nameColumn.size(); i++) {
            Value value = nameColumn.get(i);
            String name = value.getAsText();
            result.add(name);
        }
        return result;
    }

    public int getValueColumnId() {
        return fValueColumnId;
    }

    public StructuredPropertiesTable setPropertyColumnId(int propertyColumnId) {
        fPropertyColumnId = propertyColumnId;
        return this;
    }

    public StructuredPropertiesTable setValueColumnId(int valueColumnId) {
        fValueColumnId = valueColumnId;
        return this;
    }

}
