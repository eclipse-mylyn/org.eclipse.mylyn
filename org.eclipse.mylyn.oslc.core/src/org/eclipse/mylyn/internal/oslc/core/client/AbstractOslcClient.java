/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.oslc.core.IOslcCoreConstants;
import org.eclipse.mylyn.internal.oslc.core.OslcCreationDialogDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcSelectionDialogDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceFactory;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProviderCatalog;
import org.eclipse.mylyn.internal.oslc.core.ServiceHome;
import org.eclipse.mylyn.internal.oslc.core.cm.AbstractChangeRequest;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

/**
 * Base class from which to implement an OSLC client
 * 
 * @author Robert Elves
 */
public abstract class AbstractOslcClient {

	protected final AbstractWebLocation location;

	protected final HttpClient httpClient;

	protected final OslcServiceDescriptor configuration;

	public AbstractOslcClient(AbstractWebLocation location, OslcServiceDescriptor data) {
		this.location = location;
		this.httpClient = createHttpClient();
		this.configuration = data;
		configureHttpCredentials(location);
	}

	protected void configureHttpCredentials(AbstractWebLocation location) {
		AuthScope authScope = new AuthScope(WebUtil.getHost(location.getUrl()), WebUtil.getPort(location.getUrl()),
				null, AuthScope.ANY_SCHEME);

		AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);

