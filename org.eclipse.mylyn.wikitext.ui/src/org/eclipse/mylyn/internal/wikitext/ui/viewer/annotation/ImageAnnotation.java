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

package org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;

/**
 * An annotation that represents an image.
 * 
 * @author David Green
 */
public class ImageAnnotation extends Annotation {
	public static final String TYPE = "org.eclipse.mylyn.internal.wikitext.ui.viewer.annotation.image";

	private Image image;

	private final String url;

	/**
	 * @param url
	 *            the url to the image data, which may be relative
	 * @param image
	 *            the image to display for this annotation, which must be disposed by the caller. May be null
	 */
	public ImageAnnotation(String url, Image image) {
		super(TYPE, false, "");
		this.url = url;
		this.image = image;
	}

	public String getElementId() {
		return getText();
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}
}
