/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for RDF bugzilla query results.
 * 
 * @author Rob Elves
 */
public class SaxBugzillaQueryContentHandler extends DefaultHandler {

	private StringBuffer characters;

	private IBugzillaSearchResultCollector collector;

	private String repositoryUrl;

	private BugzillaSearchHit hit;

	private int maxHits = 100;

	private int numCollected = 0;

	public SaxBugzillaQueryContentHandler(String repositoryUrl, IBugzillaSearchResultCollector col, int maxHits) {
		this.repositoryUrl = repositoryUrl;
		collector = col;
		this.maxHits = maxHits;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase());
			switch (tag) {
			case LI:
				hit = new BugzillaSearchHit();
				hit.setRepository(repositoryUrl);
				break;
			}
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase());
			switch (tag) {
			case ID:
				hit.setId(Integer.parseInt(characters.toString()));
				break;
			case BUG_SEVERITY:
				hit.setSeverity(characters.toString());
				break;
			case PRIORITY:
				hit.setPriority(characters.toString());
				break;
			case REP_PLATFORM:
				hit.setPlatform(characters.toString());
				break;
			case ASSIGNED_TO:
				hit.setOwner(characters.toString());
				break;
			case BUG_STATUS:
				hit.setState(characters.toString());
				break;
			case RESOLUTION:
				hit.setResolution(characters.toString());
				break;
			case SHORT_SHORT_DESC:
				hit.setDescription(characters.toString());
				break;
			case LI:
				try {
					if (numCollected < maxHits || maxHits == IBugzillaConstants.RETURN_ALL_HITS) {
						collector.accept(hit);
						numCollected++;
					}
				} catch (CoreException e) {
					MylarStatusHandler.fail(e, "Problem recording Bugzilla search hit information", false);
				}
			}
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}

	}
}
