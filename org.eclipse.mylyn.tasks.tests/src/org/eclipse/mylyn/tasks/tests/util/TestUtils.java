/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.util;

import org.eclipse.core.runtime.Platform;

public class TestUtils {

	private static class CompatibilityAuthChecker {
		public static final boolean result;
		static {
			result = Platform.getBundle("org.eclipse.core.runtime.compatibility.auth") != null; //$NON-NLS-1$
		}
	}

	public static boolean isCompatibilityAuthInstalled() {
		return CompatibilityAuthChecker.result;
	}

}
