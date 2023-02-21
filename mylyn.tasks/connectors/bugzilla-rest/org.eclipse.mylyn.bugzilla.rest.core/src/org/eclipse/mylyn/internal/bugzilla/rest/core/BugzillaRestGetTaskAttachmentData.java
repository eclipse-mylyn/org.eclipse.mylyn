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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64InputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestGetTaskAttachmentData extends BugzillaRestGetRequest<InputStream> {
	private final TaskAttribute taskAttribute;

	public BugzillaRestGetTaskAttachmentData(CommonHttpClient client, TaskAttribute taskAttribute) {
		super(client, "/bug/attachment/" + taskAttribute.getValue() + "?include_fields=data", null); //$NON-NLS-1$ //$NON-NLS-2$
		this.taskAttribute = taskAttribute;
	}

	@Override
	protected InputStream parseFromJson(InputStreamReader in) {
		TypeToken<InputStream> type = new TypeToken<InputStream>() {
		};
		return new GsonBuilder().registerTypeAdapter(type.getType(), new JSonTaskDataDeserializer())
				.create()
				.fromJson(in, type.getType());
	}

	private class JSonTaskDataDeserializer implements JsonDeserializer<InputStream> {

		@Override
		public InputStream deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			JsonElement attachments = ((JsonObject) json.getAsJsonObject().get("attachments")) //$NON-NLS-1$
					.get(taskAttribute.getValue());
			JsonPrimitive attachment = attachments.getAsJsonObject().get("data").getAsJsonPrimitive(); //$NON-NLS-1$
			if (attachment == null) {
				throw com.google.common.base.Throwables.propagate(new CoreException(
						new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "Can not get Attachment Data"))); //$NON-NLS-1$
			}
			InputStream is = new ByteArrayInputStream(attachment.getAsString().getBytes());

			return new Base64InputStream(is);
		}
	}

}
