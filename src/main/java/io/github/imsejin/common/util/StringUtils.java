package io.github.imsejin.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 문자열 유틸리티<br>
 * String utilities
 * 
 * <p>
 * 
 * </p>
 * 
 * @author SEJIN
 */
public final class StringUtils {

    private StringUtils() {}

	/** The Constant WHITE_SPACE. */ 
	private static final char WHITE_SPACE = ' ';

	/**
	 * Checks if is alpha.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alpha
	 */
	public static boolean isAlpha(String str) {

		if (str == null) {
			return false;
		}

		int size = str.length();

		if (size == 0)
			return false;

		for (int i = 0; i < size; i++) {
			if (!Character.isLetter(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if is alpha numeric.
	 *
	 * @param str
	 *            the str
	 * @return true, if is alpha numeric
	 */
	public static boolean isAlphaNumeric(String str) {

		if (str == null) {
			return false;
		}

		int size = str.length();

		if (size == 0)
			return false;

		for (int i = 0; i < size; i++) {
			if (!Character.isLetterOrDigit(str.charAt(i))) {
				return false;
			}
		}

		return true;
	}

    /**
     * Null2void.
     *
     * @param str
     *            the str
     * @return the string
     */
    public static String null2void(String str) {
        return isBlank(str) ? "" : str;
    }

    /**
     * String2integer.
     *
     * @param str
     *            the str
     * @return the int
     */
    public static int string2integer(String str) {
        return string2integer(str, 0);
    }

    /**
     * String2integer.
     *
     * @param str
     *            the str
     * @param defaultValue
     *            the default value
     * @return the int
     */
    public static int string2integer(String str, int defaultValue) {
        return isBlank(str) ? defaultValue : Integer.parseInt(str);
    }

    /**
     * String2float.
     *
     * @param str
     *            the str
     * @return the float
     */
    public static float string2float(String str) {
        return string2float(str, 0.0F);
    }

    /**
     * String2float.
     *
     * @param str
     *            the str
     * @param defaultValue
     *            the default value
     * @return the float
     */
    public static float string2float(String str, float defaultValue) {
        return isBlank(str) ? defaultValue : Float.parseFloat(str);
    }

    /**
     * String2double.
     *
     * @param str
     *            the str
     * @return the double
     */
    public static double string2double(String str) {
        return string2double(str, 0.0D);
    }

    /**
     * String2double.
     *
     * @param str
     *            the str
     * @param defaultValue
     *            the default value
     * @return the double
     */
    public static double string2double(String str, double defaultValue) {
        return isBlank(str) ? defaultValue : Double.parseDouble(str);
    }

    /**
     * String2long.
     *
     * @param str
     *            the str
     * @return the long
     */
    public static long string2long(String str) {
        return string2long(str, 0L);
    }

    /**
     * String2long.
     *
     * @param str
     *            the str
     * @param defaultValue
     *            the default value
     * @return the long
     */
    public static long string2long(String str, long defaultValue) {
        return isBlank(str) ? defaultValue : Long.parseLong(str);
    }

    /**
     * Null2string.
     *
     * @param str
     *            the str
     * @param defaultValue
     *            the default value
     * @return the string
     */
    public static String null2string(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str;
    }

	/**
	 * Search.
	 *
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @return the int
	 */
	public static int search(String source, String target) {
		int result = 0;
		String strCheck = source;
		for (int i = 0; i < source.length();) {
			int loc = strCheck.indexOf(target);
			if (loc == -1) {
				break;
			} else {
				result++;
				i = loc + target.length();
				strCheck = strCheck.substring(i);
			}
		}
		return result;
	}

	/**
	 * Ltrim.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String ltrim(String str) {

		int index = 0;

		while (' ' == str.charAt(index++))
			;

		if (index > 0)
			str = str.substring(index - 1);

		return str;
	}

	/**
	 * Rtrim.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String rtrim(String str) {

		int index = str.length();

		while (' ' == str.charAt(--index))
			;

		if (index < str.length())
			str = str.substring(0, index + 1);

		return str;
	}

	/**
	 * L pad.
	 *
	 * @param num
	 *            the num
	 * @param len
	 *            the len
	 * @param pad
	 *            the pad
	 * @return the string
	 */
	public static String lPad(int num, int len, char pad) {
	    return lPad(String.valueOf(num), len, pad, false);
	}

	/**
	 * L pad.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @param pad
	 *            the pad
	 * @return the string
	 */
	public static String lPad(String str, int len, char pad) {
		return lPad(str, len, pad, false);
	}

	/**
	 * L pad.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @param pad
	 *            the pad
	 * @param isTrim
	 *            the is trim
	 * @return the string
	 */
    public static String lPad(String str, int len, char pad, boolean isTrim) {
        if (isBlank(str)) return null;

        if (isTrim) str = str.trim();

        StringBuilder sb = new StringBuilder();
        for (int i = str.length(); i < len; i++) {
            sb.append(pad);
        }
        sb.append(str);

        return sb.toString();
    }

    /**
     * R pad.
     *
     * @param num
     *            the num
     * @param len
     *            the len
     * @param pad
     *            the pad
     * @return the string
     */
    public static String rPad(int num, int len, char pad) {
        return rPad(String.valueOf(num), len, pad, false);
    }

	/**
	 * R pad.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @param pad
	 *            the pad
	 * @return the string
	 */
	public static String rPad(String str, int len, char pad) {
		return rPad(str, len, pad, false);
	}

	/**
	 * R pad.
	 *
	 * @param str
	 *            the str
	 * @param len
	 *            the len
	 * @param pad
	 *            the pad
	 * @param isTrim
	 *            the is trim
	 * @return the string
	 */
    public static String rPad(String str, int len, char pad, boolean isTrim) {
        if (isBlank(str)) return null;

        if (isTrim) str = str.trim();

        StringBuilder sb = new StringBuilder(str);
        for (int i = str.length(); i < len; i++) {
            sb.append(pad);
        }

        return sb.toString();
    }

	/**
	 * Align left.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String alignLeft(String str, int length) {
		return alignLeft(str, length, false);
	}

	/**
	 * <p>
	 * 문자열의 뒷쪽에 지정한 길이만큼 공백으로 채움
	 * </p>
	 * .
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @param isEllipsis
	 *            the is ellipsis
	 * @return the string
	 */
	public static String alignLeft(String str, int length, boolean isEllipsis) {

		if (str.length() <= length) {

			StringBuffer temp = new StringBuffer(str);
			for (int i = 0; i < (length - str.length()); i++) {
				temp.append(WHITE_SPACE);
			}
			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(str, 0, length - 3);
				temp.append("...");

				return temp.toString();
			} else {
				return str.substring(0, length);
			}
		}
	}

	/**
	 * Align right.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String alignRight(String str, int length) {

		return alignRight(str, length, false);
	}

	/**
	 * Align right.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @param isEllipsis
	 *            the is ellipsis
	 * @return the string
	 */
	public static String alignRight(String str, int length, boolean isEllipsis) {

		if (str.length() <= length) {

			StringBuffer temp = new StringBuffer(length);
			for (int i = 0; i < (length - str.length()); i++) {
				temp.append(WHITE_SPACE);
			}
			temp.append(str);
			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(str, 0, length - 3);
				temp.append("...");
				return temp.toString();
			} else {
				return str.substring(0, length);
			}
		}
	}

	/**
	 * Align center.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String alignCenter(String str, int length) {
		return alignCenter(str, length, false);
	}

	/**
	 * Align center.
	 *
	 * @param str
	 *            the str
	 * @param length
	 *            the length
	 * @param isEllipsis
	 *            the is ellipsis
	 * @return the string
	 */
	public static String alignCenter(String str, int length, boolean isEllipsis) {
		if (str.length() <= length) {

			StringBuffer temp = new StringBuffer(length);
			int leftMargin = (length - str.length()) / 2;

			int rightMargin;
			if ((leftMargin * 2) == (length - str.length())) {
				rightMargin = leftMargin;
			} else {
				rightMargin = leftMargin + 1;
			}

			for (int i = 0; i < leftMargin; i++) {
				temp.append(WHITE_SPACE);
			}

			temp.append(str);

			for (int i = 0; i < rightMargin; i++) {
				temp.append(WHITE_SPACE);
			}

			return temp.toString();
		} else {
			if (isEllipsis) {

				StringBuffer temp = new StringBuffer(length);
				temp.append(str, 0, length - 3);
				temp.append("...");
				return temp.toString();
			} else {
				return str.substring(0, length);
			}
		}

	}

	/**
	 * Capitalize.
	 *
	 * @param str
	 *            the str
	 * @return the string
	 */
	public static String capitalize(String str) {
		return isNotBlank(str) ? str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() : str;
	}

	/**
	 * Checks if is pattern match.
	 *
	 * @param str
	 *            the str
	 * @param pattern
	 *            the pattern
	 * @return true, if is pattern match
	 * @throws Exception
	 *             the exception
	 */
	public static boolean isPatternMatch(String str, String pattern) throws Exception {
		Matcher matcher = Pattern.compile(pattern).matcher(str);
		return matcher.matches();
	}

	/**
	 * Count of.
	 *
	 * @param str
	 *            the str
	 * @param charToFind
	 *            the char to find
	 * @return the int
	 */
	public static int countOf(String str, String charToFind) {
		int findLength = charToFind.length();
		int count = 0;

		for (int idx = str.indexOf(charToFind); idx >= 0; idx = str.indexOf(charToFind, idx + findLength)) {
			count++;
		}

		return count;
	}

	/**
	 * convert first letter to a big letter or a small letter.<br>
	 * 
	 * <pre>
	 * StringUtils.trim('Password') = 'password'
	 * StringUtils.trim('password') = 'Password'
	 * </pre>
	 * 
	 * @param str
	 *            String to be swapped
	 * @return String converting result
	 */
	public static String swapFirstLetterCase(String str) {
		StringBuffer sbuf = new StringBuffer(str);
		sbuf.deleteCharAt(0);
		if (Character.isLowerCase(str.substring(0, 1).toCharArray()[0])) {
			sbuf.insert(0, str.substring(0, 1).toUpperCase());
		} else {
			sbuf.insert(0, str.substring(0, 1).toLowerCase());
		}
		return sbuf.toString();
	}

	/**
	 * If original String has a specific String, remove specific Strings from
	 * original String.
	 * 
	 * <pre>
	 * StringUtils.trim('pass*word', '*') = 'password'
	 * </pre>
	 * 
	 * @param origString
	 *            original String
	 * @param trimString
	 *            String to be trimmed
	 * @return converting result
	 */
	public static String trim(String origString, String trimString) {
		int startPosit = origString.indexOf(trimString);
		if (startPosit != -1) {
			int endPosit = trimString.length() + startPosit;
			return origString.substring(0, startPosit) + origString.substring(endPosit);
		}
		return origString;
	}

	/**
	 * Break a string into specific tokens and return a String of last location.
	 * 
	 * <pre>
	 * StringUtils.getLastString('password*password*a*b*c', '*') = 'c'
	 * </pre>
	 * 
	 * @param origStr
	 *            original String
	 * @param strToken
	 *            specific tokens
	 * @return String of last location
	 */
	public static String getLastString(String origStr, String strToken) {
		StringTokenizer str = new StringTokenizer(origStr, strToken);
		String lastStr = "";
		while (str.hasMoreTokens()) {
			lastStr = str.nextToken();
		}
		return lastStr;
	}

	/**
	 * If original String has token, Break a string into specific tokens and change
	 * String Array. If not, return a String Array which has original String as it
	 * is.
	 * 
	 * <pre>
	 * StringUtils.getStringArray('passwordabcpassword', 'abc') 		= String[]{'password','password'}
	 * StringUtils.getStringArray('pasword*password', 'abc') 		= String[]{'pasword*password'}
	 * </pre>
	 * 
	 * @param str
	 *            original String
	 * @param strToken
	 *            specific String token
	 * @return String[]
	 */
	public static String[] getStringArray(String str, String strToken) {
		if (str.indexOf(strToken) != -1) {
			StringTokenizer st = new StringTokenizer(str, strToken);
			String[] stringArray = new String[st.countTokens()];
			for (int i = 0; st.hasMoreTokens(); i++) {
				stringArray[i] = st.nextToken();
			}
			return stringArray;
		}
		return new String[] { str };
	}

	/**
	 * If string is null or empty string, return false. <br>
	 * If not, return true.
	 * 
	 * <pre>
	 * StringUtils.isNotEmpty('') 		= false
	 * StringUtils.isNotEmpty(null) 		= false
	 * StringUtils.isNotEmpty('abc') 	= true
	 * </pre>
	 * 
	 * @param str
	 *            original String
	 * @return which empty string or not.
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * If string is null or empty string, return true. <br>
	 * If not, return false.
	 * 
	 * <pre>
	 * StringUtils.isEmpty('') 		= true
	 * StringUtils.isEmpty(null) 	= true
	 * StringUtils.isEmpty('abc') 	= false
	 * </pre>
	 * 
	 * @param str
	 *            original String
	 * @return which empty string or not.
	 */
	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}

	/**
	 * replace replaced string to specific string from original string. <br>
	 * 
	 * <pre>
	 * StringUtils.replace('work$id', '$', '.') 	= 'work.id'
	 * </pre>
	 * 
	 * @param str
	 *            original String
	 * @param replacedStr
	 *            to be replaced String
	 * @param replaceStr
	 *            replace String
	 * @return converting result
	 */
	public static String replace(String str, String replacedStr, String replaceStr) {
		String newStr = "";
		if (str.indexOf(replacedStr) != -1) {
			String s1 = str.substring(0, str.indexOf(replacedStr));
			String s2 = str.substring(str.indexOf(replacedStr) + 1);
			newStr = s1 + replaceStr + s2;
		}
		return newStr;
	}

	/**
	 * It converts the string representation of a number to integer type (eg. '27'
	 * -> 27)
	 * 
	 * <pre>
	 * StringUtils.string2integer('14') 	= 14
	 * </pre>
	 *
	 * @param str
	 *            original String
	 * @param pattern
	 *            pattern String
	 * @return integer integer type of string public static int
	 *         string2integer(String str) { int ret = Integer.parseInt(str.trim());
	 *         return ret; } /** It converts integer type to String ( 27 -> '27')
	 * 
	 *         <pre>
	 * StringUtils.integer2string(14) 	= '14'
	 *         </pre>
	 * 
	 *         String string representation of a number public static String
	 *         integer2string(int integer) { return ("" + integer); } /** It returns
	 *         true if str matches the pattern string. It performs regular
	 *         expression pattern matching.
	 * 
	 *         <pre>
	 * StringUtils.isPatternMatching('abc-def', '*-*') 	= true
	 * StringUtils.isPatternMatching('abc', '*-*') 	= false
	 *         </pre>
	 * 
	 *         boolean which matches the pattern string or not.
	 * @throws Exception
	 *             fail to check pattern matched
	 */
	public static boolean isPatternMatching(String str, String pattern) throws Exception {
		// if url has wild key, i.e. "*", convert it to
		// ".*" so that we can
		// perform regex matching
		if (pattern.indexOf('*') >= 0) {
			pattern = pattern.replaceAll("\\*", ".*");
		}

		pattern = "^" + pattern + "$";

		return Pattern.matches(pattern, str);
	}

	/**
	 * It returns true if string contains a sequence of the same character.
	 * 
	 * <pre>
	 * StringUtils.containsMaxSequence('password', '2') 	= true
	 * StringUtils.containsMaxSequence('my000', '3') 	= true
	 * StringUtils.containsMaxSequence('abbbbc', '5')	= false
	 * </pre>
	 * 
	 * @param str
	 *            original String
	 * @param maxSeqNumber
	 *            a sequence of the same character
	 * @return which contains a sequence of the same character
	 */
	public static boolean containsMaxSequence(String str, String maxSeqNumber) {
		int occurence = 1;
		int max = string2integer(maxSeqNumber);
		if (str == null) {
			return false;
		}

		int sz = str.length();
		for (int i = 0; i < (sz - 1); i++) {
			if (str.charAt(i) == str.charAt(i + 1)) {
				occurence++;

				if (occurence == max)
					return true;
			} else {
				occurence = 1;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Checks that the String contains certain characters.
	 * </p>
	 * <p>
	 * A <code>null</code> String will return <code>false</code>. A
	 * <code>null</code> invalid character array will return <code>false</code>. An
	 * empty String ("") always returns false.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.containsInvalidChars(null, *)       			= false
	 * StringUtils.containsInvalidChars(*, null)      			= false
	 * StringUtils.containsInvalidChars(&quot;&quot;, *)         = false
	 * StringUtils.containsInvalidChars(&quot;ab&quot;, '')      = false
	 * StringUtils.containsInvalidChars(&quot;abab&quot;, 'xyz') = false
	 * StringUtils.containsInvalidChars(&quot;ab1&quot;, 'xyz')  = false
	 * StringUtils.containsInvalidChars(&quot;xbz&quot;, 'xyz')  = true
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param invalidChars
	 *            an array of invalid chars, may be null
	 * @return false if it contains none of the invalid chars, or is null
	 */

	public static boolean containsInvalidChars(String str, char[] invalidChars) {
		if (str == null || invalidChars == null) {
			return false;
		}
		int strSize = str.length();
		int validSize = invalidChars.length;
		for (int i = 0; i < strSize; i++) {
			char ch = str.charAt(i);
			for (int j = 0; j < validSize; j++) {
				if (invalidChars[j] == ch) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Checks that the String contains certain characters.
	 * </p>
	 * <p>
	 * A <code>null</code> String will return <code>false</code>. A
	 * <code>null</code> invalid character array will return <code>false</code>. An
	 * empty String ("") always returns false.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.containsInvalidChars(null, *)       			= false
	 * StringUtils.containsInvalidChars(*, null)      			= false
	 * StringUtils.containsInvalidChars(&quot;&quot;, *)         = false
	 * StringUtils.containsInvalidChars(&quot;ab&quot;, '')      = false
	 * StringUtils.containsInvalidChars(&quot;abab&quot;, 'xyz') = false
	 * StringUtils.containsInvalidChars(&quot;ab1&quot;, 'xyz')  = false
	 * StringUtils.containsInvalidChars(&quot;xbz&quot;, 'xyz')  = true
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param invalidChars
	 *            a String of invalid chars, may be null
	 * @return false if it contains none of the invalid chars, or is null
	 */
	public static boolean containsInvalidChars(String str, String invalidChars) {
		if (str == null || invalidChars == null) {
			return true;
		}
		return containsInvalidChars(str, invalidChars.toCharArray());
	}

	/**
	 * <p>
	 * Checks if the String contains only unicode letters or digits.
	 * </p>
	 * <p>
	 * <code>null</code> will return <code>false</code> . An empty String ("") will
	 * return <code>false</code>.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isAlphaNumeric(null)   			 = false
	 * StringUtils.isAlphaNumeric(&quot;&quot;)     = false
	 * StringUtils.isAlphaNumeric(&quot;  &quot;)   = false
	 * StringUtils.isAlphaNumeric(&quot;abc&quot;)  = true
	 * StringUtils.isAlphaNumeric(&quot;ab c&quot;) = false
	 * StringUtils.isAlphaNumeric(&quot;ab2c&quot;) = true
	 * StringUtils.isAlphaNumeric(&quot;ab-c&quot;) = false
	 * </pre>
	 *
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if only contains letters or digits, and is non-null
	 *         public static boolean isAlphaNumeric(String str) { if (str == null) {
	 *         return false; } int sz = str.length(); if (sz == 0) return false; for
	 *         (int i = 0; i < sz; i++) { if (!Character
	 *         .isLetterOrDigit(str.charAt(i))) { return false; } } return true; }
	 *         /**
	 *         <p>
	 *         Checks if the String contains only unicode letters.
	 *         </p>
	 *         <p>
	 *         <code>null</code> will return <code>false</code>. An empty String
	 *         ("") will return <code>false</code>.
	 *         </p>
	 * 
	 *         <pre>
	 * StringUtils.isAlpha(null)   			= false
	 * StringUtils.isAlpha(&quot;&quot;)     = false
	 * StringUtils.isAlpha(&quot;  &quot;)   = false
	 * StringUtils.isAlpha(&quot;abc&quot;)  = true
	 * StringUtils.isAlpha(&quot;ab2c&quot;) = false
	 * StringUtils.isAlpha(&quot;ab-c&quot;) = false
	 *         </pre>
	 * 
	 *         <code>true</code> if only contains letters, and is non-null public
	 *         boolean isAlpha(String str) { if (str == null) { return false;
	 *         } int sz = str.length(); if (sz == 0) return false; for (int i = 0; i
	 *         < sz; i++) { if (!Character.isLetter(str.charAt(i))) { return false;
	 *         } } return true; } /**
	 *         <p>
	 *         Checks if the String contains only unicode digits. A decimal point is
	 *         not a unicode digit and returns false.
	 *         </p>
	 *         <p>
	 *         <code>null</code> will return <code>false</code>. An empty String
	 *         ("") will return <code>false</code>.
	 *         </p>
	 * 
	 *         <pre>
	 * StringUtils.isNumeric(null)   		   = false
	 * StringUtils.isNumeric(&quot;&quot;)     = false
	 * StringUtils.isNumeric(&quot;  &quot;)   = false
	 * StringUtils.isNumeric(&quot;123&quot;)  = true
	 * StringUtils.isNumeric(&quot;12 3&quot;) = false
	 * StringUtils.isNumeric(&quot;ab2c&quot;) = false
	 * StringUtils.isNumeric(&quot;12-3&quot;) = false
	 * StringUtils.isNumeric(&quot;12.3&quot;) = false
	 *         </pre>
	 * 
	 *         <code>true</code> if only contains digits, and is non-null
	 */
	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		if (sz == 0)
			return false;
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Reverses a String as per.
	 *
	 * @param str
	 *            the String to reverse, may be null
	 * @return the reversed String, <code>null</code> if null String input
	 *         {@link StringBuffer#reverse()}.
	 *         </p>
	 *         <p>
	 *         <A code>null</code> String returns <code>null</code>.
	 *         </p>
	 * 
	 *         <pre>
	 * StringUtils.reverse(null)  		   = null
	 * StringUtils.reverse(&quot;&quot;)    = &quot;&quot;
	 * StringUtils.reverse(&quot;bat&quot;) = &quot;tab&quot;
	 *         </pre>
	 */

	public static String reverse(String str) {
		if (str == null) {
			return null;
		}
		return new StringBuffer(str).reverse().toString();
	}

	/**
	 * Make a new String that filled original to a special char as cipers.
	 *
	 * @param originalStr
	 *            original String
	 * @param ch
	 *            a special char
	 * @param cipers
	 *            cipers
	 * @return filled String
	 */
	public static String fillString(String originalStr, char ch, int cipers) {
		int originalStrLength = originalStr.length();

		if (cipers < originalStrLength)
			return null;

		int difference = cipers - originalStrLength;

		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < difference; i++)
			strBuf.append(ch);

		strBuf.append(originalStr);
		return strBuf.toString();
	}

	/**
	 * Determine whether a (trimmed) string is empty.
	 *
	 * @param foo
	 *            The text to check.
	 * @return Whether empty.
	 */
	public static boolean isEmptyTrimmed(String foo) {
		return (foo == null || foo.trim().length() == 0);
	}

	/**
	 * Return token list.
	 *
	 * @param lst
	 *            the lst
	 * @param separator
	 *            the separator
	 * @return the tokens
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getTokens(String lst, String separator) {
		List tokens = new ArrayList();

		if (lst != null) {
			StringTokenizer st = new StringTokenizer(lst, separator);
			while (st.hasMoreTokens()) {
				try {
					String en = st.nextToken().trim();
					tokens.add(en);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return tokens;
	}

	/**
	 * Return token list which is separated by ",".
	 *
	 * @param lst
	 *            the lst
	 * @return the tokens
	 */
	@SuppressWarnings("rawtypes")
	public static List getTokens(String lst) {
		return getTokens(lst, ",");
	}

	/**
	 * This method convert "string_util" to "stringUtil".
	 *
	 * @param targetString
	 *            the target string
	 * @param posChar
	 *            the pos char
	 * @return String result
	 */
	public static String convertToCamelCase(String targetString, char posChar) {
		StringBuffer result = new StringBuffer();
		boolean nextUpper = false;
		String allLower = targetString.toLowerCase();

		for (int i = 0; i < allLower.length(); i++) {
			char currentChar = allLower.charAt(i);
			if (currentChar == posChar) {
				nextUpper = true;
			} else {
				if (nextUpper) {
					currentChar = Character.toUpperCase(currentChar);
					nextUpper = false;
				}
				result.append(currentChar);
			}
		}
		return result.toString();
	}

	/**
	 * Convert a string that may contain underscores to camel case.
	 * 
	 * @param underScore
	 *            Underscore name.
	 * @return Camel case representation of the underscore string.
	 */
	public static String convertToCamelCase(String underScore) {
		return convertToCamelCase(underScore, '_');
	}

	/**
	 * Convert a camel case string to underscore representation.
	 * 
	 * @param camelCase
	 *            Camel case name.
	 * @return Underscore representation of the camel case string.
	 */
	public static String convertToUnderScore(String camelCase) {
		String result = "";
		for (int i = 0; i < camelCase.length(); i++) {
			char currentChar = camelCase.charAt(i);
			// This is starting at 1 so the result does
			// not end up with an
			// underscore at the begin of the value
			if (i > 0 && Character.isUpperCase(currentChar)) {
				result = result.concat("_");
			}
			result = result.concat(Character.toString(currentChar).toLowerCase());
		}
		return result;
	}

	/**
	 * 문자열을 지정한 분리자에 의해 배열로 리턴하는 메서드.
	 * 
	 * @param source
	 *            원본 문자열
	 * @param separator
	 *            분리자
	 * @return result 분리자로 나뉘어진 문자열 배열
	 */
	public static String[] split(String source, String separator) throws NullPointerException {
		String[] returnVal = null;
		int cnt = 1;

		int index = source.indexOf(separator);
		int index0 = 0;
		while (index >= 0) {
			cnt++;
			index = source.indexOf(separator, index + 1);
		}
		returnVal = new String[cnt];
		cnt = 0;
		index = source.indexOf(separator);
		while (index >= 0) {
			returnVal[cnt] = source.substring(index0, index);
			index0 = index + 1;
			index = source.indexOf(separator, index + 1);
			cnt++;
		}
		returnVal[cnt] = source.substring(index0);

		return returnVal;
	}

	/**
	 * 문자열을 지정한 분리자에 의해 지정된 길이의 배열로 리턴하는 메서드.
	 * 
	 * @param source
	 *            원본 문자열
	 * @param separator
	 *            분리자
	 * @param arraylength
	 *            배열 길이
	 * @return 분리자로 나뉘어진 문자열 배열
	 */
	public static String[] split(String source, String separator, int arraylength) throws NullPointerException {
		String[] returnVal = new String[arraylength];
		int cnt = 0;
		int index0 = 0;
		int index = source.indexOf(separator);
		while (index >= 0 && cnt < (arraylength - 1)) {
			returnVal[cnt] = source.substring(index0, index);
			index0 = index + 1;
			index = source.indexOf(separator, index + 1);
			cnt++;
		}
		returnVal[cnt] = source.substring(index0);
		if (cnt < (arraylength - 1)) {
			for (int i = cnt + 1; i < arraylength; i++) {
				returnVal[i] = "";
			}
		}

		return returnVal;
	}
	
	/**
	 * 객체가 null인지 확인하고 null인 경우 "" 로 바꾸는 메서드
	 * 
	 * @param object
	 *            원본 객체
	 * @return resultVal 문자열
	 */
	public static String isNullToString(Object object) {
		String string = "";

		if (object != null) {
			string = object.toString().trim();
		}

		return string;
	}
	
	/**
	 * <p>
	 * 문자열 내부의 마이너스 character(-)를 모두 제거한다.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.removeMinusChar(null)       = null
	 * StringUtils.removeMinusChar("")         = ""
	 * StringUtils.removeMinusChar("a-sdfg-qweqe") = "asdfgqweqe"
	 * </pre>
	 * 
	 * @param str
	 *            입력받는 기준 문자열
	 * @return " - "가 제거된 입력문자열 입력문자열이 null인 경우 출력문자열은 null
	 */
	public static String removeMinusChar(String str) {
		return remove(str, '-');
	}
	
	/**
	 * <p>
	 * 기준 문자열에 포함된 모든 대상 문자(char)를 제거한다.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.remove(null, *)       = null
	 * StringUtils.remove("", *)         = ""
	 * StringUtils.remove("queued", 'u') = "qeed"
	 * StringUtils.remove("queued", 'z') = "queued"
	 * </pre>
	 * 
	 * @param str
	 *            입력받는 기준 문자열
	 * @param remove
	 *            입력받는 문자열에서 제거할 대상 문자열
	 * @return 제거대상 문자열이 제거된 입력문자열. 입력문자열이 null인 경우 출력문자열은 null
	 */
	public static String remove(String str, char remove) {
		if (isEmpty(str) || str.indexOf(remove) == -1) {
			return str;
		}
		char[] chars = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] != remove) {
				chars[pos++] = chars[i];
			}
		}
		return new String(chars, 0, pos);
	}

    /**
     * 가장 마지막에 일치하는 문구를 원하는 문구로 대체한다.
     * 
     * <pre>
     * StringUtils.replaceLast("ABC%DEF%GHI", "%", "-"): "ABC%DEF-GHI"
     * StringUtils.replaceLast("ABC%DEF%GHI", "%", "\\$"): "ABC%DEF$GHI"
     * </pre>
     */
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    /**
     * 공백 문자열인지 확인한다.
     * 
     * <pre>
     * StringUtils.isBlank(null): true
     * StringUtils.isBlank(""): true
     * StringUtils.isBlank(" "): true
     * StringUtils.isBlank(" ABC"): false
     * </pre>
     */
    public static boolean isBlank(String str) {
        if (str != null) str = str.trim();

        return str == null || "".equals(str);
    }

    /**
     * 공백 문자열이 하나라도 있는지 확인한다.
     * 
     * <pre>
     * StringUtils.anyBlanks(null, " "): true
     * StringUtils.anyBlanks(null, "ABC"): true
     * StringUtils.anyBlanks("ABC", ""): true
     * StringUtils.anyBlanks(" ", "ABC"): true
     * StringUtils.anyBlanks(" ABC", "ABC"): false
     * </pre>
     */
    public static boolean anyBlanks(String... strs) {
        // `new String[] {}`이 파라미터로 넘어 왔을 때
        if (strs == null || strs.length == 0) return true;

        for (String str : strs) {
            if (isBlank(str)) return true;
        }

        return false;
    }

    /**
     * 모두 공백 문자열인지 확인한다.
     * 
     * <pre>
     * StringUtils.allBlanks(null, " "): true
     * StringUtils.allBlanks(null, "ABC"): false
     * StringUtils.allBlanks("ABC", ""): false
     * StringUtils.allBlanks(" ", "ABC"): false
     * StringUtils.allBlanks(" ABC", "ABC"): false
     * </pre>
     */
    public static boolean allBlanks(String... strs) {
        // `new String[] {}`이 파라미터로 넘어 왔을 때
        if (strs == null || strs.length == 0) return true;

        return Stream.of(strs).allMatch(StringUtils::isBlank);
    }

    /**
     * 공백 문자열이 아닌지 확인한다.
     * 
     * <pre>
     * StringUtils.isNotBlank(null): false
     * StringUtils.isNotBlank(""): false
     * StringUtils.isNotBlank(" "): false
     * StringUtils.isNotBlank(" ABC"): true
     * </pre>
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 공백 문자열이 하나도 없는지 확인한다.
     * 
     * <pre>
     * StringUtils.noBlanks(null, " "): false
     * StringUtils.noBlanks(null, "ABC"): false
     * StringUtils.noBlanks("ABC", ""): false
     * StringUtils.noBlanks(" ", "ABC"): false
     * StringUtils.noBlanks(" ABC", "ABC"): true
     * </pre>
     */
    public static boolean noBlanks(String... strs) {
        return !anyBlanks(strs);
    }

    /**
     * 공백이 아닌 문자열이 하나라도 있는지 확인한다.
     * 
     * <pre>
     * StringUtils.notAllBlanks(null, " "): false
     * StringUtils.notAllBlanks(null, "ABC"): true
     * StringUtils.notAllBlanks("ABC", ""): true
     * StringUtils.notAllBlanks(" ", "ABC"): true
     * StringUtils.notAllBlanks(" ABC", "ABC"): true
     * </pre>
     */
    public static boolean notAllBlanks(String... strs) {
        return !allBlanks(strs);
    }
    
    /**
     * 두 문자열에서 시작을 기준으로 중복되는 부분을 반환한다.
     * 
     * <pre>
     * StringUtils.getDuplicity("You are so smart.", "You are so beautiful."): "You are so "
     * </pre>
     */
    public static String getDuplicity(String s1, String s2) {
        // ArrayIndexOutOfBoundsException를 방지하기 위해, 두 문자열 중 길이가 작은 걸 기준으로 한다
        final int minLength = Math.min(s1.length(), s2.length());

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < minLength; i++) {
            char c1 = s1.charAt(i);
            char c2 = s2.charAt(i);

            if (c1 != c2) break;

            sb.append(c1);
        }
        
        return sb.toString();
    }
    
    /**
     * 시작을 기준으로 `자식 문자열`에서 `부모 문자열`과 중복된 부분을 제외하고
     * `부모 문자열` 뒤에 `자식 문자열`을 붙여 반환한다. 
     * 
     * <pre>
     * StringUtils.appendWithoutDuplicity("http://www.naver.com", "http://www.naver.com/search"): "http://www.naver.com/search"
     * </pre>
     */
    public static String appendWithoutDuplicity(String parent, String child) {
        String duplicity = getDuplicity(parent, child);
        return parent + child.replaceFirst(duplicity, "");
    }

    /**
     * `기준 문자열`과 일치하는 문자열이 하나라도 있는지 확인한다.
     * 
     * <pre>
     * StringUtils.anyEquals(null, null): false
     * StringUtils.anyEquals("", null): false
     * StringUtils.anyEquals(null, ""): false
     * StringUtils.anyEquals("", null, ""): true
     * StringUtils.anyEquals("ABC", "abc"): false
     * StringUtils.anyEquals("ABC", "abc", "ABC"): true
     * </pre>
     */
    public static boolean anyEquals(String s1, String... strs) {
        // `new String[] {}`이 파라미터로 넘어 왔을 때
        if (s1 == null || strs == null || strs.length == 0) return false;

        for (String str : strs) {
            if (s1.equals(str)) return true;
        }

        return false;
    }

    /**
     * 3자리 숫자마다 ,(comma)로 구분한 문자열을 반환한다.
     * 
     * <pre>
     * StringUtils.formatComma(""): "0"
     * StringUtils.formatComma("-100"): "-100"
     * StringUtils.formatComma("100000"): "100,000"
     * </pre>
     */
    public static String formatComma(String amount) {
        String result = "0";
        DecimalFormat formatter = new DecimalFormat("###,###,###,###,###,###,###");
        result = formatter.format(amount);

        return result;
    }

    /**
     * 3자리 숫자마다 ,(comma)로 구분한 문자열을 반환한다.
     * 
     * <pre>
     * StringUtils.formatComma(0): "0"
     * StringUtils.formatComma(-100): "-100"
     * StringUtils.formatComma(100000): "100,000"
     * </pre>
     */
    public static String formatComma(int amount) {
        String result = "0";
        DecimalFormat formatter = new DecimalFormat("###,###,###,###,###,###,###");
        result = formatter.format(amount);

        return result;
    }

    /**
     * `해당 문자열`을 원하는 만큼 반복하여 복제한다.
     * 
     * <pre>
     * StringUtils.repeat(null, 2): "nullnull"
     * StringUtils.repeat("", 5): ""
     * StringUtils.repeat("abc", 3): "abcabcabc"
     * </pre>
     */
    public static String repeat(String str, int cnt) {
        return String.join("", Collections.nCopies(cnt, str));
    }

    public static Boolean string2boolean(String str) {
        if (anyEquals(str.toLowerCase(), "true", "y", "yes")) return true;
        else if (anyEquals(str.toLowerCase(), "false", "n", "no")) return false;
        else return null;
    }

    public static String match(String regex, String src) {
        return match(regex, src, 0);
    }

    public static String match(String regex, String src, int groupNo) {
        Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(src);

        String matched = null;
        while (matcher.find()) {
            matched = matcher.group(groupNo);
        }

        return matched;
    }

}
