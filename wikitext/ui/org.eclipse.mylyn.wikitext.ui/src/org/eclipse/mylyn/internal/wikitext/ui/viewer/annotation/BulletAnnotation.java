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
package org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation;

import org.eclipse.jface.text.source.Annotation;

public class BulletAnnotation extends Annotation {

	public static final String TYPE = "org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.bullet"; //$NON-NLS-1$

	private final int indentLevel;

	public BulletAnnotation(int indentLevel) {
		super(TYPE, false, Integer.toString(indentLevel));
		this.indentLevel = indentLevel;
	}

	public int getIndentLevel() {
		return indentLevel;
	}

}
