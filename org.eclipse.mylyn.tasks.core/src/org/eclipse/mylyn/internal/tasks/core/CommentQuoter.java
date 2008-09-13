/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * Utility class to handle many text quoting scenarios
 * <p>
 * Each line of text is quoted individually and wrapped, according to the {@link lineSize} attribute
 * <p>
 * The wrapping policy is the following:
 * <p>
 * <ol>
 * <li>A substring of {@link lineSize} characters is extracted and examined
 * <li>If the next character after the substring is a blank space, the substring is quoted
 * <li>If don't, the substring is searched backwards for a blank space; if one is found, the substring until the blank
 * space is quoted
 * <li>If no blank space is found, the entire substring is quoted
 * <li>The remaining of substring + line are reevaluated on step 1
 * </ol>
 * 
 * @author Willian Mitsuda
 */
public class CommentQuoter {

	public static final int DEFAULT_WRAP_SIZE = 80;

	private final int lineSize;

	public CommentQuoter() {
		this(DEFAULT_WRAP_SIZE);
	}

	public CommentQuoter(int lineSize) {
		this.lineSize = lineSize;
	}

	/**
	 * Quote a text, wrapping if necessary
	 */
	public String quote(String text) {
		StringBuilder sb = new StringBuilder(text.length() + text.length() / lineSize);

		String[] lines = text.split("\n");
		for (String line : lines) {
			if (line.trim().equals("")) {
				sb.append("> \n");
				continue;
			}

			int pos = 0;
			while (pos < line.length()) {
				int wrapPos = pos + lineSize;
				if (wrapPos < line.length()) {
					// Try to find a space to wrap the line
					while (wrapPos > pos) {
						char wrapChar = line.charAt(wrapPos);
						if (Character.isSpaceChar(wrapChar)) {
							break;
						}
						wrapPos--;
					}
					if (wrapPos == pos) {
						// There is no space; don't break the line and find the
						// next space after the limit...
						wrapPos = pos + lineSize;
						while (wrapPos < line.length()) {
							if (Character.isSpaceChar(line.charAt(wrapPos))) {
								break;
							}
							wrapPos++;
						}
					}

					// Extract the substring and recalculate the next search
					// start point
					String wrappedLine = line.substring(pos, wrapPos).trim();
					sb.append("> " + wrappedLine + "\n");
					pos = wrapPos + 1;
				} else {
					sb.append("> " + line.substring(pos).trim() + "\n");
					break;
				}
			}
		}

		return sb.toString();
	}

}
