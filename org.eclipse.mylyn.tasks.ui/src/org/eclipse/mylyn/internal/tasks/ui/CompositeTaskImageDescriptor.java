/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mik Kersten
 */
public class CompositeTaskImageDescriptor extends CompositeImageDescriptor {

	private ImageData base;

	private ImageData kind;

	protected Point size;

	public static final int OFFSET_DECORATION = 6;

	static final int WIDTH_ICON = 16;

	private int offset = 0;

	public CompositeTaskImageDescriptor(ImageDescriptor icon, ImageDescriptor overlayKind, boolean wide) {
		this.base = getImageData(icon);
		if (overlayKind != null) {
			this.kind = getImageData(overlayKind);
		}
		int width = WIDTH_ICON;
		if (wide) {
			width += OFFSET_DECORATION;
			offset = OFFSET_DECORATION;
		}
		this.size = new Point(width, base.height);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, offset, 1);
		if (kind != null) {
			drawImage(kind, offset + 5, 6);
		}
	}

	private ImageData getImageData(ImageDescriptor descriptor) {
		ImageData data = descriptor.getImageData();
		// see bug 51965: getImageData can return null
		if (data == null) {
			data = DEFAULT_IMAGE_DATA;
		}
		return data;
	}

	@Override
	protected Point getSize() {
		return new Point(size.x, size.y);
	}
}