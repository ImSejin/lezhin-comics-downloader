package io.github.imsejin.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FilenameUtilsTest {

    @Test
    public void testToSafeName() {
        // given
        String filename1 = "** <happy/\\new year> **:\"john\" -> |\"jeremy\"|";
        String filename2 = "where he is gone..";
        String filename3 = "I feel happy when coding.";

        // when
        String actual1 = FilenameUtils.toSafeName(filename1);
        String actual2 = FilenameUtils.toSafeName(filename2);
        String actual3 = FilenameUtils.toSafeName(filename3);

        // then
        System.out.println("testToSafeName#actual: " + actual1);
        assertEquals("＊＊ ＜happy／＼new year＞ ＊＊：˝john˝ -＞ ｜˝jeremy˝｜", actual1);
        System.out.println("testToSafeName#actual: " + actual2);
        assertEquals("where he is gone…", actual2);
        System.out.println("testToSafeName#actual: " + actual3);
        assertEquals("I feel happy when coding．", actual3);
    }

}
