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
package org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class BulletDrawingStrategy implements IDrawingStrategy {

	public void draw(Annotation annotation, GC gc, StyledText textWidget, int offset, int length, Color color) {
		BulletAnnotation bullet = (BulletAnnotation) annotation;
		if (gc != null) {
			final Color foreground = gc.getForeground();
			final Color background = gc.getBackground();

			if (length < 1) {
				return;
			}

			Point left = textWidget.getLocationAtOffset(offset);
			Point right = textWidget.getLocationAtOffset(offset + length);
			if (left.x > right.x) {
				// hack: sometimes linewrapping text widget gives us the wrong x/y for the first character of a line that
				// has been wrapped.
				left.x = 0;
				left.y = right.y;
			}
			int baseline = textWidget.getBaseline(offset);

			int lineHeight = textWidget.getLineHeight(offset);

			int vcenter = left.y + (baseline / 2) + (baseline / 4);
			int hcenter = left.x + ((right.x - left.x) / 2);

			gc.setLineWidth(0); // NOTE: 0 means width is 1 but with optimized performance
			gc.setLineStyle(SWT.LINE_SOLID);

			// bug 262999: determine if we're painting in a selection
			Point selection = textWidget.getSelection();
			// non-zero length selection, so see if where we're about to draw is within it
			if (offset >= selection.x && offset < selection.y && selection.x < selection.y) {
				gc.setBackground(textWidget.getSelectionBackground());
				gc.setForeground(textWidget.getSelectionForeground());
			} else {
				gc.setBackground(textWidget.getBackground());
			}

			// erase whatever character was there
			gc.fillRectangle(left.x, left.y, right.x - left.x, lineHeight);

			// now paint the bullet
			switch (bullet.getIndentLevel()) {
			case 1: // round solid bullet
				gc.setBackground(color);
				gc.fillOval(hcenter - 3, vcenter - 2, 5, 5);
				break;
			case 2: // round empty bullet
				gc.setForeground(color);
				gc.drawOval(hcenter - 3, vcenter - 3, 5, 5);
				break;
			default: // square bullet
				gc.setBackground(color);
				gc.fillRectangle(hcenter - 3, vcenter - 2, 5, 5);
				break;
			}
			gc.setForeground(foreground);
			gc.setBackground(background);
		} else {
			textWidget.redrawRange(offset, length, true);
		}
	}

}
