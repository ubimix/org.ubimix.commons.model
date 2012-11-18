package org.ubimix.model.binder;

import org.ubimix.model.xml.XmlElement;

/**
 * Instances of this type are used by the {@link ContentBinding} class to
 * associate widgets with individual XML elements.
 * 
 * @author kotelnikov
 */
public interface IBinder {

    /**
     * Creates and returns a widget associated with the specified XML element;
     * this method could return <code>null</code> if the element could/should
     * not be bind with a widget.
     * 
     * @param binding the {@link ContentBinding} instance calling this method;
     *        this object is used as a context for widgets
     * @param e an XML element
     * @return a widget corresponding to the specified XML element
     */
    IContentWidget bind(ContentBinding binding, XmlElement e);

}