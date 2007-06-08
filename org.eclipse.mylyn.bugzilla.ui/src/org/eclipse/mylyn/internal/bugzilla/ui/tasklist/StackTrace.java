/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold all of the information about a stack trace
 * 
 * @author Shawn Minto
 */
public class StackTrace {

	/** The length of the stack trace in the original string */
	private int length;

	/** The offset of the stack trace in the orignal string */
	private int offset;

	/** The string of the stack trace */
	private String stackTrace;

	/**
	 * This is the comment that the stack trace appeared in. String if
	 * desciption else Comment
	 */
	private Object comment;

	/**
	 * Constructor
	 * 
	 * @param stackTrace
	 *            The stack trace string
	 * @param offset
	 *            The offset of the stack trace in the original string
	 * @param length
	 *            The length of the stack trace in the original string
	 * @param comment
	 *            The comment that the stack trace came from
	 */
	public StackTrace(String stackTrace, int offset, int length, Object comment) {
		this.stackTrace = stackTrace;
		this.offset = offset;
		this.length = length;
		this.comment = comment;
	}

	/**
	 * Get the offset for the stack trace
	 * 
	 * @return Returns the offset.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Get the stack trace for the bug
	 * 
	 * @return Returns the stackTrace.
	 */
	public String getStackTrace() {
		return stackTrace;
	}

	/**
	 * Get the length of the bug
	 * 
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Get the Comment that this stack trace came from
	 * 
	 * @return Returns the Comment if it was a comment else a String if it was
	 *         the summary
	 */
	public Object getComment() {
		return comment;
	}

	/**
	 * Find a standard java stack trace in the given string
	 * 
	 * 
	 * @param s
	 *            The string to search for stack traces
	 * @param comment
	 *            The comment that the text came from.<br>
	 *            Comment if a comment else a String
	 * @return String[] of stack traces - each element is 1 trace
	 */
	public static StackTrace[] getStackTrace(String s, Object comment) {

		// setup the regex used to determine if it looks like we are at a
		// stack trace and whether it is something that should be skipped
		String regexExceptionType = "^(.*\\.)+.+(Exception|Error|Throwable).*";
		String regexSkip = ".*\\.\\..*";

		// get all of the individual lines for the string
		String[] lines = s.split("\r\n|\n");

		// the character start of the current stack trace
		int charStackStart = 0;

		// the current character in the string - used for the start and the
		// offset
		int[] charPos = { 0 }; // array so pass by reference

		boolean inStackTrace = false;
		List<String> stackTrace = null;
		List<StackTrace> stackTraces = new ArrayList<StackTrace>();

		// go through each of the lines of the string
		for (int i = 0; i < lines.length; i++) {

			if (lines[i].matches(regexSkip)) {

				// update the current character position
				charPos[0] += lines[i].length() + 2;

			} else if (lines[i].trim().matches(regexExceptionType) && !inStackTrace) {

				// we have matched the stack trace and we are not already in one

				// add the old stack trace to the list of stack traces
				if (stackTrace != null && stackTrace.size() > 1) {
					stackTraces.add(getStackTrace(stackTrace, charStackStart, charPos[0] - charStackStart, comment));
				}

				// prepare for a new stack trace
				stackTrace = new ArrayList<String>();
				inStackTrace = true;

				// the current line is the start of our stack trace
				stackTrace.add(lines[i]);
				charStackStart = charPos[0];
				charPos[0] += lines[i].length() + 2;
			} else if (inStackTrace) {
				// we are in a stack trace

				int[] pos = { i }; // array so pass by reference

				// get the next at clause of the stack trace
				String stack = getNextAt(lines, pos, charPos);

				// check if there was an at
				if (stack == null) {

					// there wasn't so we are done this stack trace
					inStackTrace = false;
					if (stackTrace != null && stackTrace.size() > 1) {
						stackTraces
								.add(getStackTrace(stackTrace, charStackStart, charPos[0] - charStackStart, comment));
					}
					stackTrace = null;
				} else {

					// we had one, so add it to this stack trace
					stackTrace.add(stack);
				}

				// update the position
				i = pos[0];
			} else {
				// update the current character position
				charPos[0] += lines[i].length() + 2;
			}
		}

		// make sure to add the stack trace if it was the last in the string
		if (stackTrace != null && stackTrace.size() > 1) {
			stackTraces.add(getStackTrace(stackTrace, charStackStart, charPos[0] - charStackStart, comment));
		}

		if (stackTraces.size() == 0)
			return null;

		// get the string values of the stack traces and return it
		return getTracesFromList(stackTraces);
	}

