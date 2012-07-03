package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlCDATA extends XmlText {

    public static final String CDATA_PREFIX = "!";

    public XmlCDATA(String content) {
        this(null, content);
    }

    public XmlCDATA(XmlElement parent, String object) {
        super(parent, object);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected String unwrapContent(String str) {
        if (str.startsWith(CDATA_PREFIX)) {
            str = str.substring(CDATA_PREFIX.length());
        }
        return str;
    }

}
