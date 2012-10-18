/**
 * 
 */
package org.ubimix.model.cleaner;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class TagTypeTest extends TestCase {

    /**
     * @param name
     */
    public TagTypeTest(String name) {
        super(name);
    }

    public void testTagContainer() {
        TagType tag = new TagType("tag");
        TagType inline = new TagType("inline", tag);
        TagType inlineContainer = new TagType("inlineContainer", tag)
            .setContainedTypes(inline);
        TagType block = new TagType("block", tag);
        TagType blockContainer = new TagType("blockContainer", tag)
            .setContainedTypes(block);

        TagType a = new TagType("a", inline, inlineContainer);
        TagType p = new TagType("p", block, inlineContainer);
        TagType div = new TagType("div", block, inlineContainer, blockContainer);

        assertTrue(inlineContainer.contains(inline));
        assertTrue(inlineContainer.contains(a));
        assertTrue(a.contains(a));
        assertTrue(a.contains(inline));
        assertTrue(p.contains(inline));
        assertTrue(p.contains(a));
        assertTrue(div.contains(div));
        assertTrue(div.contains(a));
        assertTrue(div.contains(p));
        assertFalse(p.contains(div));
        assertFalse(p.contains(p));
        assertFalse(a.contains(p));
    }

    public void testTagTypeCycles() {
        TagType bodyTag = new TagType("bodyTag");
        TagType block = new TagType("block", bodyTag);
        TagType div = new TagType("div", block);
        try {
            block.setParentTypes(block);
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            bodyTag.setParentTypes(block);
            fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            bodyTag.setParentTypes(div);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    public void testTagTypeHieararchy() {
        TagType simpleTag = new TagType("simple");
        TagType inline = new TagType("inline", simpleTag);
        TagType special = new TagType("special", simpleTag);
        TagType myTag = new TagType("mytag", special);
        TagType myInlineTag = new TagType("myInlineTag", myTag, inline);
        assertTrue(myTag.is(simpleTag));
        assertTrue(myTag.is(special));
        assertFalse(myTag.is(inline));
        assertTrue(myInlineTag.is(simpleTag));
        assertTrue(myInlineTag.is(special));
        assertTrue(myInlineTag.is(myTag));
        assertTrue(myInlineTag.is(inline));
    }
}
