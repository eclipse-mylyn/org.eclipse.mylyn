/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.service.BaseHttpMethodInterceptor;
import org.eclipse.mylyn.tasks.core.service.BaseXMLRPCTransportFactory;
import org.eclipse.mylyn.tasks.core.service.BaseXmlRpcClient;
import org.eclipse.mylyn.tasks.core.service.BaseXmlRpcClientRequestImpl;

@SuppressWarnings("restriction")
public class BugzillaXmlRpcClient extends BaseXmlRpcClient {

	private class XmlRpcRequest {

		private final String method;

		private final Object[] parameters;

		public XmlRpcRequest(String method, Object[] parameters) {
			this.method = method;
			this.parameters = parameters;
		}

		public Object execute(IProgressMonitor monitor) throws XmlRpcException {
			BaseXmlRpcClientRequestImpl request = new BaseXmlRpcClientRequestImpl(xmlrpc.getClientConfig(), method,
					parameters, monitor);
			return xmlrpc.execute(request);
		}
	}

	public static final String URL_XMLRPC = "/xmlrpc.cgi"; //$NON-NLS-1$

	private int userID = -1;

	private RepositoryConfiguration repositoryConfiguration;

	public BugzillaXmlRpcClient(TaskRepository repository) {
		super(repository);
	}

