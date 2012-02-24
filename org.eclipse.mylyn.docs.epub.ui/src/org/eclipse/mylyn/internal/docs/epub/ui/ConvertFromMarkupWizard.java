/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
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
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.core.wikitext.MarkupToOPS;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.ui.statushandlers.StatusManager;

public class ConvertFromMarkupWizard extends Wizard {

	private static final String PLUGIN_ID = "org.eclipse.mylyn.docs.epub.ui";

	private EPUB2Bean bean;

	OPSPublication epub;

	private IFile epubFile;

	private File epubFolder;

	private IFile markupFile;

	private File markupFolder = null;

	private MarkupLanguage markupLanguage;

	private MainPage page;

	public ConvertFromMarkupWizard() {
		setWindowTitle("Generate EPUB");
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		epub = OPSPublication.getVersion2Instance();
		// XXX: Read back epub
		File workingFolder = null;
		// if (epubFile.exists()) {
		// try {
		// workingFolder = epub.unpack(epubFile.getLocation().toFile());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		bean = new EPUB2Bean(epub, markupFile.getLocation().toFile(), epubFile.getLocation().toFile(), workingFolder);
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
			for (int i = 0; i < children.length; i++) {
				deleteFolder(new File(folder, children[i]));
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
		final MultiStatus ms = new MultiStatus(PLUGIN_ID, 0, "Could not generate EPUB", null);
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					monitor.beginTask("Generate EPUB", 3);
					try {
						if (epubFile.exists()) {
							// Delete the old one
							epubFile.delete(true, monitor);
						}
						// Parse the wiki markup and populate the EPUB
						markupFolder = markupToEPUB.parse(epub, markupFile.getLocation().toFile());
						monitor.worked(1);
						List<Diagnostic> problems = epub.validateMetadata();

						if (problems.size() > 0) {
							for (Diagnostic diagnostic : problems) {
								ms.add(new Status(IStatus.ERROR, PLUGIN_ID, diagnostic.getMessage()));
							}
							monitor.setCanceled(true);
							StatusManager.getManager().handle(ms, StatusManager.BLOCK);
							return;
						}
						EPUB publication = new EPUB();
						publication.add(epub);
						epubFolder = publication.pack(epubFile.getLocation().toFile());
						monitor.worked(1);
						epubFile.refreshLocal(IResource.DEPTH_ONE, monitor);
						monitor.worked(1);
					} catch (Exception e) {
						ms.add(new Status(IStatus.ERROR, EPUBUIPlugin.PLUGIN_ID, "An exception occured", e));
						monitor.setCanceled(true);
						StatusManager.getManager().handle(ms);
						StatusManager.getManager().handle(ms, StatusManager.BLOCK);
						return;
					} finally {
						deleteFolder(epubFolder);
						deleteFolder(markupFolder);
						monitor.done();
					}
				}
			});
		} catch (Throwable e) {
			ms.add(new Status(IStatus.ERROR, EPUBUIPlugin.PLUGIN_ID, "Could not convert to EPUB", e));
			return false;
		}
		return ms.isOK();
	}

}
