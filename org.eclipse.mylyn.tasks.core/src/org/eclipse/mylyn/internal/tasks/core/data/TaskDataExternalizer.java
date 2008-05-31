/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

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
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Steffen Pingel
 */
public class TaskDataExternalizer {

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
			throw new IOException("No repository connector for kind \"" + connectorKind + "\" found");
		}

		String repositoryUrl = taskDataState.getRepositoryUrl();
		final TaskRepository taskRepository = taskRepositoryManager.getRepository(connectorKind, repositoryUrl);
		if (taskRepository == null) {
			throw new IOException("Repository \"" + repositoryUrl + "\" not found for kind \"" + connectorKind + "\"");
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

				public void handleException(Throwable exception) {
					// ignore
				}

				public void run() throws Exception {
					taskDataHandler.migrateTaskData(taskRepository, taskData);
				}

			});
		}
	}

	public TaskDataState readState(InputStream in) throws IOException {
		try {
			XMLReader parser = XMLReaderFactory.createXMLReader();
			TaskDataStateReader handler = new TaskDataStateReader(taskRepositoryManager);
			parser.setContentHandler(handler);
			parser.parse(new InputSource(in));
			TaskDataState taskDataState = handler.getTaskDataState();
			if (taskDataState != null) {
				migrate(taskDataState);
			}
			return taskDataState;
		} catch (SAXException e) {
			e.printStackTrace();
			throw new IOException("Error parsing task data: " + e.getMessage());
		}
	}

	public void writeState(OutputStream out, ITaskDataWorkingCopy state) throws IOException {
		try {
			SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler handler = transformerFactory.newTransformerHandler();
			Transformer serializer = handler.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			handler.setResult(new StreamResult(out));
			TaskDataStateWriter writer = new TaskDataStateWriter(handler);
			writer.write(state);
		} catch (TransformerException e) {
			throw new IOException("Error writing task data" + e.getMessageAndLocation());
		} catch (SAXException e) {
			throw new IOException("Error writing task data" + e.getMessage());
		}
	}

}
