/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Method;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;

/**
 * @author Steffen Pingel
 */
public class CompareUtil {

	public static byte[] getContent(IFileVersion version) {
		String content = version.getContent();
		return (content != null) ? content.getBytes() : new byte[0];
	}

	static SourceViewer getSourceViewer(MergeSourceViewer sourceViewer) {
		if (SourceViewer.class.isInstance(sourceViewer)) {
			return SourceViewer.class.cast(sourceViewer);
		} else {
			Object returnValue;
			try {
				Method getSourceViewerRefl = MergeSourceViewer.class.getDeclaredMethod("getSourceViewer");
				getSourceViewerRefl.setAccessible(true);
				returnValue = getSourceViewerRefl.invoke(sourceViewer);
				if (returnValue instanceof SourceViewer) {
					return (SourceViewer) returnValue;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

}
