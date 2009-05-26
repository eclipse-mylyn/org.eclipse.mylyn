/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.tasks.core.RepositoryResponse;

/**
 * @author Frank Becker
 * @since 3.2
 */

public class BugzillaRepositoryResponse extends RepositoryResponse {
	private Map<String, List<String>> responseData = new LinkedHashMap<String, List<String>>();

	public BugzillaRepositoryResponse(ResponseKind reposonseKind, String taskId) {
		super(reposonseKind, taskId);
	}

	public BugzillaRepositoryResponse() {
		// ignore
	}

	public Map<String, List<String>> getResponseData() {
		return responseData;
	}

	public void setResponseData(Map<String, List<String>> responseData) {
		this.responseData = responseData;
	}

	public void addResponseData(String name, String response) {
		List<String> responseList = responseData.get(name);
		if (responseList == null) {
			responseList = new LinkedList<String>();
			responseData.put(name, responseList);
		}
		responseList.add(response);
	}

}
