/**
 * 
 */
package org.ubimix.model.xml.server;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ubimix.model.xml.IXmlListener;
import org.ubimix.model.xml.IXmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author kotelnikov
 */
public class SaxXmlParser implements IXmlParser {

    private static class ElementNode {
        private static String getName(String tagLocalName, String tagQName) {
            String name = tagLocalName;
            if (tagQName != null) {
                name = tagQName;
            }
            return name;
        }

        private final Map<String, String> fAttrs = new HashMap<String, String>();

        private final String fName;

        private final Map<String, String> fNamespaces = new HashMap<String, String>();

        public ElementNode(
            String tagNamespaceUri,
            String tagLocalName,
            String tagQName,
            Attributes attributes) {
            fName = getName(tagLocalName, tagQName);
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrQName = attributes.getQName(i);
                String prefix = null;
                if ("xmlns".equals(attrQName)) {
                    prefix = "";
                } else if (attrQName != null && attrQName.startsWith("xmlns:")) {
                    prefix = attrQName.substring("xmlns:".length());
                }
                String value = attributes.getValue(i);
                if (prefix != null) {
                    fNamespaces.put(prefix, value);
                } else {
                    String attrLocalName = attributes.getLocalName(i);
                    String key = getName(attrLocalName, attrQName);
                    fAttrs.put(key, value);
                }
            }
        }

        public Map<String, String> getAttributes() {
            return fAttrs;
        }

        public String getName() {
            return fName;
        }

        public Map<String, String> getNamespaces() {
            return fNamespaces;
        }
    }

    private DefaultHandler fInternalHandler = new DefaultHandler() {

        private StringBuilder fBuf = new StringBuilder();

        @Override
        public void characters(char[] ch, int start, int length)
            throws SAXException {
            fBuf.append(ch, start, length);
        }

        /**
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
         *      java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
            throws SAXException {
            flushText();
            ElementNode node = fStack.pop();
            fListener.endElement(
                node.getName(),
                node.getAttributes(),
                node.getNamespaces());
        }

        private void flushText() {
            int len = fBuf.length();
            if (len > 0) {
                fListener.onText(fBuf.toString());
                fBuf.delete(0, len);
            }
        }

        @Override
        public void startElement(
            String tagNamespaceUri,
            String tagLocalName,
            String tagQName,
            Attributes attributes) throws SAXException {
            flushText();
            ElementNode node = new ElementNode(
                tagNamespaceUri,
                tagLocalName,
                tagQName,
                attributes);
            fStack.push(node);
            fListener.beginElement(
                node.getName(),
                node.getAttributes(),
                node.getNamespaces());
        }

    };

    private IXmlListener fListener;

    private Stack<ElementNode> fStack = new Stack<ElementNode>();

    /**
     * 
     */
    public SaxXmlParser() {
    }

    public void parse(Reader reader, IXmlListener listener) {
        try {
            try {
                fListener = listener;
                SAXParserFactory factory = SAXParserFactory.newInstance();
                InputSource source = new InputSource(reader);
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(source, fInternalHandler);
            } finally {
                reader.close();
                fListener = null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(String xml, IXmlListener listener) {
        if (xml != null && !"".equals(xml)) {
            StringReader reader = new StringReader(xml);
            parse(reader, listener);
        }
    }

}
