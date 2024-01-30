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
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mik Kersten
 */
public class TaskListImageDescriptor extends CompositeImageDescriptor {

	private final ImageData base;

	private ImageData overlay;

	private boolean top;

	private boolean left;

	protected Point size;

	public TaskListImageDescriptor(ImageDescriptor baseDesc, ImageDescriptor overlayDesc, boolean top, boolean left) {
		base = getImageData(baseDesc);
		this.top = top;
		this.left = left;
		if (overlayDesc != null) {
			overlay = getImageData(overlayDesc);
		}
		Point size = new Point(base.width, base.height);
		setImageSize(size);
	}

	public TaskListImageDescriptor(ImageDescriptor baseDesc, Point size) {
		base = getImageData(baseDesc);
		setImageSize(size);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, 0, 0);
		int x = 0;
		int y = 0;
		if (!left) {
			x = 8;// base.width - overlay.width;
		}
		if (!top) {
			y = 8;// base.height - overlay.height;
		}
		if (overlay != null) {
			drawImage(overlay, x, y);
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

	public void setImageSize(Point size) {
		this.size = size;
	}

	@Override
	protected Point getSize() {
		return new Point(size.x, size.y);
	}
}