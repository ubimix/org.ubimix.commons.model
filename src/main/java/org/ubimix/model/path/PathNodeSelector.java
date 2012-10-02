/**
 * 
 */
package org.ubimix.model.path;

import java.util.Arrays;
import java.util.List;

/**
 * @author kotelnikov
 */
public class PathNodeSelector implements IPathSelector {

    private List<INodeSelector> fList;

    public PathNodeSelector(INodeSelector... list) {
        this(Arrays.<INodeSelector> asList(list));
    }

    public PathNodeSelector(List<INodeSelector> list) {
        fList = list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PathNodeSelector)) {
            return false;
        }
        PathNodeSelector o = (PathNodeSelector) obj;
        return fList.equals(o.fList);
    }

    @Override
    public INodeSelector getNodeSelector(int pos) {
        return fList.get(pos);
    }

    @Override
    public int getSelectorNumber() {
        return fList.size();
    }

    @Override
    public int hashCode() {
        return fList.hashCode();
    }

    @Override
    public String toString() {
        return fList.toString();
    }
}