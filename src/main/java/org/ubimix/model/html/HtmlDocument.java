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
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlFactory;
import org.ubimix.model.xml.XmlBuilder;
import org.ubimix.model.xml.XmlFactory;
import org.ubimix.model.xml.XmlUtils;

/**
 * @author kotelnikov
 */
public class HtmlDocument {

    public static IXmlElement parse(ICharLoader stream) {
        return parse(new UnboundedCharStream(stream));
    }

    public static IXmlElement parse(ICharStream stream) {
        IXmlFactory factory = XmlFactory.getInstance();
        XmlBuilder builder = new XmlBuilder(factory);
        IXmlParser parser = new HtmlParser();
        parser.parse(stream, builder);
        return builder.getResult();
    }

    public static IXmlElement parse(String html) {
        ICharStream stream = new CharStream(html);
        return parse(stream);
    }

    public static IXmlElement parseFragment(ICharLoader stream) {
        return toFragment(parse(stream));
    }

    public static IXmlElement parseFragment(ICharStream stream) {
        return toFragment(parse(stream));
    }

    public static IXmlElement parseFragment(String html) {
        return toFragment(parse(html));
    }

    private static IXmlElement toFragment(IXmlElement doc) {
        if (doc == null) {
            return null;
        }
        IXmlElement body = XmlUtils.getChildByName(doc, "body");
        if (body == null) {
            return null;
        }
        IXmlElement result = XmlUtils.getFirstChildElement(body);
        return result;
    }

}
