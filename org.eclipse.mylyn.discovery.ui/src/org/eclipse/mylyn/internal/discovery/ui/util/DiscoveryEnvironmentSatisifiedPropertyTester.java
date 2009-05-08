/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * A property tester that indicates if the current environment satisifies requirements to run discovery.
 * 
 * @author David Green
 */
public class DiscoveryEnvironmentSatisifiedPropertyTester extends PropertyTester {

	public DiscoveryEnvironmentSatisifiedPropertyTester() {
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		Bundle bundle = Platform.getBundle("org.eclipse.equinox.p2.repository"); //$NON-NLS-1$
		if (bundle != null) {
			Version version = bundle.getVersion();
			return version.compareTo(new Version(1, 0, 0)) >= 0;
		}
		return false;
	}

}
