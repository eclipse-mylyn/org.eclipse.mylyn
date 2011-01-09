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
package org.eclipse.mylyn.internal.gerrit.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.JsonEntity;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.RemoteJsonService;
import com.google.gwtjsonrpc.server.JsonServlet;

/**
 * @author Daniel Olsson
 * @author Tomas Westling
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class GerritService implements InvocationHandler {

	public static class GerritRequest {

		private static ThreadLocal<GerritRequest> currentRequest = new ThreadLocal<GerritRequest>();

		public static GerritRequest getCurrentRequest() {
			return currentRequest.get();
		}

		public static void setCurrentRequest(GerritRequest request) {
			currentRequest.set(request);
		}

		private final IProgressMonitor monitor;

		public GerritRequest(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public IProgressMonitor getMonitor() {
			return monitor;
		}

	}

	private static class JsonRequest {

		private int id;

		private final String jsonrpc = "2.0";

		private String method;

		private final List<Object> params = new ArrayList<Object>();

		private String xsrfKey;
	}

	private static class JSonResponse {

		String id;

		String jsonrpc;

		JsonElement result;

	}

	private static class JsonParameter {

		Object param;

		/**
		 * Constructor.
		 * 
		 * @param o
		 */
		public JsonParameter(Object o) {
			param = o;
		}

		public Object getElement() {
			return param;
		}

	}

	/**
	 * Parse the Json response.
	 */
	private class JSonResponseDeserializer implements JsonDeserializer<JSonResponse> {
		public JSonResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject object = json.getAsJsonObject();
			JSonResponse response = new JSonResponse();
			response.result = object.get("result"); //$NON-NLS-1$
			return response;
		}
	}

	public static <T extends RemoteJsonService> T create(Class<T> serviceClass, GerritHttpClient gerritHttpClient) {
		InvocationHandler handler = new GerritService(gerritHttpClient, "/gerrit/rpc/" + serviceClass.getSimpleName()); //$NON-NLS-1$
		return serviceClass.cast(Proxy.newProxyInstance(GerritService.class.getClassLoader(),
				new Class<?>[] { serviceClass }, handler));
	}

	private final String uri;

	protected GerritHttpClient client;

	public GerritService(GerritHttpClient client, String uri) {
		this.client = client;
		this.uri = uri;
	}

	public String getServiceUri() {
		return uri;
	}

	public Object invoke(Object proxy, final Method method, Object[] args) throws Throwable {
		// construct request
		final LinkedList<JsonParameter> parameters = new LinkedList<JsonParameter>();
		for (int i = 0; i < args.length - 1; i++) {
			parameters.add(new JsonParameter(args[i]));
		}
		@SuppressWarnings("unchecked")
		AsyncCallback<Object> callback = (AsyncCallback<Object>) args[args.length - 1];
		try {
			final Gson gson = JsonServlet.defaultGsonBuilder()
					.registerTypeAdapter(JSonResponse.class, new JSonResponseDeserializer())
					.create();

			GerritRequest request = GerritRequest.getCurrentRequest();
			IProgressMonitor monitor = (request != null) ? request.getMonitor() : null;

			// execute request
			String responseMessage = client.postJsonRequest(getServiceUri(), new JsonEntity() {
				@Override
				public String getContent() {
					return createJsonRequest(gson, parameters, method.getName());
				}
			}, monitor);

			// parse response
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
		// all methods are designed to be asynchronous and expected to return void 
		return null;
	};

	private String createJsonRequest(Gson gson, Collection<JsonParameter> args, String methodName) {
		JsonRequest msg = new JsonRequest();
		msg.method = methodName;
		if (args != null) {
			for (JsonParameter jp : args) {
				msg.params.add(jp.getElement());
			}
		}
		msg.id = client.getId();
		msg.xsrfKey = client.getXsrfKey();
		return gson.toJson(msg, msg.getClass());
	}

}
