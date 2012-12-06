package org.ubimix.model.cleaner;

import java.util.List;

import org.ubimix.model.xml.IXmlElement;
import org.ubimix.model.xml.IXmlNode;

/**
 * @author kotelnikov
 */
public interface ITagProcessor {

    ITagProcessor getParentProcessor();

    List<IXmlNode> handle(IXmlElement element, boolean keepSpaces);

    void setParent(ITagProcessor parentProcessor);
}