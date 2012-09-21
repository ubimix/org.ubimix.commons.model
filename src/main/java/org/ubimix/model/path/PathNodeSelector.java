/**
 * 
 */
package org.ubimix.model.path;

import java.util.ArrayList;

/**
 * @author kotelnikov
 */
public class PathNodeSelector implements IPathSelector {

    private ArrayList<INodeSelector> fList;

    public PathNodeSelector(ArrayList<INodeSelector> list) {
        fList = list;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PathNodeSelector))
            return false;
        PathNodeSelector o = (PathNodeSelector) obj;
        return fList.equals(o.fList);
    }

    public INodeSelector getNodeSelector(int pos) {
        return fList.get(pos);
    }

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