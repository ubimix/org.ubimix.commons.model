/**
 * 
 */
package org.ubimix.model.cleaner;

import junit.framework.TestCase;

import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class BurnerTest extends TestCase {

    /**
     * @param name
     */
    public BurnerTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        testTag(
            "<div><ul><li>B</li></ul></div>",
            "<div><ul><li>B</li></ul></div>");
        testTag(
            true,
            "<div><div><div><div><div>a</div></div></div></div></div>",
            "<div><p>a</p></div>");
        testTag(false, "<div><p>a</p>\n </div>", "<div><p>a</p></div>");

        testTag(
            true,
            "<span>  a  <div>   b   </div>  c   </span>",
            "<span><p> a </p><p> b </p><p> c </p></span>");
        testTag(false, "<div><p>a</p>\n </div>", "<div><p>a</p></div>");
        testTag(
            false,
            "<span>  a  <div>   b   </div>  c   </span>",
            "<div><p> a </p><p> b </p><p> c </p></div>");
        testTag(
            false,
            "<div>\na\n<p>\nb\n</p>\nc\n</div>",
            "<div><p> a </p><p> b </p><p> c </p></div>");
        testTag(false, "<div> <div>A</div>\n </div>", "<div><p>A</p></div>");
        testTag(false, "<div> <div>A</div> \n </div>", "<div><p>A</p></div>");
        testTag(
            true,
            "<div>  a  <div>   b   </div>  c   </div>",
            "<div><p> a </p><p> b </p><p> c </p></div>");
        testTag(
            false,
            "<div>  a  <div>   b   </div>  c   </div>",
            "<div><p> a </p><p> b </p><p> c </p></div>");

        testTag(true, "<div>    a    </div>", "<div> a </div>");
        testTag(false, "<div>    a    </div>", "<p> a </p>");

        testTag(
            true,
            "<div>    a    <span>    b     </span>    c    </div>",
            "<div> a b c </div>");
        testTag(
            false,
            "<div>    a    <span>    b     </span>    c    </div>",
            "<p> a b c </p>");
        testTag(
            true,
            "<div>    a    <div>    b     </div>" + "    c    </div>",
            "<div><p> a </p><p> b </p><p> c </p></div>");

        // Text
        testTag(true, "<div>a</div>", "<div>a</div>");
        testTag(false, "<div>a</div>", "<p>a</p>");

        // In-line element
        testTag(true, "<div><b>a</b></div>", "<div><b>a</b></div>");
        testTag(false, "<div><b>a</b></div>", "<p><b>a</b></p>");
        // Empty in-line element
        testTag(
            true,
            "<div><img src='abc' /></div>",
            "<div><img src='abc'></img></div>");
        testTag(
            false,
            "<div><img src='abc' /></div>",
            "<p><img src='abc'></img></p>");

        // Block elements and text
        testTag(true, "<div>A<p>B</p>C</div>", ""
            + "<div>"
            + "<p>A</p>"
            + "<p>B</p>"
            + "<p>C</p>"
            + "</div>");
        testTag(true, "<div>A<div>B</div>C</div>", ""
            + "<div>"
            + "<p>A</p>"
            + "<p>B</p>"
            + "<p>C</p>"
            + "</div>");
        testTag(
            true,
            "<div><img src='abc' /><div>B</div><b>C</b></div>",
            "<div><p><img src='abc'></img></p><p>B</p><p><b>C</b></p></div>");
        testTag(true, "<div><p>A<div>B</div>C</p></div>", ""
            + "<div>"
            + "<div>"
            + "<p>A</p>"
            + "<p>B</p>"
            + "<p>C</p>"
            + "</div>"
            + "</div>");

        // More complex tests
        testTag(true, ""
            + "<div>"
            + "<div>A</div>"
            + "<div>B <p>C</p>"
            + "</div>"
            + "</div>"
            + "", ""
            + "<div>"
            + "<p>A</p>"
            + "<div><p>B </p><p>C</p></div>"
            + "</div>"
            + "");
        testTag(true, ""
            + "<div>"
            + "<div>A</div>"
            + "<div><a href='#'>B</a> <p>C</p>"
            + "</div>"
            + "</div>"
            + "", ""
            + "<div>"
            + "<p>A</p>"
            + "<div><p><a href='#'>B</a> </p><p>C</p></div>"
            + "</div>"
            + "");
        testTag(
            false,
            ""
                + "<div class=\"block \">\n"
                + "    <div class=\"block-top\">\n"
                + "       Vos annonces\n"
                + "    </div>\n"
                + "    <div class=\"block-content\">\n"
                + "    \n"
                + "        <a href=\"http://petites-annonces.liberation.fr/\">\n"
                + "            \n"
                + "                <img alt=\"Vos annonces\" class=\"visual\" src=\"http://m0.libe.com/libepartnerships/businesspartner_photo/2011/07/23/visuel_PA-1.png\"/>\n"
                + "            \n"
                + "            \n"
                + "            </a>\n"
                + "                            <p>\n"
                + "                                <a href=\"http://petites-annonces.liberation.fr/\">\n"
                + "                <strong>Passez votre annonce dans Libération</strong>\n"
                + "            </a>\n"
                + "                            </p>\n"
                + "            \n"
                + "        \n"
                + "    \n"
                + "    </div>\n"
                + "</div>",
            ""
                + "<div>"
                + "<p> Vos annonces </p>"
                + "<div>"
                + "<p> <a href=\"http://petites-annonces.liberation.fr/\"> <img src=\"http://m0.libe.com/libepartnerships/businesspartner_photo/2011/07/23/visuel_PA-1.png\"/> </a> </p>"
                + "<p> <a href=\"http://petites-annonces.liberation.fr/\"> <strong>Passez votre annonce dans Libération</strong> </a> </p>"
                + "</div>"
                + "</div>");
    }

    public void testLineBreaks() {
        testTag("<div><p> <br /> </p></div>", "<div></div>");
        testTag(
            "<div><p>before<br />after</p></div>",
            "<div><p>before<br />after</p></div>");
        testTag(
            "<div><p>before<br /><br />after</p></div>",
            "<div><p>before</p><p></p><p>after</p></div>");
    }

    public void testLists() {
        testTag("<div><ol><li/></ol></div>", "<div></div>");
        testTag("<ol><li/></ol>", "<ol></ol>");
        testTag(
            "<div><ol><li>xxx</li></ol></div>",
            "<div><ol><li>xxx</li></ol></div>");
        testTag("<ol><li>xxx</li></ol>", "<ol><li>xxx</li></ol>");
        testTag("<div><ol></ol></div>", "<div></div>");
        testTag("<div><div><p>xxx</p></div></div>", "<div><p>xxx</p></div>");

        testTag(
            "<ol><li>X<ul><li>A</li></ul></li></ol>",
            "<ol><li><p>X</p><ul><li>A</li></ul></li></ol>");
        testTag(
            "<ol><li>X<ul><li>A</li></ul>Y</li></ol>",
            "<ol><li><p>X</p><ul><li>A</li></ul><p>Y</p></li></ol>");

    }

    public void testListsBroken() {
        // FIXME: should produce:
        // "<div><ol><li>before</li><li>X</li><li>after</li></ol></div>"
        testTag(
            "<div><ol>before <li>X</li> after</ol></div>",
            "<div><div><p>before </p><li>X</li><p> after</p></div></div>\n");
        // FIXME: Should produce:
        // "<div><h3>Title</h3><ul><li>Item 1</li><li>Item 2<ul><li>IItem 1</li><li>IItem 2</li></ul></li><li>Item 3</li></ul></div>"
        testTag(
            "<div><ol>before <li/> after</ol></div>",
            "<div><ol>before after</ol></div>");
        testTag(
            "<ol><li>X<li>A</li>Y</li></ol>",
            "<ol><li>X<li>A</li>Y</li></ol>");
        testTag("<div><ol>xxx</ol></div>", "<div><p>xxx</p></div>");
        testTag(
            "<div id=\"main-content\" class=\"wiki-content\"><h3 >Title</h3><ul><li class=\"item\">Item 1</li><li class=\"item\">Item 2</li><ul><li class=\"iitem\">IItem 1</li><li class=\"iitem\">IItem 2</li></ul><li>Item 3</li></ul></div>",
            "" + "<div>\n" + "    <h3>Title</h3>\n" + "    <div>\n" // Should be
                                                                    // <ul>
                + "        <li>Item 1</li>\n"
                + "        <li>Item 2</li>\n"
                + "        <ul>\n" // Should be inside of the previous <li> elm
                + "            <li>IItem 1</li>\n"
                + "            <li>IItem 2</li>\n"
                + "        </ul>\n"
                + "        <li>Item 3</li>\n"
                + "    </div>\n" // Should be </ul>
                + "</div>");

    }

    public void testMore() {
        testTag("<div>\n"
            + " A "
            + "<span style=\"font-weight: bold;\">B</span>"
            + " C "
            + "<a href=\"http://foo.bar/titi/toto\">E</a>\n"
            + " <br/>\n"
            + " <img align=\"middle\" alt=\"X\" "
            + "       src=\"http://foo.bar/photo/myphoto.jpg\" "
            + "       style=\"width: 203px;  margin: 5px;\" title=\"X\"/>\n"
            + " <br/>F\n"
            + "</div>", ""
            + "<p> A B C "
            + "<a href='http://foo.bar/titi/toto'>E</a> "
            + "<br></br> "
            + "<img src='http://foo.bar/photo/myphoto.jpg' title='X'></img> "
            + "<br></br>F "
            + "</p>");
    }

    public void testOthers() throws Exception {
        testTag("<html><head><title>Test</title></head><body>"
            + "<p>First paragraph</p>"
            + "<img src='./myimage.gif' />"
            + "</body></html>", ""
            + "<div><head><title>Test</title></head>"
            + "<p>First paragraph</p>"
            + "<p><img src='./myimage.gif'></img></p>"
            + "</div>");

        testTag("<div><p> </p></div>", "<div></div>");
        testTag("<div> This is a message<br />   \n"
            + "  <abc> \n  Toto \n  </abc>"
            + "     Xxx \n"
            + "</div>", "<div><p>"
            + " This is a message<br /> "
            + "<abc> Toto </abc>"
            + " Xxx </p></div>");
        testTag("<div>      </div>", "<div />");
        testTag(
            "<div>  Hello,     world         !    </div>",
            "<div><p> Hello, world ! </p></div>");
        testTag(
            "<div>  Hello,   <strong>  world    </strong>     !    </div>",
            "<div><p> Hello, <strong> world </strong> ! </p></div>");
        testTag(
            "<div> <img src='./myimage.gif' />        Hello,   <strong>world</strong>     !    </div>",
            "<div><p> <img src=\"./myimage.gif\" /> Hello, <strong>world</strong> ! </p></div>");

        // An empty "div" element.
        testTag(
            "<div>  Hello,   <div /> <strong>world</strong>     !    </div>",
            "<div><p> Hello, <strong>world</strong> ! </p></div>");
        testTag(
            "<div>  Hello,   <div>   </div> <strong>world</strong>     !    </div>",
            "<div><p> Hello, <strong>world</strong> ! </p></div>");

        testTag(
            "<div>  Hello,   <div>    X    </div> <strong>world</strong>     !    </div>",
            "<div><p> Hello, </p><p> X </p><p> <strong>world</strong> ! </p></div>");

        testTag(
            "<div><div><div><div>A</div></div></div></div>",
            "<div><p>A</p></div>");
        testTag("<div><div><div><div></div></div></div></div>", "<div />");
        testTag("<div>"
            + "<div><div><div>A</div></div></div>"
            + "<div><div><div>B</div></div></div>"
            + "</div>", "<div><p>A</p><p>B</p></div>");
    }

    public void testTables() {
        // Tables with a single cell are completely removed.
        testTag(
            "<div><table><tr><td>A</td></tr></table></div>",
            "<div><table><tr><td>A</td></tr></table></div>");
        testTag("<div>"
            + "<table><tr><td>"
            + "<p>A</p>"
            + "<ul><li>B</li></ul>"
            + "<p>B</p>"
            + "</td></tr></table>"
            + "</div>", ""
            + ""
            + "<div>"
            + "<table><tr><td>"
            + "<p>A</p>"
            + "<ul><li>B</li></ul>"
            + "<p>B</p>"
            + "</td></tr></table>"
            + "</div>");
        testTag("<div>"
            + "<table><tr><td>"
            + "A"
            + "<ul><li>B</li></ul>"
            + "B"
            + "</td></tr></table>"
            + "</div>", ""
            + ""
            + "<div>"
            + "<table><tr><td>"
            + "<p>A</p>"
            + "<ul><li>B</li></ul>"
            + "<p>B</p>"
            + "</td></tr></table>"
            + "</div>");
        // Embedded tables
        testTag("<div><table><tr><td>"
            + "<table><tr><td>"
            + "<table><tr><td>A</td></tr></table>"
            + "</td></tr></table>"
            + "</td></tr></table></div>", "<div><p>A</p></div>");
        testTag("<div><table><tr><td>"
            + "<table><tr><td>"
            + "<table>"
            + "<tr><td>A</td></tr>"
            + "<tr><td>B</td></tr>"
            + "</table>"
            + "</td></tr></table>"
            + "</td></tr></table></div>", "<div>"
            + "<table>"
            + "<tr><td>A</td></tr>"
            + "<tr><td>B</td></tr>"
            + "</table>"
            + "</div>");

        testTag("<div><table><tr><td>   \n"
            + "<table><tr><td>   \n"
            + "<table><tr><td>"
            + "A"
            + "<ul><li>B</li></ul>"
            + "B"
            + "</td></tr></table>   \n"
            + "</td></tr></table>   \n"
            + "</td></tr></table>    \n</div>", "<div>"
            + "<p>A</p>"
            + "<ul><li>B</li></ul>"
            + "<p>B</p>"
            + "</div>");
    }

    private void testTag(boolean keepIntact, String str, String control) {
        XmlElement e = XmlElement.parse(str);
        Burner burner = new Burner();
        burner.burn(e, keepIntact);
        XmlElement controlXml = XmlElement.parse(control);
        assertEquals(controlXml.toString(), e.toString());
    }

    private void testTag(String str, String control) {
        testTag(false, str, control);
    }

}
