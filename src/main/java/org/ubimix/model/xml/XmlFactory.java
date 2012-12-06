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
public class XmlFactory implements IXmlFactory {

    private static IXmlFactory fInstance;

    private static IXmlParser fParser;

    public static IXmlFactory getInstance() {
        if (fInstance == null) {
            fInstance = new XmlFactory();
        }
        return fInstance;
    }

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

    /**
     * @see org.ubimix.model.xml.IXmlFactory#newCDATA(java.lang.String)
     */
    @Override
    public IXmlCDATA newCDATA(String content) {
        return new XmlCDATA(this, content);
    }

    /**
     * @see org.ubimix.model.xml.IXmlFactory#newElement(java.lang.String)
     */
    @Override
    public IXmlElement newElement(String name) {
        return new XmlElement(this, name);
    }

    /**
     * @see org.ubimix.model.xml.IXmlFactory#newText(java.lang.String)
     */
    @Override
    public IXmlText newText(String string) {
        return new XmlText(this, string);
    }

    /**
     * @see org.ubimix.model.xml.IXmlFactory#parse(org.ubimix.commons.parser.ICharStream)
     */
    @Override
    public IXmlElement parse(ICharStream stream) {
        XmlBuilder builder = new XmlBuilder(this);
        IXmlParser parser = getParser();
        parser.parse(stream, builder);
        return builder.getResult();
    }

    /**
     * @see org.ubimix.model.xml.IXmlFactory#parse(java.lang.String)
     */
    @Override
    public IXmlElement parse(String xml) {
        ICharStream stream = new CharStream(xml);
        return parse(stream);
    }

}
