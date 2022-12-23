/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.SaxRepositoriesWriter;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Adapted from {@link SaxRepositoriesWriter}
 */
public class SaxTaskListWriter {

	private static final String ATTRIBUTE_VERSION = "Version"; //$NON-NLS-1$

	// Mylyn 3.0
	private static final String VALUE_VERSION = "2.0"; //$NON-NLS-1$

	private OutputStream outputStream;

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeTaskListToStream(ITransferList taskList, Document orphans) throws IOException {
		if (outputStream == null) {
			throw new IOException("OutputStream not set"); //$NON-NLS-1$
		}

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new SAXSource(new TaskListWriter(), new TaskListInputSource(taskList, orphans)),
					new StreamResult(outputStream));
		} catch (TransformerException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not write task list", e)); //$NON-NLS-1$
			throw new IOException(e.getMessage(), e);
		}

	}

	private static class TaskListInputSource extends InputSource {
		private final ITransferList taskList;

		private final Document orphans;

		public TaskListInputSource(ITransferList taskList, Document orphans) {
			this.taskList = taskList;
			this.orphans = orphans;
		}

		public ITransferList getTaskList() {
			return this.taskList;
		}

		public Document getOrphans() {
			return orphans;
		}

	}

	private static class TaskListWriter implements XMLReader {

		private ContentHandlerWrapper handler;

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
			this.handler = new ContentHandlerWrapper(handler);
		}

		public ContentHandler getContentHandler() {
			return handler.getHandler();
		}

		public void setErrorHandler(ErrorHandler handler) {
			this.errorHandler = handler;
		}

		public ErrorHandler getErrorHandler() {
			return errorHandler;
		}

		public void parse(InputSource input) throws IOException, SAXException {
			if (!(input instanceof TaskListInputSource)) {
				throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
			}
			TaskListInputSource taskListInputSource = (TaskListInputSource) input;

			handler.getHandler().startDocument();
			writeTaskList(taskListInputSource.getTaskList(), taskListInputSource.getOrphans());
			handler.getHandler().endDocument();
		}

		private void writeTaskList(ITransferList taskList, Document orphanDocument) throws IOException, SAXException {
			AttributesWrapper attributes = new AttributesWrapper();
			attributes.addAttribute(ATTRIBUTE_VERSION, VALUE_VERSION);
			handler.startElement(TaskListExternalizationConstants.NODE_TASK_LIST, attributes);

			writeTaskListElements(new SaxTaskWriter(handler), taskList.getAllTasks());
			writeTaskListElements(new SaxCategoryWriter(handler), taskList.getCategories());
			writeTaskListElements(new SaxQueryWriter(handler), taskList.getQueries());

			writeOrphans(orphanDocument);

			handler.endElement(TaskListExternalizationConstants.NODE_TASK_LIST);
		}

		private <T extends IRepositoryElement> void writeTaskListElements(SaxTaskListElementWriter<T> writer,
				Collection<T> elements) throws SAXException {
			for (T element : elements) {
				writer.writeElement(element);
			}
			if (!writer.getErrors().isOK()) {
				StatusHandler.log(writer.getErrors());
			}
		}

		private void writeOrphans(Document orphanDocument) throws SAXException {
			if (orphanDocument != null) {
				SaxOrphanWriter writer = new SaxOrphanWriter(handler);
				NodeList orphanNodes = orphanDocument.getChildNodes();
				if (orphanNodes.getLength() == 1) {
					writer.writeOrphans(orphanNodes.item(0).getChildNodes());
				}
			}
		}

		public void parse(String systemId) throws IOException, SAXException {
			throw new SAXException("Can only parse writable input sources"); //$NON-NLS-1$
		}
	}
}
