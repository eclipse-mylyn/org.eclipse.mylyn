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

package org.eclipse.mylyn.context.tests.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mylyn.context.core.IInteractionContextReader;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.core.util.XmlStringConverter;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Mik Kersten
 * 
 * TODO: merge into a single externalizer
 */
public class DomContextReader implements IInteractionContextReader {

	// private int readVersion;

	public InteractionContext readContext(String handle, File file) {
		if (!file.exists())
			return null;
		try {
			Document doc = openAsDOM(file);
			Element root = doc.getDocumentElement();
			// readVersion = Integer.parseInt(root.getAttribute("Version"));
			// String id = root.getAttribute("Id");
			InteractionContext t = new InteractionContext(handle, InteractionContextManager.getScalingFactors());
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
				InteractionEvent ie = readInteractionEvent(child);
				if (ie != null) {
					t.parseEvent(ie);
				}
			}
			return t;
		} catch (Exception e) {
			StatusHandler.fail(e, "could not read context, recreating", false);
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			return null;
		}
	}

	public Document openAsDOM(File inputFile) throws IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		ZipInputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(new FileInputStream(inputFile));
			inputStream.getNextEntry();
			builder = factory.newDocumentBuilder();
			document = builder.parse(inputStream);
		} catch (SAXException se) {
			StatusHandler.log(se, "could not build");
		} catch (ParserConfigurationException e) {
			StatusHandler.log(e, "could not parse");
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					StatusHandler.fail(e, "Failed to close context input stream.", false);
				}
			}
		}
		return document;
	}

	public InteractionEvent readInteractionEvent(Node n) {
		try {
			Element e = (Element) n;
			String kind = e.getAttribute("Kind");
			String startDate = e.getAttribute("StartDate");
			String endDate = e.getAttribute("EndDate");
			String originId = XmlStringConverter.convertXmlToString(e.getAttribute("OriginId"));
			String structureKind = XmlStringConverter.convertXmlToString(e.getAttribute("StructureKind"));
			String structureHandle = XmlStringConverter.convertXmlToString(e.getAttribute("StructureHandle"));
			String navigation = XmlStringConverter.convertXmlToString(e.getAttribute("Navigation"));
			String delta = XmlStringConverter.convertXmlToString(e.getAttribute("Delta"));
			String interest = e.getAttribute("Interest");

			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			InteractionEvent ie = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle, originId,
					navigation, delta, Float.parseFloat(interest), format.parse(startDate), format.parse(endDate));
			return ie;
		} catch (ParseException e) {
			StatusHandler.log(e, "could not read interaction event");
		}
		return null;
	}
}
