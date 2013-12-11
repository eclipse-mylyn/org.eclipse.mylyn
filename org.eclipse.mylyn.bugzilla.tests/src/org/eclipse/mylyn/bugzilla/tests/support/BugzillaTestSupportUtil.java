/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Beckers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import org.eclipse.core.runtime.CoreException;

public abstract class BugzillaTestSupportUtil {

	public static boolean isInvalidLogon(CoreException e) {
		return e.getMessage().indexOf("invalid username or password") != -1
				|| e.getMessage().indexOf("invalid login or password") != -1;
	}

}