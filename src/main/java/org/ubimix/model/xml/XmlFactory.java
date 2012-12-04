/**
 * 
 */
package org.ubimix.model.xml;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ITokenizer;
import org.ubimix.commons.parser.html.XHTMLEntities;
import org.ubimix.commons.parser.xml.EntityFactory;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.commons.parser.xml.XMLTokenizer;
import org.ubimix.commons.parser.xml.XmlParser;

/**
 * @author kotelnikov
 */
public class XmlFactory {

    private static IXmlParser fParser;

    public static IXmlParser getParser() {
        if (fParser == null) {
            EntityFactory entityFactory = new EntityFactory();
            new XHTMLEntities(entityFactory);
            ITokenizer tokenizer = XMLTokenizer
                .getFullXMLTokenizer(entityFactory);
            fParser = new XmlParser(tokenizer);
        }
        return fParser;
    }

    public static void setParser(IXmlParser parser) {
        fParser = parser;
    }

    /**
     * 
     */
    public XmlFactory() {
    }

    public XmlCDATA newCDATA(String content) {
        return new XmlCDATA(content);
    }

    public XmlElement newElement(String name) {
        return new XmlElement(name);
    }

    public XmlText newText(String string) {
        return new XmlText(string);
    }

    public XmlElement parse(ICharStream stream) {
        XmlBuilder builder = new XmlBuilder(this);
        IXmlParser parser = getParser();
        parser.parse(stream, builder);
        return builder.getResult();
    }

    public XmlElement parse(String xml) {
        ICharStream stream = new CharStream(xml);
        return parse(stream);
    }

}
