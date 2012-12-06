/**
 * 
 */
package org.ubimix.model.html;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.xml.IXmlElement;

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
        StructuredTree tree,
        String valueControl,
        String... controls) {
        Value value = tree.getValue();
        checkValue(valueControl, value);
        assertEquals(controls.length, tree.getSize());
        List<StructuredTree> list = tree.getSubtrees();
        assertEquals(controls.length, list.size());
        for (int i = 0; i < controls.length; i++) {
            String control = controls[i];

            StructuredTree subtree = tree.getSubtree(i);
            assertNotNull(subtree);
            assertEquals(i, subtree.getIndex());
            checkValue(control, subtree.getValue());

            if (i > 0) {
                StructuredTree prev = tree.getSubtree(i - 1);
                StructuredTree test = subtree.getPreviousSibling();
                assertEquals(prev, test);
            }
            if (i < controls.length - 1) {
                StructuredTree next = tree.getSubtree(i + 1);
                StructuredTree test = subtree.getNextSibling();
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

    private StructuredTree getTree(String html) {
        IXmlElement e = HtmlDocument.parseFragment(html);
        return new StructuredTree(e, Value.FACTORY);
    }

    public void testHierarchy() throws Exception {
        StructuredTree tree;

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
        StructuredTree subtree = tree.getSubtree(1);
        checkTree(subtree, "Second", "A", "B", "C");

        StructuredTree first = tree.getSubtree(0);
        StructuredTree third = tree.getSubtree(2);

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
