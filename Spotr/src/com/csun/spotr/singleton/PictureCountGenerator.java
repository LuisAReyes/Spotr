package com.csun.spotr.singleton;

public class PictureCountGenerator {
	private static int count = 0;
	public static String getValue() {
		return "pic" + count++;
	}
}
