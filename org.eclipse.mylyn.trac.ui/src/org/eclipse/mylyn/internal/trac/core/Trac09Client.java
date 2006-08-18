/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.tasks.core.HtmlTag;
import org.eclipse.mylar.internal.tasks.core.WebClientUtil;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.internal.trac.core.TracHttpClientTransportFactory.TracHttpException;
import org.eclipse.mylar.internal.trac.model.TracComponent;
import org.eclipse.mylar.internal.trac.model.TracMilestone;
import org.eclipse.mylar.internal.trac.model.TracPriority;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracSearchFilter;
import org.eclipse.mylar.internal.trac.model.TracSeverity;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.model.TracTicketType;
import org.eclipse.mylar.internal.trac.model.TracVersion;
import org.eclipse.mylar.internal.trac.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * Represents a Trac repository that is accessed through the Trac's query script
 * and web interface.
 * 
 * @author Steffen Pingel
 */
public class Trac09Client extends AbstractTracClient {

	private HttpClient httpClient = new HttpClient();

	private boolean authenticated;

	public Trac09Client(URL url, Version version, String username, String password) {
		super(url, version, username, password);
	}

	private GetMethod connect(String serverURL) throws TracException {
		try {
			return connectInternal(serverURL);
		} catch (TracException e) {
			throw e;
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private GetMethod connectInternal(String serverURL) throws TracLoginException, IOException, TracHttpException {
		WebClientUtil.setupHttpClient(httpClient, TasksUiPlugin.getDefault().getProxySettings(), serverURL);

		for (int attempt = 0; attempt < 2; attempt++) {
			// force authentication
			if (!authenticated && hasAuthenticationCredentials()) {
				authenticate();
			}

			GetMethod method = new GetMethod(WebClientUtil.getRequestPath(serverURL));
			method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			int code;
			try {
				code = httpClient.executeMethod(method);
			} catch (IOException e) {
				method.releaseConnection();
				throw e;
			}

			if (code == HttpURLConnection.HTTP_OK) {
				return method;
			} else if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				// login or reauthenticate due to an expired session
				method.releaseConnection();
				authenticated = false;
				authenticate();
			} else {
				throw new TracHttpException(code);
			}
		}

		throw new TracLoginException();
	}

	private void authenticate() throws TracLoginException, IOException {
		if (!hasAuthenticationCredentials()) {
			throw new TracLoginException();
		}

		Credentials credentials = new UsernamePasswordCredentials(username, password);
		httpClient.getState().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), credentials);

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		method.setFollowRedirects(false);

