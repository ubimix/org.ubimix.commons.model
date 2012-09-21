/**
 * 
 */
package org.ubimix.model.path;

import java.util.Iterator;

/**
 * @author kotelnikov
 */
public class PathProcessor {

    private IPathSelector fFilters;

    private INodeProvider fNodeProvider;

    public PathProcessor(INodeProvider nodeProvider, IPathSelector filters) {
        fNodeProvider = nodeProvider;
        fFilters = filters;
    }

    public void select(Object node, IPathNodeCollector collector) {
        selectNode(node, collector, 0);
    }

    private boolean selectNode(
        Object node,
        IPathNodeCollector collector,
        int selectorPos) {
        int filterNumber = fFilters.getSelectorNumber();
        if (selectorPos >= filterNumber)
            return true;
        boolean result = true;
        INodeSelector filter = fFilters.getNodeSelector(selectorPos);
        INodeSelector.Accept accept = filter.accept(node);
        int childSelectorPos = -1;
        switch (accept) {
            case YES:
                if (selectorPos + 1 >= filterNumber) {
                    result = collector.setResult(node);
                } else {
                    childSelectorPos = selectorPos + 1;
                }
                break;
            case NO:
                // Just exit. This node should not be explored anymore.
                break;
            case MAYBE:
                // This node is skipped, so we have to apply the same select
                // criteria to all children.
                childSelectorPos = selectorPos;
                break;
        }
        if (childSelectorPos >= 0 && childSelectorPos < filterNumber) {
            Iterator<?> children = fNodeProvider.getChildren(node);
            if (children != null) {
                while (children.hasNext()) {
                    Object child = children.next();
                    if (!selectNode(child, collector, childSelectorPos)) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

}