package org.ubimix.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(CssSelectProcessorTest.class);
        suite.addTestSuite(InternalXmlParserTest.class);
        suite.addTestSuite(MixedJsonXmlModelTest.class);
        suite.addTestSuite(ModelObjectTest.class);
        suite.addTestSuite(PathProcessorTest.class);
        suite.addTestSuite(SaxXmlParserTest.class);
        suite.addTestSuite(SimpleStringMatcherTest.class);
        suite.addTestSuite(XmlTest.class);
        //$JUnit-END$
        return suite;
    }

}
