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
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.wikitext.ui.util.css.CssRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * Interprets CSS styles and applies them to a default font and a {@link FontState}, creating a {@link StyleRange}.
 * 
 * @author David Green
 */
public class CssStyleManager {

	public static final String RULE_VERTICAL_ALIGN = "vertical-align"; //$NON-NLS-1$

	public static final String RULE_TEXT_DECORATION = "text-decoration"; //$NON-NLS-1$

	public static final String RULE_FONT_FAMILY = "font-family"; //$NON-NLS-1$

	public static final String RULE_FONT_SIZE = "font-size"; //$NON-NLS-1$

	public static final String RULE_FONT_WEIGHT = "font-weight"; //$NON-NLS-1$

	public static final String RULE_FONT_STYLE = "font-style"; //$NON-NLS-1$

	public static final String RULE_BACKGROUND_COLOR = "background-color"; //$NON-NLS-1$

	public static final String RULE_COLOR = "color"; //$NON-NLS-1$

	private static final int MIN_FONT_SIZE = 9;

	private static final int MAX_FONT_SIZE = 50;

	private static final Map<String, Integer> colorToRgb = new HashMap<String, Integer>();
	static {
		colorToRgb.put("AliceBlue".toLowerCase(), 0xF0F8FF); //$NON-NLS-1$
		colorToRgb.put("AntiqueWhite".toLowerCase(), 0xFAEBD7); //$NON-NLS-1$
		colorToRgb.put("Aqua".toLowerCase(), 0x00FFFF); //$NON-NLS-1$
		colorToRgb.put("Aquamarine".toLowerCase(), 0x7FFFD4); //$NON-NLS-1$
		colorToRgb.put("Azure".toLowerCase(), 0xF0FFFF); //$NON-NLS-1$
		colorToRgb.put("Beige".toLowerCase(), 0xF5F5DC); //$NON-NLS-1$
		colorToRgb.put("Bisque".toLowerCase(), 0xFFE4C4); //$NON-NLS-1$
		colorToRgb.put("Black".toLowerCase(), 0x000000); //$NON-NLS-1$
		colorToRgb.put("BlanchedAlmond".toLowerCase(), 0xFFEBCD); //$NON-NLS-1$
		colorToRgb.put("Blue".toLowerCase(), 0x0000FF); //$NON-NLS-1$
		colorToRgb.put("BlueViolet".toLowerCase(), 0x8A2BE2); //$NON-NLS-1$
		colorToRgb.put("Brown".toLowerCase(), 0xA52A2A); //$NON-NLS-1$
		colorToRgb.put("BurlyWood".toLowerCase(), 0xDEB887); //$NON-NLS-1$
		colorToRgb.put("CadetBlue".toLowerCase(), 0x5F9EA0); //$NON-NLS-1$
		colorToRgb.put("Chartreuse".toLowerCase(), 0x7FFF00); //$NON-NLS-1$
		colorToRgb.put("Chocolate".toLowerCase(), 0xD2691E); //$NON-NLS-1$
		colorToRgb.put("Coral".toLowerCase(), 0xFF7F50); //$NON-NLS-1$
		colorToRgb.put("CornflowerBlue".toLowerCase(), 0x6495ED); //$NON-NLS-1$
		colorToRgb.put("Cornsilk".toLowerCase(), 0xFFF8DC); //$NON-NLS-1$
		colorToRgb.put("Crimson".toLowerCase(), 0xDC143C); //$NON-NLS-1$
		colorToRgb.put("Cyan".toLowerCase(), 0x00FFFF); //$NON-NLS-1$
		colorToRgb.put("DarkBlue".toLowerCase(), 0x00008B); //$NON-NLS-1$
		colorToRgb.put("DarkCyan".toLowerCase(), 0x008B8B); //$NON-NLS-1$
		colorToRgb.put("DarkGoldenRod".toLowerCase(), 0xB8860B); //$NON-NLS-1$
		colorToRgb.put("DarkGray".toLowerCase(), 0xA9A9A9); //$NON-NLS-1$
		colorToRgb.put("DarkGrey".toLowerCase(), 0xA9A9A9); //$NON-NLS-1$
		colorToRgb.put("DarkGreen".toLowerCase(), 0x006400); //$NON-NLS-1$
		colorToRgb.put("DarkKhaki".toLowerCase(), 0xBDB76B); //$NON-NLS-1$
		colorToRgb.put("DarkMagenta".toLowerCase(), 0x8B008B); //$NON-NLS-1$
		colorToRgb.put("DarkOliveGreen".toLowerCase(), 0x556B2F); //$NON-NLS-1$
		colorToRgb.put("Darkorange".toLowerCase(), 0xFF8C00); //$NON-NLS-1$
		colorToRgb.put("DarkOrchid".toLowerCase(), 0x9932CC); //$NON-NLS-1$
		colorToRgb.put("DarkRed".toLowerCase(), 0x8B0000); //$NON-NLS-1$
		colorToRgb.put("DarkSalmon".toLowerCase(), 0xE9967A); //$NON-NLS-1$
		colorToRgb.put("DarkSeaGreen".toLowerCase(), 0x8FBC8F); //$NON-NLS-1$
		colorToRgb.put("DarkSlateBlue".toLowerCase(), 0x483D8B); //$NON-NLS-1$
		colorToRgb.put("DarkSlateGray".toLowerCase(), 0x2F4F4F); //$NON-NLS-1$
		colorToRgb.put("DarkSlateGrey".toLowerCase(), 0x2F4F4F); //$NON-NLS-1$
		colorToRgb.put("DarkTurquoise".toLowerCase(), 0x00CED1); //$NON-NLS-1$
		colorToRgb.put("DarkViolet".toLowerCase(), 0x9400D3); //$NON-NLS-1$
		colorToRgb.put("DeepPink".toLowerCase(), 0xFF1493); //$NON-NLS-1$
		colorToRgb.put("DeepSkyBlue".toLowerCase(), 0x00BFFF); //$NON-NLS-1$
		colorToRgb.put("DimGray".toLowerCase(), 0x696969); //$NON-NLS-1$
		colorToRgb.put("DimGrey".toLowerCase(), 0x696969); //$NON-NLS-1$
		colorToRgb.put("DodgerBlue".toLowerCase(), 0x1E90FF); //$NON-NLS-1$
		colorToRgb.put("FireBrick".toLowerCase(), 0xB22222); //$NON-NLS-1$
		colorToRgb.put("FloralWhite".toLowerCase(), 0xFFFAF0); //$NON-NLS-1$
		colorToRgb.put("ForestGreen".toLowerCase(), 0x228B22); //$NON-NLS-1$
		colorToRgb.put("Fuchsia".toLowerCase(), 0xFF00FF); //$NON-NLS-1$
		colorToRgb.put("Gainsboro".toLowerCase(), 0xDCDCDC); //$NON-NLS-1$
		colorToRgb.put("GhostWhite".toLowerCase(), 0xF8F8FF); //$NON-NLS-1$
		colorToRgb.put("Gold".toLowerCase(), 0xFFD700); //$NON-NLS-1$
		colorToRgb.put("GoldenRod".toLowerCase(), 0xDAA520); //$NON-NLS-1$
		colorToRgb.put("Gray".toLowerCase(), 0x808080); //$NON-NLS-1$
		colorToRgb.put("Grey".toLowerCase(), 0x808080); //$NON-NLS-1$
		colorToRgb.put("Green".toLowerCase(), 0x008000); //$NON-NLS-1$
		colorToRgb.put("GreenYellow".toLowerCase(), 0xADFF2F); //$NON-NLS-1$
		colorToRgb.put("HoneyDew".toLowerCase(), 0xF0FFF0); //$NON-NLS-1$
		colorToRgb.put("HotPink".toLowerCase(), 0xFF69B4); //$NON-NLS-1$
		colorToRgb.put("IndianRed".toLowerCase(), 0xCD5C5C); //$NON-NLS-1$
		colorToRgb.put("Indigo".toLowerCase(), 0x4B0082); //$NON-NLS-1$
		colorToRgb.put("Ivory".toLowerCase(), 0xFFFFF0); //$NON-NLS-1$
		colorToRgb.put("Khaki".toLowerCase(), 0xF0E68C); //$NON-NLS-1$
		colorToRgb.put("Lavender".toLowerCase(), 0xE6E6FA); //$NON-NLS-1$
		colorToRgb.put("LavenderBlush".toLowerCase(), 0xFFF0F5); //$NON-NLS-1$
		colorToRgb.put("LawnGreen".toLowerCase(), 0x7CFC00); //$NON-NLS-1$
		colorToRgb.put("LemonChiffon".toLowerCase(), 0xFFFACD); //$NON-NLS-1$
		colorToRgb.put("LightBlue".toLowerCase(), 0xADD8E6); //$NON-NLS-1$
		colorToRgb.put("LightCoral".toLowerCase(), 0xF08080); //$NON-NLS-1$
		colorToRgb.put("LightCyan".toLowerCase(), 0xE0FFFF); //$NON-NLS-1$
		colorToRgb.put("LightGoldenRodYellow".toLowerCase(), 0xFAFAD2); //$NON-NLS-1$
		colorToRgb.put("LightGray".toLowerCase(), 0xD3D3D3); //$NON-NLS-1$
		colorToRgb.put("LightGrey".toLowerCase(), 0xD3D3D3); //$NON-NLS-1$
		colorToRgb.put("LightGreen".toLowerCase(), 0x90EE90); //$NON-NLS-1$
		colorToRgb.put("LightPink".toLowerCase(), 0xFFB6C1); //$NON-NLS-1$
		colorToRgb.put("LightSalmon".toLowerCase(), 0xFFA07A); //$NON-NLS-1$
		colorToRgb.put("LightSeaGreen".toLowerCase(), 0x20B2AA); //$NON-NLS-1$
		colorToRgb.put("LightSkyBlue".toLowerCase(), 0x87CEFA); //$NON-NLS-1$
		colorToRgb.put("LightSlateGray".toLowerCase(), 0x778899); //$NON-NLS-1$
		colorToRgb.put("LightSlateGrey".toLowerCase(), 0x778899); //$NON-NLS-1$
		colorToRgb.put("LightSteelBlue".toLowerCase(), 0xB0C4DE); //$NON-NLS-1$
		colorToRgb.put("LightYellow".toLowerCase(), 0xFFFFE0); //$NON-NLS-1$
		colorToRgb.put("Lime".toLowerCase(), 0x00FF00); //$NON-NLS-1$
		colorToRgb.put("LimeGreen".toLowerCase(), 0x32CD32); //$NON-NLS-1$
		colorToRgb.put("Linen".toLowerCase(), 0xFAF0E6); //$NON-NLS-1$
		colorToRgb.put("Magenta".toLowerCase(), 0xFF00FF); //$NON-NLS-1$
		colorToRgb.put("Maroon".toLowerCase(), 0x800000); //$NON-NLS-1$
		colorToRgb.put("MediumAquaMarine".toLowerCase(), 0x66CDAA); //$NON-NLS-1$
		colorToRgb.put("MediumBlue".toLowerCase(), 0x0000CD); //$NON-NLS-1$
		colorToRgb.put("MediumOrchid".toLowerCase(), 0xBA55D3); //$NON-NLS-1$
		colorToRgb.put("MediumPurple".toLowerCase(), 0x9370D8); //$NON-NLS-1$
		colorToRgb.put("MediumSeaGreen".toLowerCase(), 0x3CB371); //$NON-NLS-1$
		colorToRgb.put("MediumSlateBlue".toLowerCase(), 0x7B68EE); //$NON-NLS-1$
		colorToRgb.put("MediumSpringGreen".toLowerCase(), 0x00FA9A); //$NON-NLS-1$
		colorToRgb.put("MediumTurquoise".toLowerCase(), 0x48D1CC); //$NON-NLS-1$
		colorToRgb.put("MediumVioletRed".toLowerCase(), 0xC71585); //$NON-NLS-1$
		colorToRgb.put("MidnightBlue".toLowerCase(), 0x191970); //$NON-NLS-1$
		colorToRgb.put("MintCream".toLowerCase(), 0xF5FFFA); //$NON-NLS-1$
		colorToRgb.put("MistyRose".toLowerCase(), 0xFFE4E1); //$NON-NLS-1$
		colorToRgb.put("Moccasin".toLowerCase(), 0xFFE4B5); //$NON-NLS-1$
		colorToRgb.put("NavajoWhite".toLowerCase(), 0xFFDEAD); //$NON-NLS-1$
		colorToRgb.put("Navy".toLowerCase(), 0x000080); //$NON-NLS-1$
		colorToRgb.put("OldLace".toLowerCase(), 0xFDF5E6); //$NON-NLS-1$
		colorToRgb.put("Olive".toLowerCase(), 0x808000); //$NON-NLS-1$
		colorToRgb.put("OliveDrab".toLowerCase(), 0x6B8E23); //$NON-NLS-1$
		colorToRgb.put("Orange".toLowerCase(), 0xFFA500); //$NON-NLS-1$
		colorToRgb.put("OrangeRed".toLowerCase(), 0xFF4500); //$NON-NLS-1$
		colorToRgb.put("Orchid".toLowerCase(), 0xDA70D6); //$NON-NLS-1$
		colorToRgb.put("PaleGoldenRod".toLowerCase(), 0xEEE8AA); //$NON-NLS-1$
		colorToRgb.put("PaleGreen".toLowerCase(), 0x98FB98); //$NON-NLS-1$
		colorToRgb.put("PaleTurquoise".toLowerCase(), 0xAFEEEE); //$NON-NLS-1$
		colorToRgb.put("PaleVioletRed".toLowerCase(), 0xD87093); //$NON-NLS-1$
		colorToRgb.put("PapayaWhip".toLowerCase(), 0xFFEFD5); //$NON-NLS-1$
		colorToRgb.put("PeachPuff".toLowerCase(), 0xFFDAB9); //$NON-NLS-1$
		colorToRgb.put("Peru".toLowerCase(), 0xCD853F); //$NON-NLS-1$
		colorToRgb.put("Pink".toLowerCase(), 0xFFC0CB); //$NON-NLS-1$
		colorToRgb.put("Plum".toLowerCase(), 0xDDA0DD); //$NON-NLS-1$
		colorToRgb.put("PowderBlue".toLowerCase(), 0xB0E0E6); //$NON-NLS-1$
		colorToRgb.put("Purple".toLowerCase(), 0x800080); //$NON-NLS-1$
		colorToRgb.put("Red".toLowerCase(), 0xFF0000); //$NON-NLS-1$
		colorToRgb.put("RosyBrown".toLowerCase(), 0xBC8F8F); //$NON-NLS-1$
		colorToRgb.put("RoyalBlue".toLowerCase(), 0x4169E1); //$NON-NLS-1$
		colorToRgb.put("SaddleBrown".toLowerCase(), 0x8B4513); //$NON-NLS-1$
		colorToRgb.put("Salmon".toLowerCase(), 0xFA8072); //$NON-NLS-1$
		colorToRgb.put("SandyBrown".toLowerCase(), 0xF4A460); //$NON-NLS-1$
		colorToRgb.put("SeaGreen".toLowerCase(), 0x2E8B57); //$NON-NLS-1$
		colorToRgb.put("SeaShell".toLowerCase(), 0xFFF5EE); //$NON-NLS-1$
		colorToRgb.put("Sienna".toLowerCase(), 0xA0522D); //$NON-NLS-1$
		colorToRgb.put("Silver".toLowerCase(), 0xC0C0C0); //$NON-NLS-1$
		colorToRgb.put("SkyBlue".toLowerCase(), 0x87CEEB); //$NON-NLS-1$
		colorToRgb.put("SlateBlue".toLowerCase(), 0x6A5ACD); //$NON-NLS-1$
		colorToRgb.put("SlateGray".toLowerCase(), 0x708090); //$NON-NLS-1$
		colorToRgb.put("SlateGrey".toLowerCase(), 0x708090); //$NON-NLS-1$
		colorToRgb.put("Snow".toLowerCase(), 0xFFFAFA); //$NON-NLS-1$
		colorToRgb.put("SpringGreen".toLowerCase(), 0x00FF7F); //$NON-NLS-1$
		colorToRgb.put("SteelBlue".toLowerCase(), 0x4682B4); //$NON-NLS-1$
		colorToRgb.put("Tan".toLowerCase(), 0xD2B48C); //$NON-NLS-1$
		colorToRgb.put("Teal".toLowerCase(), 0x008080); //$NON-NLS-1$
		colorToRgb.put("Thistle".toLowerCase(), 0xD8BFD8); //$NON-NLS-1$
		colorToRgb.put("Tomato".toLowerCase(), 0xFF6347); //$NON-NLS-1$
		colorToRgb.put("Turquoise".toLowerCase(), 0x40E0D0); //$NON-NLS-1$
		colorToRgb.put("Violet".toLowerCase(), 0xEE82EE); //$NON-NLS-1$
		colorToRgb.put("Wheat".toLowerCase(), 0xF5DEB3); //$NON-NLS-1$
		colorToRgb.put("White".toLowerCase(), 0xFFFFFF); //$NON-NLS-1$
		colorToRgb.put("WhiteSmoke".toLowerCase(), 0xF5F5F5); //$NON-NLS-1$
		colorToRgb.put("Yellow".toLowerCase(), 0xFFFF00); //$NON-NLS-1$
		colorToRgb.put("YellowGreen".toLowerCase(), 0x9ACD32); //$NON-NLS-1$
	}

