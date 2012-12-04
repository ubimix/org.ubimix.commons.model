package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlCDATA extends XmlText {

    public static final String CDATA_PREFIX = "<![CDATA[";

    public static final String CDATA_SUFFIX = "]]>";

    public static boolean isCDATA(String str) {
        return str != null
            && str.startsWith(CDATA_PREFIX)
            && str.endsWith(CDATA_SUFFIX);
    }

    public static String unwrapCDATA(String str) {
        if (str.startsWith(CDATA_PREFIX)) {
            str = str.substring(CDATA_PREFIX.length());
        }
        if (str.endsWith(CDATA_SUFFIX)) {
            str = str.substring(0, str.length() - CDATA_SUFFIX.length());
        }
        return str;
    }

    protected XmlCDATA(String content) {
        super(content);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public XmlCDATA newCopy(boolean depth) {
        return new XmlCDATA(getContent());
    }

    @Override
    protected String unwrapContent(String str) {
        return unwrapCDATA(str);
    }

}
