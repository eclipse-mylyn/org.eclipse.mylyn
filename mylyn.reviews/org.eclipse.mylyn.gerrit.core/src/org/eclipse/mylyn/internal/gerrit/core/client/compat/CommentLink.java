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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CommentLink other = (CommentLink) obj;
		if (find == null) {
			if (other.find != null) {
				return false;
			}
		} else if (!find.equals(other.find)) {
			return false;
		}
		if (replace == null) {
			if (other.replace != null) {
				return false;
			}
		} else if (!replace.equals(other.replace)) {
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((find == null) ? 0 : find.hashCode());
		result = prime * result + ((replace == null) ? 0 : replace.hashCode());
		return result;
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
