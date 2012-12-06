/**
 * 
 */
package org.ubimix.model.html.export;

import org.ubimix.model.xml.IXmlElement;

/**
 * This class exports delegates export operations to element-specific exporters.
 * 
 * @author kotelnikov
 */
public class DispatchingSimpleExporter
    extends
    AbstractDispatchingExporter<String> {

    public DispatchingSimpleExporter() {
        super();
    }

    public DispatchingSimpleExporter(IXmlElementExporter defaultExporter) {
        super(defaultExporter);
    }

    @Override
    protected String getKey(
        XmlElementExportContext context,
        IXmlElement element,
        String parentKey) {
        String result = null;
        if (parentKey == null) {
            result = element.getName();
        }
        return result;
    }

}
