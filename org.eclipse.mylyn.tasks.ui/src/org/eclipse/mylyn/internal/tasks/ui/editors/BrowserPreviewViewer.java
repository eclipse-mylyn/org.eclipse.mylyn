/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Xiaoyang Guan - browser preview
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRenderingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Steffen Pingel
 * @author Xiaoyang Guan
 */
public class BrowserPreviewViewer {

	private Browser browser;

	private boolean ignoreLocationEvents;

	private final AbstractRenderingEngine renderingEngine;

	private final TaskRepository taskRepository;

	public BrowserPreviewViewer(TaskRepository taskRepository, AbstractRenderingEngine renderingEngine) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(renderingEngine);
		this.taskRepository = taskRepository;
		this.renderingEngine = renderingEngine;
	}

	public void createControl(Composite parent, FormToolkit toolkit) {
		browser = new Browser(parent, SWT.NONE);
		// intercept links to open tasks in rich editor and urls in separate browser
		browser.addLocationListener(new LocationAdapter() {
			@Override
			public void changing(LocationEvent event) {
				// ignore events that are caused by manually setting the contents of the browser
				if (ignoreLocationEvents) {
					return;
				}

				if (event.location != null && !event.location.startsWith("about")) { //$NON-NLS-1$
					event.doit = false;
					IHyperlink link = new TaskUrlHyperlink(
							new Region(0, 0)/* a fake region just to make constructor happy */, event.location);
					link.open();
				}
			}

		});
	}

	public Browser getControl() {
		return browser;
	}

	private void previewWiki(final Browser browser, String sourceText) {
		final class PreviewWikiJob extends Job {
			private String htmlText;

			private IStatus jobStatus;

			private final String sourceText;

			public PreviewWikiJob(String sourceText) {
				super(Messages.BrowserPreviewViewer_Formatting_Wiki_Text);

				if (sourceText == null) {
					throw new IllegalArgumentException("source text must not be null"); //$NON-NLS-1$
				}

				this.sourceText = sourceText;
			}

			public String getHtmlText() {
				return htmlText;
			}

			public IStatus getStatus() {
				return jobStatus;
			}

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (renderingEngine == null) {
					jobStatus = new RepositoryStatus(taskRepository, IStatus.INFO, TasksUiPlugin.ID_PLUGIN,
							RepositoryStatus.ERROR_INTERNAL,
							Messages.BrowserPreviewViewer_The_repository_does_not_support_HTML_preview);
					return Status.OK_STATUS;
				}

				jobStatus = Status.OK_STATUS;
				try {
					htmlText = renderingEngine.renderAsHtml(taskRepository, sourceText, monitor);
				} catch (CoreException e) {
					jobStatus = e.getStatus();
				}
				return Status.OK_STATUS;
			}

		}

		final PreviewWikiJob job = new PreviewWikiJob(sourceText);

		job.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(final IJobChangeEvent event) {
				if (!browser.isDisposed()) {
					if (job.getStatus().isOK()) {
						browser.getDisplay().asyncExec(new Runnable() {
							public void run() {
								setText(browser, job.getHtmlText());
								// TODO 3.2 error handling
								//getAttributeEditorManager().setMessage(null, IMessageProvider.NONE);
							}
						});
					} else {
						browser.getDisplay().asyncExec(new Runnable() {
							public void run() {
								TasksUiInternal.displayStatus(Messages.BrowserPreviewViewer_Error, job.getStatus());
								// TODO 3.2 error handling
								//getAttributeEditorManager().setMessage(job.getStatus().getMessage(), IMessageProvider.ERROR);
							}
						});
					}
				}
				super.done(event);
			}
		});

		job.setUser(true);
		job.schedule();
	}

	private void setText(Browser browser, String html) {
		try {
			ignoreLocationEvents = true;
			browser.setText((html != null) ? html : ""); //$NON-NLS-1$
		} finally {
			ignoreLocationEvents = false;
		}

	}

	public void update(String value) {
		setText(browser, Messages.BrowserPreviewViewer_Loading_preview_);
		previewWiki(browser, value);
	}

}