	/**
	 * Get the next at clause from a potential stack trace -- looks ahead 4
	 * lines
	 * 
	 * @param lines
	 *            The array of all of the lines in the bug
	 * @param i
	 *            The current position to start at
	 * @param charPos
	 *            The current character position in the original string
	 * @return The next at clause, or <code>null</code><br>
	 *         If an at line is matched, but the end isn't within the 4 lines,
	 *         only the first line is returned. Also, charPos is updated as well
	 *         as i
	 */
	private static String getNextAt(String[] lines, int[] i, int[] charPos) {
		String regexAtString = "^at.*";
		String regexEndString = ".*:\\d+\\)$";
		int index = i[0];
		String l1, l2, l3, l4;
		l1 = l2 = l3 = l4 = null;
		String res = null;

		// get the first line to look at
		if (lines.length > index) {
			l1 = lines[index];
		} else {
			// if the first line doesn't exist, we are done and should
			// return
			return null;
		}

		// get the next 3 lines
		if (lines.length > index + 1) {
			l2 = lines[index + 1];
		}
		if (lines.length > index + 2) {
			l3 = lines[index + 2];
		}
		if (lines.length > index + 3) {
			l4 = lines[index + 3];
		}

		// make sure that the first line is the start of an at
		// if not, return null
		if (l1.trim().matches(regexAtString)) {
			charPos[0] += l1.length() + 2;
			res = l1;
		} else
			return null;

		// now determine where the end is if it wasn't on 1 line
		if (!res.trim().matches(regexEndString)) {

			if (l2 != null && l2.trim().matches(regexEndString)) {

				// it was on the second line
				// update the current position and the result string
				i[0] = index + 1;
				charPos[0] += l2.length() + 2;
				res += l2.trim();
			} else if (l3 != null && l3.trim().matches(regexEndString)) {

				// it was on the third line
				// update the current position and the result string
				i[0] = index + 2;
				charPos[0] += l2.length() + l3.length() + 4;
				res += l2.trim();
				res += l3.trim();
			} else if (l4 != null && l4.trim().matches(regexEndString)) {

				// it was on the fourth line
				// update the current position and the result string
				i[0] = index + 3;
				charPos[0] += l2.length() + l3.length() + l4.length() + 6;
				res += l2.trim();
				res += l3.trim();
				res += l4.trim();
			}
		}

		// return the result
		return res;
	}

	/**
	 * Get the StackTrace
	 * 
	 * @param l
	 *            the list of lines that contain the trace
	 * @param start
	 *            the start of the stack trace
	 * @param offset
	 *            the offset of the stack trace
	 * @param comment
	 *            The comment that the stack trace came from
	 * @return The StackTrace for the given data
	 */
	private static StackTrace getStackTrace(List<String> l, int offset, int length, Object comment) {
		String s = "";
		for (String s2 : l) {
			s += s2 + "\r\n";
		}

		return new StackTrace(s, offset, length, comment);
	}

	/**
	 * Convert a List StackTraces to a StackTrace[] <br>
	 * 
	 * @param l
	 *            The List of StackTraces
	 * @return StackTrace[] of the List
	 */
	private static StackTrace[] getTracesFromList(List<StackTrace> l) {

		// make sure that there is something to convert, else return null
		if (l == null || l.size() == 0)
			return null;

		// convert the list of strings to an array of strings
		int i = 0;
		StackTrace[] s = new StackTrace[l.size()];

		for (StackTrace st : l) {
			s[i] = st;
			i++;
		}

		// return the string array
		return s;
	}

	/**
	 * Escape all of the special regex characters from the string
	 * 
	 * @param s
	 *            The string to escape the characters for
	 * @return A string with all of the special characters escaped <br>
	 *         <code>
	 * 				. => \.<br>
	 * 				$ => \$<br>
	 * 				? => \?<br>
	 * 				{ => \{<br>
	 * 				} => \}<br>
	 * 				( => \(<br>
	 * 				) => \)<br>
	 * 				[ => \[<br>
	 * 				] => \]<br>
	 * 				+ => \+<br>
	 * 				* => \*<br>
	 * 				| => \|<br>
	 * 				^ => \^<br>
	 * 				\ => \\<br>
	 * 				/ => \/<br>
	 * 			</code>
	 */
	public static String escapeForRegex(String s) {
		String sFixed = s;

		// replace all special regex characters
		sFixed = sFixed.replaceAll("\\\\", "\\\\\\\\");
		sFixed = sFixed.replaceAll("\\$", "\\\\\\$");
		sFixed = sFixed.replaceAll("\\.", "\\\\.");
		sFixed = sFixed.replaceAll("\\?", "\\\\?");
		sFixed = sFixed.replaceAll("\\{", "\\\\{");
		sFixed = sFixed.replaceAll("\\}", "\\\\}");
		sFixed = sFixed.replaceAll("\\(", "\\\\(");
		sFixed = sFixed.replaceAll("\\)", "\\\\)");
		sFixed = sFixed.replaceAll("\\[", "\\\\[");
		sFixed = sFixed.replaceAll("\\]", "\\\\]");
		sFixed = sFixed.replaceAll("\\+", "\\\\+");
		sFixed = sFixed.replaceAll("\\*", "\\\\*");
		sFixed = sFixed.replaceAll("\\|", "\\\\|");
		sFixed = sFixed.replaceAll("\\^", "\\\\^");
		sFixed = sFixed.replaceAll("\\/", "\\\\/");

		return sFixed;
	}
}
