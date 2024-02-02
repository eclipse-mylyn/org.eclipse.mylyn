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
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IOperation;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;

/**
 * @author Steffen Pingel
 */
public class AbstractOperation implements IOperation {

	private EnumSet<OperationFlag> flags;

	private final List<OperationChangeListener> listeners = new CopyOnWriteArrayList<>();

	private List<IBuildElement> registeredElements;

	private final IOperationService service;

	public AbstractOperation(IOperationService service) {
		Assert.isNotNull(service);
		this.service = service;
	}

	public synchronized void addFlag(OperationFlag flag) {
		if (flags == null) {
			flags = EnumSet.of(flag);
		} else {
			flags.add(flag);
		}
	}

	public void addOperationChangeListener(OperationChangeListener listener) {
		listeners.add(listener);
	}

	protected void connect(BuildJob job, final IBuildElement element) {
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getJob() instanceof BuildJob) {
					handleResult((BuildJob) event.getJob());
				}
				getService().getRealm().asyncExec(() -> unregister(element));
				event.getJob().removeJobChangeListener(this);
			}

		});
		job.setUser(!hasFlag(OperationFlag.BACKGROUND));
	}

	protected void fireDone(final MultiStatus result) {
		for (final OperationChangeListener listener : listeners.toArray(new OperationChangeListener[0])) {
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					StatusHandler.log(new Status(IStatus.WARNING, BuildsCorePlugin.ID_PLUGIN, "Listener failed: " //$NON-NLS-1$
							+ listener.getClass(), e));
				}

				@Override
				public void run() throws Exception {
					OperationChangeEvent event = new OperationChangeEvent(AbstractOperation.this);
					event.setStatus(result);
					listener.done(event);
				}
			});
		}
	}

	public IOperationService getService() {
		return service;
	}

	protected void handleResult(BuildJob job) {
		IStatus status = job.getStatus();
		if (status != null && !status.isOK() && status.getSeverity() != IStatus.CANCEL) {
			getService().handleResult(AbstractOperation.this, status);
		}
	}

	public synchronized boolean hasFlag(OperationFlag flag) {
		if (flags != null) {
			return flags.contains(flag);
		}
		return false;
	}

	protected void register(List<? extends IBuildElement> elements) {
		if (registeredElements == null) {
			registeredElements = new ArrayList<>(elements.size());
		}
		for (IBuildElement element : elements) {
			element.getOperations().add(this);
			registeredElements.add(element);
		}
	}

	public synchronized void removeFlag(OperationFlag flag) {
		if (flags != null) {
			flags.remove(flag);
		}
	}

	public void removeOperationChangeListener(OperationChangeListener listener) {
		listeners.remove(listener);
	}

	protected void unregister(IBuildElement element) {
		element.getOperations().remove(this);
		if (registeredElements != null) {
			registeredElements.remove(element);
		}
	}

	protected void unregister(List<? extends IBuildElement> elements) {
		for (IBuildElement element : elements) {
			unregister(element);
		}
	}

	protected void unregisterAll() {
		if (registeredElements == null) {
			return;
		}
		for (IBuildElement element : registeredElements) {
			element.getOperations().remove(this);
		}
		registeredElements.clear();
	}

}
