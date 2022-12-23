/*******************************************************************************
 * Copyright (c) 2013 Steffen Pingel and others.
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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;

public class WebSearchResultParser {

	private final List<TracTicket> tickets = new ArrayList<TracTicket>();

	public void parse(BufferedReader reader) throws IOException, TracException {
		Key[] fields = parseHeader(reader);
		parseTickets(reader, fields);
	}

	public void parseTickets(BufferedReader reader, Key[] fields) throws IOException, InvalidTicketException {
		// create a ticket for each following line of output
		String line;
		while ((line = reader.readLine()) != null) {
			TracTicket ticket = new TracTicket();
			tickets.add(ticket);

			// include delimiters to detect empty values
			StringTokenizer t = new StringTokenizer(line, "\t", true); //$NON-NLS-1$
			for (int i = 0; i < fields.length && t.hasMoreTokens(); i++) {
				String nextToken = t.nextToken();
				if (nextToken.equals("\t")) { //$NON-NLS-1$
					// process empty value
					nextToken = ""; //$NON-NLS-1$
				} else if (t.hasMoreTokens()) {
					// skip delimiter
					t.nextToken();
				}
				Key key = fields[i];
				if (key != null) {
					assignTicketValue(ticket, key, nextToken);
				}
			}
		}
	}

	public void assignTicketValue(TracTicket ticket, Key key, String value) throws InvalidTicketException {
		try {
			if (key == Key.ID) {
				ticket.setId(Integer.parseInt(value));
			} else if (key == Key.TIME) {
				ticket.setCreated(TracUtil.parseDate(Integer.parseInt(value)));
			} else if (key == Key.CHANGE_TIME) {
				ticket.setLastChanged(TracUtil.parseDate(Integer.parseInt(value)));
			} else {
				ticket.putBuiltinValue(key, parseTicketValue(value));
			}
		} catch (NumberFormatException e) {
			// ignore to avoid spamming log
//						StatusHandler.log(new Status(IStatus.WARNING, TracCorePlugin.ID_PLUGIN, NLS.bind(
//								"Error parsing response: ''{0}''", line), e)); //$NON-NLS-1$ 
		}
	}

	private Key[] parseHeader(BufferedReader reader) throws IOException, InvalidTicketException {
		// first line contains names of returned ticket fields
		String line = reader.readLine();
		if (line == null) {
			throw new InvalidTicketException();
		}
		// the utf-8 output in Trac 1.0 starts with a byte-order mark which
		// is passed to the tokenizer since it would otherwise end up in the first token
		StringTokenizer t = new StringTokenizer(line, "\ufeff\t"); //$NON-NLS-1$
		Key[] fields = new Key[t.countTokens()];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = Key.fromKey(t.nextToken());
		}
		return fields;
	}

	public List<TracTicket> getTickets() {
		return tickets;
	}

	/**
	 * Trac has special encoding rules for the returned output: None is represented by "--".
	 */
	public static String parseTicketValue(String value) {
		if ("--".equals(value)) { //$NON-NLS-1$
			return null;
		}
		return value;
	}

}
