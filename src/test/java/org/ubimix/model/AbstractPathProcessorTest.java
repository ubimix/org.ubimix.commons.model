/**
 * 
 */
package org.ubimix.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.ModelObject;
import org.ubimix.model.TreePresenter;
import org.ubimix.model.path.INodeProvider;
import org.ubimix.model.path.IPathNodeCollector;
import org.ubimix.model.path.IPathSelector;
import org.ubimix.model.path.PathProcessor;
import org.ubimix.model.path.utils.TreeNodeProvider;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public abstract class AbstractPathProcessorTest extends TestCase {

    public AbstractPathProcessorTest(String name) {
        super(name);
    }

    protected <T> void test(
        T node,
        final boolean collect,
        INodeProvider provider,
        IPathSelector selector,
        String... controls) {
        PathProcessor processor = new PathProcessor(provider, selector);
        final List<Object> results = new ArrayList<Object>();
        processor.select(node, new IPathNodeCollector() {
            @Override
            public boolean setResult(Object node) {
                results.add(node);
                return collect;
            }
        });
        assertEquals(controls.length, results.size());
        int i = 0;
        for (String str : controls) {
            Object testNode = results.get(i++);
            assertNotNull(testNode);
            assertEquals(str, testNode.toString());
        }
    }

    public void testJson(
        String json,
        final boolean collect,
        IPathSelector selector,
        String... controls) {
        TreePresenter presenter = new TreePresenter("items");
        INodeProvider provider = new TreeNodeProvider(
            presenter,
            ModelObject.FACTORY);
        ModelObject node = ModelObject.parse(json);
        test(node, collect, provider, selector, controls);
    }

    protected void testXml(
        String xml,
        final boolean collect,
        IPathSelector selector,
        String... controls) {
        XmlElement node = XmlElement.parse(xml);
        INodeProvider provider = new TreeNodeProvider(
            XmlElement.TREE_ACCESSOR,
            node.getNodeFactory());
        test(node, collect, provider, selector, controls);
    }

    protected void testXml(
        String xml,
        IPathSelector selector,
        String... controls) {
        testXml(xml, true, selector, controls);
    }
}
