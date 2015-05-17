/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects inline images and single line block images:
 *
 * <pre>
 * image::sunset.jpg[formatting options].
 * image:sunset.jpg[formatting options].
 * </pre>
 *
 * See http://asciidoctor.org/docs/user-manual/#images
 *
 * @author Max Rydahl Andersen
 */
public class InlineImageReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "image::?(.*?)\\[(.*?)\\]"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineImageReplacementTokenProcessor();
	}

	private static class InlineImageReplacementTokenProcessor extends PatternBasedElementProcessor {

		Pattern keyValuePattern = Pattern.compile("(.*)=\"(.*)\""); //$NON-NLS-1$

		@Override
		public void emit() {
			String src = group(1);
			String formatting = group(2);

			List<String> positional = new ArrayList<>();
			positional.add("alt");
			positional.add("width");
			positional.add("height");

			Map<String, String> properties = getFormattingProperties(formatting, positional);

			if (!properties.containsKey("alt") || properties.get("alt").isEmpty()) {
				// if no alt provided make one up using base filename
				// of source image name.
				int end = src.lastIndexOf(".");

				if (end != -1) {
					String firstSrc = src.substring(0, end);
					int start = Math.max(firstSrc.lastIndexOf("/") + 1, 0);
					properties.put("alt", firstSrc.substring(start));
				} else {
					properties.put("alt", src);
				}

			}

			// TODO: honor imagedir attribute?

			if (group(0).startsWith("image::")) { // imageblock
				Attributes imageSpanAttributes = new Attributes();
				imageSpanAttributes.setCssClass("imageblock"); //$NON-NLS-1$

				builder.beginBlock(BlockType.DIV, imageSpanAttributes);

				Attributes contentAttr = new Attributes();
				contentAttr.setCssClass("content"); //$NON-NLS-1$
				builder.beginBlock(BlockType.DIV, contentAttr);

				emitImage(builder, src, properties);

				builder.endBlock(); // content

				if (properties.containsKey("title")) {
					Attributes attr = new Attributes();
					attr.setCssClass("title");
					builder.beginBlock(BlockType.DIV, attr);
					if (properties.containsKey("caption")) { //$NON-NLS-1$
						builder.characters(properties.get("caption")); //$NON-NLS-1$
					}
					builder.characters(properties.get("title")); //$NON-NLS-1$
					builder.endBlock();
				}

				builder.endBlock(); // imageBlock

			} else { // inline image
				Attributes imageBlockAttr = new Attributes();
				imageBlockAttr.setCssClass("image"); //$NON-NLS-1$

				builder.beginSpan(SpanType.SPAN, imageBlockAttr);
				emitImage(builder, src, properties);
				builder.endSpan();

			}

		}

		/**
		 * Parses format string into a Map of key/value pairs. Supports positional parameters too.
		 *
		 * @param rawFormat
		 *            The raw format string found in AsciiDoc source
		 * @param positionalParameters
		 *            a list of strings for the positional parameters (i.e. "alt", "width", "height" for images)
		 * @param defaultValueKey
		 *            the key to use if no parameters found (i.e. "alt" for images)
		 * @return
		 */
		private Map<String, String> getFormattingProperties(String rawFormat, List<String> positionalParameters) {
			Map<String, String> properties = new HashMap<>();

			// TODO: handle escaped strings and default sequence of parameters
			// i.e. sunset,100,200,title="test"
			String[] valpairs = rawFormat.split(","); //$NON-NLS-1$
			for (String pair : valpairs) {
				Matcher matcher = keyValuePattern.matcher(pair.trim());

				String key, value;

				if (matcher.find()) {
					key = matcher.group(1);
					value = matcher.group(2);
					properties.put(key, value);
				} else {
					// could not parse key/value pairs
					if (positionalParameters.isEmpty()) {
						//no more positional items left - ignoring
					} else {
						properties.put(positionalParameters.remove(0), pair.trim());
					}
				}
			}

			return properties;
		}

		static private void emitImage(DocumentBuilder builder, String src, Map<String, String> properties) {
			ImageAttributes attributes = new ImageAttributes();
			// TODO: find way to avoid unnecessary border=0 being added to
			// output
			attributes.setAlt(properties.get("alt")); //$NON-NLS-1$

			if (properties.containsKey("height")) { //$NON-NLS-1$
				try {
					int height = Integer.parseInt(properties.get("height")); //$NON-NLS-1$
					attributes.setHeight(height);
				} catch (NumberFormatException nfe) {
					// ignore
				}

			}

			if (properties.containsKey("width")) { //$NON-NLS-1$
				try {
					int width = Integer.parseInt(properties.get("width")); //$NON-NLS-1$
					attributes.setWidth(width);
				} catch (NumberFormatException nfe) {
					// ignore
				}

			}

			if (properties.containsKey("link")) { //$NON-NLS-1$
				Attributes linkAttributes = new Attributes();
				linkAttributes.setCssClass("image"); //$NON-NLS-1$
				builder.imageLink(linkAttributes, attributes, properties.get("link"), src); //$NON-NLS-1$
			} else {
				builder.image(attributes, src);
			}
		}
	}

}
