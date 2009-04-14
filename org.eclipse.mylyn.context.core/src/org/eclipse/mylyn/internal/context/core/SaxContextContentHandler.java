/*******************************************************************************
 * Copyright (c) 2004, 2008 Brock Janiczak and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Brock Janiczak - initial API and implementation
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContextScaling;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Brock Janiczak
 * @author Mik Kersten
 */
public class SaxContextContentHandler extends DefaultHandler {

	private static final int EXPECTING_ROOT = 0;

	private static final int EXPECTING_EVENT = 1;

	private int state = EXPECTING_ROOT;

	private InteractionContext context;

	private final IInteractionContextScaling contextScaling;

	private final String contextHandleIdentifier;

	static final String ATTRIBUTE_INTERACTION_EVENT = "InteractionEvent"; //$NON-NLS-1$

	static final String ATTRIBUTE_CONTENT = "Content"; //$NON-NLS-1$

	public SaxContextContentHandler(String contextHandleIdentifier, IInteractionContextScaling contextScaling) {
		this.contextHandleIdentifier = contextHandleIdentifier;
		this.contextScaling = contextScaling;
	}

	public InteractionContext getContext() {
		return context;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (state) {
		case EXPECTING_ROOT:
			context = new InteractionContext(contextHandleIdentifier, contextScaling);
			String limitContentTo = attributes.getValue(ATTRIBUTE_CONTENT);
			if (limitContentTo != null) {
				context.setContentLimitedTo(limitContentTo);
			}
			state = EXPECTING_EVENT;
			break;
		case EXPECTING_EVENT:
			try {
				InteractionEvent ie = createEventFromAttributes(attributes);
				context.parseEvent(ie);
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.WARNING, ContextCorePlugin.ID_PLUGIN,
						"Ignored unexpected activity event", e)); //$NON-NLS-1$
			}
			break;
		}
	}

	@SuppressWarnings( { "deprecation", "restriction" })
	private InteractionEvent createEventFromAttributes(Attributes attributes) throws ParseException {
		String delta = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attributes.getValue(InteractionContextExternalizer.ATR_DELTA));
		String endDate = attributes.getValue(InteractionContextExternalizer.ATR_END_DATE);
		String interest = attributes.getValue(InteractionContextExternalizer.ATR_INTEREST);
		String kind = attributes.getValue(InteractionContextExternalizer.ATR_KIND);
		String navigation = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attributes.getValue(InteractionContextExternalizer.ATR_NAVIGATION));
		String originId = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attributes.getValue(InteractionContextExternalizer.ATR_ORIGIN_ID));
		String startDate = attributes.getValue(InteractionContextExternalizer.ATR_START_DATE);
		String structureHandle = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attributes.getValue(InteractionContextExternalizer.ATR_STRUCTURE_HANDLE));
		String structureKind = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(attributes.getValue(InteractionContextExternalizer.ATR_STRUCTURE_KIND));

		// the number of events and event count on creation are needed to ensure that the context that is read in is the same as what was written out
		String numEventsString = attributes.getValue(InteractionContextExternalizer.ATR_NUM_EVENTS);
		int numEvents = 1;
		if (numEventsString != null && numEventsString.length() != 0) {
			try {
				numEvents = Integer.parseInt(numEventsString);
			} catch (NumberFormatException nfe) {
				//ignore.
			}
		}

		String eventCountOnCreationString = attributes.getValue(InteractionContextExternalizer.ATR_CREATION_COUNT);
		int eventCountOnCreation = -1;
		if (eventCountOnCreationString != null && eventCountOnCreationString.length() != 0) {
			try {
				eventCountOnCreation = Integer.parseInt(eventCountOnCreationString);
			} catch (NumberFormatException nfe) {
				//ignore.
			}
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(InteractionContextExternalizer.DATE_FORMAT_STRING,
				Locale.ENGLISH);
		Date dStartDate = dateFormat.parse(startDate);
		Date dEndDate = dateFormat.parse(endDate);
		float iInterest = Float.parseFloat(interest);

		InteractionEvent ie = null;
		if (numEventsString == null || eventCountOnCreationString == null) {
			// if we don't have the values for the collapsed event, it must be one that is uncollapsed
			ie = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle, originId, navigation,
					delta, iInterest, dStartDate, dEndDate);
		} else {
			ie = new AggregateInteractionEvent(Kind.fromString(kind), structureKind, structureHandle, originId,
					navigation, delta, iInterest, dStartDate, dEndDate, numEvents, eventCountOnCreation);
		}
		return ie;
	}
}
