/**
 * 
 */
package org.ubimix.model.selector;

/**
 * @author kotelnikov
 */
public interface IPathSelector {

    INodeSelector getNodeSelector(int pos);

    int getSelectorNumber();

}