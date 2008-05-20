/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.tests.support;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.IInteractionContextWriter;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO: remove the explicit string references
 * 
 * @author Mik Kersten
 */
public class DomContextWriter implements IInteractionContextWriter {

	private DocumentBuilderFactory dbf = null;

	private Document doc = null;

	private Element root = null;

	private OutputStream outputStream = null;

	private Result result = null;

	public DomContextWriter() throws Exception {
		dbf = DocumentBuilderFactory.newInstance();
		doc = dbf.newDocumentBuilder().newDocument();
	}

	public void writeContextToStream(IInteractionContext context) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}

		clearDocument();
		root = doc.createElement("InteractionHistory");
		root.setAttribute("Version", "1");
		root.setAttribute("Id", context.getHandleIdentifier());

		for (InteractionEvent ie : context.getInteractionHistory()) {
			writeInteractionEvent(ie);
		}
		doc.appendChild(root);
		writeDOMtoStream(doc);
		return;
	}

	private void writeDOMtoStream(Document document) {
		Source source = new DOMSource(document);
		result = new StreamResult(outputStream);
		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
	}

	@SuppressWarnings( { "deprecation" })
	private void writeInteractionEvent(InteractionEvent e) {
		Element node = doc.createElement("InteractionEvent");
		String f = "yyyy-MM-dd HH:mm:ss.S z";
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		node.setAttribute("Kind", e.getKind().toString());
		node.setAttribute("StartDate", format.format(e.getDate()));
		node.setAttribute("EndDate", format.format(e.getEndDate()));
		node.setAttribute("OriginId",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getOriginId()));
		node.setAttribute("StructureKind",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getStructureKind()));
		node.setAttribute("StructureHandle",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getStructureHandle()));
		node.setAttribute("Navigation",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getNavigation()));
		node.setAttribute("Delta",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getDelta()));
		node.setAttribute("Interest", "" + e.getInterestContribution());
		root.appendChild(node);
	}

	@SuppressWarnings( { "deprecation" })
	public void writeEventToStream(InteractionEvent e) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}

		clearDocument();
		root = doc.createElement("InteractionEvent");
		String f = "yyyy-MM-dd HH:mm:ss.S z";
		SimpleDateFormat format = new SimpleDateFormat(f, Locale.ENGLISH);
		root.setAttribute("Kind", e.getKind().toString());
		root.setAttribute("StartDate", format.format(e.getDate()));
		root.setAttribute("EndDate", format.format(e.getEndDate()));
		root.setAttribute("OriginId",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getOriginId()));
		root.setAttribute("StructureKind",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getStructureKind()));
		root.setAttribute("StructureHandle",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getStructureHandle()));
		root.setAttribute("Navigation",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getNavigation()));
		root.setAttribute("Delta",
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(e.getDelta()));
		root.setAttribute("Interest", "" + e.getInterestContribution());
		writeDOMtoStream(doc);
	}

	private void clearDocument() {
		try {
			this.doc = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
