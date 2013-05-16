/**
 * 
 */
package org.ubimix.model.sanbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.ubimix.commons.parser.UnboundedCharStream;
import org.ubimix.commons.parser.stream.StreamCharLoader;
import org.ubimix.model.cleaner.TagBurner;
import org.ubimix.model.html.HtmlArticle;
import org.ubimix.model.html.HtmlArticleBuilder;
import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.html.StructuredPropertiesTable;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.XmlUtils;

/**
 * @author kotelnikov
 */
public class XWikiArticleExample {

    public static void main(String[] args) throws IOException {
        XWikiArticleExample example = new XWikiArticleExample();

        example.execute("http://sonarmein.ubimix.org/xwiki/bin/view/wiki/Test");
    }

    public void execute(String url) throws IOException {
        TagBurner burner = new TagBurner();

        IXmlElement doc = load(url);
        IXmlElement mainBlock = XmlUtils.select(doc, "#mainContentArea");

        String title = XmlUtils.select(mainBlock, "#document-title").toString();

        IXmlFactory factory = doc.getFactory();
        HtmlArticle article = new HtmlArticle(factory);

        HtmlArticleBuilder builder = new HtmlArticleBuilder();
        builder.buildArticle(mainBlock, article, burner);

        println(title);
        println("=============================================================");
        printArticle(article, 3);

        File file = new File("./tmp/output.xml");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(article.toString());
        writer.close();

    }

    private String getShift(int level) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i <= level; i++) {
            buf.append("     ");
        }
        return buf.toString();
    }

    public IXmlElement load(String url) throws IOException {
        IXmlElement doc = null;
        println("Loading '" + url + "'...");
        URLConnection connection = new URL(url).openConnection();
        String encoding = connection.getContentEncoding();
        InputStream input = connection.getInputStream();
        try {
            if (encoding != null && "gzip".equalsIgnoreCase(encoding)) {
                input = new GZIPInputStream(input);
            }
            long start = System.currentTimeMillis();
            UnboundedCharStream.ICharLoader loader = new StreamCharLoader(input);
            UnboundedCharStream stream = new UnboundedCharStream(loader);
            doc = HtmlDocument.parse(stream);
            long stop = System.currentTimeMillis();
            println("Page was successfully loaded and parsed in "
                + (stop - start)
                + "ms.");
        } finally {
            input.close();
        }
        return doc;
    }

    private void printArticle(HtmlArticle article, int level) {
        String shift = getShift(level);
        println(shift + "-----------------------------------------");
        String title = article.getTitle().trim();
        println(shift + title);

        IXmlElement e = article.select("section table");
        if (e != null) {
            StructuredPropertiesTable table = new StructuredPropertiesTable(
                e,
                Value.FACTORY);
            if (table != null) {
                table.getElement().remove();
                Set<String> names = table.getPropertyNames();
                for (String name : names) {
                    Value value = table.getProperty(name);
                    println(name + " - " + value != null
                        ? value.getAsText()
                        : null);
                }
                // int len = table.getRowNumber();
                // for (int i = 0; i < len; i++) {
                // println(shift
                // + table.getCell(i, 0).getAsText()
                // + " - "
                // + table.getCell(i, 1).getAsText());
                // }
            }
        }
        // content = article.getContent();
        String content = article.getContentAsHtml();
        content = content.replaceAll("(\\\\r\\\\n|\\n)", " ");
        if (!"".equals(content)) {
            println(shift + content);
        }
        for (HtmlArticle a : article.getArticles()) {
            printArticle(a, level + 1);
        }
    }

    private void printArticle1(HtmlArticle article, int level) {
        String shift = getShift(level);
        println(shift + "-----------------------------------------");
        String title = article.getTitle().trim();
        println(shift + title);

        String content = article.getContentAsHtml();
        content = content.replaceAll("(\\\\r\\\\n|\\n)", " ");
        if (!"".equals(content)) {
            println(shift + content);
        }
        for (HtmlArticle a : article.getArticles()) {
            printArticle(a, level + 1);
        }
    }

    private void println(Object o) {
        System.out.println(o);
    }

}
