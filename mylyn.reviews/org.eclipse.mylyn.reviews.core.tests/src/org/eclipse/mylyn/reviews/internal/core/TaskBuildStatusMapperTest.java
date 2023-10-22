/*******************************************************************************
 * Copyright (c) 2015, 2016 Vaughan Hilts and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Vaughan Hilts - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.internal.core.BuildResult.BuildStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;

public class TaskBuildStatusMapperTest {

	@Test
	public void attributeValueDependsOnChildren() {
		TaskAttribute attribute1 = createBuildAttribute(
				"https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-tasks/1066/");
		TaskAttribute attribute2 = createBuildAttribute(
				"https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-tasks/1067/");
		assertFalse(attribute1.getValue().equals(attribute2.getValue()));
	}

	private TaskAttribute createBuildAttribute(String url) {
		List<BuildResult> results = new ArrayList<BuildResult>();
		final String JOB_NAME = "SameJob";
		results.add(new BuildResult(0, url, BuildStatus.STARTED, 1, JOB_NAME));
		TaskBuildStatusMapper mapper = new TaskBuildStatusMapper(results);
		TaskAttribute root = createRootAttribute();
		mapper.applyTo(root);
		return root;
	}

	@Test
	public void emptyCollectionReturnsRootWithNoChildren() {
		List<BuildResult> results = new ArrayList<BuildResult>();
		TaskBuildStatusMapper mapper = new TaskBuildStatusMapper(results);
		TaskAttribute root = createRootAttribute();

		mapper.applyTo(root);
		assertEquals(0, root.getAttributes().size());
	}

	@Test
	public void collectionWithSameJobNameShouldTakeHighestJobNumber() throws Exception {
		List<BuildResult> results = new ArrayList<BuildResult>();

		final String HUDSON_BUILD_URL = "https://hudson.eclipse.org/mylyn/job/gerrit-mylyn-tasks/1066/";
		final String JOB_NAME = "SameJob";

		results.add(new BuildResult(0, HUDSON_BUILD_URL, BuildStatus.STARTED, 1, JOB_NAME));
		results.add(new BuildResult(1, HUDSON_BUILD_URL, BuildStatus.STARTED, 1, JOB_NAME));

		TaskBuildStatusMapper mapper = new TaskBuildStatusMapper(results);
		TaskAttribute root = createRootAttribute();

		mapper.applyTo(root);

		assertEquals(1, root.getAttributes().size());
		assertNotNull(root.getAttribute("BUILD_RESULT-0"));

		TaskAttribute taskAttribute = root.getAttribute("BUILD_RESULT-0");
		int buildNumber = Integer.parseInt(taskAttribute.getAttribute("NUMBER").getValue());
		assertEquals(1, buildNumber);

		assertEquals(JOB_NAME, taskAttribute.getAttribute("JOB").getValue());
		assertEquals(HUDSON_BUILD_URL, taskAttribute.getAttribute("URL").getValue());
	}

	@Test
	// FIXME Assumes a specific order returned by mapper.applyTo()
	public void collectionWithDifferentJobNamesProducesUnqiueEntries() {
		List<BuildResult> results = new ArrayList<BuildResult>();

		String buildUrl = "http://hudson.someurl.com/";
		results.add(new BuildResult(0, buildUrl, BuildStatus.STARTED, 1, "SameJob"));
		String buildUrl2 = "http://hudson.someurl.com/alpha";
		results.add(new BuildResult(1, buildUrl2, BuildStatus.STARTED, 1, "DifferentJob"));

		TaskBuildStatusMapper mapper = new TaskBuildStatusMapper(results);
		TaskAttribute root = createRootAttribute();

		mapper.applyTo(root);

		assertEquals(2, root.getAttributes().size());

		TaskAttribute firstBuildResultAttribute = root.getAttribute(TaskBuildStatusMapper.ATTR_ID_BUILD_RESULT + "0");
		assertNotNull(firstBuildResultAttribute);

		assertEquals("0",
				firstBuildResultAttribute.getAttribute(TaskBuildStatusMapper.BUILD_NUMBER_ATTRIBUTE_KEY).getValue());
		assertEquals("SameJob",
				firstBuildResultAttribute.getAttribute(TaskBuildStatusMapper.JOB_NAME_ATTRIBUTE_KEY).getValue());
		assertEquals(buildUrl,
				firstBuildResultAttribute.getAttribute(TaskBuildStatusMapper.URL_ATTRIBUTE_KEY).getValue());
		assertEquals(BuildStatus.STARTED.toString(),
				firstBuildResultAttribute.getAttribute(TaskBuildStatusMapper.STATUS_ATTRIBUTE_KEY).getValue());

		TaskAttribute secondBuildResultAttribute = root.getAttribute(TaskBuildStatusMapper.ATTR_ID_BUILD_RESULT + "1");
		assertNotNull(secondBuildResultAttribute);
		assertEquals("1",
				secondBuildResultAttribute.getAttribute(TaskBuildStatusMapper.BUILD_NUMBER_ATTRIBUTE_KEY).getValue());
		assertEquals("DifferentJob",
				secondBuildResultAttribute.getAttribute(TaskBuildStatusMapper.JOB_NAME_ATTRIBUTE_KEY).getValue());
		assertEquals(buildUrl2,
				secondBuildResultAttribute.getAttribute(TaskBuildStatusMapper.URL_ATTRIBUTE_KEY).getValue());
		assertEquals(BuildStatus.STARTED.toString(),
				secondBuildResultAttribute.getAttribute(TaskBuildStatusMapper.STATUS_ATTRIBUTE_KEY).getValue());

	}

	private TaskAttribute createRootAttribute() {
		TaskAttributeMapper mapper = new TaskAttributeMapper(new TaskRepository("", ""));

		TaskData mockTestData = new TaskData(mapper, "", "", "");

		return mockTestData.getRoot();
	}

}
