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
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.wikitext.ui.annotation.AnchorHrefAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.AnchorNameAnnotation;
import org.eclipse.mylyn.wikitext.ui.annotation.IdAnnotation;

public class AnnotationHyperlinkDetector implements IHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (textViewer instanceof ISourceViewer) {

			// Note: only find hyperlinks that include the region end point, otherwise we may get hyperlinks that
			//       are not even relevant.
			int interestingOffset = region.getOffset() + region.getLength();

			ISourceViewer sourceViewer = (ISourceViewer) textViewer;
			IAnnotationModel annotationModel = sourceViewer.getAnnotationModel();
			if (annotationModel != null) {
				List<AnchorHrefAnnotation> hrefs = null;
				Iterator<?> iterator = annotationModel.getAnnotationIterator();
				while (iterator.hasNext()) {
					Annotation annotation = (Annotation) iterator.next();
					if (annotation instanceof AnchorHrefAnnotation) {
						AnchorHrefAnnotation href = (AnchorHrefAnnotation) annotation;
						Position position = annotationModel.getPosition(href);
						if (position.getOffset() <= interestingOffset
								&& position.getOffset() + position.getLength() >= interestingOffset) {
							if (hrefs == null) {
								hrefs = new ArrayList<AnchorHrefAnnotation>();
							}
							hrefs.add(href);
						}
					}
				}
				if (hrefs != null) {
					if (hrefs.size() > 1) {
						// put greatest offset annotations first.
						Collections.sort(hrefs, new OffsetComparator(annotationModel));
					}
					return new IHyperlink[] { createHyperlink(sourceViewer, annotationModel, hrefs.get(0)) };
				}
			}
		}
		return null;
	}

	protected IHyperlink createHyperlink(ISourceViewer viewer, IAnnotationModel annotationModel,
			AnchorHrefAnnotation anchorHrefAnnotation) {
		Position position = annotationModel.getPosition(anchorHrefAnnotation);
		IRegion region = new Region(position.getOffset(), position.getLength());
		String href = anchorHrefAnnotation.getAnchorHref();
		if (href != null && href.startsWith("#")) { //$NON-NLS-1$
			return new DocumentHyperlink(viewer, region, href);
		} else {
			return createUrlHyperlink(region, href);
		}
	}

	protected IHyperlink createUrlHyperlink(IRegion region, String href) {
		return new URLHyperlink(region, href);
	}

	/**
	 * a comparator that puts greatest offset annotations first
	 */
	private static class OffsetComparator implements Comparator<Annotation> {

		private final IAnnotationModel annotationModel;

		public OffsetComparator(IAnnotationModel annotationModel) {
			this.annotationModel = annotationModel;
		}

		public int compare(Annotation o1, Annotation o2) {
			if (o1 == o2) {
				return 0;
			}
			Position p1 = annotationModel.getPosition(o1);
			Position p2 = annotationModel.getPosition(o2);
			if (p1.getOffset() > p2.getOffset()) {
				return -1;
			} else if (p2.getOffset() > p1.getOffset()) {
				return 1;
			} else {
				if (p1.getLength() > p2.getLength()) {
					return -1;
				} else if (p2.getLength() > p1.getLength()) {
					return 1;
				}
				return new Integer(System.identityHashCode(p1)).compareTo(System.identityHashCode(p2));
			}
		}

	}

	/**
	 * A hyperlink implementation that causes the viewer's selection (and scrolling) to adjust to the hyperlink target.
	 * 
	 * @author David Green
	 * 
	 */
	protected static class DocumentHyperlink implements IHyperlink {

		private final ISourceViewer viewer;

		private final IRegion region;

		private final String href;

		public DocumentHyperlink(ISourceViewer viewer, IRegion region, String href) {
			this.viewer = viewer;
			this.region = region;
			this.href = href;
		}

		public IRegion getHyperlinkRegion() {
			return region;
		}

		public String getHyperlinkText() {
			return null;
		}

		public String getTypeLabel() {
			return null;
		}

		public void open() {
			String lookingFor = href.substring(1); // lose the leading '#'

			IAnnotationModel annotationModel = viewer.getAnnotationModel();
			Iterator<?> iterator = annotationModel.getAnnotationIterator();
			while (iterator.hasNext()) {
				Annotation annotation = (Annotation) iterator.next();
				if (annotation instanceof IdAnnotation) {
					IdAnnotation idAnnotation = (IdAnnotation) annotation;
					if (!idAnnotation.getElementId().equals(lookingFor)) {
						continue;
					}
				} else if (annotation instanceof AnchorNameAnnotation) {
					AnchorNameAnnotation nameAnnotation = (AnchorNameAnnotation) annotation;
					if (!nameAnnotation.getAnchorName().equals(lookingFor)) {
						continue;
					}
				} else {
					continue;
				}
				Position position = annotationModel.getPosition(annotation);
				viewer.getTextWidget().setSelection(position.getOffset());
				break;
			}
		}

	}
}
