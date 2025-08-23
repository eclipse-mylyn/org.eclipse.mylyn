/*******************************************************************************
 * Copyright (c) 2024 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util.junit4;

import org.eclipse.mylyn.commons.sdk.util.AbstractTestFixture;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;

public class MustRunOnCIServerRule implements ConditionalIgnoreRule.IgnoreCondition {

	@Override
	public boolean isSatisfied(AbstractTestFixture fixture) {
		return !CommonTestUtil.runOnCIServerTestsOnly();
	}

}