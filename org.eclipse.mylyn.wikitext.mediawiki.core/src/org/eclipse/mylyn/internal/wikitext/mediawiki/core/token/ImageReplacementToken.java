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
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes.Align;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * match [[Image:someImage.png]]
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Wikipedia:Extended_image_syntax">Extended image syntax</a>
 * 
 * @author David Green
 * 
 */
public class ImageReplacementToken extends PatternBasedElement {

	private static Pattern widthHeightPart = Pattern.compile("(\\d+)(x(\\d+))?px"); //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:\\[\\[Image:([^\\]\\|]+)(?:\\|([^\\]]*))?\\]\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ImageReplacementTokenProcessor();
	}

	private static class ImageReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String imageUrl = group(1);
			String optionsString = group(2);

			ImageAttributes attributes = new ImageAttributes();
			if (optionsString != null) {
				Matcher matcher;
				String[] options = optionsString.split("\\s*\\|\\s*"); //$NON-NLS-1$
				for (String option : options) {
					if ("center".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Middle);
					} else if ("left".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Left);
					} else if ("right".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Right);
					} else if ("none".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(null);
					} else if ("thumb".equals(option) || "thumbnail".equals(option)) { //$NON-NLS-1$ //$NON-NLS-2$
						// ignore
					} else if ((matcher = widthHeightPart.matcher(option)).matches()) {
						try {
							String sizePart = matcher.group(1);
							String heightPart = matcher.group(3);
							int size = Integer.parseInt(sizePart);
							attributes.setWidth(size);
							if (heightPart != null) {
								int height = Integer.parseInt(heightPart);
								attributes.setHeight(height);
							}
						} catch (NumberFormatException e) {
							// ignore
						}
					} else if ("frameless".equals(option)) { //$NON-NLS-1$
						attributes.setBorder(0);
					} else if ("frame".equals(option)) { //$NON-NLS-1$
						attributes.setBorder(1);
					} else {
						attributes.setTitle(option);
					}
				}
			}
			builder.image(attributes, imageUrl);
		}
	}

}
