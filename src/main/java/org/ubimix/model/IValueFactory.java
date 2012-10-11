/**
 * 
 */
package org.ubimix.model;

/**
 * @author kotelnikov
 */
public interface IValueFactory<T> {
    T newValue(Object object);
}
