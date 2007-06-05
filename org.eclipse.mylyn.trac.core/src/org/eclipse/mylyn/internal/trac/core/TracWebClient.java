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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.core.net.HtmlStreamTokenizer;
import org.eclipse.mylar.core.net.HtmlTag;
import org.eclipse.mylar.core.net.WebClientUtil;
import org.eclipse.mylar.core.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylar.internal.trac.core.model.TracComponent;
import org.eclipse.mylar.internal.trac.core.model.TracMilestone;
import org.eclipse.mylar.internal.trac.core.model.TracPriority;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylar.internal.trac.core.model.TracSeverity;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.core.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.core.model.TracTicketType;
import org.eclipse.mylar.internal.trac.core.model.TracVersion;
import org.eclipse.mylar.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.core.util.TracUtils;
import org.eclipse.mylar.internal.trac.core.util.TracHttpClientTransportFactory.TracHttpException;

/**
 * Represents a Trac repository that is accessed through the Trac's query script
 * and web interface.
 * 
 * @author Steffen Pingel
 */
public class TracWebClient extends AbstractTracClient {

	private HttpClient httpClient = new HttpClient();

	private boolean authenticated;

	public TracWebClient(URL url, Version version, String username, String password, Proxy proxy) {
		super(url, version, username, password, proxy);
	}

	private synchronized GetMethod connect(String serverURL) throws TracException {
		try {
			return connectInternal(serverURL);
		} catch (TracException e) {
			throw e;
		} catch (Exception e) {
			throw new TracException(e);
		}
	}

