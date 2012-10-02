/**
 * 
 */
package org.ubimix.model.xml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a non-validating ("irregular") parser for XML documents. This parser
 * does not try to check if each opening tag has the corresponding closing tag.
 * It just reports separately all found tags, words, spaces and special symbols.
 * To notify about found structural elements this class calls
 * {@link IXmlListener listener methods}.
 * 
 * @author kotelnikov
 */
public class XmlParser implements IXmlParser {

    /**
     * This class is used to handle tag attributes
     */
    private static class TagInfo {

        private Map<String, String> fAttributes;

        private Map<String, String> fNamespaces;

        private TagInfo fParent;

        private String fTagName;

        public TagInfo(
            TagInfo parent,
            String tagName,
            Map<String, String> attributes,
            Map<String, String> namespaces) {
            fParent = parent;
            fTagName = tagName;
            fAttributes = attributes;
            fNamespaces = namespaces;
        }

        public void beginElement(IXmlListener listener) {
            listener.beginElement(fTagName, fAttributes, fNamespaces);
        }

        public void checkTagName(String tagName) {
            if (!fTagName.equals(tagName)) {
                throw new IllegalStateException("A closing tag for '"
                    + fTagName
                    + "' is expected but '"
                    + tagName
                    + "' was found.");
            }
        }

        public TagInfo endElement(IXmlListener listener) {
            listener.endElement(fTagName, fAttributes, fNamespaces);
            return fParent;
        }
    }

    private static final String NS = "xmlns";

    private static final String NS_PREFIX = "xmlns:";

    /**
     * The internal array of characters corresponding to the parsed string
     */
    char[] fArray;

    /**
     * The begin of the currently reported element, word or a special symbol
     */
    protected int fBegin;

    private StringBuilder fBuf = new StringBuilder();

    /**
     * The end of the currently reported element, word or special symbol
     */
    protected int fEnd;

    /**
     * Listener used to notify about individual XML nodes.
     */
    private IXmlListener fListener;

    private TagInfo fPeek;

    /**
     * 
     */
    public XmlParser() {
        super();
    }

    private void flushText() {
        if (fBuf.length() > 0) {
            fListener.onText(fBuf.toString());
            fBuf.delete(0, fBuf.length());
        }
    }

    /**
     * Returns <code>true</code> if the specified character is a white space
     * 
     * @param ch the character to check
     * @return <code>true</code> if the specified character is a white space
     */
    protected boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    /**
     * Tests the specified character and returns <code>true</code> if it can be
     * interpreted as part of a word. Word characters are normal letters and
     * digits.
     * 
     * @param ch the character to test
     * @return <code>true</code> if the given character is a word character
     */
    protected boolean isWordChar(char ch) {
        return Character.isLetterOrDigit(ch);
    }

    /**
     * Returns a new string created from the specified region of the internal
     * array.
     * 
     * @param i the start position of the sub-string
     * @param j the end position of the sub-string
     * @return a new string created from the specified region of the internal
     *         array
     */
    protected String newString(int i, int j) {
        if (j < i) {
            return "";
        }
        return new String(fArray, i, j - i);
    }

    /**
     * This method is called to notify that an opening tag was found.
     * 
     * @param tagName the name of the tag
     * @param params a list parameters; each parameter is represented by a
     *        two-value array where the first cell is the key and the second
     *        cell is the value of the parameter
     * @param namespaces
     */
    protected void onBeginTag(
        String tagName,
        Map<String, String> params,
        Map<String, String> namespaces) {
        flushText();
        fPeek = new TagInfo(fPeek, tagName, params, namespaces);
        fPeek.beginElement(fListener);
    }

    /**
     * This method is called to notify that a closing tag was found
     * 
     * @param tagName the name of the closing tag
     */
    protected void onEndTag(String tagName) {
        flushText();
        fPeek.checkTagName(tagName);
        fPeek = fPeek.endElement(fListener);
    }

    /**
     * This method is used to notify about XML entities in the text.
     * 
     * @param entity an XML entity
     */
    protected void onEntity(String entity) {
        // FIXME: add entities
        printText(entity);
    }

    /**
     * This method is called to notify that a sequence of whitespace symbols was
     * found in the text
     * 
     * @param str the sequence of whitespace symbols
     */
    protected void onSpaces(String str) {
        printText(str);
    }

