/**
 * 
 */
package org.ubimix.model.xml;

import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.model.xml.server.SaxXmlParser;

/**
 * @author kotelnikov
 */
public class SaxXmlParserTest extends InternalXmlParserTest {

    /**
     * @param name
     */
    public SaxXmlParserTest(String name) {
        super(name);
    }

    @Override
    protected IXmlParser newXmlParser() {
        return new SaxXmlParser();
    }
}
