package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eugene Kuleshov (https://bugs.eclipse.org/bugs/show_bug.cgi?taskId=129511)
 */
public class TaskKeyComparator implements Comparator<String> {

	public static final Pattern PATTERN = Pattern.compile("(?:([A-Za-z]*[:_\\-]?)(\\d+))?(.*)");

	public int compare(String o1, String o2) {
		String[] a1 = split(o1);
		String[] a2 = split(o2);

		String s1 = a1[0];
		String s2 = a2[0];
		if (s1 == null && s2 != null)
			return -1;
		if (s1 != null && s2 == null)
			return 1;

		if (s1 != null && s2 != null) {
			int n = s1.compareToIgnoreCase(s2);
			if (n != 0)
				return n;

			s1 = a1[1];
			s2 = a2[1];
			if (s1.length() == s2.length() || s1.length() == 0 || s2.length() == 0) {
				n = s1.compareTo(s2);
			} else {
				n = Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
			}
			if (n != 0)
				return n;
		}

		return a1[2].compareToIgnoreCase(a2[2]);
	}

	public String[] split(String s) {
		Matcher matcher = PATTERN.matcher(s);
 
		if (!matcher.find()) {
			return new String[] { null, null, s };
		}

		int n = matcher.groupCount();
		String[] res = new String[n];
		for (int i = 1; i < n + 1; i++) {
			res[i - 1] = matcher.group(i);
		}
		return res;
	}
}