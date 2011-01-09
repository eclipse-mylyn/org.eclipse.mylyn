/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.server.JsonServlet;

/**
 * @author Daniel Olsson
 * @author Tomas Westling
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class GerritService implements InvocationHandler {

	public static class JSonResponse {

		String jsonrpc;

		String id;

		JsonElement result;

	}

	protected GerritHttpClient client;

	private final String uri;

	public GerritService(GerritHttpClient client, String uri) {
		this.client = client;
		this.uri = uri;
	}

	static protected class JsonParam {

		Object param;

		/**
		 * Constructor.
		 * 
		 * @param o
		 */
		public JsonParam(Object o) {
			param = o;
		}

		public Object getElement() {
			return param;
		}

	}

	public String getServiceUri() {
		return uri;
	}

	private static class Message {

		private final String jsonrpc = "2.0";

		private String method;

		private final List<Object> params = new ArrayList<Object>();

		private int id;

		private String xsrfKey;
	}

	protected String createJsonString(Collection<JsonParam> args, String methodName) throws GerritException {
		Message msg = new Message();
		msg.method = methodName;
		if (args != null) {
			for (JsonParam jp : args) {
				msg.params.add(jp.getElement());
			}
		}
		msg.id = client.getId();
		msg.xsrfKey = client.getXsrfKey();
		return new Gson().toJson(msg, msg.getClass());
	}

	// parse the response
	class JSonResponseDeserializer implements JsonDeserializer<JSonResponse> {
		public JSonResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			JSonResponse response = new JSonResponse();
			response.result = object.get("result");
			return response;
		}
	};

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		for (int i = 0; i < args.length - 1; i++) {
			a.add(new JsonParam(args[i]));
		}
		AsyncCallback<Object> callback = (AsyncCallback<Object>) args[args.length - 1];
		try {
			Gson gson = JsonServlet.defaultGsonBuilder()
					.registerTypeAdapter(JSonResponse.class, new JSonResponseDeserializer())
					.create();
			String jsonString = createJsonString(a, method.getName());
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			JSonResponse response = gson.fromJson(responseMessage, JSonResponse.class);

			// the last parameter is a parameterized callback that defines the
			// return type
			Type[] types = method.getGenericParameterTypes();
			final Type resultType = ((ParameterizedType) types[types.length - 1]).getActualTypeArguments()[0];
			Object result = gson.fromJson(response.result, resultType);
			callback.onSuccess(result);
		} catch (GerritException e) {
			callback.onFailure(e);
		}

		return null;
	}

}
