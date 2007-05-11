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

	private ImageData synchState;

	private ImageData kind;
	
	protected Point size;

	public static final int WIDTH_DECORATION = 5;
	
//	private static final int WIDTH_SYNCH = 9;//5;
	
	private static final int WIDTH_SQUISH = 0;
	
	private static final int WIDTH_ICON = 16;
	
	static final int OFFSET_SYNCH = 5;
		
	static int WIDTH;
	
	static {
//		if (SWT.getPlatform().equals("win32")) {
//			WIDTH_DECORATION = 0;
//		} else {
//			WIDTH_DECORATION = 4;
//		}
		WIDTH = WIDTH_DECORATION + WIDTH_ICON - WIDTH_SQUISH;
	}
	
	public CompositeTaskImageDescriptor(ImageDescriptor icon, ImageDescriptor overlayKind) {
		this.base = getImageData(icon);
		if (overlayKind != null) {
			this.kind = getImageData(overlayKind);
		}
//		if (synchState != null) {
//			this.synchState = getImageData(synchState);
//		} 
		this.size = new Point(WIDTH, base.height);
	}
	
	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(base, WIDTH_DECORATION, 1);
		if (kind != null) {
			drawImage(kind, WIDTH_DECORATION+5, 6);
		}
		if (synchState != null) {
			drawImage(synchState, WIDTH_ICON /*WIDTH_SQUISH + 1*/, OFFSET_SYNCH);
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