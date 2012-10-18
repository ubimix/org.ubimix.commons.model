/**
 * 
 */
package org.ubimix.model.cleaner;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class TagDescriptorTest extends TestCase {

    /**
     * @param name
     */
    public TagDescriptorTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        TagType tag = new TagType("tag");
        TagType block = new TagType("block", tag);
        TagType blockContainer = new TagType("blockContainer", tag)
            .setContainedTypes(block);
        TagType inline = new TagType("inline", tag);
        TagType inlineContainer = new TagType("inlineContainer", tag)
            .setContainedTypes(inline);

        TagDescriptor descriptor = new TagDescriptor();
        descriptor.setType(block, "p", "div");
        descriptor.setType(blockContainer, "div");
        descriptor.setType(inline, "a", "span", "img");
        descriptor.setType(inlineContainer, "p", "div", "a", "span");

        assertTrue(descriptor.accepts("div", "div"));
        assertTrue(descriptor.accepts("div", "p"));
        assertTrue(descriptor.accepts("div", "span"));
        assertTrue(descriptor.accepts("div", "a"));
        assertTrue(descriptor.accepts("div", "img"));

        assertFalse(descriptor.accepts("p", "div"));
        assertFalse(descriptor.accepts("p", "p"));
        assertTrue(descriptor.accepts("p", "span"));
        assertTrue(descriptor.accepts("p", "a"));
        assertTrue(descriptor.accepts("p", "img"));

        assertFalse(descriptor.accepts("span", "div"));
        assertFalse(descriptor.accepts("span", "p"));
        assertTrue(descriptor.accepts("span", "span"));
        assertTrue(descriptor.accepts("span", "a"));
        assertTrue(descriptor.accepts("span", "img"));

        assertFalse(descriptor.accepts("a", "div"));
        assertFalse(descriptor.accepts("a", "p"));
        assertTrue(descriptor.accepts("a", "span"));
        assertTrue(descriptor.accepts("a", "a"));
        assertTrue(descriptor.accepts("a", "img"));

        assertFalse(descriptor.accepts("img", "div"));
        assertFalse(descriptor.accepts("img", "p"));
        assertFalse(descriptor.accepts("img", "span"));
        assertFalse(descriptor.accepts("img", "a"));
        assertFalse(descriptor.accepts("img", "img"));

        // Negative tests for non-existing tags
        // (no types were defined for the "xxx" tag)
        assertFalse(descriptor.accepts("div", "xxx"));
        assertFalse(descriptor.accepts("p", "xxx"));
        assertFalse(descriptor.accepts("span", "xxx"));
        assertFalse(descriptor.accepts("a", "xxx"));
        assertFalse(descriptor.accepts("img", "xxx"));

    }

}
