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
package org.eclipse.mylyn.wikitext.ui.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.AnnotationHyperlinkDetector;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.TextHover;
import org.eclipse.mylyn.wikitext.ui.annotation.TitleAnnotation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;

/**
 * A configuration for use with a {@link HtmlViewer}.
 * 
 * @author David Green
 * @since 1.0
 */
public class HtmlViewerConfiguration extends AbstractTextSourceViewerConfiguration {

	private final HtmlViewer viewer;

	private TextPresentation textPresentation;

	private boolean disableHyperlinkModifiers = true;

	public HtmlViewerConfiguration(HtmlViewer viewer) {
		super(getDefaultPreferenceStore());
		this.viewer = viewer;
//		  filters the platform URL hyperlink detector since the URL hyperlink detection
//		  strategy is defined by the HTML markup. 
		addHyperlinkDetectorDescriptorFilter(new HyperlinkDetectorDescriptorFilter() {
			public boolean filter(HyperlinkDetectorDescriptor descriptor) {
				String id = descriptor.getId();
				if ("org.eclipse.ui.internal.editors.text.URLHyperlinkDetector".equals(id)) { //$NON-NLS-1$
					// filter out the platform URL hyperlink detector since Mylyn contributes one as well.
					return true;
				}
				return false;
			}
		});
	}

	private static IPreferenceStore getDefaultPreferenceStore() {
		// bug# 245759: must work outside of an Eclipse runtime
		return WikiTextUiPlugin.getDefault() == null ? null : EditorsUI.getPreferenceStore();
	}

	public HtmlViewerConfiguration(HtmlViewer viewer, IPreferenceStore preferenceStore) {
		super(preferenceStore);
		this.viewer = viewer;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		MarkupViewerDamagerRepairer dr = createMarkupViewerDamagerRepairer();
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	private MarkupViewerDamagerRepairer createMarkupViewerDamagerRepairer() {
		return new MarkupViewerDamagerRepairer();
	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DefaultAnnotationHover() {
			@Override
			protected boolean isIncluded(Annotation annotation) {
				return annotation.getType().equals(TitleAnnotation.TYPE) || isShowInVerticalRuler(annotation);
			}
		};
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return new TextHover(sourceViewer);
	}

	@Override
	protected List<IHyperlinkDetector> createCustomHyperlinkDetectors(ISourceViewer sourceViewer) {
		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>(1);
		AnnotationHyperlinkDetector annotationHyperlinkDetector = createAnnotationHyperlinkDetector();
		sourceViewer.getTextWidget().setData(AnnotationHyperlinkDetector.class.getName(), annotationHyperlinkDetector);
		detectors.add(annotationHyperlinkDetector);
		return detectors;
	}

	protected AnnotationHyperlinkDetector createAnnotationHyperlinkDetector() {
		return new AnnotationHyperlinkDetector();
	}

	protected class MarkupViewerDamagerRepairer implements IPresentationDamager, IPresentationRepairer {

		private IDocument document;

		public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged) {
			return new Region(0, document.getLength());
		}

		public void setDocument(IDocument document) {
			this.document = document;
		}

		public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
			TextPresentation viewerPresentation = textPresentation == null ? viewer.getTextPresentation()
					: textPresentation;
			presentation.clear();
			if (viewerPresentation == null) {
				return;
			}
			StyleRange defaultStyleRange = viewerPresentation.getDefaultStyleRange();
			presentation.setDefaultStyleRange((StyleRange) (defaultStyleRange == null ? null
					: defaultStyleRange.clone()));
			List<StyleRange> ranges = new ArrayList<StyleRange>();

			Iterator<?> allStyleRangeIterator = viewerPresentation.getAllStyleRangeIterator();
			while (allStyleRangeIterator.hasNext()) {
				StyleRange range = (StyleRange) allStyleRangeIterator.next();
				ranges.add((StyleRange) range.clone());
			}

			// fix for bug 237170: presentation must not have any gaps otherwise the hyperlinks won't always get reset
			// to their non-highlighted state.  So detect gaps and fill the gaps with the default presentation style.
			int start = damage.getOffset();
			for (int x = 0; x < ranges.size(); ++x) {
				StyleRange range = ranges.get(x);
				if (range.start > start) {
					StyleRange newRange = defaultStyleRange == null ? new StyleRange()
							: (StyleRange) defaultStyleRange.clone();
					newRange.start = start;
					newRange.length = range.start - start;

					ranges.add(++x, newRange);

				}
				start = range.start + range.length + 1;
			}
			if (start < (damage.getOffset() + damage.getLength())) {
				StyleRange newRange = defaultStyleRange == null ? new StyleRange()
						: (StyleRange) defaultStyleRange.clone();
				newRange.start = start;
				newRange.length = (damage.getOffset() + damage.getLength()) - start;
				ranges.add(newRange);
			}

			presentation.replaceStyleRanges(ranges.toArray(new StyleRange[ranges.size()]));
		}

	}

	@SuppressWarnings("unchecked")
	public void setTextPresentation(TextPresentation textPresentation) {
		if (textPresentation != null) {
			TextPresentation textPresentationCopy = new TextPresentation();
			textPresentationCopy.setDefaultStyleRange((StyleRange) (textPresentation.getDefaultStyleRange() == null ? null
					: textPresentation.getDefaultStyleRange().clone()));
			Iterator<StyleRange> iterator = textPresentation.getAllStyleRangeIterator();
			while (iterator.hasNext()) {
				StyleRange styleRange = iterator.next();
				textPresentationCopy.addStyleRange((StyleRange) styleRange.clone());
			}
			textPresentation = textPresentationCopy;
		}
		this.textPresentation = textPresentation;
	}

	@Override
	public int getHyperlinkStateMask(ISourceViewer sourceViewer) {
		if (disableHyperlinkModifiers) {
			return SWT.NONE;
		}
		return super.getHyperlinkStateMask(sourceViewer);
	}

	/**
	 * Indicate if hyperlink modifiers are disabled. When disabled (the default) no keyboard modifiers are required to
	 * activate hyperlinks when clicking.
	 * 
	 * @see #getHyperlinkStateMask(ISourceViewer)
	 */
	public boolean isDisableHyperlinkModifiers() {
		return disableHyperlinkModifiers;
	}

	/**
	 * Indicate if hyperlink modifiers are disabled. When disabled (the default) no keyboard modifiers are required to
	 * activate hyperlinks when clicking.
	 * 
	 * @see #getHyperlinkStateMask(ISourceViewer)
	 */
	public void setDisableHyperlinkModifiers(boolean disableHyperlinkModifiers) {
		this.disableHyperlinkModifiers = disableHyperlinkModifiers;
	}
}
