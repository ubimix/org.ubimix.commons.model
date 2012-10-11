/**
 * 
 */
package org.ubimix.model.path;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class SimpleStringMatcherTest extends TestCase {

    /**
     * Constructor for SimpleStringMatcherTest.
     * 
     * @param name
     */
    public SimpleStringMatcherTest(String name) {
        super(name);
    }

    public void checkMatch(String mask, String str, int expectedResult) {
        SimpleStringMatcher matcher = new SimpleStringMatcher();
        int result = matcher.check(mask, str, false);
        assertEquals(expectedResult, result);
    }

    public void test00() {
        checkMatch("asdf", "asdf", 0);
        checkMatch("asdf", "asdf1", 1);
        checkMatch("asdf", "asd", -1);
        checkMatch("asdf", "sdf", 1);
    }

    public void test01() {
        checkMatch("?", "a", 0);
        checkMatch("??", "as", 0);
        checkMatch("???", "asd", 0);
        checkMatch("????", "asdf", 0);

        checkMatch("X????", "asdf", 1);
        checkMatch("z????", "asdf", -1);
        checkMatch("????X", "asdf", -1);
    }

    public void test02() {
        checkMatch("asdf", "asdf", 0);
        checkMatch("a?df", "asdf", 0);
        checkMatch("a??f", "asdf", 0);
        checkMatch("?sdf", "asdf", 0);
        checkMatch("asd?", "asdf", 0);
        checkMatch("?sd?", "asdf", 0);
    }

    public void test04() {
        checkMatch("%", "asdf", 0);
        checkMatch("%f", "asdf", 0);
        checkMatch("%df", "asdf", 0);
        checkMatch("%sdf", "asdf", 0);

        checkMatch("x%sdf", "asdf", -1);
        checkMatch("a%sdf", "bsdf", 1);
    }

    public void test05() {
        checkMatch("asd%X", "asdf", -1);

        checkMatch("asd%", "asdf", 0);
        checkMatch("as%", "asdf", 0);
        checkMatch("a%", "asdf", 0);
        checkMatch("%", "asdf", 0);
    }

    public void test06() {
        checkMatch("a%f", "asdf", 0);
        checkMatch("a%%f", "asdf", 0);
        checkMatch("a%s%", "asdf", 0);
        checkMatch("%as%", "asdf", 0);
    }

    public void test07() {
        checkMatch("?%?", "asdf", 0);
        checkMatch("a?%?f", "asdf", 0);
    }

    public void testRandomMatch() {
        checkMatch("?xa%x", "dxasd%gblkjasdfc%x", 0);
        checkMatch("?xa%x%", "dxasd%gblkjasdfc%x", 0);
        checkMatch("%xa%gbl%x", "dxasd%gblkjasdfc%x", 0);

        checkMatch("%xa%gbl%X", "dxasd%gblkjasdfc%x", -1);
        checkMatch("%xa%gbl%X%x", "dxasd%gblkjasdfc%x", -1);
    }

}
