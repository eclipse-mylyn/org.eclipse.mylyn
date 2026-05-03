/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.repositories.http.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ CommonHttpClientPreemptiveAuthTest.class, CommonHttpClientTest.class, CommonHttpResponseTest.class,
		HttpUtilTest.class })
public class AllRepositoriesHttpTests {
}