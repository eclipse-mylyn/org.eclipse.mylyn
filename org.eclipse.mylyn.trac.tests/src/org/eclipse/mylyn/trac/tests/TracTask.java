/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;

/**
 * @author Steffen Pingel
 */
@Deprecated
public class TracTask extends AbstractTask {
	private boolean supportsSubtasks = false;

	public TracTask(String repositoryUrl, String id, String label) {
		super(repositoryUrl, id, label);
		setUrl(repositoryUrl + ITracClient.TICKET_URL + id);
	}

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	public boolean getSupportsSubtasks() {
		return supportsSubtasks;
	}

	public void setSupportsSubtasks(boolean supportsSubtasks) {
		this.supportsSubtasks = supportsSubtasks;
	}

}
