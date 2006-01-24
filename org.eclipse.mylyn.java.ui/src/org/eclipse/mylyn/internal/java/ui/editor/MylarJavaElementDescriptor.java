/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.java.ui.editor;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author Shawn Minto
 */
public class MylarJavaElementDescriptor extends CompositeImageDescriptor {

	private Image baseImage;

	private ImageDescriptor overlay;

	private Point fSize;

	public MylarJavaElementDescriptor(Image baseImage, ImageDescriptor overlay, Point size) {
		this.baseImage = baseImage;
		this.overlay = overlay;
		setImageSize(size);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		if (baseImage == null)
			return;
		ImageData bg = baseImage.getImageData();

		drawImage(bg, 0, 0);

		// Point size= getSize();
		ImageData data = getImageData(overlay);
		drawImage(data, data.width, bg.height - data.height);
	}

	private ImageData getImageData(ImageDescriptor descriptor) {
		ImageData data = descriptor.getImageData(); // see bug 51965:
													// getImageData can return
													// null
		if (data == null) {
			data = DEFAULT_IMAGE_DATA;
			JavaPlugin.logErrorMessage("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
		}
		return data;
	}

	/**
	 * Sets the size of the image created by calling <code>createImage()</code>.
	 * 
	 * @param size
	 *            the size of the image returned from calling
	 *            <code>createImage()</code>
	 * @see ImageDescriptor#createImage()
	 */
	public void setImageSize(Point size) {
		Assert.isNotNull(size);
		Assert.isTrue(size.x >= 0 && size.y >= 0);
		fSize = size;
	}

	@Override
	protected Point getSize() {
		return new Point(fSize.x, fSize.y);
	}

}
