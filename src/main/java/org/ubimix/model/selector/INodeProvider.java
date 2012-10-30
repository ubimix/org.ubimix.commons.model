/**
 * 
 */
package org.ubimix.model.selector;

import java.util.Iterator;

public interface INodeProvider {

    Iterator<?> getChildren(Object parent);

}