/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import static org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.util.XMLChar;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
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
			IOException ioe = new IOException("OutputStream not set"); //$NON-NLS-1$
			throw ioe;
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(
					new SAXSource(new RepositoriesWriter(), new TaskRepositoriesInputSource(repositories)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not write repositories", //$NON-NLS-1$
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
			return repositories;
		}

	}

	private static class RepositoriesWriter implements XMLReader {

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
			if (!(input instanceof TaskRepositoriesInputSource)) {
				throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
			}

			Collection<TaskRepository> repositories = ((TaskRepositoriesInputSource) input).getRepositories();

			handler.startDocument();
			writeRepositories(repositories);
			handler.endDocument();
		}

		private void writeRepositories(Collection<TaskRepository> repositories) throws IOException, SAXException {
			AttributesImpl rootAttributes = new AttributesImpl();
			rootAttributes.addAttribute("", TaskRepositoriesExternalizer.ATTRIBUTE_VERSION, //$NON-NLS-1$
					TaskRepositoriesExternalizer.ATTRIBUTE_VERSION, "", "2"); //$NON-NLS-1$ //$NON-NLS-2$

			handler.startElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES, //$NON-NLS-1$
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES, rootAttributes);

			for (TaskRepository repository : new ArrayList<>(repositories)) {
				writeRepository(repository);
			}

			handler.endElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES, //$NON-NLS-1$
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORIES);
		}

		@SuppressWarnings({ "deprecation", "restriction" })
		private void writeRepository(TaskRepository repository) throws SAXException {
			// write properties as attributes to support reading by older versions
			AttributesImpl repositoryPropertyAttributes = new AttributesImpl();
			for (String key : repository.getProperties().keySet()) {
				// avoid emitting XML we cannnot read
				if (XMLChar.isValidName(key)) {
					repositoryPropertyAttributes.addAttribute("", //$NON-NLS-1$
							key, key, "", //$NON-NLS-1$
							convertToXmlString(repository.getProperties().get(key)));
				}
			}

			handler.startElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY, //$NON-NLS-1$
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY, repositoryPropertyAttributes);

			// write properties as child nodes to support attributes with special characters in their names
			for (String key : repository.getProperties().keySet()) {
				writeProperty(repository, key);
			}

			handler.endElement("", TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY, //$NON-NLS-1$
					TaskRepositoriesExternalizer.ELEMENT_TASK_REPOSITORY);
		}

		private void writeProperty(TaskRepository repository, String key) throws SAXException {
			if (!(key.equals(IRepositoryConstants.PROPERTY_CONNECTOR_KIND)
					|| key.equals(IRepositoryConstants.PROPERTY_URL))) {
				AttributesImpl propertiesAttributes = new AttributesImpl();
				addAttribute(propertiesAttributes, TaskRepositoriesExternalizer.PROPERTY_VALUE,
						repository.getProperties().get(key));
				addAttribute(propertiesAttributes, TaskRepositoriesExternalizer.PROPERTY_KEY, key);

				handler.startElement("", //$NON-NLS-1$
						TaskRepositoriesExternalizer.ELEMENT_PROPERTY, TaskRepositoriesExternalizer.ELEMENT_PROPERTY,
						propertiesAttributes);
				handler.endElement("", //$NON-NLS-1$
						TaskRepositoriesExternalizer.ELEMENT_PROPERTY, TaskRepositoriesExternalizer.ELEMENT_PROPERTY);
			}
		}

		@Override
		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
		}

		private void addAttribute(AttributesImpl attribute, String key, String value) {
			attribute.addAttribute("", //$NON-NLS-1$
					key, key, "", //$NON-NLS-1$
					value);
		}

	}
}
