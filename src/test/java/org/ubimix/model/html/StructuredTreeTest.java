/**
 * 
 */
package org.ubimix.model.html;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class StructuredTreeTest extends TestCase {

    /**
     * @param name
     */
    public StructuredTreeTest(String name) {
        super(name);
    }

    protected void checkTree(
        StructuredTree<Value> tree,
        String valueControl,
        String... controls) {
        Value value = tree.getValue();
        testValue(valueControl, value);
        assertEquals(controls.length, tree.getSize());
        List<StructuredTree<Value>> list = tree.getSubtrees();
        assertEquals(controls.length, list.size());
        for (int i = 0; i < controls.length; i++) {
            String control = controls[i];

            StructuredTree<Value> subtree = tree.getSubtree(i);
            assertNotNull(subtree);
            assertEquals(i, subtree.getIndex());
            testValue(control, subtree.getValue());

            subtree = list.get(i);
            assertNotNull(subtree);
            testValue(control, subtree.getValue());
        }

    }

    private StructuredTree<Value> getTree(String html) {
        XmlElement e = HtmlDocument.parseFragment(html);
        return new StructuredTree<Value>(e, Value.FACTORY);
    }

    public void testHierarchy() throws Exception {
        StructuredTree<Value> tree;

        // --------------------------------------------------------------------
        tree = getTree(""
            + "<ul>"
            + " <li> First"
            + " <li> Second"
            + " <li> Third");
        checkTree(tree, "", "First", "Second", "Third");

        // --------------------------------------------------------------------
        tree = getTree(""
            + "<ul>"
            + " <li> First"
            + " <li> Second"
            + "   <ul> "
            + "     <li> A"
            + "     <li> B"
            + "     <li> C"
            + "   </ul>"
            + " <li> Third");
        checkTree(tree, "", "First", "Second", "Third");
        StructuredTree<Value> subtree = tree.getSubtree(1);
        checkTree(subtree, "Second", "A", "B", "C");

        // --------------------------------------------------------------------
        tree = getTree(""
            + "<div>"
            + "Hello, there"
            + "<ul>"
            + " <li> First"
            + " <li> Second"
            + "   <ul> "
            + "     <li> A"
            + "     <li> B"
            + "     <li> C"
            + "   </ul>"
            + " <li> Third"
            + "</ul>"
            + "</div>");
        checkTree(tree, "Hello, there", "First", "Second", "Third");
        subtree = tree.getSubtree(1);
        checkTree(subtree, "Second", "A", "B", "C");

        // --------------------------------------------------------------------
        tree = getTree(""
            + "<ul>"
            + " <li> First"
            + " <li> Before "
            + "   <ul> "
            + "     <li> A"
            + "     <li> B"
            + "     <li> C"
            + "   </ul>"
            + "   Between "
            + "   <ul> "
            + "     <li> D"
            + "     <li> E"
            + "     <li> F"
            + "   </ul>"
            + "   After "
            + " <li> Third");
        checkTree(tree, "", "First", "Before Between After", "Third");
        subtree = tree.getSubtree(1);
        checkTree(subtree, "Before Between After", "A", "B", "C", "D", "E", "F");
    }

    protected void testValue(String valueControl, Value value) {
        assertNotNull(value);
        assertEquals(valueControl, value.getAsText());
    }

}
