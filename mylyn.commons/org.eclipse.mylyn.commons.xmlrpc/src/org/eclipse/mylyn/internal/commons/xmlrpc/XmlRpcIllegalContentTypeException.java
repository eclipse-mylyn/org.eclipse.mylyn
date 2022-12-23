/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Indicates the server responded with an unexpected content type.
 * 
 * @author Steffen Pingel
 */
public class XmlRpcIllegalContentTypeException extends XmlRpcException {

	private static final long serialVersionUID = -1844484692848370951L;

	private final String contentType;

	public XmlRpcIllegalContentTypeException(String message, String contentType) {
		super(message);
		this.contentType = contentType;
	}

	/**
	 * Returns the content type specified by the server.
	 */
	public String getContentType() {
		return contentType;
	}

}
