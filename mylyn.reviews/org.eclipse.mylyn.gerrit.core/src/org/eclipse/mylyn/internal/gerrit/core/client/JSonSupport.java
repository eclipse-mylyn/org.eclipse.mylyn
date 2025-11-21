/*********************************************************************
 * Copyright (c) 2010, 2013 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      Jan Lohre (SAP) - improvements
 *********************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jgit.diff.Edit;

import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.reviewdb.ApprovalCategory.Id;
import com.google.gerrit.reviewdb.AuthType;
import com.google.gerrit.reviewdb.PatchSetApproval;
import com.google.gerrit.reviewdb.Project;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gwtjsonrpc.server.JsonServlet;

/**
 * @author Steffen Pingel
 */
public class JSonSupport {

	/**
	 * Parses a Json response.
	 */
	private static class JSonResponseDeserializer implements JsonDeserializer<JSonResponse> {
		@Override
		public JSonResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			JSonResponse response = new JSonResponse();
			response.jsonrpc = object.get("jsonrpc").getAsString(); //$NON-NLS-1$
			response.id = object.get("id").getAsInt(); //$NON-NLS-1$
			response.result = object.get("result"); //$NON-NLS-1$
			response.error = object.get("error"); //$NON-NLS-1$
			return response;
		}
	}

	static class JSonError {

		int code;

		String message;

	}

	static class JsonRequest {

		int id;

		final String jsonrpc = "2.0"; //$NON-NLS-1$

		String method;

		final List<Object> params = new ArrayList<>();

		String xsrfKey;
	}

	static class JSonResponse {

		JsonElement error;

		int id;

		String jsonrpc;

		JsonElement result;

	}

	private Gson gson;

	public JSonSupport() {
		TypeToken<Map<Id, PatchSetApproval>> approvalMapType = new TypeToken<>() {
		};
		ExclusionStrategy exclustionStrategy = new ExclusionStrategy() {

			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				// commentLinks requires instantiation of com.google.gwtexpui.safehtml.client.RegexFindReplace which is not on classpath
				if (f.getDeclaredClass() == List.class && f.getName().equals("commentLinks") //$NON-NLS-1$
						&& f.getDeclaringClass() == GerritConfig.class) {
					return true;
				}
				if (f.getDeclaredClass() == Map.class && f.getName().equals("given")) { //$NON-NLS-1$
					//return true;
				}
				// GSon 2.1 fails to deserialize the SubmitType enum
				if (f.getDeclaringClass() == Project.class && f.getName().equals("submitType")) { //$NON-NLS-1$
					return true;
				}
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		};
		gson = JsonServlet.defaultGsonBuilder()
				.registerTypeAdapter(JSonResponse.class, new JSonResponseDeserializer())
				.registerTypeAdapter(Edit.class, (JsonDeserializer<Edit>) (json, typeOfT, context) -> {
					if (json.isJsonArray()) {
						JsonArray array = json.getAsJsonArray();
						if (array.size() == 4) {
							return new Edit(array.get(0).getAsInt(), array.get(1).getAsInt(),
									array.get(2).getAsInt(), array.get(3).getAsInt());
						}
					}
					return new Edit(0, 0);
				})
				// ignore GerritForge specific AuthType "TEAMFORGE" which is unknown to Gerrit
				.registerTypeAdapter(AuthType.class, (JsonDeserializer<AuthType>) (json, typeOfT, context) -> {
					String jsonString = json.getAsString();
					if (jsonString != null) {
						try {
							return AuthType.valueOf(jsonString);
						} catch (IllegalArgumentException e) {
							// ignore the error since the connector does not make use of AuthType
							//GerritCorePlugin.logWarning("Ignoring unkown authentication type: " + jsonString, e);
						}
					}
					return null;
				})
				.registerTypeAdapter(approvalMapType.getType(), (JsonDeserializer<Map<Id, PatchSetApproval>>) (json, typeOfT, context) -> {
// Gerrit 2.2: the type of PatchSetPublishDetail.given changed from a map to a list
					Map<Id, PatchSetApproval> map = new HashMap<>();
					if (json.isJsonArray()) {
						JsonArray array = json.getAsJsonArray();
						for (Iterator<JsonElement> it = array.iterator(); it.hasNext();) {
							JsonElement element = it.next();
							Id key = context.deserialize(element, Id.class);
							if (key.get() != null) {
								// Gerrit < 2.1.x: json is map
								element = it.next();
							}
							PatchSetApproval value = context.deserialize(element, PatchSetApproval.class);
							if (key.get() == null) {
								// Gerrit 2.2: json is a list, deduct key from value
								key = value.getCategoryId();
							}
							map.put(key, value);
						}
					}
					return map;
				})
				.setExclusionStrategies(exclustionStrategy)
				.create();
	}

	String createRequest(int id, String xsrfKey, String methodName, Collection<Object> args) {
		JsonRequest msg = new JsonRequest();
		msg.method = methodName;
		if (args != null) {
			msg.params.addAll(args);
		}
		msg.id = id;
		msg.xsrfKey = xsrfKey;
		return gson.toJson(msg, msg.getClass());
	}

	<T> T parseJsonResponse(String responseMessage, Type resultType) throws GerritException {
		JSonResponse response = parseResponse(responseMessage, JSonResponse.class);
		if (response.error != null) {
			JSonError error = gson.fromJson(response.error, JSonError.class);
			throw new GerritException(error.message, error.code);
		} else {
			return gson.fromJson(response.result, resultType);
		}
	}

	public <T> T parseResponse(String responseMessage, Type resultType) {
		Assert.isLegal(responseMessage != null);
		Assert.isLegal(!responseMessage.isEmpty());

		// Gerrit 2.5 prepends the output with bogus characters
		// see http://code.google.com/p/gerrit/issues/detail?id=1648
		if (responseMessage.startsWith(")]}'\n")) { //$NON-NLS-1$
			responseMessage = responseMessage.substring(5);
		}
		if (responseMessage.startsWith(")]}'\r\n")) { //$NON-NLS-1$
			responseMessage = responseMessage.substring(6);
		}
		return gson.fromJson(responseMessage, resultType);
	}

	public String toJson(Object src) {
		return gson.toJson(src);
	}
}
