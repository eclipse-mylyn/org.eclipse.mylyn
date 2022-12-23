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

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestGetTaskComments extends BugzillaRestGetRequest<ArrayList<TaskAttribute>> {
	private final TaskData taskData;

	public BugzillaRestGetTaskComments(CommonHttpClient client, TaskData taskData) {
		super(client, "/bug/" + taskData.getTaskId() + "/comment?", null); //$NON-NLS-1$ //$NON-NLS-2$
		this.taskData = taskData;
	}

	@Override
	protected ArrayList<TaskAttribute> parseFromJson(InputStreamReader in) {
		TypeToken<ArrayList<TaskAttribute>> type = new TypeToken<ArrayList<TaskAttribute>>() {
		};
		return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer())
				.create()
				.fromJson(in, type.getType());
	}

	BugzillaRestTaskSchema taskSchema = BugzillaRestTaskSchema.getDefault();

	private class JSonTaskDataDeserializer implements JsonDeserializer<ArrayList<TaskAttribute>> {

		@Override
		public ArrayList<TaskAttribute> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			ArrayList<TaskAttribute> response = new ArrayList<TaskAttribute>();
			for (Entry<String, JsonElement> commentEntry : ((JsonObject) json.getAsJsonObject().get("bugs")) //$NON-NLS-1$
					.entrySet()) {
				for (JsonElement jsonElement : ((JsonObject) commentEntry.getValue()).get("comments") //$NON-NLS-1$
						.getAsJsonArray()) {
					JsonObject comment = (JsonObject) jsonElement;
					int count = comment.get("count").getAsInt(); //$NON-NLS-1$
					if (count == 0) {
						TaskAttribute desc = taskData.getRoot().getMappedAttribute(taskSchema.DESCRIPTION.getKey());
						desc.setValue(comment.get("text").getAsString()); //$NON-NLS-1$
						TaskAttribute cid = desc.getAttribute(taskSchema.COMMENT_NUMBER.getKey());
						cid.setValue(comment.get("id").getAsString()); //$NON-NLS-1$
						TaskAttribute cidp = desc.getAttribute(taskSchema.COMMENT_ISPRIVATE.getKey());
						cidp.setValue(comment.get("is_private").getAsBoolean() ? "1" : "0"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
						response.add(desc);
					} else {
						TaskAttribute attribute = taskData.getRoot()
								.createAttribute(TaskAttribute.PREFIX_COMMENT + count);
						TaskCommentMapper taskComment = TaskCommentMapper.createFrom(attribute);
						taskComment.setCommentId(comment.get("id").getAsString()); //$NON-NLS-1$
						taskComment.setNumber(count);
						taskComment.setUrl(taskData.getRepositoryUrl() + "/show_bug.cgi?id=" //$NON-NLS-1$
								+ taskData.getTaskId() + "#c" + count); //$NON-NLS-1$
						IRepositoryPerson author = taskData.getAttributeMapper()
								.getTaskRepository()
								.createPerson(comment.get("creator").getAsString()); //$NON-NLS-1$
						author.setName(comment.get("creator").getAsString()); //$NON-NLS-1$
						taskComment.setAuthor(author);
						JsonElement isPrivate = comment.get("is_private"); //$NON-NLS-1$
						if (isPrivate != null) {
							taskComment.setIsPrivate(isPrivate.getAsBoolean());
						} else {
							taskComment.setIsPrivate(null);
						}
						try {
							SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", //$NON-NLS-1$
									Locale.US);
							iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
							Date tempDate = iso8601Format.parse(comment.get("creation_time").getAsString()); //$NON-NLS-1$
							taskComment.setCreationDate(tempDate);
						} catch (ParseException e) {
							com.google.common.base.Throwables.propagate(new CoreException(new Status(IStatus.ERROR,
									BugzillaRestCore.ID_PLUGIN,
									"Can not parse Date (" + comment.get("creation_time").getAsString() + ")"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}

						if (comment.get("text").getAsString() != null) { //$NON-NLS-1$
							String commentText = comment.get("text").getAsString().trim(); //$NON-NLS-1$
							taskComment.setText(commentText);
						}
						taskComment.applyTo(attribute);
						response.add(attribute);
					}
				}
			}
			return response;
		}

	}

}