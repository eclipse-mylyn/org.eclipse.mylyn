/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
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
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestGetTaskAttachments extends BugzillaRestGetRequest<ArrayList<TaskAttribute>> {
	private final TaskData taskData;

	public BugzillaRestGetTaskAttachments(CommonHttpClient client, TaskData taskData) {
		super(client, "/bug/" + taskData.getTaskId() + "/attachment?exclude_fields=data", null); //$NON-NLS-1$ //$NON-NLS-2$
		this.taskData = taskData;
	}

	@Override
	protected ArrayList<TaskAttribute> parseFromJson(InputStreamReader in) {
		TypeToken<ArrayList<TaskAttribute>> type = new TypeToken<ArrayList<TaskAttribute>>() {
		};
		return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer()).create().fromJson(
				in, type.getType());
	}

	BugzillaRestTaskSchema taskSchema = BugzillaRestTaskSchema.getDefault();

	private class JSonTaskDataDeserializer implements JsonDeserializer<ArrayList<TaskAttribute>> {

		@Override
		public ArrayList<TaskAttribute> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			ArrayList<TaskAttribute> response = new ArrayList<TaskAttribute>();

			for (Entry<String, JsonElement> bugEntry : ((JsonObject) json.getAsJsonObject().get("bugs")).entrySet()) { //$NON-NLS-1$
				for (JsonElement jsonElement : bugEntry.getValue().getAsJsonArray()) {
					JsonObject attachmentObject = (JsonObject) jsonElement;
					String id = attachmentObject.get("id").getAsString(); //$NON-NLS-1$
					String creator = attachmentObject.get("creator").getAsString(); //$NON-NLS-1$
					Long size = attachmentObject.get("size").getAsLong(); //$NON-NLS-1$
					TaskAttribute attachmentAttribute = taskData.getRoot()
							.createAttribute(TaskAttribute.PREFIX_ATTACHMENT + id);
					BugzillaRestAttachmentMapper attachmentMapper = BugzillaRestAttachmentMapper
							.createFrom(attachmentAttribute);
					attachmentMapper.setAttachmentId(id);

					IRepositoryPerson author = taskData.getAttributeMapper().getTaskRepository().createPerson(creator);
					author.setName(creator);
					attachmentMapper.setAuthor(author);
					attachmentMapper.setLength(size != null ? size : -1L);
					try {
						SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US); //$NON-NLS-1$
						iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
						Date tempDate = iso8601Format.parse(attachmentObject.get("creation_time").getAsString()); //$NON-NLS-1$
						attachmentMapper.setCreationDate(tempDate);
						tempDate = iso8601Format.parse(attachmentObject.get("last_change_time").getAsString()); //$NON-NLS-1$
						attachmentMapper.setDeltaDate(tempDate);
					} catch (ParseException e) {
						com.google.common.base.Throwables.propagate(new CoreException(new Status(IStatus.ERROR,
								BugzillaRestCore.ID_PLUGIN,
								"Can not parse Date (" + attachmentObject.get("creation_time").getAsString() + ")"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					attachmentMapper.setContentType(attachmentObject.get("content_type").getAsString()); //$NON-NLS-1$
					attachmentMapper.setAttachmentIsPrivate(attachmentObject.get("is_private").getAsBoolean());
					attachmentMapper.setDeprecated(attachmentObject.get("is_obsolete") //$NON-NLS-1$
							.getAsBoolean());
					attachmentMapper.setDescription(attachmentObject.get("summary").getAsString()); //$NON-NLS-1$
					attachmentMapper.setFileName(attachmentObject.get("file_name").getAsString()); //$NON-NLS-1$
					attachmentMapper.setPatch(attachmentObject.get("is_patch").getAsBoolean()); //$NON-NLS-1$
					attachmentMapper.applyTo(attachmentAttribute);
					JsonArray flags = attachmentObject.get("flags").getAsJsonArray(); //$NON-NLS-1$
					if (flags.size() > 0) {
						for (JsonElement flagTmp : flags) {
							BugzillaRestFlagMapper flagMapper = new Gson().fromJson(flagTmp,
									BugzillaRestFlagMapper.class);
							TaskAttribute attribute = attachmentAttribute
									.createAttribute(IBugzillaRestConstants.KIND_FLAG + flagMapper.getNumber());
							flagMapper.applyTo(attribute);
						}
					}
					attachmentMapper.addMissingFlags(attachmentAttribute);
				}
			}
			return response;
		}

	}

}