/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tasktop Technologies - extracted FormHeading implementation for Mylyn
 *******************************************************************************/
package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.internal.forms.widgets.FormHeading;
import org.eclipse.ui.internal.forms.widgets.FormImages;

/**
 * Based on {@link FormHeading}.
 */
@SuppressWarnings("restriction")
public class GradientCanvas extends Canvas {

	private static final int SEPARATOR = 1 << 1;

	private static final int BOTTOM_SEPARATOR = 1 << 2;

	private static final int BACKGROUND_IMAGE_TILED = 1 << 3;

	public static final String COLOR_BASE_BG = "baseBg"; //$NON-NLS-1$

	private Image backgroundImage;

	private Image gradientImage;

	@SuppressWarnings("unchecked")
	Hashtable colors = new Hashtable();

	private int flags;

	private GradientInfo gradientInfo;

	private class GradientInfo {
		Color[] gradientColors;

		int[] percents;

		boolean vertical;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Control#forceFocus()
	 */
	@Override
	public boolean forceFocus() {
		return false;
	}

	/**
	 * Creates the form content control as a child of the provided parent.
	 * 
	 * @param parent
	 *            the parent widget
	 */
	public GradientCanvas(Composite parent, int style) {
		super(parent, style);
		setBackgroundMode(SWT.INHERIT_DEFAULT);
		setSeparatorAlignment(SWT.BOTTOM);
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				onPaint(e.gc);
			}
		});
		addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event e) {
				if (gradientImage != null) {
					FormImages.getInstance().markFinished(gradientImage);
					gradientImage = null;
				}
			}
		});
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				if (gradientInfo != null || (backgroundImage != null && !isBackgroundImageTiled())) {
					updateGradientImage();
				}
			}
		});
	}

	/**
	 * Sets the background color of the header.
	 */
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		internalSetBackground(bg);
	}

	private void internalSetBackground(Color bg) {
		putColor(COLOR_BASE_BG, bg);
	}

	public void setBackgroundGradient(Color[] gradientColors, int[] percents, boolean vertical) {
		if (gradientColors != null) {
			gradientInfo = new GradientInfo();
			gradientInfo.gradientColors = gradientColors;
			gradientInfo.percents = percents;
			gradientInfo.vertical = vertical;
			setBackground(null);
			updateGradientImage();
		} else {
			// reset
			gradientInfo = null;
			if (gradientImage != null) {
				FormImages.getInstance().markFinished(gradientImage);
				gradientImage = null;
				setBackgroundImage(null);
			}
		}
	}

	public void setHeadingBackgroundImage(Image image) {
		this.backgroundImage = image;
		if (image != null) {
			setBackground(null);
		}
		if (isBackgroundImageTiled()) {
			setBackgroundImage(image);
		} else {
			updateGradientImage();
		}
	}

	public Image getHeadingBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImageTiled(boolean tiled) {
		if (tiled) {
			flags |= BACKGROUND_IMAGE_TILED;
		} else {
			flags &= ~BACKGROUND_IMAGE_TILED;
		}
		setHeadingBackgroundImage(this.backgroundImage);
	}

	public boolean isBackgroundImageTiled() {
		return (flags & BACKGROUND_IMAGE_TILED) != 0;
	}

	@Override
	public void setBackgroundImage(Image image) {
		super.setBackgroundImage(image);
		if (image != null) {
			internalSetBackground(null);
		}
	}

	private void onPaint(GC gc) {
		if (!isSeparatorVisible() && getBackgroundImage() == null) {
			return;
		}
		Rectangle carea = getClientArea();
		Image buffer = new Image(getDisplay(), carea.width, carea.height);
		buffer.setBackground(getBackground());
		GC igc = new GC(buffer);
		igc.setBackground(getBackground());
		igc.fillRectangle(0, 0, carea.width, carea.height);
		if (getBackgroundImage() != null) {
			if (gradientInfo != null) {
				drawBackground(igc, carea.x, carea.y, carea.width, carea.height);
			} else {
				Image bgImage = getBackgroundImage();
				Rectangle ibounds = bgImage.getBounds();
				drawBackground(igc, carea.x, carea.y, ibounds.width, ibounds.height);
			}
		}

		if (isSeparatorVisible()) {
			// bg separator
			if (hasColor(IFormColors.H_BOTTOM_KEYLINE1)) {
				igc.setForeground(getColor(IFormColors.H_BOTTOM_KEYLINE1));
			} else {
				igc.setForeground(getBackground());
			}
			if (getSeparatorAlignment() == SWT.BOTTOM) {
				igc.drawLine(carea.x, carea.height - 2, carea.x + carea.width - 1, carea.height - 2);
			} else {
				igc.drawLine(carea.x, 1, carea.x + carea.width - 1, 1);
			}
			if (hasColor(IFormColors.H_BOTTOM_KEYLINE2)) {
				igc.setForeground(getColor(IFormColors.H_BOTTOM_KEYLINE2));
			} else {
				igc.setForeground(getForeground());
			}
			if (getSeparatorAlignment() == SWT.BOTTOM) {
				igc.drawLine(carea.x, carea.height - 1, carea.x + carea.width - 1, carea.height - 1);
			} else {
				igc.drawLine(carea.x, 0, carea.x + carea.width - 1, 0);
			}
		}
		igc.dispose();
		gc.drawImage(buffer, carea.x, carea.y);
		buffer.dispose();
	}

	private void updateGradientImage() {
		Rectangle rect = getBounds();
		if (gradientImage != null) {
			FormImages.getInstance().markFinished(gradientImage);
			gradientImage = null;
		}
		if (gradientInfo != null) {
			gradientImage = FormImages.getInstance().getGradient(gradientInfo.gradientColors, gradientInfo.percents,
					gradientInfo.vertical ? rect.height : rect.width, gradientInfo.vertical, getColor(COLOR_BASE_BG));
		} else if (backgroundImage != null && !isBackgroundImageTiled()) {
			gradientImage = new Image(getDisplay(), Math.max(rect.width, 1), Math.max(rect.height, 1));
			gradientImage.setBackground(getBackground());
			GC gc = new GC(gradientImage);
			gc.drawImage(backgroundImage, 0, 0);
			gc.dispose();
		}
		setBackgroundImage(gradientImage);
	}

	public boolean isSeparatorVisible() {
		return (flags & SEPARATOR) != 0;
	}

	public void setSeparatorVisible(boolean addSeparator) {
		if (addSeparator) {
			flags |= SEPARATOR;
		} else {
			flags &= ~SEPARATOR;
		}
	}

	public void setSeparatorAlignment(int alignment) {
		if (alignment == SWT.BOTTOM) {
			flags |= BOTTOM_SEPARATOR;
		} else {
			flags &= ~BOTTOM_SEPARATOR;
		}
	}

	public int getSeparatorAlignment() {
		return (flags & BOTTOM_SEPARATOR) != 0 ? SWT.BOTTOM : SWT.TOP;
	}

	@SuppressWarnings("unchecked")
	public void putColor(String key, Color color) {
		if (color == null) {
			colors.remove(key);
		} else {
			colors.put(key, color);
		}
	}

	public Color getColor(String key) {
		return (Color) colors.get(key);
	}

	public boolean hasColor(String key) {
		return colors.containsKey(key);
	}

}
