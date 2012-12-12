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

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.LineRange;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.actions.AddLineCommentToFileAction;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentAnnotation;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentAnnotationHover;
import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentInformationControlCreator;
import org.eclipse.mylyn.internal.reviews.ui.annotations.IReviewCompareSourceViewer;
import org.eclipse.mylyn.internal.reviews.ui.annotations.ReviewAnnotationModel;
import org.eclipse.mylyn.internal.reviews.ui.editors.ruler.CommentAnnotationRulerColumn;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.texteditor.AnnotationColumn;
import org.eclipse.ui.internal.texteditor.PropertyEventDispatcher;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.AnnotationPreferenceLookup;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

/**
 * @author Thomas Ehrnhoefer
 * @author Steffen Pingel
 */
class ReviewCompareInputListener implements ITextInputListener, IReviewCompareSourceViewer {

	/**
	 * Ensures that the line background is fully colored in the compare editor.
	 */
	private final class ColoringLineBackgroundListener implements LineBackgroundListener {

		private Color colorCommented;

		private PropertyEventDispatcher fDispatcher;

		private final StyledText styledText;

		private ColoringLineBackgroundListener(StyledText styledText) {
			this.styledText = styledText;
			initialize();
		}

		public void lineGetBackground(LineBackgroundEvent event) {
			int lineNr = styledText.getLineAtOffset(event.lineOffset) + 1;
			Iterator<CommentAnnotation> it = annotationModel.getAnnotationIterator();
			while (it.hasNext()) {
				CommentAnnotation annotation = it.next();
				int startLine;
				int endLine;
				ITopic comment = annotation.getTopic();
				//TODO This code assumes that we have one comment per annotation. That won't work for r4E.
				if (comment.getLocations().size() == 1) {
					ILocation location = comment.getLocations().get(0);
					if (location instanceof ILineLocation) {
						ILineLocation lineLocation = (ILineLocation) location;
						startLine = lineLocation.getRangeMin();
						endLine = lineLocation.getRangeMax();
						if (lineNr >= startLine && lineNr <= endLine) {
							AnnotationPreference pref = new AnnotationPreferenceLookup().getAnnotationPreference(annotation);
							if (pref.getHighlightPreferenceValue()) {
								event.lineBackground = colorCommented;
							}
						}
					}
				}
			}
		}

		/**
		 * Galileo hack to deal with slaveDocuments (when clicking on java structure elements). The styledText will not
		 * contain the whole text anymore, so our line numbering is off
		 * 
		 * @param event
		 * @return
		 */
		@SuppressWarnings("unused")
		private int getDocumentOffset(LineBackgroundEvent event) {
			/*
			 * there is no access to DefaultDocumentAdapter and thus the (master or slave) document.. so we have to assume
			 * that on first call this event actually has the full text. this text, and the text of the current styled text
			 * will be used to calculate the offset
			 */
			if (event.widget instanceof StyledText) {
				String currentText = ((StyledText) event.widget).getText();
				if (initialText == null) {
					initialText = currentText;
					// since it is initial call, offset should be 0 anyway
					return 0;
				}
				// if text is unchanged, offset it 0
				if (currentText.equals(initialText)) {
					return 0;
				}
				// current text is different, check if it is contained in initialText
				if (initialText.contains(currentText)) {
					// calculate the offset
					int charoffset = initialText.indexOf(currentText);
					int lineOffset = 0;
					String delimiter = ((StyledText) event.widget).getLineDelimiter();
					for (String line : initialText.split(delimiter)) {
						if (charoffset > 0) {
							charoffset -= (line.length() + delimiter.length());
							lineOffset++;
						} else {
							break;
						}
					}
					return lineOffset;
				} else {
					// log error since we assume the initial text contains all slaveTexts.
					StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
							"Could not find text offset for annotation highlighting"
									+ " - current text not contained in initial text."));
				}
			}
			return 0;
		}

		private void initialize() {
			final IPreferenceStore store = EditorsUI.getPreferenceStore();
			if (store == null) {
				return;
			}

			AnnotationPreferenceLookup lookup = EditorsUI.getAnnotationPreferenceLookup();
			final AnnotationPreference commentedPref = lookup.getAnnotationPreference(CommentAnnotation.COMMENT_ANNOTATION_ID);

			updateCommentedColor(commentedPref, store);

			fDispatcher = new PropertyEventDispatcher(store);

			if (commentedPref != null) {
				fDispatcher.addPropertyChangeListener(commentedPref.getColorPreferenceKey(),
						new IPropertyChangeListener() {
							public void propertyChange(PropertyChangeEvent event) {
								updateCommentedColor(commentedPref, store);
							}
						});
			}
		}

		private void updateCommentedColor(AnnotationPreference pref, IPreferenceStore store) {
			if (pref != null) {
				RGB rgb = CommentAnnotationRulerColumn.getColorFromAnnotationPreference(store, pref);
				colorCommented = EditorsUI.getSharedTextColors().getColor(rgb);
			}
		}
	}

	private AddLineCommentToFileAction addLineCommentAction;

	private final ReviewAnnotationModel annotationModel;

	private String initialText;

	private final MergeSourceViewer mergeSourceViewer;

