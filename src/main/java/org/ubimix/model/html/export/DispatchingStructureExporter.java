package org.ubimix.model.html.export;

import org.ubimix.model.html.StructuredNode;
import org.ubimix.model.html.StructuredNodesBinding;
import org.ubimix.model.xml.IXmlElement;

/**
 * This object delegates calls between registered type-specific structure
 * exporters. For XML nodes not associated with structured elements this class
 * delegates calls to a default exporter.
 * 
 * @author kotelnikov
 */
public class DispatchingStructureExporter
    extends
    AbstractDispatchingExporter<Class<? extends StructuredNode>> {

    /**
     * This constructor creates and sets a {@link SimpleXmlExporter} as a
     * default exporter.
     */
    public DispatchingStructureExporter() {
        super();
    }

    /**
     * This constructor initializes internal fields and sets the given exporter
     * as a default exporter used for non-structured XML elements.
     * 
     * @param defaultExporter the default XML exporter to use for non-structured
     *        XML elements.
     */
    public DispatchingStructureExporter(IXmlElementExporter defaultExporter) {
        super(defaultExporter);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends StructuredNode> getKey(
        XmlElementExportContext context,
        IXmlElement element,
        Class<? extends StructuredNode> prevType) {
        Class<? extends StructuredNode> result = null;
        if (prevType != null) {
            Class<?> type = prevType.getSuperclass();
            if (StructuredNode.class.isAssignableFrom(type)) {
                result = (Class<? extends StructuredNode>) type;
            }
        } else {
            StructuredNodesBinding binding = context.getBinding();
            StructuredNode structure = binding.getStructuredNode(element);
            if (structure != null) {
                result = structure.getClass();
            }
        }
        return result;
    }

}