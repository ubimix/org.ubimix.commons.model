package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public interface IXmlNode {

    void accept(IXmlVisitor visitor);

    IXmlFactory getFactory();

    IXmlNode getNextSibling();

    IXmlElement getParent();

    IXmlNode getPreviousSibling();

    void remove();

}