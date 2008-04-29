/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Rob Elves
 * @author Jevgeni Holodkov
 */
public class TaskRepositoriesExternalizer {

	private final SaxRepositoriesWriter writer = new SaxRepositoriesWriter();

	public static final String ELEMENT_TASK_REPOSITORIES = "TaskRepositories";

	public static final String ELEMENT_TASK_REPOSITORY = "TaskRepository";

	public static final String ATTRIBUTE_VERSION = "OutputVersion";

	public void writeRepositoriesToXML(Collection<TaskRepository> repositories, File file) {
		ZipOutputStream outputStream = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			outputStream = new ZipOutputStream(new FileOutputStream(file));
			writeRepositories(repositories, outputStream);
			outputStream.close();

		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not write: "
					+ file.getAbsolutePath(), e));
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Could not close: "
							+ file.getAbsolutePath(), e));
				}
			}
		}
	}

	/**
	 * @param repositories
	 * @param outputStream
	 * @throws IOException
	 */
	public void writeRepositories(Collection<TaskRepository> repositories, ZipOutputStream outputStream)
			throws IOException {
		ZipEntry zipEntry = new ZipEntry(TaskRepositoryManager.OLD_REPOSITORIES_FILE);
		outputStream.putNextEntry(zipEntry);
		outputStream.setMethod(ZipOutputStream.DEFLATED);

		// OutputStream stream = new FileOutputStream(file);
		writer.setOutputStream(outputStream);
		writer.writeRepositoriesToStream(repositories);
		outputStream.flush();
		outputStream.closeEntry();
	}

	public Set<TaskRepository> readRepositoriesFromXML(File file) {

		if (!file.exists()) {
			return null;
		}
		InputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(new FileInputStream(file));

			// search for REPOSITORIES entry
			ZipEntry entry = ((ZipInputStream) inputStream).getNextEntry();
			while (entry != null) {
				if (TaskRepositoryManager.OLD_REPOSITORIES_FILE.equals(entry.getName())) {
					break;
				}
				entry = ((ZipInputStream) inputStream).getNextEntry();
			}

			if (entry == null) {
				return null;
			}

			SaxRepositoriesContentHandler contentHandler = new SaxRepositoriesContentHandler();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(inputStream));
			return contentHandler.getRepositories();
		} catch (Throwable e) {
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading context file", e));
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Error closing context file", e));
				}
			}
		}
	}
}
