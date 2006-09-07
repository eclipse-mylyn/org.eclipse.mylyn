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

package org.eclipse.mylar.tasks.core;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.context.core.util.ZipFileUtil;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;

/**
 * Encapsulates synchronization policy.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryConnector {

	public static final String MESSAGE_ATTACHMENTS_NOT_SUPPORTED = "Attachments not supported by connector: ";

	public static final String MYLAR_CONTEXT_DESCRIPTION = "mylar/context/zip";

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	private static final String ZIPFILE_EXTENSION = ".zip";

	protected List<String> supportedVersions;

	protected Set<RepositoryTemplate> templates = new LinkedHashSet<RepositoryTemplate>();

	/**
	 * @return null if not supported
	 */
	public abstract IAttachmentHandler getAttachmentHandler();

	/**
	 * @return null if not supported
	 */
	public abstract IOfflineTaskHandler getOfflineTaskHandler();

	public abstract boolean validate(TaskRepository repository);

	public abstract String getRepositoryUrlFromTaskUrl(String url);

	public abstract boolean canCreateTaskFromKey(TaskRepository repository);

	public abstract boolean canCreateNewTask(TaskRepository repository);

	/**
	 * @param id
	 *            identifier, e.g. "123" bug Bugzilla bug 123
	 * @return null if task could not be created
	 */
	public abstract ITask createTaskFromExistingKey(TaskRepository repository, String id);

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @param query
	 * @param monitor
	 * @param resultCollector
	 *            IQueryHitCollector that collects the hits found
	 */
	public abstract IStatus performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			IQueryHitCollector resultCollector);

	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getRepositoryType();

	/**
	 * Reset and update the repository attributes from the server (e.g.
	 * products, components)
	 * 
	 * @param monitor
	 */
	public abstract void updateAttributes(TaskRepository repository, IProgressMonitor monitor);

	public abstract List<String> getSupportedVersions();

	public abstract void updateTaskState(AbstractRepositoryTask repositoryTask);

	public String[] repositoryPropertyNames() {
		return new String[] { IRepositoryConstants.PROPERTY_VERSION, IRepositoryConstants.PROPERTY_TIMEZONE,
				IRepositoryConstants.PROPERTY_ENCODING };
	}

	/**
	 * Implementors of this repositoryOperations must perform it locally without
	 * going to the server since it is used for frequent repositoryOperations
	 * such as decoration.
	 * 
	 * @return an empty set if no contexts
	 */
	public final Set<RepositoryAttachment> getContextAttachments(TaskRepository repository, AbstractRepositoryTask task) {
		Set<RepositoryAttachment> contextAttachments = new HashSet<RepositoryAttachment>();
		if (task.getTaskData() != null) {
			for (RepositoryAttachment attachment : task.getTaskData().getAttachments()) {
				if (attachment.getDescription().equals(MYLAR_CONTEXT_DESCRIPTION)) {
					contextAttachments.add(attachment);
				}
			}
		}
		return contextAttachments;
	}

	// TODO: move
	public final boolean hasRepositoryContext(TaskRepository repository, AbstractRepositoryTask task) {
		if (repository == null || task == null) {
			return false;
		} else {
			Set<RepositoryAttachment> remoteContextAttachments = getContextAttachments(repository, task);
			return (remoteContextAttachments != null && remoteContextAttachments.size() > 0);
		}
	}

	/**
	 * Attaches the associated context to <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	public final boolean attachContext(TaskRepository repository, AbstractRepositoryTask task, String longComment,
			Proxy proxySettings) throws CoreException {
		ContextCorePlugin.getContextManager().saveContext(task.getHandleIdentifier());
		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());

		if (sourceContextFile != null && sourceContextFile.exists()) {
			IAttachmentHandler handler = getAttachmentHandler();
			if (handler == null) {
				return false;
			}

			// compress context file
			List<File> filesToZip = new ArrayList<File>();
			filesToZip.add(sourceContextFile);

			File destinationFile;
			try {
				destinationFile = File.createTempFile(sourceContextFile.getName(), ZIPFILE_EXTENSION);
				destinationFile.deleteOnExit();
				ZipFileUtil.createZipFile(destinationFile, filesToZip, new NullProgressMonitor());
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, 0,
						"Error compressing context file", e));
			}

			try {
				// TODO: 'faking' outgoing state
				task.setSyncState(RepositoryTaskSyncState.OUTGOING);
				handler.uploadAttachment(repository, task, longComment, MYLAR_CONTEXT_DESCRIPTION, destinationFile,
						APPLICATION_OCTET_STREAM, false, proxySettings);
			} catch (CoreException e) {
				task.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
				throw e;
			}
			task.setTaskData(null);			
		}
		return true;
	}

	/**
	 * Retrieves a context stored in <code>attachment</code> from
	 * <code>task</code>.
	 * 
	 * @return false, if operation is not supported by repository
	 */
	public final boolean retrieveContext(TaskRepository repository, AbstractRepositoryTask task,
			RepositoryAttachment attachment, Proxy proxySettings, String destinationPath) throws CoreException {
		IAttachmentHandler attachmentHandler = getAttachmentHandler();
		if (attachmentHandler == null) {
			return false;
		}

		File destinationContextFile = ContextCorePlugin.getContextManager().getFileForContext(
				task.getHandleIdentifier());
		// TODO what if destinationContextFile == null?
		File destinationZipFile = new File(destinationContextFile.getPath() + ZIPFILE_EXTENSION);

		attachmentHandler.downloadAttachment(repository, task, attachment, destinationZipFile, proxySettings);
		// if (destinationContextFile.exists()) {
		try {
			ZipFileUtil.unzipFiles(destinationZipFile, destinationPath);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ContextCorePlugin.PLUGIN_ID, 0,
					"Error extracting context file", e));
		}

		return true;
	}

	public void addTemplate(RepositoryTemplate template) {
		this.templates.add(template);
	}

	public Set<RepositoryTemplate> getTemplates() {
		return templates;
	}

	/** returns null if template not found */
	public RepositoryTemplate getTemplate(String label) {
		for (RepositoryTemplate template : getTemplates()) {
			if (template.label.equals(label)) {
				return template;
			}
		}
		return null;
	}

}
