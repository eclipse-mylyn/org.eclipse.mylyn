/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

/**
 * GitHub issue label class.
 *
 * @author Kevin Sawicki (kevin@github.com)
 */
public class Label {

	private String color;

	private String name;

	private String url;

	/**
	 * @return color
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

}
