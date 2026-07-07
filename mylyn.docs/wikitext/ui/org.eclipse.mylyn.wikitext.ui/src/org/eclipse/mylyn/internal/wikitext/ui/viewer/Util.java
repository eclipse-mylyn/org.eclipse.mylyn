/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.Iterator;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;

class Util {

	static boolean annotationsIncludeOffset(IAnnotationModel annotationModel, int offset) {
		if (annotationModel != null) {
			Iterator<Annotation> annotationIterator = ((IAnnotationModelExtension2) annotationModel)
					.getAnnotationIterator(offset, 1, true, true);
			return annotationIterator.hasNext();
		}
		return false;
	}

}
