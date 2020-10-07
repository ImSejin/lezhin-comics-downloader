package io.github.imsejin.lzcodl.common.util;

import io.github.imsejin.common.util.IniUtils;
import io.github.imsejin.common.util.PathnameUtils;
import io.github.imsejin.common.util.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(StringUtils.isNullOrBlank(actual)).isFalse();
    }

    @Test
    public void readSection() throws IOException {
        // given
        String sectionName = "account";

        // when
        Map<String, String> map = IniUtils.readSection(file, sectionName);

        // then
        assertThat(StringUtils.isNullOrBlank(map.get("username"))).isFalse();
        assertThat(StringUtils.isNullOrBlank(map.get("password"))).isFalse();
    }

    @Test
    public void readValues() throws IOException {
        // given
        String sectionName = "account";

        // when
        List<String> values = IniUtils.readValues(file, sectionName);

        // then
        assertThat(values).hasSize(2);
    }

}
