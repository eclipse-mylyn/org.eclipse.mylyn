/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor;

import org.eclipse.jface.text.source.projection.ProjectionAnnotation;

/**
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
