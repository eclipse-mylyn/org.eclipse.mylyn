/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Dec 29, 2004
 */
package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HighlighterImageDescriptor extends ImageDescriptor {

	private final Image image;

	public HighlighterImageDescriptor(Color fromColor, Color toColor) {
		super();
		if (fromColor == null) {
			fromColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		}
		if (toColor == null) {
			toColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		}
		ImageData band = createGradientBand(50, 20, false, new RGB(fromColor.getRed(), fromColor.getGreen(),
				fromColor.getBlue()), new RGB(toColor.getRed(), toColor.getGreen(), toColor.getBlue()), 7, 7, 7);
		image = new Image(Display.getCurrent(), band);
	}

	@Override
	public void destroyResource(Object previouslyCreatedObject) {
		image.dispose();
		super.destroyResource(previouslyCreatedObject);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof HighlighterImageDescriptor && image.equals(((HighlighterImageDescriptor) obj).image);
	}

	@Override
	public ImageData getImageData() {
		return image.getImageData();
	}

	@Override
	public int hashCode() {
		return image.hashCode();
	}

	public Image getImage() {
		return image;
	}

	// ----------- COPIED FROM ImageData ---------------

	/**
	 * Creates an ImageData containing one band's worth of a gradient filled block. If <code>vertical</code> is true,
	 * the band must be tiled horizontally to fill a region, otherwise it must be tiled vertically.
	 * 
	 * @param width
	 *            the width of the region to be filled
	 * @param height
	 *            the height of the region to be filled
	 * @param vertical
	 *            if true sweeps from top to bottom, else sweeps from left to right
	 * @param fromRGB
	 *            the color to start with
	 * @param toRGB
	 *            the color to end with
	 * @param redBits
	 *            the number of significant red bits, 0 for palette modes
	 * @param greenBits
	 *            the number of significant green bits, 0 for palette modes
	 * @param blueBits
	 *            the number of significant blue bits, 0 for palette modes
	 * @return the new ImageData
	 */
	static ImageData createGradientBand(int width, int height, boolean vertical, RGB fromRGB, RGB toRGB, int redBits,
			int greenBits, int blueBits) {
		/* Gradients are drawn as tiled bands */
		final int bandWidth, bandHeight, bitmapDepth;
		final byte[] bitmapData;
		final PaletteData paletteData;
		/* Select an algorithm depending on the depth of the screen */
		if (redBits != 0 && greenBits != 0 && blueBits != 0) {
			paletteData = new PaletteData(0x0000ff00, 0x00ff0000, 0xff000000);
			bitmapDepth = 32;
			if (redBits >= 8 && greenBits >= 8 && blueBits >= 8) {
				/* Precise color */
				final int steps;
				if (vertical) {
					bandWidth = 1;
					bandHeight = height;
					steps = bandHeight > 1 ? bandHeight - 1 : 1;
				} else {
					bandWidth = width;
					bandHeight = 1;
					steps = bandWidth > 1 ? bandWidth - 1 : 1;
				}
				final int bytesPerLine = bandWidth * 4;
				bitmapData = new byte[bandHeight * bytesPerLine];
				buildPreciseGradientChannel(fromRGB.blue, toRGB.blue, steps, bandWidth, bandHeight, vertical,
						bitmapData, 0, bytesPerLine);
				buildPreciseGradientChannel(fromRGB.green, toRGB.green, steps, bandWidth, bandHeight, vertical,
						bitmapData, 1, bytesPerLine);
				buildPreciseGradientChannel(fromRGB.red, toRGB.red, steps, bandWidth, bandHeight, vertical, bitmapData,
						2, bytesPerLine);
			} else {
				/* Dithered color */
				final int steps;
				if (vertical) {
					bandWidth = (width < 8) ? width : 8;
					bandHeight = height;
					steps = bandHeight > 1 ? bandHeight - 1 : 1;
				} else {
					bandWidth = width;
					bandHeight = (height < 8) ? height : 8;
					steps = bandWidth > 1 ? bandWidth - 1 : 1;
				}
				final int bytesPerLine = bandWidth * 4;
				bitmapData = new byte[bandHeight * bytesPerLine];
				buildDitheredGradientChannel(fromRGB.blue, toRGB.blue, steps, bandWidth, bandHeight, vertical,
						bitmapData, 0, bytesPerLine, blueBits);
				buildDitheredGradientChannel(fromRGB.green, toRGB.green, steps, bandWidth, bandHeight, vertical,
						bitmapData, 1, bytesPerLine, greenBits);
				buildDitheredGradientChannel(fromRGB.red, toRGB.red, steps, bandWidth, bandHeight, vertical,
						bitmapData, 2, bytesPerLine, redBits);
			}
		} else {
			/* Dithered two tone */
			paletteData = new PaletteData(new RGB[] { fromRGB, toRGB });
			bitmapDepth = 8;
			final int blendi;
			if (vertical) {
				bandWidth = (width < 8) ? width : 8;
				bandHeight = height;
				blendi = (bandHeight > 1) ? 0x1040000 / (bandHeight - 1) + 1 : 1;
			} else {
				bandWidth = width;
				bandHeight = (height < 8) ? height : 8;
				blendi = (bandWidth > 1) ? 0x1040000 / (bandWidth - 1) + 1 : 1;
			}
			final int bytesPerLine = (bandWidth + 3) & -4;
			bitmapData = new byte[bandHeight * bytesPerLine];
			if (vertical) {
				for (int dy = 0, blend = 0, dp = 0; dy < bandHeight; ++dy, blend += blendi, dp += bytesPerLine) {
					for (int dx = 0; dx < bandWidth; ++dx) {
						bitmapData[dp + dx] = (blend + DITHER_MATRIX[dy & 7][dx]) < 0x1000000 ? (byte) 0 : (byte) 1;
					}
				}
			} else {
				for (int dx = 0, blend = 0; dx < bandWidth; ++dx, blend += blendi) {
					for (int dy = 0, dptr = dx; dy < bandHeight; ++dy, dptr += bytesPerLine) {
						bitmapData[dptr] = (blend + DITHER_MATRIX[dy][dx & 7]) < 0x1000000 ? (byte) 0 : (byte) 1;
					}
				}
			}
		}
		return new ImageData(bandWidth, bandHeight, bitmapDepth, paletteData, 4, bitmapData);
	}

	/*
	 * Fill in dithered gradated values for a color channel
	 */
	static final void buildDitheredGradientChannel(int from, int to, int steps, int bandWidth, int bandHeight,
			boolean vertical, byte[] bitmapData, int dp, int bytesPerLine, int bits) {
		final int mask = 0xff00 >>> bits;
		int val = from << 16;
		final int inc = ((to << 16) - val) / steps + 1;
		if (vertical) {
			for (int dy = 0; dy < bandHeight; ++dy, dp += bytesPerLine) {
				for (int dx = 0, dptr = dp; dx < bandWidth; ++dx, dptr += 4) {
					final int thresh = DITHER_MATRIX[dy & 7][dx] >>> bits;
					int temp = val + thresh;
					if (temp > 0xffffff) {
						bitmapData[dptr] = -1;
					} else {
						bitmapData[dptr] = (byte) ((temp >>> 16) & mask);
					}
				}
				val += inc;
			}
		} else {
			for (int dx = 0; dx < bandWidth; ++dx, dp += 4) {
				for (int dy = 0, dptr = dp; dy < bandHeight; ++dy, dptr += bytesPerLine) {
					final int thresh = DITHER_MATRIX[dy][dx & 7] >>> bits;
					int temp = val + thresh;
					if (temp > 0xffffff) {
						bitmapData[dptr] = -1;
					} else {
						bitmapData[dptr] = (byte) ((temp >>> 16) & mask);
					}
				}
				val += inc;
			}
		}
	}

	/*
	 * Fill in gradated values for a color channel
	 */
	static final void buildPreciseGradientChannel(int from, int to, int steps, int bandWidth, int bandHeight,
			boolean vertical, byte[] bitmapData, int dp, int bytesPerLine) {
		int val = from << 16;
		final int inc = ((to << 16) - val) / steps + 1;
		if (vertical) {
			for (int dy = 0; dy < bandHeight; ++dy, dp += bytesPerLine) {
				bitmapData[dp] = (byte) (val >>> 16);
				val += inc;
			}
		} else {
			for (int dx = 0; dx < bandWidth; ++dx, dp += 4) {
				bitmapData[dp] = (byte) (val >>> 16);
				val += inc;
			}
		}
	}

	/**
	 * Scaled 8x8 Bayer dither matrix.
	 */
	static final int[][] DITHER_MATRIX = {
			{ 0xfc0000, 0x7c0000, 0xdc0000, 0x5c0000, 0xf40000, 0x740000, 0xd40000, 0x540000 },
			{ 0x3c0000, 0xbc0000, 0x1c0000, 0x9c0000, 0x340000, 0xb40000, 0x140000, 0x940000 },
			{ 0xcc0000, 0x4c0000, 0xec0000, 0x6c0000, 0xc40000, 0x440000, 0xe40000, 0x640000 },
			{ 0x0c0000, 0x8c0000, 0x2c0000, 0xac0000, 0x040000, 0x840000, 0x240000, 0xa40000 },
			{ 0xf00000, 0x700000, 0xd00000, 0x500000, 0xf80000, 0x780000, 0xd80000, 0x580000 },
			{ 0x300000, 0xb00000, 0x100000, 0x900000, 0x380000, 0xb80000, 0x180000, 0x980000 },
			{ 0xc00000, 0x400000, 0xe00000, 0x600000, 0xc80000, 0x480000, 0xe80000, 0x680000 },
			{ 0x000000, 0x800000, 0x200000, 0xa00000, 0x080000, 0x880000, 0x280000, 0xa80000 } };
}
