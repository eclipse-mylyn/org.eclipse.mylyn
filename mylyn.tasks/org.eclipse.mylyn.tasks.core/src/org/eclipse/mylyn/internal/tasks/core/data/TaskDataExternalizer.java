/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.mylyn.internal.tasks.core.XmlReaderUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Steffen Pingel
 */
public class TaskDataExternalizer {

	/**
	 * Replaces the first 38 bytes of a an input stream with an XML 1.1 header.
	 */
	public static class Xml11InputStream extends FilterInputStream {

		/**
		 * XML 1.1 header.
		 */
		byte[] header;

		/**
		 * Current position in {@link #header}.
		 */
		int pointer;

		public Xml11InputStream(InputStream in) throws IOException {
			super(in);
			header = "<?xml version=\"1.1\" encoding=\"UTF-8\"?>".getBytes("US-ASCII"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public synchronized void reset() throws IOException {
			throw new IOException();
		}

		@Override
		public synchronized void mark(int readlimit) {
		}

		@Override
		public boolean markSupported() {
			return false;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			// fill b with bytes from header until the header has been read
			if (pointer < header.length) {
				int read = 0;
				for (; pointer < header.length && read < len; read++, pointer++) {
					b[off + read] = header[pointer];
					readByte();
				}
				return read;
			} else {
				return super.read(b, off, len);
			}
		}

		/**
		 * Advances the underlying stream by a single byte.
		 */
		private void readByte() throws IOException, EOFException {
			if (in.read() == -1) {
				throw new EOFException();
			}
		}

		@Override
		public int read() throws IOException {
			if (pointer < header.length) {
				readByte();
				return header[pointer++];
			} else {
				return super.read();
			}
		}

		@Override
		public long skip(long n) throws IOException {
			if (pointer < header.length) {
				// skip at most the number of bytes remaining in the header
				long skip = Math.min(header.length - pointer, n);
				// never skip more bytes than underlying stream
				skip = super.skip(skip);
				pointer += skip;
				return skip;
			} else {
				return super.skip(n);
			}
		}

	}

	private final IRepositoryManager taskRepositoryManager;

	public TaskDataExternalizer(IRepositoryManager taskRepositoryManager) {
		this.taskRepositoryManager = taskRepositoryManager;
	}

	private void migrate(final TaskDataState taskDataState) throws IOException {
		// for testing
		if (taskRepositoryManager == null) {
			return;
		}

		String connectorKind = taskDataState.getConnectorKind();
		AbstractRepositoryConnector connector = taskRepositoryManager.getRepositoryConnector(connectorKind);
		if (connector == null) {
			throw new IOException("No repository connector for kind \"" + connectorKind + "\" found"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		String repositoryUrl = taskDataState.getRepositoryUrl();
		final TaskRepository taskRepository = taskRepositoryManager.getRepository(connectorKind, repositoryUrl);
		if (taskRepository == null) {
			throw new IOException("Repository \"" + repositoryUrl + "\" not found for kind \"" + connectorKind + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}

		final AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		if (taskDataHandler != null) {
			migrate(taskDataState.getLastReadData(), taskRepository, taskDataHandler);
			migrate(taskDataState.getRepositoryData(), taskRepository, taskDataHandler);
			migrate(taskDataState.getEditsData(), taskRepository, taskDataHandler);
		}
	}

	private void migrate(final TaskData taskData, final TaskRepository taskRepository,
			final AbstractTaskDataHandler taskDataHandler) {
		if (taskData != null) {
			SafeRunner.run(new ISafeRunnable() {

				@Override
				public void handleException(Throwable exception) {
					// ignore
				}

				@Override
				public void run() throws Exception {
					taskDataHandler.migrateTaskData(taskRepository, taskData);
				}

			});
		}
	}

	public TaskDataState readState(InputStream in) throws IOException, SAXException {
		XMLReader parser = XmlReaderUtil.createXmlReader();
		TaskDataStateReader handler = new TaskDataStateReader(taskRepositoryManager);
		parser.setContentHandler(handler);
		parser.parse(new InputSource(in));
		TaskDataState taskDataState = handler.getTaskDataState();
		if (taskDataState != null) {
			migrate(taskDataState);
		}
		return taskDataState;
	}

	public void writeState(OutputStream out, ITaskDataWorkingCopy state) throws IOException {
		writeState(out, state, false);
	}

	private void writeState(OutputStream out, ITaskDataWorkingCopy state, boolean xml11) throws IOException {
		try {
			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = transformerFactory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			if (xml11) {
				serializer.setOutputProperty(OutputKeys.VERSION, "1.1"); //$NON-NLS-1$
			}
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			handler.setResult(new StreamResult(temp));
			TaskDataStateWriter writer = new TaskDataStateWriter(handler);
			writer.write(state);
			out.write(temp.toByteArray());
		} catch (TransformerException e) {
			throw new IOException("Error writing task data" + e.getMessageAndLocation()); //$NON-NLS-1$
		} catch (SAXException e) {
			// This condition is just like the one in org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore.readState(File)
			if (!xml11 && e.getMessage() != null && (e.getMessage().contains("invalid XML character") //$NON-NLS-1$
					|| e.getMessage().contains(" \"&#"))) { //$NON-NLS-1$
				writeState(out, state, true);
			} else {
				throw new IOException("Error writing task data" + e.getMessage()); //$NON-NLS-1$
			}
		}
	}

}
