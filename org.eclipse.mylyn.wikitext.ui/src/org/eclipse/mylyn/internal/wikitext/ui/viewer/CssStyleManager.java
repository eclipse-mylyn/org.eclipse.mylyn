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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

// FIXME: move to internal

/**
 * 
 * 
 * @author David Green
 */
public class CssStyleManager {

	private static final int MIN_FONT_SIZE = 9;

	private static final int MAX_FONT_SIZE = 50;

	private static final Map<String, Integer> colorToRgb = new HashMap<String, Integer>();
	static {
		colorToRgb.put("AliceBlue".toLowerCase(), 0xF0F8FF);
		colorToRgb.put("AntiqueWhite".toLowerCase(), 0xFAEBD7);
		colorToRgb.put("Aqua".toLowerCase(), 0x00FFFF);
		colorToRgb.put("Aquamarine".toLowerCase(), 0x7FFFD4);
		colorToRgb.put("Azure".toLowerCase(), 0xF0FFFF);
		colorToRgb.put("Beige".toLowerCase(), 0xF5F5DC);
		colorToRgb.put("Bisque".toLowerCase(), 0xFFE4C4);
		colorToRgb.put("Black".toLowerCase(), 0x000000);
		colorToRgb.put("BlanchedAlmond".toLowerCase(), 0xFFEBCD);
		colorToRgb.put("Blue".toLowerCase(), 0x0000FF);
		colorToRgb.put("BlueViolet".toLowerCase(), 0x8A2BE2);
		colorToRgb.put("Brown".toLowerCase(), 0xA52A2A);
		colorToRgb.put("BurlyWood".toLowerCase(), 0xDEB887);
		colorToRgb.put("CadetBlue".toLowerCase(), 0x5F9EA0);
		colorToRgb.put("Chartreuse".toLowerCase(), 0x7FFF00);
		colorToRgb.put("Chocolate".toLowerCase(), 0xD2691E);
		colorToRgb.put("Coral".toLowerCase(), 0xFF7F50);
		colorToRgb.put("CornflowerBlue".toLowerCase(), 0x6495ED);
		colorToRgb.put("Cornsilk".toLowerCase(), 0xFFF8DC);
		colorToRgb.put("Crimson".toLowerCase(), 0xDC143C);
		colorToRgb.put("Cyan".toLowerCase(), 0x00FFFF);
		colorToRgb.put("DarkBlue".toLowerCase(), 0x00008B);
		colorToRgb.put("DarkCyan".toLowerCase(), 0x008B8B);
		colorToRgb.put("DarkGoldenRod".toLowerCase(), 0xB8860B);
		colorToRgb.put("DarkGray".toLowerCase(), 0xA9A9A9);
		colorToRgb.put("DarkGrey".toLowerCase(), 0xA9A9A9);
		colorToRgb.put("DarkGreen".toLowerCase(), 0x006400);
		colorToRgb.put("DarkKhaki".toLowerCase(), 0xBDB76B);
		colorToRgb.put("DarkMagenta".toLowerCase(), 0x8B008B);
		colorToRgb.put("DarkOliveGreen".toLowerCase(), 0x556B2F);
		colorToRgb.put("Darkorange".toLowerCase(), 0xFF8C00);
		colorToRgb.put("DarkOrchid".toLowerCase(), 0x9932CC);
		colorToRgb.put("DarkRed".toLowerCase(), 0x8B0000);
		colorToRgb.put("DarkSalmon".toLowerCase(), 0xE9967A);
		colorToRgb.put("DarkSeaGreen".toLowerCase(), 0x8FBC8F);
		colorToRgb.put("DarkSlateBlue".toLowerCase(), 0x483D8B);
		colorToRgb.put("DarkSlateGray".toLowerCase(), 0x2F4F4F);
		colorToRgb.put("DarkSlateGrey".toLowerCase(), 0x2F4F4F);
		colorToRgb.put("DarkTurquoise".toLowerCase(), 0x00CED1);
		colorToRgb.put("DarkViolet".toLowerCase(), 0x9400D3);
		colorToRgb.put("DeepPink".toLowerCase(), 0xFF1493);
		colorToRgb.put("DeepSkyBlue".toLowerCase(), 0x00BFFF);
		colorToRgb.put("DimGray".toLowerCase(), 0x696969);
		colorToRgb.put("DimGrey".toLowerCase(), 0x696969);
		colorToRgb.put("DodgerBlue".toLowerCase(), 0x1E90FF);
		colorToRgb.put("FireBrick".toLowerCase(), 0xB22222);
		colorToRgb.put("FloralWhite".toLowerCase(), 0xFFFAF0);
		colorToRgb.put("ForestGreen".toLowerCase(), 0x228B22);
		colorToRgb.put("Fuchsia".toLowerCase(), 0xFF00FF);
		colorToRgb.put("Gainsboro".toLowerCase(), 0xDCDCDC);
		colorToRgb.put("GhostWhite".toLowerCase(), 0xF8F8FF);
		colorToRgb.put("Gold".toLowerCase(), 0xFFD700);
		colorToRgb.put("GoldenRod".toLowerCase(), 0xDAA520);
		colorToRgb.put("Gray".toLowerCase(), 0x808080);
		colorToRgb.put("Grey".toLowerCase(), 0x808080);
		colorToRgb.put("Green".toLowerCase(), 0x008000);
		colorToRgb.put("GreenYellow".toLowerCase(), 0xADFF2F);
		colorToRgb.put("HoneyDew".toLowerCase(), 0xF0FFF0);
		colorToRgb.put("HotPink".toLowerCase(), 0xFF69B4);
		colorToRgb.put("IndianRed".toLowerCase(), 0xCD5C5C);
		colorToRgb.put("Indigo".toLowerCase(), 0x4B0082);
		colorToRgb.put("Ivory".toLowerCase(), 0xFFFFF0);
		colorToRgb.put("Khaki".toLowerCase(), 0xF0E68C);
		colorToRgb.put("Lavender".toLowerCase(), 0xE6E6FA);
		colorToRgb.put("LavenderBlush".toLowerCase(), 0xFFF0F5);
		colorToRgb.put("LawnGreen".toLowerCase(), 0x7CFC00);
		colorToRgb.put("LemonChiffon".toLowerCase(), 0xFFFACD);
		colorToRgb.put("LightBlue".toLowerCase(), 0xADD8E6);
		colorToRgb.put("LightCoral".toLowerCase(), 0xF08080);
		colorToRgb.put("LightCyan".toLowerCase(), 0xE0FFFF);
		colorToRgb.put("LightGoldenRodYellow".toLowerCase(), 0xFAFAD2);
		colorToRgb.put("LightGray".toLowerCase(), 0xD3D3D3);
		colorToRgb.put("LightGrey".toLowerCase(), 0xD3D3D3);
		colorToRgb.put("LightGreen".toLowerCase(), 0x90EE90);
		colorToRgb.put("LightPink".toLowerCase(), 0xFFB6C1);
		colorToRgb.put("LightSalmon".toLowerCase(), 0xFFA07A);
		colorToRgb.put("LightSeaGreen".toLowerCase(), 0x20B2AA);
		colorToRgb.put("LightSkyBlue".toLowerCase(), 0x87CEFA);
		colorToRgb.put("LightSlateGray".toLowerCase(), 0x778899);
		colorToRgb.put("LightSlateGrey".toLowerCase(), 0x778899);
		colorToRgb.put("LightSteelBlue".toLowerCase(), 0xB0C4DE);
		colorToRgb.put("LightYellow".toLowerCase(), 0xFFFFE0);
		colorToRgb.put("Lime".toLowerCase(), 0x00FF00);
		colorToRgb.put("LimeGreen".toLowerCase(), 0x32CD32);
		colorToRgb.put("Linen".toLowerCase(), 0xFAF0E6);
		colorToRgb.put("Magenta".toLowerCase(), 0xFF00FF);
		colorToRgb.put("Maroon".toLowerCase(), 0x800000);
		colorToRgb.put("MediumAquaMarine".toLowerCase(), 0x66CDAA);
		colorToRgb.put("MediumBlue".toLowerCase(), 0x0000CD);
		colorToRgb.put("MediumOrchid".toLowerCase(), 0xBA55D3);
		colorToRgb.put("MediumPurple".toLowerCase(), 0x9370D8);
		colorToRgb.put("MediumSeaGreen".toLowerCase(), 0x3CB371);
		colorToRgb.put("MediumSlateBlue".toLowerCase(), 0x7B68EE);
		colorToRgb.put("MediumSpringGreen".toLowerCase(), 0x00FA9A);
		colorToRgb.put("MediumTurquoise".toLowerCase(), 0x48D1CC);
		colorToRgb.put("MediumVioletRed".toLowerCase(), 0xC71585);
		colorToRgb.put("MidnightBlue".toLowerCase(), 0x191970);
		colorToRgb.put("MintCream".toLowerCase(), 0xF5FFFA);
		colorToRgb.put("MistyRose".toLowerCase(), 0xFFE4E1);
		colorToRgb.put("Moccasin".toLowerCase(), 0xFFE4B5);
		colorToRgb.put("NavajoWhite".toLowerCase(), 0xFFDEAD);
		colorToRgb.put("Navy".toLowerCase(), 0x000080);
		colorToRgb.put("OldLace".toLowerCase(), 0xFDF5E6);
		colorToRgb.put("Olive".toLowerCase(), 0x808000);
		colorToRgb.put("OliveDrab".toLowerCase(), 0x6B8E23);
		colorToRgb.put("Orange".toLowerCase(), 0xFFA500);
		colorToRgb.put("OrangeRed".toLowerCase(), 0xFF4500);
		colorToRgb.put("Orchid".toLowerCase(), 0xDA70D6);
		colorToRgb.put("PaleGoldenRod".toLowerCase(), 0xEEE8AA);
		colorToRgb.put("PaleGreen".toLowerCase(), 0x98FB98);
		colorToRgb.put("PaleTurquoise".toLowerCase(), 0xAFEEEE);
		colorToRgb.put("PaleVioletRed".toLowerCase(), 0xD87093);
		colorToRgb.put("PapayaWhip".toLowerCase(), 0xFFEFD5);
		colorToRgb.put("PeachPuff".toLowerCase(), 0xFFDAB9);
		colorToRgb.put("Peru".toLowerCase(), 0xCD853F);
		colorToRgb.put("Pink".toLowerCase(), 0xFFC0CB);
		colorToRgb.put("Plum".toLowerCase(), 0xDDA0DD);
		colorToRgb.put("PowderBlue".toLowerCase(), 0xB0E0E6);
		colorToRgb.put("Purple".toLowerCase(), 0x800080);
		colorToRgb.put("Red".toLowerCase(), 0xFF0000);
		colorToRgb.put("RosyBrown".toLowerCase(), 0xBC8F8F);
		colorToRgb.put("RoyalBlue".toLowerCase(), 0x4169E1);
		colorToRgb.put("SaddleBrown".toLowerCase(), 0x8B4513);
		colorToRgb.put("Salmon".toLowerCase(), 0xFA8072);
		colorToRgb.put("SandyBrown".toLowerCase(), 0xF4A460);
		colorToRgb.put("SeaGreen".toLowerCase(), 0x2E8B57);
		colorToRgb.put("SeaShell".toLowerCase(), 0xFFF5EE);
		colorToRgb.put("Sienna".toLowerCase(), 0xA0522D);
		colorToRgb.put("Silver".toLowerCase(), 0xC0C0C0);
		colorToRgb.put("SkyBlue".toLowerCase(), 0x87CEEB);
		colorToRgb.put("SlateBlue".toLowerCase(), 0x6A5ACD);
		colorToRgb.put("SlateGray".toLowerCase(), 0x708090);
		colorToRgb.put("SlateGrey".toLowerCase(), 0x708090);
		colorToRgb.put("Snow".toLowerCase(), 0xFFFAFA);
		colorToRgb.put("SpringGreen".toLowerCase(), 0x00FF7F);
		colorToRgb.put("SteelBlue".toLowerCase(), 0x4682B4);
		colorToRgb.put("Tan".toLowerCase(), 0xD2B48C);
		colorToRgb.put("Teal".toLowerCase(), 0x008080);
		colorToRgb.put("Thistle".toLowerCase(), 0xD8BFD8);
		colorToRgb.put("Tomato".toLowerCase(), 0xFF6347);
		colorToRgb.put("Turquoise".toLowerCase(), 0x40E0D0);
		colorToRgb.put("Violet".toLowerCase(), 0xEE82EE);
		colorToRgb.put("Wheat".toLowerCase(), 0xF5DEB3);
		colorToRgb.put("White".toLowerCase(), 0xFFFFFF);
		colorToRgb.put("WhiteSmoke".toLowerCase(), 0xF5F5F5);
		colorToRgb.put("Yellow".toLowerCase(), 0xFFFF00);
		colorToRgb.put("YellowGreen".toLowerCase(), 0x9ACD32);
	}

