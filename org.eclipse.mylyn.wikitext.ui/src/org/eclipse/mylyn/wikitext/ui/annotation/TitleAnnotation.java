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
package org.eclipse.mylyn.wikitext.ui.annotation;

import org.eclipse.jface.text.source.Annotation;

/**
 * An annotation for "title" or tooltip text.
 * 
 * @author David Green
 * 
 */
public class TitleAnnotation extends Annotation {
	private final String titleType;

	public static final String TYPE = "org.eclipse.mylyn.wikitext.ui.annotation.title"; //$NON-NLS-1$

	public TitleAnnotation(String title, String titleType) {
		super(TYPE, false, title);
		this.titleType = titleType;
	}

	/**
	 * The type of title. Typically corresponds to the HTML element name for which there is a title.
	 */
	public String getTitleType() {
		return titleType;
	}

	/**
	 * The title text.
	 */
	public String getTitle() {
		return getText();
	}
}
