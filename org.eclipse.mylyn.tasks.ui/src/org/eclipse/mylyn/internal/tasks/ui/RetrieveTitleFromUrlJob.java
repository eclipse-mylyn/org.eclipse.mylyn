/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Waits for the title from the browser
 * 
 * @author Wesley Coelho
 * @author Mik Kersten
 */
public abstract class RetrieveTitleFromUrlJob extends Job implements TitleListener {

	public static final String LABEL_TITLE = "Retrieving summary from URL";

	private final static long MAX_WAIT_TIME_MILLIS = 1000 * 10; // (10 Seconds)

	private final static long SLEEP_INTERVAL_MILLIS = 500;

	private String url = null;

	private String pageTitle = null;

	private boolean retrievalFailed = false;

	private long timeWaitedMillis = 0;

	boolean ignoreChangeCall = false;

	private boolean titleRetrieved = false;

	private Shell shell = null;

	private Browser browser = null;

	public RetrieveTitleFromUrlJob(String url) {
		super(LABEL_TITLE);
		this.url = url;
	}

	protected abstract void setTitle(String pageTitle);

	@Override
	public IStatus run(IProgressMonitor monitor) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				shell = new Shell(Display.getDefault());
				shell.setVisible(false);
				browser = new Browser(shell, SWT.NONE);
				browser.addTitleListener(RetrieveTitleFromUrlJob.this);
				browser.setUrl(url);
			}
		});

		while (pageTitle == null && !retrievalFailed && (timeWaitedMillis <= MAX_WAIT_TIME_MILLIS)) {
			try {
				Thread.sleep(SLEEP_INTERVAL_MILLIS);
			} catch (InterruptedException e) {
				StatusHandler.fail(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN,
						"Unexpected thread interruption", e));
			}
			timeWaitedMillis += SLEEP_INTERVAL_MILLIS;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (pageTitle == null) {
					pageTitle = url;
					titleRetrieved = false;
				} else {
					titleRetrieved = true;
				}
				setTitle(pageTitle);

				if (shell != null && !shell.isDisposed()) {
					shell.dispose();
				}

				if (browser != null && !browser.isDisposed()) {
					browser.dispose();
				}
			}
		});
		return Status.OK_STATUS;
	}

	public void changed(TitleEvent event) {
		if (!ignoreChangeCall) {
			if (event.title.equals(url)) {
				return;
			} else {
				ignoreChangeCall = true;
				if (event.title.equals(url + "/") || event.title.equals("Object not found!")
						|| event.title.equals("No page to display") || event.title.equals("Cannot find server")
						|| event.title.equals("Invalid Bug ID")) {
					retrievalFailed = true;
				} else {
					pageTitle = event.title;
				}
			}
		}
	}

	public boolean isTitleRetrieved() {
		return titleRetrieved;
	}

	public String getPageTitle() {
		return pageTitle;
	}
}
