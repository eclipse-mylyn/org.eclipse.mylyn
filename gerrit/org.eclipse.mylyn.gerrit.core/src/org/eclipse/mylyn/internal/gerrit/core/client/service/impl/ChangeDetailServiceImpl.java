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

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeDetailService;
import com.google.gerrit.common.data.IncludedInDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.PatchSetPublishDetail;
import com.google.gerrit.reviewdb.Change.Id;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.server.JsonServlet;

import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient;
import org.eclipse.mylyn.internal.gerrit.core.client.service.AbstractGerritService;

public class ChangeDetailServiceImpl extends AbstractGerritService implements ChangeDetailService {

  public ChangeDetailServiceImpl(GerritHttpClient client) {
    super(client);
    // TODO Auto-generated constructor stub
  }

  
  public String getServiceUri() {
    return "/gerrit/rpc/ChangeDetailService";
  }

  
  public void changeDetail(Id id, AsyncCallback<ChangeDetail> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(id));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<ChangeDetail>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<ChangeDetail> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((ChangeDetail)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void patchSetDetail(com.google.gerrit.reviewdb.PatchSet.Id key,
    AsyncCallback<PatchSetDetail> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(key));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<PatchSetDetail>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<PatchSetDetail> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((PatchSetDetail)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }

  
  public void patchSetPublishDetail(com.google.gerrit.reviewdb.PatchSet.Id key,
    AsyncCallback<PatchSetPublishDetail> callback) {
    String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
    LinkedList<JsonParam> a = new LinkedList<JsonParam>();
    a.add(new JsonParam(key));
    try {
      String jsonString = createJsonString(a, methodName);
      String responseMessage = client.postJsonRequest(getServiceUri(), jsonString);
      Type resultType = new TypeToken<JsonResult<PatchSetPublishDetail>>() {
      }.getType();
      Gson gson = JsonServlet.defaultGsonBuilder().create();
      JsonResult<PatchSetPublishDetail> result = gson.fromJson(responseMessage, resultType);
      callback.onSuccess((PatchSetPublishDetail)result.getResult());
    } catch (GerritException e) {
      callback.onFailure(e);
    }
  }


public void includedInDetail(Id id, AsyncCallback<IncludedInDetail> callback) {
	// TODO Auto-generated method stub
	
}

}
