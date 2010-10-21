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
package org.eclipse.gerrit.proxy.kerberos;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.eclipse.gerrit.GerritHTTPClientError;
import org.eclipse.gerrit.proxy.AbstractGerritHttpClient;


/**
 * Implementation of AbstractGerritHttpClient that creates a
 * HttpClient that has a negotiation scheme for Kerberos. For this to
 * work the following arguments must be set to the Java VM:
 * -Djava.security.krb5.realm, -Djava.security.krb5.kdc,
 * -Djavax.security.auth.useSubjectCredsOnly,
 * -Djava.security.auth.login.config= -Dsun.security.krb5.debug=true
 * @author Daniel Olsson, ST Ericsson
 * @author Ingemar Olsén, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson - thomas.westling@sonyericsson.com
 */
public class KerberosGerritHttpClient extends AbstractGerritHttpClient {

 	private HttpClient httpClient;
 
 	private String user;
 
 	private String password;
 
 	/**
 	 * Constructor.
 	 *
 	 * @param schema
 	 *            The schema to use i.e http or https
 	 * @param host
 	 *            The server address to the Gerrit host
 	 * @param port
 	 *            The port that the communication should be relayed over
 	 */
 	public KerberosGerritHttpClient(String schema, String host, String path,
 			int port, String user, String password) {
 		super(schema, host, path, port);
 		this.user = user;
 		this.password = password;
 	}
 
 	/**
 	 * @throws GerritHTTPClientError
 	 *             if something goes wrong with the connection.
 	 */
 	@Override
 	public HttpClient getHttpClient() throws GerritHTTPClientError {
 		if (httpClient == null) {
 				httpClient = new HttpClient();
 				PostMethod method = new PostMethod(getURL()
 						+ "/gerrit/rpc/UserPassAuthService");
 				method.setRequestBody("{\"jsonrpc\":\"2.0\",\"method\":\"authenticate\",\"params\":[\""
 						+ user + "\",\"" + password + "\"],\"id\":3}");
 				method.addRequestHeader("content-type",
 						"	application/json; charset=utf-8");
 				method.setRequestHeader("Accept",
 						"application/json,application/json,application/jsonrequest");
 				try {
 					HttpClientParams params = new HttpClientParams();
 					params.setCookiePolicy(org.apache.commons.httpclient.cookie.CookiePolicy.BROWSER_COMPATIBILITY);
 					httpClient.setParams(params);
 					int status = httpClient.executeMethod(method);
 					Header cookies = method.getResponseHeader("Set-Cookie");
 
 					httpClient.setParams(params);
 				} catch (Exception ex) {
 					throw new RuntimeException(ex);
 				}
 		}
 
 		return httpClient;
 	}
 	
}
