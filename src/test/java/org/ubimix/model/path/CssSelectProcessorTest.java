package org.ubimix.model.path;

import org.ubimix.model.path.xml.CssPathSelector;

public class CssSelectProcessorTest extends AbstractPathProcessorTest {

    public CssSelectProcessorTest(String name) {
        super(name);
    }

    protected IPathSelector getPathSelector(String str) {
        return new CssPathSelector(str);
    }

    public void test() throws Exception {
        String xml = ""
            + "<html>"
            + "<title />"
            + "<body>"
            + "<div>"
            + "<p id='123'>before</p>"
            + "<p class='xxx'>first</p>"
            + "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>"
            + "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>"
            + "<p class='xxx'>third</p>"
            + "<p id='345'>after</p>"
            + "</div>"
            + "</body>"
            + "</html>";
        test(
            xml,
            getPathSelector(".xxx"),
            "<p class='xxx'>first</p>",
            "<p class='xxx'>third</p>");
        test(
            xml,
            getPathSelector(".yyy a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        test(
            xml,
            getPathSelector(".yyy a[href$=2]"),
            "<a href='http://foo.bar2'>link2</a>");
        test(
            xml,
            getPathSelector("a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        test(
            xml,
            getPathSelector(".n2 a"),
            "<a href='http://foo.bar2'>link2</a>");
        test(
            xml,
            getPathSelector("p"),
            "<p id='123'>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>",
            "<p id='345'>after</p>");
        test(xml, getPathSelector("#123"), "<p id='123'>before</p>");
        test(xml, getPathSelector("#345"), "<p id='345'>after</p>");
        test(
            xml,
            getPathSelector("[id]"),
            "<p id='123'>before</p>",
            "<p id='345'>after</p>");
        test(
            xml,
            getPathSelector("[class]"),
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>");
        test(
            xml,
            getPathSelector(".n1.yyy"),
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>");
        test(
            xml,
            getPathSelector(".n2.yyy"),
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>");
        test(
            xml,
            getPathSelector("[href='http://foo.bar1']"),
            "<a href='http://foo.bar1'>link1</a>");
        test(
            xml,
            getPathSelector(".yyy > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        test(xml, getPathSelector("[class$=yyy] > a"));
        test(
            xml,
            getPathSelector("[class $= 2] > a"),
            "<a href='http://foo.bar2'>link2</a>");
        test(xml, getPathSelector("[class=yyy] > a"));
        test(
            xml,
            getPathSelector("[class~=yyy] > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        test(
            xml,
            getPathSelector("[class^=yyy] > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        test(
            xml,
            getPathSelector(".n1 > a"),
            "<a href='http://foo.bar1'>link1</a>");
        test(
            xml,
            getPathSelector(".n2 > a"),
            "<a href='http://foo.bar2'>link2</a>");

        test(
            ""
                + "<div>"
                + "<p>blah-blah</p>"
                + "<div>"
                + "<table><tr><td>First table</td></tr></table>"
                + "<table class='props'>"
                + "  <tr><th>Property</th><th>Value</th></tr>"
                + "  <tr><td>property1</td><td>value1</td></tr>"
                + "</table>"
                + "</div>"
                + "<table><tr><td>Last table</td></tr></table>"
                + "<p>mlj</p>"
                + "</div>",
            getPathSelector("table.props tr th"),
            "<th>Property</th>",
            "<th>Value</th>");
    }
}
