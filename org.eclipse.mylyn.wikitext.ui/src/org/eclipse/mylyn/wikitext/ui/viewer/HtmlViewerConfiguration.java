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
package org.eclipse.mylyn.wikitext.ui.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetectorExtension;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetectorExtension2;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.AnnotationHyperlinkDetector;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.TextHover;
import org.eclipse.mylyn.wikitext.ui.annotation.TitleAnnotation;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;
import org.eclipse.ui.texteditor.HyperlinkDetectorRegistry;

/**
 *
 *
 * @author David Green
 */
public class HtmlViewerConfiguration extends TextSourceViewerConfiguration {

	private class HyperlinkDetectorDelegate implements IHyperlinkDetector, IHyperlinkDetectorExtension, IHyperlinkDetectorExtension2 {
		
		private HyperlinkDetectorDescriptor descriptor;
		private AbstractHyperlinkDetector delegate;
		private boolean createFailed;
		private IAdaptable context;
		private int stateMask;
		private boolean enabled;

		
		private HyperlinkDetectorDelegate(HyperlinkDetectorDescriptor descriptor) {
			this.descriptor= descriptor;
			if (fPreferenceStore != null) {
				stateMask = fPreferenceStore.getInt(descriptor.getId()
						+ HyperlinkDetectorDescriptor.STATE_MASK_POSTFIX);
				enabled = !fPreferenceStore.getBoolean(descriptor.getId());
			}
		}

		public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
			if (!isEnabled())
				return null;
			
			if (!createFailed && delegate == null) {
				try {
					delegate = descriptor.createHyperlinkDetector();
				} catch (CoreException ex) {
					createFailed = true;
				}
				if (delegate != null && context != null)
					delegate.setContext(context);
			}
			if (delegate != null) {
				return delegate.detectHyperlinks(textViewer, region,
						canShowMultipleHyperlinks);
			}
			
			return null;
		}
		
		private boolean isEnabled() {
			return enabled;
		}

		private void setContext(IAdaptable context) {
			this.context = context;
		}
		
		public void dispose() {
			if (delegate != null) {
				delegate.dispose();
				delegate = null;
			}
			descriptor = null;
			context = null;
		}
		
		public int getStateMask() {
			return stateMask;
		}
	}

	
	private final HtmlViewer viewer;
	private TextPresentation textPresentation;

	public HtmlViewerConfiguration(HtmlViewer viewer) {
		super(EditorsUI.getPreferenceStore());
		this.viewer = viewer;
	}

	public HtmlViewerConfiguration(HtmlViewer viewer,IPreferenceStore preferenceStore) {
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


	@SuppressWarnings("unchecked")
	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null || fPreferenceStore == null) {
			return new IHyperlinkDetector[] { new AnnotationHyperlinkDetector() };
		}
		HyperlinkDetectorRegistry registry = EditorsUI.getHyperlinkDetectorRegistry();
		HyperlinkDetectorDescriptor[] descriptors = registry.getHyperlinkDetectorDescriptors();
		Map<String,IAdaptable> targets = getHyperlinkDetectorTargets(sourceViewer);

		List<IHyperlinkDetector> detectors = new ArrayList<IHyperlinkDetector>(8);
		detectors.add(new AnnotationHyperlinkDetector());
		
		for (Map.Entry<String, IAdaptable> target: targets.entrySet()) {
			String targetId = target.getKey();
			IAdaptable context = target.getValue();
			
			for (HyperlinkDetectorDescriptor descriptor: descriptors) {
				if (targetId.equals(descriptor.getTargetId())) {
					String id = descriptor.getId();
					if ("org.eclipse.ui.internal.editors.text.URLHyperlinkDetector".equals(id)) {
						// filter out the platform URL hyperlink detector since Mylyn contributes one as well.
						continue;
					}
					HyperlinkDetectorDelegate delegate = new HyperlinkDetectorDelegate(descriptor);
					delegate.setContext(context);
					detectors.add(delegate);
				}
			}
		}
		return detectors.toArray(new IHyperlinkDetector[detectors.size()]);
	}


	protected class MarkupViewerDamagerRepairer implements IPresentationDamager, IPresentationRepairer {
		
		private IDocument document;

		public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged) {
			return new Region(0,document.getLength());
		}

		public void setDocument(IDocument document) {
			this.document = document;
		}

		public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
			TextPresentation viewerPresentation = textPresentation==null?viewer.getTextPresentation():textPresentation;
			presentation.clear();
			if (viewerPresentation == null) {
				return;
			}
			StyleRange defaultStyleRange = viewerPresentation.getDefaultStyleRange();
			presentation.setDefaultStyleRange((StyleRange) (defaultStyleRange==null?null:defaultStyleRange.clone()));
			List<StyleRange> ranges = new ArrayList<StyleRange>();

			Iterator<?> allStyleRangeIterator = viewerPresentation.getAllStyleRangeIterator();
			while (allStyleRangeIterator.hasNext()) {
				StyleRange range = (StyleRange) allStyleRangeIterator.next();
				ranges.add((StyleRange) range.clone());
			}
			
			// fix for bug 237170: presentation must not have any gaps otherwise the hyperlinks won't always get reset
			// to their non-highlighted state.  So detect gaps and fill the gaps with the default presentation style.
			int start = damage.getOffset();
			for (int x = 0;x<ranges.size();++x) {
				StyleRange range = ranges.get(x);
				if (range.start > start) {
					StyleRange newRange = defaultStyleRange==null?new StyleRange():(StyleRange)defaultStyleRange.clone();
					newRange.start = start;
					newRange.length = range.start-start;
					
					ranges.add(++x,newRange);
					
				}
				start = range.start+range.length+1;
			}
			if (start < (damage.getOffset()+damage.getLength())) {
				StyleRange newRange = defaultStyleRange==null?new StyleRange():(StyleRange)defaultStyleRange.clone();
				newRange.start = start;
				newRange.length = (damage.getOffset()+damage.getLength())-start;
				ranges.add(newRange);				
			}

			presentation.replaceStyleRanges(ranges.toArray(new StyleRange[ranges.size()]));
		}

	}


	@SuppressWarnings("unchecked")
	public void setTextPresentation(TextPresentation textPresentation) {
		if (textPresentation != null) {
			TextPresentation textPresentationCopy = new TextPresentation();
			textPresentationCopy.setDefaultStyleRange((StyleRange) (textPresentation.getDefaultStyleRange()==null?null:textPresentation.getDefaultStyleRange().clone()));
			Iterator<StyleRange> iterator = textPresentation.getAllStyleRangeIterator();
			while (iterator.hasNext()) {
				StyleRange styleRange = iterator.next();
				textPresentationCopy.addStyleRange((StyleRange) styleRange.clone());
			}
			textPresentation = textPresentationCopy;
		}
		this.textPresentation = textPresentation;
	}
}