	static final Pattern rgbPattern = Pattern.compile("rgb\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)"); //$NON-NLS-1$

	private final Font defaultFont;

	private final Font defaultMonospaceFont;

	private final Map<String, RuleHandler> ruleNameToHandler = new HashMap<String, RuleHandler>();
	{
		register(new ColorRuleHandler());
		register(new BackgroundColorRuleHandler());
		register(new FontStyleRuleHandler());
		register(new FontWeightRuleHandler());
		register(new FontSizeRuleHandler());
		register(new FontFamilyRuleHandler());
		register(new TextDecorationRuleHandler());
		register(new VerticalAlignRuleHandler());
	}

	public CssStyleManager(Font defaultFont) {
		this(defaultFont, null);
	}

	/**
	 * 
	 * @param defaultFont
	 *            the default font to use
	 * @param defaultMonospaceFont
	 *            the default font to use where a monospace font is required
	 */
	public CssStyleManager(Font defaultFont, Font defaultMonospaceFont) {
		if (defaultFont == null) {
			throw new IllegalArgumentException();
		}
		this.defaultFont = defaultFont;
		this.defaultMonospaceFont = defaultMonospaceFont;
	}

	/**
	 * NOT API For testing purposes only
	 */
	public CssStyleManager() {
		defaultFont = null;
		defaultMonospaceFont = null;
	}

