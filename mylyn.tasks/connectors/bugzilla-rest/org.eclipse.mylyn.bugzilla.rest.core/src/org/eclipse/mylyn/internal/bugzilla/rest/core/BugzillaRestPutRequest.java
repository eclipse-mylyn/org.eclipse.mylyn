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
import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public abstract class BugzillaRestPutRequest<T> extends BugzillaRestRequest<T> {

	class TaskAttributes {
		private final Set<TaskAttribute> taskAttributes;

		public TaskAttributes(Set<TaskAttribute> taskAttributes) {
			super();
			this.taskAttributes = taskAttributes;
		}

		public Set<TaskAttribute> getTaskAttributes() {
			return taskAttributes;
		}

		public boolean contains(TaskAttribute taskAttribute) {
			return taskAttributes.contains(taskAttribute);
		}

	}

	protected TaskAttributes taskAttributes;

	protected final Function<String, String> function = new Function<String, String>() {

		@Override
		public String apply(String input) {
			return BugzillaRestGsonUtil.convertString2GSonString(input);
		}
	};

	class TaskAttributesTypeAdapter extends TypeAdapter<TaskAttributes> {
		RepositoryLocation location;

		public TaskAttributesTypeAdapter(RepositoryLocation location) {
			super();
			this.location = location;
		}

		@Override
		public void write(JsonWriter out, TaskAttributes taskAttributes) throws IOException {
			out.beginObject();
			addAuthenticationToGson(out, location);
			addPrefixToOutput(out);
			for (TaskAttribute element : taskAttributes.taskAttributes) {
				addAttributeToOutput(out, element);
			}
			addSuffixToOutput(out);
			out.endObject();
		}

		@Override
		public TaskAttributes read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException(
					"TaskAttributeTypeAdapter in BugzillaRestPutUpdateTask only supports write"); //$NON-NLS-1$
		}
	}

	public BugzillaRestPutRequest(CommonHttpClient client, String urlSuffix, boolean authenticationRequired,
			@NonNull Set<TaskAttribute> taskAttributes) {
		super(client, urlSuffix, authenticationRequired);
		Assert.isNotNull(taskAttributes);
		this.taskAttributes = new TaskAttributes(taskAttributes);
	}

	@Override
	protected HttpRequestBase createHttpRequestBase(String url) {
		HttpPut request = new HttpPut(url);
		request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
		return request;
	}

	@Override
	protected void addHttpRequestEntities(HttpRequestBase request) throws BugzillaRestException {
		super.addHttpRequestEntities(request);
		try {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(TaskAttributes.class, new TaskAttributesTypeAdapter(getClient().getLocation()))
					.create();
			StringEntity requestEntity = new StringEntity(gson.toJson(taskAttributes));
			((HttpPut) request).setEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			Throwables.propagate(new CoreException(
					new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "Can not build HttpRequest", e))); //$NON-NLS-1$
		}
	}

	public abstract void addPrefixToOutput(JsonWriter out) throws IOException;

	public abstract void addAttributeToOutput(JsonWriter out, TaskAttribute element) throws IOException;

	public abstract void addSuffixToOutput(JsonWriter out) throws IOException;

}