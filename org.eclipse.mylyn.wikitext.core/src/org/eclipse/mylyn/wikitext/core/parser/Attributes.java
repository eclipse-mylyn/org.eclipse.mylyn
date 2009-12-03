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

/**
 * Attributes for a markup element. Note that though there are many specialized subclasses of this class, they are
 * optional.
 * 
 * @see DocumentBuilder
 * @author David Green
 * @since 1.0
 */
public class Attributes {

	private String cssClass;

	private String id;

	private String cssStyle;

	private String language;

	private String title;

	public Attributes() {
	}

	public Attributes(String id, String cssClass, String cssStyle, String language) {
		this.id = id;
		this.cssClass = cssClass;
		this.cssStyle = cssStyle;
		this.language = language;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	/**
	 * Append a css class to the {@link #getCssClass() existing value}
	 * 
	 * @since 1.3
	 * @see #setCssClass(String)
	 */
	public void appendCssClass(String cssClass) {
		String priorCssClasses = getCssClass();
		if (priorCssClasses == null) {
			setCssClass(cssClass);
		} else {
			setCssClass(priorCssClasses + ' ' + cssClass);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCssStyle() {
		return cssStyle;
	}

	public void setCssStyle(String cssStyle) {
		this.cssStyle = cssStyle;
	}

	/**
	 * Append a css style to the {@link #getCssStyle() existing value}
	 * 
	 * @since 1.3
	 * @see #setCssStyle(String)
	 */
	public void appendCssStyle(String cssStyle) {
		String priorCssStyle = getCssStyle();
		if (priorCssStyle == null) {
			setCssStyle(cssStyle);
		} else {
			setCssStyle(priorCssStyle + ' ' + cssStyle);
		}
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}