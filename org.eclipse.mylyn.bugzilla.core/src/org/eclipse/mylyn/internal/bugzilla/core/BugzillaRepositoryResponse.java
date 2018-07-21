/*******************************************************************************
 * Copyright (c) 2009 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.RepositoryResponse;

/**
 * @author Frank Becker
 */
public class BugzillaRepositoryResponse extends RepositoryResponse {

	private Map<String, Map<String, List<String>>> responseData = new LinkedHashMap<String, Map<String, List<String>>>();

	public BugzillaRepositoryResponse(ResponseKind reposonseKind, String taskId) {
		super(reposonseKind, taskId);
	}

	public BugzillaRepositoryResponse() {
		// ignore
	}

	public Map<String, Map<String, List<String>>> getResponseData() {
		return responseData;
	}

	public void setResponseData(Map<String, Map<String, List<String>>> responseData) {
		this.responseData = responseData;
	}

	public void addResponseData(String dt1, String dt2, String response) {

		Map<String, List<String>> responseMap = responseData.get(dt1);

		if (responseMap == null) {
			responseMap = new LinkedHashMap<String, List<String>>();
			responseData.put(dt1, responseMap);
		}
		List<String> responseList = responseMap.get(dt2);
		if (responseList == null) {
			responseList = new LinkedList<String>();
			responseMap.put(dt2, responseList);
		}

		responseList.add(response);
	}

}