	private void register(RuleHandler handler) {
		ruleNameToHandler.put(handler.getRuleName(), handler);
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
			String symbolicName = computeSymbolicName(fontState, "monospace", defaultMonospaceFont); //$NON-NLS-1$
			Font monospaceFont = JFaceResources.getFontRegistry().hasValueFor(symbolicName) ? JFaceResources.getFontRegistry()
					.get(symbolicName)
					: null;
			if (monospaceFont == null) {
				FontData[] fontData = null;
				if (defaultMonospaceFont != null) {
					fontData = defaultMonospaceFont.getFontData();
				} else {
					Font defaultFont = JFaceResources.getFontRegistry().defaultFont();
					// look for a monospace font.  First look for non-scalable fonts (bug 263074 comment 3 to comment 6)
					// then scalable fonts.  This addresses platform-specific issues.
					String os = Platform.getOS();
					if (Platform.OS_LINUX.equals(os)) {
						fontData = defaultFont.getDevice().getFontList("monospace", false); //$NON-NLS-1$
						if (fontData == null) {
							fontData = defaultFont.getDevice().getFontList("monospace", true); //$NON-NLS-1$	
						}
					}
					if (fontData == null) {
						fontData = defaultFont.getDevice().getFontList("Courier New", false); //$NON-NLS-1$
						if (fontData == null || fontData.length == 0) {
							fontData = defaultFont.getDevice().getFontList("Courier", false); //$NON-NLS-1$
							if (fontData == null || fontData.length == 0) {
								fontData = defaultFont.getDevice().getFontList("Courier New", true); //$NON-NLS-1$
								if (fontData == null || fontData.length == 0) {
									fontData = defaultFont.getDevice().getFontList("Courier", true); //$NON-NLS-1$
								}
							}
						}
					}
				}
				if (fontData != null && fontData.length > 0) {
					fontData = applyFontState(fontState, fontData);
					JFaceResources.getFontRegistry().put(symbolicName, fontData);
					monospaceFont = JFaceResources.getFontRegistry().get(symbolicName);
				}
			}
			if (monospaceFont != null) {
				styleRange.font = monospaceFont;
			}
		} else {
			String symbolicName = computeSymbolicName(fontState, "default", defaultFont); //$NON-NLS-1$
			Font font = JFaceResources.getFontRegistry().hasValueFor(symbolicName) ? JFaceResources.getFontRegistry()
					.get(symbolicName) : null;
			if (font == null) {
				FontData[] fontData = createFontData(fontState, defaultFont);
				JFaceResources.getFontRegistry().put(symbolicName, fontData);
				font = JFaceResources.getFontRegistry().get(symbolicName);
			}
			if (font != null) {
				styleRange.font = font;
			}

		}
		return styleRange;
	}

	private FontData[] applyFontState(FontState fontState, FontData[] fontData) {
		final int fontSize = (int) fontState.size;
		boolean bold = fontState.isBold();
		boolean italics = fontState.isItalic();

		for (FontData data : fontData) {
			data.setHeight(fontSize);
			int style = data.getStyle();
			if (bold) {
				style |= SWT.BOLD;
			}
			if (italics) {
				style |= SWT.ITALIC;
			}
			data.setStyle(style);
		}
		return fontData;
	}

	private FontData[] createFontData(FontState fontState, Font baseFont) {
		FontData[] fontData = new FontData[baseFont.getFontData().length];
		int index = -1;
		for (FontData fd : baseFont.getFontData()) {
			fontData[++index] = new FontData(fd.getName(), fd.getHeight(), fd.getStyle());
		}
		return applyFontState(fontState, fontData);
	}

	private String computeSymbolicName(FontState fontState, String key, Font defaultFont) {
		String symbolicName = getClass().getSimpleName() + '-' + key + '-' + fontState.size;
		if (fontState.isBold()) {
			symbolicName += "-bold"; //$NON-NLS-1$
		}
		if (fontState.isItalic()) {
			symbolicName += "-italic"; //$NON-NLS-1$
		}
		// if the default has changed we must change the key so that changes can actually take effect
		if (defaultFont != null) {
			FontData fontData = defaultFont.getFontData()[0];
			symbolicName += '-' + fontData.getName() + '-' + fontData.getStyle();
		}
		return symbolicName;
	}

	public Color getColorFromRgb(RGB rgb) {
		if (rgb == null) {
			return null;
		}
		String symbolicName = String.format("#%02X%02X%02X", rgb.red, rgb.green, rgb.blue); //$NON-NLS-1$
		Color color = JFaceResources.getColorRegistry().get(symbolicName);
		if (color == null) {
			JFaceResources.getColorRegistry().put(symbolicName, rgb);
			color = JFaceResources.getColorRegistry().get(symbolicName);
		}
		return color;
	}

	public SortedSet<String> getRecognizedRuleNames() {
		return new TreeSet<String>(ruleNameToHandler.keySet());
	}

	public boolean isKnownRule(CssRule rule) {
		return ruleNameToHandler.containsKey(rule.name);
	}

	private interface RuleHandler {
		public String getRuleName();

		public void process(CssRule rule, FontState fontState, FontState parentFontState);
	}

	private static class ColorRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_COLOR;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			Integer rgb = cssColorRgb(rule.value);
			if (rgb != null) {
				fontState.foreground = toRGB(rgb);
			}
		}
	}

	private static class BackgroundColorRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_BACKGROUND_COLOR;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			Integer rgb = cssColorRgb(rule.value);
			if (rgb != null) {
				fontState.background = toRGB(rgb);
			}
		}
	}

	private static class FontStyleRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_FONT_STYLE;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			String[] parts = rule.value.split("((\\s+)|(\\s*,\\s*))"); //$NON-NLS-1$
			for (String part : parts) {
				if ("italic".equals(part)) { //$NON-NLS-1$
					fontState.setItalic(true);
				} else if ("bold".equals(part)) { //$NON-NLS-1$
					fontState.setBold(true);
				} else if ("normal".equals(part)) { //$NON-NLS-1$
					fontState.setItalic(false);
				}
			}
		}
	}

	private static class FontWeightRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_FONT_WEIGHT;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			if ("bold".equals(rule.value) || "bolder".equals(rule.value)) { //$NON-NLS-1$ //$NON-NLS-2$
				fontState.setBold(true);
			} else if ("normal".equals(rule.value) || "lighter".equals(rule.value)) { //$NON-NLS-1$ //$NON-NLS-2$
				fontState.setBold(false);
			}
		}
	}

	private class FontSizeRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_FONT_SIZE;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			String cssFontSizeValue = rule.value;
			if (cssFontSizeValue.endsWith("%")) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				try {
					float percentage = Float.parseFloat(cssFontSizeValue.substring(0, cssFontSizeValue.length() - 1)) / 100f;
					if (percentage > 0) {
						fontState.size = percentage * defaultSize;
					}
				} catch (NumberFormatException e) {
					// ignore
				}
			} else if ("xx-small".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize - (defaultSize * 0.6f);
			} else if ("x-small".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize - (defaultSize * 0.4f);
			} else if ("small".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize - (defaultSize * 0.2f);
			} else if ("medium".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize;
			} else if ("large".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize + (defaultSize * 0.2f);
			} else if ("x-large".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize + (defaultSize * 0.4f);
			} else if ("xx-large".equals(cssFontSizeValue)) { //$NON-NLS-1$
				float defaultSize = defaultFont.getFontData()[0].getHeight();
				fontState.size = defaultSize + (defaultSize * 0.6f);
			} else if ("larger".equals(cssFontSizeValue)) { //$NON-NLS-1$
				fontState.size = parentFontState.size * 1.2f;
			} else if ("smaller".equals(cssFontSizeValue)) { //$NON-NLS-1$
				fontState.size = parentFontState.size - (parentFontState.size * 0.2f);
			} else {
				try {
					if (cssFontSizeValue.endsWith("pt") || cssFontSizeValue.endsWith("px")) { //$NON-NLS-1$ //$NON-NLS-2$
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
	}

	private static class FontFamilyRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_FONT_FAMILY;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			String[] parts = rule.value.split("((\\s+)|(\\s*,\\s*))"); //$NON-NLS-1$
			for (String part : parts) {
				if (part.length() > 1 && part.startsWith("'") && part.endsWith("'")) { //$NON-NLS-1$ //$NON-NLS-2$
					part = part.substring(1, part.length() - 1);
				}
				if ("monospace".equals(part) || "courier".equalsIgnoreCase(part) //$NON-NLS-1$ //$NON-NLS-2$ 
						|| "courier new".equalsIgnoreCase(part)) { //$NON-NLS-1$
					fontState.setFixedWidth(true);
				} else {
					fontState.setFixedWidth(false);
				}
			}
		}
	}

	private static class TextDecorationRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_TEXT_DECORATION;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			String[] parts = rule.value.split("((\\s+)|(\\s*,\\s*))"); //$NON-NLS-1$
			for (String part : parts) {
				if ("none".equals(part)) { //$NON-NLS-1$
					fontState.setStrikethrough(false);
					fontState.setUnderline(false);
				} else if ("line-through".equals(part)) { //$NON-NLS-1$
					fontState.setStrikethrough(true);
				} else if ("underline".equals(part)) { //$NON-NLS-1$
					fontState.setUnderline(true);
				}
			}
		}
	}

	private static class VerticalAlignRuleHandler implements RuleHandler {
		public String getRuleName() {
			return RULE_VERTICAL_ALIGN;
		}

		public void process(CssRule rule, FontState fontState, FontState parentFontState) {
			if ("super".equals(rule.value)) { //$NON-NLS-1$
				fontState.setSuperscript(true);
			} else if ("sub".equals(rule.value)) { //$NON-NLS-1$
				fontState.setSubscript(true);
			}
		}
	}

	private static RGB toRGB(int rgb) {
		return new RGB((rgb & 0xFF0000) >> 16, (rgb & 0x00FF00) >> 8, (rgb & 0x0000FF));
	}

	/**
	 * get the RGB value for a color name or any other valid CSS expression of a color value.
	 * 
	 * @param cssColor
	 *            the css color
	 * 
	 * @return the RGB value or null if it cannot be determined.
	 */
	public static Integer cssColorRgb(String cssColor) {
		Integer rgb = colorToRgb.get(cssColor.toLowerCase());
		if (rgb == null) {
			try {
				if (cssColor.startsWith("#")) { //$NON-NLS-1$
					String rgbNumeric = cssColor.substring(1);
					if (rgbNumeric.length() == 3) {
						String firstDigit = rgbNumeric.substring(0, 1);
						String secondDigit = rgbNumeric.substring(1, 2);
						String thirdDigit = rgbNumeric.substring(2, 3);
						rgbNumeric = firstDigit + firstDigit + secondDigit + secondDigit + thirdDigit + thirdDigit;
					}
					rgb = Integer.parseInt(rgbNumeric, 16);
				} else {
					Matcher rgbMatcher = rgbPattern.matcher(cssColor);
					if (rgbMatcher.matches()) {
						String r = rgbMatcher.group(1);
						String g = rgbMatcher.group(2);
						String b = rgbMatcher.group(3);
						String rgbNumeric = String.format("%02X%02X%02X", Integer.parseInt(r), Integer.parseInt(g), //$NON-NLS-1$
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

	public void processCssStyles(FontState fontState, FontState parentFontState, CssRule rule) {
		RuleHandler ruleHandler = ruleNameToHandler.get(rule.name);
		if (ruleHandler != null) {
			ruleHandler.process(rule, fontState, parentFontState);
		}
	}

}
