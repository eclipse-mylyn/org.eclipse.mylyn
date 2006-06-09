/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests.mockconnector;

import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;

/**
 * @author Mik Kersten
 */
public class MockRepositoryTask extends AbstractRepositoryTask {
	
	public MockRepositoryTask(String handle) {
		super(handle, "label for " + handle, true);
	}

	@Override
	public String getRepositoryKind() {
		return "mock";
	}

	@Override
	public boolean isDownloaded() {
		return false;
	}

	@Override
	public boolean isPersistentInWorkspace() {
		return false;
	}

}
