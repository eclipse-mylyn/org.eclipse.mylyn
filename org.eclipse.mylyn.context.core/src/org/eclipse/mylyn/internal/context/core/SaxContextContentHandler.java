


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
package org.eclipse.mylar.internal.context.core;

import java.text.ParseException;
import java.util.Date;

import org.eclipse.mylar.internal.core.util.XmlStringConverter;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.core.InteractionEvent.Kind;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Brock Janiczak
 * @author Mik Kersten (minor refactoring)
 */
public class SaxContextContentHandler extends DefaultHandler {

	private static final int EXPECTING_ROOT = 0;

	private static final int EXPECTING_EVENT = 1;

	private int state = EXPECTING_ROOT;

	private MylarContext context;

	private String contextHandleIdentifier;

	static final String ATTRIBUTE_INTERACTION_EVENT = "InteractionEvent";

	public SaxContextContentHandler(String contextHandleIdentifier) {
		this.contextHandleIdentifier = contextHandleIdentifier;
	}

	public MylarContext getContext() {
		return context;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (state) {
		case EXPECTING_ROOT:
			// String id = attributes.getValue(MylarContextExternalizer.ATR_ID);
			// String version = attributes.getValue(1);
			context = new MylarContext(contextHandleIdentifier, ContextManager.getScalingFactors());
			state = EXPECTING_EVENT;
			break;
		case EXPECTING_EVENT:
			try {
				InteractionEvent ie = createEventFromAttributes(attributes);
				context.parseEvent(ie);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public static InteractionEvent createEventFromAttributes(Attributes attributes) throws ParseException {
		String delta = XmlStringConverter.convertXmlToString(attributes
				.getValue(MylarContextExternalizer.ATR_DELTA));
		String endDate = attributes.getValue(MylarContextExternalizer.ATR_END_DATE);
		String interest = attributes.getValue(MylarContextExternalizer.ATR_INTEREST);
		String kind = attributes.getValue(MylarContextExternalizer.ATR_KIND);
		String navigation = XmlStringConverter.convertXmlToString(attributes
				.getValue(MylarContextExternalizer.ATR_NAVIGATION));
		String originId = XmlStringConverter.convertXmlToString(attributes
				.getValue(MylarContextExternalizer.ATR_ORIGIN_ID));
		String startDate = attributes.getValue(MylarContextExternalizer.ATR_START_DATE);
		String structureHandle = XmlStringConverter.convertXmlToString(attributes
				.getValue(MylarContextExternalizer.ATR_STRUCTURE_HANDLE));
		String structureKind = XmlStringConverter.convertXmlToString(attributes
				.getValue(MylarContextExternalizer.ATR_STRUCTURE_KIND));

		Date dStartDate = MylarContextExternalizer.DATE_FORMAT.parse(startDate);
		Date dEndDate = MylarContextExternalizer.DATE_FORMAT.parse(endDate);
		float iInterest = Float.parseFloat(interest);

		InteractionEvent ie = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle,
				originId, navigation, delta, iInterest, dStartDate, dEndDate);
		return ie;
	}
}
