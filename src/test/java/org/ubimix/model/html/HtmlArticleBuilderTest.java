/**
 * 
 */
package org.ubimix.model.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.ModelObject;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class HtmlArticleBuilderTest extends TestCase {

    public static Comparator<String> fStringComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return -o1.compareTo(o2);
        }
    };

    /**
     * @param name
     */
    public HtmlArticleBuilderTest(String name) {
        super(name);
    }

    public void testHierarchyBuilder() throws IOException {
        testHierarchyBuilder(""
            + "<div>"
            + "<p>First paragraph</p>"
            + "<h1>Title 1</h1>"
            + "<p>Second paragraph</p>"
            + "<h1>Title 2</h1>"
            + "<p>Third paragraph</p>"
            + "</div>", ""
            + "{\n"
            + "  \"title\":\"\",\n"
            + "  \"content\":\"<div><p>First paragraph</p></div>\",\n"
            + "  \"children\":[\n"
            + "    {\n"
            + "      \"title\":\"Title 1\",\n"
            + "      \"content\":\"<p>Second paragraph</p>\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"title\":\"Title 2\",\n"
            + "      \"content\":\"<p>Third paragraph</p>\"\n"
            + "    }\n"
            + "  ]\n"
            + "}");
        testHierarchyBuilder(""
            + "<div>"
            + "<h1>Title 1</h1>"
            + "<p>First paragraph</p>"
            + "<h1>Title 2</h1>"
            + "<p>Second paragraph</p>"
            + "<h1>Title 3</h1>"
            + "<p>Third paragraph</p>"
            + "</div>", ""
            + "{\n"
            + "  \"title\":\"\",\n"
            + "  \"content\":\"<div></div>\",\n"
            + "  \"children\":[\n"
            + "    {\n"
            + "      \"title\":\"Title 1\",\n"
            + "      \"content\":\"<p>First paragraph</p>\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"title\":\"Title 2\",\n"
            + "      \"content\":\"<p>Second paragraph</p>\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"title\":\"Title 3\",\n"
            + "      \"content\":\"<p>Third paragraph</p>\"\n"
            + "    }\n"
            + "  ]\n"
            + "}");
        testHierarchyBuilder(""
            + "<div>"
            + "<div>"
            + "<div>"
            + "<div>"
            + "<div>"
            + "<div>"
            + "<div>"
            + "<h1>Title 1</h1>"
            + "</div>"
            + "<p>First paragraph</p>"
            + "</div>"
            + "<h1>Title 2</h1>"
            + "</div>"
            + "<p>Second paragraph</p>"
            + "</div>"
            + "<h1>Title 3</h1>"
            + "</div>"
            + "<p>Third paragraph</p>"
            + "</div>"
            + "</div>", ""
            + "{\n"
            + "  \"title\":\"\",\n"
            + "  \"content\":\"<div>"
            + "<div><div><div><div><div>"
            + "<div></div>"
            + "</div></div></div></div></div></div>\",\n"
            + "  \"children\":[\n"
            + "    {\n"
            + "      \"title\":\"Title 1\",\n"
            + "      \"content\":\"<p>First paragraph</p>\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"title\":\"Title 2\",\n"
            + "      \"content\":\"<p>Second paragraph</p>\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"title\":\"Title 3\",\n"
            + "      \"content\":\"<p>Third paragraph</p>\"\n"
            + "    }\n"
            + "  ]\n"
            + "}");
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<h3>Title 1</h3>"
                + "</div>"
                + "<p>First paragraph</p>"
                + "</div>"
                + "<h2>Title 2</h2>"
                + "</div>"
                + "<p>Second paragraph</p>"
                + "</div>"
                + "<h1>Title 3</h1>"
                + "</div>"
                + "<p>Third paragraph</p>"
                + "</div>"
                + "</div>",
            ""
                + "{\n"
                + "  \"title\":\"\",\n"
                + "  \"content\":\"<div>"
                + "<div><div><div><div><div><div></div></div></div></div></div></div></div>\",\n"
                + "  \"children\":[\n"
                + "    {\n"
                + "      \"title\":\"Title 1\",\n"
                + "      \"content\":\"<p>First paragraph</p>\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"title\":\"Title 2\",\n"
                + "      \"content\":\"<p>Second paragraph</p>\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"title\":\"Title 3\",\n"
                + "      \"content\":\"<p>Third paragraph</p>\"\n"
                + "    }\n"
                + "  ]\n"
                + "}");
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<div>"
                + "<h1>Title 1</h1>"
                + "</div>"
                + "<p>First paragraph</p>"
                + "</div>"
                + "<h3>Title 2</h3>"
                + "</div>"
                + "<p>Second paragraph</p>"
                + "</div>"
                + "<h5>Title 3</h5>"
                + "</div>"
                + "<p>Third paragraph</p>"
                + "</div>"
                + "</div>",
            ""
                + "{\n"
                + "  \"title\":\"\",\n"
                + "  \"content\":\"<div>"
                + "<div><div><div><div><div><div></div></div></div></div></div></div></div>\",\n"
                + "  \"children\":[\n"
                + "    {\n"
                + "      \"title\":\"Title 1\",\n"
                + "      \"content\":\"<p>First paragraph</p>\",\n"
                + "      \"children\":[\n"
                + "        {\n"
                + "          \"title\":\"Title 2\",\n"
                + "          \"content\":\"<p>Second paragraph</p>\",\n"
                + "          \"children\":[\n"
                + "            {\n"
                + "              \"title\":\"Title 3\",\n"
                + "              \"content\":\"<p>Third paragraph</p>\"\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<h1>Title 1</h1>"
                + "<div>"
                + "<p>First paragraph</p>"
                + "<div>"
                + "<h1>Title 2</h1>"
                + "<div>"
                + "<p>Second paragraph</p>"
                + "<div>"
                + "<h1>Title 3</h1>"
                + "<div>"
                + "<p>Third paragraph</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>",
            ""
                + "{\n"
                + "  \"title\":\"\",\n"
                + "  \"content\":\"<div></div>\",\n"
                + "  \"children\":[\n"
                + "    {\n"
                + "      \"title\":\"Title 1\",\n"
                + "      \"content\":\"<div><p>First paragraph</p><div></div></div>\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"title\":\"Title 2\",\n"
                + "      \"content\":\"<div><p>Second paragraph</p><div></div></div>\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"title\":\"Title 3\",\n"
                + "      \"content\":\"<div><p>Third paragraph</p></div>\"\n"
                + "    }\n"
                + "  ]\n"
                + "}");
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<h1>Title 1</h1>"
                + "<div>"
                + "<p>First paragraph</p>"
                + "<div>"
                + "<h2>Title 2</h2>"
                + "<div>"
                + "<p>Second paragraph</p>"
                + "<div>"
                + "<h1>Title 3</h1>"
                + "<div>"
                + "<p>Third paragraph</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</div>",
            ""
                + "{\n"
                + "  \"title\":\"\",\n"
                + "  \"content\":\"<div></div>\",\n"
                + "  \"children\":[\n"
                + "    {\n"
                + "      \"title\":\"Title 1\",\n"
                + "      \"content\":\"<div><p>First paragraph</p><div></div></div>\",\n"
                + "      \"children\":[\n"
                + "        {\n"
                + "          \"title\":\"Title 2\",\n"
                + "          \"content\":\"<div><p>Second paragraph</p><div></div></div>\"\n"
                + "        }\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"title\":\"Title 3\",\n"
                + "      \"content\":\"<div><p>Third paragraph</p></div>\"\n"
                + "    }\n"
                + "  ]\n"
                + "}");
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<h1>Title 1</h1>"
                + "<p>First paragraph</p>"
                + "<div>"
                + (""
                    + "<p>Second paragraph</p>"
                    + "<div><div>"
                    + "<h3>Title 1.1</h3>"
                    + "</div></div>"
                    + "<p>Third paragraph</p>"
                    + "<p>Fourth paragraph</p>"
                    + "<h2>Title 1.2</h2>"
                    + "<p>Fifth paragraph</p>"
                    + "<div><div>"
                    + "<h5>Title 1.2.3</h5>"
                    + "</div></div>"
                    + "")
                + "</div>"
                + "<div><div>"
                + "<p>Sixth paragraph</p>"
                + "</div></div>"
                + "</div>",
            ""
                + "{\n"
                + "  'title':'',\n"
                + "  'content':'<div></div>',\n"
                + "  'children':[\n"
                + "    {\n"
                + "      'title':'Title 1',\n"
                + "      'content':'<p>First paragraph</p><div><p>Second paragraph</p><div><div></div></div></div>',\n"
                + "      'children':[\n"
                + "        {\n"
                + "          'title':'Title 1.1',\n"
                + "          'content':'<p>Third paragraph</p><p>Fourth paragraph</p>'\n"
                + "        },\n"
                + "        {\n"
                + "          'title':'Title 1.2',\n"
                + "          'content':'<p>Fifth paragraph</p><div><div></div></div>',\n"
                + "          'children':[\n"
                + "            {\n"
                + "              'title':'Title 1.2.3',\n"
                + "              'content':'<div><div><p>Sixth paragraph</p></div></div>'\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");

        testHierarchyBuilder(""
            + "<div>"
            + "<ul>"
            + "<li>item 1</li>"
            + "<li>item 2"
            + "<ul>"
            + "<li>A</li>"
            + "<li>B</li>"
            + "<li>C</li>"
            + "</ul>"
            + "</li>"
            + "<li>item 3</li>"
            + "</ul>"
            + "</div>", ""
            + "{\n"
            + "  \"title\":\"\",\n"
            + "  \"content\":\""
            + "<div>"
            + "<ul>"
            + "<li>item 1</li>"
            + "<li>item 2<ul><li>A</li><li>B</li><li>C</li></ul></li>"
            + "<li>item 3</li>"
            + "</ul>"
            + "</div>\"\n"
            + "}");
        testHierarchyBuilder(""
            + "<div>"
            + "<h5>Title 1</h5>"
            + "<ul>"
            + "<li>item 1</li>"
            + "<li>item 2"
            + "<ul>"
            + "<li>A</li>"
            + "<li>B</li>"
            + "<li>C</li>"
            + "</ul>"
            + "</li>"
            + "<li>item 3</li>"
            + "</ul>"
            + "</div>", ""
            + "{\n"
            + "  'title':'',\n"
            + "  'content':'<div></div>',\n"
            + "  'children': [\n"
            + "    {\n"
            + "     'title':'Title 1',\n"
            + "     'content':'"
            + "<ul>"
            + "<li>item 1</li>"
            + "<li>item 2<ul><li>A</li><li>B</li><li>C</li></ul></li>"
            + "<li>item 3</li>"
            + "</ul>'\n"
            + "     }\n"
            + "   ]\n"
            + "}");

        // Titles in tables and lists are not handled as new sections
        testHierarchyBuilder(
            ""
                + "<div>"
                + "<h5>Title 1</h5>"
                + "<ul>"
                + "<li>item 1</li>"
                + "<li>item 2"
                + "<h2>Title 2</h2>"
                + "<ul>"
                + "<li>A</li>"
                + "<li>B</li>"
                + "<li>C</li>"
                + "</ul>"
                + "<p>text</p>"
                + "</li>"
                + "<li>item 3</li>"
                + "</ul>"
                + "</div>",
            ""
                + "{\n"
                + "  \"title\":\"\",\n"
                + "  \"content\":\"<div></div>\",\n"
                + "  \"children\":[\n"
                + "    {\n"
                + "      \"title\":\"Title 1\",\n"
                + "      \"content\":\"<ul><li>item 1</li><li>item 2<h2>Title 2</h2><ul><li>A</li><li>B</li><li>C</li></ul><p>text</p></li><li>item 3</li></ul>\"\n"
                + "    }\n"
                + "  ]\n"
                + "}");
    }

    protected void testHierarchyBuilder(String xml, String control)
        throws IOException {
        HtmlArticleBuilder builder = new HtmlArticleBuilder();
        XmlElement w = HtmlDocument.parseFragment(xml);
        HtmlArticle article = builder.buildArticle(w);
        ModelObject o = toModelObject(article);
        ModelObject controlJson = ModelObject.parse(control);
        assertEquals(controlJson + "", o + "");
        // ModelObject x = ModelObject.from(article);
        // System.out.println(x);
    }

    private void toModel(ModelObject obj, HtmlArticle article) {
        obj.setValue("title", article.getTitle());
        XmlElement content = article.getSection();
        obj.setValue("content", content.toString(false, false));
        List<ModelObject> children = new ArrayList<ModelObject>();
        for (HtmlArticle entry : article.getArticles()) {
            ModelObject childObj = new ModelObject();
            children.add(childObj);
            toModel(childObj, entry);
        }
        if (!children.isEmpty()) {
            obj.setValues("children", children);
        }
    }

    public ModelObject toModelObject(HtmlArticle article) {
        ModelObject o = new ModelObject();
        toModel(o, article);
        return o;
    }

}
