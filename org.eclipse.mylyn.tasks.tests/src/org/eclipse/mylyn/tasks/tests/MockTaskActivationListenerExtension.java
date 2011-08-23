/*******************************************************************************
 * Copyright (c) 2011 Manuel Doninger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manuel Doninger - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;

/**
 * A task activation listener that is registered through an extension point.
 * 
 * @author Manuel Doninger
 */
public class MockTaskActivationListenerExtension extends TaskActivationAdapter {

	public static MockTaskActivationListenerExtension INSTANCE;

	public boolean hasActivated = false;

	public boolean hasPreActivated = false;

	public boolean hasDeactivated = false;

	public boolean hasPreDeactivated = false;

	public MockTaskActivationListenerExtension() {
		super();
		INSTANCE = this;
	}

	public void reset() {
		hasActivated = false;
		hasPreActivated = false;

		hasDeactivated = false;
		hasPreDeactivated = false;

	}

	@Override
	public void preTaskActivated(ITask task) {
		hasPreActivated = true;
	}

	@Override
	public void preTaskDeactivated(ITask task) {
		hasPreDeactivated = true;
	}

	@Override
	public void taskActivated(ITask task) {
		hasActivated = true;
	}

	@Override
	public void taskDeactivated(ITask task) {
		hasDeactivated = true;
	}

}