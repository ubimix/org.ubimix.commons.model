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
        checkValue(valueControl, value);
        assertEquals(controls.length, tree.getSize());
        List<StructuredTree<Value>> list = tree.getSubtrees();
        assertEquals(controls.length, list.size());
        for (int i = 0; i < controls.length; i++) {
            String control = controls[i];

            StructuredTree<Value> subtree = tree.getSubtree(i);
            assertNotNull(subtree);
            assertEquals(i, subtree.getIndex());
            checkValue(control, subtree.getValue());

            if (i > 0) {
                StructuredTree<Value> prev = tree.getSubtree(i - 1);
                StructuredTree<Value> test = subtree.getPreviousSibling();
                assertEquals(prev, test);
            }
            if (i < controls.length - 1) {
                StructuredTree<Value> next = tree.getSubtree(i + 1);
                StructuredTree<Value> test = subtree.getNextSibling();
                assertEquals(next, test);
            }

            subtree = list.get(i);
            assertNotNull(subtree);
            checkValue(control, subtree.getValue());
        }

    }

    protected void checkValue(String valueControl, Value value) {
        assertNotNull(value);
        assertEquals(valueControl, value.getAsText());
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

        StructuredTree<Value> first = tree.getSubtree(0);
        StructuredTree<Value> third = tree.getSubtree(2);

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

}
