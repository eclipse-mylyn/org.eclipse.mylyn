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
package org.eclipse.egit.github.core;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.egit.github.core.client.IGitHubConstants;

/**
 * Blob model class
 */
public class Blob implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -7538850340225102994L;

	/**
	 * ENCODING_BASE64
	 */
	public static final String ENCODING_BASE64 = "base64"; //$NON-NLS-1$

	/**
	 * ENCODING_UTF8
	 */
	public static final String ENCODING_UTF8 = "utf-8"; //$NON-NLS-1$

	/**
	 * Decode base64 encoded string
	 *
	 * @param content
	 * @return byte array
	 */
	public static final byte[] decodeBase64(String content) {
		try {
			return Base64.decodeBase64(content
					.getBytes(IGitHubConstants.CHARSET_UTF8));
		} catch (UnsupportedEncodingException e) {
			return Base64.decodeBase64(content.getBytes());
		}
	}

	/**
	 * Decode base64 encoded string
	 *
	 * @param content
	 * @return byte array
	 */
	public static final byte[] encodeBase64(byte[] content) {
		return Base64.encodeBase64(content);
	}

	private String content;

	private String encoding;

	/**
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 * @return this blob
	 */
	public Blob setContent(String content) {
		this.content = content;
		return this;
	}

	/**
	 * @return encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 * @return this blob
	 */
	public Blob setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}
}
