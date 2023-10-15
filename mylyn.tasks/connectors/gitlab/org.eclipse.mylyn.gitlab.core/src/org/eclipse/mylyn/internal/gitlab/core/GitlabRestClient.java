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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

    public static String AUTHORIZATION_HEADER = "authorization_header";
    private static final boolean TRACE = false;

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
	String[] queryProjects = query.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey()).split(",");
	String groupAttribute = query.getAttribute("group");

	if (!queryProjects[0].isEmpty()) {
	    for (String string : queryProjects) {
		String path = "/projects/" + string.replaceAll("/", "%2F");
		getIssuesInternal(query, collector, path, monitor);
	    }
	}
	if (groupAttribute != null) {
	    String[] gueryGroups = groupAttribute.split(",");
	    if (!gueryGroups[0].isEmpty()) {
		for (String string : gueryGroups) {
		    String path = "/groups/" + string.replaceAll("/", "%2F");
		    getIssuesInternal(query, collector, path, monitor);
		}
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
		ArrayList<String> suffix = new ArrayList<String>();
		switch (state != null ? state : "") {
		case "opened":
		    suffix.add("state=opened");
		    break;
		case "closed":
		    suffix.add("state=closed");
		    break;
		default:
		}
		String search = query.getAttribute("SEARCH");
		if (search != null && !search.isBlank()) {
		    try {
			suffix.add("search=" + URLEncoder.encode(search, StandardCharsets.UTF_8.toString()));
			String searchIn = query.getAttribute("SEARCH_IN");
			if (searchIn != null && !searchIn.isBlank()) {
			    suffix.add("in=" + searchIn);
			}
		    } catch (UnsupportedEncodingException e) {
			// ignore if we not can encode the search value
			e.printStackTrace();
		    }
		}
		if (Boolean.valueOf(query.getAttribute("CONFIDENTIAL"))) {
		    suffix.add("confidential=true");
		}
		if (Boolean.valueOf(query.getAttribute("ASSIGNED_TO_ME"))) {
		    suffix.add("scope=assigned_to_me");
		}

		String suffixStr = suffix.size() > 0 ? "?" + String.join("&", suffix) : "";
		HttpRequestBase request = new HttpGet(url + suffixStr);
		return request;
	    }

	    private String nextPage(Header[] linkHeader) {
		if (linkHeader.length > 0) {
		    Header firstLinkHeader = linkHeader[0];
		    for (String linkHeaderEntry : firstLinkHeader.getValue().split(", ")) {
			String[] linkHeaderElements = linkHeaderEntry.split("; ");
			if ("rel=\"next\"".equals(linkHeaderElements[1])) {
			    return linkHeaderElements[0].substring(1, linkHeaderElements[0].length() - 1);
			}
		    }
		}

		return null;
	    }

	    protected java.util.List<TaskData> execute(IOperationMonitor monitor) throws IOException, GitlabException {
		List<TaskData> result = null;
		HttpRequestBase request = createHttpRequestBase();
		addHttpRequestEntities(request);
		CommonHttpResponse response = execute(request, monitor);
		result = processAndRelease(response, monitor);
		Header[] linkHeader = response.getResponse().getHeaders("Link");
		String nextPageValue = nextPage(linkHeader);
		while (nextPageValue != null) {
		    HttpRequestBase looprequest = new HttpGet(nextPageValue);
		    addHttpRequestEntities(looprequest);
		    CommonHttpResponse loopresponse = execute(looprequest, monitor);
		    List<TaskData> loopresult = processAndRelease(loopresponse, monitor);
		    result.addAll(loopresult);
		    linkHeader = loopresponse.getResponse().getHeaders("Link");
		    nextPageValue = nextPage(linkHeader);
		}
		return result;
	    };

	    @Override
	    protected List<TaskData> parseFromJson(InputStreamReader in) throws GitlabException {

		TypeToken<List<TaskData>> type = new TypeToken<List<TaskData>>() {
		};

		return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataListDeserializer())
			.create().fromJson(in, type.getType());
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
	    GitlabConfiguration config = getConfiguration();
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
	String selfString = jo.get("_links").getAsJsonObject().get("self").getAsString();
	TaskData response = new TaskData(mapper, connector.getConnectorKind(), taskRepository.getRepositoryUrl(),
		selfString.replace(taskRepository.getUrl() + GitlabCoreActivator.API_VERSION, ""));
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
		attribute.setValue(personObject.get("name").getAsString());
		IRepositoryPerson author = taskRepository.createPerson(personObject.get("username").getAsString());
		author.setName(personObject.get("name").getAsString());
		author.setAttribute("avatar_url", personObject.get("avatar_url").getAsString());
		mapper.setRepositoryPerson(attribute, author);
	    }
	    if (GitlabTaskSchema.getDefault().TASK_MILESTONE.getKey().equals(attributeId) && attribute != null
		    && entry.getValue() != null && entry.getValue().isJsonObject()) {
		JsonObject obj = (JsonObject) entry.getValue();
		attribute.setValue(obj.get("id").getAsString());
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
		throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, "Exception", e));
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
	    accessToken = response.split("\"access_token\":\"")[1].split("\"")[0];
	}

	setClientAttribute(AUTHORIZATION_HEADER, "Bearer " + accessToken);
	return accessToken;
    }

    public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
	    throws GitlabException {
	TaskData result = null;
	long startTime, endTime = 0;
	Thread thread;
	if (TRACE) {
	    thread = Thread.currentThread();
	    System.out.println(
		    thread.getName() + " getTaskData start: " + repository.getRepositoryUrl() + " id " + taskId);
	    startTime = Calendar.getInstance().getTimeInMillis();
	}
	String searchString = ".*(/projects/\\d+)/issues/(\\d+)";
	Pattern pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(taskId);
	if (matcher.find()) {
	    GitlabConfiguration config = getConfiguration();
	    JsonObject issue = getIssue(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
	    TypeToken<TaskData> type = new TypeToken<TaskData>() {
	    };
	    result = new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer()).create()
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
	    if (discussions != null) {
		int commentIdx = 0;
		TaskAttribute attrib = null;
		for (JsonElement jsonElement : discussions) {
		    JsonObject discussion = (JsonObject) jsonElement;

		    JsonArray notesArray = discussion.get("notes").getAsJsonArray();
		    if (discussion.get("individual_note").getAsBoolean()) {
			JsonObject note = notesArray.get(0).getAsJsonObject();
			if (!note.get("system").getAsBoolean()) {
			    attrib = createNoteTaskAttribute(repository, result.getRoot(), commentIdx++, note);
			    attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString());
			    attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString());
			    attrib.createAttribute("note_id").setValue(note.get("id").getAsString());
			} else {
			    attrib = createActivityEventTaskAttribute(repository, result.getRoot(), note);
			    attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString());
			    attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString());
			    attrib.createAttribute("note_id").setValue(note.get("id").getAsString());
			}
		    } else {
			TaskAttribute reply = null;
			for (JsonElement jsonElement2 : notesArray) {
			    JsonObject note = jsonElement2.getAsJsonObject();
			    if (!note.get("system").getAsBoolean()) {
				attrib = createNoteTaskAttribute(repository, reply == null ? result.getRoot() : reply,
					commentIdx++, note);
				if (reply == null) {
				    reply = attrib.createAttribute("reply");
				}
				attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString());
				attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString());
				attrib.createAttribute("note_id").setValue(note.get("id").getAsString());
			    } else {
				attrib = createActivityEventTaskAttribute(repository, result.getRoot(), note);
				attrib.createAttribute("discussions").setValue(discussion.get("id").getAsString());
				attrib.createAttribute("noteable_id").setValue(note.get("noteable_id").getAsString());
				attrib.createAttribute("note_id").setValue(note.get("id").getAsString());
			    }
			}
		    }
		}
	    }

	    JsonArray states = getIssueStateEvents(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
	    if (states != null) {
		for (JsonElement stateElem : states) {
		    JsonObject state = (JsonObject) stateElem;
		    Date date = GitlabTaskAttributeMapper.parseDate(state.get("created_at").getAsString());
		    TaskAttribute taskAttribute = result.getRoot()
			    .createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + date.getTime());
		    taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

		    String stateText = state.get("state").getAsString();

		    TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
			    .createAttribute(taskAttribute);
		    child.setValue(stateText);
		    taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, child.getId());

		    child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE).createAttribute(taskAttribute);
		    child.setValue(DateFormat.getDateTimeInstance().format(date));

		    child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
		    child.setValue(state.get("user").getAsJsonObject().get("name").getAsString());

		    TaskAttribute typeAttribute = taskAttribute
			    .createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

		    if (stateText.startsWith("closed")) {
			typeAttribute.setValue(ActivityType.LOCK.toString());
		    } else if (stateText.startsWith("reopened")) {
			typeAttribute.setValue(ActivityType.REOPEN.toString());
		    }

		}
	    }
	    JsonArray labels = getIssueLabelEvents(matcher.group(1), matcher.group(2), OperationUtil.convert(monitor));
	    if (labels != null) {
		long lastLabelAt = 0;
		TaskAttribute labelText = null;
		ArrayList<String> added = new ArrayList<String>();
		ArrayList<String> removed = new ArrayList<String>();
		for (JsonElement stateElem : labels) {
		    JsonObject label = (JsonObject) stateElem;
		    Date date = GitlabTaskAttributeMapper.parseDate(label.get("created_at").getAsString());

		    if (lastLabelAt != date.getTime()) {
			lastLabelAt = date.getTime();

			buildLableText(labelText, added, removed);

			added.clear();
			removed.clear();

			TaskAttribute taskAttribute = result.getRoot()
				.createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + date.getTime());
			taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
			TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE)
				.createAttribute(taskAttribute);
			child.setValue(DateFormat.getDateTimeInstance().format(date));

			child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
			child.setValue(label.get("user").getAsJsonObject().get("name").getAsString());

			TaskAttribute typeAttribute = taskAttribute
				.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);
			typeAttribute.setValue(ActivityType.LABEL.toString());
			labelText = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
				.createAttribute(taskAttribute);
			taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID,
				labelText.getId());
		    }
		    if (label.getAsJsonObject().get("action").getAsString().equals("add")) {
			added.add(label.get("label").getAsJsonObject().get("name").getAsString());
		    }
		    if (label.getAsJsonObject().get("action").getAsString().equals("remove")) {
			removed.add(label.get("label").getAsJsonObject().get("name").getAsString());
		    }

		}
		buildLableText(labelText, added, removed);

	    }

	    config.updateProductOptions(result);
	}
	if (TRACE) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getTaskData taken: " + (endTime - startTime) + " ms "
		    + repository.getRepositoryUrl() + " id " + taskId);
	}
	return result;
    }

    private void buildLableText(TaskAttribute labelText, ArrayList<String> added, ArrayList<String> removed) {
	if (labelText != null) {
	    String text = "";
	    if (added.size() > 0) {
		text += "added ";
		text += String.join(", ", added);
		if (added.size() > 1) {
		    text += " labels";
		} else {
		    text += " label";
		}
	    }
	    if (removed.size() > 0) {
		if (text.length() == 0) {
		    text += "removed ";
		} else {
		    text += " and removed ";
		}
		text += String.join(", ", removed);
		if (removed.size() > 1) {
		    text += " labels";
		} else {
		    text += " label";
		}
	    }
	    labelText.setValue(text);
	}
    }

    private TaskAttribute createActivityEventTaskAttribute(TaskRepository repository, TaskAttribute result,
	    JsonObject note) {
	Date date = GitlabTaskAttributeMapper.parseDate(note.get("created_at").getAsString());
	TaskAttribute taskAttribute = result.createAttribute(GitlabCoreActivator.PREFIX_ACTIVITY + date.getTime());
	taskAttribute.getMetaData().setType(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

	TaskAttribute child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_DATE).createAttribute(taskAttribute);
	child.setValue(DateFormat.getDateTimeInstance().format(date));

	child = DefaultTaskSchema.getField(TaskAttribute.COMMENT_AUTHOR).createAttribute(taskAttribute);
	child.setValue(note.get("author").getAsJsonObject().get("name").getAsString());

	buildActivityEventStyleInformation(taskAttribute, note.get("body").getAsString());

	return taskAttribute;
    }

    private void buildActivityEventStyleInformation(TaskAttribute taskAttribute, String bodyText) {
	TaskAttribute textAttribute = DefaultTaskSchema.getField(TaskAttribute.COMMENT_TEXT)
		.createAttribute(taskAttribute);
	taskAttribute.getMetaData().putValue(TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID, textAttribute.getId());
	TaskAttribute typeAttribute = taskAttribute.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY);

	if (bodyText.startsWith("changed due date ") || bodyText.startsWith("removed due date ")) {
	    typeAttribute.setValue(ActivityType.CALENDAR.toString());
	} else if (bodyText.startsWith("assigned to ") || bodyText.startsWith("unassigned ")) {
	    typeAttribute.setValue(ActivityType.PERSON.toString());
	} else if (bodyText.startsWith("changed ")) {
	    typeAttribute.setValue(ActivityType.PENCIL.toString());
	} else if (bodyText.startsWith("unlocked ")) {
	    typeAttribute.setValue(ActivityType.UNLOCK.toString());
	} else if (bodyText.startsWith("locked ")) {
	    typeAttribute.setValue(ActivityType.LOCK.toString());
	} else if (bodyText.contains("/designs?version=")) {
	    typeAttribute.setValue(ActivityType.DESIGN.toString());
	} else {
	    typeAttribute.setValue(ActivityType.UNKNOWN.toString());
	}

	TaskAttribute styleAttribute = taskAttribute.createAttribute(GitlabCoreActivator.ATTRIBUTE_TYPE_ACTIVITY_STYLE);
	String resultText = "";
	Pattern r = Pattern.compile("\\[(.+)\\]\\((.+)\\)");

	String[] parts = bodyText.split("\\*\\*|\\*\\*\\{\\-|\\{\\-|\\-\\}|\\{\\+|\\+\\}");
	ArrayList<GitlabActivityStyle> styles = new ArrayList<GitlabActivityStyle>(parts.length);
	int textIdx = 0;
	GitlabActivityStyle styleRange = new GitlabActivityStyle(0);

	int textLen = bodyText.length();
	for (int idx = 0; idx < parts.length; idx++) {
	    int actPartLen = parts[idx].length();
	    textIdx += actPartLen;
	    String marker = textIdx + 2 <= textLen ? bodyText.substring(textIdx, textIdx + 2) : "  ";

	    String matchText = parts[idx];
	    Matcher m = r.matcher(matchText);

	    if (m.find()) {
		if (m.start(1) > 0) {
		    resultText += matchText.substring(0, m.start(1) - 1);
		    styleRange.add2Length(m.start(1) - 1);
		    styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
		    styleRange.setFontStyle(GitlabActivityStyle.UNDERLINE_LINK);
		    styleRange.add2Length(m.group(1).length());
		    styleRange.setUrl(getTaskRepository().getUrl() + m.group(2));
		    resultText += m.group(1);
		    styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange,
			    matchText.length() - m.end(2));
		    styleRange.setFontStyle(GitlabActivityStyle.NORMAL);
		    actPartLen = matchText.length() - m.end(2) - 1;
		}
	    }

	    if (actPartLen > 0) {
		resultText += parts[idx];
		styleRange.add2Length(actPartLen);
	    }
	    if ("**".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
		if (styleRange.getFontStyle() == GitlabActivityStyle.BOLD) {
		    styleRange.setFontStyle(GitlabActivityStyle.NORMAL);
		} else {
		    styleRange.setFontStyle(GitlabActivityStyle.BOLD);
		}
	    }
	    if ("{-".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
		styleRange.setColor(GitlabActivityStyle.COLOR_RED);
	    }
	    if ("{+".equals(marker)) {
		styleRange = createNewRangeIfNeeded(resultText.length(), styles, styleRange, actPartLen);
		styleRange.setColor(GitlabActivityStyle.COLOR_GREEN);
	    }
	    if ("-}".equals(marker) || "+}".equals(marker)) {
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
		.createPerson(note.get("author").getAsJsonObject().get("username").getAsString());
	author.setName(note.get("author").getAsJsonObject().get("name").getAsString());
	author.setAttribute("avatar_url", note.get("author").getAsJsonObject().get("avatar_url").getAsString());
	cmapper.setAuthor(author);
	cmapper.setCreationDate(GitlabTaskAttributeMapper.parseDate(note.get("created_at").getAsString()));
	cmapper.setText(note.get("body").getAsString());
	cmapper.setNumber(++i);
	TaskAttribute attribute = result.createAttribute(TaskAttribute.PREFIX_COMMENT + i);
	cmapper.applyTo(attribute);
	attribute.createAttribute("system").setValue(note.get("system").getAsString());
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

    public JsonArray getIssueDiscussions(String path, String id, IOperationMonitor monitor) throws GitlabException {
	getAccessTokenIfNotPresent(monitor);
	JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/discussions") {
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
	JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/resource_state_events") {
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
	JsonArray jsonArray = new GitlabJSonArrayOperation(client, path + "/issues/" + id + "/resource_label_events") {
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
		path + "/issues/" + id + "/resource_milestone_events") {
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
		path + "/issues/" + id + "/discussions/" + discussion_id) {
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
	jsonElement = new GitlabPostOperation<JsonObject>(client, path + "/issues/" + id + "/notes") {

	    @Override
	    protected void addHttpRequestEntities(HttpRequestBase request) throws GitlabException {
		super.addHttpRequestEntities(request);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		try {
		    ((HttpPost) request).setEntity(new StringEntity(body));
		} catch (UnsupportedEncodingException e) {
		    throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
			    "UnsupportedEncodingException", e));
		}
	    };

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
	jsonElement = new GitlabPutOperation<JsonObject>(client, path + "/issues/" + id, body) {
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
	long startTime, endTime = 0;
	Thread thread;
	if (TRACE) {
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getConfiguration start: " + repository.getRepositoryUrl());
	    startTime = Calendar.getInstance().getTimeInMillis();
	}
	GitlabConfiguration config = new GitlabConfiguration(repository.getUrl());
	JsonObject user = getUser(monitor);
	config.setUserID(user.get("id").getAsBigInteger());
	config.setUserDetails(user);
	if (TRACE) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getConfiguration getUser: " + (endTime - startTime) + " ms "
		    + repository.getRepositoryUrl());
	}
	JsonElement projects = getProjects("/users/" + config.getUserID(), monitor);
	for (JsonElement project : (JsonArray) projects) {
	    JsonObject projectObject = (JsonObject) project;
	    JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor);
	    JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor);
	    config.addProject(projectObject, labels, milestones);
	}
	if (TRACE) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getConfiguration get Projects: " + (endTime - startTime) + " ms "
		    + repository.getRepositoryUrl());
	}
	String projectValue = repository.getProperty(GitlabCoreActivator.PROJECTS);
	if (projectValue != null && !projectValue.isBlank()) {
	    String[] projectList = projectValue.split(",");
	    for (String project : projectList) {
		try {
		    JsonObject projectDetail = getProject(URLEncoder.encode(project, StandardCharsets.UTF_8.toString()),
			    monitor);
		    if (TRACE) {
			endTime = Calendar.getInstance().getTimeInMillis();
			thread = Thread.currentThread();
			System.out.println(thread.getName() + " getConfiguration get Project: " + (endTime - startTime)
				+ " ms " + project + " " + repository.getRepositoryUrl());
		    }
		    JsonObject projectObject = (JsonObject) projectDetail;
		    JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor);
		    JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor);
		    if (TRACE) {
			endTime = Calendar.getInstance().getTimeInMillis();
			thread = Thread.currentThread();
			System.out.println(thread.getName() + " getConfiguration get Projects Labels/Milestone: "
				+ (endTime - startTime) + " ms " + project + " " + repository.getRepositoryUrl());
		    }
		    config.addProject(projectObject, labels, milestones);
		} catch (UnsupportedEncodingException e) {
		    throw new GitlabException(new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID,
			    "UnsupportedEncodingException", e));
		}
	    }
	}
	if (TRACE) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getConfiguration get Groups: " + (endTime - startTime) + " ms "
		    + repository.getRepositoryUrl());
	}
	String groupsValue = repository.getProperty(GitlabCoreActivator.GROUPS);
	if (groupsValue != null && !groupsValue.isBlank()) {
	    String[] groupList = groupsValue.split(",");
	    for (String group : groupList) {
		JsonObject groupDetail = getGroup("/" + group, monitor);
		config.addGroup(groupDetail);
		if (TRACE) {
		    endTime = Calendar.getInstance().getTimeInMillis();
		    thread = Thread.currentThread();
		    System.out.println(thread.getName() + " getConfiguration get Group: " + (endTime - startTime)
			    + " ms " + group + " " + repository.getRepositoryUrl());
		}
		projects = getGroupProjects(group, monitor);
		if (TRACE) {
		    endTime = Calendar.getInstance().getTimeInMillis();
		    thread = Thread.currentThread();
		    System.out.println(thread.getName() + " getConfiguration get Group projects: "
			    + (endTime - startTime) + " ms " + group + " " + repository.getRepositoryUrl());
		}
		for (JsonElement project : (JsonArray) projects) {
		    JsonObject projectObject = (JsonObject) project;
		    JsonArray labels = getProjectLabels(projectObject.get("id").getAsString(), monitor);
		    JsonArray milestones = getProjectMilestones(projectObject.get("id").getAsString(), monitor);
		    if (TRACE) {
			endTime = Calendar.getInstance().getTimeInMillis();
			thread = Thread.currentThread();
			System.out.println(thread.getName() + " getConfiguration get Group Projects Labels/Milestone: "
				+ (endTime - startTime) + " ms " + group + " " + projectObject.get("id").getAsString()
				+ " " + repository.getRepositoryUrl());
		    }
		    config.addProject(projectObject, labels, milestones);
		}
		if (TRACE) {
		    endTime = Calendar.getInstance().getTimeInMillis();
		    thread = Thread.currentThread();
		    System.out.println(thread.getName() + " getConfiguration get Group projects: "
			    + (endTime - startTime) + " ms " + group + " " + repository.getRepositoryUrl());
		}
	    }
	}
	if (TRACE) {
	    endTime = Calendar.getInstance().getTimeInMillis();
	    thread = Thread.currentThread();
	    System.out.println(thread.getName() + " getConfiguration taken: " + (endTime - startTime) + " ms "
		    + repository.getRepositoryUrl());
	}
	return config;
    }

    Map<String, String> updatable = Map.ofEntries(Map.entry(GitlabTaskSchema.getDefault().SUMMARY.getKey(), "title"),
	    Map.entry(GitlabTaskSchema.getDefault().DESCRIPTION.getKey(), "description"),
	    Map.entry(GitlabTaskSchema.getDefault().DISCUSSION_LOCKED.getKey(),
		    GitlabTaskSchema.getDefault().DISCUSSION_LOCKED.getKey()),
	    Map.entry(GitlabTaskSchema.getDefault().CONFIDENTIAL.getKey(),
		    GitlabTaskSchema.getDefault().CONFIDENTIAL.getKey()),
	    Map.entry(GitlabTaskSchema.getDefault().ISSUE_TYPE.getKey(),
		    GitlabTaskSchema.getDefault().ISSUE_TYPE.getKey()),
	    Map.entry(GitlabTaskSchema.getDefault().OPERATION.getKey(), "state_event"),
	    Map.entry(GitlabTaskSchema.getDefault().DUE_DATE.getKey(), GitlabTaskSchema.getDefault().DUE_DATE.getKey()),
	    Map.entry(GitlabTaskSchema.getDefault().TASK_LABELS.getKey(),
		    GitlabTaskSchema.getDefault().TASK_LABELS.getKey()),
	    Map.entry(GitlabTaskSchema.getDefault().TASK_MILESTONE.getKey(), "milestone_id"));

    private static SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");

    public RepositoryResponse postTaskData(TaskData taskData, Set<TaskAttribute> oldAttributes,
	    IOperationMonitor monitor) throws GitlabException {
	if (taskData.isNew()) {
	    JsonElement result = createNewIssue(taskData, monitor);
	    JsonObject resObj = (JsonObject) result;
	    String newID = resObj.get("iid").getAsString();
	    String projectID = resObj.get("project_id").getAsString();
	    return new RepositoryResponse(ResponseKind.TASK_CREATED, "/projects/" + projectID + "/issues/" + newID);
	} else {
	    return updateExistingIssue(taskData, oldAttributes, monitor);
	}
    }

    private RepositoryResponse updateExistingIssue(TaskData taskData, Set<TaskAttribute> oldAttributes,
	    IOperationMonitor monitor) throws GitlabException {
	ArrayList<String> changedAtributes = new ArrayList<>();
	String newComentValue = "";
	String discussionsId = "";
	for (TaskAttribute taskAttribute : oldAttributes) {
	    String attributeID = taskAttribute.getId();
	    if (updatable.containsKey(attributeID)) {
		TaskAttribute newAttrib = taskData.getRoot().getAttribute(attributeID);
		String newValue = newAttrib.getValue();
		if (attributeID.equals(GitlabTaskSchema.getDefault().TASK_LABELS.getKey())) {
		    newValue = newAttrib.getValues().toString().substring(1,
			    newAttrib.getValues().toString().length() - 1);
		}
		if (attributeID.equals("due_date")) {
		    if (newValue.length() > 0) {
			newValue = dmyFormat.format(new Date(Long.parseLong(newValue)));
		    }
		}
		changedAtributes.add(NLS.bind("\"{0}\":\"{1}\"", updatable.get(attributeID), newValue));
	    }

	    if (GitlabTaskSchema.getDefault().NEW_COMMENT.getKey().equals(taskAttribute.getId())) {
		TaskAttribute newAttrib = taskData.getRoot().getAttribute(attributeID);
		TaskAttribute noteableIdAttrib = newAttrib.getAttribute("noteable_id");
		TaskAttribute iidAttribute = taskData.getRoot()
			.getAttribute(GitlabTaskSchema.getDefault().IID.getKey());
		discussionsId = iidAttribute.getValue();
		if (noteableIdAttrib != null) {
		    newComentValue = "{\"note_id\":" + noteableIdAttrib.getValue() + ",\"body\":\""
			    + newAttrib.getValue().replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"") + "\"}";
		    TaskAttribute xx1 = newAttrib.getAttribute("discussions");
		    discussionsId += ("/discussions/" + xx1.getValue());
		} else {
		    newComentValue = "{\"body\":\"" + newAttrib.getValue().replaceAll("\n", "\\\n") + "\"}";
		}
	    }
	}

	TaskAttribute productAttribute = taskData.getRoot()
		.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey());
	if (productAttribute != null && !productAttribute.getValue().isEmpty()) {
	    TaskAttribute iidAttribute = taskData.getRoot().getAttribute(GitlabTaskSchema.getDefault().IID.getKey());
	    if (!changedAtributes.isEmpty()) {
		updateIssue("/projects/" + productAttribute.getValue(), iidAttribute.getValue(),
			"{" + String.join(",", changedAtributes) + "}", monitor);
	    }
	    if (!newComentValue.isEmpty()) {
		createIssueNote("/projects/" + productAttribute.getValue(), discussionsId, newComentValue, monitor);
	    }
	}
	return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskData.getTaskId());
    }

    public JsonElement createNewIssue(TaskData taskData, IOperationMonitor monitor) throws GitlabException {
	getAccessTokenIfNotPresent(monitor);
	TaskAttribute productAttribute = taskData.getRoot()
		.getAttribute(GitlabTaskSchema.getDefault().PRODUCT.getKey());
	if (productAttribute == null || productAttribute.getValue().isEmpty()) {
	    throw new GitlabException(
		    new Status(IStatus.ERROR, GitlabCoreActivator.PLUGIN_ID, "productAttribute should not be null"));
	}
	JsonObject jsonElement;
	jsonElement = new GitlabPostOperation<JsonObject>(client,
		"/projects/" + productAttribute.getValue() + "/issues") {

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
			    "UnsupportedEncodingException", e));
		}
	    };

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

    class TaskAttributeTypeAdapter extends TypeAdapter<TaskData> {

	@Override
	public void write(JsonWriter out, TaskData taskData) throws IOException {
	    out.beginObject();
	    for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
		String id = taskAttribute.getId();
		String attributValue = convertString2GSonString(taskAttribute.getValue());
		id = GitlabNewTaskSchema.getJsonNameFromAttributeName(id);
		if ("project_id".equals(id) || "state".equals(id)) {
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
	JsonArray jsonArray = new GitlabJSonArrayOperation(client, "/projects/" + projectid + "/labels") {
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
		"/projects/" + projectid + "/milestones?include_parent_milestones=true") {
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
