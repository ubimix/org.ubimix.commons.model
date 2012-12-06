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
         * @see org.ubimix.model.xml.IXmlVisitor#visit(IXmlCDATA)
         */
        public void visit(IXmlCDATA cdata) {
        }

        /**
         * @see org.ubimix.model.xml.IXmlVisitor#visit(org.ubimix.model.xml.XmlElement)
         */
        public void visit(IXmlElement element) {
        }

        /**
         * @see org.ubimix.model.xml.IXmlVisitor#visit(org.ubimix.model.xml.XmlText)
         */
        public void visit(IXmlText text) {
        }

    }

    void visit(IXmlCDATA cdata);

    void visit(IXmlElement element);

    void visit(IXmlText text);
}