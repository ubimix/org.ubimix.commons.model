/**
 * 
 */
package org.ubimix.model.html;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class StructuredPropertiesTable<T extends Value>
    extends
    StructuredTable<T> {

    private int fPropertyColumnId = 0;

    private int fValueColumnId = 1;

    public StructuredPropertiesTable(
        XmlElement element,
        IValueFactory<T> factory) {
        super(element, factory);
    }

    public T getProperty(String name) {
        T result = getCell(fPropertyColumnId, name, fValueColumnId);
        return result;
    }

    public int getPropertyColumnId() {
        return fPropertyColumnId;
    }

    public int getValueColumnId() {
        return fValueColumnId;
    }

    public StructuredPropertiesTable<T> setPropertyColumnId(int propertyColumnId) {
        fPropertyColumnId = propertyColumnId;
        return this;
    }

    public StructuredPropertiesTable<T> setValueColumnId(int valueColumnId) {
        fValueColumnId = valueColumnId;
        return this;
    }

}
