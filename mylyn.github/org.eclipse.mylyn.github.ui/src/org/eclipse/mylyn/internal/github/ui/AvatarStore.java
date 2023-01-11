/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Stores Avatars
 */
public class AvatarStore implements Serializable, ISchedulingRule {

	/**
	 * Callback interface for avatar image data loaded
	 */
	public interface IAvatarCallback {

		/**
		 * Avatar loaded
		 *
		 * @param data
		 * @param store
		 */
		void loaded(ImageData data, AvatarStore store);

	}

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7791322302733784441L;

	/**
	 * TIMEOUT
	 */
	public static final int TIMEOUT = 30 * 1000;

	/**
	 * BUFFER_SIZE
	 */
	public static final int BUFFER_SIZE = 8192;

	private Map<String, byte[]> avatars = new HashMap<>();

	/**
	 * Get cached avatar image data
	 *
	 * @param url
	 * @return image data or null if not present in store
	 */
	public ImageData getAvatar(String url) {
		ImageData data = null;
		url = normalize(url);
		if (url != null) {
			byte[] cached = this.avatars.get(url);
			if (cached != null)
				data = getData(cached);
		}
		return data;
	}

	/**
	 * Normalize url removing query parameters
	 *
	 * @param url
	 * @return normalized
	 */
	protected String normalize(String url) {
		try {
			URL parsed = new URL(url);
			parsed = new URL(parsed.getProtocol(), parsed.getHost(),
					parsed.getPath());
			return parsed.toExternalForm();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Load avatar
	 *
	 * @param url
	 * @param callback
	 */
	public void loadAvatar(final String url, final IAvatarCallback callback) {
		Job job = new Job(MessageFormat.format("Loading avatar for {0}", url)) { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ImageData data = loadAvatar(url);
					if (data != null)
						callback.loaded(data, AvatarStore.this);
				} catch (IOException ignore) {
					// Ignored
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * Load avatar image data from url
	 *
	 * @param url
	 * @return avatar image data
	 * @throws IOException
	 */
	public ImageData loadAvatar(String url) throws IOException {
		url = normalize(url);
		URL parsed = new URL(url);

		byte[] data = this.avatars.get(url);
		if (data != null)
			return getData(data);

		URLConnection connection = parsed.openConnection();
		connection.setConnectTimeout(TIMEOUT);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStream input = connection.getInputStream();
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = input.read(buffer);
			while (read != -1) {
				output.write(buffer, 0, read);
				read = input.read(buffer);
			}
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
				// Ignored
			}
			try {
				output.close();
			} catch (IOException ignore) {
				// Ignored
			}
		}
		data = output.toByteArray();
		this.avatars.put(url, data);
		return getData(data);
	}

	/**
	 * Get scaled image
	 *
	 * @param size
	 * @param data
	 * @return image data scaled to specified size
	 */
	public Image getScaledImage(int size, ImageData data) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Image image = new Image(display, data);
		Rectangle sourceBounds = image.getBounds();

		// Return original image and don't scale if size matches request
		if (sourceBounds.width == size)
			return image;

		Image scaled = new Image(display, size, size);
		GC gc = new GC(scaled);
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			Rectangle targetBounds = scaled.getBounds();
			gc.drawImage(image, 0, 0, sourceBounds.width, sourceBounds.height,
					0, 0, targetBounds.width, targetBounds.height);
		} finally {
			gc.dispose();
			image.dispose();
		}
		return scaled;
	}

	/**
	 * Get avatar image data
	 *
	 * @param bytes
	 * @return image data
	 */
	public ImageData getData(byte[] bytes) {
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
		try {
			ImageData[] images = new ImageLoader().load(stream);
			if (images.length > 0)
				return images[0];
		} finally {
			try {
				stream.close();
			} catch (IOException ignore) {
				// Ignored
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean contains(ISchedulingRule rule) {
		return this == rule;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		return this == rule;
	}
}
