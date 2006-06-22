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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.trac.MylarTracPlugin;
import org.eclipse.mylar.internal.trac.model.TracSearch;
import org.eclipse.mylar.internal.trac.model.TracSearchFilter;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;

public class Trac09Repository extends AbstractTracRepository {

	private static final String TICKET_SUMMARY_PREFIX = " <h2 class=\"summary\">";

	private static final String TICKET_SUMMARY_POSTFIX = "</h2>";

	private InputStream in;

	private String authCookie;

	public Trac09Repository(URL url, Version version, String username, String password) {
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

	public void connectInternal(URL serverURL) throws IOException, KeyManagementException, NoSuchAlgorithmException,
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
		connect(repositoryUrl + ITracRepository.TICKET_URL + id);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ITracRepository.CHARSET));
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
		connect(repositoryUrl + ITracRepository.QUERY_URL + query.toUrl());
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, ITracRepository.CHARSET));
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

}
