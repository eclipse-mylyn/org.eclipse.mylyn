/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.ui;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.core.PublicationProxy;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage;
import org.eclipse.mylyn.docs.epub.core.wikitext.MarkupToOPS;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.ui.statushandlers.StatusManager;

public class ConvertFromMarkupWizard extends Wizard {

	private PublicationProxy bean;

	Publication oebps;

	private IFile epubFile;

	private File epubFolder;

	private IFile markupFile;

	private File markupFolder = null;

	private MarkupLanguage markupLanguage;

	private MainPage page;

	public ConvertFromMarkupWizard() {
		setWindowTitle(Messages.ConvertFromMarkupWizard_0);
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		oebps = Publication.getVersion2Instance();
		bean = new PublicationProxy(oebps, markupFile.getLocation().toFile());
		page = new MainPage(bean);
		addPage(page);
	}

	/**
	 * Delete the folder recursively.
	 *
	 * @param folder
	 *            the folder to delete
	 * @return <code>true</code> if the folder was deleted
	 */
	private void deleteFolder(File folder) {
		if (folder == null) {
			return;
		}
		if (folder.isDirectory() && folder.exists()) {
			String[] children = folder.list();
			for (String element : children) {
				deleteFolder(new File(folder, element));
			}
		}
		if (folder.exists()) {
			folder.delete();
		}
	}

	public void init(IFile markupFile, IFile epubFile, MarkupLanguage markupLanguage) {
		this.markupFile = markupFile;
		this.epubFile = epubFile;
		this.markupLanguage = markupLanguage;
	}

	@Override
	public boolean performFinish() {
		final MarkupToOPS markupToEPUB = new MarkupToOPS();
		markupToEPUB.setMarkupLanguage(markupLanguage);
		final MultiStatus ms = new MultiStatus(EPUBUIPlugin.PLUGIN_ID, 0, Messages.ConvertFromMarkupWizard_1, null);
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask(Messages.ConvertFromMarkupWizard_2, 3);
					try {
						if (epubFile.exists()) {
							// Delete the old one
							epubFile.delete(true, monitor);
						}
						// Parse the wiki markup and populate the EPUB
						markupFolder = markupToEPUB.parse(oebps, markupFile.getLocation().toFile());
						monitor.worked(1);
						EPUB publication = new EPUB();
						publication.add(oebps);
						epubFolder = publication.pack(epubFile.getLocation().toFile());
						monitor.worked(1);
						epubFile.refreshLocal(IResource.DEPTH_ONE, monitor);
						monitor.worked(1);

						List<ValidationMessage> messages = oebps.getMessages();
						for (ValidationMessage validationMessage : messages) {
							ms.add(new Status(IStatus.WARNING, EPUBUIPlugin.PLUGIN_ID, validationMessage.getMessage()));
						}
					} catch (Exception e) {
						e.printStackTrace();
						ms.add(new Status(IStatus.ERROR, EPUBUIPlugin.PLUGIN_ID, Messages.ConvertFromMarkupWizard_3,
								e));
					} finally {
						deleteFolder(epubFolder);
						deleteFolder(markupFolder);
						monitor.done();
					}
				}
			});
		} catch (Throwable e) {
			ms.add(new Status(IStatus.ERROR, EPUBUIPlugin.PLUGIN_ID, Messages.ConvertFromMarkupWizard_4, e));
			return false;
		}
		if (!ms.isOK()) {
			StatusManager.getManager().handle(ms, StatusManager.BLOCK);
		}
		return ms.isOK();
	}

}
