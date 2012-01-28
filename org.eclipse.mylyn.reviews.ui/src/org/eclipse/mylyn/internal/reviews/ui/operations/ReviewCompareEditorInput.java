/*******************************************************************************
 * Copyright (c) 2009 Atlassian and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.operations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.ContentMergeViewer;
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewCompareAnnotationModel;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class ReviewCompareEditorInput extends CompareEditorInput {

	static class ByteArrayInput implements ITypedElement, IStreamContentAccessor {

		byte[] content;

		private final String name;

		public ByteArrayInput(byte[] content, String name) {
			this.content = content;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Image getImage() {
			return null;
		}

		public String getType() {
			String extension = FilenameUtils.getExtension(name);
			return extension != null && extension.length() > 0 ? extension : ITypedElement.TEXT_TYPE;
		}

		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(content);
		}

	}

	private final ReviewCompareAnnotationModel annotationModel;

	private final IFileItem file;

	public ReviewCompareEditorInput(IFileItem file, ReviewCompareAnnotationModel annotationModel,
			CompareConfiguration compareConfiguration) {
		super(compareConfiguration);
		this.annotationModel = annotationModel;
		this.file = file;

		compareConfiguration.setLeftLabel(NLS.bind("{0}: {1}", file.getTarget().getRevision(), file.getName()));
		compareConfiguration.setRightLabel(NLS.bind("{0}: {1}", file.getBase().getRevision(), file.getName()));
		setTitle(NLS.bind("Compare {0} {1} and {2}", new Object[] { file.getName(), file.getTarget().getRevision(),
				file.getBase().getRevision() }));
	}

	private byte[] getContent(IFileRevision revision) {
		String content = revision.getContent();
		return (content != null) ? content.getBytes() : new byte[0];
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
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
		Differencer differencer = new Differencer();
		Object diff = differencer.findDifferences(false, monitor, null, null, new ByteArrayInput(targetContent,
				targetPath), new ByteArrayInput(baseContent, basePath));
		return diff;
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer contentViewer = super.findContentViewer(oldViewer, input, parent);
		return ReviewCompareEditorInput.findContentViewer(contentViewer, input, parent, annotationModel);
	}

	private static Viewer findContentViewer(Viewer contentViewer, ICompareInput input, Composite parent,
			ReviewCompareAnnotationModel annotationModel) {

		// FIXME: hack
		if (contentViewer instanceof TextMergeViewer) {
			TextMergeViewer textMergeViewer = (TextMergeViewer) contentViewer;
			try {
				Class<TextMergeViewer> clazz = TextMergeViewer.class;
				Field declaredField = clazz.getDeclaredField("fLeft");
				declaredField.setAccessible(true);
				final MergeSourceViewer fLeft = (MergeSourceViewer) declaredField.get(textMergeViewer);

				declaredField = clazz.getDeclaredField("fRight");
				declaredField.setAccessible(true);
				final MergeSourceViewer fRight = (MergeSourceViewer) declaredField.get(textMergeViewer);

				annotationModel.attachToViewer(textMergeViewer, fLeft, fRight);
				annotationModel.focusOnComment();
				annotationModel.registerContextMenu();

				Method setActiveViewer = clazz.getDeclaredMethod("setActiveViewer", MergeSourceViewer.class,
						boolean.class);
				setActiveViewer.setAccessible(true);
				setActiveViewer.invoke(textMergeViewer, fRight, true);

				hackGalileo(contentViewer, textMergeViewer, fLeft, fRight);
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID,
						"Could not initialize annotation model for " + input.getName(), t));
			}
		}
		return contentViewer;
	}

	private static void hackGalileo(Viewer contentViewer, TextMergeViewer textMergeViewer,
			final MergeSourceViewer fLeft, final MergeSourceViewer fRight) {
		// FIXME: hack for e3.5
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
			// ignore as it may not exist in other versions
		}
	}

	public ReviewCompareAnnotationModel getAnnotationModelToAttach() {
		return annotationModel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationModel == null) ? 0 : annotationModel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ReviewCompareEditorInput other = (ReviewCompareEditorInput) obj;
		if (annotationModel == null) {
			if (other.annotationModel != null) {
				return false;
			}
		} else if (!annotationModel.equals(other.annotationModel)) {
			return false;
		}
		return true;
	}

	public IFileItem getFile() {
		return file;
	}

	@Override
	protected void contentsCreated() {
		super.contentsCreated();
		getAnnotationModelToAttach().focusOnComment();
	}

}