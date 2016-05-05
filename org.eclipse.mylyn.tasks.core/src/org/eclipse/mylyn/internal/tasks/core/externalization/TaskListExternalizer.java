/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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
import java.util.List;
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
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.XmlReaderUtil;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListExternalizer {

	private static final String ERROR_TASKLIST_READ = "Failed to load Task List"; //$NON-NLS-1$

	private static final String TRANSFORM_PROPERTY_VERSION = "version"; //$NON-NLS-1$

	// May 2007: There was a bug when reading in 1.1
	// Result was an infinite loop within the parser
	private static final String XML_VERSION = "1.0"; //$NON-NLS-1$

	public static final String ATTRIBUTE_VERSION = "Version"; //$NON-NLS-1$

	public static final String ELEMENT_TASK_LIST = "TaskList"; //$NON-NLS-1$

	// Mylyn 3.0
	private static final String VALUE_VERSION = "2.0"; //$NON-NLS-1$

	private final DelegatingTaskExternalizer delegatingExternalizer;

	private final RepositoryModel repositoryModel;

	private final IRepositoryManager repositoryManager;

	private Document orphanDocument;

	public TaskListExternalizer(RepositoryModel repositoryModel, IRepositoryManager repositoryManager) {
		this.repositoryModel = repositoryModel;
		this.repositoryManager = repositoryManager;
		this.delegatingExternalizer = new DelegatingTaskExternalizer(repositoryModel, repositoryManager);
	}

	public void initialize(List<AbstractTaskListMigrator> migrators) {
		this.delegatingExternalizer.initialize(migrators);
	}

	public void writeTaskList(ITransferList taskList, File outFile) throws CoreException {
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
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Saving Task List failed", //$NON-NLS-1$
					e));
		}
	}

	private Document createTaskListDocument(ITransferList taskList) throws CoreException {
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
		if (orphanDocument != null) {
			NodeList orphans = orphanDocument.getDocumentElement().getChildNodes();
			for (int i = 0; i < orphans.getLength(); i++) {
				Node node = orphans.item(i);
				Node tempNode = doc.importNode(node, true);
				if (tempNode != null) {
					root.appendChild(tempNode);
				}
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
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Failed write task list", //$NON-NLS-1$
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
			throw new CoreException(
					new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Failed to create document", e)); //$NON-NLS-1$
		}
	}

	public void readTaskList(ITransferList taskList, File inFile) throws CoreException {
		if (!inFile.exists()) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task list file not found \"" + inFile.getAbsolutePath() + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (inFile.length() == 0) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task list file contains no data \"" + inFile.getAbsolutePath() + "\"")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try (InputStream taskListFile = openTaskList(inFile)) {
			XMLReader reader = XmlReaderUtil.createXmlReader();
			SaxTaskListHandler handler = new SaxTaskListHandler(taskList, repositoryModel, repositoryManager);
			reader.setContentHandler(handler);
			reader.parse(new InputSource(taskListFile));
			this.orphanDocument = handler.getOrphans();
		} catch (SAXException | IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, e.getMessage(), e));
		}
	}

	/**
	 * Opens the specified XML file
	 *
	 * @throws CoreException
	 */
	private InputStream openTaskList(File inputFile) throws CoreException {
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
							"Task list file contains no entry for the task list")); //$NON-NLS-1$
				}
			} else {
				in = new FileInputStream(inputFile);
			}

			return in;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, ERROR_TASKLIST_READ, e));
		}
	}

}
