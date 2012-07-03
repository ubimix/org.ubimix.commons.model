package org.ubimix.model.xml;

/**
 * @author kotelnikov
 */
public interface IXmlVisitor {

    /**
     * Default implementation of the {@link IXmlVisitor} interface.
     * 
     * @author kotelnikov
     */
    public static class XmlVisitor implements IXmlVisitor {

        /**
         * 
         */
        public XmlVisitor() {
        }

        /**
         * @see org.ubimix.model.xml.IXmlVisitor#visit(org.ubimix.model.xml.XmlCDATA)
         */
        public void visit(XmlCDATA cdata) {
        }

        /**
         * @see org.ubimix.model.xml.IXmlVisitor#visit(org.ubimix.model.xml.XmlElement)
         */
        public void visit(XmlElement element) {
        }

        /**
         * @see org.ubimix.model.xml.IXmlVisitor#visit(org.ubimix.model.xml.XmlText)
         */
        public void visit(XmlText text) {
        }

    }

    void visit(XmlCDATA cdata);

    void visit(XmlElement element);

    void visit(XmlText text);
}