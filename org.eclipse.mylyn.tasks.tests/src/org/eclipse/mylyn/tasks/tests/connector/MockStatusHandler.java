/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import junit.framework.Assert;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.monitor.core.util.IStatusHandler;

public class MockStatusHandler implements IStatusHandler {

	private IStatus status;

	public void displayStatus(String title, IStatus status) {
		this.status = status;
	}

	public void fail(IStatus status, boolean informUser) {
		this.status = status;
	}

	public void assertNoStatus() {
		Assert.assertNull("Unexpected error reported through StatusHandler", status);
	}

}
