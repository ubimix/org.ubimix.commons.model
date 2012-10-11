package org.ubimix.model.path;

import java.util.ArrayList;

import org.ubimix.model.ModelObject;
import org.ubimix.model.path.utils.CssPathSelectorBuilder;
import org.ubimix.model.path.utils.MapNodeSelector;
import org.ubimix.model.path.utils.PathSelector;

public class CssSelectProcessorTest extends AbstractPathProcessorTest {

    public CssSelectProcessorTest(String name) {
        super(name);
    }

    protected IPathSelector getPathSelector(String str) {
        CssPathSelectorBuilder builder = new CssPathSelectorBuilder();
        return builder.build(str);
    }

    private String json(String json) {
        return new ModelObject(json).toString();
    }

    public void testSelectModel() {
        String json = "{"
            + " title: 'Top Level',"
            + " items: ["
            + "  {"
            + "    title: 'Subitem A',"
            + "    items: [{title:'Sub-sub item X1'}, {title:'Sub-sub item X2'}]"
            + "  },"
            + "  {"
            + "    title: 'Subitem B',"
            + "    items: [{title:'X'}, {title:'Y'}]"
            + "  }"
            + " ]"
            + "}";

        {
            ArrayList<INodeSelector> list = new ArrayList<INodeSelector>();
            list.add(MapNodeSelector.getTagSelector("title", "~", "Top Level"));
            // list.add(MapNodeSelector.getTagSelector("title", "$", "X"));
            PathSelector selector = new PathSelector(list);
            ModelObject control = new ModelObject(json);
            testJson(json, true, selector, control.toString());
        }
        {
            ArrayList<INodeSelector> list = new ArrayList<INodeSelector>();
            list.add(MapNodeSelector.getTagSelector("title", "~", "Top Level"));
            list.add(MapNodeSelector.getTagSelector("title", "$", "X"));
            PathSelector selector = new PathSelector(list);
            testJson(json, true, selector, json("{title:'X'}"));
        }
        {
            ArrayList<INodeSelector> list = new ArrayList<INodeSelector>();
            list.add(MapNodeSelector.getTagSelector("title", "~", "Top Level"));
            list.add(MapNodeSelector.getTagSelector("title", "~", "X"));
            PathSelector selector = new PathSelector(list);
            testJson(
                json,
                true,
                selector,
                json("{title:'Sub-sub item X1'}"),
                json("{title:'Sub-sub item X2'}"),
                json("{title:'X'}"));
        }

    }

    public void testSelectXml() throws Exception {
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
        testXml(
            xml,
            getPathSelector(".xxx"),
            "<p class='xxx'>first</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            getPathSelector(".yyy a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            getPathSelector(".yyy a[href$=2]"),
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            getPathSelector("a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            getPathSelector(".n2 a"),
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            getPathSelector("p"),
            "<p id='123'>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>",
            "<p id='345'>after</p>");
        testXml(xml, getPathSelector("#123"), "<p id='123'>before</p>");
        testXml(xml, getPathSelector("#345"), "<p id='345'>after</p>");
        testXml(
            xml,
            getPathSelector("[id]"),
            "<p id='123'>before</p>",
            "<p id='345'>after</p>");
        testXml(
            xml,
            getPathSelector("[class]"),
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            getPathSelector(".n1.yyy"),
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>");
        testXml(
            xml,
            getPathSelector(".n2.yyy"),
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>");
        testXml(
            xml,
            getPathSelector("[href='http://foo.bar1']"),
            "<a href='http://foo.bar1'>link1</a>");
        testXml(
            xml,
            getPathSelector(".yyy > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        testXml(xml, getPathSelector("[class$=yyy] > a"));
        testXml(
            xml,
            getPathSelector("[class $= 2] > a"),
            "<a href='http://foo.bar2'>link2</a>");
        testXml(xml, getPathSelector("[class=yyy] > a"));
        testXml(
            xml,
            getPathSelector("[class~=yyy] > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            getPathSelector("[class^=yyy] > a"),
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        testXml(
            xml,
            getPathSelector(".n1 > a"),
            "<a href='http://foo.bar1'>link1</a>");
        testXml(
            xml,
            getPathSelector(".n2 > a"),
            "<a href='http://foo.bar2'>link2</a>");

        testXml(
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
