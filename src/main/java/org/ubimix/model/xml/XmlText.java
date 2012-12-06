/**
 * 
 */
package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlText extends XmlNode implements IXmlText {

    protected XmlText(IXmlFactory factory, String content) {
        super(factory, null, content);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @see org.ubimix.model.xml.IXmlText#getContent()
     */
    @Override
    public String getContent() {
        String str = (String) getObject();
        str = unwrapContent(str);
        return str;
    }

    @Override
    protected Object newObject() {
        return "";
    }

    @Override
    public void setContent(String content) {
        setObject(content);
    }

    protected String unwrapContent(String str) {
        return str;
    }

}
