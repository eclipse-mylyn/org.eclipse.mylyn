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

/**
 * Model class for resources identified bya SHA-1
 */
public class ShaResource {

	private String sha;

	/**
	 * @return sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @param sha
	 * @return this resource
	 */
	public ShaResource setSha(String sha) {
		this.sha = sha;
		return this;
	}
}