		try {
			httpClient.getParams().setAuthenticationPreemptive(true);
			int code = httpClient.executeMethod(method);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new TracLoginException();
			}
		} finally {
			method.releaseConnection();
			httpClient.getParams().setAuthenticationPreemptive(false);
		}

		authenticated = true;
	}

	/**
	 * Fetches the web site of a single ticket and returns the Trac ticket.
	 * 
	 * @param id
	 *            Trac id of ticket
	 * @throws LoginException
	 */
	public TracTicket getTicket(int id) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.TICKET_URL + id);
		try {
			TracTicket ticket = new TracTicket(id);

			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					ITracClient.CHARSET));
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.TD) {
						String headers = tag.getAttribute("headers");
						if ("h_component".equals(headers)) {
							ticket.putBuiltinValue(Key.COMPONENT, getText(tokenizer));
						} else if ("h_milestone".equals(headers)) {
							ticket.putBuiltinValue(Key.MILESTONE, getText(tokenizer));
						} else if ("h_priority".equals(headers)) {
							ticket.putBuiltinValue(Key.PRIORITY, getText(tokenizer));
						} else if ("h_severity".equals(headers)) {
							ticket.putBuiltinValue(Key.SEVERITY, getText(tokenizer));
						} else if ("h_version".equals(headers)) {
							ticket.putBuiltinValue(Key.VERSION, getText(tokenizer));
						} else if ("h_keywords".equals(headers)) {
							ticket.putBuiltinValue(Key.KEYWORDS, getText(tokenizer));
						} else if ("h_cc".equals(headers)) {
							ticket.putBuiltinValue(Key.CC, getText(tokenizer));
						} else if ("h_owner".equals(headers)) {
							ticket.putBuiltinValue(Key.OWNER, getText(tokenizer));
						} else if ("h_reporter".equals(headers)) {
							ticket.putBuiltinValue(Key.REPORTER, getText(tokenizer));
						}
						// TODO handle custom fields
					} else if (tag.getTagType() == HtmlTag.Type.H2 && "summary".equals(tag.getAttribute("class"))) {
						ticket.putBuiltinValue(Key.SUMMARY, getText(tokenizer));
					} else if (tag.getTagType() == HtmlTag.Type.H3 && "status".equals(tag.getAttribute("class"))) {
						String text = getStrongText(tokenizer);
						if (text.length() > 0) {
							int i = text.indexOf(" (");
							if (i != -1) {
								// status contains resolution as well
								ticket.putBuiltinValue(Key.STATUS, text.substring(0, i));
								ticket.putBuiltinValue(Key.RESOLUTION, text.substring(i, text.length() - 1));
							} else {
								ticket.putBuiltinValue(Key.STATUS, text);
							}
						}
					}
					// TODO parse description
				}
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
			method.releaseConnection();
		}
	}

	public void search(TracSearch query, List<TracTicket> tickets) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.QUERY_URL + query.toUrl());
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					ITracClient.CHARSET));
			String line;

			Map<String, String> constantValues = getExactMatchValues(query);

			// first line contains names of returned ticket fields
			line = reader.readLine();
			if (line == null) {
				throw new InvalidTicketException();
			}
			StringTokenizer t = new StringTokenizer(line, "\t");
			Key[] fields = new Key[t.countTokens()];
			for (int i = 0; i < fields.length; i++) {
				fields[i] = Key.fromKey(t.nextToken());
			}

			// create a ticket for each following line of output
			while ((line = reader.readLine()) != null) {
				t = new StringTokenizer(line, "\t");
				TracTicket ticket = new TracTicket();
				for (int i = 0; i < fields.length && t.hasMoreTokens(); i++) {
					if (fields[i] != null) {
						try {
							if (fields[i] == Key.ID) {
								ticket.setId(Integer.parseInt(t.nextToken()));
							} else if (fields[i] == Key.TIME) {
								ticket.setCreated(Integer.parseInt(t.nextToken()));
							} else if (fields[i] == Key.CHANGE_TIME) {
								ticket.setLastChanged(Integer.parseInt(t.nextToken()));
							} else {
								ticket.putBuiltinValue(fields[i], parseTicketValue(t.nextToken()));
							}
						} catch (NumberFormatException e) {
							MylarStatusHandler.log(e, "Error parsing repsonse: " + line);
						}
					}
				}

				if (ticket.isValid()) {
					for (String key : constantValues.keySet()) {
						ticket.putValue(key, parseTicketValue(constantValues.get(key)));
					}

					tickets.add(ticket);
				}
			}
		} catch (IOException e) {
			throw new TracException(e);
		} finally {
			method.releaseConnection();
		}
	}

	/**
	 * Trac has sepcial encoding rules for the returned output: None is
	 * represented by "--".
	 */
	private String parseTicketValue(String value) {
		if ("--".equals(value)) {
			return "";
		}
		return value;
	}

	/**
	 * Extracts constant values from <code>query</code>. The Trac query
	 * script does not return fields that matched exactly againt a single value.
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

	public void validate() throws TracException {
		GetMethod method = connect(repositoryUrl + "/");
		method.releaseConnection();
	}

	public void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", IProgressMonitor.UNKNOWN);

		GetMethod method = connect(repositoryUrl + ITracClient.NEW_TICKET_URL);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					ITracClient.CHARSET));
			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}

				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.SELECT) {
						String name = tag.getAttribute("id");
						if ("component".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.components = new ArrayList<TracComponent>(values.size());
							for (String value : values) {
								data.components.add(new TracComponent(value));
							}
						} else if ("milestone".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.milestones = new ArrayList<TracMilestone>(values.size());
							for (String value : values) {
								data.milestones.add(new TracMilestone(value));
							}
						} else if ("priority".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.priorities = new ArrayList<TracPriority>(values.size());
							for (int i = 0; i < values.size(); i++) {
								data.priorities.add(new TracPriority(values.get(i), i + 1));
							}
						} else if ("severity".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.severities = new ArrayList<TracSeverity>(values.size());
							for (int i = 0; i < values.size(); i++) {
								data.severities.add(new TracSeverity(values.get(i), i + 1));
							}
						} else if ("type".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.ticketTypes = new ArrayList<TracTicketType>(values.size());
							for (int i = 0; i < values.size(); i++) {
								data.ticketTypes.add(new TracTicketType(values.get(i), i + 1));
							}
						} else if ("version".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							data.versions = new ArrayList<TracVersion>(values.size());
							for (String value : values) {
								data.versions.add(new TracVersion(value));
							}
						}
					}
				}
			}

			data.ticketResolutions = new ArrayList<TracTicketResolution>(5);
			data.ticketResolutions.add(new TracTicketResolution("fixed", 1));
			data.ticketResolutions.add(new TracTicketResolution("invalid", 2));
			data.ticketResolutions.add(new TracTicketResolution("wontfix", 3));
			data.ticketResolutions.add(new TracTicketResolution("duplicate", 4));
			data.ticketResolutions.add(new TracTicketResolution("worksforme", 5));

			data.ticketStatus = new ArrayList<TracTicketStatus>(4);
			data.ticketStatus.add(new TracTicketStatus("new", 1));
			data.ticketStatus.add(new TracTicketStatus("assigned", 2));
			data.ticketStatus.add(new TracTicketStatus("reopened", 3));
			data.ticketStatus.add(new TracTicketStatus("closed", 4));
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			method.releaseConnection();
		}
	}

	private List<String> getOptionValues(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		List<String> values = new ArrayList<String>();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG) {
				HtmlTag tag = (HtmlTag) token.getValue();
				if (tag.getTagType() == HtmlTag.Type.OPTION && !tag.isEndTag()) {
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
		StringBuffer sb = new StringBuffer();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TEXT) {
				sb.append(token.toString());
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else {
				break;
			}
		}
		return HtmlStreamTokenizer.unescape(sb).toString();
	}

	/**
	 * Looks for a <code>strong</code> tag and returns the text enclosed by
	 * the tag.
	 */
	private String getStrongText(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TAG && ((HtmlTag) token.getValue()).getTagType() == HtmlTag.Type.STRONG) {
				return getText(tokenizer);
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else if (token.getType() == Token.TEXT) {
				// ignore
			} else {
				break;
			}
		}
		return "";
	}

	public byte[] getAttachmentData(int id, String filename) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public void putAttachmentData(int id, String name, String description, byte[] data) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public void createTicket(TracTicket ticket) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public void updateTicket(TracTicket ticket, String comment) throws TracException {
		throw new TracException("Unsupported operation");
	}

}
