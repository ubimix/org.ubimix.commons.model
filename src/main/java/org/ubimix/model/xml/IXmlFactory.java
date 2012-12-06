package org.ubimix.model.xml;

import org.ubimix.commons.parser.ICharStream;

public interface IXmlFactory {

    IXmlCDATA newCDATA(String content);

    IXmlElement newElement(String name);

    IXmlText newText(String string);

    IXmlElement parse(ICharStream stream);

    IXmlElement parse(String xml);

}