/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
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

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes.Align;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

/**
 * match [[Image:someImage.png]] or [[File:someImage.png]]
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Wikipedia:Images">Images</a>
 * @see <a href="http://en.wikipedia.org/wiki/Wikipedia:Extended_image_syntax">Extended image syntax</a>
 * @author David Green
 */
public class ImageReplacementToken extends PatternBasedElement {

	private static Pattern widthHeightPart = Pattern.compile("(\\d+)(x(\\d+))?px"); //$NON-NLS-1$

	private static Pattern altPattern = Pattern.compile("\\s*alt\\s*=\\s*(.+)"); //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:\\[\\[(?:[Ii]mage|[Ff]ile):\\s*([^\\]\\|]+)(?:\\|(([^\\[\\]]|(\\[\\[[^\\[\\]]+\\]\\]))*))?\\]\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 4;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ImageReplacementTokenProcessor();
	}

	private static class ImageReplacementTokenProcessor extends PatternBasedElementProcessor {

		@Override
		public MediaWikiLanguage getMarkupLanguage() {
			return (MediaWikiLanguage) super.getMarkupLanguage();
		}

		@Override
		public void emit() {
			String imageUrl = group(1);
			if (imageUrl.indexOf('/') == -1) {
				// images hosted on the wiki should have spaces removed from their name
				// this may seem a little odd but there's an issue here: files can be uploaded
				// to the wiki with a name that differs from the URL.  Page authors can use
				// either name in the wiki markup, and there's no way to know what they've used.
				// to be safe we always replace space with underscore, and do the same in 
				// the image fetching strategy.
				imageUrl = imageUrl.replace(' ', '_');
			}
			imageUrl = getMarkupLanguage().mapImageName(imageUrl);

			String optionsString = group(2);

			boolean thumbnail = false;

			ImageAttributes attributes = new ImageAttributes();
			if (optionsString != null) {
				Matcher matcher;
				String[] options = optionsString.split("\\s*\\|\\s*"); //$NON-NLS-1$
				for (int optionIndex = 0; optionIndex < options.length; ++optionIndex) {
					String option = options[optionIndex];
					if ("center".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Middle);
					} else if ("left".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Left);
					} else if ("right".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(Align.Right);
					} else if ("none".equals(option)) { //$NON-NLS-1$
						attributes.setAlign(null);
					} else if ("thumb".equals(option) || "thumbnail".equals(option)) { //$NON-NLS-1$ //$NON-NLS-2$
						thumbnail = true;
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
						Matcher altMatcher = altPattern.matcher(option);
						if (altMatcher.matches()) {
							attributes.setAlt(altMatcher.group(1));
						} else {
							if (optionIndex == options.length - 1) {
								// the last one is a caption
								attributes.setTitle(option);
							} else {
								// if not the last one, then it's alt text
								attributes.setAlt(option);
							}
						}
					}
				}
			}
			if (thumbnail) {
				// we want to generate something like this:
//				<div class="thumb tleft">
//				<div class="thumbinner" style="width:182px;"><a href="/wiki/File:Example.jpg"
//				class="image"><img alt=""
//				src="http://upload.wikimedia.org/wikipedia/mediawiki/thumb/a/a9/Example.jpg/180px-Example.jpg"
//				width="180" height="120" class="thumbimage" /></a>
//				<div class="thumbcaption">
//				<div class="magnify"><a href="/wiki/File:Example.jpg" class="internal"
//				title="Enlarge"><img src="/skins-1.5/common/images/magnify-clip.png" width="15"
//				height="11" alt="" /></a></div>
//				Official logo of the <a
//				href="/w/index.php?title=International_Floorball_Federation&amp;action=edit&amp;redlink=1"
//				class="new" title="International Floorball Federation (page does not
//				exist)">International Floorball Federation</a>, floorball's governing
//				body.</div>
//				</div>
//				</div>

				String caption = attributes.getTitle();
				attributes.setTitle(null);

				Attributes outerDivAttributes = new Attributes(null, "thumb", null, null); //$NON-NLS-1$
				if (attributes.getAlign() != null) {
					outerDivAttributes.appendCssClass(attributes.getAlign().name().toLowerCase());
				}
				builder.beginBlock(BlockType.DIV, outerDivAttributes);

				final Attributes thumbInnerDivAttributes = new Attributes(
						null,
						"thumbinner", attributes.getWidth() > 0 ? String.format("width:%spx;", attributes.getWidth() + 2) : null, null); //$NON-NLS-1$ //$NON-NLS-2$
				builder.beginBlock(BlockType.DIV, thumbInnerDivAttributes);
				//FIXME: image
				LinkAttributes linkAttributes = new LinkAttributes();
				linkAttributes.setCssClass("image"); //$NON-NLS-1$

				attributes.appendCssClass("thumbimage"); //$NON-NLS-1$
				builder.imageLink(linkAttributes, attributes, imageUrl, imageUrl);

				if (caption != null) {
					builder.beginBlock(BlockType.DIV, new Attributes(null, "thumbcaption", null, null)); //$NON-NLS-1$
					markupLanguage.emitMarkupText(parser, state, caption);
					builder.endBlock(); // div
				}

				builder.endBlock(); // div
				builder.endBlock(); // div

			} else {
				builder.image(attributes, imageUrl);
			}
		}
	}

}