	static final Pattern cssRulePattern = Pattern.compile("(?:^|\\s?)([\\w-]+)\\s*:\\s*([^;]+)(;|$)", Pattern.MULTILINE
			| Pattern.DOTALL);

	static final Pattern rgbPattern = Pattern.compile("rgb\\((\\d+),(\\d+),(\\d+)\\)");

	private final Font defaultFont;

	public CssStyleManager(Font defaultFont) {
		if (defaultFont == null) {
			throw new IllegalArgumentException();
		}
		this.defaultFont = defaultFont;
	}

	/**
	 * For testing purposes only
	 */
	CssStyleManager() {
		defaultFont = null;
	}

	public StyleRange createStyleRange(FontState fontState, int offset, int length) {
		StyleRange styleRange = new StyleRange(offset, length, getColorFromRgb(fontState.foreground),
				getColorFromRgb(fontState.background));
		if (fontState.isBold()) {
			styleRange.fontStyle |= SWT.BOLD;
		}
		if (fontState.isUnderline()) {
			styleRange.underline = true;
		}
		if (fontState.isStrikethrough()) {
			styleRange.strikeout = true;
		}
		if (fontState.isItalic()) {
			styleRange.fontStyle |= SWT.ITALIC;
		}
		if (fontState.isSubscript()) {
			styleRange.rise = -4;
		} else if (fontState.isSuperscript()) {
			styleRange.rise = 4;
		}
		if (fontState.isFixedWidth()) {
			String symbolicName = getClass().getSimpleName() + "-monospace-" + fontState.size;
			Font monospaceFont = JFaceResources.getFontRegistry().get(symbolicName);
			if (monospaceFont == null) {
				Font defaultFont = JFaceResources.getFontRegistry().defaultFont();
				FontData[] fontData = defaultFont.getDevice().getFontList("Courier New", true);
				if (fontData == null || fontData.length == 0) {
					fontData = defaultFont.getDevice().getFontList("Courier", true);
				}
				if (fontData != null && fontData.length > 0) {
					for (FontData fd : fontData) {
						fd.setHeight((int) fontState.size);
					}
					JFaceResources.getFontRegistry().put(symbolicName, fontData);
					monospaceFont = JFaceResources.getFontRegistry().get(symbolicName);
				}
			}
			if (monospaceFont != null) {
				styleRange.font = monospaceFont;
			}
		} else {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			String defaultName = defaultFont.getFontData()[0].getName();
			if (fontState.size != defaultSize) {
				String symbolicName = defaultName + "-default-" + fontState.size;
				if (fontState.isBold()) {
					symbolicName = symbolicName + "-bold";
				}
				if (fontState.isItalic()) {
					symbolicName = symbolicName + "-italic";
				}
				Font font = JFaceResources.getFontRegistry().hasValueFor(symbolicName) ? JFaceResources.getFontRegistry()
						.get(symbolicName)
						: null;
				if (font == null) {
					FontData[] fontData = new FontData[defaultFont.getFontData().length];
					int index = -1;
					for (FontData fd : defaultFont.getFontData()) {
						FontData newData = new FontData(fd.getName(), fd.getHeight(), fd.getStyle());
						newData.setHeight((int) fontState.size);
						int style = newData.getStyle();
						if (fontState.isBold()) {
							style |= SWT.BOLD;
						}
						if (fontState.isItalic()) {
							style |= SWT.ITALIC;
						}
						newData.setStyle(style);
						fontData[++index] = newData;

					}
					JFaceResources.getFontRegistry().put(symbolicName, fontData);
					font = JFaceResources.getFontRegistry().get(symbolicName);
				}
				if (font != null) {
					styleRange.font = font;
				}
			}
		}
		return styleRange;
	}

