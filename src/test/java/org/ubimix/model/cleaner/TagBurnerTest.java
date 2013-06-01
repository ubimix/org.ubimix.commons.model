/**
 * 
 */
package org.ubimix.model.cleaner;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlUtils;

/**
 * @author kotelnikov
 */
public class TagBurnerTest extends TestCase {

    private final IXmlFactory fFactory = newXmlFactory();

    /**
     * @param name
     */
    public TagBurnerTest(String name) {
        super(name);
    }

    protected IXmlFactory newXmlFactory() {
        return XmlFactory.getInstance();
    }

    public void testANodeWithJavaScript() {
        String input = "<a href=\"javascript://print();\">Imprimer</a>";
        IXmlElement element = HtmlDocument.parseFragment(input);
        testTagBurner(element.toString(), "<a href=''>Imprimer</a>");

    }

    public void testBr() {
        String input = "<span><br>a b c</span>";
        IXmlElement element = HtmlDocument.parseFragment(input);
        testTagBurner(element.toString(), "<span><br/>a b c</span>");
    }

    public void testHtmlLists() {
        testHtmlTagBurner(
            "<div><ul><ul><li> a <div> b </div> c </li></ul></ul></div>",
            "<ul><li><p> a </p><p> b </p><p> c </p></li></ul>");
        testHtmlTagBurner(
            "<div><ul>before<ul><li> a <div> b </div> c </li></ul>after</ul></div>",
            "<div>"
                + "<p>before</p>"
                + "<ul>"
                + "<li>"
                + "<p> a </p>"
                + "<p> b </p>"
                + "<p> c </p>"
                + "</li>"
                + "</ul>"
                + "<p>after</p>"
                + "</div>");
        testHtmlTagBurner(
            "<div><ul><p>before</p><li> a <div> b </div> c </li>after</ul></div>",
            ""
                + "<div>"
                + "<p>before</p>"
                + "<ul>"
                + "<li>"
                + "<p> a </p><p> b </p><p> c </p>"
                + "</li>"
                + "</ul>"
                + "<p>after</p>"
                + "</div>");
        testHtmlTagBurner(
            "<div><ul><p>before</p><li> a <div> b </div> c </li>after</ul></div>",
            ""
                + "<div>"
                + "<p>before</p>"
                + "<ul>"
                + "<li><p> a </p><p> b </p><p> c </p></li>"
                + "</ul>"
                + "<p>after</p>"
                + "</div>");
        testHtmlTagBurner(
            "<div><ul>before<li> a <div> b </div> c </li>after</ul></div>",
            "<div><p>before</p><ul><li><p> a </p><p> b </p><p> c </p></li></ul><p>after</p></div>");

    }

    private void testHtmlTagBurner(String str, String control) {
        TagBurner burner = new TagBurner();
        IXmlElement div = HtmlDocument.parseFragment(str);
        List<IXmlNode> l = burner.handle(div, false);
        if (l.size() > 1) {
            div = fFactory.newElement("div");
            div.setChildren(l);
            assertEquals(control, div.toString());
        } else if (l.size() == 1) {
            IXmlNode e = l.get(0);
            assertTrue(e instanceof IXmlElement);
            assertEquals(control, e.toString());
        }
    }

    public void testIds() {
        testTagBurner(
            "<div><div id='x'> a <div> b </div> c </div></div>",
            "<div id='x'><p> a </p><p> b </p><p> c </p></div>");
        testTagBurner(
            "<div><div id='x'> a </div><div> b </div> c </div></div>",
            "<p id='x'> a </p><p> b </p><p> c </p>");
        testTagBurner(
            "<div><div id='x'><div id='y'><div id='z'> a </div></div></div><div> b </div> c </div></div>",
            "<div id='x'><div id='y'><p id='z'> a </p></div></div><p> b </p><p> c </p>");
        testTagBurner(
            "<div><div id='x'><div><div id='z'> a </div></div></div><div> b </div> c </div></div>",
            "<div id='x'><p id='z'> a </p></div><p> b </p><p> c </p>");

        testTagBurner(
            "<div><p id='1'>a<p>b</p><p>c</p></p></div>",
            "<p id='1'><p>a</p><p>b</p><p>c</p></p>");
        testHtmlTagBurner(
            "<div><p id='1'>a<p>b</p><p>c</p></p></div>",
            "<div><p id='1'>a</p><p>b</p><p>c</p></div>");
    }

