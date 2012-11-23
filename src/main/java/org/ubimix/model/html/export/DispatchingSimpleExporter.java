/**
 * 
 */
package org.ubimix.model.html.export;

import org.ubimix.model.xml.XmlElement;

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
        ExportContext context,
        XmlElement element,
        String parentKey) {
        String result = null;
        if (parentKey == null) {
            result = element.getName();
        }
        return result;
    }

}