    /**
     * This method is called to notify that a sequence of special symbols was
     * found.
     * 
     * @param str the sequence of special symbols
     */
    protected void onSpecialSymbols(String str) {
        printText(str);
    }

    /**
     * This method is called to notify that a separate word was found in the
     * document
     * 
     * @param word the found word
     */
    protected void onWord(String word) {
        printText(word);
    }

    /**
     * The main method of the class. It split the specified string into
     * individual compounds and report about them using the <code>onXxx</code>
     * protected methods.
     * 
     * @param str the string to parse
     */
    @Override
    public void parse(String str, IXmlListener listener) {
        try {
            fListener = listener;
            fArray = str.toCharArray();
            for (int i = 0; i < fArray.length;) {
                char ch = fArray[i];
                if (isSpace(ch)) {
                    int next = skipSpaces(i);
                    boolean ok = reportSpaces(i, next);
                    i = next;
                    if (ok) {
                        continue;
                    }
                }
                if (isWordChar(ch)) {
                    int next = skipWord(i);
                    boolean ok = reportWord(i, next);
                    i = next;
                    if (ok) {
                        continue;
                    }
                }
                if (ch == '<') {
                    int next = skipTag(i);
                    boolean ok = reportTag(i, next);
                    i = next;
                    if (ok) {
                        continue;
                    }
                }
                if (ch == '&') {
                    int next = skipEntity(i);
                    boolean ok = reportEntity(i, next);
                    i = next;
                    if (ok) {
                        continue;
                    }
                }
                int next = skipSpecialSymbols(i);
                reportSpecialSymbols(i, next);
                i = next;
            }
        } finally {
            fListener = null;
            fArray = null;
        }
    }

    private void printText(String str) {
        fBuf.append(str);
    }

    /**
     * Parse the specified range of characters in the internal array and returns
     * an array of parameters extracted from this range
     * 
     * @param i the initial position of the region
     * @param j the end position of the region
     * @return an array of parameters; each parameter is a two-value array
     *         containing the key of the parameter in the first cell and the
     *         corresponding value in the second cell.
     */
    private void readParams(
        int i,
        int j,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        for (; i < j;) {
            i = skipSpaces(i);
            i = skipSpecialSymbols(i);
            if (i >= j) {
                break;
            }
            int end = skipXmlName(i);
            String key = newString(i, end);
            i = end;
            i = skipSpaces(i);
            String value = "";
            if (i < j && fArray[i] == '=') {
                i++;
                i = skipSpaces(i);
                end = skipParam(i, j);
                value = newString(i, end);
                i = end;
                char beginChar = value.charAt(0);
                char endChar = value.charAt(value.length() - 1);
                if (beginChar == endChar
                    && beginChar == '\''
                    || beginChar == '\"') {
                    value = value.substring(1, value.length() - 1);
                }
            }
            String prefix = null;
            if (key.startsWith(NS_PREFIX)) {
                prefix = key.substring(NS_PREFIX.length());
            } else if (key.equals(NS)) {
                prefix = "";
            }
            if (prefix != null) {
                namespaces.put(prefix, value);
            } else {
                attributes.put(key, value);
            }
        }
    }

    /**
     * Reports about an XML entity in the given region
     * 
     * @param i the initial position of the region
     * @param j the end position of the region
     * @return <code>true</code> if an entity was reported
     */
    private boolean reportEntity(int i, int j) {
        if (j >= i) {
            fBegin = i;
            fEnd = j;
            fListener.onText(newString(i, j));
            return true;
        }
        return false;
    }

    /**
     * Reports about special symbols in the give region
     * 
     * @param i the initial position of the region
     * @param j the end position of the region
     * @return <code>true</code> if the spaces was reported
     */
    private boolean reportSpaces(int i, int j) {
        if (j >= i) {
            fBegin = i;
            fEnd = j;
            onSpaces(newString(i, j));
            return true;
        }
        return false;
    }

    /**
     * Reports about a sequence of special symbols located in the specified
     * region.
     * 
     * @param i the initial position of the region
     * @param j the end position of the region
     * @return <code>true</code> if a sequence of special symbols was reported
     */
    private boolean reportSpecialSymbols(int i, int j) {
        if (j >= i) {
            fBegin = i;
            fEnd = j;
            onSpecialSymbols(newString(i, j));
            return true;
        }
        return false;
    }

