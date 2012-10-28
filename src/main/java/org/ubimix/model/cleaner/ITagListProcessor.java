package org.ubimix.model.cleaner;

import java.util.List;

import org.ubimix.model.xml.XmlElement;
import org.ubimix.model.xml.XmlNode;

/**
 * @author kotelnikov
 */
public interface ITagListProcessor {

    ITagListProcessor getParentProcessor();

    List<XmlNode> handle(XmlElement element, boolean keepSpaces);

    void setParent(ITagListProcessor parentProcessor);
}