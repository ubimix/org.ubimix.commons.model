package org.ubimix.model.html.export;

import org.ubimix.commons.parser.xml.IXmlListener;
import org.ubimix.model.html.StructuredNodesBinding;
import org.ubimix.model.xml.XmlElement;

/**
 * A common interface for XML exporters used to recursively call
 * {@link IXmlListener} defined in the export context for XML elements and/or
 * for structured nodes associated with these elements.
 * 
 * @author kotelnikov
 */
public interface IXmlElementExporter {

    /**
     * Objects of this type contain context information used by exporters. It
     * gives access to the topmost exporter instance as well as to the XML
     * listener used to perform real operations (like XML node formatting).
     * 
     * @author kotelnikov
     */
    public static class ExportContext {

        /**
         * Binding containing mapping between XML elements and structured
         * elements.
         */
        private StructuredNodesBinding fBinding;

        /**
         * The topmost formatter used to recursively perform export operations.
         */
        private IXmlElementExporter fTopExporter;

        /**
         * XML listener performing real formatting operations
         */
        private IXmlListener fXmlListener;

        /**
         * This constructor initializes all internal fields.
         * 
         * @param binding the binding between XML elements and the corresponding
         *        structured elements
         * @param topExporter the topmost exporter used to recursively format
         *        child elements
         * @param listener a listener to set
         */
        public ExportContext(
            StructuredNodesBinding binding,
            IXmlElementExporter topExporter,
            IXmlListener listener) {
            fBinding = binding;
            fTopExporter = topExporter;
            fXmlListener = listener;
        }

        /**
         * Exports the specified element using the internal
         * {@link IXmlElementExporter} instance and returns the result of this
         * operation.
         * 
         * @param element the element to export
         * @return <code>true</code> if the specified element was successfully
         *         exported
         */
        public boolean export(XmlElement element) {
            return fTopExporter.export(this, element);
        }

        /**
         * Returns binding containing mapping between XML elements and
         * structured elements.
         * 
         * @return binding containing mapping between XML elements and
         *         structured elements.
         */
        public StructuredNodesBinding getBinding() {
            return fBinding;
        }

        /**
         * Returns the topmost formatter used to recursively perform export
         * operations.
         * 
         * @return the topmost formatter used to recursively perform export
         *         operations.
         */
        public IXmlElementExporter getTopExporter() {
            return fTopExporter;
        }

        /**
         * Returns XML listener performing real formatting operations.
         * 
         * @return XML listener performing real formatting operations
         */
        public IXmlListener getXmlListener() {
            return fXmlListener;
        }

    }

    /**
     * Exports the specified XML element and returns <code>true</code> if the
     * node was exported.
     * 
     * @param context the export context
     * @param element the element to export
     * @return <code>true</code> if the specified node was successfully exported
     */
    boolean export(IXmlElementExporter.ExportContext context, XmlElement element);

}