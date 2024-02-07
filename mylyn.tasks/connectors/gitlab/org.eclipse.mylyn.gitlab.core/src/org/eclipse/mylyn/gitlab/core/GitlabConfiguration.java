/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.gitlab.core;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.internal.gitlab.core.GitlabTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GitlabConfiguration implements Serializable {

	private static final long serialVersionUID = 3705325855842441295L;

	private class GitlabProjectDetail {
		private JsonObject project;

		private Map<String, JsonElement> labelsHash = new HashMap<>();

		private Map<String, JsonElement> milestonesHash = new HashMap<>();

		public GitlabProjectDetail(JsonObject project, JsonArray labels, JsonArray milestones) {
			this.project = project;
			for (JsonElement jsonElement : labels) {
				JsonObject jsonObject = (JsonObject) jsonElement;
				labelsHash.put(jsonObject.get("name").getAsString(), jsonObject); //$NON-NLS-1$
			}
			for (JsonElement jsonElement : milestones) {
				JsonObject jsonObject = (JsonObject) jsonElement;
				milestonesHash.put(jsonObject.get("id").getAsString(), jsonObject); //$NON-NLS-1$
			}
		}

		public JsonObject getProject() {
			return project;
		}

		public Set<String> getLabelNames() {
			return labelsHash.keySet();
		}

		public Map<String, JsonElement> getMilestones() {
			return milestonesHash;
		}

	}

	private static final GitlabTaskSchema SCHEMA = GitlabTaskSchema.getDefault();

	private final String repositoryURL;

	private BigInteger userID;

	private JsonElement userDetails;

	private Map<Integer, GitlabProjectDetail> projectIDsMap = new HashMap<>();

	private Map<String, JsonElement> groupsDetailsMap = new HashMap<>();

	private List<JsonElement> groups = new ArrayList<>();

	public GitlabConfiguration(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public String getRepositoryURL() {
		return repositoryURL;
	}

	public BigInteger getUserID() {
		return userID;
	}

	public void setUserID(BigInteger userID) {
		this.userID = userID;
	}

	public JsonElement getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(JsonElement userDetails) {
		this.userDetails = userDetails;
	}

	public void addProject(JsonElement project, JsonElement lables, JsonElement milestones) {
		GitlabProjectDetail gitlabProject = new GitlabProjectDetail(project.getAsJsonObject(), lables.getAsJsonArray(),
				milestones.getAsJsonArray());
		projectIDsMap.put(project.getAsJsonObject().get("id").getAsInt(), gitlabProject); //$NON-NLS-1$
	}

	public Set<Integer> getProjectIDs() {
		return projectIDsMap.keySet();
	}

	public JsonElement getGroupDetail(String group) {
		return groupsDetailsMap.get(group);
	}

	public Set<String> getGroupNames() {
		return groupsDetailsMap.keySet();
	}

	public void addGroup(JsonElement group) {
		groups.add(group);
		groupsDetailsMap.put(group.getAsJsonObject().get("full_path").getAsString(), group); //$NON-NLS-1$

	}

	public boolean updateProductOptions(@NonNull TaskData taskData) {
		if (taskData == null) {
			return false;
		}
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		TaskAttribute attributeLabels = taskData.getRoot().getMappedAttribute(SCHEMA.TASK_LABELS.getKey());
		TaskAttribute attributeMilestone = taskData.getRoot().getMappedAttribute(SCHEMA.TASK_MILESTONE.getKey());

		if (attributeProduct == null) {
			return false;
		}
		for (GitlabProjectDetail product : projectIDsMap.values()) {
			JsonObject productObject = product.getProject();
			if (attributeProduct != null) {
				attributeProduct.putOption(productObject.get("id").getAsString(), //$NON-NLS-1$
						productObject.get("name_with_namespace").getAsString()); //$NON-NLS-1$
			}
		}
		if (attributeProduct.getValue() != null && !attributeProduct.getValue().isBlank()) {
			for (String label : projectIDsMap.get(Integer.parseInt(attributeProduct.getValue())).getLabelNames()) {
				attributeLabels.putOption(label, label);
			}
		}
		if (attributeProduct.getValue() != null && !attributeProduct.getValue().isBlank()) {
			for (Entry<String, JsonElement> milestone : projectIDsMap.get(Integer.parseInt(attributeProduct.getValue()))
					.getMilestones()
					.entrySet()) {
				attributeMilestone.putOption(milestone.getKey(),
						milestone.getValue().getAsJsonObject().get("title").getAsString()); //$NON-NLS-1$
			}
		}

		TaskAttribute priorityAttrib = taskData.getRoot().getMappedAttribute(SCHEMA.PRIORITY.getKey());
		if (priorityAttrib != null) {
			priorityAttrib.putOption("CRITICAL", "critical"); //$NON-NLS-1$ //$NON-NLS-2$
			priorityAttrib.putOption("HIGH", "high");  //$NON-NLS-1$ //$NON-NLS-2$
			priorityAttrib.putOption("MEDIUM", "Medium");  //$NON-NLS-1$ //$NON-NLS-2$
			priorityAttrib.putOption("LOW", "low");  //$NON-NLS-1$ //$NON-NLS-2$
			priorityAttrib.putOption("UNKNOWN", "unknown");  //$NON-NLS-1$ //$NON-NLS-2$
		}

		TaskAttribute typeAttrib = taskData.getRoot().getMappedAttribute(SCHEMA.ISSUE_TYPE.getKey());
		if (typeAttrib != null) {
			typeAttrib.putOption("issue", "Issue");  //$NON-NLS-1$//$NON-NLS-2$ 
			typeAttrib.putOption("incident", "Incident");  //$NON-NLS-1$//$NON-NLS-2$ 
		}

		return true;
	}

	public boolean updateQueryOptions(@NonNull TaskData taskData) {
		if (taskData == null) {
			return false;
		}

		TaskAttribute attributeGroups = taskData.getRoot().getMappedAttribute("GROUP"); //$NON-NLS-1$
		if (attributeGroups != null) {
			Set<String> groups = getGroupNames();
			for (String string : groups) {
				attributeGroups.putOption(string, string);
			}
		}
		TaskAttribute stateAttrib = taskData.getRoot().getMappedAttribute("STATE"); //$NON-NLS-1$
		if (stateAttrib != null) {
			stateAttrib.putOption("opened", "Opened"); //$NON-NLS-1$ //$NON-NLS-2$
			stateAttrib.putOption("closed", "Closed"); //$NON-NLS-1$ //$NON-NLS-2$
			stateAttrib.putOption("", "All"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TaskAttribute searchinAttrib = taskData.getRoot().getMappedAttribute("SEARCH_IN"); //$NON-NLS-1$
		if (searchinAttrib != null) {
			searchinAttrib.putOption("title", "Title"); //$NON-NLS-1$ //$NON-NLS-2$
			searchinAttrib.putOption("description", "Description"); //$NON-NLS-1$ //$NON-NLS-2$
			searchinAttrib.putOption("", "Title and Description"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return updateProductOptions(taskData);
	}

	public String getGroupID(String path) {
		Optional<Object> groupId = Optional.ofNullable(groupsDetailsMap.get(path))
				.map(ma -> ma.getAsJsonObject().get("id").getAsString()); //$NON-NLS-1$
		if (groupId.isPresent()) {
			return (String) groupId.get();
		}
		return ""; //$NON-NLS-1$
	}

	public void addValidOperations(TaskData bugReport) {
		TaskAttribute attributeStatus = bugReport.getRoot().getMappedAttribute(TaskAttribute.STATUS);
		String attributeStatusValue = attributeStatus.getValue();
		TaskAttribute operationAttribute = bugReport.getRoot().getAttribute(TaskAttribute.OPERATION);
		if (operationAttribute == null) {
			operationAttribute = bugReport.getRoot().createAttribute(TaskAttribute.OPERATION);
		}
		TaskOperation.applyTo(operationAttribute, attributeStatusValue, attributeStatusValue);

		TaskAttribute attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + "opened"); //$NON-NLS-1$
		TaskOperation.applyTo(attribute, attributeStatusValue, attributeStatusValue);

		attribute = bugReport.getRoot().createAttribute(TaskAttribute.PREFIX_OPERATION + "closed"); //$NON-NLS-1$
		if (attributeStatusValue.equals("closed")) { //$NON-NLS-1$
			TaskOperation.applyTo(attribute, "reopen", "Reopen"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			TaskOperation.applyTo(attribute, "close", "Close"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
