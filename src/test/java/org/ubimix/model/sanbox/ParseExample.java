/**
 * 
 */
package org.ubimix.model.sanbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.html.XmlSelect;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class ParseExample {

    public static XmlElement load(InputStream input) throws IOException {
        String str = null;
        try {
            byte[] buf = new byte[1024 * 10];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            str = new String(out.toByteArray());
        } finally {
            input.close();
        }

        XmlElement doc = null;
        int count = 50;
        long delta = 0;
        for (int i = 0; i < count + 1; i++) {
            long start = System.currentTimeMillis();
            doc = HtmlDocument.parse(str);
            long stop = System.currentTimeMillis();
            if (i > 0) {
                delta += (stop - start);
            }
            System.out.println("Parsed in " + (stop - start) + "ms");
        }
        System.out.println("Parsing average time: "
            + (delta / count)
            + "ms. The first run is not counted.");
        return doc;
    }

    public static XmlElement load(String url) throws IOException {
        InputStream input = new URL(url).openStream();
        return load(input);
    }

    /**
     * @param args
     * @throws IOException
     * @throws MalformedURLException
     */
    public static void main(String[] args)
        throws MalformedURLException,
        IOException {
        // site1();
        // site2();
        site3();
        // site4();
        // site5();
    }

    private static void printDocument(String url) throws IOException {
        XmlElement e = load(url);
        System.out.println(e);
    }

    public static void site1() throws IOException {
        String url = "http://tema.livejournal.com/";
        XmlElement doc = load(url);
        System.out.println(doc);

        List<XmlElement> list = XmlSelect.on(doc).selectAll("tr[valign=TOP]");
        for (XmlElement entry : list) {
            XmlElement div = XmlSelect.on(entry).select(
                "td > div[style='text-align:left']");
            XmlElement a = XmlSelect.on(div).select("a");
            if (a == null) {
                continue;
            }
            a.remove();
            System.out.println("===============================");
            System.out.println("Title : " + a.toText());
            System.out.println("Content: " + div);
        }
    }

    public static void site2() throws IOException {
        String url = "http://www.ardeche.cci.fr/";
        XmlElement doc = load(url);
        List<XmlElement> list = XmlSelect.on(doc).selectAll(
            "#list_actu_in ul > li");
        for (XmlElement entry : list) {
            XmlSelect s = XmlSelect.on(entry);
            XmlElement a = s.select("a");
            String href = a.getAttribute("href");
            a.remove();
            XmlElement h3 = s.select("h3");
            String title = h3.toText();
            h3.remove();
            String content = entry
                .toString(true, false)
                .replaceAll("<br></br>", "")
                .replaceAll("[\\s\\r\\n]+", " ")
                .trim();
            System.out.println("===============================");
            System.out.println("URL: " + href);
            System.out.println("Title: " + title);
            System.out.println("Content: ");
            System.out.println(content);
        }
    }

    public static void site3() throws IOException {
        File file = new File(
            "/home/kotelnikov/dev/workspaces/ubimix/org.ubimix.model/tmp/WikipediaFrance.html");
        XmlElement doc = load(new FileInputStream(file));

        XmlElement main = XmlSelect.on(doc).select("#mw-content-text");

        List<XmlElement> headers = XmlSelect.on(main).selectAll("[umx|tag^=h]");

        for (XmlElement header : headers) {
            // Element span = header.select("span[id]").first();
            XmlElement span = XmlSelect.on(header).select("[id]");
            if (span != null) {
                List<XmlNode> children = span.getChildren();
                header.removeChildren();
                int len = children.size();
                for (int i = 0; i < len; i++) {
                    XmlNode node = children.get(i);
                    header.addChild(node);
                }
                header.setAttribute("id", span.getAttribute("id"));
            }
            System.out.println(header);
        }

        List<XmlElement> boxes = XmlSelect.on(main).selectAll("div.thumb");
        for (XmlElement box : boxes) {
            List<XmlElement> divs = XmlSelect.on(box).selectAll("div.magnify");
            for (XmlElement div : divs) {
                div.remove();
            }
            System.out.println("==============");
            System.out.println(box);
        }
    }

    public static void site4() throws IOException {
        printDocument("http://permalink.gmane.org/gmane.comp.programming.swig/4870");
    }

    public static void site5() throws IOException {
        String url = "";
        url = "http://www.markboulton.co.uk/journal/a-richer-canvas";
        url = "http://www.theverge.com/2012/4/3/2922589/read-it-laters-top-saved-domain-is-youtube-so-thats-ironic";
        url = "http://www.serene-naturist.com";
        printDocument(url);
    }

    public static void site6() throws IOException {
        String url = "";
        url = "http://oregonstate.edu/instruct/phl302/texts/hobbes/leviathan-c.html";
        printDocument(url);
    }

    /**
     * 
     */
    public ParseExample() {
        // TODO Auto-generated constructor stub
    }

}
