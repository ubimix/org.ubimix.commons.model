/**
 * 
 */
package org.ubimix.model.path;

import java.util.Iterator;

/**
 * @author kotelnikov
 */
public class PathProcessor {

    private INodeProvider fNodeProvider;

    private IPathSelector fSelectors;

    public PathProcessor(INodeProvider nodeProvider, IPathSelector filters) {
        fNodeProvider = nodeProvider;
        fSelectors = filters;
    }

    public void select(Object node, IPathNodeCollector collector) {
        selectNode(node, collector, 0);
    }

    private boolean selectNode(
        Object node,
        IPathNodeCollector collector,
        int selectorPos) {
        int selectorNumber = fSelectors.getSelectorNumber();
        if (selectorPos >= selectorNumber) {
            return true;
        }
        boolean shouldContinue = true;
        INodeSelector selector = fSelectors.getNodeSelector(selectorPos);
        INodeSelector.SelectionResult selectionResult = selector.accept(node);
        int childSelectorPos = -1;
        switch (selectionResult) {
            case YES:
                if (selectorPos + 1 >= selectorNumber) {
                    shouldContinue = collector.setResult(node);
                    childSelectorPos = selectorPos;
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
        if (shouldContinue
            && childSelectorPos >= 0
            && childSelectorPos < selectorNumber) {
            Iterator<?> children = fNodeProvider.getChildren(node);
            if (children != null) {
                while (children.hasNext()) {
                    Object child = children.next();
                    if (!selectNode(child, collector, childSelectorPos)) {
                        shouldContinue = false;
                        break;
                    }
                }
            }
        }
        return shouldContinue;
    }

}