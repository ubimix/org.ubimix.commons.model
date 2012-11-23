package org.ubimix.model.html.export;

import java.util.Map;

import org.ubimix.commons.parser.xml.IXmlListener;
import org.ubimix.model.xml.XmlCDATA;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;
import org.ubimix.model.xml.XmlText;

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
     *      org.ubimix.model.xml.XmlElement)
     */
    @Override
    public boolean export(ExportContext context, XmlElement element) {
        IXmlListener listener = context.getXmlListener();
        String tagName = element.getName();
        Map<String, String> attributes = element.getAttributes();
        Map<String, String> namespaces = element.getNamespaces();
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
    protected void exportChildren(ExportContext context, XmlElement element) {
        IXmlListener listener = context.getXmlListener();
        for (XmlNode node : element) {
            if (node instanceof XmlElement) {
                XmlElement e = (XmlElement) node;
                context.export(e);
            } else if (node instanceof XmlCDATA) {
                XmlCDATA cdata = (XmlCDATA) node;
                listener.onCDATA(cdata.getContent());
            } else if (node instanceof XmlText) {
                XmlText text = (XmlText) node;
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
        XmlElement element,
        String tagName,
        Map<String, String> attributes,
        Map<String, String> namespaces) {
        return tagName;
    }
}