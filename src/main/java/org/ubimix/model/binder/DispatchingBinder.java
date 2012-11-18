package org.ubimix.model.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.xml.XmlElement;

/**
 * This implementation of the {@link IBinder} interface is used to associate
 * individual binder instances with XML tag names.
 * 
 * @author kotelnikov
 */
public class DispatchingBinder implements IBinder {

    /**
     * This map contains tag names with a list of binders used to create
     * widgets for XML elements with this name.
     */
    private Map<String, List<IBinder>> fMap = new HashMap<String, List<IBinder>>();

    /**
     * Associates a binder with specified tags.
     * 
     * @param binder a binder to add
     * @param tagNames an array of tag names to associate with the binder
     */
    public void addBinder(IBinder binder, String... tagNames) {
        for (String tagName : tagNames) {
            tagName = checkTagName(tagName);
            List<IBinder> list = fMap.get(tagName);
            if (list == null) {
                list = new ArrayList<IBinder>();
                fMap.put(tagName, list);
            }
            list.add(binder);
        }
    }

    /**
     * Associates a binder with the specified tag.
     * 
     * @param tagName the name of the tag
     * @param binder the binder to add name to associate with the binder
     */
    public void addBinder(String tagName, IBinder binder) {
        addBinder(binder, tagName);
    }

    /**
     * @see IBinder#bind(ContentBinding, org.ubimix.model.xml.XmlElement)
     */
    @Override
    public IContentWidget bind(ContentBinding binding, XmlElement e) {
        IContentWidget result = null;
        String name = checkTagName(e.getName());
        List<IBinder> list = fMap.get(name);
        if (list != null) {
            for (IBinder binder : list) {
                result = binder.bind(binding, e);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the normalized tag name.
     * 
     * @param tagName the tag name to normalize
     * @return the normalized tag name
     */
    private String checkTagName(String tagName) {
        tagName = tagName.toLowerCase();
        return tagName;
    }
}