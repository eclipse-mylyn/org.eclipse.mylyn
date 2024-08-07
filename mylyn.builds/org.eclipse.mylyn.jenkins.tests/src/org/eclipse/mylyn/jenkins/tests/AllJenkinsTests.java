/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests;

import org.eclipse.mylyn.jenkins.tests.client.JenkinsClientTest;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsUrlTest;
import org.eclipse.mylyn.jenkins.tests.client.JenkinsValidationTest;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsConnectorTest;
import org.eclipse.mylyn.jenkins.tests.core.JenkinsServerBehaviourTest;
import org.eclipse.mylyn.jenkins.tests.integration.JenkinsIntegrationTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Jenkins Test Suite")
@SelectClasses({ SuiteSetup.class, JenkinsConnectorTest.class, JenkinsServerBehaviourTest.class, JenkinsUrlTest.class,
	JenkinsValidationTest.class, JenkinsClientTest.class, JenkinsIntegrationTest.class })
public class AllJenkinsTests {

}
