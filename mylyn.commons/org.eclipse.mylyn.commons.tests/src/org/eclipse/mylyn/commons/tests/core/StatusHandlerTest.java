/*******************************************************************************
 * Copyright (c) 2014, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class StatusHandlerTest extends TestCase {

	public void testLogDumpsErrorToConsoleInTestMode() throws Exception {
		if (!CoreUtil.TEST_MODE) {
			return;
		}
		PrintStream oldErr = System.err;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setErr(new PrintStream(baos));
		Status status = new Status(IStatus.ERROR, CommonsCorePlugin.ID_PLUGIN, "boom!");

		try {
			StatusHandler.log(status);

			assertTrue(baos.toString()
					.matches("\\[\\d{4}-\\d{2}-\\d{2}T\\d{1,2}-\\d{1,2}-\\d{1,2}\\] " + status.toString() + ",\\s+"));
		} finally {
			baos.close();
			System.setErr(oldErr);
		}

	}

}
