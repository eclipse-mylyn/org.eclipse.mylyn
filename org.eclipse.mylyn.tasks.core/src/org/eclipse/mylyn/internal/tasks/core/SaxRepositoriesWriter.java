/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.commons.core.XmlStringConverter;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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
 * Adapted from SaxContextWriter
 * 
 * @author Rob Elves
 */
public class SaxRepositoriesWriter {

	private OutputStream outputStream;

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeRepositoriesToStream(Collection<TaskRepository> repositories) throws IOException {
		if (outputStream == null) {
			IOException ioe = new IOException("OutputStream not set");
			throw ioe;
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(
					new SAXSource(new RepositoriesWriter(), new TaskRepositoriesInputSource(repositories)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not write repositories",
					e));
			throw new IOException(e.getMessage());
		}

	}

	private static class TaskRepositoriesInputSource extends InputSource {
		private final Collection<TaskRepository> repositories;

		public TaskRepositoriesInputSource(Collection<TaskRepository> repositories) {
			this.repositories = repositories;
		}

		public Collection<TaskRepository> getRepositories() {
			return this.repositories;
		}

	}

	private static class RepositoriesWriter implements XMLReader {

//		private static final String ELEMENT_TASK_REPOSITORIES = "TaskRepositories";
//
//		public static final String ELEMENT_TASK_REPOSITORY = "TaskRepository";
//
//		private static final String ATTRIBUTE_VERSION = "xmlVersion";

//		private static final String ATTRIBUTE_URL = "Url";
//
//		private static final String ATTRIBUTE_KIND = "Kind";

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
			if (!(input instanceof TaskRepositoriesInputSource)) {
				throw new SAXException("Can only parse writable input sources");
			}

			Collection<TaskRepository> repositories = ((TaskRepositoriesInputSource) input).getRepositories();

			handler.startDocument();
			AttributesImpl rootAttributes = new AttributesImpl();
			rootAttributes.addAttribute("", TaskRepositoriesExternalizer.ATTRIBUTE_VERSION,
					TaskRepositoriesExternalizer.ATTRIBUTE_VERSION, "", "1");

			handler.startElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES,
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES, rootAttributes);

			for (TaskRepository repository : new ArrayList<TaskRepository>(repositories)) {

				AttributesImpl ieAttributes = new AttributesImpl();
				for (String key : repository.getProperties().keySet()) {
					ieAttributes.addAttribute("", key, key, "",
							XmlStringConverter.convertToXmlString(repository.getProperties().get(key)));
				}

				handler.startElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY,
						TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY, ieAttributes);
				handler.endElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY,
						TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY);
			}
			handler.endElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES,
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES);

			handler.endDocument();
		}

		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources");
		}

	}
}
