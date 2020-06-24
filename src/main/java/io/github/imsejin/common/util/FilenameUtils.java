package io.github.imsejin.common.util;

import java.io.File;

/**
 * 파일명 유틸리티<br>
 * Filename utilities
 * 
 * 
 * @author SEJIN
 */
public final class FilenameUtils {

    private FilenameUtils() {}

    private static final char EXTENSION_SEPARATOR = '.';

    /**
     * 확장자의 위치를 반환한다.<br>
     * Returns the position of the extension.
     * 
     * <pre>
     * File file = new File("D:/Program Files/Java/jdk1.8.0_202/README.html");
     * FilenameUtils.indexOfExtension(file): 6
     * 
     * File anotherFile = new File("D:/Program Files/Java/jdk1.8.0_202/.gitignore");
     * FilenameUtils.indexOfExtension(anotherFile): -1
     * </pre>
     */
    public static int indexOfExtension(String filename) {
        if (filename == null) return -1;

        int index = filename.lastIndexOf(EXTENSION_SEPARATOR);
        return index == 0 ? -1 : index;
    }

    /**
     * 확장자를 제외한 파일명을 반환한다.<br>
     * Returns the filename excluding the extension.
     * 
     * <pre>
     * File file = new File("D:/Program Files/Java/jdk1.8.0_202/README.html");
     * FilenameUtils.baseName(file): "README"
     * 
     * File anotherFile = new File("D:/Program Files/Java/jdk1.8.0_202/LICENSE");
     * FilenameUtils.baseName(anotherFile): "LICENSE"
     * </pre>
     */
    public static String baseName(File file) {
        if (file == null) return "";

        String filename = file.getName();
        int index = indexOfExtension(filename);
        return index == -1
                ? filename
                : filename.substring(0, index);
    }

    /**
     * 파일의 확장자를 반환한다.<br>
     * Returns the file's extension.
     * 
     * <pre>
     * File file = new File("D:/Program Files/Java/jdk1.8.0_202/README.html");
     * FilenameUtils.extension(file): "html"
     * </pre>
     */
    public static String extension(File file) {
        if (file == null) return "";

        String filename = file.getName();
        int index = indexOfExtension(filename);
        return index == -1
                ? ""
                : filename.substring(index + 1);
    }
    
    /**
     * 파일명에 사용할 수 없는 문자를 특수문자로 치환한다.<br>
     * Replace a character that cannot be used in a file name with a special character.
     * 
     * <pre>
     * "\\" -> "＼"
     * "/"  -> "／"
     * ":"  -> "："
     * "*"  -> "＊"
     * "?"  -> "？"
     * "\"" -> "˝"
     * "<"  -> "＜"
     * ">"  -> "＞"
     * "|"  -> "｜"
     * 
     * String filename = "** <happy/\\new year> **:\"john\" -> |\"jeremy\"|";
     * FilenameUtils.toSafeName(filename): "＊＊ ＜happy／＼new year＞ ＊＊：˝john˝ -＞ ｜˝jeremy˝｜"
     * 
     * FilenameUtils.toSafeName("where he is gone.."): "where he is gone…"
     * FilenameUtils.toSafeName("I feel happy when coding."): "I feel happy when coding．"
     * </pre>
     */
    public static String toSafeName(String filename) {
        return filename.replaceAll("\\\\", "＼")
                .replaceAll("/", "／")
                .replaceAll(":", "：")
                .replaceAll("\\*", "＊")
                .replaceAll("\\?", "？")
                .replaceAll("\"", "˝")
                .replaceAll("<", "＜")
                .replaceAll(">", "＞")
                .replaceAll("\\|", "｜")
                .replaceAll("\\.{2,}+$", "…")
                .replaceAll("\\.$", "．");
    }

}
