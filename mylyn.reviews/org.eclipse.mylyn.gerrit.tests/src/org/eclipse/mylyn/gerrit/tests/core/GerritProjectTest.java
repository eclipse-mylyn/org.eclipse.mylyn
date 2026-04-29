/*******************************************************************************
 * Copyright (c) 2026 george
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core;

import org.eclipse.mylyn.gerrit.tests.AbstractGerritFixtureTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
@Disabled("No gerrit instance available")
public class GerritProjectTest extends AbstractGerritFixtureTest {

	@Test
	public void testCommitAndPushFile() throws Exception {
		GerritProject project = new GerritProject(fixture);
		try {
			project.commitAndPushFile("test");
		} finally {
			System.err.println(project.getFolder());
		}
	}

}
