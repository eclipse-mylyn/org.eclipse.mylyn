/*******************************************************************************
 * Copyright (c) 2019 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;

public class DiffPreferencesInfo {

	private Whitespace ignore_whitespace;

	private int tab_size;

	private int line_Length;

	private boolean syntax_highlighting;

	private boolean show_whitespace_errors;

	private boolean intraline_difference;

	private boolean show_tabs;

	private boolean expand_all_comments;

	private short context;

	private String theme;

	/**
	 * @return the theme
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme
	 *            the theme to set
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * @return the cursor_blink_rate
	 */
	public int getCursor_blink_rate() {
		return cursor_blink_rate;
	}

	/**
	 * @param cursor_blink_rate
	 *            the cursor_blink_rate to set
	 */
	public void setCursor_blink_rate(int cursor_blink_rate) {
		this.cursor_blink_rate = cursor_blink_rate;
	}

	private int cursor_blink_rate;

	/**
	 * @return the ignoreWhitespace
	 */
	public Whitespace getIgnoreWhitespace() {
		return ignore_whitespace;
	}

	/**
	 * @param ignoreWhitespace
	 *            the ignoreWhitespace to set
	 */
	public void setIgnoreWhitespace(Whitespace ignoreWhitespace) {
		ignore_whitespace = ignoreWhitespace;
	}

	/**
	 * @return the tabSize
	 */
	public int getTabSize() {
		return tab_size;
	}

	/**
	 * @param tabSize
	 *            the tabSize to set
	 */
	public void setTabSize(int tabSize) {
		tab_size = tabSize;
	}

	/**
	 * @return the lineLength
	 */
	public int getLineLength() {
		return line_Length;
	}

	/**
	 * @param lineLength
	 *            the lineLength to set
	 */
	public void setLineLength(int lineLength) {
		line_Length = lineLength;
	}

	/**
	 * @return the syntaxHighlighting
	 */
	public boolean isSyntaxHighlighting() {
		return syntax_highlighting;
	}

	/**
	 * @param syntaxHighlighting
	 *            the syntaxHighlighting to set
	 */
	public void setSyntaxHighlighting(boolean syntaxHighlighting) {
		syntax_highlighting = syntaxHighlighting;
	}

	/**
	 * @return the showWhitespaceErrors
	 */
	public boolean isShowWhitespaceErrors() {
		return show_whitespace_errors;
	}

	/**
	 * @param showWhitespaceErrors
	 *            the showWhitespaceErrors to set
	 */
	public void setShowWhitespaceErrors(boolean showWhitespaceErrors) {
		show_whitespace_errors = showWhitespaceErrors;
	}

	/**
	 * @return the intralineDifference
	 */
	public boolean isIntralineDifference() {
		return intraline_difference;
	}

	/**
	 * @param intralineDifference
	 *            the intralineDifference to set
	 */
	public void setIntralineDifference(boolean intralineDifference) {
		intraline_difference = intralineDifference;
	}

	/**
	 * @return the showTabs
	 */
	public boolean isShowTabs() {
		return show_tabs;
	}

	/**
	 * @param showTabs
	 *            the showTabs to set
	 */
	public void setShowTabs(boolean showTabs) {
		show_tabs = showTabs;
	}

	/**
	 * @return the expand_all_comments
	 */
	public boolean isExpand_all_comments() {
		return expand_all_comments;
	}

	/**
	 * @param expand_all_comments
	 *            the expand_all_comments to set
	 */
	public void setExpand_all_comments(boolean expand_all_comments) {
		this.expand_all_comments = expand_all_comments;
	}

	/**
	 * @return the context
	 */
	public short getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(short context) {
		this.context = context;
	}

}
