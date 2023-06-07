package org.eclipse.mylyn.gitlab.core;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GitlabConfiguration implements Serializable {

	private static final long serialVersionUID = -6859757478504901423L;
	
	private static final GitlabTaskSchema SCHEMA = GitlabTaskSchema.getDefault();
	private final String repositoryURL;
	private BigInteger userID;
	private JsonElement userDetails;
	private Map<String, JsonElement> projectDetailsMap = new HashMap<>();
	private Map<Integer, JsonElement> projectIDsMap = new HashMap<>();
	private ArrayList<JsonElement> projects = new ArrayList<>();
	private Map<String, JsonElement> groupsDetailsMap = new HashMap<>();
	private ArrayList<JsonElement> groups = new ArrayList<>();

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

	public ArrayList<JsonElement> getProjects() {
		return projects;
	}

	public void addProject(JsonElement project) {
		projects.add(project);
		projectDetailsMap.put(project.getAsJsonObject().get("name_with_namespace").getAsString(), project);
		projectIDsMap.put(project.getAsJsonObject().get("id").getAsInt(), project);
	}

	public JsonElement getProjcetDetail(String project) {
		return projectDetailsMap.get(project);
	}

	public JsonElement getProjcetDetailFromNumber(Integer id) {
		return projectIDsMap.get(id);
	}

	public JsonElement getGroupDetail(String project) {
		return groupsDetailsMap.get(project);
	}

	public Set<String> getProjectNames() {
		return projectDetailsMap.keySet();
	}

	public Set<String> getGroupNames() {
		return groupsDetailsMap.keySet();
	}

	public ArrayList<JsonElement> getGroups() {
		return groups;
	}

	public void addGroup(JsonElement group) {
		groups.add(group);
		groupsDetailsMap.put(group.getAsJsonObject().get("full_path").getAsString(), group);

	}

	public boolean updateProductOptions(@NonNull TaskData taskData) {
		if (taskData == null) {
			return false;
		}
		TaskAttribute attributeProduct = taskData.getRoot().getMappedAttribute(SCHEMA.PRODUCT.getKey());
		if (attributeProduct == null) {
			return false;
		}
		for (JsonElement product : projects) {
			JsonObject productObject = (JsonObject) product;
			attributeProduct.putOption(productObject.get("id").getAsString(), productObject.get("name_with_namespace").getAsString());
		}
		TaskAttribute attributeGroups = taskData.getRoot().getMappedAttribute(SCHEMA.GROUP.getKey());
		if (attributeGroups == null) {
			return false;
		}
		Set<String> groups = getGroupNames();
		for (String string : groups) {
			attributeGroups.putOption(string, string);
		}
		TaskAttribute stateAttrib = taskData.getRoot().getMappedAttribute("STATE");
		if (stateAttrib!=null) {
			stateAttrib.putOption("opened", "Opened");
			stateAttrib.putOption("closed", "Closed");
			stateAttrib.putOption("", "All");
		}
		return true;
	}

	public String getGroupID(String path) {
		Optional<Object> groupId = Optional.ofNullable(groupsDetailsMap.get(path))
				.map(ma -> ma.getAsJsonObject().get("id").getAsString());
		if (groupId.isPresent()) {
			return (String) groupId.get();
		}
		return "";
	}
}
