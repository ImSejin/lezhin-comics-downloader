package io.github.imsejin.common.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FilenameUtilsTest {

    @Parameterized.Parameters
    public static Collection<Object> params() {
        return Arrays.asList(new String[][]{
                {"** <happy/\\new year> **:\"john\" -> |\"jeremy\"|", "＊＊ ＜happy／＼new year＞ ＊＊：˝john˝ -＞ ｜˝jeremy˝｜"},
                {"where he is gone..", "where he is gone…"},
                {"I feel happy when coding.", "I feel happy when coding．"},
        });
    }

    @Parameterized.Parameter(0)
    public String filename;

    @Parameterized.Parameter(1)
    public String expectedFilename;

//    private String filename;
//    private String expectedFilename;
//    public FilenameUtilsTest(String filename, String expectedFilename) {
//        this.filename = filename;
//        this.expectedFilename = expectedFilename;
//    }

    @Test
    public void testToSafeName() {
        // when
        String actual = FilenameUtils.toSafeName(filename);

        // then
        assertEquals(expectedFilename, actual);
    }

}
