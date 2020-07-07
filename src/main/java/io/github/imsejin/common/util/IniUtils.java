package io.github.imsejin.common.util;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class IniUtils {

    private IniUtils() {}

    public static Map<String, String> readSection(File file, String sectionName) throws IOException {
        return read(file).get(sectionName);
    }

    public static String readValue(File file, String sectionName, String name) throws IOException {
        Profile.Section section = read(file).get(sectionName);
        return section.get(name);
    }

    public static List<String> readValues(File file, String sectionName) throws IOException {
        Profile.Section section = read(file).get(sectionName);
        return new ArrayList<>(section.values());
    }

    private static Ini read(File file) throws IOException {
        Config conf = new Config();
        conf.setMultiSection(true);
        Ini ini = new Ini(file);
        ini.setConfig(conf);
        ini.load();

        return ini;
    }

}
