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

package org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * A drawing strategy that can draw images for {@link ImageAnnotation}.
 * 
 * @author David Green
 */
public class ImageDrawingStrategy implements IDrawingStrategy {

	public void draw(Annotation annotation, GC gc, StyledText textWidget, int offset, int length, Color color) {
		if (gc != null) {
			ImageAnnotation imageAnnotation = (ImageAnnotation) annotation;
			Image image = imageAnnotation.getImage();
			if (image != null) {
				Point left = textWidget.getLocationAtOffset(offset);
				if (length > 0) {
					Point right = textWidget.getLocationAtOffset(offset + length);
					if (left.x > right.x) {
						// hack: sometimes linewrapping text widget gives us the wrong x/y for the first character of a line that
						// has been wrapped.
						left.x = 0;
						left.y = right.y;
					}
				}
				// fill the background first so that drawing works properly when
				// the viewer itself is not redrawing and image contains semi-transparent
				// regions
				Color foreground = gc.getForeground();
				Color background = gc.getBackground();
				gc.setForeground(textWidget.getBackground());
				gc.setBackground(textWidget.getBackground());
				Rectangle bounds = image.getBounds();
				gc.fillRectangle(new Rectangle(left.x, left.y, bounds.width, bounds.height));

				// now draw the image.
				gc.setForeground(foreground);
				gc.setBackground(background);
				gc.drawImage(image, left.x, left.y);
			}
		} else {
			textWidget.redrawRange(offset, length, true);
		}
	}

}
