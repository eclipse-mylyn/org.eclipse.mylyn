/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.File;

import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/**
 * Represents a attachment created from a {@link Image}; handles lazy persistence into a {@link File} and image data
 * change
 * 
 * @author Willian Mitsuda
 */
public class ImageAttachment extends LocalAttachment {

	private static final long serialVersionUID = 28264291629999181L;

	/**
	 * Provides the {@link Image} object that will be converted to desired file format, and then attached
	 */
	private final IImageCreator imageCreator;

	public ImageAttachment(IImageCreator imageCreator) {
		this.imageCreator = imageCreator;
	}

	@Override
	public void setContentType(String contentType) {
		// Does not apply; actually always save as JPEG
		// Will be implemented on bug#210179
	}

	@Override
	public String getContentType() {
		return "image/jpeg";
	}

	@Override
	public String getFilename() {
		return "screenshot.jpg";
	}

	private boolean dirty = true;

	public void markDirty() {
		dirty = true;
	}

	public void ensureImageFileWasCreated() {
		if (!dirty) {
			return;
		}

		dirty = false;
		createContents();
	}

	private void createContents() {
		Image image = imageCreator.createImage();
		try {
			String path = TasksUiPlugin.getDefault().getDefaultDataDirectory();
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] { image.getImageData() };
			String fileName = path + "/" + getFilename();
			loader.save(fileName, SWT.IMAGE_JPEG);
			setFile(new File(fileName));
			setFilePath(fileName);
		} finally {
			image.dispose();
		}
	}

	public void clearImageFile() {
		String path = TasksUiPlugin.getDefault().getDefaultDataDirectory();
		new File(path + "/" + getFilename()).delete();
	}

}