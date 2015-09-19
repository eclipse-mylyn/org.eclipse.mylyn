/*******************************************************************************
 * Copyright (c) 2015 Vaughan Hilts and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Vaughan Hilts - 	   Initial implementation
 *     Kyle Ross 	 - 	   Initial implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.internal.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class TaskBuildStatusMapper {
	public static final String BUILD_RESULT_TYPE = "BuildResult";

	public static final String JOB_NAME_ATTRIBUTE_KEY = "JOB"; //$NON-NLS-1$

	public static final String BUILD_NUMBER_ATTRIBUTE_KEY = "NUMBER"; //$NON-NLS-1$

	public static final String STATUS_ATTRIBUTE_KEY = "STATUS"; //$NON-NLS-1$

	public static final String URL_ATTRIBUTE_KEY = "URL"; //$NON-NLS-1$

	public static final String ATTR_ID_BUILD_RESULT = "BUILD_RESULT-"; //$NON-NLS-1$

	public static final String ATTR_TYPE_PATCH_SET = "PATCH_SET-";

	private final Iterable<BuildResult> buildResults;

	public TaskBuildStatusMapper(Collection<BuildResult> buildResults) {
		this.buildResults = buildResults;
	}

	public void applyTo(TaskAttribute taskAttribute) {
		Assert.isNotNull(taskAttribute);

		TaskData taskData = taskAttribute.getTaskData();
		TaskAttributeMapper mapper = taskData.getAttributeMapper();
		taskAttribute.getMetaData().defaults().setType(BUILD_RESULT_TYPE);

		Function<BuildResult, String> groupFunction = new Function<BuildResult, String>() {
			@Override
			public String apply(BuildResult source) {
				return source.getJobName();
			}

		};
		final Multimap<String, BuildResult> buildsByJobName = Multimaps.index(this.buildResults, groupFunction);

		int i = 0;
		for (Entry<String, Collection<BuildResult>> jobEntry : buildsByJobName.asMap().entrySet()) {

			// We'll take the biggest entry build number and use that for mapping right now
			BuildResult result = Collections.max(jobEntry.getValue(), new Comparator<BuildResult>() {
				@Override
				public int compare(BuildResult o1, BuildResult o2) {
					return Integer.compare(o1.getBuildNumber(), o2.getBuildNumber());
				}
			});

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

			i++;
		}

	}

}
