/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * All tests against a live GitHub instance.
 */
@Suite
@SelectClasses({ CollaboratorTest.class, CommitTest.class, DataTest.class, DeployKeyTest.class, EventTest.class,
	GistTest.class, IssueTest.class, LabelTest.class, LiveTest.class, MilestoneTest.class, OrganizationTest.class,
	PullRequestTest.class, RepositoryTest.class, TeamTest.class, UserTest.class })
public class LiveTests {

}
