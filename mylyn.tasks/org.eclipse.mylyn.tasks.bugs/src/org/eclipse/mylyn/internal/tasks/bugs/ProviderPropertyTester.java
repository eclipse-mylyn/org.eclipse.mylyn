/*******************************************************************************
 * Copyright (c) 2024 Frank Becker
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import org.eclipse.core.expressions.PropertyTester;

public final class ProviderPropertyTester extends PropertyTester {

	private static final String ANY_PROVIDER = "anyProvider"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (ANY_PROVIDER.equals(property)) {
			SupportProviderManager providerManager = TasksBugsPlugin.getTaskErrorReporter().getProviderManager();
			return providerManager != null && providerManager.getProviders().size() > 0;
		}
		return false;
	}

}
