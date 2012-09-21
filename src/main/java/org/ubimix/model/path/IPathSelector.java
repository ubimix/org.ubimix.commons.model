/**
 * 
 */
package org.ubimix.model.path;

/**
 * @author kotelnikov
 */
public interface IPathSelector {

    INodeSelector getNodeSelector(int pos);

    int getSelectorNumber();

}