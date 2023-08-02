/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
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
public class JenkinsLoginForm {

	String j_username;

	String j_password;

	boolean remember_me;

	String from;

	public UrlEncodedFormEntity createEntity() throws UnsupportedEncodingException {
		// set form content
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("j_username", j_username)); //$NON-NLS-1$
		requestParameters.add(new BasicNameValuePair("j_password", j_password)); //$NON-NLS-1$
		requestParameters.add(new BasicNameValuePair("from", from)); //$NON-NLS-1$

		// set json encoded content
		requestParameters.add(new BasicNameValuePair("json", new Gson().toJson(this))); //$NON-NLS-1$

		// set form parameters
		requestParameters.add(new BasicNameValuePair("Submit", "log in")); //$NON-NLS-1$ //$NON-NLS-2$

		// create entity
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(requestParameters);
		return entity;
	}

}
