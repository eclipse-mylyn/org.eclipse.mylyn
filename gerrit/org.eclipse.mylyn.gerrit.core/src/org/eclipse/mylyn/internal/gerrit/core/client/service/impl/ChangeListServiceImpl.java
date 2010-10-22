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
import java.util.Set;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.common.data.ToggleStarRequest;
import com.google.gerrit.reviewdb.Account.Id;
import com.google.gerrit.reviewdb.Change.Status;
import com.google.gerrit.reviewdb.Project.NameKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.VoidResult;
import com.google.gwtjsonrpc.server.JsonServlet;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;
import org.eclipse.mylyn.internal.gerrit.core.client.service.AbstractGerritService;

public class ChangeListServiceImpl extends AbstractGerritService implements ChangeListService {

	public ChangeListServiceImpl(GerritHttpClient client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	public String getServiceUri() {
		return "/gerrit/rpc/ChangeListService";
	}

	public void allOpenNext(String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void allOpenPrev(String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void allClosedNext(Status status, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void allClosedPrev(Status status, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void allQueryNext(String query, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(query));
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void allQueryPrev(String query, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void byProjectClosedNext(NameKey project, Status status, String pos, int limit,
			AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(project));
		a.add(new JsonParam(status));
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void byProjectClosedPrev(NameKey project, Status status, String pos, int limit,
			AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(project));
		a.add(new JsonParam(status));
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void byProjectOpenNext(NameKey project, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(project));
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void byProjectOpenPrev(NameKey project, String pos, int limit, AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(project));
		a.add(new JsonParam(pos));
		a.add(new JsonParam(limit));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void forAccount(Id id, AsyncCallback<AccountDashboardInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(id));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<AccountDashboardInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<AccountDashboardInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((AccountDashboardInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void myDraftChanges(AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void myStarredChangeIds(AsyncCallback<Set<com.google.gerrit.reviewdb.Change.Id>> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<Set<com.google.gerrit.reviewdb.Change.Id>>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<Set<com.google.gerrit.reviewdb.Change.Id>> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((Set<com.google.gerrit.reviewdb.Change.Id>) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void myStarredChanges(AsyncCallback<SingleListChangeInfo> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<SingleListChangeInfo>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<SingleListChangeInfo> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((SingleListChangeInfo) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	public void toggleStars(ToggleStarRequest req, AsyncCallback<VoidResult> callback) {
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		LinkedList<JsonParam> a = new LinkedList<JsonParam>();
		a.add(new JsonParam(req));
		try {
			String jsonString = createJsonString(a, methodName);
			String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
			Type resultType = new TypeToken<JsonResult<VoidResult>>() {
			}.getType();
			Gson gson = JsonServlet.defaultGsonBuilder().create();
			JsonResult<VoidResult> result = gson.fromJson(responseMessage, resultType);
			callback.onSuccess((VoidResult) result.getResult());
		} catch (GerritException e) {
			callback.onFailure(e);
		}
	}

	//TODO Above methods could be made more general, something like the code below.
	// public <E> void general(AsyncCallback<E> callback, JsonParam...
	// jsonParams) {
	// JsonResult<E> result;
	// Type typ = new TypeToken<JsonResult<?>>() {
	// }.getType();
	// try {
	// LinkedList<JsonParam> a = new LinkedList<JsonParam>();
	// for (JsonParam jp : jsonParams) {
	// a.add(jp);
	// }
	// String methodName =
	// Thread.currentThread().getStackTrace()[2].getMethodName();
	// result = invoke(methodName, a, typ);
	// callback.onSuccess(result.getResult());
	// // result = invoke("forAccount", new Object[] { id }, typ);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// callback.onFailure(e);
	// }
	// }

}
