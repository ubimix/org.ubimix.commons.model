/**
 * 
 */
package org.ubimix.model.cleaner;

import junit.framework.TestCase;

import org.ubimix.model.cleaner.TagBalancer.IListener;

/**
 * @author kotelnikov
 */
public class TagHierarchyTest extends TestCase {

    /**
     * @param name
     */
    public TagHierarchyTest(String name) {
        super(name);
    }

    public void test() {
        test(
            "div",
            "a",
            "p",
            "<html><body><div><a></a><p></p></div></body></html>");
        test(
            "div",
            "p",
            "li",
            "<html><body><div><p></p><ul><li></li></ul></div></body></html>");
        test("div", "p", "li", "span", "li", "span", "<html><body>"
            + "<div>"
            + "<p></p>"
            + "<ul>"
            + "<li><span></span></li>"
            + "<li><span></span></li>"
            + "</ul>"
            + "</div>"
            + "</body></html>");
        test("div", "td", "span", "<html><body>"
            + "<div><table><tr><td><span></span></td></tr></table></div>"
            + "</body></html>");
        test("div", "td", "span", "td", "a", ""
            + "<html><body>"
            + "<div>"
            + "<table><tr>"
            + "<td><span></span></td>"
            + "<td><a></a></td>"
            + "</tr></table>"
            + "</div>"
            + "</body></html>");
        test("div", "td", "span", "td", "a", ""
            + "<html><body>"
            + "<div><table>"
            + "<tr>"
            + "<td><span></span></td>"
            + "<td><a></a></td>"
            + "</tr>"
            + "</table></div>"
            + "</body></html>");
        test("div", "td", "span", "tr", "a", "<html><body>"
            + "<div><table>"
            + "<tr><td><span></span></td></tr>"
            + "<tr></tr></table><a></a></div>"
            + "</body></html>");
        test(
            "div",
            "p",
            "span",
            "/p",
            "a",
            "<html><body><div><p><span></span></p><a></a></div></body></html>");
        test("div", "p", "/p", "<html><body><div><p></p></div></body></html>");
        test("div", "td", "span", "/tr", "td", "a", ""
            + "<html><body>"
            + "<div>"
            + "<table>"
            + "<tr><td><span></span></td></tr>"
            + "<tr><td><a></a></td></tr>"
            + "</table>"
            + "</div></body></html>");
        test("div", "td", "span", "/table", "td", "a", "/table", "span", ""
            + "<html><body>"
            + "<div>"
            + "<table><tr><td><span></span></td></tr></table>"
            + "<table><tr><td><a></a></td></tr></table>"
            + "<span></span>"
            + "</div></body></html>");
        test(
            "div",
            "td",
            "span",
            "/table",
            "td",
            "a",
            "/tr",
            "b",
            "/table",
            "span",
            ""
                + "<html><body>"
                + "<div>"
                + "<table><tr><td><span></span></td></tr></table>"
                + "<table><tr><td><a></a></td></tr></table>"
                + "<b><span></span></b>"
                + "</div></body></html>");
        test("div", "span", "xxx", "a", ""
            + "<html><body>"
            + "<div><span><xxx><a></a></xxx></span></div></body></html>");
        test(
            "div",
            "span",
            "xxx",
            "yyy",
            "a",
            ""
                + "<html><body>"
                + "<div><span><xxx><yyy><a></a></yyy></xxx></span></div></body></html>");
        test("div", "xxx", "yyy", "a", "/yyy", "b", ""
            + "<html><body>"
            + "<div><xxx><yyy><a></a></yyy><b></b></xxx></div></body></html>");
        test("div", "span", "xxx", "a", "yyy", "/xxx", "strong", ""
            + "<html><body>"
            + "<div>"
            + "<span><xxx><a><yyy></yyy></a></xxx>"
            + "<strong></strong></span>"
            + "</div></body></html>");
        test("div", "tr", "xxx", "td", "a", "/xxx", "strong", ""
            + "<html><body>"
            + "<div>"
            + "<table>"
            + "<tr>"
            + "<xxx>"
            + "<td><a></a></td>"
            + "</xxx>"
            + "</tr>"
            + "</table>"
            + "<strong></strong>"
            + "</div></body></html>");

        test("div", "li", "*text", "<html><body>"
            + "<div><ul><li><text></text></li></ul></div></body></html>");

    }

    private void test(String... strs) {
        if (strs == null || strs.length == 0) {
            return;
        }
        int tagCount = strs.length - 1;
        String control = strs[tagCount];

        HtmlTagDescriptor descr = new HtmlTagDescriptor();
        final StringBuilder buf = new StringBuilder();
        TagBalancer balancer = new TagBalancer(descr, new IListener() {

            private int fDepth;

            @Override
            public void begin(String tag) {
                println("<" + tag + ">");
                fDepth++;
            }

            @Override
            public void end(String tag) {
                fDepth--;
                println("</" + tag + ">");
            }

            protected void print(String string) {
                System.out.print(string);
            }

            protected void println(String string) {
                buf.append(string);
                for (int i = 0; i < fDepth; i++) {
                    print("  ");
                }
                print(string);
                print("\n");

            }
        });
        for (int i = 0; i < tagCount; i++) {
            String tag = strs[i];
            if (tag.startsWith("*")) {
                tag = tag.substring(1);
                balancer.begin(tag);
                balancer.end(tag);
            } else if (tag.startsWith("/")) {
                tag = tag.substring(1);
                balancer.end(tag);
            } else {
                balancer.begin(tag);
            }
        }
        balancer.finish();
        String test = buf.toString();
        assertEquals(control, test);
    }
}
