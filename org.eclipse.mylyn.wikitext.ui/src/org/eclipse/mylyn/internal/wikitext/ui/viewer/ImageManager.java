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
import org.eclipse.jface.text.hyperlink.IHyperlink;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.ibm.icu.text.MessageFormat;

/**
 * Manages all aspects of image download/display in an {@link HtmlViewer}.
 * 
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
	private class HyperlinkMouseListener implements MouseMoveListener, MouseListener {
		public void mouseEnter(MouseEvent e) {
			// ignore
		}

		public void mouseExit(MouseEvent e) {
			disarm();
		}

		public void mouseMove(MouseEvent e) {
			adjust(e);
		}

		public void mouseDoubleClick(MouseEvent e) {
			// ignore
		}

		public void mouseDown(MouseEvent e) {
			// ignore
		}

		public void mouseUp(MouseEvent e) {
			clicked(e);
		}
	}

	private final HtmlViewer viewer;

	private final Display display;

	private final ImageCache imageCache;

	private final Set<ImageAnnotation> annotations = new HashSet<ImageAnnotation>();

	private ImageResolver imageResolver;

	private boolean computingChanges;

	private final AnnotationPainter painter;

	private boolean armed = false;

	private Cursor cursor;

	private Cursor previousCursor;

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

		// bug 257868 support image hyperlinks
		HyperlinkMouseListener mouseListener = new HyperlinkMouseListener();
		viewer.getTextWidget().addMouseMoveListener(mouseListener);
		viewer.getTextWidget().addMouseListener(mouseListener);

	}

	private IHyperlink getHyperlink(MouseEvent e) {
		// bug 257868 support image hyperlinks
		if (annotations.isEmpty()) {
			return null;
		}
		Point point = new Point(e.x, e.y);
		for (ImageAnnotation annotation : annotations) {
			if (annotation.getHyperlnkAnnotation() == null) {
				continue;
			}
			Rectangle region = getRegion(annotation);
			if (region != null) {
				if (region.contains(point)) {
					AnnotationHyperlinkDetector detector = (AnnotationHyperlinkDetector) viewer.getTextWidget()
							.getData(AnnotationHyperlinkDetector.class.getName());
					if (detector != null) {
						IHyperlink hyperlink = detector.createHyperlink(viewer, viewer.getAnnotationModel(),
								annotation.getHyperlnkAnnotation());
						return hyperlink;
					}
				}
			}
		}
		return null;
	}

	public void clicked(MouseEvent e) {
		IHyperlink hyperlink = getHyperlink(e);
		if (hyperlink != null) {
			disarm();
			hyperlink.open();
		}
	}

	/**
	 * get the widget-relative region of the given annotation
	 * 
	 * @return the region, or null if it is unknown
	 */
	private Rectangle getRegion(ImageAnnotation annotation) {
		if (annotation.getImage() == null) {
			return null;
		}
		Position position = viewer.getAnnotationModel().getPosition(annotation);
		Point locationAtOffset = viewer.getTextWidget().getLocationAtOffset(position.offset);
		Rectangle bounds = annotation.getImage().getBounds();
		Rectangle rectange = new Rectangle(locationAtOffset.x, locationAtOffset.y, bounds.width, bounds.height);
		return rectange;
	}

	void adjust(MouseEvent e) {
		IHyperlink hyperlink = getHyperlink(e);
		if (hyperlink == null) {
			disarm();
		} else {
			// always arm here even if already armed, otherwise the cursor
			// setting interacts poorly with other things that set the cursor.
			armed = true;
			Cursor currentCursor = viewer.getTextWidget().getCursor();
			if (cursor == null || currentCursor != cursor) {
				previousCursor = currentCursor;
				if (cursor == null) {
					cursor = new Cursor(viewer.getTextWidget().getDisplay(), SWT.CURSOR_HAND);
				}
				viewer.getTextWidget().setCursor(cursor);
			}
		}
	}

	void disarm() {
		if (armed) {
			if (previousCursor != null) {
				viewer.getTextWidget().setCursor(previousCursor);
			}
			armed = false;
		}
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
		if (cursor != null) {
			cursor.dispose();
			cursor = null;
		}
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
						Point extent = gc.textExtent("\n"); //$NON-NLS-1$
						if (extent.y > 0) {
							int numNewlines = (int) Math.ceil(((double) height) / ((double) extent.y));
							final int originalNewlines = numNewlines;
							IDocument document = viewer.getDocument();
							try {
								for (int x = position.offset; x < document.getLength(); ++x) {
									if (document.getChar(x) == '\n') {
										if (x != position.offset
												&& Util.annotationsIncludeOffset(viewer.getAnnotationModel(), x)) {
											break;
										}
										--numNewlines;
									} else {
										break;
									}
								}
								if (numNewlines > 0) {
									String newlines = ""; //$NON-NLS-1$
									for (int x = 0; x < numNewlines; ++x) {
										newlines += "\n"; //$NON-NLS-1$
									}
									document.replace(position.offset + 1, 0, newlines);
								} else if (numNewlines < 0) {
									document.replace(position.offset, -numNewlines, ""); //$NON-NLS-1$
								}
								if (numNewlines != 0) {
									// no need to fixup other annotation positions, since the annotation model is hooked into the document.

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

								// bug# 248643: update the annotation size to reflect the full size of the image
								//              so that it gets repainted when some portion of the image is exposed
								//              as a result of scrolling
								if (position.getLength() != originalNewlines) {
									annotationModel.modifyAnnotationPosition(annotation, new Position(position.offset,
											originalNewlines));
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
									WikiTextUiPlugin.getDefault()
											.log(
													IStatus.ERROR,
													MessageFormat.format(
															Messages.getString("ImageManager.4"), new Object[] { location }), e); //$NON-NLS-1$
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
