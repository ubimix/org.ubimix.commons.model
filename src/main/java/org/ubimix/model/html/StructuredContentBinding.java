package org.ubimix.model.html;

import org.ubimix.model.IValueFactory;
import org.ubimix.model.binder.ContentBinding;
import org.ubimix.model.binder.ContentWidget;
import org.ubimix.model.binder.IBinder;
import org.ubimix.model.binder.IContentWidget;
import org.ubimix.model.xml.XmlElement;

/**
 * @author kotelnikov
 */
public class StructuredContentBinding extends ContentBinding {

    /**
     * @author kotelnikov
     */
    public static abstract class StructuredBinder implements IBinder {

        @Override
        public IContentWidget bind(ContentBinding binding, XmlElement e) {
            IContentWidget result = null;
            if (binding instanceof StructuredContentBinding) {
                StructuredContentBinding b = (StructuredContentBinding) binding;
                result = bindStructuredContent(b, e);
            }
            return result;
        }

        public abstract IContentWidget bindStructuredContent(
            StructuredContentBinding binding,
            XmlElement e);

    }

    /**
     * @author kotelnikov
     * @param <N>
     */
    public static class StructuredWidget<N extends StructuredNode>
        extends
        ContentWidget {

        protected N fNode;

        public StructuredWidget(ContentBinding binding, N node) {
            super(binding, node.getElement());
            fNode = node;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof StructuredWidget<?>)) {
                return false;
            }
            StructuredWidget<?> o = (StructuredWidget<?>) obj;
            return fNode.equals(o.fNode);
        }

        public N getNode() {
            return fNode;
        }

        @Override
        public int hashCode() {
            return fNode.hashCode();
        }

        @Override
        public String toString() {
            return fNode.toString();
        }

    }

    private IValueFactory<?> fValueFactory;

    public StructuredContentBinding(
        IBinder binder,
        IValueFactory<?> valueFactory) {
        super(binder);
        fValueFactory = valueFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> IValueFactory<T> getValueFactory() {
        return (IValueFactory<T>) fValueFactory;
    }

    public void setValueFactory(IValueFactory<?> valueFactory) {
        fValueFactory = valueFactory;
    }

}