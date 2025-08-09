/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.client;

import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class JenkinsValidationTest extends TestCase {

	public void testValidateNonExistantUrl() throws Exception {
		// invalid url
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://non.existant/repository");
		RestfulJenkinsClient client = JenkinsFixture.connect(location);
		try {
			client.validate(OperationUtil.convert(null));
			fail("Expected JenkinsException");
		} catch (JenkinsException e) {
		}
	}

	public void testValidateNonHudsonUrl() throws Exception {
		// non Hudson url
		RepositoryLocation location = new RepositoryLocation();
		location.setUrl("http://eclipse.org/mylyn");
		RestfulJenkinsClient client = JenkinsFixture.connect(location);
		try {
			client.validate(OperationUtil.convert(null));
			fail("Expected JenkinsException");
		} catch (JenkinsException e) {
		}
	}

}
