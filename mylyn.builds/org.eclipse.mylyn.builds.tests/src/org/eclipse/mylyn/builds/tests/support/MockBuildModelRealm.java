/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.support;

import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;

/**
 * Realm that executes operations synchronously.
 * 
 * @author Steffen Pingel
 */
public class MockBuildModelRealm implements IBuildModelRealm {

	public void exec(Runnable runnable) {
		runnable.run();
	}

	public void asyncExec(Runnable runnable) {
		runnable.run();
	}

	public void syncExec(Runnable runnable) {
		runnable.run();
	}

}
