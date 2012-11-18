/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.core.data;

public class RepositoryConfigurationProductItem extends RepositoryConfigurationSubItem {

	private static final long serialVersionUID = 958830945072017152L;

	private String defaultMilestone = null;

	private Boolean unconfirmedAllowed = false;

	public RepositoryConfigurationProductItem(String name) {
		super(name);
	}

	public String getDefaultMilestone() {
		return defaultMilestone;
	}

	public void setDefaultMilestone(String defaultMilestone) {
		this.defaultMilestone = defaultMilestone;
	}

	public Boolean getUnconfirmedAllowed() {
		return unconfirmedAllowed;
	}

	public void setUnconfirmedAllowed(Boolean unconfirmedAllowed) {
		this.unconfirmedAllowed = unconfirmedAllowed;
	}

}