//		private AddGeneralCommentToFileAction addGeneralCommentAction;

	private final SourceViewer sourceViewer;

	ReviewCompareInputListener(MergeSourceViewer mergeSourceViewer, ReviewAnnotationModel annotationModel) {
		this.sourceViewer = CompareUtil.getSourceViewer(mergeSourceViewer);
		this.mergeSourceViewer = mergeSourceViewer;
		this.annotationModel = annotationModel;
	}

	public void focusOnLines(ILocation range) {
		if (range instanceof ILineLocation) {
			ILineLocation lineLocation = (ILineLocation) range;
			// editors count lines from 0, Crucible counts from 1
			final int startLine = lineLocation.getRangeMin() - 1;
			final int endLine = lineLocation.getRangeMax() - 1;
			if (sourceViewer != null) {
				IDocument document = sourceViewer.getDocument();
				if (document != null) {
					try {
						int offset = document.getLineOffset(startLine);
						int length = document.getLineOffset(endLine) - offset;
						StyledText widget = sourceViewer.getTextWidget();
						try {
							widget.setRedraw(false);
							//sourceViewer.revealRange(offset, length);
							//sourceViewer.setSelectedRange(offset, 0);
							sourceViewer.setSelection(new TextSelection(offset, length), true);
						} finally {
							widget.setRedraw(true);
						}
					} catch (BadLocationException e) {
						StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e.getMessage(), e));
					}
				}
			}
		}
	}

	public void forceCustomAnnotationHover() throws NoSuchFieldException, IllegalAccessException {
		Class<SourceViewer> sourceViewerClazz = SourceViewer.class;
		sourceViewer.setAnnotationHover(new CommentAnnotationHover(null));

		// hack for Eclipse 3.5
		try {
			Field hoverControlCreator = TextViewer.class.getDeclaredField("fHoverControlCreator");
			hoverControlCreator.setAccessible(true);
			hoverControlCreator.set(sourceViewer, new CommentInformationControlCreator());
		} catch (Throwable t) {
			// ignore as it may not exist in other versions
		}

		// hack for Eclipse 3.5
		try {
			Method ensureMethod = sourceViewerClazz.getDeclaredMethod("ensureAnnotationHoverManagerInstalled");
			ensureMethod.setAccessible(true);
			ensureMethod.invoke(sourceViewer);
		} catch (Throwable t) {
			// ignore as it may not exist in other versions
		}

		Field hoverManager = SourceViewer.class.getDeclaredField("fVerticalRulerHoveringController");
		hoverManager.setAccessible(true);
		AnnotationBarHoverManager manager = (AnnotationBarHoverManager) hoverManager.get(sourceViewer);
		if (manager != null) {
			Field annotationHover = AnnotationBarHoverManager.class.getDeclaredField("fAnnotationHover");
			annotationHover.setAccessible(true);
			IAnnotationHover hover = (IAnnotationHover) annotationHover.get(manager);
			annotationHover.set(manager, new CommentAnnotationHover(hover));
		}
		sourceViewer.showAnnotations(true);
		sourceViewer.showAnnotationsOverview(true);
	}

	public ReviewAnnotationModel getAnnotationModel() {
		return annotationModel;
	}

	public LineRange getSelection() {
		if (sourceViewer != null) {
			TextSelection selection = (TextSelection) sourceViewer.getSelection();
			return new LineRange(selection.getStartLine() + 1, selection.getEndLine() - selection.getStartLine());
		}
		return null;
	}

	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		// ignore
	}

	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		if (oldInput != null) {
			annotationModel.disconnect(oldInput);
		}
		if (newInput != null && sourceViewer != null) {
			IAnnotationModel originalAnnotationModel = sourceViewer.getAnnotationModel();
			if (originalAnnotationModel instanceof IAnnotationModelExtension) {
				IAnnotationModelExtension annotationModelExtension = (IAnnotationModelExtension) originalAnnotationModel;
				annotationModelExtension.addAnnotationModel(ReviewsUiPlugin.PLUGIN_ID, originalAnnotationModel);
			} else {
				try {
					Class<SourceViewer> sourceViewerClazz = SourceViewer.class;
					Field declaredField2 = sourceViewerClazz.getDeclaredField("fVisualAnnotationModel");
					declaredField2.setAccessible(true);
					Method declaredMethod = sourceViewerClazz.getDeclaredMethod("createVisualAnnotationModel",
							IAnnotationModel.class);
					declaredMethod.setAccessible(true);
					originalAnnotationModel = (IAnnotationModel) declaredMethod.invoke(sourceViewer, annotationModel);
					declaredField2.set(sourceViewer, originalAnnotationModel);
					originalAnnotationModel.connect(newInput);
					sourceViewer.showAnnotations(true);

					createVerticalRuler(newInput, sourceViewerClazz);
					createOverviewRuler(newInput, sourceViewerClazz);
					createHighlighting(sourceViewerClazz);
				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
							"Error attaching annotation model", t));
				}
			}
		}
	}

	public boolean isListenerFor(MergeSourceViewer viewer, ReviewAnnotationModel annotationModel) {
		return this.mergeSourceViewer == viewer && this.annotationModel == annotationModel;
	}

	public void registerContextMenu() {
		addLineCommentAction = new AddLineCommentToFileAction(this);
//				addLineCommentAction.setImageDescriptor(CrucibleImages.ADD_COMMENT);
//				addGeneralCommentAction = new AddGeneralCommentToFileAction(crucibleAnnotationModel.getCrucibleFile());

		if (sourceViewer != null) {
			sourceViewer.addSelectionChangedListener(addLineCommentAction);
//					sourceViewer.addSelectionChangedListener(addGeneralCommentAction);
		}
		mergeSourceViewer.addTextAction(addLineCommentAction);
//				mergeSourceViewer.addTextAction(addGeneralCommentAction);
	}

	private void createHighlighting(Class<SourceViewer> sourceViewerClazz) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchFieldException {
		// TODO this could use some performance tweaks
		final StyledText styledText = sourceViewer.getTextWidget();
		styledText.addLineBackgroundListener(new ColoringLineBackgroundListener(styledText));
	}

	/*
	 * overview ruler problem: displayed in both viewers. the diff editor ruler is actually custom drawn (see
	 * TextMergeViewer.fBirdsEyeCanvas) the ruler that gets created in this method is longer than the editor, meaning its
	 * not an overview (not next to the scrollbar)
	 */
	@SuppressWarnings("unused")
	private void createOverviewRuler(IDocument newInput, Class<SourceViewer> sourceViewerClazz)
			throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		sourceViewer.setOverviewRulerAnnotationHover(new CommentAnnotationHover(null));

		OverviewRuler ruler = new OverviewRuler(new DefaultMarkerAnnotationAccess(), 15, EditorsPlugin.getDefault()
				.getSharedTextColors());
		Field compositeField = sourceViewerClazz.getDeclaredField("fComposite");
		compositeField.setAccessible(true);

		ruler.createControl((Composite) compositeField.get(sourceViewer), sourceViewer);
		ruler.setModel(annotationModel);
		//ruler.setModel(compareAnnotationModel.leftAnnotationModel);
		// XXX should go through SourceViewerDecorationSupport instead
