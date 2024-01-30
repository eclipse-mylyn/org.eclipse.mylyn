/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.identity.core.spi;

import java.io.Serializable;

import org.eclipse.mylyn.commons.identity.core.IProfileImage;

/**
 * @author Steffen Pingel
 * @since 0.8
 */
public final class ProfileImage implements IProfileImage, Serializable {

	private static final long serialVersionUID = 8211724823497362719L;

	byte[] data;

	int width;

	int height;

	String format;

	long timestamp;

	public ProfileImage(byte[] data, int width, int height, String format) {
		this.data = data;
		this.width = width;
		this.height = height;
		this.format = format;
		timestamp = System.currentTimeMillis();
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public String getFormat() {
		return format;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
