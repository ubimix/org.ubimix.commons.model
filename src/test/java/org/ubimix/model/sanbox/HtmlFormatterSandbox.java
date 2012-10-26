/**
 * 
 */
package org.ubimix.model.sanbox;

import java.util.Map;

import org.ubimix.commons.parser.ICharStream;
import org.ubimix.commons.parser.ICharStream.IPointer;
import org.ubimix.commons.parser.StreamToken;
import org.ubimix.commons.parser.html.HtmlParser;
import org.ubimix.model.xml.XmlBuilder;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlText;

/**
 * @author kotelnikov
 */
public class HtmlFormatterSandbox {
    public static void main(String[] args) {
        final XmlElement formattedDoc = XmlElement.parse(""
            + "<html>\n"
            + "<head></head>\n"
            + "<body>\n"
            + "<pre class='doc'></pre>\n"
            + "</body>\n"
            + "</html>");
        final XmlElement formatted = formattedDoc.select("pre");
        final int[] id = { 0 };
        final HtmlParser parser = new HtmlParser() {
            @Override
            protected void dispatchToken(StreamToken token) {
                super.dispatchToken(token);
                IPointer begin = token.getBegin();
                IPointer end = token.getEnd();
                XmlElement span = new XmlElement("span")
                    .setAttribute("id", "" + id[0])
                    .setAttribute("begin", formatPos(begin))
                    .setAttribute("end", formatPos(end));
                String name = token.getClass().getName();
                int idx = name.lastIndexOf('.');
                if (idx > 0) {
                    name = name.substring(idx + 1);
                }
                idx = name.lastIndexOf('$');
                if (idx > 0) {
                    name = name.substring(idx + 1);
                }
                span.setAttribute("class", name);
                formatted.addChild(span);
                span.addChild(new XmlText(token.getText()));
                id[0]++;
            }

            private String formatPos(ICharStream.IPointer pos) {
                return pos.getPos()
                    + "["
                    + pos.getLine()
                    + ":"
                    + pos.getColumn()
                    + "]";
            }
        };
        XmlBuilder builder = new XmlBuilder() {
            @Override
            public void beginElement(
                String name,
                Map<String, String> attributes,
                Map<String, String> namespaces) {
                super.beginElement(name, attributes, namespaces);
                XmlElement e = fContext.getActiveElement();
                if (e != null) {
                    e.setAttribute("startTokenId", id[0] + "");
                }
            }

            @Override
            public void endElement(
                String name,
                Map<String, String> attributes,
                Map<String, String> namespaces) {
                XmlElement e = fContext != null
                    ? fContext.getActiveElement()
                    : null;
                if (e != null) {
                    e.setAttribute("endTokenId", (id[0] - 1) + "");
                }
                super.endElement(name, attributes, namespaces);
            }
        };
        parser.parse(""
            + "<p \n"
            + "  class=test \n"
            + " x  \n"
            + "       = \n"
            + "  y\n"
            + " >First paragraph\n"
            + "\n"
            + "<li>item one\n"
            + "\n"
            + "<li>item \n"
            + "two", builder);
        XmlElement e = builder.getResult();
        XmlElement formattedBody = formattedDoc.select("body");
        formattedBody.addChild(new XmlElement("hr"));
        formattedBody.addChildren(e.select("body"));

        System.out.println(formattedDoc);
    }
}
