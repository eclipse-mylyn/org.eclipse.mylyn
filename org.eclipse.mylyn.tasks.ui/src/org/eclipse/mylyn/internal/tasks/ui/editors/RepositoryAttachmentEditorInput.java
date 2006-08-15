/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.editors;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * @author Jeff Pound
 */
public class RepositoryAttachmentEditorInput extends PlatformObject implements IStorageEditorInput {

	private RepositoryAttachment attachment;
	private RepositoryAttachmentStorage storage;

	public RepositoryAttachmentEditorInput(RepositoryAttachment att) {
		this.attachment = att;
		this.storage = new RepositoryAttachmentStorage();
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
			URLConnection urlConnect;
			InputStream stream = null;
			try {
				urlConnect = (new URL(attachment.getUrl())).openConnection();
				urlConnect.connect();
				stream = urlConnect.getInputStream();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return stream;
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
