/**
 * 
 */
package org.ubimix.model.cleaner;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XMLTokenizer;
import org.ubimix.commons.parser.xml.utils.XmlSerializer;

/**
 * @author kotelnikov
 */
public class HtmlParserExample {

    public static void main(String[] args) throws IOException {
        new HtmlParserExample().parse();
        new HtmlParserExample().tokenize();
    }

    protected ICharStream newStream() throws IOException {
        String file = "./tmp/WikipediaFrance.html";
        String str = read(file);
        return newStream(str);
    }

    protected ICharStream newStream(String str) {
        ICharStream stream;
        // stream = new StringBufferCharStream(str);
        stream = new CharStream(str);
        return stream;
    }

    private void parse() throws IOException {
        String file = "./tmp/WikipediaFrance.html";
        String str = read(file);
        IXmlParser parser = new HtmlParser();
        long start = System.currentTimeMillis();

        XmlSerializer listener = null;
        int count = 30;
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + " parse iteration...");
            listener = new XmlSerializer();
            listener.setSortAttributes(false);
            ICharStream stream = newStream(str);
            parser.parse(stream, listener);
        }

        long stop = System.currentTimeMillis();
        System.out.println("Parsed in " + ((stop - start) / count) + "ms");

        write("./tmp/WikipediaFrance.xml", listener.toString());
    }

    private String read(String file) throws IOException {
        FileReader reader = new FileReader(file);
        try {
            StringBuilder buf = new StringBuilder();
            char[] array = new char[1024 * 10];
            int len;
            while ((len = reader.read(array)) > 0) {
                buf.append(array, 0, len);
            }
            return buf.toString();
        } finally {
            reader.close();
        }
    }

    private void tokenize() throws IOException {
        String file = "./tmp/WikipediaFrance.html";
        String str = read(file);
        ICharStream stream = newStream(str);
        XMLTokenizer tokenizer = XMLTokenizer.getFullXMLTokenizer();
        long start = System.currentTimeMillis();
        StreamToken token = tokenizer.read(stream);
        StringBuilder buf = new StringBuilder();
        while (token != null) {
            buf.append(token.getText());
            token = tokenizer.read(stream);
        }
        long stop = System.currentTimeMillis();
        System.out.println("Tokenized in " + (stop - start) + "ms");
        write("./tmp/WikipediaFrance.txt", buf.toString());

        if (!str.equals(buf.toString())) {
            throw new IllegalStateException();
        }
    }

    protected void write(String file, String str) throws IOException {
        FileWriter w = new FileWriter(file);
        w.write(str);
        w.close();
    }

}
