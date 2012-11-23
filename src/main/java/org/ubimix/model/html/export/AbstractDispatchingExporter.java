/**
 * 
 */
package org.ubimix.model.html.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ubimix.model.xml.XmlElement;

/**
 * This object delegates calls between registered type-specific element
 * exporters. For XML elements without any specific exporters this class
 * delegates calls to a default exporter.
 * 
 * @author kotelnikov
 */
public abstract class AbstractDispatchingExporter<T>
    implements
    IXmlElementExporter {

    /**
     * Default XML exporter used for XML nodes not associated with structured
     * nodes.
     */
    private IXmlElementExporter fDefaultExporter;

    /**
     * Mapping between structure types and list of exporters.
     */
    private Map<T, List<IXmlElementExporter>> fExporters = new HashMap<T, List<IXmlElementExporter>>();

    /**
     * This constructor creates and sets a {@link SimpleXmlExporter} as a
     * default exporter.
     */
    public AbstractDispatchingExporter() {
        this(new SimpleXmlExporter());
    }

    /**
     * This constructor initializes internal fields and sets the given exporter
     * as a default exporter used for non-structured XML elements.
     * 
     * @param defaultExporter the default XML exporter to use for non-structured
     *        XML elements.
     */
    public AbstractDispatchingExporter(IXmlElementExporter defaultExporter) {
        fDefaultExporter = defaultExporter;
    }

    /**
     * Adds a new XML exporter in the list of exporters associated with the
     * specified XML type.
     * 
     * @param type the type of structure associated with XML nodes which should
     *        be handled by the given exporter
     * @param exporter the exporter to use for the specified type of structures
     */
    public <N extends AbstractDispatchingExporter<T>> N addExporter(
        T type,
        IXmlElementExporter exporter) {
        List<IXmlElementExporter> list = fExporters.get(type);
        if (list == null) {
            list = new ArrayList<IXmlElementExporter>();
            fExporters.put(type, list);
        }
        list.add(exporter);
        return cast();
    }

    @SuppressWarnings("unchecked")
    protected <N extends AbstractDispatchingExporter<T>> N cast() {
        return (N) this;
    }

    /**
     * @see org.ubimix.model.html.export.IXmlElementExporter#export(org.ubimix.model.html.export.IXmlElementExporter.ExportContext,
     *      org.ubimix.model.xml.XmlElement)
     */
    @Override
    public boolean export(ExportContext context, XmlElement element) {
        boolean result = false;
        T key = getKey(context, element, null);
        while (key != null) {
            List<IXmlElementExporter> exporters = fExporters.get(key);
            if (exporters != null) {
                for (IXmlElementExporter exporter : exporters) {
                    result = exporter.export(context, element);
                }
                if (result) {
                    break;
                }
            }
            key = getKey(context, element, key);
        }
        if (!result) {
            result = fDefaultExporter.export(context, element);
        }
        return result;
    }

    /**
     * Returns a key used to access to exporters.
     * 
     * @param context the export context
     * @param element the element to export
     * @param parentKey the parent key (if any; it could be <code>null</code>
     *        for the first iteration)
     * @return an exporter key for the specified element
     */
    protected abstract T getKey(
        ExportContext context,
        XmlElement element,
        T parentKey);

    /**
     * Removes the specified exporter from the list of exporters associated with
     * the given structure type
     * 
     * @param key the key of the structures to handle
     * @param exporter the exporter to remove
     */
    public <N extends AbstractDispatchingExporter<T>> N removeExporter(
        T key,
        IXmlElementExporter exporter) {
        List<IXmlElementExporter> list = fExporters.get(key);
        if (list != null) {
            list.remove(exporter);
            if (list.isEmpty()) {
                fExporters.remove(key);
            }
        }
        return cast();
    }

}
