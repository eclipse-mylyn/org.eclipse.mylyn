/*******************************************************************************
 * Copyright (c) 2013, 2014 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Beckers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import org.eclipse.core.runtime.CoreException;

public abstract class BugzillaTestSupportUtil {

	public static boolean isInvalidLogon(CoreException e) {
		return e.getMessage().indexOf("invalid username or password") != -1
				|| e.getMessage().indexOf("invalid login or password") != -1
				|| e.getMessage().indexOf("untrusted authentication request:") != -1
				|| e.getMessage().indexOf("An unknown repository error has occurred: file is empty") != -1
				|| e.getMessage()
				.indexOf(
						"file is empty:  The file you are trying to attach is empty, does not exist, or you don't have permission to read it.") != -1;
	}

}