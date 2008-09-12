/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.eclipse.mylyn.internal.web.tasks.WebRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Eugene Kuleshov
 */
public class WebRepositoryConnectorTest extends TestCase {

	// bug 213075
	public void testEncodingParameters() throws Exception {
		TaskRepository repository = new TaskRepository(WebRepositoryConnector.REPOSITORY_TYPE, "http://foo.net");

		repository.setAuthenticationCredentials("USER", "PASSWORD");

		repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_METHOD,
				WebRepositoryConnector.REQUEST_POST);

		repository.setProperty(WebRepositoryConnector.PROPERTY_LOGIN_REQUEST_URL, //
				"${serverUrl}/Login.php?xajax=xCheckUserLogin&xajaxargs[]=<xjxquery><q>${xjxquery}</q></xjxquery>");

		repository.setProperty("param_xjxquery", "TestUserName=${userId}&TestUserPWD=${password}&HttpRefer=");

		Map<String, String> params = new HashMap<String, String>();

		PostMethod method = (PostMethod) WebRepositoryConnector.getLoginMethod(params, repository);

		String form = EncodingUtil.formUrlEncode(method.getParameters(), method.getRequestCharSet());

		assertEquals("xajax=xCheckUserLogin&" + //
				"xajaxargs%5B%5D=%3Cxjxquery%3E%3Cq%3E" + //
				"TestUserName%3DUSER%26" + //
				"TestUserPWD%3DPASSWORD%26" + //
				"HttpRefer%3D" + //
				"%3C%2Fq%3E%3C%2Fxjxquery%3E", form);
	}

}
