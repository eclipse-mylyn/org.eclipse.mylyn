/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

/**
 * Attributes for images.
 * 
 * @author David Green
 */
public class ImageAttributes extends Attributes {

	public enum Align {
		Left, Right, Top, Texttop, Middle, Absmiddle, Baseline, Bottom, Absbottom, Center
	}

	private int width = -1;

	private int height = -1;

	private int border = 0;

	private Align align = null;

	private String alt;

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}
}