    /**
     * Extracts the tag info from the specified region in the internal array and
     * report about it to the listener methods (
     * {@link #onBeginTag(String, List)} or {@link #onEndTag(String)})
     * 
     * @param i the start position of the tag
     * @param j the end position of the tag
     * @return <code>true</code> if a tag was reported
     */
    private boolean reportTag(int i, int j) {
        if (j > i) {
            fBegin = i;
            fEnd = j;
            i++;
            j--;
            if (i <= j && fArray[i] == '/') {
                i++;
                int next = skipXmlName(i);
                String name = newString(i, next);
                onEndTag(name);
            } else {
                int next = skipXmlName(i);
                String name = newString(i, next);
                Map<String, String> params = new LinkedHashMap<String, String>();
                Map<String, String> namespaces = new LinkedHashMap<String, String>();
                readParams(next, j, params, namespaces);
                onBeginTag(name, params, namespaces);
                if (j < fArray.length && fArray[j - 1] == '/') {
                    onEndTag(name);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Reports about a new word found in the specified region of the internal
     * array
     * 
     * @param i the start position of the word
     * @param j the end position of the word
     * @return <code>true</code> if a word was reported
     */
    private boolean reportWord(int i, int j) {
        if (j >= i) {
            fBegin = i;
            fEnd = j;
            onWord(newString(i, j));
            return true;
        }
        return false;
    }

    /**
     * Skips an XML entity.
     * 
     * @param i the start position of the entity
     * @return the first position after the entity
     */
    private int skipEntity(int i) {
        if (fArray[i] != '&') {
            return i;
        }
        int end = skipWord(i + 1);
        if (end == i || fArray[end] != ';') {
            return i;
        }
        return end + 1;
    }

    /**
     * Skip a tag parameter in the specified region in the internal array of
     * characters
     * 
     * @param i the initial position
     * @param j the final position
     * @return the first position after the parameter
     */
    private int skipParam(int i, int j) {
        char quot = fArray[i] == '\'' || fArray[i] == '\"' ? fArray[i] : '\0';
        if (quot != '\0') {
            i++;
            boolean stop = false;
            for (; !stop && i <= j; i++) {
                stop = fArray[i] == quot;
            }
        } else {
            int prev = -1;
            while (prev != i) {
                prev = i;
                i = skipSpecialSymbols(i);
                i = skipWord(i);
            }
            if (i > j) {
                i = j;
            }
        }
        return i;
    }

    /**
     * Skips all spaces starting from the specified position
     * 
     * @param i the first position where the space character should be checked
     * @return the first non-space position
     */
    private int skipSpaces(int i) {
        for (; i < fArray.length; i++) {
            if (!isSpace(fArray[i])) {
                break;
            }
        }
        return i;
    }

    /**
     * Skips all special symbols from the specified position and returns the
     * first position with a non-special-symbol character.
     * 
     * @param i the first position where a special symbol should be checked
     * @return the first non-special-symbol position
     */
    private int skipSpecialSymbols(int i) {
        for (; i < fArray.length; i++) {
            char ch = fArray[i];
            if (isSpace(ch) || isWordChar(ch) || ch == '<') {
                break;
            }
        }
        return i;
    }

    /**
     * Skips all symbols included in a tag.
     * 
     * @param i the start position of the tag
     * @return the first position after the tag
     */
    private int skipTag(int i) {
        if (fArray[i] != '<') {
            return i;
        }
        char quot = '\0';
        boolean finished = false;
        for (; !finished && i < fArray.length; i++) {
            char ch = fArray[i];
            if (quot != '\0') {
                if (ch == quot) {
                    quot = '\0';
                }
            } else {
                if (ch == '"' || ch == '\'') {
                    quot = ch;
                } else if (ch == '>') {
                    finished = true;
                }
            }
        }
        return i;
    }

    /**
     * Skips all symbols corresponding to a word
     * 
     * @param i the initial position where the word should be checked
     * @return the first position after the word
     */
    private int skipWord(int i) {
        for (; i < fArray.length; i++) {
            if (!isWordChar(fArray[i])) {
                break;
            }
        }
        return i;
    }

    /**
     * Skips all symbols corresponding to a word
     * 
     * @param i the initial position where the word should be checked
     * @return the first position after the word
     */
    private int skipXmlName(int i) {
        for (; i < fArray.length; i++) {
            if (fArray[i] != ':' && !isWordChar(fArray[i])) {
                break;
            }
        }
        return i;
    }

}