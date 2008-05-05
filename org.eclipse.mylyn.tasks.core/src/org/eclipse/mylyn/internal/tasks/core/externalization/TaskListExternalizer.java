/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskExternalizationException;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 * @author Rob Elves
 * @author Jevgeni Holodkov
 */
public class TaskListExternalizer {

	private static final String ERROR_TASKLIST_READ = "Failed to load Task List";

	private static final String TRANSFORM_PROPERTY_VERSION = "version";

	// May 2007: There was a bug when reading in 1.1
	// Result was an infinite loop within the parser
	private static final String XML_VERSION = "1.0";

	public static final String ATTRIBUTE_VERSION = "Version";

	public static final String ELEMENT_TASK_LIST = "TaskList";

	private static final String VALUE_VERSION = "1.0.1";

	private static final String VALUE_VERSION_1_0_0 = "1.0.0";

	private List<AbstractTaskListFactory> externalizers;

	private DelegatingTaskExternalizer delagatingExternalizer;

	private final List<Node> orphanedTaskNodes = new ArrayList<Node>();

	private final List<Node> orphanedQueryNodes = new ArrayList<Node>();

	private String readVersion = "";

	public TaskListExternalizer() {
		this.delagatingExternalizer = new DelegatingTaskExternalizer();
	}

	public void setDelegateExternalizers(List<AbstractTaskListFactory> externalizers) {
		this.externalizers = externalizers;
		this.delagatingExternalizer.setFactories(externalizers);
	}

