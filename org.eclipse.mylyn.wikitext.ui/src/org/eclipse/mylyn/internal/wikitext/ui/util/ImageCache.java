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

package org.eclipse.mylyn.internal.wikitext.ui.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * An image cache that manages image lifecycle and maps images to URLs.
 * 
 * The lifetime of the image cache is intended to be short, as it keeps image widgets and image data in memory.
 * Typically an ImageCache lifetime corresponds to the lifetime of the object that is displaying the images.
 * 
 * @author David Green
 */
public class ImageCache {

	private final Map<String, Image> imageByUrl = new HashMap<String, Image>();

	private URL base;

	private Image missingImage;

	private boolean disposed = false;

	/**
	 * get an image by its url
	 * 
	 * @param url
	 *            the url which may be absolute or relative
	 * 
	 * @return the image, or the {@link ImageDescriptor#getMissingImageDescriptor() missing image} if the image cannot
	 *         be found
	 */
	public Image getImage(String url) {
		if (disposed) {
			throw new IllegalStateException();
		}
		Image image = imageByUrl.get(url);
		if (image == null) {
			try {
				URL location = base == null ? new URL(url) : new URL(base, url);
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(location);
				image = descriptor.createImage(false);
				if (image == null) {
					image = getMissingImage();
				}
				imageByUrl.put(url, image);
			} catch (MalformedURLException e) {
				image = getMissingImage();
				imageByUrl.put(url, image);
			}
		}
		return image;
	}

	public void putImage(String url, Image image) {
		if (disposed) {
			throw new IllegalStateException();
		}
		Image previous = imageByUrl.put(url, image);
		if (previous != null && previous != missingImage) {
			previous.dispose();
		}
	}

	public Image getMissingImage() {
		if (missingImage == null) {
			missingImage = ImageDescriptor.createFromURL(ImageCache.class.getResource("resources/missing-image.png"))
					.createImage();
		}
		return missingImage;
	}

	public void dispose() {
		disposed = true;
		for (Image image : imageByUrl.values()) {
			if (image != missingImage) {
				image.dispose();
			}
		}
		imageByUrl.clear();
		if (missingImage != null) {
			missingImage.dispose();
			missingImage = null;
		}
	}

	/**
	 * the base url from which relative urls are computed, or null if it is unknown
	 * 
	 * @see URL#URL(URL, String)
	 */
	public URL getBase() {
		return base;
	}

	/**
	 * the base url from which relative urls are computed, or null if it is unknown
	 * 
	 * @see URL#URL(URL, String)
	 */
	public void setBase(URL base) {
		this.base = base;
	}
}
