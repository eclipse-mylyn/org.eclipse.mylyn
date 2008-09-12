/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Mik Kersten
 */
public class ManualUiTest extends AbstractManualTest {

	public void testErrorDialog() {
		try {
			int i = 10 / 0;
			System.out.println(i);
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, "org.eclipse.mylyn", "whoops", t));
		}
		StatusHandler.fail(new Status(IStatus.ERROR, "org.eclipse.mylyn", "whoops"));

		assertTrue(confirmWithUser("Did an error dialog show up correctly?"));
	}

}
