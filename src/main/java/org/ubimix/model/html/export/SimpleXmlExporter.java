package org.ubimix.model.html.export;

import java.util.Map;

import org.ubimix.commons.parser.xml.IXmlListener;
import org.ubimix.model.xml.IXmlCDATA;
import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;
import org.ubimix.model.xml.IXmlText;
import org.ubimix.model.xml.XmlUtils;

/**
 * This exporter simply calls methods of the {@link IXmlListener} object defined
 * in the {@link ExportContext}. For all child elements this class recursively
 * calls the top exporter from the context.
 * 
 * @author kotelnikov
 */
public class SimpleXmlExporter implements IXmlElementExporter {

    /**
     * @see org.ubimix.model.html.export.IXmlElementExporter#export(org.ubimix.model.html.export.IXmlElementExporter.ExportContext,
     *      IXmlElement)
     */
    @Override
    public boolean export(ExportContext context, IXmlElement element) {
        IXmlListener listener = context.getXmlListener();
        String tagName = element.getName();
        Map<String, String> attributes = element.getAttributes();
        Map<String, String> namespaces = XmlUtils.getNamespaces(element);
        tagName = fixElementValues(
            context,
            element,
            tagName,
            attributes,
            namespaces);
        listener.beginElement(tagName, attributes, namespaces);
        exportChildren(context, element);
        listener.endElement(tagName, attributes, namespaces);
        return true;
    }

    /**
     * Exports all child XML nodes of the specified element
     * 
     * @param context the export context
     * @param element the element to export
     */
    protected void exportChildren(ExportContext context, IXmlElement element) {
        IXmlListener listener = context.getXmlListener();
        for (IXmlNode node : element) {
            if (node instanceof IXmlElement) {
                IXmlElement e = (IXmlElement) node;
                context.export(e);
            } else if (node instanceof IXmlCDATA) {
                IXmlCDATA cdata = (IXmlCDATA) node;
                listener.onCDATA(cdata.getContent());
            } else if (node instanceof IXmlText) {
                IXmlText text = (IXmlText) node;
                listener.onText(text.getContent());
            }
        }
    }

    /**
     * Checks and replace attributes, namespaces and tag names before exporting.
     * 
     * @param context the export context
     * @param element the element to export
     * @param tagName the name of the tag to replace (eventually)
     * @param attributes attributes to fix
     * @param namespaces namespaces to fix
     * @return a new tag name
     */
    protected String fixElementValues(
        ExportContext context,
        IXmlElement element,
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        return tagName;
    }
}