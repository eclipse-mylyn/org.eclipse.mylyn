/*******************************************************************************
 * Copyright (c) 2014, 2015 Ericsson AB and others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Ericsson AB - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client.rest;

import java.util.List;
import java.util.Map;

public class ChangeInfo28 extends ChangeInfo {

	public Boolean starred;

	public Integer insertions;

	public Integer deletions;

	public String currentRevision;

	public Map<String, ActionInfo> actions;

	private List<ChangeMessageInfo> messages;

	public List<ChangeMessageInfo> getMessages() {
		return messages;
	}

	public Map<String, ActionInfo> getActions() {
		return actions;
	}

	public Boolean getStarred() {
		return starred;
	}
}
