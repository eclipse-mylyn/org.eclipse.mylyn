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

package org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation;

import org.eclipse.jface.text.source.Annotation;

/**
 * An annotation to represent the presence of a horizontal rule (as per HTML &lt;hr/>)
 * 
 * @author David Green
 */
public class HorizontalRuleAnnotation extends Annotation {

	public static final String TYPE = "org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.hr"; //$NON-NLS-1$

	public HorizontalRuleAnnotation() {
		super(TYPE, false, ""); //$NON-NLS-1$
	}

	public String getElementId() {
		return getText();
	}
}
