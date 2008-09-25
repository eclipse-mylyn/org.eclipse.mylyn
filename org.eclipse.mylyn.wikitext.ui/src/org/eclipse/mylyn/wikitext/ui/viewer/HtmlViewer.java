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

import java.io.IOException;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.mylyn.internal.wikitext.ui.util.ImageCache;
import org.eclipse.mylyn.internal.wikitext.ui.util.WikiTextUiResources;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.HtmlTextPresentationParser;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.ImageManager;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.BulletAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.BulletDrawingStrategy;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.HorizontalRuleAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.HorizontalRuleDrawingStrategy;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.ImageAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.ImageDrawingStrategy;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author David Green
 */
public class HtmlViewer extends SourceViewer {

	private TextPresentation textPresentation;

	private boolean haveInit = false;

	private HtmlViewerConfiguration configuration;

	private final ImageCache imageCache = new ImageCache();

	private boolean displayImages = true;

	public HtmlViewer(Composite parent, IVerticalRuler ruler, int styles) {
		super(parent, ruler, styles);
		setEditable(false);
	}

	public HtmlViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean showAnnotationsOverview, int styles) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		setEditable(false);
	}

	@SuppressWarnings("deprecation")
	private void initPainter() {
		if (haveInit) {
			return;
		}
		haveInit = true;

		// bug# 245759: must work outside of an Eclipse runtime
		ColorRegistry colorRegistry = WikiTextUiResources.getColors();

		IAnnotationAccess annotationAccess = new IAnnotationAccess() {
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}

			public boolean isMultiLine(Annotation annotation) {
				return true;
			}

			public boolean isTemporary(Annotation annotation) {
				return true;
			}

		};
		AnnotationPainter painter = new AnnotationPainter(this, annotationAccess);
		// paint bullets
		painter.addDrawingStrategy(BulletAnnotation.TYPE, new BulletDrawingStrategy());
		painter.addAnnotationType(BulletAnnotation.TYPE, BulletAnnotation.TYPE);
		painter.setAnnotationTypeColor(BulletAnnotation.TYPE, getTextWidget().getForeground());
		// paint HR
		painter.addDrawingStrategy(HorizontalRuleAnnotation.TYPE, new HorizontalRuleDrawingStrategy());
		painter.addAnnotationType(HorizontalRuleAnnotation.TYPE, HorizontalRuleAnnotation.TYPE);
		painter.setAnnotationTypeColor(HorizontalRuleAnnotation.TYPE, colorRegistry.get(WikiTextUiResources.COLOR_HR));
		if (displayImages) {
			// paint images
			painter.addDrawingStrategy(ImageAnnotation.TYPE, new ImageDrawingStrategy(this));
			painter.addAnnotationType(ImageAnnotation.TYPE, ImageAnnotation.TYPE);
			painter.setAnnotationTypeColor(ImageAnnotation.TYPE, getTextWidget().getForeground());
		}

		addTextPresentationListener(painter);
		addPainter(painter);

		getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				imageCache.dispose();
			}
		});

		if (displayImages) {
			new ImageManager(this, imageCache, painter);
		}
	}

	protected ParseResult parse(String htmlText) {
		initPainter();

		ParseResult result = new ParseResult();

		result.textPresentation = new TextPresentation();

		HtmlTextPresentationParser parser = new HtmlTextPresentationParser();
		if (displayImages) {
			parser.setImageCache(imageCache);
			parser.setEnableImages(displayImages);
		}
		parser.setPresentation(result.textPresentation);
		parser.setDefaultFont(getTextWidget().getFont());
		result.annotationModel = new AnnotationModel();
		parser.setAnnotationModel(result.annotationModel);

		GC gc = new GC(getTextWidget());
		try {
			parser.setGC(gc);

			parser.parse(htmlText);
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			gc.dispose();
		}

		result.text = parser.getText();

		return result;
	}

	protected static class ParseResult {
		public TextPresentation textPresentation;

		TextPresentation presentation;

		String text;

		AnnotationModel annotationModel;
	}

	@Override
	public void configure(SourceViewerConfiguration configuration) {
		if (configuration instanceof HtmlViewerConfiguration) {
			this.configuration = (HtmlViewerConfiguration) configuration;
			if (textPresentation != null) {
				this.configuration.setTextPresentation(textPresentation);
			}
		}
		super.configure(configuration);
	}

	public void setHtml(String htmlText) {
		ParseResult result = parse(htmlText);
		textPresentation = result.textPresentation;

		if (configuration != null) {
			configuration.setTextPresentation(textPresentation);
		}
		setDocumentNoMarkup(new Document(result.text), result.annotationModel);
	}

	@Override
	public void setDocument(IDocument document, IAnnotationModel annotationModel, int modelRangeOffset,
			int modelRangeLength) {
		if (document != null) {
			String htmlText = document.get();
			if (htmlText.length() > 0) {
				ParseResult result = parse(htmlText);
				textPresentation = result.textPresentation;
				document.set(result.text);
				annotationModel = result.annotationModel;
			} else {
				if (textPresentation != null) {
					textPresentation.clear();
				} else {
					textPresentation = new TextPresentation();
				}
			}
			if (configuration != null) {
				configuration.setTextPresentation(textPresentation);
			}
		}
		setDocumentNoMarkup(document, annotationModel);
	}

	protected void setDocumentNoMarkup(IDocument document, IAnnotationModel annotationModel) {
		super.setDocument(document, annotationModel, -1, -1);
	}

	public TextPresentation getTextPresentation() {
		return textPresentation;
	}

	public void setTextPresentation(TextPresentation textPresentation) {
		this.textPresentation = textPresentation;
		if (configuration != null) {
			configuration.setTextPresentation(textPresentation);
		}
		changeTextPresentation(textPresentation, false);
	}

	/**
	 * indicate if the viewer should support display of images.
	 */
	public boolean isDisplayImages() {
		return displayImages;
	}

	/**
	 * indicate if the viewer should support display of images.
	 */
	public void setDisplayImages(boolean displayImages) {
		this.displayImages = displayImages;
	}

}
