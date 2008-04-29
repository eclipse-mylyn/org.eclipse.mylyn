/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext.KIND;

/**
 * @author Rob Elves
 */
public class ExternalizationManager {

	private ExternalizationJob saveJob;

	private IStatus loadStatus;

	private String rootFolderPath;

	private static boolean saveDisabled = false;

	private final Set<IExternalizationParticipant> externalizationParticipants = new HashSet<IExternalizationParticipant>();

	public ExternalizationManager(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}

	private ExternalizationJob createJob(String jobName, IExternalizationContext context) {
		//create save job
		ExternalizationJob job = new ExternalizationJob(jobName, context);
		job.setUser(false);
		return job;
	}

	public void addParticipant(IExternalizationParticipant participant) {
		externalizationParticipants.add(participant);
	}

	public void load(IExternalizationParticipant participant) {
		IExternalizationContext loadContext = new LoadContext(rootFolderPath, participant);
		ExternalizationJob job = createJob("Loading participant " + participant.getDescription(), loadContext);
		job.setContext(loadContext);
		// TODO: run async
		job.run(new NullProgressMonitor());
		//reschedule(job, loadContext);
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

	private synchronized void reschedule(ExternalizationJob job, IExternalizationContext context) {
		if (!saveDisabled) {
			job.setContext(context);
			job.schedule(5000);
		}
	}

	protected void setStatus(MultiStatus status) {
		this.loadStatus = status;
	}

	public IStatus getLoadStatus() {
		return loadStatus;
	}

	public void reset() {
		saveDisabled = false;
		loadStatus = null;
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
				for (IExternalizationParticipant participant : externalizationParticipants) {
					ISchedulingRule rule = participant.getSchedulingRule();
					if (participant.isDirty()) {
						try {
							Job.getJobManager().beginRule(rule, monitor);
							monitor.setTaskName("Saving " + participant.getDescription());
							participant.execute(context, monitor);
						} catch (CoreException e) {
							StatusHandler.log(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
									"Save failed for " + participant.getDescription(), e));
						} finally {
							Job.getJobManager().endRule(rule);
						}
					}
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
