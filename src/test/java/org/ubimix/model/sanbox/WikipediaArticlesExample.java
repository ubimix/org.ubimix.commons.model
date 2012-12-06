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
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.ubimix.commons.parser.UnboundedCharStream;
import org.ubimix.commons.parser.stream.StreamCharLoader;
import org.ubimix.model.cleaner.TagBurner;
import org.ubimix.model.html.HtmlArticle;
import org.ubimix.model.html.HtmlArticleBuilder;
import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.XmlUtils;

/**
 * @author kotelnikov
 */
public class WikipediaArticlesExample {

    public interface IPageSelector {

        String getRootUrl();

        IXmlElement selectArticleContent(IXmlElement article);

        List<IXmlElement> selectArticles(IXmlElement mainBlock);

        String selectArticleTitle(IXmlElement article);

        IXmlElement selectMainBlock(IXmlElement document);

    }

    public static class PageSelector implements IPageSelector {

        private String fArticleContentSelect;

        private String fArticleSelect;

        private String fArticleTitleSelect;

        private String fMainContentSelect;

        private String fRootUrl;

        public String getArticleContentSelect() {
            return fArticleContentSelect;
        }

        public String getArticleSelect() {
            return fArticleSelect;
        }

        public String getArticleTitleSelect() {
            return fArticleTitleSelect;
        }

        public String getMainContentSelect() {
            return fMainContentSelect;
        }

        @Override
        public String getRootUrl() {
            return fRootUrl;
        }

        @Override
        public IXmlElement selectArticleContent(IXmlElement article) {
            IXmlElement result = null;
            if (fArticleContentSelect != null) {
                result = XmlUtils.select(article, fArticleContentSelect);
            } else {
                result = article;
            }
            return result;
        }

        @Override
        public List<IXmlElement> selectArticles(IXmlElement mainBlock) {
            List<IXmlElement> result = null;
            if (fArticleSelect == null) {
                result = Arrays.asList(mainBlock);
            } else {
                result = XmlUtils.selectAll(mainBlock, fArticleSelect);
            }
            return result;
        }

        @Override
        public String selectArticleTitle(IXmlElement article) {
            String result = null;
            IXmlElement titleElement = fArticleTitleSelect != null ? XmlUtils
                .select(article, fArticleTitleSelect) : null;
            if (titleElement != null) {
                result = XmlUtils.toText(titleElement);
                titleElement.remove();
            }
            return result;
        }

        @Override
        public IXmlElement selectMainBlock(IXmlElement document) {
            if (fMainContentSelect == null) {
                fMainContentSelect = "body";
            }
            return XmlUtils.select(document, fMainContentSelect);
        }

        public PageSelector setArticleContentSelect(String articleContentSelect) {
            this.fArticleContentSelect = articleContentSelect;
            return this;
        }

        public PageSelector setArticleSelect(String articleSelect) {
            this.fArticleSelect = articleSelect;
            return this;
        }

        public PageSelector setArticleTitleSelect(String articleTitleSelect) {
            this.fArticleTitleSelect = articleTitleSelect;
            return this;
        }

        public PageSelector setMainContentSelect(String mainContentSelect) {
            this.fMainContentSelect = mainContentSelect;
            return this;
        }

        public PageSelector setRootUrl(String url) {
            this.fRootUrl = url;
            return this;
        }
    }

    public static void main(String[] args) throws IOException {
        WikipediaArticlesExample example = new WikipediaArticlesExample();

        PageSelector s;
        String url = "http://fr.wikipedia.org/wiki/Pablo_Picasso";
        // url = "http://fr.wikipedia.org/wiki/France";
        s = new PageSelector().setRootUrl(url).setMainContentSelect(
            "#mw-content-text");

        // s = new PageSelector()
        // .setRootUrl(
        // "http://www.liberation.fr/economie/2012/10/31/protection-sociale-a-la-sante-du-travail_857463")
        // .setMainContentSelect(".article")
        // .setArticleContentSelect(".object-content")
        // .setArticleTitleSelect(".object-header h1");
        example.execute(s);
    }

    public void execute(IPageSelector selector) throws IOException {
        TagBurner burner = new TagBurner();

        IXmlElement doc = load(selector.getRootUrl());
        IXmlElement mainBlock = selector.selectMainBlock(doc);

        List<IXmlElement> articleBlocks = selector.selectArticles(mainBlock);
        IXmlFactory factory = doc.getFactory();
        HtmlArticle topArticle = new HtmlArticle(factory);
        for (IXmlElement articleBlock : articleBlocks) {
            HtmlArticle article = new HtmlArticle(factory);

            String title = selector.selectArticleTitle(articleBlock);
            article.setTitle(title);

            IXmlElement articleContentBlock = selector
                .selectArticleContent(articleBlock);

            HtmlArticleBuilder builder = new HtmlArticleBuilder();
            builder.buildArticle(articleContentBlock, article, burner);
            topArticle.addArticle(article);
        }
        printArticle(topArticle, 0);

        println("=============================================================");
        println(topArticle);
        File file = new File("./tmp/output.html");
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(topArticle.toString());
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
