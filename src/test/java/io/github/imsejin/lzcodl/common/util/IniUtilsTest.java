package io.github.imsejin.lzcodl.common.util;

import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.common.util.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IniUtilsTest {

    private final File file = new File(PathnameUtils.getCurrentPathname(), "config.ini");

    @Test
    public void readValue() throws IOException {
        // given
        String sectionName = "account";
        String name = "username";

        // when
        String actual = IniUtils.readValue(file, sectionName, name);

        // then
        assertFalse(StringUtils.isNullOrBlank(actual));
    }

    @Test
    public void readSection() throws IOException {
        // given
        String sectionName = "account";

        // when
        Map<String, String> map = IniUtils.readSection(file, sectionName);

        // then
        assertFalse(StringUtils.isNullOrBlank(map.get("username")));
        assertFalse(StringUtils.isNullOrBlank(map.get("password")));
    }

    @Test
    public void readValues() throws IOException {
        // given
        String sectionName = "account";

        // when
        List<String> values = IniUtils.readValues(file, sectionName);

        // then
        assertEquals(2, values.size());
    }

}
