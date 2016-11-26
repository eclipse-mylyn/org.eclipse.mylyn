/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * Attributes that may used when creating blocks of type {@link BlockType#TABLE}.
 * 
 * @author David Green
 * @since 1.0
 */
public class TableAttributes extends Attributes {

	private String border;

	private String summary;

	private String width;

	private String frame;

	private String rules;

	private String cellspacing;

	private String cellpadding;

	private String bgcolor;

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public String getCellspacing() {
		return cellspacing;
	}

	public void setCellspacing(String cellspacing) {
		this.cellspacing = cellspacing;
	}

	public String getCellpadding() {
		return cellpadding;
	}

	public void setCellpadding(String cellpadding) {
		this.cellpadding = cellpadding;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

}
