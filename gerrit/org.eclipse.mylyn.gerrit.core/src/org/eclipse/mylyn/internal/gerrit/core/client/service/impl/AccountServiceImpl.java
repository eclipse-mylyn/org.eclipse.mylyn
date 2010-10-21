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
import java.util.List;
import java.util.Set;

import com.google.gerrit.common.data.AccountProjectWatchInfo;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.AgreementInfo;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountGeneralPreferences;
import com.google.gerrit.reviewdb.AccountProjectWatch;
import com.google.gerrit.reviewdb.AccountProjectWatch.Key;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.VoidResult;
import com.google.gwtjsonrpc.server.JsonServlet;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;
import org.eclipse.mylyn.internal.gerrit.core.client.service.AbstractGerritService;

public class AccountServiceImpl extends AbstractGerritService implements AccountService {

  public AccountServiceImpl(GerritHttpClient client) {
    super(client);
    // TODO Auto-generated constructor stub
  }

  
  public String getServiceUri() {
    return "/gerrit/rpc/AccountService";
  }

  
  public void addProjectWatch(String projectName, AsyncCallback<AccountProjectWatchInfo> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(projectName));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<AccountProjectWatchInfo>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<AccountProjectWatchInfo> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((AccountProjectWatchInfo)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void changePreferences(AccountGeneralPreferences pref,
    AsyncCallback<VoidResult> gerritCallback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(pref));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<VoidResult>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<VoidResult> result = gson.fromJson(responseMessage, resultType);
      gerritCallback.onSuccess((VoidResult)result.getResult());
    } catch (GerritException e) {
      gerritCallback.onFailure(e);
    }
  }

  
  public void deleteProjectWatches(Set<Key> keys, AsyncCallback<VoidResult> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(keys));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<VoidResult>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<VoidResult> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((VoidResult)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void myAccount(AsyncCallback<Account> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<Account>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<Account> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((Account)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void myAgreements(AsyncCallback<AgreementInfo> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<AgreementInfo>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<AgreementInfo> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((AgreementInfo)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void myProjectWatch(AsyncCallback<List<AccountProjectWatchInfo>> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<List<AccountProjectWatchInfo>>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<List<AccountProjectWatchInfo>> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((List<AccountProjectWatchInfo>)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void updateProjectWatch(AccountProjectWatch watch, AsyncCallback<VoidResult> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(watch));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<VoidResult>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<VoidResult> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((VoidResult)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }


public void myDiffPreferences(AsyncCallback<AccountDiffPreference> callback) {
	// TODO Auto-generated method stub
	
}


public void changeDiffPreferences(AccountDiffPreference diffPref,
		AsyncCallback<VoidResult> callback) {
	// TODO Auto-generated method stub
	
}


public void addProjectWatch(String projectName, String filter,
		AsyncCallback<AccountProjectWatchInfo> callback) {
	// TODO Auto-generated method stub
	
}

}
