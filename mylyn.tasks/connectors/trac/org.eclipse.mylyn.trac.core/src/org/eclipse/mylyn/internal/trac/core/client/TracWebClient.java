/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.core.HtmlTag;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.SslCertificateException;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.trac.core.model.TracComment;
import org.eclipse.mylyn.internal.trac.core.model.TracComponent;
import org.eclipse.mylyn.internal.trac.core.model.TracMilestone;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.internal.trac.core.model.TracRepositoryInfo;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.internal.trac.core.model.TracSeverity;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylyn.internal.trac.core.model.TracTicketType;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory.TracHttpException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Represents a Trac repository that is accessed through the Trac's query script and web interface.
 * 
 * @author Steffen Pingel
 */
public class TracWebClient extends AbstractTracClient {

	private interface AttributeFactory {

		void initialize();

		void addAttribute(String value);

	}

	private static class TracConfiguration {

		private final Map<String, AttributeFactory> factoryByField = new HashMap<String, AttributeFactory>();

		public TracConfiguration(final TracClientData data) {
			AttributeFactory attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.components.add(new TracComponent(value));
				}

				public void initialize() {
					data.components = new ArrayList<TracComponent>();
				}
			};
			factoryByField.put("component", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.milestones.add(new TracMilestone(value));
				}

