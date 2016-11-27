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
package org.eclipse.mylyn.wikitext.ui.annotation;

import org.eclipse.jface.text.source.Annotation;

/**
 * An annotation that marks the location of an element that has an @id.
 * 
 * @author David Green
 * @since 1.0
 */
public class IdAnnotation extends Annotation {

	public static final String TYPE = "org.eclipse.mylyn.wikitext.ui.annotation.id"; //$NON-NLS-1$

	public IdAnnotation(String id) {
		super(TYPE, false, id);
	}

	public String getElementId() {
		return getText();
	}
}
