/**
 * 
 */
package org.ubimix.model.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kotelnikov
 */
public class PathNodeSelector implements IPathSelector {

    private List<INodeSelector> fList = new ArrayList<INodeSelector>();

    public PathNodeSelector(INodeSelector... list) {
        setNodeSelectors(list);
    }

    public PathNodeSelector(List<INodeSelector> list) {
        setNodeSelectors(list);
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

    public void setNodeSelectors(INodeSelector... list) {
        setNodeSelectors(Arrays.<INodeSelector> asList(list));
    }

    public void setNodeSelectors(List<INodeSelector> list) {
        fList.clear();
        fList.addAll(list);
    }

    @Override
    public String toString() {
        return fList.toString();
    }
}