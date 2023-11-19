/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence.internal.phrase;

import org.eclipse.mylyn.wikitext.confluence.internal.util.Options;
import org.eclipse.mylyn.wikitext.confluence.internal.util.Options.Handler;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes.Align;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class ImagePhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 1;

	private static final int OPTIONS_GROUP = 2;

	@Override
	protected String getPattern(int groupOffset) {

		return "!([^\\|!\\s]+)(?:\\|([^!]*))?!"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ImagePhraseModifierProcessor();
	}

	private static class ImagePhraseModifierProcessor extends PatternBasedElementProcessor {

		@Override
		public void emit() {
			String imageUrl = group(CONTENT_GROUP);
			String imageOptions = group(OPTIONS_GROUP);

			final ImageAttributes attributes = new ImageAttributes();
			if (imageOptions != null) {
				Options.parseOptions(imageOptions, new Handler() {
					public void setOption(String key, String value) {
						if ("alt".equalsIgnoreCase(key)) { //$NON-NLS-1$
							attributes.setAlt(value);
						} else if ("title".equalsIgnoreCase(key)) { //$NON-NLS-1$
							attributes.setTitle(value);
						} else if ("align".equalsIgnoreCase(key)) { //$NON-NLS-1$
							if ("middle".equalsIgnoreCase(value)) { //$NON-NLS-1$
								attributes.setAlign(Align.Middle);
							} else if ("left".equalsIgnoreCase(value)) { //$NON-NLS-1$
								attributes.setAlign(Align.Left);
							} else if ("right".equalsIgnoreCase(value)) { //$NON-NLS-1$
								attributes.setAlign(Align.Right);
							} else if ("center".equalsIgnoreCase(value)) { //$NON-NLS-1$
								attributes.setAlign(Align.Center);
							}
						} else {
							try {
								if ("border".equalsIgnoreCase(key)) { //$NON-NLS-1$
									attributes.setBorder(Integer.parseInt(value));
								} else if ("height".equalsIgnoreCase(key)) { //$NON-NLS-1$
									attributes.setHeight(Integer.parseInt(value));
								} else if ("width".equalsIgnoreCase(key)) { //$NON-NLS-1$
									attributes.setWidth(Integer.parseInt(value));
								}
							} catch (NumberFormatException e) {
								// ignore
							}
						}
					}

					public void setOption(String option) {
						// ignore
					}
				});
			}
			if (attributes.getAlign() == Align.Center) {
				// bug 293573: confluence centers images using div
				Attributes divAttributes = new Attributes();
				divAttributes.setCssStyle("text-align: center;"); //$NON-NLS-1$
				builder.beginBlock(BlockType.DIV, divAttributes);
				attributes.setAlign(null);
				builder.image(attributes, imageUrl);
				builder.endBlock();
			} else {
				builder.image(attributes, imageUrl);
			}
		}
	}

}
