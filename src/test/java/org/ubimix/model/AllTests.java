package org.ubimix.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ubimix.model.cleaner.InlineNodesBurnerTest;
import org.ubimix.model.cleaner.TagBurnerTest;
import org.ubimix.model.conversion.ConverterTest;
import org.ubimix.model.html.HtmlArticleBuilderTest;
import org.ubimix.model.html.HtmlDocumentTest;
import org.ubimix.model.html.StructuredNodesBindingTest;
import org.ubimix.model.html.StructuredTableTest;
import org.ubimix.model.html.StructuredTreeTest;

public class AllTests {

    /**
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        // $JUnit-BEGIN$
        suite.addTestSuite(CssSelectProcessorTest.class);
        suite.addTestSuite(ConverterTest.class);
        suite.addTestSuite(MixedJsonXmlModelTest.class);
        suite.addTestSuite(ModelObjectTest.class);
        suite.addTestSuite(PathProcessorTest.class);
        suite.addTestSuite(XmlTest.class);
        suite.addTestSuite(InternalXmlParserTest.class);
        suite.addTestSuite(SaxXmlParserTest.class);
        suite.addTestSuite(SimpleStringMatcherTest.class);

        suite.addTestSuite(HtmlArticleBuilderTest.class);
        suite.addTestSuite(HtmlDocumentTest.class);
        suite.addTestSuite(HtmlArticleBuilderTest.class);
        suite.addTestSuite(InlineNodesBurnerTest.class);
        suite.addTestSuite(TagBurnerTest.class);
        suite.addTestSuite(StructuredTableTest.class);
        suite.addTestSuite(StructuredTreeTest.class);
        suite.addTestSuite(StructuredNodesBindingTest.class);
        // $JUnit-END$
        return suite;
    }

}
