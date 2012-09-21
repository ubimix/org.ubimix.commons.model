/**
 * 
 */
package org.ubimix.model.path;

import java.util.Iterator;

public interface INodeProvider {

    Iterator<?> getChildren(Object parent);

}