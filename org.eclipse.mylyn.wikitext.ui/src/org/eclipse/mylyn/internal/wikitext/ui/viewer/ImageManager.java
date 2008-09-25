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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.util.ImageCache;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.ImageAnnotation;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.ImageDrawingStrategy;
import org.eclipse.mylyn.wikitext.ui.viewer.HtmlViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * Manages the download of images for viewing in an {@link HtmlViewer}, and creates appropriate space for their display.
 * 
 * Downloads image data in a background thread, instantiates the corresopnding images, and ensures that enough vertical
 * space exists in the viewer to display the images.
 * 
 * @see ImageAnnotation
 * @see ImageDrawingStrategy
 * @see ImageCache
 * @see HtmlViewer
 * 
 * @author David Green
 * 
 */
public class ImageManager implements ITextInputListener, DisposeListener, IDocumentListener, ISelectionChangedListener {
	private final HtmlViewer viewer;

	private final Display display;

	private final ImageCache imageCache;

	private final Set<ImageAnnotation> annotations = new HashSet<ImageAnnotation>();

	private ImageResolver imageResolver;

	private boolean computingChanges;

	private final AnnotationPainter painter;

	public ImageManager(HtmlViewer viewer, ImageCache imageCache, AnnotationPainter painter) {
		this.viewer = viewer;
		this.painter = painter;
		display = viewer.getTextWidget().getDisplay();
		this.imageCache = imageCache;
		inspect();
		viewer.getTextWidget().addDisposeListener(this);
		viewer.addTextInputListener(this);
		if (viewer.getDocument() != null) {
			viewer.getDocument().addDocumentListener(this);
		}
		viewer.addSelectionChangedListener(this);
		viewer.addPostSelectionChangedListener(this);
	}

	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		stop();
	}

	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		if (oldInput != null) {
			oldInput.removeDocumentListener(this);
		}
		if (newInput != null) {
			newInput.addDocumentListener(this);
		}
		inspect();
	}

	@SuppressWarnings("unchecked")
	private void inspect() {
		synchronized (this) {
			annotations.clear();
			if (viewer.getAnnotationModel() != null) {
				Iterator<Annotation> iterator = viewer.getAnnotationModel().getAnnotationIterator();
				while (iterator.hasNext()) {
					Annotation annotation = iterator.next();
					if (annotation instanceof ImageAnnotation) {
						annotations.add((ImageAnnotation) annotation);
					}
				}
			}
		}
		if (!annotations.isEmpty()) {
			ImageResolver resolver;
			synchronized (this) {
				resolver = imageResolver;
			}
			if (resolver != null) {
				try {
					resolver.join();
				} catch (InterruptedException e) {
					return;
				}
			}
			imageResolver = new ImageResolver();
			imageResolver.start();
		}
	}

	private synchronized void stop() {
		ImageResolver resolver = imageResolver;
		if (resolver != null) {
			resolver.interrupt();
		}
	}

	public void widgetDisposed(DisposeEvent e) {
		stop();
	}

	@SuppressWarnings("unchecked")
	private void updateImage(String imgSrc, ImageData imageData) {
		if (display.isDisposed() || viewer.getTextWidget().isDisposed()) {
			return;
		}
		Image image = imageData == null ? imageCache.getMissingImage() : ImageDescriptor.createFromImageData(imageData)
				.createImage();
		imageCache.putImage(imgSrc, image);

		Set<ImageAnnotation> modifiedAnnotations = new HashSet<ImageAnnotation>();

		AnnotationModel annotationModel = (AnnotationModel) viewer.getAnnotationModel();
		Object annotationLockObject = annotationModel.getLockObject();
		if (annotationLockObject == null) {
			annotationLockObject = annotationModel;
		}
		synchronized (annotationLockObject) {
			Iterator<Annotation> iterator = annotationModel.getAnnotationIterator();
			while (iterator.hasNext()) {
				Annotation annotation = iterator.next();
				if (annotation instanceof ImageAnnotation) {
					ImageAnnotation imageAnnotation = (ImageAnnotation) annotation;
					if (imgSrc.equals(imageAnnotation.getUrl())) {
						imageAnnotation.setImage(image);
						modifiedAnnotations.add(imageAnnotation);
					}
				}
			}
		}

		if (!modifiedAnnotations.isEmpty()) {
			computingChanges = true;
			try {
				boolean rangesAdjusted = false;
				List<StyleRange> ranges = new ArrayList<StyleRange>();

				Iterator<?> allStyleRangeIterator = viewer.getTextPresentation().getAllStyleRangeIterator();
				while (allStyleRangeIterator.hasNext()) {
					StyleRange range = (StyleRange) allStyleRangeIterator.next();
					ranges.add((StyleRange) range.clone());
				}

				GC gc = new GC(viewer.getTextWidget());
				try {
					viewer.getTextWidget().setRedraw(false);
					TextPresentation textPresentation = viewer.getTextPresentation();
					//			textPresentation.
					for (ImageAnnotation annotation : modifiedAnnotations) {
						int height = annotation.getImage().getBounds().height;
						Position position = annotationModel.getPosition(annotation);
						String widgetText = viewer.getTextWidget().getText();
						Font font = null;
						if (widgetText.length() > 0 && widgetText.length() > position.offset) {
							StyleRange styleRange = viewer.getTextWidget().getStyleRangeAtOffset(position.offset);
							if (styleRange != null) {
								font = styleRange.font;
							}
						}
						if (font == null) {
							font = viewer.getTextWidget().getFont();
						}
						gc.setFont(font);
						Point extent = gc.textExtent("\n");
						if (extent.y > 0) {
							int numNewlines = (int) Math.ceil(((double) height) / ((double) extent.y));
							IDocument document = viewer.getDocument();
							try {
								for (int x = position.offset; x < document.getLength(); ++x) {
									if (document.getChar(x) == '\n') {
										if (x != position.offset && annotationsIncludeOffset(x)) {
											break;
										}
										--numNewlines;
									} else {
										break;
									}
								}
								if (numNewlines > 0) {
									String newlines = "";
									for (int x = 0; x < numNewlines; ++x) {
										newlines += "\n";
									}
									document.replace(position.offset + 1, 0, newlines);
								} else if (numNewlines < 0) {
									document.replace(position.offset, -numNewlines, "");
								}
								if (numNewlines != 0) {
									// no need to fixup annotation positions, since the annotation model is hooked into the document.

									// fix up styles
									for (StyleRange range : ranges) {
										if (range.start > position.offset) {
											range.start += numNewlines;
											rangesAdjusted = true;
										} else if (range.start + range.length > position.offset) {
											range.length += numNewlines;
											rangesAdjusted = true;
										}
									}
								}
							} catch (BadLocationException e) {
								// ignore
							}
						}
					}
					if (rangesAdjusted) {
						TextPresentation presentation = new TextPresentation();
						if (textPresentation.getDefaultStyleRange() != null) {
							StyleRange defaultStyleRange = (StyleRange) textPresentation.getDefaultStyleRange().clone();
							if (viewer.getDocument() != null) {
								if (defaultStyleRange.length < viewer.getDocument().getLength()) {
									defaultStyleRange.length = viewer.getDocument().getLength();
								}
							}
							presentation.setDefaultStyleRange(defaultStyleRange);
						}
						for (StyleRange range : ranges) {
							presentation.addStyleRange(range);
						}
						viewer.setTextPresentation(presentation);
						viewer.invalidateTextPresentation();
					}
				} finally {
					viewer.getTextWidget().setRedraw(true);
					gc.dispose();
				}
				viewer.getTextWidget().redraw();
			} finally {
				computingChanges = false;
			}
		}
	}

	private boolean annotationsIncludeOffset(int offset) {
		AnnotationModel annotationModel = (AnnotationModel) viewer.getAnnotationModel();
		if (annotationModel == null) {
			return false;
		}
		Iterator<?> annotationIterator = annotationModel.getAnnotationIterator(offset, 1, true, true);
		return annotationIterator.hasNext();
	}

	private static final AtomicInteger resolverIdSeed = new AtomicInteger(1);

	private class ImageResolver extends Thread {

		public ImageResolver() {
			setName(ImageResolver.class.getSimpleName() + '-' + resolverIdSeed.getAndIncrement());
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				final Map<String, ImageData> urlToImageData = new HashMap<String, ImageData>();
				for (ImageAnnotation annotation : annotations) {
					final String imgSrc = annotation.getUrl();
					if (imgSrc != null && !urlToImageData.containsKey(imgSrc)) {
						try {
							URL location = imageCache.getBase() == null ? new URL(imgSrc) : new URL(
									imageCache.getBase(), imgSrc);

							try {
								InputStream in = new BufferedInputStream(location.openStream());
								try {
									urlToImageData.put(imgSrc, new ImageData(in));
								} catch (SWTException e) {
									if (e.code != SWT.ERROR_INVALID_IMAGE) {
										throw e;
									}
									urlToImageData.put(imgSrc, null);
								} finally {
									in.close();
								}
							} catch (Exception e) {
								if (WikiTextUiPlugin.getDefault() != null) {
									WikiTextUiPlugin.getDefault().log(IStatus.ERROR,
											String.format("Cannot access %s", location), e);
								}
								urlToImageData.put(imgSrc, null);
							}
						} catch (MalformedURLException e) {
							urlToImageData.put(imgSrc, null);
						}
						display.asyncExec(new Runnable() {
							public void run() {
								updateImage(imgSrc, urlToImageData.get(imgSrc));
							}
						});
					}
					if (Thread.currentThread().isInterrupted()) {
						break;
					}
				}
			} finally {
				imageResolver = null;
			}
		}
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		if (computingChanges) {
			return;
		}
		stop();
	}

	public void documentChanged(DocumentEvent event) {
		if (computingChanges) {
			return;
		}
		inspect();
	}

	public void selectionChanged(SelectionChangedEvent event) {
		GC gc = new GC(viewer.getTextWidget());
		try {
			Event e = new Event();
			e.gc = gc;
			e.widget = viewer.getTextWidget();
			Rectangle bounds = viewer.getTextWidget().getBounds();
			e.height = bounds.height;
			e.width = bounds.width;
			e.x = 0;
			e.y = 0;
			PaintEvent paintEvent = new PaintEvent(e);
			painter.paintControl(paintEvent);
		} finally {
			gc.dispose();
		}
	}

}
