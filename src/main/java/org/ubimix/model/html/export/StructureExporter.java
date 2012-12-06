package org.ubimix.model.html.export;

import org.ubimix.model.html.StructuredNode;
import org.ubimix.model.html.StructuredNodesBinding;
import org.ubimix.model.xml.IXmlElement;

/**
 * This is a common parent class for XML nodes associated with structured nodes.
 * It calls a
 * {@link #export(IXmlElementExporter.XmlElementExportContext, IXmlElement, StructuredNode)}
 * protected method which should be implemented in sub-classes.
 * 
 * @author kotelnikov
 */
public abstract class StructureExporter implements IXmlElementExporter {

    @Override
    public boolean export(XmlElementExportContext context, IXmlElement element) {
        boolean result = false;
        StructuredNodesBinding binding = context.getBinding();
        StructuredNode structure = binding.getStructuredNode(element);
        if (structure != null) {
            result = export(context, element, structure);
        }
        return result;
    }

    /**
     * Exports the specified XML element and/or the associated structure object
     * and returns <code>true</code> if the export was successfull.
     * 
     * @param context the export context
     * @param element the XML element to export
     * @param structure the structure associated with the specified element
     * @return <code>true</code> if the specified structured element was
     *         successfully exported
     */
    protected abstract boolean export(
        XmlElementExportContext context,
        IXmlElement element,
        StructuredNode structure);

}