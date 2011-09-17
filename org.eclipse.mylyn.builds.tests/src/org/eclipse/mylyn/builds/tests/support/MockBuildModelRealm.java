/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
