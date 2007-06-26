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

import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Adapted from MylarContextExternalizer
 * 
 * @author Rob Elves
 */
public class TaskRepositoriesExternalizer {

	private SaxRepositoriesWriter writer = new SaxRepositoriesWriter();

	public static final String ELEMENT_TASK_REPOSITORIES = "TaskRepositories";

	public static final String ELEMENT_TASK_REPOSITORY = "TaskRepository";

	public static final String ATTRIBUTE_VERSION = "OutputVersion";

	public void writeRepositoriesToXML(Collection<TaskRepository> repositories, File file) {
		ZipOutputStream outputStream = null;
		try {
			if (!file.exists())
				file.createNewFile();

			outputStream = new ZipOutputStream(new FileOutputStream(file));
			ZipEntry zipEntry = new ZipEntry(TaskRepositoryManager.OLD_REPOSITORIES_FILE);
			outputStream.putNextEntry(zipEntry);
			outputStream.setMethod(ZipOutputStream.DEFLATED);

			// OutputStream stream = new FileOutputStream(file);
			writer.setOutputStream(outputStream);
			writer.writeRepositoriesToStream(repositories);
			outputStream.flush();
			outputStream.closeEntry();
			outputStream.close();

		} catch (IOException e) {
			StatusHandler.fail(e, "Could not write: " + file.getAbsolutePath(), true);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					StatusHandler.fail(e, "Unable to terminate output stream to repositories file.", false);
				}
			}
		}
	}

	public Set<TaskRepository> readRepositoriesFromXML(File file) {

		if (!file.exists())
			return null;
		InputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(new FileInputStream(file));
			((ZipInputStream) inputStream).getNextEntry();
			SaxRepositoriesContentHandler contentHandler = new SaxRepositoriesContentHandler();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(inputStream));
			return contentHandler.getRepositories();
		} catch (Throwable e) {
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			StatusHandler.log(e, "Error while reading context file");
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					StatusHandler.fail(e, "Failed to close repositories input stream.", false);
				}
			}
		}
	}
}
