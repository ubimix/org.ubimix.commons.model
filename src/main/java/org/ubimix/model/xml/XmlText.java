/**
 * 
 */
package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public class XmlText extends XmlNode implements IXmlText {

    private String fContent;

    protected XmlText(IXmlFactory factory, String content) {
        super(factory, null);
        setContent(content);
    }

    @Override
    public void accept(IXmlVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof XmlText)) {
            return false;
        }
        XmlText o = (XmlText) obj;
        return equals(fContent, o.fContent);
    }

    /**
     * @see org.ubimix.model.xml.IXmlText#getContent()
     */
    @Override
    public String getContent() {
        return fContent;
    }

    @Override
    public int hashCode() {
        int code = fContent != null ? fContent.hashCode() : 0;
        return code;
    }

    @Override
    public void setContent(String content) {
        fContent = content;
    }

}
