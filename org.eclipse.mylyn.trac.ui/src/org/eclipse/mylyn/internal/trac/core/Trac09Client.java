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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer;
import org.eclipse.mylar.internal.tasks.core.HtmlTag;
import org.eclipse.mylar.internal.tasks.core.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.internal.trac.MylarTracPlugin;
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

public class Trac09Client extends AbstractTracClient {

	private static final String TICKET_SUMMARY_PREFIX = " <h2 class=\"summary\">";

	private static final String TICKET_SUMMARY_POSTFIX = "</h2>";

	private InputStream in;

	private String authCookie;

	public Trac09Client(URL url, Version version, String username, String password) {
		super(url, version, username, password);
	}

	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				MylarStatusHandler.log(e, "Error closing connection");
			}
			in = null;
		}
	}

	public void connect(String serverURL) throws TracException {
		try {
			connectInternal(new URL(serverURL));
		} catch (TracException e) {
			throw e;
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private void connectInternal(URL serverURL) throws IOException, KeyManagementException, NoSuchAlgorithmException,
			TracLoginException {
		for (int attempt = 0; attempt < 2; attempt++) {
			HttpURLConnection serverConnection = MylarTracPlugin.getHttpConnection(serverURL);
			setupSession(serverConnection);

			serverConnection.connect();

			int code = serverConnection.getResponseCode();
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				// retry to authenticate due to an expired session
				authCookie = null;
				continue;
			}

			in = new BufferedInputStream(serverConnection.getInputStream());
			return;
		}

		throw new TracLoginException();
	}

	private void setupSession(HttpURLConnection serverConnection) throws IOException, TracLoginException,
			KeyManagementException, NoSuchAlgorithmException {
		if (hasAuthenticationCredentials()) {
			if (authCookie == null) {
				// go through the /login page redirection
				HttpURLConnection loginConnection = MylarTracPlugin
						.getHttpConnection(new URL(repositoryUrl + LOGIN_URL));
				MylarTracPlugin.setAuthCredentials(loginConnection, username, password);

				loginConnection.connect();

				int code = loginConnection.getResponseCode();
				if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
					throw new TracLoginException();
				}

				String cookie = loginConnection.getHeaderField("Set-Cookie");
				if (cookie == null) {
					throw new TracLoginException("Missing authorization cookie");
				}

				int index = cookie.indexOf(";");
				if (index >= 0) {
					cookie = cookie.substring(0, index);
				}
				authCookie = cookie;
			}

			serverConnection.setRequestProperty("Cookie", authCookie);
		}
	}

	/**
	 * Fetches the web site of a single ticket and returns the Trac ticket.
	 * 
	 * @param id
	 *            Trac id of ticket
	 * @throws LoginException
	 */
	public TracTicket getTicket(int id) throws TracException {
		connect(repositoryUrl + ITracClient.TICKET_URL + id);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ITracClient.CHARSET));
			String line;
			while ((line = reader.readLine()) != null) {
				// look for heading tags in html output
				if (line.startsWith(TICKET_SUMMARY_PREFIX) && line.endsWith(TICKET_SUMMARY_POSTFIX)) {
					String summary = line.substring(TICKET_SUMMARY_PREFIX.length(), line.length()
							- TICKET_SUMMARY_POSTFIX.length());

					TracTicket ticket = new TracTicket(id);
					ticket.putBuiltinValue(Key.SUMMARY, summary);
					return ticket;
				}
			}
			throw new InvalidTicketException();
		} catch (IOException e) {
			throw new TracException(e);
		} finally {
			close();
		}
	}

	public void search(TracSearch query, List<TracTicket> tickets) throws TracException {
		connect(repositoryUrl + ITracClient.QUERY_URL + query.toUrl());
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ITracClient.CHARSET));
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
						ticket.putTracValue(key, parseTicketValue(constantValues.get(key)));
					}

					tickets.add(ticket);
				}
			}
		} catch (IOException e) {
			throw new TracException(e);
		} finally {
			close();
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
		try {
			connect(repositoryUrl + "/");
		} finally {
			close();
		}
	}

	public void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", IProgressMonitor.UNKNOWN);

		connect(repositoryUrl + ITracClient.NEW_TICKET_URL);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ITracClient.CHARSET));
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
							components = new ArrayList<TracComponent>(values.size());
							for (String value : values) {
								components.add(new TracComponent(value));
							}
						} else if ("milestone".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							milestones = new ArrayList<TracMilestone>(values.size());
							for (String value : values) {
								milestones.add(new TracMilestone(value));
							}
						} else if ("priority".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							priorities = new ArrayList<TracPriority>(values.size());
							for (int i = 0; i < values.size(); i++) {
								priorities.add(new TracPriority(values.get(i), i + 1));
							}
						} else if ("severity".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							severities = new ArrayList<TracSeverity>(values.size());
							for (int i = 0; i < values.size(); i++) {
								severities.add(new TracSeverity(values.get(i), i + 1));
							}
						} else if ("type".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							ticketTypes = new ArrayList<TracTicketType>(values.size());
							for (int i = 0; i < values.size(); i++) {
								ticketTypes.add(new TracTicketType(values.get(i), i + 1));
							}
						} else if ("version".equals(name)) {
							List<String> values = getOptionValues(tokenizer);
							versions = new ArrayList<TracVersion>(values.size());
							for (String value : values) {
								versions.add(new TracVersion(value));
							}
						}
					}
				}
			}
			
			ticketResolutions = new ArrayList<TracTicketResolution>(5);
			ticketResolutions.add(new TracTicketResolution("fixed", 1));
			ticketResolutions.add(new TracTicketResolution("invalid", 2));
			ticketResolutions.add(new TracTicketResolution("wontfix", 3));
			ticketResolutions.add(new TracTicketResolution("duplicate", 4));
			ticketResolutions.add(new TracTicketResolution("worksforme", 5));
			
			ticketStatus = new ArrayList<TracTicketStatus>(4);
			ticketStatus.add(new TracTicketStatus("new", 1));
			ticketStatus.add(new TracTicketStatus("assigned", 2));
			ticketStatus.add(new TracTicketStatus("reopened", 3));
			ticketStatus.add(new TracTicketStatus("closed", 4));
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			close();
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
				sb.append(token.getValue());
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else {
				break;
			}
		}
		return sb.toString();
	}

}
