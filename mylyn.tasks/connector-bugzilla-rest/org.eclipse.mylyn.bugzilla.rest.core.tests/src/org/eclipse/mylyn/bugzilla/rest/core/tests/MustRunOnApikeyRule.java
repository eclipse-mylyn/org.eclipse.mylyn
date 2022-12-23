/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.core.tests;

import org.eclipse.mylyn.bugzilla.rest.test.support.BugzillaRestTestFixture;
import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.ConditionalIgnoreRule;

@SuppressWarnings("restriction")
public class MustRunOnApikeyRule implements ConditionalIgnoreRule.IgnoreCondition {

	@Override
	public boolean isSatisfied(AbstractTestFixture fixture) {
		if (fixture instanceof BugzillaRestTestFixture) {
			return !((BugzillaRestTestFixture) fixture).isApiKeyEnabled();
		} else {
			return false;
		}
	}
}