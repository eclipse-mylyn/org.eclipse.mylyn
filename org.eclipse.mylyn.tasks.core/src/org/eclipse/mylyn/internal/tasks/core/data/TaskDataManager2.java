/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler2;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager2;
import org.eclipse.mylyn.tasks.core.data.ITaskDataState;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Steffen Pingel
 */
public class TaskDataManager2 implements ITaskDataManager2 {

	private static final String ENCODING_UTF_8 = "UTF-8";

	private static final String EXTENSION = ".zip";

	private static final String FILE_NAME_INTERNAL = "data.xml";

	private static final String FOLDER_DATA = "tasks";

	private static final String FOLDER_DATA_1_0 = "offline";

	private String dataPath;

	private final ITaskRepositoryManager repositoryManager;

	public TaskDataManager2(ITaskRepositoryManager taskRepositoryManager) {
		this.repositoryManager = taskRepositoryManager;
	}

	public ITaskDataState createWorkingCopy(AbstractTask task, String kind) throws CoreException {
		TaskDataState state = readState(task, kind);
		if (state == null) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task data for task "
					+ task.getHandleIdentifier() + " not found"));
		}
		state.init(this, task);
		state.revert();
		return state;
	}

	private File findFile(AbstractTask task, String kind) {
		File file = getFile(task, kind);
		if (file.exists()) {
			return file;
		}
		return getFile10(task, kind);
	}

	public String getDataPath() {
		return dataPath;
	}

	private File getFile(AbstractTask task, String kind) {
		try {
			String pathName = task.getConnectorKind() + "-"
					+ URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
			String fileName = kind + "-" + URLEncoder.encode(task.getTaskId(), ENCODING_UTF_8) + EXTENSION;
			File path = new File(dataPath + File.separator + FOLDER_DATA, pathName);
			return new File(path, fileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private File getFile10(AbstractTask task, String kind) {
		try {
			String pathName = URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
			String fileName = task.getTaskId() + EXTENSION;
			File path = new File(dataPath + File.separator + FOLDER_DATA_1_0, pathName);
			return new File(path, fileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}

	public boolean hasTaskData(AbstractTask task, String kind) {
		return findFile(task, kind).exists();
	}

	private void migrate(TaskDataState taskDataState) throws IOException {
		// for testing
		if (repositoryManager == null) {
			return;
		}

		String connectorKind = taskDataState.getConnectorKind();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(connectorKind);
		if (connector == null) {
			throw new IOException("No repository connector for kind \"" + connectorKind + "\" found");
		}

		String repositoryUrl = taskDataState.getRepositoryUrl();
		TaskRepository taskRepository = repositoryManager.getRepository(connectorKind, repositoryUrl);
		if (taskRepository == null) {
			throw new IOException("Repository \"" + repositoryUrl + "\" not found for kind \"" + connectorKind + "\"");
		}

		AbstractTaskDataHandler2 taskDataHandler = connector.getTaskDataHandler2();
		if (taskDataHandler != null) {
			if (taskDataState.getLastReadData() != null) {
				taskDataHandler.migrateTaskData(taskRepository, taskDataState.getLastReadData());
			}
			if (taskDataState.getRepositoryData() != null) {
				taskDataHandler.migrateTaskData(taskRepository, taskDataState.getRepositoryData());
			}
			if (taskDataState.getEditsData() != null) {
				taskDataHandler.migrateTaskData(taskRepository, taskDataState.getEditsData());
			}
		}
	}

	private TaskDataState readState(AbstractTask task, String kind) throws CoreException {
		try {
			File file = findFile(task, kind);
			if (file.exists()) {
				ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				try {
					in.getNextEntry();
					return readState(in);
				} finally {
					in.close();
				}
			}
			return null;
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading task data",
					e));
		}
	}

	public TaskDataState readState(InputStream in) throws IOException {
		try {
			XMLReader parser = XMLReaderFactory.createXMLReader();
			TaskDataStateReader handler = new TaskDataStateReader(repositoryManager);
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

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public void setEdits(AbstractTask task, String kind, TaskData data) throws CoreException {
		Assert.isNotNull(task);
		Assert.isNotNull(kind);
		Assert.isNotNull(data);

		TaskDataState state = readState(task, kind);
		if (state == null) {
			state = new TaskDataState(task.getConnectorKind(), task.getRepositoryUrl(), task.getTaskId());
		}
		state.setEditsData(data);
		writeState(task, kind, state);
	}

	public void setTaskData(AbstractTask task, String kind, TaskData data) throws CoreException {
		Assert.isNotNull(task);
		Assert.isNotNull(kind);
		Assert.isNotNull(data);

		TaskDataState state = readState(task, kind);
		if (state == null) {
			state = new TaskDataState(task.getConnectorKind(), task.getRepositoryUrl(), task.getTaskId());
		}
		state.setRepositoryData(data);
		writeState(task, kind, state);
	}

	void writeState(AbstractTask task, String kind, ITaskDataState state) throws CoreException {
		try {
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(getFile(task, kind))));
			try {
				out.setMethod(ZipOutputStream.DEFLATED);

				ZipEntry entry = new ZipEntry(FILE_NAME_INTERNAL);
				out.putNextEntry(entry);

				writeState(out, state);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading task data",
					e));
		}
	}

	public void writeState(OutputStream out, ITaskDataState state) throws IOException {
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
