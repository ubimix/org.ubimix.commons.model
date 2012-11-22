/**
 * 
 */
package org.ubimix.model.html;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class StructuredPropertiesTable extends StructuredTable {

    private int fPropertyColumnId = 0;

    private int fValueColumnId = 1;

    public StructuredPropertiesTable(
        XmlElement element,
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
