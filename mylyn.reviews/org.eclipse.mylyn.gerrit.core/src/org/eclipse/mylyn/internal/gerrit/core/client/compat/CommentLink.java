/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.compat;

import java.util.Objects;

import org.eclipse.core.runtime.Assert;

/**
 * @author Steffen Pingel
 */
public class CommentLink {

	private String find;

	private String replace;

	public CommentLink(String find, String replace) {
		Assert.isNotNull(find);
		Assert.isNotNull(replace);
		this.find = find;
		this.replace = replace;
	}

	/**
	 * Intended to be used by GSon.
	 */
	protected CommentLink() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		CommentLink other = (CommentLink) obj;
		if (!Objects.equals(find, other.find)) {
			return false;
		}
		if (!Objects.equals(replace, other.replace)) {
			return false;
		}
		return true;
	}

	public String getFind() {
		return find;
	}

	public String getReplace() {
		return replace;
	}

	@Override
	public int hashCode() {
		return Objects.hash(find, replace);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommentLink [find="); //$NON-NLS-1$
		builder.append(find);
		builder.append(", replace="); //$NON-NLS-1$
		builder.append(replace);
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}

}
