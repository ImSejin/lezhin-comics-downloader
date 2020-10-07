package io.github.imsejin.lzcodl.common.util;

import io.github.imsejin.common.util.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FilenameUtilsTest {

    @Parameterized.Parameter(0)
    public String filename;

    @Parameterized.Parameter(1)
    public String expectedFilename;

    @Parameterized.Parameters
    public static Collection<Object> params() {
        return Arrays.asList(new String[][]{
                {"** <happy/\\new year> **:\"john\" -> |\"jeremy\"|", "＊＊ ＜happy／＼new year＞ ＊＊：˝john˝ -＞ ｜˝jeremy˝｜"},
                {"where he is gone..", "where he is gone…"},
                {"I feel happy when coding.", "I feel happy when coding．"},
        });
    }

    @Test
    public void toSafeName() {
        // when
        String actual = FilenameUtils.replaceUnallowables(filename);

        // then
        assertThat(actual).isEqualTo(expectedFilename);
    }

}
