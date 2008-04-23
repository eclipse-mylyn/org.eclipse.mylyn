/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.ZipInputStream;

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

	private static final String FOLDER_DATA = "tasks";

	private static final String FOLDER_DATA_1_0 = "offline";

	private static final String FILE_NAME_INTERNAL = "data.xml";

	private final ITaskRepositoryManager repositoryManager;

	private static final String ENCODING_UTF_8 = "UTF-8";

	private static final String SCHEMA_VERSION = "1.0";

	private static final String EXTENSION = ".zip";

	private String dataPath;

	public TaskDataManager2(ITaskRepositoryManager taskRepositoryManager) {
		this.repositoryManager = taskRepositoryManager;
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
			FileOutputStream out = new FileOutputStream(getFile(task, kind));
			try {
				writeState(out, state);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading task data",
					e));
		}
	}

	private TaskDataState readState(AbstractTask task, String kind) throws CoreException {
		try {
			File file = getFile(task, kind);
			if (file.exists()) {
				ZipInputStream in = new ZipInputStream(new FileInputStream(file));
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

	private File getFile(AbstractTask task, String kind) {
		try {
			String folder = task.getConnectorKind() + "-" + URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
			File repositoryFolder = new File(dataPath + File.separator + FOLDER_DATA, folder);
			File dataFile = new File(repositoryFolder, URLEncoder.encode(task.getTaskId(), ENCODING_UTF_8) + EXTENSION);
			if (dataFile.exists()) {
				return dataFile;
			}
			return getFile10(task, kind);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private File getFile10(AbstractTask task, String kind) throws UnsupportedEncodingException {
		String folder = URLEncoder.encode(task.getRepositoryUrl(), ENCODING_UTF_8);
		File repositoryFolder = new File(dataPath + File.separator + FOLDER_DATA_1_0, folder);
		File dataFile = new File(repositoryFolder, task.getTaskId() + EXTENSION);
		return dataFile;
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

	private void migrate(TaskDataState taskDataState) throws IOException {
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

	private void writeState(OutputStream out, ITaskDataState state) throws IOException {
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

	public ITaskDataState createWorkingCopy(AbstractTask task, String kind) throws CoreException {
		TaskDataState state = readState(task, kind);
		if (state == null) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task data for task "
					+ task.getHandleIdentifier() + " not found"));
		}
		state.init(this, task);
		state.createLocalData();

		return state;
	}

	public boolean hasTaskData(AbstractTask task, String kind) {
		return getFile(task, kind).exists();
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	//	/**
//	 * Returns the old copy if exists, null otherwise.
//	 */
//	public RepositoryTaskData getOldTaskData(String repositoryUrl, String id) {
//		if (repositoryUrl == null || id == null) {
//			return null;
//		}
//		TaskDataState state = readTaskData(repositoryUrl, id);
//		if (state != null) {
//			return state.getOldTaskData();
//		}
//		return null;
//	}
//
//	/**
//	 * 
//	 * @return editable copy of task data with any edits applied
//	 */
//	public RepositoryTaskData getEditableCopy(String repositoryUrl, String id) {
//		if (repositoryUrl == null || id == null) {
//			return null;
//		}
//		TaskDataState state = readTaskData(repositoryUrl, id);
//		RepositoryTaskData clone = null;
//		if (state != null) {
//			if (state.getNewTaskData() != null) {
//				try {
//					clone = (RepositoryTaskData) ObjectCloner.deepCopy(state.getNewTaskData());
//					updateAttributeFactory(clone);
//				} catch (Exception e) {
//					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
//							"Error constructing modifiable task", e));
//					return null;
//				}
//			}
//			if (clone != null) {
//				for (RepositoryTaskAttribute attribute : state.getEdits()) {
//					if (attribute == null) {
//						continue;
//					}
//					RepositoryTaskAttribute existing = clone.getAttribute(attribute.getId());
//					if (existing != null) {
//						existing.clearValues();
//						List<String> options = existing.getOptions();
//
//						for (String value : attribute.getValues()) {
//							if (options.size() > 0) {
//								if (options.contains(value)) {
//									existing.addValue(value);
//								}
//							} else {
//								existing.addValue(value);
//							}
//						}
//
//					} else {
//						clone.addAttribute(attribute.getId(), attribute);
//					}
//				}
//			}
//		}
//		return clone;
//
//	}
//
//	// API 3.0 review: the state of the elements of changedAttribues could change between this call and the time state is written to disk, might need to make a full copy  
//	public void saveEdits(String repositoryUrl, String id, Set<RepositoryTaskAttribute> changedAttributes) {
//		TaskDataState state = readTaskData(repositoryUrl, id);
//		if (state != null) {
//			Set<RepositoryTaskAttribute> edits = state.getEdits();
//			if (edits == null) {
//				// Copy here?
//				state.setEdits(changedAttributes);
//			} else {
//				edits.removeAll(changedAttributes);
//				edits.addAll(changedAttributes);
//			}
//			try {
//				storage.put(state);
//			} catch (Exception e) {
//				// FIXME what exception is caught here?
//				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error saving edits", e));
//			}
//		}
//
//	}
//
//	public Set<RepositoryTaskAttribute> getEdits(String repositoryUrl, String id) {
//		if (repositoryUrl == null || id == null) {
//			return Collections.emptySet();
//		}
//		TaskDataState state = readTaskData(repositoryUrl, id);
//		if (state != null) {
//			if (state.getEdits() != null) {
//				return Collections.unmodifiableSet(state.getEdits());
//			}
//		}
//		return Collections.emptySet();
//	}
//
//	public void discardEdits(String repositoryUrl, String id) {
//		if (repositoryUrl == null || id == null) {
//			return;
//		}
//		TaskDataState state = readTaskData(repositoryUrl, id);
//		if (state != null) {
//			state.discardEdits();
//			try {
//				storage.put(state);
//			} catch (Exception e) {
//				// FIXME what exception is caught here?
//				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error discarding edits", e));
//			}
//		}
//	}

}
