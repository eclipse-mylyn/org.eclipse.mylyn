/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.tests;

import org.eclipse.mylyn.github.internal.GistServiceTest;
import org.eclipse.mylyn.github.internal.GitHubClientTest;
import org.eclipse.mylyn.github.internal.IssueServiceTest;
import org.eclipse.mylyn.github.internal.LabelServiceTest;
import org.eclipse.mylyn.github.internal.MilestoneServiceTest;
import org.eclipse.mylyn.github.internal.PullRequestServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ GitHubClientTest.class, IssueServiceTest.class,
		LabelServiceTest.class, MilestoneServiceTest.class,
		GistServiceTest.class, PullRequestServiceTest.class })
public class AllHeadlessTests {

}
