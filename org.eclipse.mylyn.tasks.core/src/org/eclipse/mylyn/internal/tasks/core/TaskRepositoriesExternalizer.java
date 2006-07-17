/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.TaskRepository;
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
		if (repositories.isEmpty())
			return;
		try {
			if (!file.exists())
				file.createNewFile();
			OutputStream stream = new FileOutputStream(file);
			writer.setOutputStream(stream);
			writer.writeRepositoriesToStream(repositories);
			stream.close();
		} catch (IOException e) {
			MylarStatusHandler.fail(e, "Could not write: " + file.getAbsolutePath(), true);
		}
	}

	public Set<TaskRepository> readRepositoriesFromXML(File file) {

		if (!file.exists())
			return null;
		try {
			SaxRepositoriesContentHandler contentHandler = new SaxRepositoriesContentHandler();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			reader.parse(new InputSource(new FileInputStream(file)));
			return contentHandler.getRepositories();
		} catch (Exception e) {
			file.renameTo(new File(file.getAbsolutePath() + "-save"));
			return null;
		}
	}
}
