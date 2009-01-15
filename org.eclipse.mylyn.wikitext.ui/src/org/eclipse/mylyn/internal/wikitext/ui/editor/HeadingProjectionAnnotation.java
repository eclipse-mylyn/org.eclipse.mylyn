/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

/**
 * 
 * @author David Green
 */
class HeadingProjectionAnnotation extends ProjectionAnnotation {
	private final String headingId;

	public HeadingProjectionAnnotation(String headingId) {
		this.headingId = headingId;
	}

	public String getHeadingId() {
		return headingId;
	}
}