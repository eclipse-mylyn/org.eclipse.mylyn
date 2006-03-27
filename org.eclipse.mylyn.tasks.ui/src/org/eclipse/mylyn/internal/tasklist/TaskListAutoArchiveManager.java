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

package org.eclipse.mylar.internal.tasklist;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.util.TaskDataExportJob;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Rob Elves
 */
public class TaskListAutoArchiveManager implements IPropertyChangeListener {

	public static final String ARCHIVE_FAILURE_MESSAGE = "Could not archive task data. Check auto archive preferences.\n";

	private static final String FILENAME_PREFIX = "MylarTaskListArchive";

	private final static String ZIP_FILE_EXTENSION = ".zip";

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long HOUR = 60 * MINUTE;

	private static final long DAY = 24 * HOUR;

	private Timer timer;

	public TaskListAutoArchiveManager() {
		boolean enabled = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
				TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY);

		if (enabled) {
			start();
		}
	}

	public void start() {
		timer = new Timer();
		timer.schedule(new CheckArchiveRequired(), MINUTE, HOUR);
	}

	public void stop() {
		timer.cancel();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TaskListPreferenceConstants.ARCHIVE_AUTOMATICALLY)) {
			if ((Boolean) event.getNewValue() == true) {
				start();
			} else {
				stop();
			}
		}
	}

	/**
	 * @throws InvocationTargetException
	 */
	static public void archiveNow() throws InvocationTargetException {
		String destination = MylarTaskListPlugin.getMylarCorePrefs().getString(
				TaskListPreferenceConstants.ARCHIVE_FOLDER);

		String formatString = "yyyy-MM-dd";
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.ENGLISH);
		String date = format.format(new Date());
		String fileName = FILENAME_PREFIX + "-" + date + ZIP_FILE_EXTENSION;

		final TaskDataExportJob archiveJob = new TaskDataExportJob(destination, true, fileName);
			   
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
//			service.runInUI(service, archiveJob, null);
			service.run(true, false, archiveJob);
		} catch (InterruptedException e) {
			// ignore
		}

		MylarTaskListPlugin.getMylarCorePrefs()
				.setValue(TaskListPreferenceConstants.ARCHIVE_LAST, new Date().getTime());
	}
	
	class CheckArchiveRequired extends TimerTask {

		@Override
		public void run() {
			long lastArchive = MylarTaskListPlugin.getMylarCorePrefs()
					.getLong(TaskListPreferenceConstants.ARCHIVE_LAST);
			int days = MylarTaskListPlugin.getMylarCorePrefs().getInt(TaskListPreferenceConstants.ARCHIVE_SCHEDULE);
			long waitPeriod = days * DAY;
			final long now = new Date().getTime();

			if ((now - lastArchive) > waitPeriod) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							// MessageDialog.openQuestion(shell, title,
							// message);
							archiveNow();
						} catch (InvocationTargetException e) {
							MylarStatusHandler.fail(e, ARCHIVE_FAILURE_MESSAGE + e.getCause().getMessage(), true);
							return;
						}
					}
				});
			}
		}
	}

}
