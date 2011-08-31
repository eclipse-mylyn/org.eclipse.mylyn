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
package org.eclipse.egit.github.core;

import java.io.Serializable;

/**
 * GitHub issue label class.
 */
public class Label implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 859851442075061861L;

	private String color;

	private String name;

	private String url;

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		return obj instanceof Label && name != null
				&& name.equals(((Label) obj).name);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

	/**
	 * @return color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @return this label
	 */
	public Label setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return url;
	}
}