	public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration) {
		this.repositoryConfiguration = repositoryConfiguration;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return repositoryConfiguration;
	}

	private synchronized XmlRpcClient getClient() throws MalformedURLException {
		if (xmlrpc == null) {
			xmlConfig = new XmlRpcClientConfigImpl();
			xmlConfig.setContentLengthOptional(false);
			xmlConfig.setConnectionTimeout(WebUtil.getConnectionTimeout());
			xmlConfig.setReplyTimeout(WebUtil.getSocketTimeout());
			xmlConfig.setServerURL(new URL(repository.getUrl() + URL_XMLRPC));

			xmlrpc = new XmlRpcClient();
			httpClient = new HttpClient();
			httpClient.setHttpConnectionManager(WebUtil.getConnectionManager());
			httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);
			TaskRepositoryLocation location = new TaskRepositoryLocation(repository);
			AuthenticationCredentials httpAuthCredentials = location.getCredentials(AuthenticationType.HTTP);
			if (httpAuthCredentials != null && httpAuthCredentials.getUserName() != null
					&& httpAuthCredentials.getUserName().length() > 0) {
				httpClient.getParams().setAuthenticationPreemptive(true);
			}

			WebUtil.configureHttpClient(httpClient, "BugzillaClient"); //$NON-NLS-1$
			BaseXMLRPCTransportFactory factory = new BaseXMLRPCTransportFactory(xmlrpc, httpClient);
			factory.setLocation(location);
			int i = 0;
			if (i == 1) {
				factory.setInterceptor(new BaseHttpMethodInterceptor() {
					@SuppressWarnings("unused")
					public void processRequest(HttpMethod method) {
						Cookie[] c = httpClient.getState().getCookies();
						int i = 9;
						i++;
					}

					@SuppressWarnings("unused")
					public void processResponse(HttpMethod method) {
						Cookie[] c = httpClient.getState().getCookies();
						int i = 9;
						i++;
					}
				});
			}
			xmlrpc.setTransportFactory(factory);
			xmlrpc.setConfig(xmlConfig);
			try {
				login();
			} catch (Exception e) {
				userID = -1;
			}
		}
		return xmlrpc;
	}

	private Object call(IProgressMonitor monitor, String method, Object... parameters) throws XmlRpcException,
			MalformedURLException {
		monitor = Policy.monitorFor(monitor);
		getClient();
		XmlRpcRequest request = new XmlRpcRequest(method, parameters);
		return request.execute(monitor);
	}

	public int getUserID() {
		return userID;
	}

	/*
	 * Modul Webservice:User 
	 */
	@SuppressWarnings("unchecked")
	private void login() {
		AuthenticationCredentials credentials = repository.getCredentials(AuthenticationType.REPOSITORY);
		HashMap<String, String> loginpar = new HashMap<String, String>();
		Object[] paramslogin = new Object[] { loginpar };

		loginpar.put("login", credentials.getUserName()); //$NON-NLS-1$
		loginpar.put("password", credentials.getPassword()); //$NON-NLS-1$
		loginpar.put("remember", "TRUE"); //$NON-NLS-1$//$NON-NLS-2$
		try {
			Object result = call(null, "User.login", paramslogin); //$NON-NLS-1$
			if (result instanceof HashMap<?, ?>) {
				HashMap<String, Integer> resultHash = (HashMap<String, Integer>) result;
				Integer resultUser = resultHash.get("id"); //$NON-NLS-1$
				userID = resultUser;
			}
		} catch (XmlRpcException e) {
			userID = -1;
		} catch (MalformedURLException e) {
			userID = -1;
		}
	}

	@SuppressWarnings("serial")
	public Object[] getUserInfoFromIDs(final Integer[] ids) {
		return getUserInfoInternal(new Object[] { new HashMap<String, Object[]>() {
			{
				put("ids", ids); //$NON-NLS-1$
			}
		} });
	}

	@SuppressWarnings("serial")
	public Object[] getUserInfoFromNames(final String[] names) {
		return getUserInfoInternal(new Object[] { new HashMap<String, Object[]>() {
			{
				put("names", names); //$NON-NLS-1$
			}
		} });
	}

	public Object[] getUserInfoWithMatch(String[] matchs) {
		HashMap<String, Object[]> parmArray = new HashMap<String, Object[]>();
		Object[] callParm = new Object[] { parmArray };
		parmArray.put("match", matchs); //$NON-NLS-1$
		return getUserInfoInternal(callParm);
	}

	@SuppressWarnings("unchecked")
	private Object[] getUserInfoInternal(Object[] callParm) {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null, "User.get", callParm); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			Object[] a = result.get("users"); //$NON-NLS-1$
			return a;
		}
		return null;
	}

	/*
	 * Modul Webservice Bugzilla 
	 */

	public String getVersion() {
		HashMap<?, ?> result;
		try {
			result = (HashMap<?, ?>) call(null, "Bugzilla.version", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return (String) result.get("version"); //$NON-NLS-1$
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Date> getTime() {
		HashMap<String, Date> result;
		try {
			result = (HashMap<String, Date>) call(null, "Bugzilla.time", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			result.remove("tz_offset"); //$NON-NLS-1$
			result.remove("tz_short_name"); //$NON-NLS-1$
			result.remove("web_time_utc"); //$NON-NLS-1$
			result.remove("tz_name"); //$NON-NLS-1$
		}
		return result;
	}

	public Date getDBTime() {
		HashMap<?, ?> result;
		try {
			result = (HashMap<?, ?>) call(null, "Bugzilla.time", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return (Date) result.get("db_time"); //$NON-NLS-1$
		}
		return null;
	}

	public Date getWebTime() {
		HashMap<?, ?> result;
		try {
			result = (HashMap<?, ?>) call(null, "Bugzilla.time", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return (Date) result.get("web_time"); //$NON-NLS-1$
		}
		return null;
	}

	/*
	 * Modul Webservice Bug 
	 */

	public Object[] getAllFields() {
		return getFieldsInternal(null);
	}

	@SuppressWarnings("serial")
	public Object[] getFieldsWithNames(final String[] names) {
		return getFieldsInternal(new Object[] { new HashMap<String, Object[]>() {
			{
				put("names", names); //$NON-NLS-1$
			}
		} });
	}

	@SuppressWarnings("serial")
	public Object[] getFieldsWithIDs(final Integer[] ids) {
		return getFieldsInternal(new Object[] { new HashMap<String, Object[]>() {
			{
				put("ids", ids); //$NON-NLS-1$
			}
		} });
	}

	@SuppressWarnings("unchecked")
	private Object[] getFieldsInternal(Object[] param) {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null, "Bug.fields", param); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return result.get("fields"); //$NON-NLS-1$
		}
		return null;

	}

	/*
	 * Modul Webservice Bug 
	 */
	public Object[] getSelectableProducts() {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null, "Product.get_selectable_products", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return result.get("ids"); //$NON-NLS-1$
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object[] getEnterableProducts() {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null, "Product.get_enterable_products", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return result.get("ids"); //$NON-NLS-1$
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object[] getAccessibleProducts() {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null, "Product.get_accessible_products", (Object[]) null); //$NON-NLS-1$
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return result.get("ids"); //$NON-NLS-1$
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "serial" })
	public Object[] getProducts(final Integer[] ids) {
		HashMap<String, Object[]> result;
		try {
			result = (HashMap<String, Object[]>) call(null,
					"Product.get", new Object[] { new HashMap<String, Object[]>() { //$NON-NLS-1$
						{
							put("ids", ids); //$NON-NLS-1$
						}
					} });
		} catch (XmlRpcException e) {
			result = null;
		} catch (MalformedURLException e) {
			result = null;
		}
		if (result != null) {
			return result.get("products"); //$NON-NLS-1$
		}
		return null;
	}

}