				public void initialize() {
					data.milestones = new ArrayList<TracMilestone>();
				}
			};
			factoryByField.put("milestone", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.priorities.add(new TracPriority(value, data.priorities.size() + 1));
				}

				public void initialize() {
					data.priorities = new ArrayList<TracPriority>();
				}
			};
			factoryByField.put("priority", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.ticketResolutions.add(new TracTicketResolution(value, data.ticketResolutions.size() + 1));
				}

				public void initialize() {
					data.ticketResolutions = new ArrayList<TracTicketResolution>();
				}
			};
			factoryByField.put("resolution", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.severities.add(new TracSeverity(value, data.severities.size() + 1));
				}

				public void initialize() {
					data.severities = new ArrayList<TracSeverity>();
				}
			};
			factoryByField.put("severity", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.ticketStatus.add(new TracTicketStatus(value, data.ticketStatus.size() + 1));
				}

				public void initialize() {
					data.ticketStatus = new ArrayList<TracTicketStatus>();
				}
			};
			factoryByField.put("status", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.ticketTypes.add(new TracTicketType(value, data.ticketTypes.size() + 1));
				}

				public void initialize() {
					data.ticketTypes = new ArrayList<TracTicketType>();
				}
			};
			factoryByField.put("type", attributeFactory); //$NON-NLS-1$

			attributeFactory = new AttributeFactory() {
				public void addAttribute(String value) {
					data.versions.add(new TracVersion(value));
				}

				public void initialize() {
					data.versions = new ArrayList<TracVersion>();
				}
			};
			factoryByField.put("version", attributeFactory); //$NON-NLS-1$
		}

		public AttributeFactory getFactoryByField(String field) {
			return factoryByField.get(field);
		}

	}

	private static class TracConfigurationField {

		@SuppressWarnings("unused")
		String label;

		@SuppressWarnings("unused")
		String type;

		List<String> options;

		List<TracConfigurationOptGroup> optgroups;

	}

	private static class TracConfigurationOptGroup {

		@SuppressWarnings("unused")
		String label;

		List<String> options;

	}

	private class Request {

		private final String url;

		private HostConfiguration hostConfiguration;

		public Request(String url) {
			this.url = url;
		}

		public GetMethod execute(IProgressMonitor monitor) throws TracLoginException, IOException, TracHttpException {
			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);

			for (int attempt = 0; attempt < 2; attempt++) {
				// force authentication
				if (!authenticated) {
					AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
					if (credentialsValid(credentials)) {
						try {
							authenticate(monitor);
						} catch (TracLoginException e) {
							// re-try once, see bug 302792							
							authenticate(monitor);
						}
					}
				}

				GetMethod method = new GetMethod(WebUtil.getRequestPath(url));
				int code;
				try {
					code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
				} catch (IOException e) {
					WebUtil.releaseConnection(method, monitor);
					throw e;
				} catch (RuntimeException e) {
					WebUtil.releaseConnection(method, monitor);
					throw e;
				}

				if (code == HttpURLConnection.HTTP_OK) {
					return method;
				} else {
					WebUtil.releaseConnection(method, monitor);
					if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
						// login or re-authenticate due to an expired session
						authenticated = false;
						authenticate(monitor);
					} else {
						throw new TracHttpException(code);
					}
				}
			}

			throw new TracLoginException();
		}

		private void authenticate(IProgressMonitor monitor) throws TracLoginException, IOException {
			while (true) {
				AuthenticationCredentials credentials = location.getCredentials(AuthenticationType.REPOSITORY);
				if (!credentialsValid(credentials)) {
					throw new TracLoginException();
				}

				// try standard basic/digest/ntlm authentication first
				AuthScope authScope = new AuthScope(WebUtil.getHost(repositoryUrl), WebUtil.getPort(repositoryUrl),
						null, AuthScope.ANY_SCHEME);
				Credentials httpCredentials = WebUtil.getHttpClientCredentials(credentials,
						WebUtil.getHost(repositoryUrl));
				httpClient.getState().setCredentials(authScope, httpCredentials);
//				if (CoreUtil.TEST_MODE) {
//					System.err.println(" Setting credentials: " + httpCredentials); //$NON-NLS-1$
//				}

				GetMethod method = new GetMethod(WebUtil.getRequestPath(repositoryUrl + LOGIN_URL));
				method.setFollowRedirects(false);
				int code;
				try {
					code = WebUtil.execute(httpClient, hostConfiguration, method, monitor);
					if (needsReauthentication(code, monitor)) {
						continue;
					}
				} catch (SslCertificateException e) {
					if (needsReauthentication(SC_CERT_AUTH_FAILED, monitor)) {
						continue;
					}
					throw e;
				} finally {
					WebUtil.releaseConnection(method, monitor);
				}

				// the expected return code is a redirect, anything else is suspicious
				if (code == HttpURLConnection.HTTP_OK) {
					// try form-based authentication via AccountManagerPlugin as a
					// fall-back
					authenticateAccountManager(httpClient, hostConfiguration, credentials, monitor);
				}

				validateAuthenticationState(httpClient);

				// success since no exception was thrown
				authenticated = true;
				break;
			}
		}

		private boolean needsReauthentication(int code, IProgressMonitor monitor)
				throws IOException, TracLoginException {
			final AuthenticationType authenticationType;
			if (code == HttpStatus.SC_UNAUTHORIZED || code == HttpStatus.SC_FORBIDDEN) {
				authenticationType = AuthenticationType.REPOSITORY;
			} else if (code == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
				authenticationType = AuthenticationType.PROXY;
			} else if (code == SC_CERT_AUTH_FAILED) {
				authenticationType = AuthenticationType.CERTIFICATE;
			} else {
				return false;
			}

			try {
				location.requestCredentials(authenticationType, null, monitor);
			} catch (UnsupportedRequestException e) {
				throw new TracLoginException();
			}

			hostConfiguration = WebUtil.createHostConfiguration(httpClient, location, monitor);
			return true;
		}

	}

	private final HttpClient httpClient;

	private boolean authenticated;

	public TracWebClient(AbstractWebLocation location, Version version) {
		super(location, version);
		this.httpClient = createHttpClient();
	}

	private synchronized GetMethod connect(String requestUrl, IProgressMonitor monitor) throws TracException {
		monitor = Policy.monitorFor(monitor);
		try {
			Request request = new Request(requestUrl);
			return request.execute(monitor);
		} catch (TracException e) {
			throw e;
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	/**
	 * Fetches the web site of a single ticket and returns the Trac ticket.
	 * 
	 * @param id
	 *            Trac id of ticket
	 */
	public TracTicket getTicket(int id, IProgressMonitor monitor) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.TICKET_URL + id, monitor);
		try {
			TracTicket ticket = new TracTicket(id);

			InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, method.getResponseCharSet()));
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (token.getType() == Token.TAG) {
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == Tag.TD) {
							String headers = tag.getAttribute("headers"); //$NON-NLS-1$
							if ("h_component".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.COMPONENT, getText(tokenizer));
							} else if ("h_milestone".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.MILESTONE, getText(tokenizer));
							} else if ("h_priority".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.PRIORITY, getText(tokenizer));
							} else if ("h_severity".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.SEVERITY, getText(tokenizer));
							} else if ("h_version".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.VERSION, getText(tokenizer));
							} else if ("h_keywords".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.KEYWORDS, getText(tokenizer));
							} else if ("h_cc".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.CC, getText(tokenizer));
							} else if ("h_owner".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.OWNER, getText(tokenizer));
							} else if ("h_reporter".equals(headers)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.REPORTER, getText(tokenizer));
							}
							// TODO handle custom fields
						} else if ((tag.getTagType() == Tag.H2 && ("summary".equals(tag.getAttribute("class")) //$NON-NLS-1$//$NON-NLS-2$
								|| "summary searchable".equals(tag.getAttribute("class")))) //$NON-NLS-1$ //$NON-NLS-2$
								|| tag.getTagType() == Tag.SPAN && ("summary".equals(tag.getAttribute("class")))) { //$NON-NLS-1$ //$NON-NLS-2$ 
							ticket.putBuiltinValue(Key.SUMMARY, getText(tokenizer));
						} else if (tag.getTagType() == Tag.H3 && "status".equals(tag.getAttribute("class"))) { //$NON-NLS-1$ //$NON-NLS-2$
							String text = getStrongText(tokenizer);
							if (text.length() > 0) {
								// Trac 0.9 format: status / status (resolution)
								int i = text.indexOf(" ("); //$NON-NLS-1$
								if (i != -1) {
									ticket.putBuiltinValue(Key.STATUS, text.substring(0, i));
									ticket.putBuiltinValue(Key.RESOLUTION, text.substring(i + 2, text.length() - 1));
								} else {
									ticket.putBuiltinValue(Key.STATUS, text);
								}
							}
						} else if (tag.getTagType() == Tag.SPAN) {
							String clazz = tag.getAttribute("class"); //$NON-NLS-1$
							if ("status".equals(clazz)) { //$NON-NLS-1$
								// Trac 0.10 format: (status type) / (status type: resolution)
								String text = getText(tokenizer);
								if (text.startsWith("(") && text.endsWith(")")) { //$NON-NLS-1$ //$NON-NLS-2$
									StringTokenizer t = new StringTokenizer(text.substring(1, text.length() - 1), " :"); //$NON-NLS-1$
									if (t.hasMoreTokens()) {
										ticket.putBuiltinValue(Key.STATUS, t.nextToken());
									}
									if (t.hasMoreTokens()) {
										ticket.putBuiltinValue(Key.TYPE, t.nextToken());
									}
									if (t.hasMoreTokens()) {
										ticket.putBuiltinValue(Key.RESOLUTION, t.nextToken());
									}
								}
							} else if ("trac-status".equals(clazz)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.STATUS, getText(tokenizer));
							} else if ("trac-type".equals(clazz)) { //$NON-NLS-1$
								ticket.putBuiltinValue(Key.TYPE, getText(tokenizer));
							} else if ("trac-resolution".equals(clazz)) { //$NON-NLS-1$
								String text = getText(tokenizer);
								if (text.startsWith("(") && text.endsWith(")")) { //$NON-NLS-1$ //$NON-NLS-2$
									ticket.putBuiltinValue(Key.RESOLUTION, text.substring(1, text.length() - 1).trim());
								} else {
									ticket.putBuiltinValue(Key.RESOLUTION, text);
								}
							}

						}
						// TODO parse description
					}
				}
			} finally {
				in.close();
			}

			if (ticket.isValid() && ticket.getValue(Key.SUMMARY) != null) {
				return ticket;
			}

			throw new InvalidTicketException();
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	public void searchForTicketIds(TracSearch query, List<Integer> result, IProgressMonitor monitor)
			throws TracException {
		List<TracTicket> ticketResult = new ArrayList<TracTicket>();
		search(query, ticketResult, monitor);
		for (TracTicket tracTicket : ticketResult) {
			result.add(tracTicket.getId());
		}
	}

	public void search(TracSearch query, List<TracTicket> result, IProgressMonitor monitor) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.QUERY_URL + query.toUrl(), monitor);
		try {
			InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, method.getResponseCharSet()));

				WebSearchResultParser parser = new WebSearchResultParser();
				parser.parse(reader);

				Map<String, String> constantValues = getExactMatchValues(query);
				for (TracTicket ticket : parser.getTickets()) {
					if (ticket.isValid()) {
						for (String key : constantValues.keySet()) {
							ticket.putValue(key, WebSearchResultParser.parseTicketValue(constantValues.get(key)));
						}
						result.add(ticket);
					}
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new TracException(e);
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	/**
	 * Extracts constant values from <code>query</code>. The Trac query script does not return fields that matched
	 * exactly againt a single value.
	 */
	private Map<String, String> getExactMatchValues(TracSearch query) {
		Map<String, String> values = new HashMap<String, String>();
		List<TracSearchFilter> filters = query.getFilters();
		for (TracSearchFilter filter : filters) {
			if (filter.getOperator() == CompareOperator.IS && filter.getValues().size() == 1) {
				values.put(filter.getFieldName(), filter.getValues().get(0));
			}
		}
		return values;
	}

	public TracRepositoryInfo validate(IProgressMonitor monitor) throws TracException {
		GetMethod method = connect(repositoryUrl + "/", monitor); //$NON-NLS-1$
		try {
			return new TracRepositoryInfo();
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	@Override
	public void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask(Messages.TracWebClient_Updating_attributes, IProgressMonitor.UNKNOWN);

		GetMethod method = connect(repositoryUrl + ITracClient.CUSTOM_QUERY_URL, monitor);
		try {
			InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, method.getResponseCharSet()));
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}

					if (token.getType() == Token.TAG) {
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == Tag.SCRIPT) {
							String text = getText(tokenizer).trim();
							int i = text.indexOf("var properties="); //$NON-NLS-1$
							if (i != -1) {
								if (!parseAttributesJSon(text.substring(i))) {
									// fall back
									parseAttributesTokenizer(text.substring(i));
								}
							}
						}
					}
				}

				addResolutionAndStatus();
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	enum AttributeState {
		INIT, IN_LIST, IN_ATTRIBUTE_KEY, IN_ATTRIBUTE_VALUE, IN_ATTRIBUTE_VALUE_LIST
	};

	private boolean parseAttributesJSon(String text) {
		// remove surrounding JavaScript
		if (text.startsWith("var properties=")) { //$NON-NLS-1$
			text = text.substring("var properties=".length()); //$NON-NLS-1$
		}
		int i = text.indexOf("};"); //$NON-NLS-1$
		if (i != -1) {
			text = text.substring(0, i + 1);
		}

		// parse JSon stream
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		TypeToken<Map<String, TracConfigurationField>> type = new TypeToken<Map<String, TracConfigurationField>>() {
		};
		Map<String, TracConfigurationField> fieldByName;
		try {
			fieldByName = gson.fromJson(text, type.getType());
			if (fieldByName == null) {
				return false;
			}
		} catch (JsonSyntaxException e) {
			return false;
		}

		// copy parsed JSon objects in to client data 
		TracConfiguration configuration = new TracConfiguration(data);
		for (Map.Entry<String, TracConfigurationField> entry : fieldByName.entrySet()) {
			AttributeFactory factory = configuration.getFactoryByField(entry.getKey());
			if (factory != null) {
				factory.initialize();

				TracConfigurationField field = entry.getValue();
				if (field.options != null && field.options.size() > 0) {
					for (String option : field.options) {
						factory.addAttribute(option);
					}
				} else if (field.optgroups != null && field.optgroups.size() > 0) {
					// milestones in Trac 0.13 support groups for labeling related options: ignore groups but extract options  
					for (TracConfigurationOptGroup group : field.optgroups) {
						if (group.options != null) {
							for (String option : group.options) {
								factory.addAttribute(option);
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Parses the JavaScript code from the query page to extract repository configuration.
	 */
	private void parseAttributesTokenizer(String text) throws IOException {
		StreamTokenizer t = new StreamTokenizer(new StringReader(text));
		t.quoteChar('"');

		TracConfiguration configuration = new TracConfiguration(data);
		AttributeFactory attributeFactory = null;
		String attributeType = null;

		AttributeState state = AttributeState.INIT;
		int tokenType;
		while ((tokenType = t.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tokenType) {
			case StreamTokenizer.TT_WORD:
			case '"':
				if (state == AttributeState.IN_LIST) {
					attributeFactory = configuration.getFactoryByField(t.sval);
					if (attributeFactory != null) {
						attributeFactory.initialize();
					}
				} else if (state == AttributeState.IN_ATTRIBUTE_KEY) {
					attributeType = t.sval;
				} else if (state == AttributeState.IN_ATTRIBUTE_VALUE_LIST && "options".equals(attributeType)) { //$NON-NLS-1$
					if (attributeFactory != null) {
						attributeFactory.addAttribute(t.sval);
					}
				}
				break;
			case ':':
				if (state == AttributeState.IN_ATTRIBUTE_KEY) {
					state = AttributeState.IN_ATTRIBUTE_VALUE;
				}
				break;
			case ',':
				if (state == AttributeState.IN_ATTRIBUTE_VALUE) {
					state = AttributeState.IN_ATTRIBUTE_KEY;
				}
				break;
			case '[':
				if (state == AttributeState.IN_ATTRIBUTE_VALUE) {
					state = AttributeState.IN_ATTRIBUTE_VALUE_LIST;
				}
				break;
			case ']':
				if (state == AttributeState.IN_ATTRIBUTE_VALUE_LIST) {
					state = AttributeState.IN_ATTRIBUTE_VALUE;
				}
				break;
			case '{':
				if (state == AttributeState.INIT) {
					state = AttributeState.IN_LIST;
				} else if (state == AttributeState.IN_LIST) {
					state = AttributeState.IN_ATTRIBUTE_KEY;
				} else {
					throw new IOException("Error parsing attributes: unexpected token '{'"); //$NON-NLS-1$
				}
				break;
			case '}':
				if (state == AttributeState.IN_ATTRIBUTE_KEY || state == AttributeState.IN_ATTRIBUTE_VALUE) {
					state = AttributeState.IN_LIST;
				} else if (state == AttributeState.IN_LIST) {
					state = AttributeState.INIT;
				} else {
					throw new IOException("Error parsing attributes: unexpected token '}'"); //$NON-NLS-1$
				}
				break;
			}
		}
	}

	public void updateAttributesNewTicketPage(IProgressMonitor monitor) throws TracException {
		monitor.beginTask(Messages.TracWebClient_Updating_attributes, IProgressMonitor.UNKNOWN);

		GetMethod method = connect(repositoryUrl + ITracClient.NEW_TICKET_URL, monitor);
		try {
			InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, method.getResponseCharSet()));
				HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
				for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
					if (monitor.isCanceled()) {
						throw new OperationCanceledException();
					}

					if (token.getType() == Token.TAG) {
						HtmlTag tag = (HtmlTag) token.getValue();
						if (tag.getTagType() == Tag.SELECT) {
							String name = tag.getAttribute("id"); //$NON-NLS-1$
							if ("component".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.components = new ArrayList<TracComponent>(values.size());
								for (String value : values) {
									data.components.add(new TracComponent(value));
								}
							} else if ("milestone".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.milestones = new ArrayList<TracMilestone>(values.size());
								for (String value : values) {
									data.milestones.add(new TracMilestone(value));
								}
							} else if ("priority".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.priorities = new ArrayList<TracPriority>(values.size());
								for (int i = 0; i < values.size(); i++) {
									data.priorities.add(new TracPriority(values.get(i), i + 1));
								}
							} else if ("severity".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.severities = new ArrayList<TracSeverity>(values.size());
								for (int i = 0; i < values.size(); i++) {
									data.severities.add(new TracSeverity(values.get(i), i + 1));
								}
							} else if ("type".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.ticketTypes = new ArrayList<TracTicketType>(values.size());
								for (int i = 0; i < values.size(); i++) {
									data.ticketTypes.add(new TracTicketType(values.get(i), i + 1));
								}
							} else if ("version".equals(name)) { //$NON-NLS-1$
								List<String> values = getOptionValues(tokenizer);
								data.versions = new ArrayList<TracVersion>(values.size());
								for (String value : values) {
									data.versions.add(new TracVersion(value));
								}
							}
						}
					}
				}

				addResolutionAndStatus();
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			WebUtil.releaseConnection(method, monitor);
		}
	}

	private void addResolutionAndStatus() {
		if (data.ticketResolutions == null || data.ticketResolutions.isEmpty()) {
			data.ticketResolutions = new ArrayList<TracTicketResolution>(5);
			data.ticketResolutions.add(new TracTicketResolution("fixed", 1)); //$NON-NLS-1$
			data.ticketResolutions.add(new TracTicketResolution("invalid", 2)); //$NON-NLS-1$
			data.ticketResolutions.add(new TracTicketResolution("wontfix", 3)); //$NON-NLS-1$
			data.ticketResolutions.add(new TracTicketResolution("duplicate", 4)); //$NON-NLS-1$
			data.ticketResolutions.add(new TracTicketResolution("worksforme", 5)); //$NON-NLS-1$
		}

		if (data.ticketStatus == null || data.ticketStatus.isEmpty()) {
			data.ticketStatus = new ArrayList<TracTicketStatus>(4);
			data.ticketStatus.add(new TracTicketStatus("new", 1)); //$NON-NLS-1$
			data.ticketStatus.add(new TracTicketStatus("assigned", 2)); //$NON-NLS-1$
			data.ticketStatus.add(new TracTicketStatus("reopened", 3)); //$NON-NLS-1$
			data.ticketStatus.add(new TracTicketStatus("closed", 4)); //$NON-NLS-1$
		}
	}

	private List<String> getOptionValues(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		List<String> values = new ArrayList<String>();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == Tag.OPTION && !tag.isEndTag()) {
					String value = getText(tokenizer).trim();
					if (value.length() > 0) {
						values.add(value);
					}
				} else {
					return values;
				}
			}
		}
		return values;
	}

	private String getText(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		StringBuilder sb = new StringBuilder();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TEXT) {
				sb.append(token.toString().trim());
				sb.append(" "); //$NON-NLS-1$
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else if (token.getType() == Token.TAG && ((HtmlTag) token.getValue()).getTagType() == Tag.A) {
				// ignore, Trac 0.11 wraps milestone values in links
			} else {
				break;
			}
		}
		return StringEscapeUtils.unescapeHtml(sb.toString().trim());
	}

	/**
	 * Looks for a <code>strong</code> tag and returns the text enclosed by the tag.
	 */
	private String getStrongText(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG && ((HtmlTag) token.getValue()).getTagType() == Tag.STRONG) {
				return getText(tokenizer);
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else if (token.getType() == Token.TEXT) {
				// ignore
			} else {
				break;
			}
		}
		return ""; //$NON-NLS-1$
	}

	public InputStream getAttachmentData(int id, String filename, IProgressMonitor monitor) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.ATTACHMENT_URL + id + "/" + filename + "?format=raw", //$NON-NLS-1$ //$NON-NLS-2$
				monitor);
		try {
			// the receiver is responsible for closing the stream which will
			// release the connection
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			WebUtil.releaseConnection(method, monitor);
			throw new TracException(e);
		}
	}

	public void putAttachmentData(int id, String name, String description, InputStream in, IProgressMonitor monitor,
			boolean replace) throws TracException {
		throw new TracException("Unsupported operation"); //$NON-NLS-1$
	}

	public void deleteAttachment(int ticketId, String filename, IProgressMonitor monitor) throws TracException {
		throw new TracException("Unsupported operation"); //$NON-NLS-1$
	}

	public int createTicket(TracTicket ticket, IProgressMonitor monitor) throws TracException {
		throw new TracException("Unsupported operation"); //$NON-NLS-1$
	}

	public void updateTicket(TracTicket ticket, String comment, IProgressMonitor monitor) throws TracException {
		throw new TracException("Unsupported operation"); //$NON-NLS-1$
	}

	public Set<Integer> getChangedTickets(Date since, IProgressMonitor monitor) throws TracException {
		return null;
	}

	public Date getTicketLastChanged(Integer id, IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	public void deleteTicket(int ticketId, IProgressMonitor monitor) throws TracException {
		throw new UnsupportedOperationException();
	}

	public List<TracComment> getComments(int id, IProgressMonitor monitor) throws TracException {
		throw new UnsupportedOperationException();
	}

}
