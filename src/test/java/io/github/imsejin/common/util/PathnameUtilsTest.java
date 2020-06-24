package io.github.imsejin.common.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class PathnameUtilsTest {

    @Test
    public void testRemoveFileSeparators() {
        // given
        String pathname = "C:\\Program Files\\Java";

        // when
        final String separatorForRegex = File.separator.equals("\\") ? "\\\\" : File.separator;
        String actual = pathname.replaceAll(separatorForRegex, "");

        // then
        System.out.println("testReplaceFileSeparators#actual: " + actual);
        assertEquals("C:Program FilesJava", actual);
    }

    @Test
    public void testCorrect() {
        // given
        String pathname = "\\// / C:\\ Program Files / \\/\\ \\ Java\\jdk8 /\\/ / \\ \\ ";

        // when
        String actual = PathnameUtils.correct(true, pathname);

        // then
        System.out.println("testCorrect#actual: " + actual);
        assertEquals("C:\\Program Files\\Java\\jdk8", actual);
    }

    @Test
    public void testConcat() {
        // given
//        String pathname1 = "/users";
//        String pathname2 = "/data/";
//        String pathname3 = "java";
        String pathname1 = " C:\\";
        String pathname2 = "/ Program Files \\ Java ";
        String pathname3 = " jdk8 ";

        // when
        String actual = PathnameUtils.concat(true, pathname1, pathname2, pathname3);

        // then
        System.out.println("testConcat#actual: " + actual);
//        assertEquals("/users/data/java", actual);
        assertEquals("C:\\Program Files\\Java\\jdk8", actual);
    }

}
