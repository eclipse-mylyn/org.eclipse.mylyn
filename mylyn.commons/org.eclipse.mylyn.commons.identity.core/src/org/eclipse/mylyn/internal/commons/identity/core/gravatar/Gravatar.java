/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

import java.io.Serializable;

import org.eclipse.core.runtime.Assert;

/**
 * Gravatar class containing id and image data.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Gravatar implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7303486086217698261L;

	private final String id;

	private final long updateTime;

	private final byte[] bytes;

	/**
	 * Create gravatar
	 * 
	 * @param id
	 * @param updateTime
	 * @param bytes
	 */
	public Gravatar(String id, long updateTime, byte[] bytes) {
		Assert.isNotNull(id, "Id cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(bytes, "Bytes cannot be null"); //$NON-NLS-1$
		this.id = id;
		this.updateTime = updateTime;
		this.bytes = bytes;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Gravatar) {
			return getId().equals(((Gravatar) obj).getId());
		}
		return false;
	}

	/**
	 * Get gravatar image as byte array
	 * 
	 * @return non-null byte array
	 */
	public byte[] getBytes() {
		byte[] copy = new byte[this.bytes.length];
		System.arraycopy(this.bytes, 0, copy, 0, copy.length);
		return copy;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.id;
	}

	/**
	 * Get gravatar id
	 * 
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Get time gravatar was loaded
	 * 
	 * @return update time
	 */
	public long getUpdateTime() {
		return this.updateTime;
	}

}
