/**
 * 
 */
package org.ubimix.model.path;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kotelnikov
 */
public class PathSelectorBuilder {

    protected List<INodeSelector> fList = new ArrayList<INodeSelector>();

    public PathSelectorBuilder add(INodeSelector selector) {
        fList.add(selector);
        return this;
    }

    public IPathSelector build() {
        final ArrayList<INodeSelector> list = new ArrayList<INodeSelector>(
            fList);
        return new PathNodeSelector(list);
    }

    public PathProcessor buildPath(INodeProvider manager) {
        IPathSelector filters = build();
        return new PathProcessor(manager, filters);
    }

    public PathSelectorBuilder clear() {
        fList.clear();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PathSelectorBuilder)) {
            return false;
        }
        PathSelectorBuilder o = (PathSelectorBuilder) obj;
        return fList.equals(o.fList);
    }

    @Override
    public int hashCode() {
        return fList.hashCode();
    }

    public PathSelectorBuilder node() {
        add(new ThisSelector());
        return this;
    }

    @Override
    public String toString() {
        String name = getClass().getName();
        int idx = name.lastIndexOf(".");
        if (idx > 0) {
            name = name.substring(idx + 1);
        }
        return name + "( " + fList + " )";
    }
}
