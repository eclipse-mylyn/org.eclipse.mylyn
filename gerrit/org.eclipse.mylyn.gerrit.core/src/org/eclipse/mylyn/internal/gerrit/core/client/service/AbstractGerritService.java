/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST E ricsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

/**
 * @author Daniel Olsson, ST Ericsson
 * @author Tomas Westling, Sony Ericsson - thomas.westling@sonyericsson.com
 * @author Shawn Minto
 */

public abstract class AbstractGerritService {

	public static class JsonResult<T> {

		private T result;

		public T getResult() {
			return result;
		}

	}

	protected GerritHttpClient client;

	/**
	 * Constructor.
	 * 
	 * @param client
	 *            The AbstractGerritHttpClient for communicating with the server.
	 */
	public AbstractGerritService(GerritHttpClient client) {
		this.client = client;
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

//		@Override
//		protected void toString(Appendable sb, Escaper escaper)
//				throws IOException {
//			sb.append(gson.toJson(param, param.getClass()));
//		}

	}

	public abstract String getServiceUri();

	protected <E> JsonResult<E> invoke(Collection<JsonParam> args, Type returnType) {

		JsonResult<E> result;
		Type resultType = new TypeToken<JsonResult<E>>() {
		}.getType();
		try {
			// TODO: Think another turn about this
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
			// result = invoke("forAccount", new Object[] { id }, typ);
			// This creates a ugly dependency to GWT, but we don't want to
			// duplicate
			// that code...
			String message = createJsonString(args, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), message.toString());
			System.err.println("Received: " + responseMessage);

			Gson gson = new Gson();//.defaultGsonBuilder().create();
			result = gson.fromJson(responseMessage, resultType);
			// result = gson.fromJson(responseMessage, resultType);
			return result;
			// callback.onSuccess(result.result);
		} catch (GerritException exception) {
			// callback.onFailure(exception);
			return null;
		}
	}

	private static class Message {
		
		private String jsonrpc = "2.0";
		
		private String method;
		
		private List<Object> params = new ArrayList<Object>();
		
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
	
//		// This creates a ugly dependency to GWT, but we don't want to
//		// duplicate
//		// that code...
//		JsonObject message = new JsonObject();
//		message.addProperty("jsonrpc", "2.0");
//		message.addProperty("method", methodName);
//		JsonArray array = new JsonArray();
//		array.add(new JsonObject());
//		if (args != null) {
//			for (JsonParam jp : args) {
//				array.add(jp.getElement());
//			}
//		}
//		message.add("params", array);
//		message.addProperty("id", client.getId());
//		//TODO Without the line below, method which require login to the Gerrit server cannot be run.
//		//Without it, the allOpenNext method can still be run.
//		//While implementing this, we worked towards an SonyEricsson internal Gerrit server where we are single signed on.
//		//Once logging on to the Gerrit server is taken care of, please use the below functionality to be able to
//		//use the forAccount method, which is used to get the user's reviews.
//
//		message.addProperty("xsrfKey", client.getXsrfKey());
//		return message.toString();

}
