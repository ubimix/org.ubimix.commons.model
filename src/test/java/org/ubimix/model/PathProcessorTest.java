/**
 * 
 */
package org.ubimix.model;

import java.util.ArrayList;
import java.util.List;

import org.ubimix.model.path.INodeSelector;
import org.ubimix.model.path.IPathSelector;
import org.ubimix.model.path.utils.MapNodeSelector;
import org.ubimix.model.path.utils.PathSelector;
import org.ubimix.model.path.utils.SkipSelector;

/**
 * @author kotelnikov
 */
public class PathProcessorTest extends AbstractPathProcessorTest {

    /**
     * @param name
     */
    public PathProcessorTest(String name) {
        super(name);
    }

    public IPathSelector getPathSelector(String tagName, String... attrs) {
        SkipSelector selector = MapNodeSelector.getDefaultTagSelector(tagName, attrs);
        IPathSelector pathSelector = new PathSelector(selector);
        return pathSelector;
    }

    public void testEmbeddedNodeSelection() {
        String xml = ""
            + "<html>"
            + "<head><title /></head>"
            + "<body>"
            + "<div class='xxx'>"
            + "<span class='nn'>A <span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span> G</span>"
            + "</div>"
            + "<div>"
            + "<span class='nn'>N <span class='nn'>M <span>P</span> P <span class='nn'>Q</span> R</span> S</span>"
            + "</div>"
            + "</body>"
            + "</html>";

        List<INodeSelector> list = new ArrayList<INodeSelector>();
        list.add(MapNodeSelector.getDefaultTagSelector("div", "class", "xxx"));
        list.add(MapNodeSelector.getDefaultTagSelector("span", "class", "nn"));
        IPathSelector pathSelector = new PathSelector(list);
        testXml(
            xml,
            true,
            pathSelector,
            "<span class='nn'>A <span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span> G</span>",
            "<span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span>",
            "<span class='nn'>E</span>");
        testXml(
            xml,
            false,
            pathSelector,
            "<span class='nn'>A <span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span> G</span>");

        //
        list = new ArrayList<INodeSelector>();
        list.add(MapNodeSelector.getDefaultTagSelector("div"));
        list.add(MapNodeSelector.getDefaultTagSelector("span", "class", "nn"));
        pathSelector = new PathSelector(list);
        testXml(
            xml,
            true,
            pathSelector,
            "<span class='nn'>A <span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span> G</span>",
            "<span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span>",
            "<span class='nn'>E</span>",
            "<span class='nn'>N <span class='nn'>M <span>P</span> P <span class='nn'>Q</span> R</span> S</span>",
            "<span class='nn'>M <span>P</span> P <span class='nn'>Q</span> R</span>",
            "<span class='nn'>Q</span>");
        testXml(
            xml,
            false,
            pathSelector,
            "<span class='nn'>A <span class='nn'>B <span>C</span> D <span class='nn'>E</span> F</span> G</span>");

    }

    public void testResultCollectingMode() {
        // Collecting embedded nodes
        String xml = ""
            + "<html>"
            + "<head><title /></head>"
            + "<body>"
            + "<div>before</div>"
            + "<div class='xxx'>A <div class='xxx'>B</div> C</div>"
            + "<div>after</div>"
            + "</body>"
            + "</html>";
        testXml(
            xml,
            true,
            getPathSelector("div"),
            "<div>before</div>",
            "<div class='xxx'>A <div class='xxx'>B</div> C</div>",
            "<div class='xxx'>B</div>",
            "<div>after</div>");
        testXml(xml, false, getPathSelector("div"), "<div>before</div>");
        testXml(
            xml,
            true,
            getPathSelector("div", "class", "xxx"),
            "<div class='xxx'>A <div class='xxx'>B</div> C</div>",
            "<div class='xxx'>B</div>");
        testXml(
            xml,
            false,
            getPathSelector("div", "class", "xxx"),
            "<div class='xxx'>A <div class='xxx'>B</div> C</div>");

    }

    public void testSimplePathSelectors() throws Exception {
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

        testXml(
            xml,
            getPathSelector("p"),
            "<p>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>",
            "<p>after</p>");
        testXml(
            xml,
            getPathSelector("p", "class"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            getPathSelector("p", "class", "yyy"),
            "<p class='yyy'>second</p>");
        testXml(
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
        testXml(
            xml,
            getPathSelector(null, "class"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>blah-blah <a class='xxx'>ref</a> blah-blah</p>",
            "<a class='xxx'>ref</a>",
            "<p class='xxx'>third</p>");
    }
}