//		ruler.addAnnotationType("org.eclipse.mylyn.reviews.ui.comment.Annotation");
//		ruler.setAnnotationTypeLayer("org.eclipse.mylyn.reviews.ui.comment.Annotation", 1);
//		ruler.update();

		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(sourceViewer, ruler,
				annotationAccess, EditorsUI.getSharedTextColors());
		Iterator<?> e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}
		support.install(EditorsUI.getPreferenceStore());
		sourceViewer.getControl().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.dispose();
			}
		});

		Field overViewRulerField = sourceViewerClazz.getDeclaredField("fOverviewRuler");
		overViewRulerField.setAccessible(true);

		if (overViewRulerField.get(sourceViewer) == null) {
			overViewRulerField.set(sourceViewer, ruler);
		}

		Method declareMethod = sourceViewerClazz.getDeclaredMethod("ensureOverviewHoverManagerInstalled");
		declareMethod.setAccessible(true);
		declareMethod.invoke(sourceViewer);
		// overviewRuler is null

		Field hoverManager = sourceViewerClazz.getDeclaredField("fOverviewRulerHoveringController");
		hoverManager.setAccessible(true);
		AnnotationBarHoverManager manager = (AnnotationBarHoverManager) hoverManager.get(sourceViewer);
		if (manager != null) {
			Field annotationHover = AnnotationBarHoverManager.class.getDeclaredField("fAnnotationHover");
			annotationHover.setAccessible(true);
			IAnnotationHover hover = (IAnnotationHover) annotationHover.get(manager);
			annotationHover.set(manager, new CommentAnnotationHover(null));
		}
		sourceViewer.showAnnotations(true);
		sourceViewer.showAnnotationsOverview(true);

		declareMethod = sourceViewerClazz.getDeclaredMethod("showAnnotationsOverview", new Class[] { Boolean.TYPE });
		declareMethod.setAccessible(true);
	}

	private void createVerticalRuler(IDocument newInput, Class<SourceViewer> sourceViewerClazz)
			throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException {

		forceCustomAnnotationHover();

		Method declaredMethod2 = sourceViewerClazz.getDeclaredMethod("getVerticalRuler");
		declaredMethod2.setAccessible(true);
		CompositeRuler ruler = (CompositeRuler) declaredMethod2.invoke(sourceViewer);
		boolean hasDecorator = false;

		Iterator<?> iter = (ruler).getDecoratorIterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof AnnotationColumn) {
				hasDecorator = true;
			}
		}

		if (!hasDecorator) {
			AnnotationColumn annotationColumn = new AnnotationColumn();
			annotationColumn.createControl(ruler, ruler.getControl().getParent());
			ruler.addDecorator(0, annotationColumn);
		}
	}

}