/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
