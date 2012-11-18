/**
 * 
 */
package org.ubimix.model.binder;

import org.ubimix.model.xml.XmlElement;

/**
 * Default implementation of the {@link IContentWidget} interface
 * 
 * @author kotelnikov
 */
public class ContentWidget implements IContentWidget {

    /**
     * Content binding creating this object
     */
    private ContentBinding fContentBinding;

    /**
     * Xml element associated with this widget
     */
    private XmlElement fElement;

    /**
     * This constructor sets the content binding and the XML element associated
     * with this widget.
     * 
     * @param binding the content binding creating this widget
     * @param element the XML element corresponding to this widget
     */
    public ContentWidget(ContentBinding binding, XmlElement element) {
        fContentBinding = binding;
        fElement = element;
    }

    /**
     * @see org.ubimix.model.binder.IContentWidget#getBinding()
     */
    @Override
    public ContentBinding getBinding() {
        return fContentBinding;
    }

    /**
     * @see org.ubimix.model.binder.IContentWidget#getElement()
     */
    @Override
    public XmlElement getElement() {
        return fElement;
    }

    /**
     * @see org.ubimix.model.binder.IContentWidget#getWidgetId()
     */
    @Override
    public String getWidgetId() {
        return fContentBinding.getWidgetId(fElement);
    }

}
