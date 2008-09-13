/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.util.Date;

/**
 * @author Steffen Pingel
 */
public class TracComment {

	private String author;

	private Date created;

	private String field;

	private String newValue;

	private String oldValue;

	private boolean permanent;

	public TracComment() {
	}

	public String getAuthor() {
		return author;
	}

	public Date getCreated() {
		return created;
	}

	public String getField() {
		return field;
	}

	public String getNewValue() {
		return newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public boolean isPermanent() {
		return permanent;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}

	@Override
	public String toString() {
		return "[" + field + "] " + author + ": " + oldValue + " -> " + newValue;
	}

}
