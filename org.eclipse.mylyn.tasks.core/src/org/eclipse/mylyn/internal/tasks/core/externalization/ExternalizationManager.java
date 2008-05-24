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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext.KIND;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class ExternalizationManager {

	private ExternalizationJob saveJob;

	private IStatus loadStatus;

	private String rootFolderPath;

	private static boolean saveDisabled = false;

	private final List<IExternalizationParticipant> externalizationParticipants = new ArrayList<IExternalizationParticipant>();

	private boolean forceSave = false;

	public ExternalizationManager(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}

	private ExternalizationJob createJob(String jobName, IExternalizationContext context) {
		//create save job
		ExternalizationJob job = new ExternalizationJob(jobName, context);
		job.setUser(false);
		job.setSystem(true);
		return job;
	}

	public void addParticipant(IExternalizationParticipant participant) {
		externalizationParticipants.add(participant);
	}

	public void reLoad() {
		reset();
		for (IExternalizationParticipant participant : externalizationParticipants) {
			load(participant);
		}
	}

	private void load(IExternalizationParticipant participant) {
		try {
			saveDisabled = true;
			IExternalizationContext loadContext = new LoadContext(rootFolderPath, participant);
			ExternalizationJob job = createJob("Loading participant " + participant.getDescription(), loadContext);
			job.setContext(loadContext);
			// TODO: run async
			job.run(new NullProgressMonitor());
			//reschedule(job, loadContext);
		} finally {
			saveDisabled = false;
		}
	}

	public void setRootFolderPath(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}

	public synchronized void requestSave() {

		ExternalizationContext saveContext = new ExternalizationContext(KIND.SAVE, rootFolderPath);

		if (saveJob == null) {
			saveJob = createJob("Saving participants", saveContext);
		}

		reschedule(saveJob, saveContext);
	}

	public void stop() {
//		requestSave();
//
//		if (saveJob != null) {
//			try {
//				saveJob.join();
//				saveJob = null;
//			} catch (InterruptedException e) {
//				StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
//						"Task List save on shutdown canceled."));
//			}
//		}
	}

	public synchronized void saveNow(IProgressMonitor monitor) {
		monitor = Policy.monitorFor(monitor);
		if (saveJob != null) {
			saveJob.cancel();
			saveJob = null;
		}
		try {
			forceSave = true;
			ExternalizationJob job = createJob("Save Now", new ExternalizationContext(KIND.SAVE, rootFolderPath));
			job.run(monitor);
		} finally {
			forceSave = false;
		}
	}

	private void reschedule(ExternalizationJob job, IExternalizationContext context) {
		if (!saveDisabled) {
			if (!CoreUtil.TEST_MODE) {
				job.setContext(context);
				job.schedule(3000);
			} else {
				job.run(new NullProgressMonitor());
			}
		}
	}

	protected void setStatus(MultiStatus status) {
		this.loadStatus = status;
	}

	public IStatus getLoadStatus() {
		return loadStatus;
	}

	private void reset() {
		saveDisabled = false;
		loadStatus = null;
		if (saveJob != null) {
			saveJob.cancel();
			saveJob = null;
		}
	}

	class ExternalizationJob extends Job {

		IExternalizationContext context;

		public ExternalizationJob(String jobTitle, IExternalizationContext context) {
			super(jobTitle);
			this.context = context;
		}

		public void setContext(IExternalizationContext saveContext) {
			this.context = saveContext;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
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
			case SNAPSHOT:

				break;
			case LOAD:
				if (context instanceof LoadContext) {
					LoadContext loadContext = ((LoadContext) context);
					IExternalizationParticipant participant = loadContext.getParticipant();
					ISchedulingRule rule = participant.getSchedulingRule();
					try {
						Job.getJobManager().beginRule(rule, monitor);
						try {
							participant.execute(context, monitor);
						} catch (CoreException e) {
							if (loadStatus == null) {
								loadStatus = e.getStatus();
							} else {
								IStatus[] stati = { loadStatus, e.getStatus() };
								loadStatus = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.ERROR, stati,
										"Externalization Failure", null);
							}
							saveDisabled = true;
						}
					} finally {
						Job.getJobManager().endRule(rule);
					}
				}

				break;
			}
			return Status.OK_STATUS;
		}
	}

	class LoadContext extends ExternalizationContext {

		private final IExternalizationParticipant participant;

		public LoadContext(String rootPath, IExternalizationParticipant participant) {
			super(IExternalizationContext.KIND.LOAD, rootPath);
			this.participant = participant;
		}

		public IExternalizationParticipant getParticipant() {
			return participant;
		}

	}

	class ExternalizationContext implements IExternalizationContext {

		private final KIND kind;

		private final String rootPath;

		public ExternalizationContext(IExternalizationContext.KIND kind, String rootPath) {
			this.kind = kind;
			this.rootPath = rootPath;
		}

		public KIND getKind() {
			return kind;
		}

		public String getRootPath() {
			return rootPath;
		}
	}

}
