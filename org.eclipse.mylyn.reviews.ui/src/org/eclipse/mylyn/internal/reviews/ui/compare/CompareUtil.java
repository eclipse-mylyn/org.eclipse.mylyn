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
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.ContentMergeViewer;
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;

/**
 * @author Steffen Pingel
 */
public class CompareUtil {

	public static DiffNode createDiffNode(IFileItem file) {
		byte[] targetContent = getContent(file.getTarget());
		byte[] baseContent = getContent(file.getBase());
		String targetPath = file.getTarget().getPath();
		if (targetPath == null) {
			targetPath = file.getBase().getPath();
		}
		String basePath = file.getBase().getPath();
		if (basePath == null) {
			basePath = targetPath;
		}
		return new DiffNode(new ByteArrayInput(targetContent, targetPath), new ByteArrayInput(baseContent, basePath));
	}

	static byte[] getContent(IFileRevision revision) {
		String content = revision.getContent();
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

	static void setActiveViewer(TextMergeViewer mergeViewer, MergeSourceViewer focusViewer) {
		try {
			Method setActiveViewer = TextMergeViewer.class.getDeclaredMethod("setActiveViewer",
					MergeSourceViewer.class, boolean.class);
			setActiveViewer.setAccessible(true);
			setActiveViewer.invoke(mergeViewer, focusViewer, true);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID, "Failed to activate viewer", e));
		}
	}

	static void configureSourceViewers(Viewer contentViewer, TextMergeViewer textMergeViewer,
			final MergeSourceViewer fLeft, final MergeSourceViewer fRight) {
		// hack for Eclipse 3.5
		try {
			Method getCompareConfiguration = ContentMergeViewer.class.getDeclaredMethod("getCompareConfiguration");
			getCompareConfiguration.setAccessible(true);
			CompareConfiguration cc = (CompareConfiguration) getCompareConfiguration.invoke(textMergeViewer);

			Method getMergeContentProvider = ContentMergeViewer.class.getDeclaredMethod("getMergeContentProvider");
			getMergeContentProvider.setAccessible(true);
			IMergeViewerContentProvider cp = (IMergeViewerContentProvider) getMergeContentProvider.invoke(textMergeViewer);

			Method getSourceViewer = MergeSourceViewer.class.getDeclaredMethod("getSourceViewer");

			Method configureSourceViewer = TextMergeViewer.class.getDeclaredMethod("configureSourceViewer",
					SourceViewer.class, boolean.class);
			configureSourceViewer.setAccessible(true);
			configureSourceViewer.invoke(contentViewer, getSourceViewer.invoke(fLeft),
					cc.isLeftEditable() && cp.isLeftEditable(textMergeViewer.getInput()));
			configureSourceViewer.invoke(contentViewer, getSourceViewer.invoke(fRight),
					cc.isRightEditable() && cp.isRightEditable(textMergeViewer.getInput()));

			Field isConfiguredField = TextMergeViewer.class.getDeclaredField("isConfigured");
			isConfiguredField.setAccessible(true);
			isConfiguredField.set(contentViewer, true);
		} catch (Throwable t) {
			t.printStackTrace();
			// ignore as it may not exist in other versions
		}
	}

}
