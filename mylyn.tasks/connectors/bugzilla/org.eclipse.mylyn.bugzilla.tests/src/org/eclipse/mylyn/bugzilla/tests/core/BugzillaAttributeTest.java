/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse @Test
	public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class BugzillaAttributeTest {

	@Test
	public void testTaskDataSchema() throws Exception {
		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "http://repository");
		BugzillaAttributeMapper mapper = new BugzillaAttributeMapper(repository, connector);
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");
		BugzillaAttribute[] attributes = BugzillaAttribute.values();
		for (BugzillaAttribute attribute : attributes) {
			TaskAttribute taskDataAttribute = taskData.getRoot().createAttribute(attribute.getKey());
			taskDataAttribute.getMetaData().setReadOnly(attribute.isReadOnly());
			taskDataAttribute.getMetaData().setKind(attribute.getKind());
			taskDataAttribute.getMetaData().setType(attribute.getType());
		}
		assertEquals(IOUtils.toString(CommonTestUtil.getResource(this, "testdata/schema/taskdata.txt"))
				.replace("\r\n", "\n"), taskData.getRoot().toString().replace("\r\n", "\n"));
	}

}