/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.core;

public interface IOfflineBugListener {

	public enum BugzillaOfflineStaus {
		SAVED, SAVED_WITH_OUTGOING_CHANGES, DELETED, SAVED_WITH_INCOMMING_CHANGES, CONFLICT
	}

	public void offlineStatusChange(IBugzillaBug bug, BugzillaOfflineStaus status);

}
