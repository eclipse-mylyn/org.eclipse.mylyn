/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListExternalizer {

	private static final String ERROR_TASKLIST_READ = "Failed to load Task List";

	private static final String TRANSFORM_PROPERTY_VERSION = "version";

	// May 2007: There was a bug when reading in 1.1
	// Result was an infinite loop within the parser
	private static final String XML_VERSION = "1.0";

	public static final String ATTRIBUTE_VERSION = "Version";

	public static final String ELEMENT_TASK_LIST = "TaskList";

	// Mylyn 3.0
	private static final String VALUE_VERSION = "2.0";

	// Mylyn 2.3.2
	//private static final String VALUE_VERSION_1_0_1 = "1.0.1";

	private static final String VALUE_VERSION_1_0_0 = "1.0.0";

	private final DelegatingTaskExternalizer delegatingExternalizer;

	private final List<Node> orphanedNodes = new ArrayList<Node>();

	private String readVersion = "";

	public TaskListExternalizer(RepositoryModel repositoryModel, IRepositoryManager repositoryManager) {
		this.delegatingExternalizer = new DelegatingTaskExternalizer(repositoryModel, repositoryManager);
	}

	public void initialize(List<AbstractTaskListFactory> externalizers, List<AbstractTaskListMigrator> migrators) {
		this.delegatingExternalizer.initialize(externalizers, migrators);
	}

	public void writeTaskList(TaskList taskList, File outFile) throws CoreException {
		try {
			FileOutputStream outStream = new FileOutputStream(outFile);
			try {
				Document doc = createTaskListDocument(taskList);

				ZipOutputStream zipOutStream = new ZipOutputStream(outStream);

				ZipEntry zipEntry = new ZipEntry(ITasksCoreConstants.OLD_TASK_LIST_FILE);
				zipOutStream.putNextEntry(zipEntry);
				zipOutStream.setMethod(ZipOutputStream.DEFLATED);

				writeDocument(doc, zipOutStream);

				zipOutStream.flush();
				zipOutStream.closeEntry();
				zipOutStream.finish();
			} finally {
				outStream.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Saving Task List failed",
					e));
		}
	}

	private Document createTaskListDocument(TaskList taskList) throws CoreException {
		Document doc = createDocument();

		delegatingExternalizer.clearErrorStatus();

		Element root = doc.createElement(ELEMENT_TASK_LIST);
		root.setAttribute(ATTRIBUTE_VERSION, VALUE_VERSION);
		doc.appendChild(root);

		// create task nodes...
		for (AbstractTask task : taskList.getAllTasks()) {
			delegatingExternalizer.createTaskElement(task, doc, root);
		}

		// create the category nodes...
		for (AbstractTaskCategory category : taskList.getCategories()) {
			delegatingExternalizer.createCategoryElement(category, doc, root);
		}

		// create query nodes...
		for (RepositoryQuery query : taskList.getQueries()) {
			delegatingExternalizer.createQueryElement(query, doc, root);
		}

		// Persist orphaned tasks...
		for (Node node : orphanedNodes) {
			Node tempNode = doc.importNode(node, true);
			if (tempNode != null) {
				root.appendChild(tempNode);
			}
		}

		if (delegatingExternalizer.getErrorStatus() != null) {
			StatusHandler.log(delegatingExternalizer.getErrorStatus());
		}

		return doc;
	}

	private void writeDocument(Document doc, OutputStream outputStream) throws CoreException {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(outputStream);
		try {
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(TRANSFORM_PROPERTY_VERSION, XML_VERSION);
			xformer.transform(source, result);
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Failed write task list",
					e));
		}
	}

	private Document createDocument() throws CoreException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			return db.newDocument();
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Failed to create document", e));
		}
	}

	public void readTaskList(TaskList taskList, File inFile) throws CoreException {
		if (!inFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task list file not found \"" + inFile.getAbsolutePath() + "\""));
		}
		if (inFile.length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task list file contains no data \"" + inFile.getAbsolutePath() + "\""));
		}

		delegatingExternalizer.reset();
		orphanedNodes.clear();

		Document doc = openTaskList(inFile);
		Element root = doc.getDocumentElement();
		readVersion = root.getAttribute(ATTRIBUTE_VERSION);
		if (readVersion.equals(VALUE_VERSION_1_0_0)) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task list version \""
					+ readVersion + "\" not supported"));
		}

		NodeList list = root.getChildNodes();

		// read tasks
		Map<AbstractTask, NodeList> tasksWithSubtasks = new HashMap<AbstractTask, NodeList>();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (!child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)
					&& !child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
				AbstractTask task = delegatingExternalizer.readTask(child, null, null);
				if (task != null) {
					taskList.addTask(task);
					if (child.getChildNodes() != null && child.getChildNodes().getLength() > 0) {
						tasksWithSubtasks.put(task, child.getChildNodes());
					}
				} else {
					orphanedNodes.add(child);
				}
			}
		}
		// create subtask hierarchy
		for (AbstractTask task : tasksWithSubtasks.keySet()) {
			NodeList nodes = tasksWithSubtasks.get(task);
			delegatingExternalizer.readTaskReferences(task, nodes, taskList);
		}

		// read queries
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
				RepositoryQuery query = delegatingExternalizer.readQuery(child);
				if (query != null) {
					taskList.addQuery(query);
					if (child.getChildNodes() != null && child.getChildNodes().getLength() > 0) {
						delegatingExternalizer.readTaskReferences(query, child.getChildNodes(), taskList);
					}
				} else {
					orphanedNodes.add(child);
				}
			}
		}

		// Read Categories
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)) {
				delegatingExternalizer.readCategory(child, taskList);
			}
		}

		// Legacy migration for task nodes that have the old Category handle on the element
		Map<AbstractTask, String> legacyParentCategoryMap = delegatingExternalizer.getLegacyParentCategoryMap();
		if (legacyParentCategoryMap.size() > 0) {
			for (AbstractTask task : legacyParentCategoryMap.keySet()) {
				AbstractTaskCategory category = taskList.getContainerForHandle(legacyParentCategoryMap.get(task));
				if (category != null) {
					taskList.addTask(task, category);
				}
			}
		}

//		if (delegatingExternalizer.getErrorStatus() != null) {
//			StatusHandler.log(delegatingExternalizer.getErrorStatus());
//		}
	}

	/**
	 * Opens the specified XML file and parses it into a DOM Document.
	 * 
	 * Filename - the name of the file to open Return - the Document built from the XML file Throws - XMLException if
	 * the file cannot be parsed as XML - IOException if the file cannot be opened
	 * 
	 * @throws CoreException
	 * 
	 */
	private Document openTaskList(File inputFile) throws CoreException {
		InputStream in = null;
		try {
			if (inputFile.getName().endsWith(ITasksCoreConstants.FILE_EXTENSION)) {
				in = new ZipInputStream(new FileInputStream(inputFile));
				ZipEntry entry = ((ZipInputStream) in).getNextEntry();
				while (entry != null) {
					if (ITasksCoreConstants.OLD_TASK_LIST_FILE.equals(entry.getName())) {
						break;
					}
					entry = ((ZipInputStream) in).getNextEntry();
				}
				if (entry == null) {
					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Task list file contains no entry for the task list"));
				}
			} else {
				in = new FileInputStream(inputFile);
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(in);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, ERROR_TASKLIST_READ, e));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Failed to close task list", e));
				}
			}
		}
	}

}
