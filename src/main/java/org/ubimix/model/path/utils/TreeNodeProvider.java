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
public class TreeNodeProvider implements INodeProvider {

    private IValueFactory<?> fFactory;

    private TreePresenter fTreePresenter;

    /**
     * 
     */
    public TreeNodeProvider(TreePresenter presenter, IValueFactory<?> factory) {
        fTreePresenter = presenter;
        fFactory = factory;
    }

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
        List<?> children = fTreePresenter.getChildren(
            element.getMap(),
            fFactory);
        return children.iterator();
    }

}
