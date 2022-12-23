/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - improvements
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.XmlReaderUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class TaskListExternalizer {

	private static final String ERROR_TASKLIST_READ = "Failed to load Task List"; //$NON-NLS-1$

	private final RepositoryModel repositoryModel;

	private final IRepositoryManager repositoryManager;

	private Document orphanDocument;

	public TaskListExternalizer(RepositoryModel repositoryModel, IRepositoryManager repositoryManager) {
		this.repositoryModel = repositoryModel;
		this.repositoryManager = repositoryManager;
	}

	public void writeTaskList(ITransferList taskList, File outFile) throws CoreException {
		try (FileOutputStream outStream = new FileOutputStream(outFile)) {
			try (ZipOutputStream zipOutStream = new ZipOutputStream(outStream)) {
				ZipEntry zipEntry = new ZipEntry(ITasksCoreConstants.OLD_TASK_LIST_FILE);
				zipOutStream.putNextEntry(zipEntry);
				zipOutStream.setMethod(ZipOutputStream.DEFLATED);

				SaxTaskListWriter writer = new SaxTaskListWriter();
				writer.setOutputStream(zipOutStream);
				writer.writeTaskListToStream(taskList, orphanDocument);

				zipOutStream.flush();
				zipOutStream.closeEntry();
				zipOutStream.finish();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Saving Task List failed", //$NON-NLS-1$
					e));
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
				in = new ZipInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
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
