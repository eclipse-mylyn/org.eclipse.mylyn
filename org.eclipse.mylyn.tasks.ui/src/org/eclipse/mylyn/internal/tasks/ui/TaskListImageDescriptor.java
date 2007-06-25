/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mik Kersten
 */
public class TaskListImageDescriptor extends CompositeImageDescriptor {

	private ImageData base;

	private ImageData overlay;

	private boolean top;

	private boolean left;

	protected Point size;

	public TaskListImageDescriptor(ImageDescriptor baseDesc, ImageDescriptor overlayDesc, boolean top, boolean left) {
		this.base = getImageData(baseDesc);
		this.top = top;
		this.left = left;
		if (overlayDesc != null) {
			this.overlay = getImageData(overlayDesc);
		}
		Point size = new Point(base.width, base.height);
		setImageSize(size);
	}

	public TaskListImageDescriptor(ImageDescriptor baseDesc, Point size) {
		this.base = getImageData(baseDesc);
		setImageSize(size);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, 0, 0);
		int x = 0;
		int y = 0;
		if (!left)
			x = 8;// base.width - overlay.width;
		if (!top)
			y = 8;// base.height - overlay.height;
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