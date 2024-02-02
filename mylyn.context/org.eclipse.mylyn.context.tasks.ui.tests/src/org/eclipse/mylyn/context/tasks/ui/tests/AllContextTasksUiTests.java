/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.ui.tests;

import org.eclipse.mylyn.internal.context.tasks.ui.ContextTaskActivationListenerTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllContextTasksUiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllContextTasksUiTests.class.getName());
		suite.addTestSuite(ContextTaskActivationListenerTest.class);
		return suite;
	}

}
