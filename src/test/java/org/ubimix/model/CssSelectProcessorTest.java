package org.ubimix.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ubimix.model.selector.INodeProvider;
import org.ubimix.model.selector.IPathSelector;
import org.ubimix.model.selector.utils.CssPathSelectorBuilder;

public class CssSelectProcessorTest extends AbstractPathProcessorTest {

    public CssSelectProcessorTest(String name) {
        super(name);
    }

    protected IPathSelector getPathSelector(String str) {
        return getJsonSelector("!", str);
    }

    protected IPathSelector getJsonSelector(String nameProperty, String str) {
        CssPathSelectorBuilder builder = new CssPathSelectorBuilder(
            nameProperty);
        return builder.build(str);
    }

    private String json(String json) {
        return ModelObject.parse(json).toString();
    }

    public void testSelectMixedStructure() {

        INodeProvider provider = new INodeProvider() {
            @Override
            public Iterator<?> getChildren(Object parent) {
                List<Object> list = new ArrayList<Object>();
                ModelObject o = (ModelObject) parent;
                Set<String> keys = o.getKeys();
                for (String key : keys) {
                    ModelObject child = o.getObject(key);
                    if (child != null) {
                        list.add(child);
                    } else {
                        // Try to expand list properties
                        List<ModelObject> children = o.getList(key);
                        list.addAll(children);
                    }
                }
                return list.iterator();
            }
        };
        ModelTestFeed f = new ModelTestFeed();
        ModelObject obj = f.getFeed();
        ModelObject firstPost = f.getFirstPost();
        ModelObject secondPost = f.getSecondPost();
        ModelObject author = f.getAuthor();
        ModelObject postWithSubentreis = f.getPostWithSubentreis();
        ModelObject subEntry1 = f.getSubEntry1();
        ModelObject subEntry2 = f.getSubEntry2();
        test(
            obj,
            true,
            provider,
            getPathSelector("[title*=Post]"),
            firstPost.toString(),
            secondPost.toString());
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [content*=Just]"),
            firstPost.toString());
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [content*=long]"),
            secondPost.toString());
        test(obj, true, provider, getPathSelector("[author]"), obj.toString());
        test(
            obj,
            true,
            provider,
            getPathSelector("[author] [firstName]"),
            author.toString());

        // Selection using the string-serialized value of a property
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries*='very long post']"),
            obj.toString());
        test(obj, true, provider, getPathSelector("[entries*=post1]"));

        // Sub-entries selection
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [entries*=tempor]"),
            postWithSubentreis.toString());
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [entries] [title]"),
            subEntry1.toString(),
            subEntry2.toString());
        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [entries] [title*='aliquam commodo']"),
            subEntry2.toString());

        test(
            obj,
            true,
            provider,
            getPathSelector("[entries] [entries*=temporXX]"));
    }

    public void testSelectModel() {
        String json = "{"
            + " name: 'TopLevel',"
            + " items: ["
            + "  {"
            + "    name: 'SubitemA',"
            + "    items: [{name:'Sub-sub-item-X1'}, {name:'Sub-sub-item-X2'}]"
            + "  },"
            + "  {"
            + "    name: 'SubitemB',"
            + "    items: [{name:'X'}, {name:'Y'}]"
            + "  }"
            + " ]"
            + "}";

        ModelObject control = ModelObject.parse(json);
        IPathSelector selector = getJsonSelector("name", "TopLevel");
        testJson(json, true, selector, control.toString());

        // The "name" field is used as a tag name as well as a property
        testJson(
            json,
            true,
            getJsonSelector("name", "TopLevel [name=X]"),
            json("{name:'X'}"));
        testJson(json, true, getJsonSelector("name", "X"), json("{name:'X'}"));
        testJson(
            json,
            true,
            getJsonSelector("name", "TopLevel [name~='^.*X(\\d)?$']"),
            json("{name:'Sub-sub-item-X1'}"),
            json("{name:'Sub-sub-item-X2'}"),
            json("{name:'X'}"));
        testJson(
            json,
            true,
            getJsonSelector("name", "TopLevel SubitemA [name~='^.*X\\d$']"),
            json("{name:'Sub-sub-item-X1'}"),
            json("{name:'Sub-sub-item-X2'}"));

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
            ".xxx",
            "<p class='xxx'>first</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            ".yyy",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>");
        testXml(
            xml,
            ".yyy a",
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(xml, ".yyy a[href$=2]", "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            "a",
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(xml, ".n2 a", "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            "p",
            "<p id='123'>before</p>",
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>",
            "<p id='345'>after</p>");
        testXml(xml, "#123", "<p id='123'>before</p>");
        testXml(xml, "#345", "<p id='345'>after</p>");
        testXml(xml, "[id]", "<p id='123'>before</p>", "<p id='345'>after</p>");
        testXml(
            xml,
            "[class]",
            "<p class='xxx'>first</p>",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>",
            "<p class='xxx'>third</p>");
        testXml(
            xml,
            ".n1.yyy",
            "<p class='yyy n1'>before1 <a href='http://foo.bar1'>link1</a> after1</p>");
        testXml(
            xml,
            ".n2.yyy",
            "<p class='yyy n2'>before2 <a href='http://foo.bar2'>link2</a> after2</p>");
        testXml(
            xml,
            "[href='http://foo.bar1']",
            "<a href='http://foo.bar1'>link1</a>");
        testXml(
            xml,
            ".yyy > a",
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        testXml(xml, "[class$=yyy] > a");
        testXml(xml, "[class $= 2] > a", "<a href='http://foo.bar2'>link2</a>");
        testXml(xml, "[class=yyy] > a");
        testXml(
            xml,
            "[class*=yyy] > a",
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");
        testXml(
            xml,
            "[class^=yyy] > a",
            "<a href='http://foo.bar1'>link1</a>",
            "<a href='http://foo.bar2'>link2</a>");

        testXml(xml, ".n1 > a", "<a href='http://foo.bar1'>link1</a>");
        testXml(xml, ".n2 > a", "<a href='http://foo.bar2'>link2</a>");

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
            "table.props tr th",
            "<th>Property</th>",
            "<th>Value</th>");

    }
}
