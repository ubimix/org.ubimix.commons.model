/**
 * 
 */
package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlText extends XmlNode {

    protected XmlText(String content) {
        super(null, content);
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
    public XmlText newCopy(boolean depth) {
        return new XmlText(getContent());
    }

    @Override
    protected Object newObject() {
        return "";
    }

    protected String unwrapContent(String str) {
        return str;
    }

}
