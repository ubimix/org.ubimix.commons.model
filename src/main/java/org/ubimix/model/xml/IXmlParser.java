package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public interface IXmlParser {

    void parse(String str, IXmlListener listener);

}
