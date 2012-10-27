/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class InlineNodesBurnerTest extends TestCase {

    /**
     * @param name
     */
    public InlineNodesBurnerTest(String name) {
        super(name);
    }

    private List<XmlNode> list(String... strings) {
        List<XmlNode> result = new ArrayList<XmlNode>();
        for (String str : strings) {
            XmlNode node = null;
            if (str.startsWith("<")) {
                node = HtmlDocument.parseFragment(str);
            } else {
                node = new XmlText(str);
            }
            result.add(node);
        }
        return result;
    }

    private void testHandler(
        ITagListProcessor processor,
        boolean keepSpaces,
        List<XmlNode> list,
        String... controls) {
        XmlElement div = new XmlElement("div");
        div.setChildren(list);

        List<XmlNode> l = processor.handle(div, keepSpaces);
        assertEquals(1, l.size());
        XmlNode e = l.get(0);
        assertTrue(e instanceof XmlElement);
        List<XmlNode> result = ((XmlElement) e).getChildren();
        assertEquals(controls.length, result.size());
        int i = 0;
        for (String str : controls) {
            XmlNode node = result.get(i++);
            assertNotNull(node);
            assertEquals(str, node.toString());
        }
    }

    public void testInlineNodesProcessor() {
        testInlineNodesProcessor(list(""));
        testInlineNodesProcessor(list(" "), " ");
        testInlineNodesProcessor(list("  "), "  ");
        testInlineNodesProcessor(list(" ", "a", " "), "a");
        testInlineNodesProcessor(list("a", " ", "b"), "a", " ", "b");
        testInlineNodesProcessor(list(" ", "a", " ", "b", " "), "a", " ", "b");
        testInlineNodesProcessor(
            list(" ", "a", " ", "<br></br>", " ", "b", " "),
            "a",
            "<br></br>",
            "b");
        testInlineNodesProcessor(list(" ", "a", " "), "a");
        testInlineNodesProcessor(list(" ", "<br></br>", " "), " ");
        testInlineNodesProcessor(
            list(" ", "a", " ", "<br></br>", " ", "b", " "),
            "a",
            "<br></br>",
            "b");
        testInlineNodesProcessor(
            list(" ", "<br></br>", " ", "a", " ", "<br></br>", " "),
            "a");
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<br></br>",
                " ",
                "b",
                " ",
                "<br></br>",
                " ",
                "c",
                " "),
            "a",
            "<br></br>",
            "b",
            "<br></br>",
            "c");
        // --------------------------------------------------------
        testInlineNodesProcessor(
            list(" ", "a", "<img></img>", "<br></br>", "<img></img>", "b", " "),
            "a",
            "<img></img>",
            "<br></br>",
            "<img></img>",
            "b");

        // --------------------------------------------------------
        testInlineNodesProcessor(list("a", "<p>X</p>"), "<p>a</p>", "<p>X</p>");
        testInlineNodesProcessor(
            list(" ", "a", "<p>X</p>", "b", " "),
            "<p>a</p>",
            "<p>X</p>",
            "<p>b</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<p>X</p>",
                " ",
                "b",
                " ",
                "<div>N</div>",
                " ",
                "c",
                " "),
            "<p>a</p>",
            "<p>X</p>",
            "<p>b</p>",
            "<div>N</div>",
            "<p>c</p>");

        // --------------------------------------------------------
        testInlineNodesProcessor(
            list("a", "<br></br>", "<br></br>", "b"),
            "<p>a</p>",
            "<p>b</p>");
        testInlineNodesProcessor(
            list(" ", "a", " ", "<br></br>", "<br></br>", " ", "b", " "),
            "<p>a</p>",
            "<p>b</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " ",
                "b",
                " "),
            "<p>a</p>",
            "<p>b</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<br></br>",
                "<br></br>",
                " ",
                "b",
                " ",
                "<br></br>",
                "<br></br>",
                " ",
                "c",
                " "),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<br></br>",
                " ",
                " ",
                "<br></br>",
                " ",
                "b",
                " ",
                "<br></br>",
                " ",
                " ",
                "<br></br>",
                " ",
                "c",
                " "),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c</p>");
        // --------------------------------------------------------
        testInlineNodesProcessor(
            list(
                " ",
                "a",
                " ",
                "<br></br>",
                "<br></br>",
                " ",
                "b",
                " ",
                "<br></br>",
                " ",
                "c",
                " ",
                "<br></br>",
                " ",
                "d",
                " "),
            "<p>a</p>",
            "<p>b<br></br>c<br></br>d</p>");
        // --------------------------------------------------------
        testInlineNodesProcessor(list("    ", "<p>a</p>", "     "), "<p>a</p>");
        testInlineNodesProcessor(
            list(" ", "<br></br>", " ", "<p>a</p>", " ", "<br></br>", " "),
            "<p>a</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "<br></br>",
                "<br></br>",
                "<br></br>",
                " ",
                "<p>a</p>",
                " ",
                "<br></br>",
                "<br></br>",
                "<br></br>",
                " "),
            "<p>a</p>");
        testInlineNodesProcessor(
            list(
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " ",
                "<p>a</p>",
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " ",
                "<br></br>",
                " "),
            "<p>a</p>");
        testInlineNodesProcessor(
            list(" ", "<p>a</p>", " ", "<p>b</p>", " "),
            "<p>a</p>",
            "<p>b</p>");
        testInlineNodesProcessor(
            list(" ", "<p>a</p>", "b", "<p>c</p>", " "),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c</p>");
        testInlineNodesProcessor(
            list("a", "<p>b</p>", "c", "<p>d</p>", "e"),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c</p>",
            "<p>d</p>",
            "<p>e</p>");
        testInlineNodesProcessor(
            list("a", "<p>b</p>", "c", "<img></img>", "d", "<p>e</p>", "f"),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c<img></img>d</p>",
            "<p>e</p>",
            "<p>f</p>");
        testInlineNodesProcessor(
            list(
                "a",
                " ",
                "<p>b</p>",
                " ",
                "c",
                " ",
                "<img></img>",
                " ",
                "d",
                " ",
                "<p>e</p>",
                " ",
                "f"),
            "<p>a</p>",
            "<p>b</p>",
            "<p>c <img></img> d</p>",
            "<p>e</p>",
            "<p>f</p>");

    }

    private void testInlineNodesProcessor(
        List<XmlNode> list,
        String... controls) {
        testHandler(new InlineNodesProcessor(), false, list, controls);
    }

    public void testTextNodeReducer() {
        testTextNodeReducer(
            false,
            list(" ", " ", " a ", " b ", " ", "  c  ", "   "),
            " a b c ");
        testTextNodeReducer(
            false,
            list(" ", "\n\n", " a \n", "\n b \n ", "  c \n ", " \n  "),
            " a b c ");
        testTextNodeReducer(
            true,
            list(" ", "\n\n", " a \n", "\n b \n ", "  c \n ", " \n  "),
            " \n\n a \n\n b \n   c \n  \n  ");

        testTextNodeReducer(
            false,
            list(" ", " ", " a ", " b ", "<br></br>", "  c  ", "   "),
            " a b ",
            "<br></br>",
            " c ");
        testTextNodeReducer(
            false,
            list(
                " ",
                "\n\n",
                " a \n",
                "\n b \n ",
                "<br></br>",
                "  c \n ",
                " \n  "),
            " a b ",
            "<br></br>",
            " c ");
        testTextNodeReducer(
            true,
            list(
                " ",
                "\n\n",
                " a \n",
                "\n b \n ",
                "<br></br>",
                "  c \n ",
                " \n  "),
            " \n\n a \n\n b \n ",
            "<br></br>",
            "  c \n  \n  ");
    }

    private void testTextNodeReducer(
        boolean keepSpaces,
        List<XmlNode> list,
        String... controls) {
        testHandler(new TextNodeReducer(), keepSpaces, list, controls);
    }

}
