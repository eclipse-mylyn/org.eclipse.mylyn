/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractElementOperation<T extends IBuildElement> extends AbstractOperation {

	private List<BuildJob> jobs;

	public AbstractElementOperation(IOperationService service) {
		super(service);
	}

	protected abstract BuildJob doCreateJob(T element);

	protected List<T> doInitInput() {
		final AtomicReference<List<T>> input = new AtomicReference<>();
		getService().getRealm().syncExec(() -> {
			List<T> elements = doSyncInitInput();
			register(elements);
			input.set(elements);
		});
		return input.get();
	}

	protected abstract List<T> doSyncInitInput();

	public void execute() {
		jobs = init();
		final MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Operation result", null);
		final CountDownLatch latch = new CountDownLatch(jobs.size());
		for (final BuildJob job : jobs) {
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (event.getJob() instanceof BuildJob) {
						BuildJob job = (BuildJob) event.getJob();
						IStatus status = job.getStatus();
						if (status != null && !status.isOK() && status.getSeverity() != IStatus.CANCEL) {
							result.add(status);
						}
					}

					boolean fireDone;
					synchronized (latch) {
						latch.countDown();
						fireDone = latch.getCount() == 0;
					}
					job.removeJobChangeListener(this);
					if (fireDone) {
						fireDone(result);
					}
				}
			});
		}
		schedule(jobs);
	}

	protected void schedule(List<BuildJob> jobs) {
		getService().getScheduler().schedule(jobs);
	}

	public List<BuildJob> init() {
		List<T> input = doInitInput();

		List<BuildJob> jobs = new ArrayList<>(input.size());
		for (final T element : input) {
			BuildJob job = doCreateJob(element);
			connect(job, element);
			jobs.add(job);
		}
		return jobs;
	}

	public IStatus doExecute(IOperationMonitor progress) {
		List<BuildJob> jobs = init();
		MultiStatus result = new MultiStatus(BuildsCorePlugin.ID_PLUGIN, 0, "Operation failed", null);
		progress.beginTask("", jobs.size()); //$NON-NLS-1$
		try {
			for (BuildJob job : jobs) {
				IStatus status = job.run(progress.newChild(1));
				handleResult(job);
				final IBuildElement element = (IBuildElement) job.getAdapter(IBuildElement.class);
				if (element != null) {
					getService().getRealm().asyncExec(() -> unregister(element));
				}
				if (status.getSeverity() == IStatus.CANCEL) {
					return Status.CANCEL_STATUS;
				} else if (!status.isOK()) {
					result.add(status);
				}
			}
		} finally {
			getService().getRealm().asyncExec(this::unregisterAll);
		}
		return result;
	}

	public void cancel() {
		if (jobs != null) {
			for (BuildJob job : jobs) {
				job.cancel();
			}
		}
	}

}
