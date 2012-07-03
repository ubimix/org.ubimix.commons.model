/**
 * 
 */
package org.ubimix.model.xml.listeners;

import java.util.Map;

import org.ubimix.model.xml.IXmlListener;

/**
 * @author kotelnikov
 */
public abstract class AbstractTextSerializer implements IXmlListener {

    private boolean fNormalizeSpaces;

    private boolean fSpace;

    public AbstractTextSerializer() {
        this(true);
    }

    public AbstractTextSerializer(boolean normalizeSpaces) {
        fNormalizeSpaces = normalizeSpaces;
    }

    public void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
    }

    public void endElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
    }

    private String normalizeSpaces(String content) {
        if (!fNormalizeSpaces) {
            return content;
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch == ' '
                || ch == '\0'
                || ch == '\n'
                || ch == '\r'
                || ch == '\t') {
                if (!fSpace) {
                    buf.append(" ");
                }
                fSpace = true;
            } else {
                buf.append(ch);
                fSpace = false;
            }
        }
        return buf.toString();
    }

    public void onCDATA(String content) {
        String str = normalizeSpaces(content);
        print(str);
    }

    public void onText(String text) {
        String str = normalizeSpaces(text);
        print(str);
    }

    protected abstract void print(String string);

}