	public Color getColorFromRgb(RGB rgb) {
		if (rgb == null) {
			return null;
		}
		String symbolicName = String.format("#%02X%02X%02X", rgb.red, rgb.green, rgb.blue);
		Color color = JFaceResources.getColorRegistry().get(symbolicName);
		if (color == null) {
			JFaceResources.getColorRegistry().put(symbolicName, rgb);
			color = JFaceResources.getColorRegistry().get(symbolicName);
		}
		return color;
	}

	public void processCssStyles(FontState fontState, FontState parentFontState, String styleValue) {
		if (styleValue == null) {
			return;
		}
		Matcher matcher = cssRulePattern.matcher(styleValue);
		while (matcher.find()) {
			String ruleName = matcher.group(1);
			String ruleValue = matcher.group(2);
			ruleValue = ruleValue.trim();

			if ("color".equals(ruleName)) {
				Integer rgb = cssColorRgb(ruleValue);
				if (rgb != null) {
					fontState.foreground = toRGB(rgb);
				}
			} else if ("background-color".equals(ruleName)) {
				Integer rgb = cssColorRgb(ruleValue);
				if (rgb != null) {
					fontState.background = toRGB(rgb);
				}
			} else if ("font-style".equals(ruleName)) {
				String[] parts = ruleValue.split("((\\s+)|(\\s*,\\s*))");
				for (String part : parts) {
					if ("italic".equals(part)) {
						fontState.setItalic(true);
					} else if ("bold".equals(part)) {
						fontState.setBold(true);
					} else if ("normal".equals(part)) {
						fontState.setItalic(false);
					}
				}
			} else if ("font-weight".equals(ruleName)) {
				if ("bold".equals(ruleValue) || "bolder".equals(ruleValue)) {
					fontState.setBold(true);
				} else if ("normal".equals(ruleValue) || "lighter".equals(ruleValue)) {
					fontState.setBold(false);
				}
			} else if ("font-size".equals(ruleName)) {
				updateFontSize(fontState, parentFontState, ruleValue);
			} else if ("font-family".equals(ruleName)) {
				String[] parts = ruleValue.split("((\\s+)|(\\s*,\\s*))");
				for (String part : parts) {
					if ("monospace".equals(part) || "courier".equalsIgnoreCase(part)
							|| "courier new".equalsIgnoreCase(part)) {
						fontState.setFixedWidth(true);
					} else {
						fontState.setFixedWidth(false);
					}
				}
			} else if ("text-decoration".equals(ruleName)) {
				String[] parts = ruleValue.split("((\\s+)|(\\s*,\\s*))");
				for (String part : parts) {
					if ("none".equals(part)) {
						fontState.setStrikethrough(false);
						fontState.setUnderline(false);
					} else if ("line-through".equals(part)) {
						fontState.setStrikethrough(true);
					} else if ("underline".equals(part)) {
						fontState.setUnderline(true);
					}
				}
			} else if ("vertical-align".equals(ruleName)) {
				if ("super".equals(ruleValue)) {
					fontState.setSuperscript(true);
				} else if ("sub".equals(ruleValue)) {
					fontState.setSubscript(true);
				}
			}
		}
	}

