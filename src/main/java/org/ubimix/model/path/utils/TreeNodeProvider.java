/**
 * 
 */
package org.ubimix.model.path.utils;

import java.util.Iterator;
import java.util.List;

import org.ubimix.model.IHasValueMap;
import org.ubimix.model.IValueFactory;
import org.ubimix.model.TreePresenter;
import org.ubimix.model.path.INodeProvider;

/**
 * @author kotelnikov
 */
public abstract class TreeNodeProvider implements INodeProvider {

    private TreePresenter fTreePresenter;

    /**
     * 
     */
    public TreeNodeProvider(TreePresenter presenter) {
        fTreePresenter = presenter;
    }

    protected abstract IValueFactory<?> getChildNodeFactory(IHasValueMap element);

    /**
     * @see org.ubimix.model.path.INodeProvider
     *      <T>#getChildren(java.lang.Object)
     */
    @Override
    public Iterator<?> getChildren(final Object parent) {
        if (!(parent instanceof IHasValueMap)) {
            return null;
        }
        final IHasValueMap element = (IHasValueMap) parent;
        IValueFactory<?> fFactory = getChildNodeFactory(element);
        List<?> children = fTreePresenter.getChildren(
            element.getMap(),
            fFactory);
        return children.iterator();
    }

}
