/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author kotelnikov
 */
public class TagCardinalitiesMap {

    /**
     * This class defines how much a tag occurs in the parent tag.
     * 
     * @author kotelnikov
     */
    public static class TagCardinality {

        public static TagCardinality get(int min, int max, String... tagNames) {
            return new TagCardinality(min, max, tagNames);
        }

        private int fMaxCardinality = Integer.MAX_VALUE;

        private int fMinCardinality;

        private Set<String> fNames = new HashSet<String>();

        public TagCardinality(int min, int max, Collection<String> tagNames) {
            fMinCardinality = min;
            fMaxCardinality = max;
            fNames.addAll(tagNames);
        }

        public TagCardinality(int min, int max, String... tagNames) {
            this(min, max, Arrays.asList(tagNames));
        }

        public boolean accept(String tagName) {
            return fNames.contains(tagName);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TagCardinality)) {
                return false;
            }
            TagCardinality o = (TagCardinality) obj;
            return fNames.equals(o.fNames)
                && fMinCardinality == o.fMinCardinality
                && fMaxCardinality == o.fMaxCardinality;
        }

        public int getMaxCardinality() {
            return fMaxCardinality;
        }

        public int getMinCardinality() {
            return fMinCardinality;
        }

        public Set<String> getTagNames() {
            return fNames;
        }

        @Override
        public int hashCode() {
            return fNames.hashCode() ^ fMinCardinality ^ fMaxCardinality;
        }

        @Override
        public String toString() {
            return fNames + "(" + fMinCardinality + ":" + fMaxCardinality + ")";
        }
    }

    /**
     * Defines cardinalities for child tags.
     */
    private Map<String, List<TagCardinality>> fTagCardinality = new HashMap<String, List<TagCardinality>>();

    /**
     * 
     */
    public TagCardinalitiesMap() {
    }

    /**
     * Returns cardinalities for child tags.
     * 
     * @param tagName the name of the tag
     * @return cardinalities for child tags
     */
    public List<TagCardinality> getTagCardinalities(String tagName) {
        List<TagCardinality> result = fTagCardinality.get(tagName);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * Sets children cardinalities for the specified tag.
     * 
     * @param tagName the name of the tag
     * @param cardinalities cardinalities for child tags
     */
    public void setTagCardinalities(
        String tagName,
        List<TagCardinality> cardinalities) {
        fTagCardinality.put(tagName, cardinalities);
    }

    /**
     * Sets children cardinalities for the specified tag.
     * 
     * @param tagName the name of the tag
     * @param cardinalities cardinalities for child tags
     */
    public void setTagCardinalities(
        String tagName,
        TagCardinality... cardinalities) {
        fTagCardinality.put(tagName, Arrays.asList(cardinalities));
    }
}
