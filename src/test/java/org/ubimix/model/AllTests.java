package org.ubimix.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ubimix.model.html.HtmlArticleBuilderTest;
import org.ubimix.model.html.HtmlDocumentTest;
import org.ubimix.model.html.StructuredTableTest;

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(CssSelectProcessorTest.class);
        suite.addTestSuite(InternalXmlParserTest.class);
        suite.addTestSuite(MixedJsonXmlModelTest.class);
        suite.addTestSuite(ModelObjectTest.class);
        suite.addTestSuite(PathProcessorTest.class);
        suite.addTestSuite(SaxXmlParserTest.class);
        suite.addTestSuite(SimpleStringMatcherTest.class);
        suite.addTestSuite(XmlTest.class);
        suite.addTestSuite(HtmlArticleBuilderTest.class);
        suite.addTestSuite(HtmlDocumentTest.class);
        suite.addTestSuite(HtmlArticleBuilderTest.class);
        suite.addTestSuite(StructuredTableTest.class);
        // $JUnit-END$
        return suite;
    }

}
