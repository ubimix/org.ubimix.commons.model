/**
 * 
 */
package org.ubimix.model.path;

/**
 * @author kotelnikov
 */
public class SimpleStringMatcher {

    public final static char DEFAULT_ANY_CHAR = '?';

    public final static char DEFAULT_ANY_SEQUENCE_CHAR = '%';

    public final static char DEFAULT_ESCAPE_CHAR = '\\';

    private final static int STATE_ANY_LETTER = 1;

    private final static int STATE_ANY_SEQUENCE = 2;

    private final static int STATE_ESCAPE_LETTER = 3;

    private final static int STATE_EXACT_LETTER = 0;

    private char fAnyChar;

    private char fAnySequenceChar;

    private char fEscapeChar;

    /**
     * Constructor.
     * 
     * @see java.lang.Object#Object()
     */
    public SimpleStringMatcher() {
        this(DEFAULT_ESCAPE_CHAR, DEFAULT_ANY_CHAR, DEFAULT_ANY_SEQUENCE_CHAR);
    }

    /**
     * Constructor.
     * 
     * @param escapeChar
     */
    public SimpleStringMatcher(char escapeChar) {
        this(escapeChar, DEFAULT_ANY_CHAR, DEFAULT_ANY_SEQUENCE_CHAR);
    }

    /**
     * Constructor.
     * 
     * @param escapeChar
     * @param anyChar
     * @param anySequenceChar
     */
    public SimpleStringMatcher(
        char escapeChar,
        char anyChar,
        char anySequenceChar) {
        setEscapeChar(escapeChar);
        setAnyChar(anyChar);
        setAnySequenceChar(anySequenceChar);
    }

    /**
     * @param mask
     * @param maskPos
     * @param maskLen
     * @param matchedMaskLen
     * @param str
     * @param strPos
     * @param strLen
     * @return boolean
     */
    public int check(String mask, int maskPos, String str, int strPos) {
        int maskLen = mask.length();
        int strLen = str.length();
        int matchedMaskLen = getMatchedMaskLen(mask, maskPos);

        boolean escape = false;
        int realMatchCounter = 0;
        int cmpResult = 0;

        loop: while (maskPos < maskLen && strPos < strLen) {
            char maskChar = mask.charAt(maskPos);
            char strChar = str.charAt(strPos);
            int charStatus = getCharStatus(maskChar, escape);
            switch (charStatus) {
                case STATE_EXACT_LETTER:
                    realMatchCounter++;
                    cmpResult = strChar > maskChar ? 1 : strChar < maskChar
                        ? -1
                        : 0;
                    if (cmpResult != 0) {
                        break loop;
                    } else {
                        break;
                    }
                case STATE_ANY_LETTER:
                    realMatchCounter++;
                    break;
                case STATE_ESCAPE_LETTER:
                    realMatchCounter++;
                    strPos--;
                    break;
                case STATE_ANY_SEQUENCE:
                    int i;
                    for (i = strPos; i <= strLen; i++) {
                        cmpResult = check(mask, maskPos + 1, str, i);
                        if (cmpResult == 0) {
                            break;
                        }
                    }
                    return cmpResult;
            }
            escape = (charStatus == STATE_ESCAPE_LETTER);
            maskPos++;
            strPos++;
        }
        if (cmpResult != 0) {
            return cmpResult;
        } else if (strPos < strLen) {
            return 1;
        } else if (realMatchCounter < matchedMaskLen) {
            return -1;
        }
        return 0;
    }

    public int check(String mask, String str, boolean ignoreCase) {
        if (ignoreCase) {
            mask = mask.toLowerCase();
            str = str.toLowerCase();
        }
        return check(mask, 0, str, 0);
    }

    /**
     * @return
     */
    public char getAnyChar() {
        return fAnyChar;
    }

    /**
     * @return
     */
    public char getAnySequenceChar() {
        return fAnySequenceChar;
    }

    /**
     * Method getCharStatus.
     * 
     * @param ch
     * @return int
     */
    public int getCharStatus(char ch, boolean escape) {
        return escape ? STATE_EXACT_LETTER : (ch == fAnySequenceChar)
            ? STATE_ANY_SEQUENCE
            : (ch == fAnyChar) ? STATE_ANY_LETTER : (ch == fEscapeChar)
                ? STATE_ESCAPE_LETTER
                : STATE_EXACT_LETTER;
    }

    /**
     * @return
     */
    public char getEscapeChar() {
        return fEscapeChar;
    }

    /**
     * Method getMatchedMaskLen.
     * 
     * @param mask
     * @return int
     */
    private int getMatchedMaskLen(String mask, int pos) {
        int len = mask.length();
        boolean escape = false;
        for (int i = pos; i < len; i++) {
            char ch = mask.charAt(i);
            switch (getCharStatus(ch, escape)) {
                case STATE_ESCAPE_LETTER:
                    escape = true;
                    break;
                case STATE_ANY_SEQUENCE:
                    len--;
                case STATE_ANY_LETTER:
                default:
                    escape = false;
            }
        }
        return len - pos;
    }

    /**
     * @param c
     */
    public void setAnyChar(char c) {
        fAnyChar = c;
    }

    /**
     * @param c
     */
    public void setAnySequenceChar(char c) {
        fAnySequenceChar = c;
    }

    /**
     * @param c
     */
    public void setEscapeChar(char c) {
        fEscapeChar = c;
    }

}
