package org.eclipse.mylar.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.InteractionEvent.Kind;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Brock Janiczak
 */
public class SaxContextContentHandler extends DefaultHandler {

	private static final int EXPECTING_ROOT = 0;

	private static final int EXPECTING_EVENT = 1;

	private int state = EXPECTING_ROOT;

	private MylarContext t;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z", Locale.ENGLISH);

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
	}

	public MylarContext getContext() {
		return t;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (state) {
		case EXPECTING_ROOT:
			String id = attributes.getValue(0);
			// String version = attributes.getValue(1);
			t = new MylarContext(id, MylarContextManager.getScalingFactors());
			state = EXPECTING_EVENT;
			break;
		case EXPECTING_EVENT:
			try {
				String delta = XmlStringConverter.convertXmlToString(attributes.getValue("Delta"));
				String endDate = attributes.getValue("EndDate");
				String interest = attributes.getValue("Interest");
				String kind = attributes.getValue("Kind");
				String navigation = XmlStringConverter.convertXmlToString(attributes.getValue("Navigation"));
				String originId = XmlStringConverter.convertXmlToString(attributes.getValue("OriginId"));
				String startDate = attributes.getValue("StartDate");
				String structureHandle = XmlStringConverter.convertXmlToString(attributes.getValue("StructureHandle"));
				String structureKind = XmlStringConverter.convertXmlToString(attributes.getValue("StructureKind"));

				Date dStartDate = DATE_FORMAT.parse(startDate);
				Date dEndDate = DATE_FORMAT.parse(endDate);
				float iInterest = Float.parseFloat(interest);

				InteractionEvent ie = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle,
						originId, navigation, delta, iInterest, dStartDate, dEndDate);
				t.parseEvent(ie);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}

	}
}
