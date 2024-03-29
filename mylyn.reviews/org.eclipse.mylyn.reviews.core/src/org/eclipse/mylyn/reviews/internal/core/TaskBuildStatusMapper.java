/*******************************************************************************
 * Copyright (c) 2015, 2023 Vaughan Hilts and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   See git history
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.collections4.MultiValuedMap;
import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.core.collections.LinkedHashMappArrayListValuedHashMap;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class TaskBuildStatusMapper {
	public static final String BUILD_RESULT_TYPE = "BuildResult"; //$NON-NLS-1$

	public static final String JOB_NAME_ATTRIBUTE_KEY = "JOB"; //$NON-NLS-1$

	public static final String BUILD_NUMBER_ATTRIBUTE_KEY = "NUMBER"; //$NON-NLS-1$

	public static final String STATUS_ATTRIBUTE_KEY = "STATUS"; //$NON-NLS-1$

	public static final String URL_ATTRIBUTE_KEY = "URL"; //$NON-NLS-1$

	public static final String ATTR_ID_BUILD_RESULT = "BUILD_RESULT-"; //$NON-NLS-1$

	public static final String ATTR_TYPE_PATCH_SET = "PATCH_SET-"; //$NON-NLS-1$

	public static final String KIND_PATCH_SET = "review.patch.set"; //$NON-NLS-1$

	private final Collection<BuildResult> buildResults;

	public TaskBuildStatusMapper(Collection<BuildResult> buildResults) {
		this.buildResults = buildResults;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);

		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		taskAttribute.getMetaData().defaults().setType(BUILD_RESULT_TYPE).setKind(TaskBuildStatusMapper.KIND_PATCH_SET);

		MultiValuedMap<String, BuildResult> buildsByJobName = new LinkedHashMappArrayListValuedHashMap<>();
		buildResults.forEach(result -> buildsByJobName.put(result.getJobName(), result));

		int i = 0;
		for (Entry<String, Collection<BuildResult>> jobEntry : buildsByJobName.asMap().entrySet()) {

			// We'll take the biggest entry build number and use that for mapping right now
			BuildResult result = Collections.max(jobEntry.getValue(), Comparator.comparing(BuildResult::getBuildNumber));

			TaskAttribute buildAttribute = taskAttribute.createAttribute(ATTR_ID_BUILD_RESULT + i);
			buildAttribute.getMetaData().defaults().setType(BUILD_RESULT_TYPE);

			if (result.getBuildUrl() != null) {
				TaskAttribute child = buildAttribute.createAttribute(URL_ATTRIBUTE_KEY);
				child.getMetaData().defaults().setType(BUILD_RESULT_TYPE);
				mapper.setValue(child, result.getBuildUrl());
			}

			if (result.getBuildStatus() != null) {
				TaskAttribute child = buildAttribute.createAttribute(STATUS_ATTRIBUTE_KEY);
				child.getMetaData().defaults().setType(BUILD_RESULT_TYPE);
				mapper.setValue(child, result.getBuildStatus().toString());
			}

			if (result.getBuildNumber() > -1) {
				TaskAttribute child = buildAttribute.createAttribute(BUILD_NUMBER_ATTRIBUTE_KEY);
				child.getMetaData().defaults().setType(BUILD_RESULT_TYPE);
				mapper.setValue(child, String.valueOf(result.getBuildNumber()));
			}

			if (result.getJobName() != null) {
				TaskAttribute child = buildAttribute.createAttribute(JOB_NAME_ATTRIBUTE_KEY);
				child.getMetaData().defaults().setType(BUILD_RESULT_TYPE);
				mapper.setValue(child, result.getJobName());
			}
			buildAttribute.setValue(hashChildAttributeValues(buildAttribute));
			i++;
		}
		taskAttribute.setValue(hashChildAttributeValues(taskAttribute));
	}

	private String hashChildAttributeValues(TaskAttribute attribute) {
		Object[] childValues = attribute.getAttributes().values().stream().map(toValue()).toArray(Object[]::new);

		return Integer.toString(Objects.hash(childValues));
	}

	private static Function<TaskAttribute, Object> toValue() {
		return TaskAttribute::getValue;
	}

}
