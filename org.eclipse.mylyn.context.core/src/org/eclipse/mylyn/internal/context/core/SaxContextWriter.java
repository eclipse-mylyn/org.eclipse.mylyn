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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.mylar.context.core.IContextWriter;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.core.util.XmlStringConverter;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.xml.sax.Attributes;
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
 * @author Mik Kersten (minor refactoring)
 */
public class SaxContextWriter implements IContextWriter {

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
			MylarStatusHandler.fail(e, "could not write context", false);
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
			rootAttributes.addAttribute("", MylarContextExternalizer.ATR_ID, MylarContextExternalizer.ATR_ID, "",
					context.getHandleIdentifier());
			rootAttributes.addAttribute("", MylarContextExternalizer.ATR_VERSION, MylarContextExternalizer.ATR_VERSION,
					"", "1");

			handler.startElement("", MylarContextExternalizer.ELMNT_INTERACTION_HISTORY,
					MylarContextExternalizer.ELMNT_INTERACTION_HISTORY, rootAttributes);
			// List could get modified as we're writing
			for (InteractionEvent ie : new ArrayList<InteractionEvent>(context.getInteractionHistory())) {
				Attributes ieAttributes = createEventAttributes(ie);
				handler.startElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT,
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT, ieAttributes);
				handler.endElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT,
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT);
			}
			handler.endElement("", MylarContextExternalizer.ELMNT_INTERACTION_HISTORY,
					MylarContextExternalizer.ELMNT_INTERACTION_HISTORY);

			handler.endDocument();
		}

		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources");
		}
	}
	
	public static Attributes createEventAttributes(InteractionEvent ie) {
		AttributesImpl ieAttributes = new AttributesImpl();

		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_DELTA, MylarContextExternalizer.ATR_DELTA,
				"", XmlStringConverter.convertToXmlString(ie.getDelta()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_END_DATE,
				MylarContextExternalizer.ATR_END_DATE, "", MylarContextExternalizer.DATE_FORMAT.format(ie
						.getEndDate()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_INTEREST,
				MylarContextExternalizer.ATR_INTEREST, "", Float.toString(ie.getInterestContribution()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_KIND, MylarContextExternalizer.ATR_KIND, "",
				ie.getKind().toString());
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_NAVIGATION,
				MylarContextExternalizer.ATR_NAVIGATION, "", XmlStringConverter.convertToXmlString(ie
						.getNavigation()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_ORIGIN_ID,
				MylarContextExternalizer.ATR_ORIGIN_ID, "", XmlStringConverter.convertToXmlString(ie
						.getOriginId()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_START_DATE,
				MylarContextExternalizer.ATR_START_DATE, "", MylarContextExternalizer.DATE_FORMAT.format(ie
						.getDate()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_STRUCTURE_HANDLE,
				MylarContextExternalizer.ATR_STRUCTURE_HANDLE, "", XmlStringConverter.convertToXmlString(ie
						.getStructureHandle()));
		ieAttributes.addAttribute("", MylarContextExternalizer.ATR_STRUCTURE_KIND,
				MylarContextExternalizer.ATR_STRUCTURE_KIND, "", XmlStringConverter.convertToXmlString(ie
						.getStructureKind()));
		return ieAttributes;
	}
}
