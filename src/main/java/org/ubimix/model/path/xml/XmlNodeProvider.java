/**
 * 
 */
package org.ubimix.model.path.xml;

import java.util.Iterator;

import org.ubimix.model.path.INodeProvider;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public class XmlNodeProvider implements INodeProvider {

    /**
     * 
     */
    public XmlNodeProvider() {
    }

    /**
     * @see org.ubimix.model.path.INodeProvider
     *      <T>#getChildren(java.lang.Object)
     */
    @Override
    public Iterator<?> getChildren(final Object parent) {
        if (!(parent instanceof XmlElement)) {
            return null;
        }
        final XmlElement element = (XmlElement) parent;
        final int len = element.getChildCount();
        if (len == 0) {
            return null;
        }
        return new Iterator<XmlNode>() {

            private int fPos;

            @Override
            public boolean hasNext() {
                return fPos < len;
            }

            @Override
            public XmlNode next() {
                if (!hasNext()) {
                    return null;
                }
                XmlNode child = element.getChild(fPos);
                fPos++;
                return child;
            }

            @Override
            public void remove() {
            }

        };
    }

}
