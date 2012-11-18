/**
 * 
 */
package org.ubimix.model.binder;

import java.util.List;

import junit.framework.TestCase;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.binder.ContentBinding;
import org.ubimix.model.binder.DispatchingBinder;
import org.ubimix.model.binder.IBinder;
import org.ubimix.model.binder.IContentWidget;
import org.ubimix.model.html.HtmlDocument;
import org.ubimix.model.html.StructuredContentBinding.StructuredBinder;
import org.ubimix.model.html.StructuredContentBinding.StructuredWidget;
import org.ubimix.model.html.StructuredNode.Value;
import org.ubimix.model.html.StructuredContentBinding;
import org.ubimix.model.html.StructuredTable;
import org.ubimix.model.html.StructuredTree;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class ContentBinderTest extends TestCase {

    public static class TableContentWrapper<T extends Value>
        extends
        StructuredWidget<StructuredTable<T>> {

        public static <T extends Value> IBinder getBinder(
            final String... headers) {
            return new StructuredBinder() {
                @Override
                public IContentWidget bindStructuredContent(
                    StructuredContentBinding binding,
                    XmlElement e) {
                    TableContentWrapper<T> result = null;
                    IValueFactory<T> valueFactory = binding.getValueFactory();
                    StructuredTable<T> t = new StructuredTable<T>(
                        e,
                        valueFactory);
                    if (t.checkColumnNames(headers)) {
                        result = new TableContentWrapper<T>(binding, t);
                    }
                    return result;
                }
            };
        }

        public TableContentWrapper(
            ContentBinding binding,
            StructuredTable<T> node) {
            super(binding, node);
        }

        public StructuredTable<T> getTable() {
            return getNode();
        }

    }

    public static class TocWidget<T extends Value>
        extends
        StructuredWidget<StructuredTree<T>> {

        public static <T extends Value> IBinder getBinder() {
            return new StructuredBinder() {
                @Override
                public IContentWidget bindStructuredContent(
                    StructuredContentBinding binding,
                    XmlElement e) {
                    List<TocWidget<T>> list = binding
                        .getWidgets(TocWidget.class);
                    TocWidget<T> result = null;
                    if (list.isEmpty()) {
                        IValueFactory<T> factory = binding.getValueFactory();
                        StructuredTree<T> t = new StructuredTree<T>(e, factory);
                        result = new TocWidget<T>(binding, t);
                    }
                    return result;
                }
            };
        }

        public TocWidget(ContentBinding binding, StructuredTree<T> node) {
            super(binding, node);
        }

        public StructuredTree<T> getTree() {
            return getNode();
        }

    }

    public ContentBinderTest(String name) {
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
            + "<p>Fourth paragraph.</p>\n";
        XmlElement e = HtmlDocument.parse(content);
        DispatchingBinder binder = new DispatchingBinder();
        binder.addBinder("table", TableContentWrapper.getBinder());
        binder.addBinder(TocWidget.getBinder(), "ul", "ol");

        IValueFactory<Value> factory = Value.FACTORY;
        StructuredContentBinding binding = new StructuredContentBinding(
            binder,
            factory);
        binding.bindWidgets(e);

        @SuppressWarnings("unchecked")
        TableContentWrapper<Value> table = binding
            .getWidget(TableContentWrapper.class);
        assertNotNull(table);
        System.out.println(table);

        @SuppressWarnings("unchecked")
        TocWidget<Value> tree = binding.getWidget(TocWidget.class);
        assertNotNull(tree);
        List<IContentWidget> list = binding.getWidgets(TocWidget.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(tree, list.get(0));
        System.out.println(tree);

    }
}
