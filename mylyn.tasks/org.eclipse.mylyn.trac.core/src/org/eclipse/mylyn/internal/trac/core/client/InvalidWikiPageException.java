/*******************************************************************************
 * Copyright (c) 2006, 2008 Xiaoyang Guan and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

/**
 * Indicates an error while parsing the page info retrieved from a repository. If the requested page name or version
 * doesn't exist on the repository, the XmlRpcPlugin returns an Integer of 0 without generating any error info.
 * 
 * @author Xiaoyang Guan
 */
public class InvalidWikiPageException extends TracRemoteException {

	private static final long serialVersionUID = 7505355497334178587L;

	public InvalidWikiPageException() {
	}

	public InvalidWikiPageException(String message) {
		super(message);
	}

	public InvalidWikiPageException(Throwable cause) {
		super(cause);
	}

	public InvalidWikiPageException(String message, Throwable cause) {
		super(message, cause);
	}
}
