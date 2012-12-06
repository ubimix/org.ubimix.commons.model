/**
 * 
 */
package org.ubimix.model.html;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.html.StructuredNodesBinding.DispatchingStructureBinder;
import org.ubimix.model.html.StructuredNodesBinding.IStructureBinder;
import org.ubimix.model.xml.IXmlElement;

/**
 * @author kotelnikov
 */
public class StructuredNodesBindingTest extends TestCase {

    public static class Reference extends Value {
        public Reference(IXmlElement element) {
            super(element);
        }
    }

    public static class TocTree extends StructuredTree {

        public TocTree(
            IXmlElement element,
            IValueFactory<? extends Value> factory) {
            super(null, element, factory);
        }

        public TocTree(
            StructuredTree parent,
            IXmlElement element,
            IValueFactory<? extends Value> factory) {
            super(parent, element, factory);
        }

        @Override
        protected StructuredTree newChildTree(IXmlElement e) {
            return new TocTree(this, e, getValueFactory());
        }

    }

    public StructuredNodesBindingTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        String content = "<html><body>"
            + "<table>\n"
            + "<tr><th>Property</th><th>Value</th></tr>\n"
            + "<tr><th>date</th><th>02/03/2012</th></tr>\n"
            + "<tr><th>about</th><th><a href='./about/about.html'>About</a></th></tr>\n"
            + "</table>\n"
            + "<ul>\n"
            + "<li><a href='./first.html'>First section</a>\n"
            + "<li><a href='./second.html'>Second section</a>\n"
            + "   <ul>\n"
            + "     <li><a href='./second/begin.html'>Begin</a>\n"
            + "     <li><a href='./second/end.html'>End</a>\n"
            + "   </ul>\n"
            + "<li><a href='./third.html'>Third section</a>\n"
            + "</ul>\n"
            + "<h1>This is a book</h1>\n"
            + "<p>First paragraph.</p>\n"
            + "<p>Second paragraph.</p>\n"
            + ""
            + "<ul><li>A</li><li>B</li></ul>\n"
            + ""
            + "<p>Third paragraph.</p>\n"
            + "<p>Paragraph with a <a href='./page.html'>reference</a>."
            + "<p>Fourth paragraph.</p>\n";
        IXmlElement e = HtmlDocument.parse(content);
        DispatchingStructureBinder binder = new DispatchingStructureBinder();
        binder.addBinder("a", new IStructureBinder() {
            @Override
            public StructuredNode bind(
                StructuredNodesBinding binding,
                IXmlElement e) {
                binding.bindStructuredNodes(e);
                return new Reference(e);
            }
        });
        binder.addBinder("table", new IStructureBinder() {
            @Override
            public StructuredNode bind(
                StructuredNodesBinding binding,
                IXmlElement e) {
                return new StructuredTable(e, binding.getValueFactory());
            }
        });
        binder.addBinder(new IStructureBinder() {
            @Override
            public StructuredNode bind(
                StructuredNodesBinding binding,
                IXmlElement e) {
                TocTree prev = binding.getStructuredNode(TocTree.class);
                if (prev != null) {
                    return null;
                }
                return new TocTree(e, binding.getValueFactory());
            }
        }, "ul", "ol");
        binder.addBinder(new IStructureBinder() {
            @Override
            public StructuredNode bind(
                StructuredNodesBinding binding,
                IXmlElement e) {
                return new StructuredTree(e, binding.getValueFactory());
            }
        }, "ul", "ol");

        IValueFactory<Value> factory = Value.FACTORY;

        StructuredNodesBinding binding = new StructuredNodesBinding(
            factory,
            binder);
        binding.bindStructuredNodes(e);

        StructuredTable table = binding
            .getStructuredNode(StructuredTable.class);
        assertNotNull(table);
        System.out.println(table);

        TocTree toc = binding.getStructuredNode(TocTree.class);
        assertNotNull(toc);
        List<TocTree> tocList = binding.getStructuredNodes(TocTree.class);
        assertNotNull(tocList);
        assertEquals(1, tocList.size());
        assertEquals(toc, tocList.get(0));

        StructuredTree tree = binding.getStructuredNode(StructuredTree.class);
        assertNotNull(tree);
        assertEquals(toc, tree);
        List<StructuredTree> list = binding
            .getStructuredNodes(StructuredTree.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(tree, list.get(0));
        StructuredTree test = list.get(1);
        assertNotNull(test);

        List<Reference> references = binding
            .getStructuredNodes(Reference.class);
        assertEquals(1, references.size());
        Reference ref = references.get(0);
        IXmlElement a = ref.getReferenceElement();
        assertNotNull(a);
        assertEquals("./page.html", a.getAttribute("href"));
    }
}
