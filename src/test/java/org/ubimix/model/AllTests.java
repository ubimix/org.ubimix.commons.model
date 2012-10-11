package org.ubimix.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ubimix.model.path.CssSelectProcessorTest;
import org.ubimix.model.path.PathProcessorTest;
import org.ubimix.model.path.SimpleStringMatcherTest;
import org.ubimix.model.xml.InternalXmlParserTest;
import org.ubimix.model.xml.MixedJsonXmlModelTest;
import org.ubimix.model.xml.SaxXmlParserTest;
import org.ubimix.model.xml.XmlTest;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(ModelObjectTest.class);

        suite.addTestSuite(CssSelectProcessorTest.class);
        suite.addTestSuite(PathProcessorTest.class);
        suite.addTestSuite(SimpleStringMatcherTest.class);

        suite.addTestSuite(InternalXmlParserTest.class);
        suite.addTestSuite(MixedJsonXmlModelTest.class);
        suite.addTestSuite(SaxXmlParserTest.class);
        suite.addTestSuite(XmlTest.class);
        // $JUnit-END$
        return suite;
    }

}