	public void writeTaskList(TaskList taskList, File outFile) throws CoreException {
		try {
			FileOutputStream outStream = new FileOutputStream(outFile);
			try {
				writeTaskList(taskList, outStream);
			} finally {
				outStream.close();
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Saving Task List failed",
					e));
		}
	}

	private void writeTaskList(TaskList taskList, OutputStream outputStream) throws IOException {
		Document doc = createTaskListDocument();
		if (doc == null) {
			return;
		}

		Element root = createTaskListRoot(doc);

		// create task nodes...
		for (AbstractTask task : taskList.getAllTasks()) {
			delagatingExternalizer.createTaskElement(task, doc, root);
		}

		// create the categorie nodes...
		for (ITaskElement category : taskList.getCategories()) {
			delagatingExternalizer.createCategoryElement(category, doc, root);
		}

		// create query nodes...
		for (AbstractRepositoryQuery query : taskList.getQueries()) {
			try {
				delagatingExternalizer.createQueryElement(query, doc, root);
			} catch (Throwable t) {
				// FIXME use log?
				StatusHandler.fail(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Did not externalize: "
						+ query.getSummary(), t));
			}
		}

		// Persist orphaned tasks...
		for (Node orphanedTaskNode : orphanedTaskNodes) {
			Node tempNode = doc.importNode(orphanedTaskNode, true);
			if (tempNode != null) {
				root.appendChild(tempNode);
			}
		}

		// Persist orphaned queries....
		for (Node orphanedQueryNode : orphanedQueryNodes) {
			Node tempNode = doc.importNode(orphanedQueryNode, true);
			if (tempNode != null) {
				root.appendChild(tempNode);
			}
		}

		ZipOutputStream zipOutStream = new ZipOutputStream(outputStream);
		writeTaskList(doc, zipOutStream);
		zipOutStream.finish();
	}

	/**
	 * @param doc
	 * @param outputStream
	 * @throws IOException
	 */
	private void writeTaskList(Document doc, ZipOutputStream outputStream) throws IOException {
		ZipEntry zipEntry = new ZipEntry(ITasksCoreConstants.OLD_TASK_LIST_FILE);
		outputStream.putNextEntry(zipEntry);
		outputStream.setMethod(ZipOutputStream.DEFLATED);
		writeDOMtoStream(doc, outputStream);
		outputStream.flush();
		outputStream.closeEntry();
	}

	/**
	 * Writes the provided XML document out to the specified output stream.
	 * 
	 * doc - the document to be written outputStream - the stream to which the document is to be written
	 */
	private void writeDOMtoStream(Document doc, OutputStream outputStream) {
		// Prepare the DOM document for writing
		// DOMSource - Acts as a holder for a transformation Source tree in the
		// form of a Document Object Model (DOM) tree
		Source source = new DOMSource(doc);

		// StreamResult - Acts as an holder for a XML transformation result
		// Prepare the output stream
		Result result = new StreamResult(outputStream);

		// An instance of this class can be obtained with the
		// TransformerFactory.newTransformer method. This instance may
		// then be used to process XML from a variety of sources and write
		// the transformation output to a variety of sinks

		Transformer xformer = null;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(TRANSFORM_PROPERTY_VERSION, XML_VERSION);
			xformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e1) {
			e1.printStackTrace();
		}
	}

	private Document createTaskListDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.newDocument();
		} catch (ParserConfigurationException e) {
			// FIXME propagate exception?
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not create document", e));
			return doc;
		}

		return doc;
	}

	private Element createTaskListRoot(Document doc) {
		Element root = doc.createElement(ELEMENT_TASK_LIST);
		root.setAttribute(ATTRIBUTE_VERSION, VALUE_VERSION);
		doc.appendChild(root);
		return root;
	}

	/**
	 * Reads the Query from the specified Node. If taskList is not null, then also adds this query to the TaskList
	 * 
	 * @throws TaskExternalizationException
	 */
	private AbstractRepositoryQuery readQuery(TaskList taskList, Node child) {
		AbstractRepositoryQuery query = null;
		for (AbstractTaskListFactory externalizer : externalizers) {
			Set<String> queryTagNames = externalizer.getQueryElementNames();
			if (queryTagNames != null && queryTagNames.contains(child.getNodeName())) {
				Element childElement = (Element) child;
				// TODO: move this stuff into externalizer
				String repositoryUrl = childElement.getAttribute(DelegatingTaskExternalizer.KEY_REPOSITORY_URL);
				String queryString = childElement.getAttribute(AbstractTaskListFactory.KEY_QUERY_STRING);
				if (queryString.length() == 0) { // fallback for legacy
					queryString = childElement.getAttribute(AbstractTaskListFactory.KEY_QUERY);
				}
				String label = childElement.getAttribute(DelegatingTaskExternalizer.KEY_NAME);
				if (label.length() == 0) { // fallback for legacy
					label = childElement.getAttribute(DelegatingTaskExternalizer.KEY_LABEL);
				}

				query = externalizer.createQuery(repositoryUrl, queryString, label, childElement);
				if (query != null) {
					if (childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH) != null
							&& !childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH).equals("")) {
						query.setLastSynchronizedStamp(childElement.getAttribute(DelegatingTaskExternalizer.KEY_LAST_REFRESH));
					}
				}

				// add created Query to the TaskList and read QueryHits (Tasks related to the Query)
				if (taskList != null) {
					if (query != null) {
						taskList.addQuery(query);
					}

					NodeList queryChildren = child.getChildNodes();
					delagatingExternalizer.readTaskReferences(query, queryChildren, taskList);
				}

				break;
			}
		}
		if (query == null) {
			orphanedQueryNodes.add(child);
		}

		return query;
	}

	public void setDelegatingExternalizer(DelegatingTaskExternalizer delagatingExternalizer) {
		this.delagatingExternalizer = delagatingExternalizer;
	}

	public List<AbstractTaskListFactory> getExternalizers() {
		return externalizers;
	}

	public void readTaskList(TaskList taskList, File inFile) throws CoreException {
		delagatingExternalizer.getLegacyParentCategoryMap().clear();
		Map<AbstractTask, NodeList> tasksWithSubtasks = new HashMap<AbstractTask, NodeList>();
		if (!inFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task list file not found \"" + inFile.getAbsolutePath() + "\""));
		}

		Document doc;
		doc = openAsDOM(inFile);

		if (doc == null) {
			StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Empty TaskList"));
			return;
		}

		Element root = doc.getDocumentElement();
		readVersion = root.getAttribute(ATTRIBUTE_VERSION);

		if (readVersion.equals(VALUE_VERSION_1_0_0)) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task list version \""
					+ readVersion + "\" not supported"));
		} else {
			NodeList list = root.getChildNodes();

			// Read Tasks
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
				if (!child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)
						&& !child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {

					AbstractTask task = delagatingExternalizer.readTask(child, null, null);
					if (task == null) {
						orphanedTaskNodes.add(child);
					} else {
						taskList.addTask(task);
						if (child.getChildNodes() != null && child.getChildNodes().getLength() > 0) {
							tasksWithSubtasks.put(task, child.getChildNodes());
						}
					}
				}
			}

			for (AbstractTask task : tasksWithSubtasks.keySet()) {
				NodeList nodes = tasksWithSubtasks.get(task);
				delagatingExternalizer.readTaskReferences(task, nodes, taskList);
			}

			// Read Queries
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
				if (child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
					readQuery(taskList, child);
				}
			}

			// Read Categories
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
				if (child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)) {
					delagatingExternalizer.readCategory(child, taskList);
				}
			}

			// Legacy migration for task nodes that have the old Category handle on the element
			if (delagatingExternalizer.getLegacyParentCategoryMap().size() > 0) {
				for (AbstractTask task : delagatingExternalizer.getLegacyParentCategoryMap().keySet()) {
					AbstractTaskCategory category = taskList.getContainerForHandle(delagatingExternalizer.getLegacyParentCategoryMap()
							.get(task));
					if (category != null) {
						if (task instanceof LocalTask && !task.getParentContainers().isEmpty()) {
							continue;
						}
						taskList.addTask(task, category);
					}
				}
			}
		}
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
	private Document openAsDOM(File inputFile) throws CoreException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		try {
			builder = factory.newDocumentBuilder();
			InputStream inputStream = null;
			if (inputFile.getName().endsWith(ITasksCoreConstants.FILE_EXTENSION)) {
				inputStream = new ZipInputStream(new FileInputStream(inputFile));
				ZipEntry entry = ((ZipInputStream) inputStream).getNextEntry();
				while (entry != null) {
					if (ITasksCoreConstants.OLD_TASK_LIST_FILE.equals(entry.getName())) {
						break;
					}
					entry = ((ZipInputStream) inputStream).getNextEntry();
				}
				if (entry == null) {
					return null;
				}
			} else {
				inputStream = new FileInputStream(inputFile);
			}
			document = builder.parse(inputStream);
		} catch (Exception se) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, ERROR_TASKLIST_READ, se));
		}
		return document;
	}

	// Stream based versions of task list reading
