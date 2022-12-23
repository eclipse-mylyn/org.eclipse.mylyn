/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestIdResult;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.osgi.util.NLS;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BugzillaRestPostNewTask extends BugzillaRestPostRequest<BugzillaRestIdResult> {
	TaskData taskData;

	public BugzillaRestPostNewTask(CommonHttpClient client, TaskData taskData) {
		super(client, "/bug"); //$NON-NLS-1$
		this.taskData = taskData;
	}

	List<NameValuePair> requestParameters;

	class TaskAttributeTypeAdapter extends TypeAdapter<TaskData> {
		RepositoryLocation location;

		public TaskAttributeTypeAdapter(RepositoryLocation location) {
			super();
			this.location = location;
		}

		@Override
		public void write(JsonWriter out, TaskData taskData) throws IOException {
			out.beginObject();
			addAuthenticationToGson(out, location);
			for (Object element : taskData.getRoot().getAttributes().values()) {
				TaskAttribute taskAttribute = (TaskAttribute) element;
				String id = taskAttribute.getId();
				String attributValue = BugzillaRestGsonUtil.convertString2GSonString(taskAttribute.getValue());
				if (legalCreateAttributes.contains(id) || id.startsWith("cf_")) { //$NON-NLS-1$
					id = BugzillaRestCreateTaskSchema.getFieldNameFromAttributeName(id);
					if (id.equals("cc")) { //$NON-NLS-1$
						HashSet<String> setNew = new HashSet<String>(
								Arrays.asList(taskAttribute.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
						BugzillaRestGsonUtil.buildArrayFromHash(out, id, setNew, false);
					} else if (id.equals(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey())
							|| id.equals(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey())) {
						if (taskAttribute.getValues().size() > 1) {
							HashSet<String> setNew = new HashSet<String>(taskAttribute.getValues());
							BugzillaRestGsonUtil.buildArrayFromHash(out, id, setNew, true);
						} else {
							HashSet<String> setNew = new HashSet<String>(
									Arrays.asList(taskAttribute.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
							BugzillaRestGsonUtil.buildArrayFromHash(out, id, setNew, true);
						}
					} else {

						if (id.equals(BugzillaRestCreateTaskSchema.getDefault().KEYWORDS.getKey())) {
							attributValue = taskAttribute.getValues().toString();
							attributValue = attributValue.substring(1, attributValue.length() - 1);
						}

						if (taskAttribute.getMetaData().getType() != null
								&& taskAttribute.getMetaData().getType().equals(TaskAttribute.TYPE_MULTI_SELECT)) {
							List<String> values = taskAttribute.getValues();
							int ii = 0;
							attributValue = ""; //$NON-NLS-1$
							for (String string : values) {
								string = BugzillaRestGsonUtil.convertString2GSonString(string);
								attributValue += ((ii++ == 0 ? "" : ",") + string); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						out.name(id).value(attributValue);
						if (id.equals("description")) { //$NON-NLS-1$
							TaskAttribute descriptionpri = taskAttribute.getAttribute(
									BugzillaRestCreateTaskSchema.getDefault().DESCRIPTION_IS_PRIVATE.getKey());
							Boolean descriptionprivalue = (descriptionpri != null)
									? (descriptionpri.getValue().equals("1")) //$NON-NLS-1$
									: false;
							out.name("comment_is_private").value(Boolean.toString(descriptionprivalue)); //$NON-NLS-1$
						}
					}
				}
			}
			out.endObject();
		}

		@Override
		public TaskData read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException(
					"TaskAttributeTypeAdapter in BugzillaRestPostNewTask only supports write"); //$NON-NLS-1$
		}
	}

	@Override
	protected void addHttpRequestEntities(HttpRequestBase request) throws BugzillaRestException {
		super.addHttpRequestEntities(request);

		try {
			// set form parameters
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(TaskData.class, new TaskAttributeTypeAdapter(getClient().getLocation()))
					.create();
			StringEntity requestEntity = new StringEntity(gson.toJson(taskData));
			((HttpPost) request).setEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			throw new BugzillaRestException("could not build REST String", e); //$NON-NLS-1$
		}
	}

	@Override
	protected BugzillaRestIdResult parseFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestIdResult> type = new TypeToken<BugzillaRestIdResult>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

	protected BugzillaRestStatus parseErrorFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestStatus> type = new TypeToken<BugzillaRestStatus>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

	@Override
	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		int statusCode = response.getStatusCode();
		if (statusCode != HttpURLConnection.HTTP_BAD_REQUEST && statusCode != HttpURLConnection.HTTP_OK) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BugzillaRestResourceNotFoundException(
						NLS.bind("Requested resource ''{0}'' does not exist", response.getRequestPath())); //$NON-NLS-1$
			}
			throw new BugzillaRestException(NLS.bind("Unexpected response from Bugzilla REST server for ''{0}'': {1}", //$NON-NLS-1$
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}

	}

	ImmutableList<String> legalCreateAttributes = new ImmutableList.Builder<String>()
			.add(BugzillaRestCreateTaskSchema.getDefault().PRODUCT.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().COMPONENT.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().SUMMARY.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().VERSION.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().DESCRIPTION.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().OS.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().PLATFORM.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().PRIORITY.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().SEVERITY.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().ALIAS.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().ASSIGNED_TO.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().QA_CONTACT.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().TARGET_MILESTONE.getKey())
			.add(TaskAttribute.OPERATION)
			.add(BugzillaRestCreateTaskSchema.getDefault().CC.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().BLOCKS.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().DEPENDS_ON.getKey())
			.add(BugzillaRestCreateTaskSchema.getDefault().KEYWORDS.getKey())
			.build();

	@Override
	protected BugzillaRestIdResult doProcess(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		InputStream is = response.getResponseEntityAsStream();
		InputStreamReader in = new InputStreamReader(is);
		switch (response.getStatusCode()) {
		case HttpURLConnection.HTTP_OK:
			return parseFromJson(in);
		default:
			BugzillaRestStatus status = parseErrorFromJson(in);
			throw new BugzillaRestException(
					NLS.bind("{2}  (status: {1} from {0})", new String[] { response.getRequestPath(), //$NON-NLS-1$
							HttpUtil.getStatusText(response.getStatusCode()), status.getMessage() }));
		}
	}
}