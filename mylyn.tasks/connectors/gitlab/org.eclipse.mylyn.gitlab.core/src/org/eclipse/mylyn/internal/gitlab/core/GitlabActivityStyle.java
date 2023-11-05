/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.gitlab.core;

public class GitlabActivityStyle implements Cloneable {

	public static final int NORMAL = 0;

	public static final int BOLD = 1;

//    public static final int UNDERLINE_ERROR = 2;
//    public static final int UNDERLINE_SQUIGGLE = 3;
	public static final int UNDERLINE_LINK = 4;

	public static final int COLOR_RED = 3;

	public static final int COLOR_GREEN = 5;

	public static final int COLOR_INHERIT_DEFAULT = 1;

	private int start;

	private int length;

	private int fontStyle;

	private int color;

	private String url;

	public GitlabActivityStyle(int start) {
		super();
		this.start = start;
		color = COLOR_INHERIT_DEFAULT;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void add2Length(int addLength) {
		length += addLength;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public GitlabActivityStyle clone() throws CloneNotSupportedException {
		return (GitlabActivityStyle) super.clone();
	}

	@Override
	public String toString() {
		return "GitlabActivityStyle [start=" + start + ", length=" + length + ", fontStyle=" + fontStyle + ", color="
				+ color + ", url=" + url + "]";
	}

}
