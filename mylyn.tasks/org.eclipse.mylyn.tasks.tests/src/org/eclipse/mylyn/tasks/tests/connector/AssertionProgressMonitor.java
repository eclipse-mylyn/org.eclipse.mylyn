/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Benjamin Muskalla
 */
public class AssertionProgressMonitor extends NullProgressMonitor {
	private final StringBuilder progressLog = new StringBuilder();

	@Override
	public void beginTask(String name, int totalWork) {
		progressLog.append("beginTask|");
	}

	@Override
	public void done() {
		progressLog.append("done");
	}

	@Override
	public void subTask(String name) {
		progressLog.append("subTask|");
	}

	public String getProgressLog() {
		return progressLog.toString();
	}
}