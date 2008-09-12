/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mik Kersten
 */
public class CompositeContainerImageDescriptor extends CompositeImageDescriptor {

	private final ImageData base;

	private ImageData overlay;

	protected Point size;

	public CompositeContainerImageDescriptor(ImageDescriptor icon, ImageDescriptor overlay, boolean wide) {
		this.base = getImageData(icon);
		if (overlay != null) {
			this.overlay = getImageData(overlay);
		}
		int width = CompositeElementImageDescriptor.WIDTH_ICON;
		if (wide) {
			width += CompositeElementImageDescriptor.OFFSET_DECORATION;
		}
		this.size = new Point(width, base.height);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, 0, 0);
		if (overlay != null) {
			drawImage(overlay, base.width + 2, 0);
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