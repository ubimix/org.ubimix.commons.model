/**
 * 
 */
package org.ubimix.model.path.utils;

import org.ubimix.model.path.INodeSelector;

/**
 * @author kotelnikov
 */
public class TextSelector implements INodeSelector {

    public static TextSelector selector(char match, String matchValue) {
        if (matchValue == null) {
            return null;
        }
        if (matchValue.startsWith("\'") || matchValue.startsWith("\"")) {
            matchValue = matchValue.substring(1);
        }
        if (matchValue.endsWith("\'") || matchValue.endsWith("\"")) {
            matchValue = matchValue.substring(0, matchValue.length() - 1);
        }
        TextSelector selector = new TextSelector(matchValue, match);
        return selector;
    }

    public static TextSelector selector(String matchType, String matchValue) {
        if (matchValue == null) {
            return null;
        }
        char match = '~';
        if (matchType.length() > 0) {
            match = matchType.charAt(0);
        }
        return selector(match, matchValue);
    }

    private String fMask;

    private char fMatch;

    public TextSelector(String mask) {
        this(mask, '~');
    }

    public TextSelector(String mask, char match) {
        fMask = mask;
        fMatch = match;
    }

    @Override
    public SelectionResult accept(Object node) {
        INodeSelector.SelectionResult result = INodeSelector.SelectionResult.MAYBE;
        String value = getTextValue(node);
        if (value != null) {
            result = INodeSelector.SelectionResult.YES;
            if (fMask != null) {
                if (!match(value, fMask)) {
                    result = INodeSelector.SelectionResult.NO;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TextSelector)) {
            return false;
        }
        TextSelector o = (TextSelector) obj;
        return fMask == null || o.fMask == null ? fMask == o.fMask : fMask
            .equals(o.fMask);
    }

    protected String getTextValue(Object node) {
        return node != null ? node.toString() : null;
    }

    @Override
    public int hashCode() {
        return fMask != null ? fMask.hashCode() : 0;
    }

    protected boolean match(String value, String mask) {
        boolean result = false;
        switch (fMatch) {
            case '=':
                result = value.equals(mask);
                break;
            case '~':
                result = value.indexOf(mask) >= 0;
                break;
            case '^':
                result = value.startsWith(mask);
                break;
            case '$':
                result = value.endsWith(mask);
                break;
        }
        return result;
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        int idx = name.lastIndexOf('.');
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        return name + "[" + fMask + "]";
    }
}
