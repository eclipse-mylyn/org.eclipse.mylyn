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
package org.eclipse.gerrit.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import org.eclipse.gerrit.GerritHTTPClientError;
import org.eclipse.gerrit.proxy.AbstractGerritHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gwtjsonrpc.server.JsonServlet;

/**
 * @author Daniel Olsson, ST Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 * @author Shawn Minto 
 */

public abstract class AbstractGerritService {

	public static class JsonResult<T> {

		private T result;
		
		public T getResult() {
			return result;
		}

	}
	
  protected AbstractGerritHttpClient client;

  /**
   * Constructor.
   * 
   * @param client
   *          The AbstractGerritHttpClient for communicating with the
   *          server.
   */
  public AbstractGerritService(AbstractGerritHttpClient client) {
    this.client = client;
  }

  static protected class JsonParam extends JsonElement {

    static Gson gson = JsonServlet.defaultGsonBuilder().create();

    Object param;

    /**
     * Constructor.
     * 
     * @param o
     */
    public JsonParam(Object o) {
      param = o;
    }

    @Override
    protected void toString(Appendable sb) throws IOException {
    	sb.append(gson.toJson(param, param.getClass()));
    }
    
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

      Gson gson = JsonServlet.defaultGsonBuilder().create();
      result = gson.fromJson(responseMessage, resultType);
      // result = gson.fromJson(responseMessage, resultType);
      return result;
      // callback.onSuccess(result.result);
    } catch (GerritHTTPClientError exception) {
      // callback.onFailure(exception);
      return null;
    }
  }

  protected String createJsonString(Collection<JsonParam> args, String methodName) throws GerritHTTPClientError {
    // This creates a ugly dependency to GWT, but we don't want to
    // duplicate
    // that code...
    JsonObject message = new JsonObject();
    message.addProperty("jsonrpc", "2.0");
    message.addProperty("method", methodName);
    JsonArray array = new JsonArray();
    if (args != null) {
      for (JsonParam jp : args) {
        array.add(jp);
      }
    }
    message.add("params", array);
    message.addProperty("id", client.getId());
    //TODO Without the line below, method which require login to the Gerrit server cannot be run.
    //Without it, the allOpenNext method can still be run.
    //While implementing this, we worked towards an SonyEricsson internal Gerrit server where we are single signed on.
    //Once logging on to the Gerrit server is taken care of, please use the below functionality to be able to
    //use the forAccount method, which is used to get the user's reviews.
    
    message.addProperty("xsrfKey", client.getXsrfKey());
    return message.toString();
  }

}