    public void testLists() {
        testTagBurner(
            "<div><ul><ul><li> a <div> b </div> c </li></ul></ul></div>",
            "<ul><li><ul><li><p> a </p><p> b </p><p> c </p></li></ul></li></ul>");
        testTagBurner(
            "<div><ul>before<ul><li> a <div> b </div> c </li></ul>after</ul></div>",
            ""
                + "<ul><li>"
                + "<p>before</p>"
                + "<ul><li><p> a </p><p> b </p><p> c </p></li></ul>"
                + "<p>after</p>"
                + "</li></ul>");
        testTagBurner(
            "<div><ul><p>before</p><li> a <div> b </div> c </li>after</ul></div>",
            ""
                + "<ul>"
                + "<li><p>before</p></li>"
                + "<li><p> a </p><p> b </p><p> c </p></li>"
                + "<li><p>after</p></li>"
                + "</ul>");
        testTagBurner(
            "<div><ul><p>before</p><li> a <div> b </div> c </li>after</ul></div>",
            ""
                + "<ul>"
                + "<li><p>before</p></li>"
                + "<li><p> a </p><p> b </p><p> c </p></li>"
                + "<li><p>after</p></li>"
                + "</ul>");
        testTagBurner(
            "<div><ul>before<li> a <div> b </div> c </li>after</ul></div>",
            ""
                + "<ul>"
                + "<li><p>before</p></li>"
                + "<li><p> a </p><p> b </p><p> c </p></li>"
                + "<li><p>after</p></li>"
                + "</ul>");

        testTagBurner("<div><ul></ul></div>", "");
        testTagBurner("<div><ul>A</ul></div>", "<ul><li>A</li></ul>");
        testTagBurner("<div><ul><li>A</li></ul></div>", "<ul><li>A</li></ul>");
        testTagBurner(
            "<div><ul><li>         A          </li></ul></div>",
            "<ul><li> A </li></ul>");
        testTagBurner(
            "<div><ul><li> a <div> b </div> c </li></ul></div>",
            "<ul><li><p> a </p><p> b </p><p> c </p></li></ul>");
        testTagBurner(
            "<div>before<ul><li>         A          </li></ul>after</div>",
            "" + "<p>before</p>" + "<ul><li> A </li></ul>" + "<p>after</p>");
        testTagBurner(
            "<div><ul><ul><li> a <div> b </div> c </li></ul></ul></div>",
            "<ul><li><ul><li><p> a </p><p> b </p><p> c </p></li></ul></li></ul>");
    }

    public void testPre() {
        testTagBurner("<pre>\n"
            + " pre-\n"
            + "      -forma-\n"
            + "    -tt\n"
            + "          ed"
            + "</pre>", "<pre>\n"
            + " pre-\n"
            + "      -forma-\n"
            + "    -tt\n"
            + "          ed</pre>");
        testTagBurner("<div>before<pre>\n"
            + " pre-\n"
            + "      -forma-\n"
            + "    -tt\n"
            + "          ed"
            + "</pre>after</div>", ""
            + "<p>before</p>"
            + "<pre>\n"
            + " pre-\n"
            + "      -forma-\n"
            + "    -tt\n"
            + "          ed</pre>"
            + "<p>after</p>");

        testTagBurner(""
            + "<div>  a  <pre>\n"
            + "   b \n"
            + "  <div>    c     </div>\n"
            + "  </pre>  c \n"
            + "  </div>", "<p> a </p><pre>\n"
            + "   b \n"
            + "  <div>    c     </div>\n"
            + "  </pre><p> c </p>");
    }

    public void testTagBurner() {
        testTagBurner(
            "<div>  a  <div>   b   </div>  c   </div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner(
            "<div><span>  a  <div>   b   </div>  c   </span></div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner(
            "<div>\na\n<p>\nb\n</p>\nc\n</div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner("<div><div>   b   </div></div>", "<p> b </p>");
        testTagBurner("<div>   <div>    b    </div>   </div>", "<p> b </p>");
        testTagBurner("<div>   b   </div>", "<p> b </p>");
        testTagBurner(
            "<div>   a  <strong>  b  </strong> c  </div>",
            "<p> a <strong> b </strong> c </p>");
        testTagBurner(
            "<div>   a   b    <img /> c  </div>",
            "<p> a b <img></img> c </p>");
        testTagBurner("<div><div>b</div></div>", "<p>b</p>");

        testTagBurner("<div>   <div>    b    </div>   </div>", "<p> b </p>");
        testTagBurner(
            "<div>   <div>  <div>  <div>  <div>  b  </div>  </div>  </div>  </div>   </div>",
            "<p> b </p>");
        testTagBurner(
            "<div>    a    <span>    b     </span>    c    </div>",
            "<p> a b c </p>");
        testTagBurner(
            "<div>    a    <div>    b     </div>    c    </div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner(
            "<div><div> a  <div>    b    </div> c  </div></div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner(
            "<div> a  <div>    b    </div> c  </div>",
            "<p> a </p><p> b </p><p> c </p>");
        testTagBurner(""
            + "<div>"
            + "<div>A</div>"
            + "<div>B <p>C</p>"
            + "</div>"
            + "</div>"
            + "", "<p>A</p><p>B </p><p>C</p>");
        testTagBurner(""
            + "<div>"
            + "<div><div>A</div>"
            + "<div>B <p>C</p>"
            + "</div>"
            + "</div>"
            + "</div>"
            + "", "<p>A</p><p>B </p><p>C</p>");
        testTagBurner(""
            + "<div>"
            + "<div><div>A</div>"
            + "<div>B<p>C</p>"
            + "</div>"
            + "</div>D"
            + "</div>"
            + "", "<p>A</p><p>B</p><p>C</p><p>D</p>");
        testTagBurner(""
            + "<div>"
            + "<div>"
            + "<h1>Title</h1>"
            + "<div>Subtitle</div>"
            + "</div>"
            + "<img src='http://foo.bar/toto.jpg'/>"
            + "<div>Image description</div>"
            + "Text"
            + "</div>"
            + "", ""
            + "<h1>Title</h1>"
            + "<p>Subtitle</p>"
            + "<p><img src='http://foo.bar/toto.jpg'></img></p>"
            + "<p>Image description</p>"
            + "<p>Text</p>");
    }

    private void testTagBurner(String str, String control) {
        TagBurner burner = new TagBurner();
        IXmlFactory factory = newXmlFactory();
        IXmlElement div = factory.parse(str);
        List<IXmlNode> l = burner.handle(div, false);
        String test = XmlUtils.toString(l, true);
        if (!control.equals(test)) {
            System.out.println("============================================");
            System.out.println(control);
            System.out.println(div);
            System.out.println("-----------");
        }
        try {
            assertEquals(control, test);
        } catch (Error e) {
            e.printStackTrace();
            throw e;
        }
    }

}
