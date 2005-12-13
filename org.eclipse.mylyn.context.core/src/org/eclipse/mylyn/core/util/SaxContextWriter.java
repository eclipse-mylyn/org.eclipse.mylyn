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

package org.eclipse.mylar.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.internal.MylarContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Brock Janiczak
 */
public class SaxContextWriter {

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");

	private OutputStream outputStream;

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeContextToStream(MylarContext context) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new SaxWriter(), new MylarContextInputSource(context)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			throw new IOException(e.getMessage());
		}

	}

	private static class MylarContextInputSource extends InputSource {
		private MylarContext context;

		public MylarContextInputSource(MylarContext context) {
			this.context = context;
		}

		public MylarContext getContext() {
			return this.context;
		}

		public void setContext(MylarContext context) {
			this.context = context;
		}
	}

	private static class SaxWriter implements XMLReader {

		private ContentHandler handler;

		private ErrorHandler errorHandler;

		public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			return false;
		}

		public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {

		}

		public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			return null;
		}

		public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		}

		public void setEntityResolver(EntityResolver resolver) {
		}

		public EntityResolver getEntityResolver() {
			return null;
		}

		public void setDTDHandler(DTDHandler handler) {
		}

		public DTDHandler getDTDHandler() {
			return null;
		}

		public void setContentHandler(ContentHandler handler) {
			this.handler = handler;

		}

		public ContentHandler getContentHandler() {
			return handler;
		}

		public void setErrorHandler(ErrorHandler handler) {
			this.errorHandler = handler;

		}

		public ErrorHandler getErrorHandler() {
			return errorHandler;
		}

		public void parse(InputSource input) throws IOException, SAXException {
			if (!(input instanceof MylarContextInputSource)) {
				throw new SAXException("Can only parse writable input sources");
			}

			MylarContext context = ((MylarContextInputSource) input).getContext();

			handler.startDocument();
			AttributesImpl rootAttributes = new AttributesImpl();
			rootAttributes.addAttribute("", "Version", "Version", "", "1");
			rootAttributes.addAttribute("", "Id", "Id", "", context.getId());

			handler.startElement("", "InteractionHistory", "InteractionHistory", rootAttributes);
			for (InteractionEvent ie : context.getInteractionHistory()) {
				AttributesImpl ieAttributes = new AttributesImpl();

				ieAttributes.addAttribute("", "Delta", "Delta", "", XmlStringConverter
						.convertToXmlString(ie.getDelta()));
				ieAttributes.addAttribute("", "EndDate", "EndDate", "", DATE_FORMAT.format(ie.getEndDate()));
				ieAttributes.addAttribute("", "Interest", "Interest", "", Float.toString(ie.getInterestContribution()));
				ieAttributes.addAttribute("", "Kind", "Kind", "", ie.getKind().toString());
				ieAttributes.addAttribute("", "Navigation", "Navigation", "", XmlStringConverter.convertToXmlString(ie
						.getNavigation()));
				ieAttributes.addAttribute("", "OriginId", "OriginId", "", XmlStringConverter.convertToXmlString(ie
						.getOriginId()));
				ieAttributes.addAttribute("", "StartDate", "StartDate", "", DATE_FORMAT.format(ie.getDate()));
				ieAttributes.addAttribute("", "StructureHandle", "StructureHandle", "", XmlStringConverter
						.convertToXmlString(ie.getStructureHandle()));
				ieAttributes.addAttribute("", "StructureKind", "StructureKind", "", XmlStringConverter
						.convertToXmlString(ie.getContentType()));

				handler.startElement("", "InteractionEvent", "InteractionEvent", ieAttributes);
				handler.endElement("", "InteractionEvent", "InteractionEvent");
			}
			handler.endElement("", "InteractionHistory", "InteractionHistory");

			handler.endDocument();
		}

		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources");
		}

	}
}