//	public void readTaskList(TaskList taskList, InputStream inFile) throws CoreException {
//	delagatingExternalizer.getLegacyParentCategoryMap().clear();
//	Map<AbstractTask, NodeList> tasksWithSubtasks = new HashMap<AbstractTask, NodeList>();
//	orphanedTaskNodes.clear();
//	orphanedQueryNodes.clear();
//	Document doc = openAsDOM(inFile);
//	Element root = doc.getDocumentElement();
//	readVersion = root.getAttribute(ATTRIBUTE_VERSION);
//
//	if (readVersion.equals(VALUE_VERSION_1_0_0)) {
//		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Task list version \""
//				+ readVersion + "\" not supported"));
//	} else {
//		NodeList list = root.getChildNodes();
//
//		// Read Tasks
//		for (int i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			if (!child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)
//					&& !child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
//
//				AbstractTask task = delagatingExternalizer.readTask(child, null, null);
//				if (task == null) {
//					orphanedTaskNodes.add(child);
//				} else {
//					taskList.addTask(task);
//					if (child.getChildNodes() != null && child.getChildNodes().getLength() > 0) {
//						tasksWithSubtasks.put(task, child.getChildNodes());
//					}
//				}
//			}
//		}
//
//		for (AbstractTask task : tasksWithSubtasks.keySet()) {
//			NodeList nodes = tasksWithSubtasks.get(task);
//			delagatingExternalizer.readTaskReferences(task, nodes, taskList);
//		}
//
//		// Read Queries
//		for (int i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			if (child.getNodeName().endsWith(AbstractTaskListFactory.KEY_QUERY)) {
//				readQuery(taskList, child);
//			}
//		}
//
//		// Read Categories
//		for (int i = 0; i < list.getLength(); i++) {
//			Node child = list.item(i);
//			if (child.getNodeName().endsWith(DelegatingTaskExternalizer.KEY_CATEGORY)) {
//				delagatingExternalizer.readCategory(child, taskList);
//			}
//		}
//
//		// Legacy migration for task nodes that have the old Category handle on the element
//		if (delagatingExternalizer.getLegacyParentCategoryMap().size() > 0) {
//			for (AbstractTask task : delagatingExternalizer.getLegacyParentCategoryMap().keySet()) {
//				AbstractTaskCategory category = taskList.getContainerForHandle(delagatingExternalizer.getLegacyParentCategoryMap()
//						.get(task));
//				if (category != null) {
//					taskList.addTask(task, category);
//				}
//			}
//		}
//	}
//}

///**
// * Parses <code>inputStream</code> into a DOM Document.
// */
//private Document openAsDOM(InputStream inputStream) throws CoreException {
//	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//	DocumentBuilder builder = null;
//	Document document = null;
//	try {
//		builder = factory.newDocumentBuilder();
//		document = builder.parse(inputStream);
//	} catch (Exception e) {
//		throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, ERROR_READING_TASKLIST, e));
//	}
//	return document;
//}
}
