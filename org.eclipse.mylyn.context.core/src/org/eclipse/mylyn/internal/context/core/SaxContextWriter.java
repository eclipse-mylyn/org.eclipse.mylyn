/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContextWriter;
import org.eclipse.mylyn.internal.monitor.core.util.XmlStringConverter;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.StatusHandler;
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
public class SaxContextWriter implements IInteractionContextWriter {

	private OutputStream outputStream;

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeContextToStream(InteractionContext context) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new SaxWriter(), new InteractionContextInputSource(context)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, "Could not write context", e));
			throw new IOException(e.getMessage());
		}
	}

	private static class InteractionContextInputSource extends InputSource {
		private InteractionContext context;

		public InteractionContextInputSource(InteractionContext context) {
			this.context = context;
		}

		public InteractionContext getContext() {
			return this.context;
		}

		public void setContext(InteractionContext context) {
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
			if (!(input instanceof InteractionContextInputSource)) {
				throw new SAXException("Can only parse writable input sources");
			}

			InteractionContext context = ((InteractionContextInputSource) input).getContext();

			handler.startDocument();
			AttributesImpl rootAttributes = new AttributesImpl();
			rootAttributes.addAttribute("", InteractionContextExternalizer.ATR_ID,
					InteractionContextExternalizer.ATR_ID, "", context.getHandleIdentifier());
			if (context.getContentLimitedTo() != null) {
				rootAttributes.addAttribute("", SaxContextContentHandler.ATTRIBUTE_CONTENT,
						SaxContextContentHandler.ATTRIBUTE_CONTENT, "", context.getContentLimitedTo());
			}
			rootAttributes.addAttribute("", InteractionContextExternalizer.ATR_VERSION,
					InteractionContextExternalizer.ATR_VERSION, "", "1");

			handler.startElement("", InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY,
					InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY, rootAttributes);
			// List could get modified as we're writing
			for (InteractionEvent ie : context.getInteractionHistory()) {
				Attributes ieAttributes = createEventAttributes(ie);
				handler.startElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT,
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT, ieAttributes);
				handler.endElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT,
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT);
			}
			handler.endElement("", InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY,
					InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY);

			handler.endDocument();
		}

		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources");
		}
	}

	// API-3.0: make this method non-static and private?
	public static Attributes createEventAttributes(InteractionEvent ie) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(InteractionContextExternalizer.DATE_FORMAT_STRING,
				Locale.ENGLISH);

		AttributesImpl ieAttributes = new AttributesImpl();

		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_DELTA,
				InteractionContextExternalizer.ATR_DELTA, "", XmlStringConverter.convertToXmlString(ie.getDelta()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_END_DATE,
				InteractionContextExternalizer.ATR_END_DATE, "", dateFormat.format(ie.getEndDate()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_INTEREST,
				InteractionContextExternalizer.ATR_INTEREST, "", Float.toString(ie.getInterestContribution()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_KIND, InteractionContextExternalizer.ATR_KIND,
				"", ie.getKind().toString());
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_NAVIGATION,
				InteractionContextExternalizer.ATR_NAVIGATION, "",
				XmlStringConverter.convertToXmlString(ie.getNavigation()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_ORIGIN_ID,
				InteractionContextExternalizer.ATR_ORIGIN_ID, "",
				XmlStringConverter.convertToXmlString(ie.getOriginId()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_START_DATE,
				InteractionContextExternalizer.ATR_START_DATE, "", dateFormat.format(ie.getDate()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_STRUCTURE_HANDLE,
				InteractionContextExternalizer.ATR_STRUCTURE_HANDLE, "",
				XmlStringConverter.convertToXmlString(ie.getStructureHandle()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_STRUCTURE_KIND,
				InteractionContextExternalizer.ATR_STRUCTURE_KIND, "",
				XmlStringConverter.convertToXmlString(ie.getStructureKind()));
		return ieAttributes;
	}
}
