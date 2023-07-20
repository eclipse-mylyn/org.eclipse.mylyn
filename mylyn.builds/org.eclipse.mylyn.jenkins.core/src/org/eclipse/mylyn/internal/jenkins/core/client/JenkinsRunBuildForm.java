/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

/**
 * @author Steffen Pingel
 */
public class JenkinsRunBuildForm {

	private class Parameters {

		@SuppressWarnings("unused")
		private NameValue[] parameter;
	}

	private static class NameValue {

		@SuppressWarnings("unused")
		private Object name;

		@SuppressWarnings("unused")
		private Object value;

	}

	List<NameValuePair> requestParameters;

	List<NameValue> params;

	public JenkinsRunBuildForm() {
		this.requestParameters = new ArrayList<NameValuePair>();
		this.params = new ArrayList<NameValue>();
	}

	public void add(String key, String value) {
		requestParameters.add(new BasicNameValuePair("name", key));
		if (value != null) {
			requestParameters.add(new BasicNameValuePair("value", value));
		}
		NameValue param = new NameValue();
		param.name = key;
		param.value = value;
		params.add(param);
	}

	// TODO verify if both url encoding and json representation are needed
	public UrlEncodedFormEntity createEntity() throws UnsupportedEncodingException {
		Parameters jsonObject = new Parameters();
		jsonObject.parameter = params.toArray(new NameValue[0]);

		// set json encoded entities
		requestParameters.add(new BasicNameValuePair("json", new Gson().toJson(jsonObject))); //$NON-NLS-1$

		// set form parameters
		requestParameters.add(new BasicNameValuePair("Submit", "Build"));

		// create entity
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(requestParameters);
		return entity;
	}

}
