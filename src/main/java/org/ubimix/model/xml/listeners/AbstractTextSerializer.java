/**
 * 
 */
package org.ubimix.model.xml.listeners;

import java.util.Map;

import org.ubimix.commons.parser.xml.Entity;
import org.ubimix.commons.parser.xml.XmlListener;

/**
 * @author kotelnikov
 */
public abstract class AbstractTextSerializer extends XmlListener {

    private boolean fNormalizeSpaces;

    private boolean fSpace;

    public AbstractTextSerializer() {
        this(true);
    }

    public AbstractTextSerializer(boolean normalizeSpaces) {
        fNormalizeSpaces = normalizeSpaces;
    }

    @Override
    public void beginElement(
        String name,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
    }

    @Override
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

    @Override
    public void onCDATA(String content) {
        String str = normalizeSpaces(content);
        print(str);
    }

    @Override
    public void onEntity(Entity entity) {
        char[] ch = Character.toChars(entity.getCode());
        print(new String(ch));
    }

    @Override
    public void onText(String text) {
        String str = normalizeSpaces(text);
        print(str);
    }

    protected abstract void print(String string);

}