		if (credentialsValid(credentials)) {
			Credentials creds = WebUtil.getHttpClientCredentials(credentials, WebUtil.getHost(location.getUrl()));
			httpClient.getState().setCredentials(authScope, creds);
			this.httpClient.getParams().setAuthenticationPreemptive(true);
		} else {
			httpClient.getState().clearCredentials();
		}
	}

	protected boolean credentialsValid(AuthenticationCredentials credentials) {
		return credentials != null && credentials.getUserName().length() > 0;
	}

	protected HttpClient createHttpClient() {
		HttpClient httpClient = new HttpClient();
		httpClient.setHttpConnectionManager(WebUtil.getConnectionManager());
		httpClient.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

		// See: https://jazz.net/jazz/web/projects/Rational%20Team%20Concert#action=com.ibm.team.workitem.viewWorkItem&id=85127\
		// Added to support fix session cookie issue when talking to tomcat
		httpClient.getParams().setParameter("http.protocol.single-cookie-header", true); //$NON-NLS-1$

		WebUtil.configureHttpClient(httpClient, getUserAgent());
		return httpClient;
	}

	/**
	 * Return your unique connector identifier i.e. com.mycompany.myconnector
	 */
	public abstract String getUserAgent();

	/**
	 * Exposed at connector level via IOslcCoreConnector.getAvailableServices()
	 */
	public List<OslcServiceProvider> getAvailableServices(String url, IProgressMonitor monitor) throws CoreException {

		RequestHandler<List<OslcServiceProvider>> handler = new RequestHandler<List<OslcServiceProvider>>(
				"Requesting Available Services") { //$NON-NLS-1$

			@Override
			public List<OslcServiceProvider> run(HttpMethodBase method, IProgressMonitor monitor) throws CoreException {
				try {
					final List<OslcServiceProvider> result = new ArrayList<OslcServiceProvider>();
					parseServices(method.getResponseBodyAsStream(), result, monitor);
					return result;
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
							"Network error occurred retrieving available services: " + e.getMessage(), e)); //$NON-NLS-1$
				}
			}
		};

		return executeMethod(createGetMethod(url), handler, monitor);
	}

	protected Document getDocumentFromMethod(HttpMethodBase method) throws CoreException {
		try {
			return getDocumentFromStream(method.getResponseBodyAsStream());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
					"Network error obtaining response from server: " + e.getMessage(), e)); //$NON-NLS-1$
		}
	}

	protected Document getDocumentFromStream(InputStream inStream) throws CoreException {
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		try {
			return builder.build(inStream);
		} catch (JDOMException e) {
			throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
					"Error parsing response: " + e.getMessage(), e)); //$NON-NLS-1$
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
					"Network error parsing response: " + e.getMessage(), e)); //$NON-NLS-1$
		}
	}

	/**
	 * public for testing
	 */
	public void parseServices(InputStream inStream, Collection<OslcServiceProvider> providers, IProgressMonitor monitor)
			throws CoreException {

		Document doc = getDocumentFromStream(inStream);

		Iterator<?> itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_SERVICE_PROVIDER_CATALOG));
		while (itr.hasNext()) {
			Element element = (Element) itr.next();
			if (element != doc.getRootElement()) {
				Attribute attrAbout = element.getAttribute(IOslcCoreConstants.ATTRIBUTE_ABOUT,
						IOslcCoreConstants.NAMESPACE_RDF);
				String title = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC)
						.getText();
				if (attrAbout != null && attrAbout.getValue().length() > 0) {
					providers.add(new OslcServiceProviderCatalog(title, attrAbout.getValue()));
				}
			}
		}
		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_SERVICE_PROVIDER));
		while (itr.hasNext()) {
			Element element = (Element) itr.next();
			String title = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC)
					.getText();
			Element service = element.getChild(IOslcCoreConstants.ELEMENT_SERVICES,
					IOslcCoreConstants.NAMESPACE_OSLC_DISCOVERY_1_0);
			if (service != null) {
				String resource = service.getAttributeValue(IOslcCoreConstants.ATTRIBUTE_RESOURCE,
						IOslcCoreConstants.NAMESPACE_RDF);
				providers.add(new OslcServiceProvider(title, resource));
			}
		}
	}

	/**
	 * Retrieve a service descriptor for the given service provider. Exposed at connector level by
	 * IOslcConnector.getServiceDescriptor()
	 * 
	 * @throws CoreException
	 */
	public OslcServiceDescriptor getServiceDescriptor(OslcServiceProvider provider, IProgressMonitor monitor)
			throws CoreException {
		OslcServiceDescriptor configuration = new OslcServiceDescriptor(provider.getUrl());
		downloadServiceDescriptor(configuration, monitor);
		return configuration;
	}

	/**
	 * Populate the provided configuration with new data from the remote repository.
	 */
	protected void downloadServiceDescriptor(final OslcServiceDescriptor config, IProgressMonitor monitor)
			throws CoreException {

		RequestHandler<OslcServiceDescriptor> handler = new RequestHandler<OslcServiceDescriptor>(
				"Retrieving Service Descriptor") { //$NON-NLS-1$

			@Override
			public OslcServiceDescriptor run(HttpMethodBase method, IProgressMonitor monitor) throws CoreException,
					IOException {
				config.clear();
				parseServiceDescriptor(method.getResponseBodyAsStream(), config, monitor);
				return config;
			}
		};

		executeMethod(createGetMethod(config.getAboutUrl()), handler, monitor);

	}

	/**
	 * public for testing
	 */
	public void parseServiceDescriptor(InputStream inStream, OslcServiceDescriptor config, IProgressMonitor monitor)
			throws CoreException {
		Document doc = getDocumentFromStream(inStream);

		Iterator<?> itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_TITLE,
				IOslcCoreConstants.NAMESPACE_DC));
		if (itr.hasNext()) {
			Element element = (Element) itr.next();
			config.setTitle(element.getText());
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_DESCRIPTION,
				IOslcCoreConstants.NAMESPACE_DC));
		if (itr.hasNext()) {
			Element element = (Element) itr.next();
			config.setDescription(element.getText());
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_CREATIONDIALOG,
				IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0));
		while (itr.hasNext()) {
			boolean isDefault = false;
			Element element = (Element) itr.next();
			String label = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC)
					.getText();
			String url = element.getChild(IOslcCoreConstants.ELEMENT_URL, IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0)
					.getText();
			Attribute attrDefault = element.getAttribute(IOslcCoreConstants.ATTRIBUTE_DEFAULT,
					IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
			if (attrDefault != null && attrDefault.getValue().equals("true")) { //$NON-NLS-1$
				isDefault = true;
			}
			OslcCreationDialogDescriptor recordType = new OslcCreationDialogDescriptor(label, url);
			config.addCreationDialog(recordType);
			if (isDefault) {
				config.setDefaultCreationDialog(recordType);
			}
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_SIMPLEQUERY));
		if (itr.hasNext()) {
			Element element = (Element) itr.next();
			String url = element.getChild(IOslcCoreConstants.ELEMENT_URL, IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0)
					.getText();
			if (url != null) {
				config.setSimpleQueryUrl(url);
			}
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_FACTORY));
		while (itr.hasNext()) {
			boolean isDefault = false;
			Element element = (Element) itr.next();
			String title = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC)
					.getText();
			String url = element.getChild(IOslcCoreConstants.ELEMENT_URL, IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0)
					.getText();
			if (element.getAttribute(IOslcCoreConstants.ATTRIBUTE_DEFAULT) != null
					&& element.getAttribute(IOslcCoreConstants.ATTRIBUTE_DEFAULT).getValue().equals("true")) { //$NON-NLS-1$
				isDefault = true;
			}
			OslcServiceFactory factory = new OslcServiceFactory(title, url);
			if (isDefault) {
				config.setDefaultFactory(factory);
			}
			config.addServiceFactory(factory);
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_HOME));
		if (itr.hasNext()) {
			Element element = (Element) itr.next();
			Element childTitle = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC);
			Element childUrl = element.getChild(IOslcCoreConstants.ELEMENT_URL,
					IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
			if (childTitle != null && childTitle.getText().length() > 0 && childUrl != null
					&& childUrl.getText().length() > 0) {
				ServiceHome home = new ServiceHome(childTitle.getText(), childUrl.getText());
				config.setHome(home);
			}
		}

		itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_SELECTIONDIALOG));
		if (itr.hasNext()) {
			Element element = (Element) itr.next();
			Element childTitle = element.getChild(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC);
			Element childUrl = element.getChild(IOslcCoreConstants.ELEMENT_URL,
					IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
			if (childTitle != null && childTitle.getText().length() > 0 && childUrl != null
					&& childUrl.getText().length() > 0) {

				OslcSelectionDialogDescriptor selection = new OslcSelectionDialogDescriptor(childTitle.getText(),
						childUrl.getText());

				String isDefault = element.getAttributeValue(IOslcCoreConstants.ATTRIBUTE_DEFAULT,
						IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
				if (isDefault != null) {
					selection.setDefault(isDefault.equals("true")); //$NON-NLS-1$
				}

				String hintHeight = element.getAttributeValue(IOslcCoreConstants.ATTRIBUTE_HINTHEIGHT,
						IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
				if (hintHeight != null) {
					selection.setHintHeight(hintHeight);
				}

				String hintWidth = element.getAttributeValue(IOslcCoreConstants.ATTRIBUTE_HINTWIDTH,
						IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
				if (hintWidth != null) {
					selection.setHintWidth(hintWidth);
				}

				String label = element.getChildText(IOslcCoreConstants.ELEMENT_LABEL,
						IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0);
				if (label != null) {
					selection.setLabel(label);
				}

				config.addSelectionDialog(selection);
			}
		}

	}

	public Collection<AbstractChangeRequest> performQuery(String queryUrl, IProgressMonitor monitor)
			throws CoreException {

		RequestHandler<Collection<AbstractChangeRequest>> handler = new RequestHandler<Collection<AbstractChangeRequest>>(
				"Performing Query") { //$NON-NLS-1$

			@Override
			public Collection<AbstractChangeRequest> run(HttpMethodBase method, IProgressMonitor monitor)
					throws CoreException, IOException {
				Collection<AbstractChangeRequest> result = new ArrayList<AbstractChangeRequest>();
				parseQueryResponse(method.getResponseBodyAsStream(), result, monitor);
				return result;
			}
		};

		return executeMethod(createGetMethod(queryUrl), handler, monitor);

	}

	// TODO: Handle pagination
	public void parseQueryResponse(InputStream inStream, Collection<AbstractChangeRequest> requests,
			IProgressMonitor monitor) throws CoreException {
		Document doc = getDocumentFromStream(inStream);

		Iterator<?> itr = doc.getDescendants(new ElementFilter(IOslcCoreConstants.ELEMENT_CHANGEREQUEST,
				IOslcCoreConstants.NAMESPACE_OSLC_CM_1_0));
		while (itr.hasNext()) {
			Element element = (Element) itr.next();
			String title = element.getChildText(IOslcCoreConstants.ELEMENT_TITLE, IOslcCoreConstants.NAMESPACE_DC);
			String id = element.getChildText(IOslcCoreConstants.ELEMENT_IDENTIFIER, IOslcCoreConstants.NAMESPACE_DC);

			if (title != null && id != null) {
				AbstractChangeRequest request = createChangeRequest(id, title);
				request.setType(element.getChildText(IOslcCoreConstants.ELEMENT_TYPE, IOslcCoreConstants.NAMESPACE_DC));
				request.setDescription(element.getChildText(IOslcCoreConstants.ELEMENT_DESCRIPTION,
						IOslcCoreConstants.NAMESPACE_DC));
				request.setSubject(element.getChildText(IOslcCoreConstants.ELEMENT_SUBJECT,
						IOslcCoreConstants.NAMESPACE_DC));
				request.setCreator(element.getChildText(IOslcCoreConstants.ELEMENT_CREATOR,
						IOslcCoreConstants.NAMESPACE_DC));
				request.setModified(element.getChildText(IOslcCoreConstants.ELEMENT_MODIFIED,
						IOslcCoreConstants.NAMESPACE_DC));
				requests.add(request);
			}

		}
	}

	protected abstract AbstractChangeRequest createChangeRequest(String id, String title);

	/**
	 * Updates this clients 'repository configuration'. If old types were in use (locally cached) and still exist they
	 * are re-read from repository.
	 */
	public void updateRepositoryConfiguration(IProgressMonitor monitor) throws CoreException {
		configuration.clear();
		downloadServiceDescriptor(configuration, monitor);
	}

	public abstract TaskData getTaskData(final String encodedTaskId, TaskAttributeMapper mapper,
			IProgressMonitor monitor) throws CoreException;

	public abstract RepositoryResponse putTaskData(TaskData taskData, Set<TaskAttribute> oldValues,
			IProgressMonitor monitor) throws CoreException;

	protected GetMethod createGetMethod(String requestPath) {
		GetMethod method = new GetMethod(getRequestPath(requestPath));
		method.setFollowRedirects(true);
		method.setDoAuthentication(true);
		// application/xml is returned by oslc servers by default (but some may not play nice)
		method.setRequestHeader("Accept", "application/xml"); //$NON-NLS-1$ //$NON-NLS-2$
		return method;
	}

	protected PostMethod createPostMethod(String requestPath) {
		PostMethod method = new PostMethod(getRequestPath(requestPath));
		method.setFollowRedirects(false);
		method.setDoAuthentication(true);
//		this.entity = getRequestEntity(method);
//		if (pairs != null) {
//			method.setRequestBody(pairs);
//		} else if (entity != null) {
//			method.setRequestEntity(entity);
//		} else {
//			StatusHandler.log(new Status(IStatus.WARNING, IOslcCoreConstants.ID_PLUGIN,
//					"Request body or entity missing upon post.")); //$NON-NLS-1$
//		}
		return method;
	}

	protected PutMethod createPutMethod(String requestPath) {
		PutMethod method = new PutMethod(getRequestPath(requestPath));
		method.setFollowRedirects(false);
		method.setDoAuthentication(true);
		return method;
	}

	protected <T> T executeMethod(HttpMethodBase method, RequestHandler<T> handler, IProgressMonitor monitor)
			throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask(handler.getRequestName(), IProgressMonitor.UNKNOWN);

			HostConfiguration hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			int code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);

			handler.handleReturnCode(code, method);

			return handler.run(method, monitor);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.WARNING, IOslcCoreConstants.ID_PLUGIN,
					"An unexpected network error has occurred: " + e.getMessage(), e)); //$NON-NLS-1$
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
			monitor.done();
		}

	}

	public String getRequestPath(String repositoryUrl) {
		if (repositoryUrl.startsWith("./")) { //$NON-NLS-1$
			return WebUtil.getRequestPath(location.getUrl()) + repositoryUrl.substring(1);
		} else if (repositoryUrl.startsWith("/")) { //$NON-NLS-1$
			return WebUtil.getRequestPath(location.getUrl()) + repositoryUrl;
		}
		return WebUtil.getRequestPath(repositoryUrl);
	}

	public abstract class RequestHandler<T> {

		private final String requestName;

		public RequestHandler(String requestName) {
			this.requestName = requestName;
		}

		public abstract T run(HttpMethodBase method, IProgressMonitor monitor) throws CoreException, IOException;

		public String getRequestName() {
			return requestName;
		}

		protected void handleReturnCode(int code, HttpMethodBase method) throws CoreException {
			try {
				if (code == java.net.HttpURLConnection.HTTP_OK) {
					return;// Status.OK_STATUS;
				} else if (code == java.net.HttpURLConnection.HTTP_MOVED_TEMP
						|| code == java.net.HttpURLConnection.HTTP_CREATED) {
					// A new resource created...
					return;// Status.OK_STATUS;
				} else if (code == java.net.HttpURLConnection.HTTP_UNAUTHORIZED
						|| code == java.net.HttpURLConnection.HTTP_FORBIDDEN) {
					throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
							"Unable to log into server, ensure repository credentials are correct.")); //$NON-NLS-1$
				} else if (code == java.net.HttpURLConnection.HTTP_PRECON_FAILED) {
					// Mid-air collision
					throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
							"Mid-air collision occurred.")); //$NON-NLS-1$
				} else if (code == java.net.HttpURLConnection.HTTP_CONFLICT) {
					throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN,
							"A conflict occurred.")); //$NON-NLS-1$
				} else {
					throw new CoreException(
							new Status(
									IStatus.ERROR,
									IOslcCoreConstants.ID_PLUGIN,
									"Unknown error occurred. Http Code: " + code + " Request: " + method.getURI() + " Response: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
											+ method.getResponseBodyAsString()));
				}
			} catch (URIException e) {
				throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN, "Network Error: " //$NON-NLS-1$
						+ e.getMessage()));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, IOslcCoreConstants.ID_PLUGIN, "Network Error: " //$NON-NLS-1$
						+ e.getMessage()));
			}
		}

	}
}