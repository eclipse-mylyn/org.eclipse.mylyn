/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests.support;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.IInteractionContextReader;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 * 
 *         TODO: merge into a single externalizer
 */
public class DomContextReader implements IInteractionContextReader {

	public InteractionContext readContext(String handle, File file) {
		if (!file.exists()) {
			return null;
		}
		try {
			Document doc = openAsDOM(file);
			Element root = doc.getDocumentElement();
			// readVersion = Integer.parseInt(root.getAttribute("Version"));
			// String id = root.getAttribute("Id");
			InteractionContext t = new InteractionContext(handle, ContextCore.getCommonContextScaling());
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
			throw new RuntimeException(e);
		}
	}

	public Document openAsDOM(File inputFile) throws IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		ZipInputStream zipInputStream = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(inputFile);
			zipInputStream = new ZipInputStream(fileInputStream);
			zipInputStream.getNextEntry();
			builder = factory.newDocumentBuilder();
			document = builder.parse(zipInputStream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			closeStream(zipInputStream);
			closeStream(fileInputStream);
		}
		return document;
	}

	@SuppressWarnings( { "deprecation" })
	public InteractionEvent readInteractionEvent(Node n) {
		try {
			Element e = (Element) n;
			String kind = e.getAttribute("Kind");
			String startDate = e.getAttribute("StartDate");
			String endDate = e.getAttribute("EndDate");
			String originId = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(e.getAttribute("OriginId"));
			String structureKind = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(e.getAttribute("StructureKind"));
			String structureHandle = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(e.getAttribute("StructureHandle"));
			String navigation = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(e.getAttribute("Navigation"));
			String delta = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(e.getAttribute("Delta"));
			String interest = e.getAttribute("Interest");

			String formatString = "yyyy-MM-dd HH:mm:ss.S z";
			SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
			InteractionEvent ie = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle, originId,
					navigation, delta, Float.parseFloat(interest), format.parse(startDate), format.parse(endDate));
			return ie;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static final void closeStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
