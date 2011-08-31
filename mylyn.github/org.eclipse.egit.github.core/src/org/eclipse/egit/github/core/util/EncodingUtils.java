/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.util;

import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * Encoding utilities
 */
public abstract class EncodingUtils {

	/**
	 * Decode base64 encoded string
	 *
	 * @param content
	 * @return byte array
	 */
	public static final byte[] fromBase64(final String content) {
		try {
			return Base64.decodeBase64(content.getBytes(CHARSET_UTF8));
		} catch (UnsupportedEncodingException e) {
			return Base64.decodeBase64(content.getBytes());
		}
	}

	/**
	 * Base64 encode given byte array
	 *
	 * @param content
	 * @return byte array
	 */
	public static final byte[] toBase64(final byte[] content) {
		return Base64.encodeBase64(content);
	}
}
