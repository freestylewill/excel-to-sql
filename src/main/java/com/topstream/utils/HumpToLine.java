package com.topstream.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HumpToLine {

	private static Pattern linePattern = Pattern.compile("_(\\w)");
	private static Pattern humpPattern = Pattern.compile("[A-Z]");

	/**
	 * 驼峰转下划线,最后转为大写
	 * 
	 * @param str
	 * @return
	 */
	public static String humpToLine(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString().toUpperCase();
	}

	/**
	 * 下划线转驼峰,正常输出
	 * 
	 * @param str
	 * @return
	 */
	public static String lineToHump(String str) {
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static void main(String[] args) {
		String aaa = "app_version_fld";
		System.out.println(lineToHump(aaa));
		aaa = "appVersionFld";
		System.out.println(humpToLine(aaa));
	}

}
