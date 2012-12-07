/**
 * 
 */
package org.ubimix.model;

import org.ubimix.model.selector.IPathSelector;

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

    public void test() {
        String xml = "<h1 id='SR1877'><a href='http://www.sdcinfo.com/semio2011/?rubrique1873'>\n"
            + "                                    <span class='logoplierT2'>Signes fonctionnels SR1877</span>\n"
            + "                                    \n"
            + "                                </a></h1>";
        testXml(xml, "span[id]");
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

        IPathSelector pathSelector = getCssSelector("div[class=xxx] span.nn");
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
        pathSelector = getCssSelector("div span[class='nn']");
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
            getCssSelector("div"),
            "<div>before</div>",
            "<div class='xxx'>A <div class='xxx'>B</div> C</div>",
            "<div class='xxx'>B</div>",
            "<div>after</div>");
        testXml(xml, false, getCssSelector("div"), "<div>before</div>");
        testXml(
            xml,
            true,
            getCssSelector("div.xxx"),
            "<div class='xxx'>A <div class='xxx'>B</div> C</div>",
            "<div class='xxx'>B</div>");
        testXml(
            xml,
            false,
            getCssSelector("div.xxx"),
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
            getCssSelector("p"),
            "<p>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>",
            "<p>after</p>");
        testXml(
            xml,
            getCssSelector("p[class]"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>second</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            getCssSelector("p[class=yyy]"),
            "<p class='yyy'>second</p>");
        testXml(
            xml,
            getCssSelector("p[class=xxx]"),
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
            getCssSelector("[class]"),
            "<p class='xxx'>first</p>",
            "<p class='yyy'>blah-blah <a class='xxx'>ref</a> blah-blah</p>",
            "<a class='xxx'>ref</a>",
            "<p class='xxx'>third</p>");
    }
}
