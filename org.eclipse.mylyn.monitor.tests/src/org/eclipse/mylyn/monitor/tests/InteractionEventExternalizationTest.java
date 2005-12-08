/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.monitor.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.monitor.InteractionEventLogger;

/**
 * @author Mik Kersten
 */
public class InteractionEventExternalizationTest extends AbstractContextTest {

	private static final String PATH = "test-log.xml";

	public void testManualExternalization() {
		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
		File f = new File(PATH);
		if (f.exists()) {
			f.delete();
		}
		InteractionEventLogger logger = new InteractionEventLogger(f);
		logger.startObserving();
		for (int i = 0; i < 100; i++) {
			InteractionEvent event = mockSelection();
			events.add(event);
			logger.interactionObserved(event);
		}
		logger.stopObserving();
		
		File infile = new File(PATH);
		List<InteractionEvent> readEvents = logger.getHistoryFromFile(infile);
		for (int i = 0; i < events.size(); i++) {
			processEvent(events.get(i), readEvents.get(i));
		}
//		f.delete();
	}

	private void processEvent(InteractionEvent event1, InteractionEvent event2) {
		assertNotNull(event2);
		assertTrue(event1.equals(event2));
	}

}

//public void testXStreamExternalization() {
//	try {
//		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
//		File f = new File("./externMylarMem.xml");
//		FileOutputStream fos = new FileOutputStream(f);
//
//		XStream xstream = new XStream(new DomDriver());
//		xstream.setMode(XStream.XPATH_REFERENCES);
//		xstream.alias("interactionEvent", InteractionEvent.class);
//		xstream.setClassLoader(InteractionEvent.class.getClassLoader());
//		for (int i = 0; i < 100; i++) {
//			InteractionEvent event = mockSelection();
//			events.add(event);
//
//			// externalize by appending to a file
//			String xml = xstream.toXML(event) + "\r\n";
//			fos.write(xml.getBytes());
//		}
//		fos.close();
//
//		File infile = new File("./externMylarMem.xml");
//		String tag = "</interactionEvent>";
//		String endl = "\r\n";
//		String buf = "";
//		int index;
//		int i = 0;
//		FileInputStream reader = new FileInputStream(infile);
//		byte[] buffer = new byte[1000];
//		while (reader.read(buffer) != -1) {
//			buf = buf + new String(buffer);
//			while ((index = buf.indexOf(tag)) != -1) {
//				index += tag.length();
//				String xml = buf.substring(0, index);
//				buf = buf.substring(index + endl.length(), buf.length());
//				InteractionEvent event = (InteractionEvent) xstream
//						.fromXML(xml);
//				processEvent(events, event, i);
//				event = readEvent(xml);
//				processEvent(events, event, i);
//				i++;
//			}
//			buffer = new byte[1000];
//		}
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
//	} catch (IOException e) {
//		e.printStackTrace();
//	}
//}