	private RGB toRGB(int rgb) {
		return new RGB((rgb & 0xFF0000) >> 16, (rgb & 0x00FF00) >> 8, (rgb & 0x0000FF));
	}

	private void updateFontSize(FontState fontState, FontState parentFontState, String cssFontSizeValue) {

		if (cssFontSizeValue.endsWith("%")) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			try {
				float percentage = Float.parseFloat(cssFontSizeValue.substring(0, cssFontSizeValue.length() - 1)) / 100f;
				if (percentage > 0) {
					fontState.size = percentage * defaultSize;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} else if ("xx-small".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize - (defaultSize * 0.6f);
		} else if ("x-small".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize - (defaultSize * 0.4f);
		} else if ("small".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize - (defaultSize * 0.2f);
		} else if ("medium".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize;
		} else if ("large".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize + (defaultSize * 0.2f);
		} else if ("x-large".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize + (defaultSize * 0.4f);
		} else if ("xx-large".equals(cssFontSizeValue)) {
			float defaultSize = defaultFont.getFontData()[0].getHeight();
			fontState.size = defaultSize + (defaultSize * 0.6f);
		} else if ("larger".equals(cssFontSizeValue)) {
			fontState.size = parentFontState.size * 1.2f;
		} else if ("smaller".equals(cssFontSizeValue)) {
			fontState.size = parentFontState.size - (parentFontState.size * 0.2f);
		} else {
			try {
				if (cssFontSizeValue.endsWith("pt") || cssFontSizeValue.endsWith("px")) {
					cssFontSizeValue = cssFontSizeValue.substring(0, cssFontSizeValue.length() - 2);
				}
				float exactSize = Float.parseFloat(cssFontSizeValue);
				if (exactSize > 0) {
					fontState.size = exactSize;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		// prevent the font size from being unusable
		fontState.size = Math.min(MAX_FONT_SIZE, Math.max(MIN_FONT_SIZE, fontState.size));
	}

	Integer cssColorRgb(String ruleValue) {
		Integer rgb = colorToRgb.get(ruleValue.toLowerCase());
		if (rgb == null) {
			try {
				if (ruleValue.startsWith("#")) {
					String rgbNumeric = ruleValue.substring(1);
					if (rgbNumeric.length() == 3) {
						String firstDigit = rgbNumeric.substring(0, 1);
						String secondDigit = rgbNumeric.substring(1, 2);
						String thirdDigit = rgbNumeric.substring(2, 3);
						rgbNumeric = firstDigit + firstDigit + secondDigit + secondDigit + thirdDigit + thirdDigit;
					}
					rgb = Integer.parseInt(rgbNumeric, 16);
				} else {
					Matcher rgbMatcher = rgbPattern.matcher(ruleValue);
					if (rgbMatcher.matches()) {
						String r = rgbMatcher.group(1);
						String g = rgbMatcher.group(2);
						String b = rgbMatcher.group(3);
						String rgbNumeric = String.format("%02X%02X%02X", Integer.parseInt(r), Integer.parseInt(g),
								Integer.parseInt(b));
						rgb = Integer.parseInt(rgbNumeric, 16);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return rgb;
	}

	public FontState createDefaultFontState() {
		FontState fontState = new FontState();
		fontState.size = defaultFont.getFontData()[0].getHeight();
		return fontState;
	}
}
