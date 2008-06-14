/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext.Kind;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class ExternalizationManager {

	private static final int LAZY_SAVE_DELAY = 1000 * 60;

	private static final int SAVE_DELAY = 3000;

	private final ExternalizationJob saveJob;

	private IStatus loadStatus;

	private String rootFolderPath;

	private static volatile boolean saveDisabled = false;

	private final List<IExternalizationParticipant> externalizationParticipants;

	private boolean forceSave = false;

	public ExternalizationManager(String rootFolderPath) {
		Assert.isNotNull(rootFolderPath);
		this.externalizationParticipants = new CopyOnWriteArrayList<IExternalizationParticipant>();
		this.forceSave = false;
		this.saveJob = createJob();
		setRootFolderPath(rootFolderPath);
	}

	private ExternalizationJob createJob() {
		ExternalizationJob job = new ExternalizationJob("Task List Save Job");
		job.setUser(false);
		job.setSystem(true);
		return job;
	}

	public void addParticipant(IExternalizationParticipant participant) {
		Assert.isNotNull(participant);
		externalizationParticipants.add(participant);
	}

	public IStatus load() {
		try {
			saveDisabled = true;
			loadStatus = null;

			List<IStatus> statusList = new ArrayList<IStatus>();
			IProgressMonitor monitor = Policy.monitorFor(null);
			for (IExternalizationParticipant participant : externalizationParticipants) {
				IStatus status = load(participant, monitor);
				if (status != null) {
					statusList.add(status);
				}
			}

			if (statusList.size() > 0) {
				loadStatus = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.ERROR,
						statusList.toArray(new IStatus[0]), "Failed to load Task List", null);
			}
			return loadStatus;
		} finally {
			saveDisabled = false;
		}
	}

	private IStatus load(final IExternalizationParticipant participant, final IProgressMonitor monitor) {
		final IStatus[] result = new IStatus[1];
		final ExternalizationContext context = new ExternalizationContext(Kind.LOAD, rootFolderPath);
		ISchedulingRule rule = participant.getSchedulingRule();
		try {
			Job.getJobManager().beginRule(rule, monitor);
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					if (e instanceof CoreException) {
						result[0] = ((CoreException) e).getStatus();
					} else {
						result[0] = new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Load participant failed",
								e);
					}
				}

				public void run() throws Exception {
					participant.execute(context, monitor);
				}
			});
		} finally {
			Job.getJobManager().endRule(rule);
		}
		return result[0];
	}

	public void setRootFolderPath(String rootFolderPath) {
		Assert.isNotNull(rootFolderPath);
		this.rootFolderPath = rootFolderPath;
		saveJob.setContext(new ExternalizationContext(Kind.SAVE, rootFolderPath));
	}

	public void requestSave() {
		if (!saveDisabled) {
			if (!CoreUtil.TEST_MODE) {
				saveJob.schedule(SAVE_DELAY);
			} else {
				saveJob.run(new NullProgressMonitor());
			}
		}
	}

	public void stop() {
		try {
			saveDisabled = true;

			// run save job as early as possible
			saveJob.wakeUp();
			saveJob.join();
		} catch (InterruptedException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Task List save on shutdown canceled.", e));
		}
	}

	public void requestSaveAndWait(boolean force) throws InterruptedException {
		try {
			forceSave = force;
			saveJob.schedule();
			saveJob.join();
		} finally {
			forceSave = false;
		}
	}

	public IStatus getLoadStatus() {
		return loadStatus;
	}

	private class ExternalizationJob extends Job {

		private volatile IExternalizationContext context;

		public ExternalizationJob(String jobTitle) {
			super(jobTitle);
		}

		public IExternalizationContext getContext() {
			return context;
		}

		public void setContext(IExternalizationContext saveContext) {
			this.context = saveContext;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IExternalizationContext context = this.context;
			switch (context.getKind()) {
			case SAVE:
				try {
					monitor.beginTask("Saving...", externalizationParticipants.size());
					for (IExternalizationParticipant participant : externalizationParticipants) {
						ISchedulingRule rule = participant.getSchedulingRule();
						if (forceSave || participant.isDirty()) {
							try {
								Job.getJobManager().beginRule(rule, monitor);
								monitor.setTaskName("Saving " + participant.getDescription());
								participant.execute(context, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
							} catch (CoreException e) {
								StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
										"Save failed for " + participant.getDescription(), e));
							} finally {
								Job.getJobManager().endRule(rule);
							}
						}
						monitor.worked(1);
					}
				} finally {
					monitor.done();
				}
				break;
			default:
				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Unsupported externalization kind: " + context.getKind()));
			}
			return Status.OK_STATUS;
		}
	}

	private class ExternalizationContext implements IExternalizationContext {

		private final Kind kind;

		private final String rootPath;

		public ExternalizationContext(IExternalizationContext.Kind kind, String rootPath) {
			this.kind = kind;
			this.rootPath = rootPath;
		}

		public Kind getKind() {
			return kind;
		}

		public String getRootPath() {
			return rootPath;
		}
	}

}
