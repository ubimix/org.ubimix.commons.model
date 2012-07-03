/**
 * 
 */
package org.ubimix.model.xml.listeners;

/**
 * @author kotelnikov
 */
public class TextSerializer extends AbstractTextSerializer {

    protected StringBuilder fBuilder = new StringBuilder();

    /**
     * 
     */
    public TextSerializer() {
    }

    /**
     * @param normalizeSpaces
     */
    public TextSerializer(boolean normalizeSpaces) {
        super(normalizeSpaces);
    }

    @Override
    protected void print(String string) {
        fBuilder.append(string);
    }

    @Override
    public String toString() {
        return fBuilder.toString();
    }

}
