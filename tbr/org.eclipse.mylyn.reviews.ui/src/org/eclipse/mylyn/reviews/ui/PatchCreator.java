/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Kilian Matt
 */
public class PatchCreator implements IPatchCreator {

	private static final Logger log = Logger.getAnonymousLogger();
	private TaskAttribute attribute;

	public PatchCreator(TaskAttribute attribute) {
		this.attribute = attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.ui.IPatchCreator#create()
	 */
	public Patch create() throws CoreException {
		try {
			ITaskAttachment taskAttachment = getTaskAttachment();
			URL url = new URL(attribute.getMappedAttribute(
					TaskAttribute.ATTACHMENT_URL).getValue());
			Patch patch = ReviewFactory.eINSTANCE.createPatch();
			patch.setAuthor(taskAttachment.getAuthor().getName());
			patch.setCreationDate(taskAttachment.getCreationDate());
			patch.setContents(readContents(url));
			patch.setFileName(taskAttachment.getFileName());
			return patch;
		} catch (Exception e) {
			e.printStackTrace();
			log.warning(e.toString());
			throw new CoreException(new Status(IStatus.ERROR,
					ReviewsUiPlugin.PLUGIN_ID,
					Messages.PatchCreator_ReaderCreationFailed, e));
		}
	}

	private ITaskAttachment taskAttachment;

	private ITaskAttachment getTaskAttachment() {
		if (taskAttachment == null) {
			// TODO move RepositoryModel.createTaskAttachment to interface?
			taskAttachment = ((RepositoryModel) TasksUi.getRepositoryModel())
					.createTaskAttachment(attribute);
			// new TaskAttachment(repository, task, attribute);
			// attributeMapper.updateTaskAttachment(taskAttachment, attribute);
		}
		return taskAttachment;
	}

	private String readContents(URL url) throws IOException {
		InputStream stream = null;
		try {
			stream = url.openStream();
			InputStreamReader reader = new InputStreamReader(stream);
			char[] buffer = new char[256];
			int readChars = 0;
			StringBuilder sb = new StringBuilder();
			while ((readChars = reader.read(buffer)) > 0) {
				sb.append(buffer, 0, readChars);
			}
			return sb.toString();
		} finally {
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	@Override
	public String toString() {
		return attribute.getMappedAttribute(TaskAttribute.ATTACHMENT_FILENAME)
				.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.ui.IPatchCreator#getFileName()
	 */
	public String getFileName() {
		return getTaskAttachment().getFileName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.ui.IPatchCreator#getAuthor()
	 */
	public String getAuthor() {
		return getTaskAttachment().getAuthor().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.mylyn.reviews.ui.IPatchCreator#getCreationDate()
	 */
	public Date getCreationDate() {
		return getTaskAttachment().getCreationDate();
	}
}
