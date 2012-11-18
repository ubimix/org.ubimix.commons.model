package org.ubimix.model.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * This class is used to associate "widgets" with XML elements and manage them.
 * These widgets could be used to give typed access to individual XML nodes. For
 * example it could be used to represent an HTML table as a sequence of
 * key/value pairs. Or a set of <code>div</code> tags as an image with a
 * description etc.
 * 
 * @author kotelnikov
 */
public class ContentBinding {

    /**
     * The name of an XML element attribute used to keep a widget identifier in
     * XML elements. This class adds this attribute to all XML elements
     * associated with widgets.
     */
    public static String ATTR_BINDING_ID = "data-binding-id";

    /**
     * The root widget binder used as a factory for widgets.
     */
    private IBinder fBinder;

    /**
     * Internal counter for widget identifiers
     */
    private int fIdCounter;

    /**
     * This map is used to keep identifiers and the corresponding widgets.
     */
    private Map<String, IContentWidget> fMap = new HashMap<String, IContentWidget>();

    /**
     * The main constructor. Initializes internal fields and sets the given
     * binder used as a factory for widgets.
     * 
     * @param binder the widget factory
     */
    public ContentBinding(IBinder binder) {
        fBinder = binder;
    }

    /**
     * Initiales the internal fields and binds widgets to XML elements in the
     * list with XML nodes.
     * 
     * @param binder the binder used as a widget factory
     * @param content list of XML nodes to associate with widgets
     */
    public ContentBinding(IBinder binder, Iterable<XmlNode> content) {
        this(binder);
        bindWidgets(content);
    }

    /**
     * Recursively binds widgets to XML elements. All bound widgets are stored
     * in an internal map and could be retrieved using the
     * {@link #getWidget(XmlElement)} or {@link #getWidget(XmlElement, Class)}
     * methods.
     * 
     * @param content container of XML nodes to associate with widgets
     */
    public void bindWidgets(Iterable<XmlNode> content) {
        for (XmlNode node : content) {
            if (!(node instanceof XmlElement)) {
                continue;
            }
            XmlElement e = (XmlElement) node;
            IContentWidget widget = fBinder.bind(this, e);
            if (widget != null) {
                String id = newId();
                e.setAttribute(ATTR_BINDING_ID, id);
                fMap.put(id, widget);
            } else {
                bindWidgets(e);
            }
        }
    }

    /**
     * Searches and returns the first widget of the specified type.
     * 
     * @param type the type of the widget
     * @return the widget of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T> T getWidget(Class<T> type) {
        T result = null;
        for (IContentWidget w : fMap.values()) {
            if (type.isInstance(w)) {
                result = (T) w;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a content widget associated with the specified XML element (or
     * <code>null</code> if this element has no corresponding widget).
     * 
     * @param e an XML element to check
     * @return a widget associated with the given element
     */
    public IContentWidget getWidget(XmlElement e) {
        IContentWidget result = null;
        String id = getWidgetId(e);
        if (id != null) {
            result = fMap.get(id);
        }
        return result;
    }

    /**
     * Returns an identifier of a widget associated with the specified XML
     * element.
     * 
     * @param e an XML element to check
     * @return an identifier of a widget
     */
    public String getWidgetId(XmlElement e) {
        String id = e.getAttribute(ATTR_BINDING_ID);
        return id;
    }

    /**
     * Returns a list of all widgets of the specified type.
     * 
     * @param type the type of widgets to return
     * @return a list of all widgets of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends IContentWidget> List<T> getWidgets(Class<?> type) {
        List<T> result = new ArrayList<T>();
        for (IContentWidget w : fMap.values()) {
            if (type.isInstance(w)) {
                result.add((T) w);
            }
        }
        return result;
    }

    private String newId() {
        return "binding-" + (fIdCounter++);
    }
}