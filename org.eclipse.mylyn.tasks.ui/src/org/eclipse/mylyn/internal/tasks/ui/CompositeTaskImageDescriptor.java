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

package org.eclipse.mylar.internal.tasks.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Mik Kersten
 */
public class CompositeTaskImageDescriptor extends CompositeImageDescriptor {

	private ImageData base;

	private ImageData context;

	private ImageData kind;
	
//	private ImageData priority;
	
	protected Point size;

	private static final int WIDTH_PRIORITY = 0;//5;
	
	private static final int WIDTH_CONTEXT = 8;
	
	private static final int WIDTH_SQUISH = 0;
	
	private static final int WIDTH_ICON = 16;
	
	static int WIDTH = WIDTH_CONTEXT + WIDTH_PRIORITY + WIDTH_ICON - WIDTH_SQUISH;
	
	public CompositeTaskImageDescriptor(ImageDescriptor icon, ImageDescriptor overlayKind, ImageDescriptor contextToggle) {
		this.base = getImageData(icon);
		if (overlayKind != null) {
			this.kind = getImageData(overlayKind);
		}
//		if (overlayPriority != null) {
//			this.priority = getImageData(overlayPriority);
//		}
		if (contextToggle != null) {
			this.context = getImageData(contextToggle);
		} 
		this.size = new Point(WIDTH, base.height);
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		if (context != null) {
			drawImage(context, 0, 0);
		}
//		if (priority != null) {
//			drawImage(priority, WIDTH_CONTEXT+2, 0);
//		}
		drawImage(base, WIDTH_CONTEXT + WIDTH_PRIORITY - WIDTH_SQUISH, 0);
		if (kind != null) {
			drawImage(kind, WIDTH_CONTEXT + WIDTH_PRIORITY + 4 - WIDTH_SQUISH, 5);
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