/**
 * 
 */
package org.ubimix.model.path;

/**
 * @author kotelnikov
 */
public interface INodeSelector {

    Boolean accept(Object node);
}