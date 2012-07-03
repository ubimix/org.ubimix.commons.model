/**
 * 
 */
package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlText extends XmlNode {

    public XmlText(String content) {
        this(null, content);
    }

    public XmlText(XmlElement parent, String content) {
        super(parent, content);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    public String getContent() {
        String str = (String) getObject();
        str = unwrapContent(str);
        return str;
    }

    @Override
    protected Object newObject() {
        return "";
    }

    protected String unwrapContent(String str) {
        return str;
    }

}
