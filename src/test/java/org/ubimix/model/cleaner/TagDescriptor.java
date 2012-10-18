package org.ubimix.model.cleaner;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This object is used to define tag types.
 * 
 * @author kotelnikov
 */
public class TagDescriptor {

    /**
     * Parent tags.
     */
    private Map<String, String> fParentTags = new HashMap<String, String>();

    /**
     * Defines mapping between tags and their respective types. Each tag can
     * have multiple types.
     */
    private Map<String, Set<TagType>> fTagTypes = new HashMap<String, Set<TagType>>();

    /**
     * Checks if a parent tag can contain a specified child tag.
     * 
     * @param parent parent tag to check
     * @param tag a child tag to check
     * @return <code>true</code> if the parent tag can contain the specified
     *         child tag
     */
    public boolean accepts(String parent, String tag) {
        Set<TagType> parentTypes = getTagTypes(parent);
        if (parentTypes == null || parentTypes.isEmpty()) {
            return false;
        }
        Set<TagType> childTypes = getTagTypes(tag);
        if (childTypes == null || childTypes.isEmpty()) {
            return false;
        }
        boolean result = false;
        loop: for (TagType parentType : parentTypes) {
            for (TagType childType : childTypes) {
                if (parentType.contains(childType)) {
                    result = true;
                    break loop;
                }
            }
        }
        return result;
    }

    /**
     * Returns a parent for the given tag
     * 
     * @param tag the tag for which a parent should be returned
     * @return a parent for the given tag
     */
    public String getParentTag(String tag) {
        return fParentTags.get(tag);
    }

    /**
     * Returns a set of types for the specified tag
     * 
     * @param tag the tag to check
     * @return a set of types for the specified tag
     */
    public Set<TagType> getTagTypes(String tag) {
        return fTagTypes.get(tag);
    }

    /**
     * This method checks if the some types were declared for the specified tag.
     * 
     * @param tag the tag to check
     * @return <code>true</code> if some types were declared for the specified
     *         tag
     */
    public boolean isDeclared(String tag) {
        return fTagTypes.containsKey(tag);
    }

    /**
     * Sets a new parent for the specified type.
     * 
     * @param parent a tag parent
     * @param tag the tag to set
     */
    public void setParentTag(String parent, String tag) {
        String t = parent;
        while (t != null) {
            if (tag.equals(t)) {
                throw new IllegalArgumentException(
                    "A cycle in the tag hierarchy was found. Tag: '"
                        + tag
                        + "'. Parent: '"
                        + parent
                        + "'.");
            }
            t = fParentTags.get(t);
        }
        if (!accepts(parent, tag)) {
            throw new IllegalArgumentException("Tag '"
                + parent
                + "' can not contain the '"
                + tag
                + "' tag.");
        }
        fParentTags.put(tag, parent);
    }

    /**
     * Set parents for the specified tag
     * 
     * @param parent the parent tag
     * @param children a collection of children
     */
    public void setParentTags(String parent, Iterable<String> children) {
        for (String child : children) {
            setParentTag(parent, child);
        }
    }

    /**
     * Sets parent for the specified tags.
     * 
     * @param parent the parent tag
     * @param tags child tags
     */
    public void setParentTags(String parent, String... tags) {
        for (String tag : tags) {
            setParentTag(parent, tag);
        }
    }

    /**
     * Sets tag types.
     * 
     * @param type the type of the tag
     * @param tags a collection of types for the tag
     */
    public void setType(TagType type, Collection<String> tags) {
        for (String tag : tags) {
            setType(type, tag);
        }
    }

    /**
     * Sets tag types.
     * 
     * @param type the type of the tag
     * @param tags an array of types for the tag
     */
    public void setType(TagType type, String... tags) {
        for (String tag : tags) {
            setType(type, tag);
        }
    }

    /**
     * Sets a type for the tag
     * 
     * @param type the type to set
     * @param tag the tag to set
     */
    public void setType(TagType type, String tag) {
        Set<TagType> set = fTagTypes.get(tag);
        if (set == null) {
            set = new HashSet<TagType>();
            fTagTypes.put(tag, set);
        }
        set.add(type);
    }
}