	private GetMethod connectInternal(String serverURL) throws TracLoginException, IOException, TracHttpException {
		WebClientUtil.setupHttpClient(httpClient, proxy, serverURL, null, null);

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
				// login or re-authenticate due to an expired session
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

		// try standard basic/digest authentication first
		Credentials credentials = new UsernamePasswordCredentials(username, password);
		httpClient.getState().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM), credentials);

		GetMethod method = new GetMethod(WebClientUtil.getRequestPath(repositoryUrl + LOGIN_URL));
		method.setFollowRedirects(false);
		int code;
		try {
			httpClient.getParams().setAuthenticationPreemptive(true);
			code = httpClient.executeMethod(method);
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN) {
				throw new TracLoginException();
			}
		} finally {
			method.releaseConnection();
			httpClient.getParams().setAuthenticationPreemptive(false);
		}

		// the expected return code is a redirect, anything else is suspicious
		if (code == HttpURLConnection.HTTP_OK) {
			// try form-based authentication via AccountManagerPlugin as a
			// fall-back
			authenticateAccountManager(httpClient);
		}

		validateAuthenticationState(httpClient);

		// success since no exception was thrown
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
								ticket.setCreated(TracUtils.parseDate(Integer.parseInt(t.nextToken())));
							} else if (fields[i] == Key.CHANGE_TIME) {
								ticket.setLastChanged(TracUtils.parseDate(Integer.parseInt(t.nextToken())));
							} else {
								ticket.putBuiltinValue(fields[i], parseTicketValue(t.nextToken()));
							}
						} catch (NumberFormatException e) {
							MylarStatusHandler.log(e, "Error parsing response: " + line);
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
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),
					ITracClient.CHARSET));

			boolean inFooter = false;
			boolean valid = false;
			String version = null;

			HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getTagType() == HtmlTag.Type.DIV) {
						String id = tag.getAttribute("id");
						inFooter = !tag.isEndTag() && "footer".equals(id);
					} else if (tag.getTagType() == HtmlTag.Type.STRONG && inFooter) {
						version = getText(tokenizer);
					} else if (tag.getTagType() == HtmlTag.Type.A) {
						String id = tag.getAttribute("id");
						if ("tracpowered".equals(id)) {
							valid = true;
						}
					}
				}
			}

			if (version != null && !(version.startsWith("Trac 0.9") || version.startsWith("Trac 0.10"))) {
				throw new TracException("The Trac version " + version
						+ " is unsupported. Please use version 0.9.x or 0.10.x.");
			}

			if (!valid) {
				throw new TracException("Not a valid Trac repository");
			}
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			method.releaseConnection();
		}
	}

	@Override
	public void updateAttributes(IProgressMonitor monitor) throws TracException {
		monitor.beginTask("Updating attributes", IProgressMonitor.UNKNOWN);

		GetMethod method = connect(repositoryUrl + ITracClient.CUSTOM_QUERY_URL);
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
					if (tag.getTagType() == HtmlTag.Type.SCRIPT) {
						String text = getText(tokenizer).trim();
						if (text.startsWith("var properties=")) {
							parseAttributes(text);
						}
					}
				}
			}

			addResolutionAndStatus();
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			method.releaseConnection();
		}
	}

	enum AttributeState {
		INIT, IN_LIST, IN_ATTRIBUTE_KEY, IN_ATTRIBUTE_VALUE, IN_ATTRIBUTE_VALUE_LIST
	};

	/**
	 * Parses the JavaScript code from the query page to extract repository
	 * configuration.
	 */
	private void parseAttributes(String text) throws IOException {
		StreamTokenizer t = new StreamTokenizer(new StringReader(text));
		t.quoteChar('"');

		AttributeFactory attributeFactory = null;
		String attributeType = null;

		AttributeState state = AttributeState.INIT;
		int tokenType;
		while ((tokenType = t.nextToken()) != StreamTokenizer.TT_EOF) {
			switch (tokenType) {
			case StreamTokenizer.TT_WORD:
				if (state == AttributeState.IN_LIST) {
					if ("component".equals(t.sval)) {
						data.components = new ArrayList<TracComponent>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.components.add(new TracComponent(value));
							}
						};
					} else if ("milestone".equals(t.sval)) {
						data.milestones = new ArrayList<TracMilestone>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.milestones.add(new TracMilestone(value));
							}
						};
					} else if ("priority".equals(t.sval)) {
						data.priorities = new ArrayList<TracPriority>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.priorities.add(new TracPriority(value, data.priorities.size() + 1));
							}
						};
					} else if ("resolution".equals(t.sval)) {
						data.ticketResolutions = new ArrayList<TracTicketResolution>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.ticketResolutions.add(new TracTicketResolution(value, data.ticketResolutions
										.size() + 1));
							}
						};
					} else if ("severity".equals(t.sval)) {
						data.severities = new ArrayList<TracSeverity>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.severities.add(new TracSeverity(value, data.severities.size() + 1));
							}
						};
					} else if ("status".equals(t.sval)) {
						data.ticketStatus = new ArrayList<TracTicketStatus>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.ticketStatus.add(new TracTicketStatus(value, data.ticketStatus.size() + 1));
							}
						};
					} else if ("type".equals(t.sval)) {
						data.ticketTypes = new ArrayList<TracTicketType>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.ticketTypes.add(new TracTicketType(value, data.ticketTypes.size() + 1));
							}
						};
					} else if ("version".equals(t.sval)) {
						data.versions = new ArrayList<TracVersion>();
						attributeFactory = new AttributeFactory() {
							public void addAttribute(String value) {
								data.versions.add(new TracVersion(value));
							}
						};
					} else {
						attributeFactory = null;
					}
				} else if (state == AttributeState.IN_ATTRIBUTE_KEY) {
					attributeType = t.sval;
				}
				break;
			case '"':
				if (state == AttributeState.IN_ATTRIBUTE_VALUE_LIST && "options".equals(attributeType)) {
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
					throw new IOException("Error parsing attributes: unexpected token '{'");
				}
				break;
			case '}':
				if (state == AttributeState.IN_ATTRIBUTE_KEY || state == AttributeState.IN_ATTRIBUTE_VALUE) {
					state = AttributeState.IN_LIST;
				} else if (state == AttributeState.IN_LIST) {
					state = AttributeState.INIT;
				} else {
					throw new IOException("Error parsing attributes: unexpected token '}'");
				}
				break;
			}
		}
	}

	public void updateAttributesNewTicketPage(IProgressMonitor monitor) throws TracException {
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

			addResolutionAndStatus();
		} catch (IOException e) {
			throw new TracException(e);
		} catch (ParseException e) {
			throw new TracException(e);
		} finally {
			method.releaseConnection();
		}
	}

	private void addResolutionAndStatus() {
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

	public InputStream getAttachmentData(int id, String filename) throws TracException {
		GetMethod method = connect(repositoryUrl + ITracClient.ATTACHMENT_URL + id + "/" + filename + "?format=raw");
		try {
			// the receiver is responsible for closing the stream which will
			// release the connection
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			method.releaseConnection();
			throw new TracException(e);
		}
	}

	public void putAttachmentData(int id, String name, String description, InputStream in) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public void deleteAttachment(int ticketId, String filename) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public int createTicket(TracTicket ticket) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public void updateTicket(TracTicket ticket, String comment) throws TracException {
		throw new TracException("Unsupported operation");
	}

	public Set<Integer> getChangedTickets(Date since) throws TracException {
		return null;
	}

	private interface AttributeFactory {

		void addAttribute(String value);

	}

}
