/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
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
		base = getImageData(icon);
		if (overlay != null) {
			this.overlay = getImageData(overlay);
		}
		int width = CompositeElementImageDescriptor.WIDTH_ICON;
		if (wide) {
			width += CompositeElementImageDescriptor.OFFSET_DECORATION;
		}
		size = new Point(width, base.height);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, 0, 0);
		if (overlay != null) {
			drawImage(overlay, base.width + 2, 0);
		}
	}

	private ImageData getImageData(ImageDescriptor descriptor) {
		ImageData data = descriptor.getImageData(100);
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