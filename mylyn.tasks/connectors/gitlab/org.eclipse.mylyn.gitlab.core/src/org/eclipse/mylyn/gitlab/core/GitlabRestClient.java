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
 *******************************************************************************/

package org.eclipse.mylyn.gitlab.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.osgi.util.NLS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class GitlabRestClient {

	@SuppressWarnings("restriction")
	private final CommonHttpClient client;
	private final GitlabRepositoryConnector connector;
	private final TaskRepository taskRepository;

	public static String AUTHORIZATION_HEADER = "authorization_header";

	@SuppressWarnings("restriction")
	public GitlabRestClient(RepositoryLocation location, GitlabRepositoryConnector connector,
			TaskRepository taskRepository) {
		super();
		client = new CommonHttpClient(location);
		this.connector = connector;
		this.taskRepository = taskRepository;
	}

	@SuppressWarnings("restriction")
	public RepositoryLocation getLocation() {
		return client.getLocation();
	}

	@SuppressWarnings("restriction")
	public CommonHttpClient getClient() {
		return client;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public IStatus getIssues(IRepositoryQuery query, TaskDataCollector collector, final IOperationMonitor monitor)
			throws GitlabException, CoreException {
		getAccessTokenIfNotPresent(monitor);
//		GitlabConfiguration conf = connector.getRepositoryConfiguration(taskRepository);
		String[] queryProjects = query.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey()).split(",");
		String[] gueryGroups = query.getAttribute(GitlabTaskSchema.getDefault().GROUP.getKey()).split(",");

		if (!queryProjects[0].isEmpty()) {
			for (String string : queryProjects) {
//				String path = "/projects/" + ((JsonObject) conf.getProjcetDetail(string)).get("path_with_namespace")
//						.getAsString().replaceAll("/", "%2F");
				String path = "/projects/" + string.replaceAll("/", "%2F");
				getIssuesInternal(query, collector, path, monitor);
			}
		}
		if (!gueryGroups[0].isEmpty()) {
			for (String string : gueryGroups) {
//				String path = "/groups/" + ((JsonObject) conf.getGroupDetail(string)).get("full_path").getAsString()
//						.replaceAll("/", "%2F");
				String path = "/groups/" + string.replaceAll("/", "%2F");
				getIssuesInternal(query, collector, path, monitor);
			}
		}

		return Status.OK_STATUS;
	}

	private void getIssuesInternal(IRepositoryQuery query, TaskDataCollector collector, String path,
			final IOperationMonitor monitor) throws GitlabException {
		List<TaskData> taskDataArray = new GitlabOperation<List<TaskData>>(client, path + "/issues") {

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				String state = query.getAttribute("STATE");
				String suffix;
				switch (state != null ? state : "") {
				case "opened":
					suffix = "?state=opened";
					break;
				case "closed":
					suffix = "?state=closed";
					break;
				default:
					suffix = "";
				}
				HttpRequestBase request = new HttpGet(url + suffix);
				return request;
			}

			@Override
			protected List<TaskData> parseFromJson(InputStreamReader in) throws GitlabException {

				String text = new BufferedReader(in).lines().collect(Collectors.joining("\n"));
				JsonArray convertedObject = new Gson().fromJson(text, JsonArray.class);
				TypeToken<List<TaskData>> type = new TypeToken<List<TaskData>>() {
				};

				return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataListDeserializer())
						.create().fromJson(text, type.getType());
			}
		}.run(monitor);
		for (final TaskData taskData : taskDataArray) {
			taskData.setPartial(true);
			SafeRunner.run(new ISafeRunnable() {

				@Override
				public void run() throws Exception {
					collector.accept(taskData);
				}

				@Override
				public void handleException(Throwable exception) {
					StatusHandler.log(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
							NLS.bind("Unexpected error during result collection. TaskID {0} in repository {1}", //$NON-NLS-1$
									taskData.getTaskId(), taskData.getRepositoryUrl()),
							exception));
				}
			});
		}
	}

	public String getVersion(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject versionInfo = new GitlabOperation<JsonObject>(client, "/version") {

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return versionInfo.get("version").getAsString();
	}

	public String getVersionAndRevision(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject versionInfo = new GitlabOperation<JsonObject>(client, "/version") {

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return versionInfo.get("version").getAsString() + "(rev: " + versionInfo.get("revision").getAsString() + ")";
	}

	public boolean validate(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		String validate = new GitlabOperation<String>(client, "/version") {

			protected boolean isRepeatable() {
				return false;
			};

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected String parseFromJson(InputStreamReader in) throws GitlabException {
				return new BufferedReader(in).lines().parallel().collect(Collectors.joining("\n"));
			}
		}.run(monitor);
		return validate.length() > 0 && validate.contains("version");
	}

	public JsonObject getMetadata(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/metadata") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	private class JSonTaskDataListDeserializer implements JsonDeserializer<ArrayList<TaskData>> {

		@Override
		public ArrayList<TaskData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			ArrayList<TaskData> response = new ArrayList<TaskData>();
			GitlabConfiguration config =getConfiguration();
			JsonArray ja = json.getAsJsonArray();
			for (JsonElement jsonElement : ja) {
				JsonObject jo = jsonElement.getAsJsonObject();
				TaskData taskData = getFromJson(jo);
				if (config != null)
					config.updateProductOptions(taskData);
				response.add(taskData);
			}
			return response;
		}
	}

	private TaskData getFromJson(JsonObject jo) {
		GitlabTaskDataHandler dataHandler = (GitlabTaskDataHandler) connector.getTaskDataHandler();
		TaskAttributeMapper mapper = dataHandler.getAttributeMapper(taskRepository);

		TaskData response = null;
		String selfString = jo.get("_links").getAsJsonObject().get("self").getAsString();

		TaskData taskData = null;
		taskData = new TaskData(mapper, connector.getConnectorKind(), taskRepository.getRepositoryUrl(), selfString);
		response = taskData;
		try {
			dataHandler.initializeTaskData(taskRepository, taskData, null, null);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		for (Entry<String, JsonElement> entry : jo.entrySet()) {
			String attributeId = GitlabTaskSchema.getAttributeNameFromJsonName(entry.getKey());
			TaskAttribute attribute = taskData.getRoot().getAttribute(attributeId);
			if (attribute == null) {
				PrintStream ps = attribute == null ? System.err : System.out;
				ps.println(entry.getKey() + " -> " + entry.getValue() //
						+ " -> " + attributeId + " -> " + attribute + "\n" //
						+ entry.getValue().isJsonPrimitive() + entry.getValue().isJsonObject()
						+ entry.getValue().isJsonArray());
				ps.close();
			}
			if (attribute != null && entry.getValue() != null && entry.getValue().isJsonPrimitive()) {
				attribute.setValue(entry.getValue().getAsString());
			}
			if (entry.getKey().equals("severity")) {
				attribute.putOption(entry.getValue().getAsString(), entry.getValue().getAsString());
			}
			if (entry.getKey().equals("author") && entry.getValue().isJsonObject()) {
				attribute.setValue(entry.getValue().getAsJsonObject().get("name").getAsString());
			}
		}
		return response;
	}

	private class JSonTaskDataDeserializer implements JsonDeserializer<TaskData> {

		@Override
		public TaskData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			GitlabConfiguration config = getConfiguration();
			JsonObject jo = json.getAsJsonObject();
			TaskData taskData = getFromJson(jo);
			if (config != null)
				config.updateProductOptions(taskData);
			return taskData;
		}

	}

	private GitlabConfiguration getConfiguration() {
		GitlabConfiguration config;
		try {
			config = connector.getRepositoryConfiguration(taskRepository);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			config = null;
		}
		return config;
	}

	private void getAccessTokenIfNotPresent(IOperationMonitor monitor) {
		if (getClientAttribute(AUTHORIZATION_HEADER) == null) {
			try {
				obtainAccessToken(monitor);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("restriction")
	private Object getClientAttribute(String attribute) {
		return getClient().getAttribute(attribute);
	}

	@SuppressWarnings("restriction")
	private void setClientAttribute(String attribute, Object value) {
		getClient().setAttribute(attribute, value);
	}

	public String obtainAccessToken(IOperationMonitor monitor) throws Exception {
		AuthenticationCredentials credentials1 = taskRepository
				.getCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY);
		String username = credentials1.getUserName();
		String password = credentials1.getPassword();
		String repositoryUrl = taskRepository.getRepositoryUrl();

		URL url = new URL(repositoryUrl + "/oauth/token");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.getOutputStream()
				.write(("grant_type=password&username=" + username + "&password=" + password).getBytes());
		connection.connect();

		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			throw new Exception("Failed to obtain access token");
		}

		String response = new String(connection.getInputStream().readAllBytes());
		String accessToken = response.split("\"access_token\":\"")[1].split("\"")[0];
		setClientAttribute(AUTHORIZATION_HEADER, "Bearer " + accessToken);
		return accessToken;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws GitlabException {
		String searchString = ".+(/projects/\\d+)/issues/(\\d+)";
		Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(taskId);
		if (matcher.find()) {
			TaskData result;
//			System.out.println("found: " + matcher.group(0));
//			System.out.println("       " + matcher.group(1));
//			System.out.println("       " + matcher.group(2));
			GitlabConfiguration config = getConfiguration();
			JsonObject iss = getIssue(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
			TypeToken<TaskData> type = new TypeToken<TaskData>() {
			};
			result = new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer()).create()
					.fromJson(iss, type.getType());

			JsonArray notes = getIssueNotes(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
			if (notes != null) {
				for (int i = 0; i < notes.size(); i++) {
					JsonObject note = notes.get(i).getAsJsonObject();
					TaskCommentMapper cmapper = new TaskCommentMapper();
					cmapper.setAuthor(
							repository.createPerson(note.get("author").getAsJsonObject().get("name").getAsString()));
					cmapper.setCreationDate(GitlabTaskAttributeMapper.parseDate(note.get("created_at").getAsString()));
					cmapper.setText(note.get("body").getAsString());
					cmapper.setNumber(i + 1);
					TaskAttribute attribute = result.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + (i + 1));
					cmapper.applyTo(attribute);
				}
			}
			config.updateProductOptions(result);

			return result;
		}
		return null;
	}

	public void getTaskData(Set<String> taskIds, TaskRepository taskRepository, TaskDataCollector collector,
			IOperationMonitor monitor) throws GitlabException {

		for (String taskId : taskIds) {
			TaskData data;
				data = getTaskData(taskRepository, taskId, monitor);
				collector.accept(data);
		}
	}

	public JsonObject getUser(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/user") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getUsers(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/users" + path) {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getNamespaces(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/namespaces") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonObject getGroup(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/groups" + path) {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getSubGroups(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/groups" + path + "/subgroups?all_available=true") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getDescendantGroups(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client,
				"/groups" + path + "/descendant_groups?all_available=true") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonObject getNamespace(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonObject = new GitlabOperation<JsonObject>(client, "/namespaces" + path) {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonObject;
	}

	public JsonArray getProjects(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/projects") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonObject getProject(String projectid, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/projects/" + projectid) {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getGroupProjects(String projectid, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client,
				"/groups/" + projectid + "/projects?include_subgroups=true") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getIssues(String path, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonObject getIssue(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, path + "/issues/" + id) {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public JsonArray getIssueNotes(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/notes?sort=asc") {
			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected JsonArray parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonArray.class);
			}
		}.run(monitor);
		return jsonArray;
	}

	public GitlabConfiguration getConfiguration(TaskRepository repository, IOperationMonitor monitor) {
		GitlabConfiguration config = new GitlabConfiguration(repository.getUrl());
		try {
			JsonObject user = getUser(monitor);
			config.setUserID(user.get("id").getAsBigInteger());
			config.setUserDetails(user);
			JsonElement projects = getProjects("/users/" + config.getUserID(), monitor);
			for (JsonElement project : (JsonArray) projects) {
//				System.out.println(project.getAsJsonObject().get("name_with_namespace").getAsString());
				config.addProject(project);
			}
			String groupsValue = repository.getProperty(GitlabCoreActivator.GROUPS);
			String[] groupList = groupsValue.split(",");
			for (String group : groupList) {
				JsonObject groupDetail = getGroup("/" + group, monitor);
//				System.out.println(groupDetail.getAsJsonObject().get("full_path").getAsString());
				config.addGroup(groupDetail);
				projects = getGroupProjects(group, monitor);
				for (JsonElement project : (JsonArray) projects) {
//					System.out.print(project.getAsJsonObject().get("id").getAsString());
//					System.out.print(" ");
//					System.out.println(project.getAsJsonObject().get("name_with_namespace").getAsString());
					config.addProject(project);
				}
			}
		} catch (GitlabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return config;
	}

//	public String getProjects(IOperationMonitor monitor) throws GitlabException {
//		getAccessTokenIfNotPresent(monitor);
//		Version versionInfo = new GitlabOperation<Version>(client, "/namespaces") {
////			Version versionInfo = new GitlabOperation<Version>(client, "/projects?membership=true") {
////			Version versionInfo = new GitlabOperation<Version>(client, "/projects?owned=true") {
//
//			@Override
//			protected HttpRequestBase createHttpRequestBase(String url) {
//				HttpRequestBase request = new HttpGet(url);
//				return request;
//			}
//
//			@Override
//			protected Version parseFromJson(InputStreamReader in) throws GitlabException {
//				String result = new BufferedReader(in)
//						   .lines().collect(Collectors.joining("\n"));
////				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//				JsonArray convertedObject = new Gson().fromJson(result, JsonArray.class);
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//			      String prettyJson = gson.toJson(convertedObject);
//				TypeToken<Version> type = new TypeToken<Version>() {
//				};
//				return new Gson().fromJson(result, type.getType());
//			}
//		}.run(monitor);
//		return versionInfo.getVersion();
//	}

}
