/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Jeff Pound
 */
public class RepositoryAttachmentEditorInput extends PlatformObject implements IStorageEditorInput {

	private RepositoryAttachment attachment;

	private RepositoryAttachmentStorage storage;

	private TaskRepository repository;

	public RepositoryAttachmentEditorInput(TaskRepository repository, RepositoryAttachment att) {
		this.attachment = att;
		this.storage = new RepositoryAttachmentStorage();
		this.repository = repository;
	}

	public IStorage getStorage() throws CoreException {
		return storage;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		// ignore
		return null;
	}

	public String getName() {
		return storage.getName();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Repository Attachment: " + attachment.getId() + " [" + attachment.getUrl() + "]";
	}

	class RepositoryAttachmentStorage extends PlatformObject implements IStorage {

		private static final String ATTR_FILENAME = "filename";

		private static final String ATTACHMENT_DEFAULT_NAME = "attachment";

		private static final String CTYPE_ZIP = "zip";

		private static final String CTYPE_OCTET_STREAM = "octet-stream";

		private static final String CTYPE_TEXT = "text";

		private static final String CTYPE_HTML = "html";

		public InputStream getContents() throws CoreException {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			AbstractAttachmentHandler handler = connector.getAttachmentHandler();
			return handler.getAttachmentAsStream(repository, attachment, new NullProgressMonitor());
		}

		public IPath getFullPath() {
			// ignore
			return null;
		}

		public String getName() {
			String name = attachment.getAttributeValue(ATTR_FILENAME);

			// if no filename is set, make one up with the proper extension so
			// we can support opening in that filetype's default editor
			if (name == null || "".equals(name)) {
				String ctype = attachment.getContentType();
				if (ctype.endsWith(CTYPE_HTML)) {
					name = ATTACHMENT_DEFAULT_NAME + ".html";
				} else if (ctype.startsWith(CTYPE_TEXT)) {
					name = ATTACHMENT_DEFAULT_NAME + ".txt";
				} else if (ctype.endsWith(CTYPE_OCTET_STREAM)) {
					name = ATTACHMENT_DEFAULT_NAME;
				} else if (ctype.endsWith(CTYPE_ZIP)) {
					name = ATTACHMENT_DEFAULT_NAME + "." + CTYPE_ZIP;
				} else {
					name = ATTACHMENT_DEFAULT_NAME + "." + ctype.substring(ctype.indexOf("/") + 1);
				}
			}
			// treat .patch files as text files
			if (name.endsWith(".patch")) {
				name += ".txt";
			}

			return name;
		}

		public boolean isReadOnly() {
			return true;
		}

	}
}
