/*******************************************************************************
 * Copyright (c) 2004, 2011 Brock Janiczak and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Brock Janiczak - initial API and implementation
 *     Tasktop Technologies - improvements
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
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
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
 * @author Mik Kersten (refactoring)
 */
public class SaxContextWriter implements IInteractionContextWriter {

	private OutputStream outputStream;

	@Override
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void writeContextToStream(IInteractionContext context) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set"); //$NON-NLS-1$
			throw ioe;
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new SaxWriter(), new InteractionContextInputSource(context)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextCorePlugin.ID_PLUGIN, "Could not write context", e)); //$NON-NLS-1$
			throw new IOException(e.getMessage());
		}
	}

	private static class InteractionContextInputSource extends InputSource {

		private final IInteractionContext context;

		public InteractionContextInputSource(IInteractionContext context) {
			this.context = context;
		}

		public IInteractionContext getContext() {
			return context;
		}

	}

	private class SaxWriter implements XMLReader {

		private ContentHandler handler;

		private ErrorHandler errorHandler;

		@Override
		public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			return false;
		}

		@Override
		public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {

		}

		@Override
		public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
			return null;
		}

		@Override
		public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		}

		@Override
		public void setEntityResolver(EntityResolver resolver) {
		}

		@Override
		public EntityResolver getEntityResolver() {
			return null;
		}

		@Override
		public void setDTDHandler(DTDHandler handler) {
		}

		@Override
		public DTDHandler getDTDHandler() {
			return null;
		}

		@Override
		public void setContentHandler(ContentHandler handler) {
			this.handler = handler;

		}

		@Override
		public ContentHandler getContentHandler() {
			return handler;
		}

		@Override
		public void setErrorHandler(ErrorHandler handler) {
			errorHandler = handler;

		}

		@Override
		public ErrorHandler getErrorHandler() {
			return errorHandler;
		}

		@Override
		public void parse(InputSource input) throws IOException, SAXException {
			if (!(input instanceof InteractionContextInputSource)) {
				throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
			}

			IInteractionContext context = ((InteractionContextInputSource) input).getContext();

			handler.startDocument();
			AttributesImpl rootAttributes = new AttributesImpl();
			rootAttributes.addAttribute("", InteractionContextExternalizer.ATR_ID, //$NON-NLS-1$
					InteractionContextExternalizer.ATR_ID, "", context.getHandleIdentifier()); //$NON-NLS-1$
			if (context.getContentLimitedTo() != null) {
				rootAttributes.addAttribute("", SaxContextContentHandler.ATTRIBUTE_CONTENT, //$NON-NLS-1$
						SaxContextContentHandler.ATTRIBUTE_CONTENT, "", context.getContentLimitedTo()); //$NON-NLS-1$
			}
			rootAttributes.addAttribute("", InteractionContextExternalizer.ATR_VERSION, //$NON-NLS-1$
					InteractionContextExternalizer.ATR_VERSION, "", "1"); //$NON-NLS-1$ //$NON-NLS-2$

			handler.startElement("", InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY, //$NON-NLS-1$
					InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY, rootAttributes);
			// List could get modified as we're writing
			for (InteractionEvent ie : context.getInteractionHistory()) {
				Attributes ieAttributes = createEventAttributes(ie);
				handler.startElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT, //$NON-NLS-1$
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT, ieAttributes);
				handler.endElement("", SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT, //$NON-NLS-1$
						SaxContextContentHandler.ATTRIBUTE_INTERACTION_EVENT);
			}
			handler.endElement("", InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY, //$NON-NLS-1$
					InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY);

			handler.endDocument();
		}

		@Override
		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
		}
	}

	@SuppressWarnings({ "deprecation", "restriction" })
	private Attributes createEventAttributes(InteractionEvent ie) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(InteractionContextExternalizer.DATE_FORMAT_STRING,
				Locale.ENGLISH);

		AttributesImpl ieAttributes = new AttributesImpl();

		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_DELTA, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_DELTA, "", //$NON-NLS-1$
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(ie.getDelta()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_END_DATE, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_END_DATE, "", dateFormat.format(ie.getEndDate())); //$NON-NLS-1$
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_INTEREST, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_INTEREST, "", Float.toString(ie.getInterestContribution())); //$NON-NLS-1$
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_KIND, InteractionContextExternalizer.ATR_KIND, //$NON-NLS-1$
				"", ie.getKind().toString()); //$NON-NLS-1$
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_NAVIGATION, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_NAVIGATION, "", //$NON-NLS-1$
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(ie.getNavigation()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_ORIGIN_ID, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_ORIGIN_ID, "", //$NON-NLS-1$
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(ie.getOriginId()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_START_DATE, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_START_DATE, "", dateFormat.format(ie.getDate())); //$NON-NLS-1$
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_STRUCTURE_HANDLE, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_STRUCTURE_HANDLE, "", //$NON-NLS-1$
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(ie.getStructureHandle()));
		ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_STRUCTURE_KIND, //$NON-NLS-1$
				InteractionContextExternalizer.ATR_STRUCTURE_KIND, "", //$NON-NLS-1$
				org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(ie.getStructureKind()));

		if (ie instanceof AggregateInteractionEvent) {
			// keep the state of the element (how it was collapsed and when it was created) to ensure that the context is the same after writing
			ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_NUM_EVENTS, //$NON-NLS-1$
					InteractionContextExternalizer.ATR_NUM_EVENTS, "", //$NON-NLS-1$
					Integer.toString(((AggregateInteractionEvent) ie).getNumCollapsedEvents()));
			ieAttributes.addAttribute("", InteractionContextExternalizer.ATR_CREATION_COUNT, //$NON-NLS-1$
					InteractionContextExternalizer.ATR_CREATION_COUNT, "", //$NON-NLS-1$
					Integer.toString(((AggregateInteractionEvent) ie).getEventCountOnCreation()));
		}
		return ieAttributes;
	}
}
