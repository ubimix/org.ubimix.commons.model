/**
 * 
 */
package org.ubimix.model.path;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.path.INodeSelector.SelectionResult;
import org.ubimix.model.path.xml.XmlElementSelector;
import org.ubimix.model.path.xml.XmlNodeProvider;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class PathProcessorTest extends TestCase {

    /**
     * @param name
     */
    public PathProcessorTest(String name) {
        super(name);
    }

    private IPathSelector getPathSelector(String tagName, String... attrs) {
        SkipSelector selector = new SkipSelector(new XmlElementSelector(
            tagName,
            SelectionResult.NO,
            attrs));
        IPathSelector pathSelector = new PathNodeSelector(selector);
        return pathSelector;
    }

    public void test() throws Exception {
        String xml = ""
            + "<html>"
            + "<title />"
            + "<body>"
            + "<p>before</p>"
            + "<p class='xxx'>first</p>"
            + "<p class='yyy'>second</p>"
            + "<p class='xxx'>third</p>"
            + "<p>after</p>"
            + "</body>"
            + "</html>";

        test(
            xml,
            getPathSelector("p"),
            "<p>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>",
            "<p>after</p>");
        test(
            xml,
            getPathSelector("p", "class"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>");
        test(
            xml,
            getPathSelector("p", "class", "yyy"),
            "<p class='yyy'>second</p>");
        test(
            xml,
            getPathSelector("p", "class", "xxx"),
            "<p class='xxx'>first</p>",
            "<p class='xxx'>third</p>");

        xml = ""
            + "<html>"
            + "<title />"
            + "<body>"
            + "<p>before</p>"
            + "<p class='xxx'>first</p>"
            + "<p class='yyy'>blah-blah <a class='xxx'>ref</a> blah-blah</p>"
            + "<p class='xxx'>third</p>"
            + "<p>after</p>"
            + "</body>"
            + "</html>";
        test(
            xml,
            getPathSelector(null, "class"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>blah-blah <a class='xxx'>ref</a> blah-blah</p>",
            "<a class='xxx'>ref</a>",
            "<p class='xxx'>third</p>");
    }

    private void test(String xml, IPathSelector selector, String... controls) {
        INodeProvider provider = new XmlNodeProvider();
        PathProcessor processor = new PathProcessor(provider, selector);
        XmlNode node = XmlElement.parse(xml);
        final List<XmlElement> results = new ArrayList<XmlElement>();
        processor.select(node, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                results.add((XmlElement) node);
                return true;
            }
        });
        assertEquals(controls.length, results.size());
        int i = 0;
        for (String str : controls) {
            XmlElement testNode = results.get(i++);
            assertNotNull(testNode);
            assertEquals(str, testNode.toString());
        }
    }
}
