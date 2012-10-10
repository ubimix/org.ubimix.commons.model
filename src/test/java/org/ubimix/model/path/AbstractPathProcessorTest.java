/**
 * 
 */
package org.ubimix.model.path;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.path.xml.XmlNodeProvider;
import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public abstract class AbstractPathProcessorTest extends TestCase {

    public AbstractPathProcessorTest(String name) {
        super(name);
    }

    protected void test(
        String xml,
        final boolean collect,
        IPathSelector selector,
        String... controls) {
        INodeProvider provider = new XmlNodeProvider();
        PathProcessor processor = new PathProcessor(provider, selector);
        XmlNode node = XmlElement.parse(xml);
        final List<XmlElement> results = new ArrayList<XmlElement>();
        processor.select(node, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                results.add((XmlElement) node);
                return collect;
            }
        });
        assertEquals(controls.length, results.size());
        int i = 0;
        for (String str : controls) {
            XmlElement testNode = results.get(i++);
            assertNotNull(testNode);
            assertEquals(str, testNode.toString());
        }
    }

    protected void test(String xml, IPathSelector selector, String... controls) {
        test(xml, true, selector, controls);
    }
}
