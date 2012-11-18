package org.ubimix.model.binder;

import org.ubimix.model.xml.XmlElement;

/**
 * A interface used to declare widget objects associated with XML elements.
 * Widgets could be used to add additional/simplified/structured access to the
 * content of XML documents.
 * 
 * @author kotelnikov
 */
public interface IContentWidget {

    /**
     * Returns the binding object managing associations between XML elements and
     * widgets.
     * 
     * @return the binding object managing associations between XML elements and
     *         widgets
     */
    ContentBinding getBinding();

    /**
     * Returns an XML element corresponding to this widget.
     * 
     * @return an XML element corresponding to this widget
     */
    XmlElement getElement();

    /**
     * Returns a unique identifier of this widget.
     * 
     * @return the identifier of this widget
     */
    String getWidgetId();
}