/**
 * 
 */
package org.ubimix.model.html;

import org.ubimix.commons.parser.CharStream;
import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.UnboundedCharStream;
import org.ubimix.commons.parser.UnboundedCharStream.ICharLoader;
import org.ubimix.commons.parser.html.HtmlParser;
import org.ubimix.commons.parser.xml.IXmlParser;
import org.ubimix.model.xml.XmlBuilder;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class HtmlDocument {

    public static XmlElement parse(ICharLoader stream) {
        return parse(new UnboundedCharStream(stream));
    }

    public static XmlElement parse(ICharStream stream) {
        XmlBuilder builder = new XmlBuilder();
        IXmlParser parser = new HtmlParser();
        parser.parse(stream, builder);
        return builder.getResult();
    }

    public static XmlElement parse(String html) {
        ICharStream stream = new CharStream(html);
        return parse(stream);
    }

    public static XmlElement parseFragment(ICharLoader stream) {
        return toFragment(parse(stream));
    }

    public static XmlElement parseFragment(ICharStream stream) {
        return toFragment(parse(stream));
    }

    public static XmlElement parseFragment(String html) {
        return toFragment(parse(html));
    }

    private static XmlElement toFragment(XmlElement doc) {
        if (doc == null) {
            return null;
        }
        XmlElement body = doc.getChildByName("body");
        if (body == null) {
            return null;
        }
        XmlElement result = body.getChildElement();
        return result;
    }

}
