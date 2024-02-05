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

package org.eclipse.mylyn.internal.gitlab.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
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
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.gitlab.core.GitlabConfiguration;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator.ActivityType;
import org.eclipse.mylyn.gitlab.core.GitlabException;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
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
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class GitlabRestClient {

	@SuppressWarnings("restriction")
	private final CommonHttpClient client;

	private final GitlabRepositoryConnector connector;

	private final TaskRepository taskRepository;

	private static final Pattern linkPattern = Pattern.compile("\\[(.+)\\]\\((.+)\\)"); //$NON-NLS-1$

	public static String AUTHORIZATION_HEADER = "authorization_header"; //$NON-NLS-1$

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
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(GitlabCoreActivator.REST_CLIENT,
					query.toString() + " " + query.getSummary()); //$NON-NLS-1$
		}
		getAccessTokenIfNotPresent(monitor);
		String[] queryProjects = query.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey()).split(","); //$NON-NLS-1$
		String groupAttribute = query.getAttribute("group"); //$NON-NLS-1$

		if (!queryProjects[0].isEmpty()) {
			for (String string : queryProjects) {
				String path = "/projects/" + string.replaceAll("/", "%2F"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				getIssuesInternal(query, collector, path, monitor);
			}
		}
		if (groupAttribute != null) {
			String[] gueryGroups = groupAttribute.split(","); //$NON-NLS-1$
			if (!gueryGroups[0].isEmpty()) {
				for (String string : gueryGroups) {
					String path = "/groups/" + string.replaceAll("/", "%2F"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					getIssuesInternal(query, collector, path, monitor);
				}
			}
		}
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceExit(GitlabCoreActivator.REST_CLIENT);
		}
		return Status.OK_STATUS;
	}

	private void getIssuesInternal(IRepositoryQuery query, TaskDataCollector collector, String path,
			final IOperationMonitor monitor) throws GitlabException {
		List<TaskData> taskDataArray = new GitlabOperation<List<TaskData>>(client, path + "/issues") { //$NON-NLS-1$

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				String state = query.getAttribute("STATE"); //$NON-NLS-1$
				ArrayList<String> suffix = new ArrayList<>();
				switch (state != null ? state : "") { //$NON-NLS-1$
					case "opened": //$NON-NLS-1$
						suffix.add("state=opened"); //$NON-NLS-1$
						break;
					case "closed": //$NON-NLS-1$
						suffix.add("state=closed"); //$NON-NLS-1$
						break;
					default:
				}
				String search = query.getAttribute("SEARCH"); //$NON-NLS-1$
				if (search != null && !search.isBlank()) {
					try {
						suffix.add("search=" + URLEncoder.encode(search, StandardCharsets.UTF_8.toString())); //$NON-NLS-1$
						String searchIn = query.getAttribute("SEARCH_IN"); //$NON-NLS-1$
						if (searchIn != null && !searchIn.isBlank()) {
							suffix.add("in=" + searchIn); //$NON-NLS-1$
						}
					} catch (UnsupportedEncodingException e) {
						// ignore if we not can encode the search value
						e.printStackTrace();
					}
				}
				if (Boolean.valueOf(query.getAttribute("CONFIDENTIAL"))) { //$NON-NLS-1$
					suffix.add("confidential=true"); //$NON-NLS-1$
				}
				if (Boolean.valueOf(query.getAttribute("ASSIGNED_TO_ME"))) { //$NON-NLS-1$
					suffix.add("scope=assigned_to_me"); //$NON-NLS-1$
				}

				String suffixStr = suffix.size() > 0 ? "?" + String.join("&", suffix) : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				HttpRequestBase request = new HttpGet(url + suffixStr);
				return request;
			}

			private String nextPage(Header[] linkHeader) {
				if (linkHeader.length > 0) {
					Header firstLinkHeader = linkHeader[0];
					for (String linkHeaderEntry : firstLinkHeader.getValue().split(", ")) { //$NON-NLS-1$
						String[] linkHeaderElements = linkHeaderEntry.split("; "); //$NON-NLS-1$
						if ("rel=\"next\"".equals(linkHeaderElements[1])) { //$NON-NLS-1$
							return linkHeaderElements[0].substring(1, linkHeaderElements[0].length() - 1);
						}
					}
				}

				return null;
			}

			@Override
			protected java.util.List<TaskData> execute(IOperationMonitor monitor) throws IOException, GitlabException {
				List<TaskData> result = null;
				HttpRequestBase request = createHttpRequestBase();
				addHttpRequestEntities(request);
				CommonHttpResponse response = execute(request, monitor);
				result = processAndRelease(response, monitor);
				Header[] linkHeader = response.getResponse().getHeaders("Link"); //$NON-NLS-1$
				String nextPageValue = nextPage(linkHeader);
				while (nextPageValue != null) {
					HttpRequestBase looprequest = new HttpGet(nextPageValue);
					addHttpRequestEntities(looprequest);
					CommonHttpResponse loopresponse = execute(looprequest, monitor);
					List<TaskData> loopresult = processAndRelease(loopresponse, monitor);
					result.addAll(loopresult);
					linkHeader = loopresponse.getResponse().getHeaders("Link"); //$NON-NLS-1$
					nextPageValue = nextPage(linkHeader);
				}
				return result;
			}

			@Override
			protected List<TaskData> parseFromJson(InputStreamReader in) throws GitlabException {

				TypeToken<List<TaskData>> type = new TypeToken<>() {
				};

				return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataListDeserializer())
						.create()
						.fromJson(in, type.getType());
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
		JsonObject versionInfo = new GitlabOperation<JsonObject>(client, "/version") { //$NON-NLS-1$

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
		return versionInfo.get("version").getAsString(); //$NON-NLS-1$
	}

	public String getVersionAndRevision(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject versionInfo = new GitlabOperation<JsonObject>(client, "/version") { //$NON-NLS-1$

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
		return versionInfo.get("version").getAsString() + "(rev: " + versionInfo.get("revision").getAsString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public boolean validate(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		String validate = new GitlabOperation<String>(client, "/version") { //$NON-NLS-1$

			@Override
			protected boolean isRepeatable() {
				return false;
			}

			@Override
			protected HttpRequestBase createHttpRequestBase(String url) {
				HttpRequestBase request = new HttpGet(url);
				return request;
			}

			@Override
			protected String parseFromJson(InputStreamReader in) throws GitlabException {
				return new BufferedReader(in).lines().parallel().collect(Collectors.joining("\n")); //$NON-NLS-1$
			}
		}.run(monitor);
		return validate.length() > 0 && validate.contains("version"); //$NON-NLS-1$
	}

	public JsonObject getMetadata(IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/metadata") { //$NON-NLS-1$
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
			ArrayList<TaskData> response = new ArrayList<>();
			GitlabConfiguration config = getConfiguration();
			JsonArray ja = json.getAsJsonArray();
			for (JsonElement jsonElement : ja) {
				JsonObject jo = jsonElement.getAsJsonObject();
				TaskData taskData = getFromJson(jo);
				if (config != null) {
					config.updateProductOptions(taskData);
				}
				response.add(taskData);
			}
			return response;
		}
	}

	private TaskData getFromJson(JsonObject jo) {
		GitlabTaskDataHandler dataHandler = (GitlabTaskDataHandler) connector.getTaskDataHandler();
		TaskAttributeMapper mapper = dataHandler.getAttributeMapper(taskRepository);
		String selfString = jo.get("_links").getAsJsonObject().get("self").getAsString(); //$NON-NLS-1$ //$NON-NLS-2$
		TaskData response = new TaskData(mapper, connector.getConnectorKind(), taskRepository.getRepositoryUrl(),
				selfString.replace(taskRepository.getUrl() + GitlabCoreActivator.API_VERSION, "")); //$NON-NLS-1$
		try {
			dataHandler.initializeTaskData(taskRepository, response, null, null);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		for (Entry<String, JsonElement> entry : jo.entrySet()) {
			String attributeId = GitlabTaskSchema.getAttributeNameFromJsonName(entry.getKey());
			TaskAttribute attribute = response.getRoot().getAttribute(attributeId);
			Field field = GitlabTaskSchema.getDefault().getFieldByKey(attributeId);
//	    if (attribute == null) {
//		PrintStream ps = attribute == null ? System.err : System.out;
//		ps.println(entry.getKey() + " -> " + entry.getValue() //
//			+ " -> " + attributeId + " -> " + attribute + "\n" //
//			+ entry.getValue().isJsonPrimitive() + entry.getValue().isJsonObject()
//			+ entry.getValue().isJsonArray());
//		ps.flush();
//	    }
			if (attribute != null && entry.getValue() != null && entry.getValue().isJsonPrimitive()) {
				attribute.setValue(entry.getValue().getAsString());
			}
			if (attribute != null && entry.getValue() != null && entry.getValue().isJsonArray()) {
				for (JsonElement arrayElement : entry.getValue().getAsJsonArray()) {
					attribute.addValue(arrayElement.getAsString());
				}
			}
			if (field != null && TaskAttribute.TYPE_PERSON.equals(field.getType()) && entry.getValue().isJsonObject()) {
				JsonObject personObject = entry.getValue().getAsJsonObject();
				attribute.setValue(personObject.get("name").getAsString()); //$NON-NLS-1$
				IRepositoryPerson author = taskRepository.createPerson(personObject.get("username").getAsString()); //$NON-NLS-1$
				author.setName(personObject.get("name").getAsString()); //$NON-NLS-1$
				author.setAttribute("avatar_url", personObject.get("avatar_url").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
				mapper.setRepositoryPerson(attribute, author);
			}
			if (GitlabTaskSchema.getDefault().TASK_MILESTONE.getKey().equals(attributeId) && attribute != null
					&& entry.getValue() != null && entry.getValue().isJsonObject()) {
				JsonObject obj = (JsonObject) entry.getValue();
				attribute.setValue(obj.get("id").getAsString()); //$NON-NLS-1$
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
			if (config != null) {
				config.updateProductOptions(taskData);
				config.addValidOperations(taskData);
			}
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

	private void getAccessTokenIfNotPresent(IOperationMonitor monitor) throws GitlabException {
		if (getClientAttribute(AUTHORIZATION_HEADER) == null) {
			try {
				obtainAccessToken(monitor);
			} catch (Exception e) {
				throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, "Exception", e)); //$NON-NLS-1$
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
		String accessToken;
		if (Boolean.parseBoolean(taskRepository.getProperty(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN))) {
			accessToken = taskRepository.getProperty(GitlabCoreActivator.PERSONAL_ACCESS_TOKEN);
		} else {
			AuthenticationCredentials credentials1 = taskRepository
					.getCredentials(org.eclipse.mylyn.commons.net.AuthenticationType.REPOSITORY);
			String username = credentials1.getUserName();
			String password = credentials1.getPassword();
			String repositoryUrl = taskRepository.getRepositoryUrl();

			URL url = new URL(repositoryUrl + "/oauth/token"); //$NON-NLS-1$
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST"); //$NON-NLS-1$
			connection.setDoOutput(true);
			connection.getOutputStream()
					.write(("grant_type=password&username=" + username + "&password=" + password).getBytes()); //$NON-NLS-1$ //$NON-NLS-2$
			connection.connect();

			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				throw new Exception("Failed to obtain access token"); //$NON-NLS-1$
			}

			String response = new String(connection.getInputStream().readAllBytes());
			accessToken = response.split("\"access_token\":\"")[1].split("\"")[0]; //$NON-NLS-1$ //$NON-NLS-2$
		}

		setClientAttribute(AUTHORIZATION_HEADER, "Bearer " + accessToken); //$NON-NLS-1$
		return accessToken;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws GitlabException {
		TaskData result = null;
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(GitlabCoreActivator.REST_CLIENT,
					repository.getUrl() + " id " + taskId); //$NON-NLS-1$
		}
		String searchString = ".*(/projects/\\d+)/issues/(\\d+)"; //$NON-NLS-1$
		Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(taskId);
		if (matcher.find()) {
			GitlabConfiguration config = getConfiguration();
			if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
				GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE, "get Configuration "); //$NON-NLS-1$
			}
			JsonObject issue = getIssue(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
			if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
				GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
						"get Issue with path " + matcher.group(1) + " an ID " + matcher.group(2)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			TypeToken<TaskData> type = new TypeToken<>() {
			};
			result = new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer())
					.create()
					.fromJson(issue, type.getType());

//			JsonArray notes = getIssueNotes(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
//			if (notes != null) {
//				int i = 0;
//				for (JsonElement jsonElement : notes) {
//					JsonObject note = jsonElement.getAsJsonObject();
//					i = createNoteTaskAttribute(repository, result, i, note);
//				}
//			}
			JsonArray discussions = getIssueDiscussions(matcher.group(1), matcher.group(2),
					OperationUtil.convert(monitor));
			if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
				GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
						"get IssueDiscussions with path " + matcher.group(1) + " an ID " + matcher.group(2)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (discussions != null) {
				int commentIdx = 0;
				TaskAttribute attrib = null;
				for (JsonElement jsonElement : discussions) {
					JsonObject discussion = (JsonObject) jsonElement;

					JsonArray notesArray = discussion.get("notes").getAsJsonArray(); //$NON-NLS-1$
					if (discussion.get("individual_note").getAsBoolean()) { //$NON-NLS-1$
						JsonObject note = notesArray.get(0).getAsJsonObject();
						if (!note.get("system").getAsBoolean()) { //$NON-NLS-1$
							attrib = createNoteTaskAttribute(repository, result.getRoot(), commentIdx++, note);
							attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							attrib.createAttribute("note_id").setValue(note.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							attrib = createActivityEventTaskAttribute(repository, result.getRoot(), note);
							attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							attrib.createAttribute("note_id").setValue(note.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					} else {
						TaskAttribute reply = null;
						for (JsonElement jsonElement2 : notesArray) {
							JsonObject note = jsonElement2.getAsJsonObject();
							if (!note.get("system").getAsBoolean()) { //$NON-NLS-1$
								attrib = createNoteTaskAttribute(repository, reply == null ? result.getRoot() : reply,
										commentIdx++, note);
								if (reply == null) {
									reply = attrib.createAttribute("reply"); //$NON-NLS-1$
								}
								attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
								attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
								attrib.createAttribute("note_id").setValue(note.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								attrib = createActivityEventTaskAttribute(repository, result.getRoot(), note);
								attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
								attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
								attrib.createAttribute("note_id").setValue(note.get("id").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
					}
				}
			}

			JsonArray states = getIssueStateEvents(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
			if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
				GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
						"get IssueStateEvents with path " + matcher.group(1) + " an ID " + matcher.group(2)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (states != null) {
				for (JsonElement stateElem : states) {
					JsonObject state = (JsonObject) stateElem;
					Instant instant = Instant
							.from(DateTimeFormatter.ISO_INSTANT.parse(state.get("created_at").getAsString())); //$NON-NLS-1$
					LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());

					TaskAttribute taskAttribute = result.getRoot()
							.createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + instant);
					taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

					String stateText = state.get("state").getAsString(); //$NON-NLS-1$

					TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
							.createAttribute(taskAttribute);
					child.setValue(stateText);
					taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, child.getId());

					child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE).createAttribute(taskAttribute);
					child.setValue(localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

					child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
					child.setValue(state.get("user").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$

					TaskAttribute typeAttribute = taskAttribute
							.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

					if (stateText.startsWith("closed")) { //$NON-NLS-1$
						typeAttribute.setValue(ActivityType.LOCK.toString());
					} else if (stateText.startsWith("reopened")) { //$NON-NLS-1$
						typeAttribute.setValue(ActivityType.REOPEN.toString());
					}

				}
			}
			JsonArray labels = getIssueLabelEvents(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
			if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
				GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
						"get getIssueLabelEvents with path " + matcher.group(1) + " an ID " + matcher.group(2)); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (labels != null) {
				long lastLabelAt = 0;
				Instant instantAt = null;
				TaskAttribute labelText = null;
				ArrayList<String> added = new ArrayList<>();
				ArrayList<String> removed = new ArrayList<>();
				for (JsonElement stateElem : labels) {
					JsonObject label = (JsonObject) stateElem;
					Instant instant = Instant
							.from(DateTimeFormatter.ISO_INSTANT.parse(label.get("created_at").getAsString())); //$NON-NLS-1$
					LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());

					if (!instant.equals(instantAt)) {
						instantAt = instant;

						buildLableText(labelText, added, removed);

						added.clear();
						removed.clear();

						TaskAttribute taskAttribute = result.getRoot()
								.createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + instant);
						taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
						TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE)
								.createAttribute(taskAttribute);
						child.setValue(localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

						child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
						child.setValue(label.get("user").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$

						TaskAttribute typeAttribute = taskAttribute
								.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
						typeAttribute.setValue(ActivityType.LABEL.toString());
						labelText = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
								.createAttribute(taskAttribute);
						taskAttribute.getMetaData()
								.putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, labelText.getId());
					}
					if (label.getAsJsonObject().get("action").getAsString().equals("add")) { //$NON-NLS-1$ //$NON-NLS-2$
						added.add(label.get("label").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
					}
					if (label.getAsJsonObject().get("action").getAsString().equals("remove")) { //$NON-NLS-1$ //$NON-NLS-2$
						removed.add(label.get("label").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
					}

				}
				buildLableText(labelText, added, removed);

			}

			config.updateProductOptions(result);
		}
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceExit(GitlabCoreActivator.REST_CLIENT, result.toString());
		}
		return result;
	}

	private void buildLableText(TaskAttribute labelText, ArrayList<String> added, ArrayList<String> removed) {
		if (labelText != null) {
			String text = ""; //$NON-NLS-1$
			if (added.size() > 0) {
				text += "added "; //$NON-NLS-1$
				text += String.join(", ", added); //$NON-NLS-1$
				text += added.size() > 1 ? " labels" : " label"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (removed.size() > 0) {
				text += text.length() == 0 ? "removed " : " and removed "; //$NON-NLS-1$ //$NON-NLS-2$
				text += String.join(", ", removed); //$NON-NLS-1$
				text += removed.size() > 1 ? " labels" : " label"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			labelText.setValue(text);
		}
	}

	private TaskAttribute createActivityEventTaskAttribute(TaskRepository repository, TaskAttribute result,
			JsonObject note) {
		Instant instant = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(note.get("created_at").getAsString())); //$NON-NLS-1$
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
		TaskAttribute taskAttribute = result.createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + instant);
		taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

		TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE).createAttribute(taskAttribute);
		child.setValue(localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

		child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
		child.setValue(note.get("author").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$

		buildActivityEventStyleInformation(taskAttribute, note.get("body").getAsString()); //$NON-NLS-1$

		return taskAttribute;
	}

	private void buildActivityEventStyleInformation(TaskAttribute taskAttribute, String bodyText) {
		TaskAttribute textAttribute = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
				.createAttribute(taskAttribute);
		taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, textAttribute.getId());
		TaskAttribute typeAttribute = taskAttribute.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

		if (bodyText.startsWith("changed due date ") || bodyText.startsWith("removed due date ")) { //$NON-NLS-1$ //$NON-NLS-2$
			typeAttribute.setValue(ActivityType.CALENDAR.toString());
		} else if (bodyText.startsWith("assigned to ") || bodyText.startsWith("unassigned ")) { //$NON-NLS-1$ //$NON-NLS-2$
			typeAttribute.setValue(ActivityType.PERSON.toString());
		} else if (bodyText.startsWith("changed ")) { //$NON-NLS-1$
			typeAttribute.setValue(ActivityType.PENCIL.toString());
		} else if (bodyText.startsWith("unlocked ")) { //$NON-NLS-1$
			typeAttribute.setValue(ActivityType.UNLOCK.toString());
		} else if (bodyText.startsWith("locked ")) { //$NON-NLS-1$
			typeAttribute.setValue(ActivityType.LOCK.toString());
		} else if (bodyText.contains("/designs?version=")) { //$NON-NLS-1$
			typeAttribute.setValue(ActivityType.DESIGN.toString());
		} else {
			typeAttribute.setValue(ActivityType.UNKNOWN.toString());
		}

		TaskAttribute styleAttribute = taskAttribute.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY_STYLE);
		String resultText = ""; //$NON-NLS-1$

		String[] parts = bodyText.split("\\*\\*|\\*\\*\\{\\-|\\{\\-|\\-\\}|\\{\\+|\\+\\}"); //$NON-NLS-1$
		ArrayList<GitlabActivityStyle> styles = new ArrayList<>(parts.length);
		int textIdx = 0;
		GitlabActivityStyle styleRange = new GitlabActivityStyle(0);

		int textLen = bodyText.length();
		for (String matchText : parts) {
			int actPartLen = matchText.length();
			textIdx += actPartLen;
			String marker = textIdx + 2 <= textLen ? bodyText.substring(textIdx, textIdx + 2) : "  "; //$NON-NLS-1$

			Matcher linkMatcher = linkPattern.matcher(matchText);

			if (linkMatcher.find()) {
				if (linkMatcher.start(1) > 0) {
					resultText += matchText.substring(0, linkMatcher.start(1) - 1);
					styleRange.add2Length(linkMatcher.start(1) - 1);
					styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
					styleRange.setFontStyle(GitlabActivityStyle.UNDERLINE_LINK);
					styleRange.add2Length(linkMatcher.group(1).length());
					styleRange.setUrl(getTaskRepository().getUrl() + linkMatcher.group(2));
					resultText += linkMatcher.group(1);
					styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange,
							matchText.length() - linkMatcher.end(2));
					styleRange.setFontStyle(GitlabActivityStyle.NORMAL);
					actPartLen = matchText.length() - linkMatcher.end(2) - 1;
				}
			}

			if (actPartLen > 0) {
				resultText += matchText;
				styleRange.add2Length(actPartLen);
			}
			if ("**".equals(marker)) { //$NON-NLS-1$
				styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
				if (styleRange.getFontStyle() == GitlabActivityStyle.BOLD) {
					styleRange.setFontStyle(GitlabActivityStyle.NORMAL);
				} else {
					styleRange.setFontStyle(GitlabActivityStyle.BOLD);
				}
			}
			if ("{-".equals(marker)) { //$NON-NLS-1$
				styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
				styleRange.setColor(GitlabActivityStyle.COLOR_RED);
			}
			if ("{+".equals(marker)) { //$NON-NLS-1$
				styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
				styleRange.setColor(GitlabActivityStyle.COLOR_GREEN);
			}
			if ("-}".equals(marker) || "+}".equals(marker)) { //$NON-NLS-1$ //$NON-NLS-2$
				styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
				styleRange.setColor(GitlabActivityStyle.COLOR_INHERIT_DEFAULT);

			}
			textIdx += 2;
		}
		textAttribute.setValue(resultText);
		styleAttribute.setValue(gson.toJson(styles, listOfMyClassObject));

	}

	private final Type listOfMyClassObject = new TypeToken<ArrayList<GitlabActivityStyle>>() {
	}.getType();

	private final Gson gson = new Gson();

	private GitlabActivityStyle createNewRangeIfNeeded(int resultTextLen, ArrayList<GitlabActivityStyle> styles,
			GitlabActivityStyle styleRange, int actPartLen) {
		if (actPartLen > 0) {
			styles.add(styleRange);

			try {
				styleRange = styleRange.clone();
				styleRange.setStart(resultTextLen);
				styleRange.setLength(0);
				return styleRange;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return styleRange;
	}

	private TaskAttribute createNoteTaskAttribute(TaskRepository repository, TaskAttribute result, int i,
			JsonObject note) {
		TaskCommentMapper cmapper = new TaskCommentMapper();
		IRepositoryPerson author = repository
				.createPerson(note.get("author").getAsJsonObject().get("username").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
		author.setName(note.get("author").getAsJsonObject().get("name").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
		author.setAttribute("avatar_url", note.get("author").getAsJsonObject().get("avatar_url").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		cmapper.setAuthor(author);
		cmapper.setCreationDate(GitlabTaskAttributeMapper.parseDate(note.get("created_at").getAsString())); //$NON-NLS-1$
		cmapper.setText(note.get("body").getAsString()); //$NON-NLS-1$
		cmapper.setNumber(++i);
		TaskAttribute attribute = result.createAttribute(TaskAttribute.PREFIX_COMMENT + i);
		cmapper.applyTo(attribute);
		attribute.createAttribute("system").setValue(note.get("system").getAsString()); //$NON-NLS-1$ //$NON-NLS-2$
		return attribute;
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
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/user") { //$NON-NLS-1$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/users" + path) { //$NON-NLS-1$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/namespaces") { //$NON-NLS-1$
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
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/groups" + path) { //$NON-NLS-1$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/groups" + path + "/subgroups?all_available=true") { //$NON-NLS-1$ //$NON-NLS-2$
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
				"/groups" + path + "/descendant_groups?all_available=true") { //$NON-NLS-1$ //$NON-NLS-2$
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
		JsonObject jsonObject = new GitlabOperation<JsonObject>(client, "/namespaces" + path) { //$NON-NLS-1$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/projects") { //$NON-NLS-1$
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
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, "/projects/" + projectid) { //$NON-NLS-1$
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
				"/groups/" + projectid + "/projects?include_subgroups=true") { //$NON-NLS-1$ //$NON-NLS-2$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues") { //$NON-NLS-1$
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
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client, path + "/issues/" + id) { //$NON-NLS-1$
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
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/notes?sort=asc") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonArray getIssueDiscussions(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/discussions") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonArray getIssueStateEvents(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/resource_state_events") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonArray getIssueLabelEvents(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/resource_label_events") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonArray getIssueMilestoneEvents(String path, String id, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client,
				path + "/issues/" + id + "/resource_milestone_events") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonObject getIssueDiscussion(String path, String id, String discussion_id, IOperationMonitor monitor)
			throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonArray = new GitlabOperation<JsonObject>(client,
				path + "/issues/" + id + "/discussions/" + discussion_id) { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonElement createIssueNote(String path, String id, String body, IOperationMonitor monitor)
			throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonElement;
		jsonElement = new GitlabPostOperation<JsonObject>(client, path + "/issues/" + id + "/notes") { //$NON-NLS-1$ //$NON-NLS-2$

			@Override
			protected void addHttpRequestEntities(HttpRequestBase request) throws GitlabException {
				super.addHttpRequestEntities(request);
				request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
				try {
					((HttpPost) request).setEntity(new StringEntity(body));
				} catch (UnsupportedEncodingException e) {
					throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
							"UnsupportedEncodingException", e)); //$NON-NLS-1$
				}
			}

			@Override
			protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, GitlabException {
				validate(response, HttpStatus.SC_CREATED, monitor);
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonElement;
	}

	public JsonElement updateIssue(String path, String id, String body, IOperationMonitor monitor)
			throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonObject jsonElement;
		jsonElement = new GitlabPutOperation<JsonObject>(client, path + "/issues/" + id, body) { //$NON-NLS-1$
			@Override

			protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
					throws IOException, GitlabException {
				validate(response, HttpStatus.SC_OK, monitor);
			}

			@Override
			protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
				StringBuilder result = new StringBuilder();
//				String rr = "";
//				try {
//					// Read each byte and convert into a char, adding to the StringBuilder
//					for (int data = in.read(); data != -1; data = in.read()) {
//						result.append((char) data);
//						rr = result.toString();
//					}
//				} catch (IOException e) {
//					// TODO: handle exception
//				}
//				return null;
				return new Gson().fromJson(in, JsonObject.class);
			}
		}.run(monitor);
		return jsonElement;
	}

	public GitlabConfiguration getConfiguration(TaskRepository repository, IOperationMonitor monitor)
			throws GitlabException {
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(GitlabCoreActivator.REST_CLIENT, repository.getUrl());
		}
		GitlabConfiguration config = new GitlabConfiguration(repository.getUrl());
		JsonObject user = getUser(monitor);
		config.setUserID(user.get("id").getAsBigInteger()); //$NON-NLS-1$
		config.setUserDetails(user);
		if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
			GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
					/* repository.getRepositoryUrl() + */ "get User"); //$NON-NLS-1$
		}
		JsonElement projects = getProjects("/users/" + config.getUserID(), monitor); //$NON-NLS-1$
		for (JsonElement project : (JsonArray) projects) {
			JsonObject projectObject = (JsonObject) project;
			JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
			JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
			config.addProject(projectObject, labels, milestones);
		}
		if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
			GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
					/* repository.getRepositoryUrl() + */ "get User Projects"); //$NON-NLS-1$
		}
		String projectValue = repository.getProperty(GitlabCoreActivator.PROJECTS);
		if (projectValue != null && !projectValue.isBlank()) {
			String[] projectList = projectValue.split(","); //$NON-NLS-1$
			for (int i = 0; i < projectList.length; i++) {
				try {
					String project = projectList[i];
					JsonObject projectDetail = getProject(URLEncoder.encode(project, StandardCharsets.UTF_8.toString()),
							monitor);
					if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
						GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
								/* repository.getRepositoryUrl() + */ "get Project: (" + (i + 1) + "/" //$NON-NLS-1$ //$NON-NLS-2$
										+ projectList.length + "): " + project + " "); //$NON-NLS-1$ //$NON-NLS-2$
					}
					JsonObject projectObject = projectDetail;
					JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
					JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
					if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
						GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
								/* repository.getRepositoryUrl() + */ "get Project: (" + (i + 1) + "/" //$NON-NLS-1$ //$NON-NLS-2$
										+ projectList.length + "): " + project + " Labels/Milestone "); //$NON-NLS-1$ //$NON-NLS-2$
					}
					config.addProject(projectObject, labels, milestones);
				} catch (UnsupportedEncodingException e) {
					throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
							"UnsupportedEncodingException", e)); //$NON-NLS-1$
				}
			}
		}
		String groupsValue = repository.getProperty(GitlabCoreActivator.GROUPS);
		if (groupsValue != null && !groupsValue.isBlank()) {
			String[] groupList = groupsValue.split(","); //$NON-NLS-1$
			for (int i = 0; i < groupList.length; i++) {
				String group = groupList[i];
				JsonObject groupDetail = getGroup("/" + group, monitor); //$NON-NLS-1$
				config.addGroup(groupDetail);
				if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
					GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
							/* repository.getRepositoryUrl() + */ "get Group (" + (i + 1) + "/" + groupList.length //$NON-NLS-1$ //$NON-NLS-2$
									+ "): " + group); //$NON-NLS-1$
				}
				projects = getGroupProjects(group, monitor);
				if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
					GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
							/* repository.getRepositoryUrl() + */ "get Group (" + (i + 1) + "/" + groupList.length //$NON-NLS-1$ //$NON-NLS-2$
									+ "): " + group + " projects"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				JsonArray projectsArray = (JsonArray) projects;
				for (int j = 0; j < projectsArray.size(); j++) {
					JsonElement project = projectsArray.get(j);
					JsonObject projectObject = (JsonObject) project;
					JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
					JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor); //$NON-NLS-1$
					if (GitlabCoreActivator.DEBUG_REST_CLIENT_TRACE) {
						GitlabCoreActivator.DEBUG_TRACE.trace(GitlabCoreActivator.REST_CLIENT_TRACE,
								/* repository.getRepositoryUrl() + */ "get Group (" + (i + 1) + "/" + groupList.length //$NON-NLS-1$ //$NON-NLS-2$
										+ "): " + group + " Projects (" + (j + 1) + "/" + projectsArray.size() + "): " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
										+ projectObject.get("id").getAsString() + " Labels/Milestone: "); //$NON-NLS-1$ //$NON-NLS-2$
					}
					config.addProject(projectObject, labels, milestones);
				}
			}
		}
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceExit(GitlabCoreActivator.REST_CLIENT, config.toString());
		}
		return config;
	}

	Map<String, String> updatable = Map.ofEntries(Map.entry(GitlabTaskSchema.getDefault().SUMMARY.getKey(), "title"), //$NON-NLS-1$
			Map.entry(GitlabTaskSchema.getDefault().DESCRIPTION.getKey(), "description"), //$NON-NLS-1$
			Map.entry(GitlabTaskSchema.getDefault().DISCUSSION_LOCKED.getKey(),
					GitlabTaskSchema.getDefault().DISCUSSION_LOCKED.getKey()),
			Map.entry(GitlabTaskSchema.getDefault().CONFIDENTIAL.getKey(),
					GitlabTaskSchema.getDefault().CONFIDENTIAL.getKey()),
			Map.entry(GitlabTaskSchema.getDefault().ISSUE_TYPE.getKey(),
					GitlabTaskSchema.getDefault().ISSUE_TYPE.getKey()),
			Map.entry(GitlabTaskSchema.getDefault().OPERATION.getKey(), "state_event"), //$NON-NLS-1$
			Map.entry(GitlabTaskSchema.getDefault().DUE_DATE.getKey(), GitlabTaskSchema.getDefault().DUE_DATE.getKey()),
			Map.entry(GitlabTaskSchema.getDefault().TASK_LABELS.getKey(),
					GitlabTaskSchema.getDefault().TASK_LABELS.getKey()),
			Map.entry(GitlabTaskSchema.getDefault().TASK_MILESTONE.getKey(), "milestone_id")); //$NON-NLS-1$

	private static SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$

	public RepositoryResponse postTaskData(TaskData taskData, Set<TaskAttribute> oldAttributes,
			IOperationMonitor monitor) throws GitlabException {
		if (taskData.isNew()) {
			JsonElement result = createNewIssue(taskData, monitor);
			JsonObject resObj = (JsonObject) result;
			String newID = resObj.get("iid").getAsString(); //$NON-NLS-1$
			String projectID = resObj.get("project_id").getAsString(); //$NON-NLS-1$
			return new RepositoryResponse(ResponseKind.TASK_CREATED, "/projects/" + projectID + "/issues/" + newID); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return updateExistingIssue(taskData, oldAttributes, monitor);
		}
	}

	private RepositoryResponse updateExistingIssue(TaskData taskData, Set<TaskAttribute> oldAttributes,
			IOperationMonitor monitor) throws GitlabException {
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(GitlabCoreActivator.REST_CLIENT,
					taskData.getRepositoryUrl() + " id " + taskData.getTaskId()); //$NON-NLS-1$
		}
		ArrayList<String> changedAtributes = new ArrayList<>();
		String newComentValue = ""; //$NON-NLS-1$
		String discussionsId = ""; //$NON-NLS-1$
		for (TaskAttribute taskAttribute : oldAttributes) {
			String attributeID = taskAttribute.getId();
			if (updatable.containsKey(attributeID)) {
				TaskAttribute newAttrib = taskData.getRoot().getAttribute(attributeID);
				String newValue = newAttrib.getValue();
				if (attributeID.equals(GitlabTaskSchema.getDefault().TASK_LABELS.getKey())) {
					newValue = newAttrib.getValues()
							.toString()
							.substring(1, newAttrib.getValues().toString().length() - 1);
				}
				if (attributeID.equals("due_date")) { //$NON-NLS-1$
					if (newValue.length() > 0) {
						newValue = dmyFormat.format(new Date(Long.parseLong(newValue)));
					}
				}
				changedAtributes.add(NLS.bind("\"{0}\":\"{1}\"", updatable.get(attributeID), newValue)); //$NON-NLS-1$
			}

			if (GitlabTaskSchema.getDefault().NEW_COMMENT.getKey().equals(taskAttribute.getId())) {
				TaskAttribute newAttrib = taskData.getRoot().getAttribute(attributeID);
				TaskAttribute noteableIdAttrib = newAttrib.getAttribute("noteable_id"); //$NON-NLS-1$
				TaskAttribute iidAttribute = taskData.getRoot()
						.getAttribute(GitlabTaskSchema.getDefault().IID.getKey());
				discussionsId = iidAttribute.getValue();
				if (noteableIdAttrib != null) {
					newComentValue = "{\"note_id\":" + noteableIdAttrib.getValue() + ",\"body\":\"" //$NON-NLS-1$ //$NON-NLS-2$
							+ newAttrib.getValue().replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"") + "\"}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					TaskAttribute xx1 = newAttrib.getAttribute("discussions"); //$NON-NLS-1$
					discussionsId += ("/discussions/" + xx1.getValue()); //$NON-NLS-1$
				} else {
					newComentValue = "{\"body\":\"" + newAttrib.getValue().replaceAll("\n", "\\\n") + "\"}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
			}
		}

		TaskAttribute productAttribute = taskData.getRoot()
				.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey());
		if (productAttribute != null && !productAttribute.getValue().isEmpty()) {
			TaskAttribute iidAttribute = taskData.getRoot().getAttribute(GitlabTaskSchema.getDefault().IID.getKey());
			if (!changedAtributes.isEmpty()) {
				updateIssue("/projects/" + productAttribute.getValue(), iidAttribute.getValue(), //$NON-NLS-1$
						"{" + String.join(",", changedAtributes) + "}", monitor); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (!newComentValue.isEmpty()) {
				createIssueNote("/projects/" + productAttribute.getValue(), discussionsId, newComentValue, monitor); //$NON-NLS-1$
			}
		}
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceExit(GitlabCoreActivator.REST_CLIENT);
		}
		return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
	}

	public JsonElement createNewIssue(TaskData taskData, IOperationMonitor monitor) throws GitlabException {
		if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
			GitlabCoreActivator.DEBUG_TRACE.traceEntry(GitlabCoreActivator.REST_CLIENT,
					taskData.getRepositoryUrl() + " id " + taskData.getTaskId()); //$NON-NLS-1$
		}
		try {
			getAccessTokenIfNotPresent(monitor);
			TaskAttribute productAttribute = taskData.getRoot()
					.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey());
			if (productAttribute == null || productAttribute.getValue().isEmpty()) {
				throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
						"productAttribute should not be null")); //$NON-NLS-1$
			}
			JsonObject jsonElement;
			jsonElement = new GitlabPostOperation<JsonObject>(client,
					"/projects/" + productAttribute.getValue() + "/issues") { //$NON-NLS-1$ //$NON-NLS-2$

				@Override
				protected void addHttpRequestEntities(HttpRequestBase request) throws GitlabException {
					super.addHttpRequestEntities(request);
					request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
					Gson gson = new GsonBuilder().registerTypeAdapter(TaskData.class, new TaskAttributeTypeAdapter())
							.create();
					String jsondata = gson.toJson(taskData);
					try {
						((HttpPost) request).setEntity(new StringEntity(jsondata));
					} catch (UnsupportedEncodingException e) {
						throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
								"UnsupportedEncodingException", e)); //$NON-NLS-1$
					}
				}

				@Override
				protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
						throws IOException, GitlabException {
					validate(response, HttpStatus.SC_CREATED, monitor);
				}

				@Override
				protected JsonObject parseFromJson(InputStreamReader in) throws GitlabException {
					return new Gson().fromJson(in, JsonObject.class);
				}
			}.run(monitor);
			return jsonElement;
		} finally {
			if (GitlabCoreActivator.DEBUG_REST_CLIENT) {
				GitlabCoreActivator.DEBUG_TRACE.traceExit(GitlabCoreActivator.REST_CLIENT);
			}
		}
	}

	class TaskAttributeTypeAdapter extends TypeAdapter<TaskData> {

		@Override
		public void write(JsonWriter out, TaskData taskData) throws IOException {
			out.beginObject();
			for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
				String id = taskAttribute.getId();
				String attributValue = convertString2GSonString(taskAttribute.getValue());
				id = GitlabNewTaskSchema.getJsonNameFromAttributeName(id);
				if ("project_id".equals(id) || "state".equals(id)) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
				out.name(id).value(attributValue);
			}
			out.endObject();
		}

		@Override
		public TaskData read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException("TaskAttributeTypeAdapter in GitlabRestClient only supports write"); //$NON-NLS-1$
		}
	}

	public static String convertString2GSonString(String str) {
		str = str.replace("\"", "\\\"").replace("\n", "\\\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		StringBuffer ostr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if ((ch >= 0x0020) && (ch <= 0x007e)) {
				ostr.append(ch);
			} else {
				ostr.append("\\u"); //$NON-NLS-1$
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
				for (int j = 0; j < 4 - hex.length(); j++) {
					ostr.append("0"); //$NON-NLS-1$
				}
				ostr.append(hex.toLowerCase());
			}
		}
		return (new String(ostr));
	}

	public JsonArray getProjectLabels(String projectid, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/projects/" + projectid + "/labels") { //$NON-NLS-1$ //$NON-NLS-2$
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

	public JsonArray getProjectMilestones(String projectid, IOperationMonitor monitor) throws GitlabException {
		getAccessTokenIfNotPresent(monitor);
		JsonArray jsonArray = new GitlabJSonArrayOperation(client,
				"/projects/" + projectid + "/milestones?include_parent_milestones=true") { //$NON-NLS-1$ //$NON-NLS-2$
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

}
