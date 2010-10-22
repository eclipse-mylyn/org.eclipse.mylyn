/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client.service.impl;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gerrit.common.auth.userpass.LoginResult;
import com.google.gerrit.common.auth.userpass.UserPassAuthService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.server.JsonServlet;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;
import org.eclipse.mylyn.internal.gerrit.core.client.service.AbstractGerritService;

public class UserPassAuthServiceImpl extends AbstractGerritService implements UserPassAuthService {

	public UserPassAuthServiceImpl(GerritHttpClient client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	public String getServiceUri() {
		return "/gerrit/rpc/UserPassAuthService";
	}

	public void authenticate(String username, String password, AsyncCallback<LoginResult> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(username));
		a.add(new JsonParam(password));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<LoginResult>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<LoginResult> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((LoginResult) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

}
