package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This object is used to define types for tags. Each tag has one or multiple
 * types. All rules are defined for tag types and automatically applied for tag
 * itself. For example the following tags "a", "span", "strong" and "img" are
 * inline tags. So it is possible to introduce the tag type called "inline"
 * which defines the common behaviour of all these tags. Multiple typing allows
 * to define more complex tag behaviour. For example the "span" tag is not only
 * an "inline" tag but also an "inlineContainer", which means that it can
 * contain other inline tags including other "span" tags.
 * 
 * @author kotelnikov
 */
public class TagType {

    /**
     * This collection is used to keep all tag types which can be contained in
     * this type.
     */
    private Set<TagType> fContainedTypes = new LinkedHashSet<TagType>();

    /**
     * The name of this type.
     */
    private String fName;

    /**
     * Parent types
     */
    private Set<TagType> fParentTypes = new LinkedHashSet<TagType>();

    /**
     * Common constructor defining the name and parent types.
     * 
     * @param name the name of this tag
     * @param parentTypes parent types
     */
    public TagType(String name, TagType... parentTypes) {
        fName = name;
        setParentTypes(parentTypes);
    }

    /**
     * This method could be used in sub-classes to cast this object to a
     * sub-type.
     * 
     * @return reference to this object casted to a sub-type
     */
    @SuppressWarnings("unchecked")
    protected <T extends TagType> T cast() {
        return (T) this;
    }

    /**
     * Returns <code>true</code> if this object of one of the parents contains
     * the specified type.
     * 
     * @param type the type to check
     * @return <code>true</code> if this object of one of the parents contains
     *         the specified type.
     */
    protected boolean checkContains(TagType type) {
        if (fContainedTypes.contains(type)) {
            return true;
        }
        for (TagType parent : fParentTypes) {
            if (parent.contains(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that the specified type does not have this type as a parent. This
     * method is used to avoid cycles in type hierarchies.
     * 
     * @param type the type to check
     */
    private void checkCycle(TagType type) {
        if (this.equals(type)) {
            throw new IllegalArgumentException("Cyclic dependencies between '"
                + this
                + "' and '"
                + type
                + "'.");
        }
        for (TagType parent : fParentTypes) {
            parent.checkCycle(type);
        }
    }

    /**
     * Returns <code>true</code> if this type contains the specified type.
     * 
     * @param type the type to check
     * @return <code>true</code> if this type contains the specified type
     */
    public boolean contains(TagType type) {
        if (checkContains(type)) {
            return true;
        }
        for (TagType typeParent : type.getParentTypes()) {
            if (checkContains(typeParent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TagType)) {
            return false;
        }
        TagType o = (TagType) obj;
        return fName.equals(o.fName);
    }

    /**
     * Returns a set of all types contained directly in this type
     * 
     * @return a set of all types contained directly in this type
     */
    public Set<TagType> getContainedTypes() {
        return fContainedTypes;
    }

    /**
     * Returns the name of this type
     * 
     * @return the name of this type
     */
    public String getName() {
        return fName;
    }

    /**
     * Returns a set of all parent types for this type
     * 
     * @return a set of all parent types
     */
    public Set<TagType> getParentTypes() {
        return fParentTypes;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return fName.hashCode();
    }

    /**
     * This method checks if the specified type is a parent type for this one.
     * 
     * @param parent a parent type to check
     * @return <code>true</code> if this type is a sub-type of the specified
     *         type
     */
    public boolean is(TagType parent) {
        if (this.equals(parent)) {
            return true;
        }
        if (fParentTypes.contains(parent)) {
            return true;
        }
        for (TagType p : fParentTypes) {
            if (p.is(parent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds new types contained in this type.
     * 
     * @param types an array of types allowed in tags of this type
     * @return a reference to this instance
     */
    public <T extends TagType> T setContainedTypes(TagType... types) {
        fContainedTypes.addAll(Arrays.asList(types));
        return cast();
    }

    /**
     * Sets new super-types for this type.
     * 
     * @param types an array of parent types.
     * @return reference to this object
     */
    public <T extends TagType> T setParentTypes(TagType... types) {
        for (TagType type : types) {
            type.checkCycle(this);
        }
        fParentTypes.addAll(Arrays.asList(types));
        return cast();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return fName.toString();
    }

}