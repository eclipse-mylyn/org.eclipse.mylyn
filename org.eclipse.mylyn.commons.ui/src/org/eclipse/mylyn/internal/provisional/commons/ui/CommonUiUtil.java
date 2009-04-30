/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class CommonUiUtil {

	private static final String KEY_DISABLED = "org.eclipse.mylyn.commons.ui.disabled"; //$NON-NLS-1$

	public static void busyCursorWhile(final ICoreRunnable runnable) throws OperationCanceledException, CoreException {
		try {
			IRunnableWithProgress runner = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						runnable.run(monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} finally {
						monitor.done();
					}
				}
			};
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runner);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else {
				CommonsUiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Unexpected exception", e)); //$NON-NLS-1$
			}
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public static void run(IRunnableContext context, final ICoreRunnable runnable) throws CoreException {
		try {
			IRunnableWithProgress runner = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						runnable.run(monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} finally {
						monitor.done();
					}
				}
			};
			context.run(true, true, runner);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else {
				CommonsUiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Unexpected exception", e)); //$NON-NLS-1$
			}
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public static void runInUi(ICoreRunnable runnable, ISchedulingRule rule) throws CoreException {
		runInUi(PlatformUI.getWorkbench().getProgressService(), runnable, rule);
	}

	public static void runInUi(IRunnableContext context, final ICoreRunnable runnable, ISchedulingRule rule)
			throws CoreException {
		try {
			IRunnableWithProgress runner = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						runnable.run(monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} finally {
						monitor.done();
					}
				}
			};
			PlatformUI.getWorkbench().getProgressService().runInUI(context, runner, rule);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else {
				CommonsUiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN, "Unexpected exception", e)); //$NON-NLS-1$
			}
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public static void setMessage(DialogPage page, IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
		case IStatus.OK:
			page.setMessage(null, IMessageProvider.NONE);
			break;
		case IStatus.INFO:
			page.setMessage(message, IMessageProvider.INFORMATION);
			break;
		case IStatus.WARNING:
			page.setMessage(message, IMessageProvider.WARNING);
			break;
		default:
			page.setMessage(message, IMessageProvider.ERROR);
			break;
		}
	}

	/**
	 * Recursively sets the menu of all children of <code>composite</code>.
	 */
	public static void setMenu(Composite composite, Menu menu) {
		if (!composite.isDisposed()) {
			composite.setMenu(menu);
			for (Control child : composite.getChildren()) {
				child.setMenu(menu);
				if (child instanceof Composite) {
					setMenu((Composite) child, menu);
				}
			}
		}
	}

	public static void setEnabled(Composite composite, boolean enabled) {
		if (enabled) {
			enable(composite);
		} else {
			disable(composite);
		}
	}

	private static void disable(Composite composite) {
		if (!composite.isDisposed()) {
			if (!composite.getEnabled()) {
				composite.setData(KEY_DISABLED, Boolean.TRUE);
			} else {
				composite.setEnabled(false);
			}
			for (Control control : composite.getChildren()) {
				if (control instanceof Composite) {
					disable((Composite) control);
				} else {
					if (!control.getEnabled()) {
						control.setData(KEY_DISABLED, Boolean.TRUE);
					} else {
						control.setEnabled(false);
					}
				}
			}
		}
	}

	private static void enable(Composite composite) {
		if (!composite.isDisposed()) {
			if (composite.getData(KEY_DISABLED) == null) {
				composite.setEnabled(true);
			} else {
				composite.setData(KEY_DISABLED, null);
			}
			for (Control control : composite.getChildren()) {
				if (control instanceof Composite) {
					enable((Composite) control);
				} else {
					if (control.getData(KEY_DISABLED) == null) {
						control.setEnabled(true);
					} else {
						control.setData(KEY_DISABLED, null);
					}
				}
			}
		}
	}

}
