/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     IBM Corporation - helper methods from
 *       org.eclipse.wst.common.frameworks.internal.ui.WTPActivityHelper
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPluginContribution;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.IProgressConstants2;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.7
 */
public class WorkbenchUtil {

	public static final String GROUP_EDIT = "group.edit"; //$NON-NLS-1$

	public static final String GROUP_FILE = "group.file"; //$NON-NLS-1$

	public static final String GROUP_REFRESH = "group.refresh"; //$NON-NLS-1$

	public static final String GROUP_FILTER = "group.filter"; //$NON-NLS-1$

	public static final String GROUP_NAVIGATE = "group.navigate"; //$NON-NLS-1$

	public static final String GROUP_NEW = "group.new"; //$NON-NLS-1$

	public static final String GROUP_OPEN = "group.open"; //$NON-NLS-1$

	public static final String GROUP_PREFERENCES = "group.preferences"; //$NON-NLS-1$

	public static final String GROUP_PROPERTIES = "group.properties"; //$NON-NLS-1$

	public static final String GROUP_RUN = "group.run"; //$NON-NLS-1$

	/**
	 * @deprecated use {@link IProgressConstants2#SHOW_IN_TASKBAR_ICON_PROPERTY} instead
	 */
	@Deprecated
	public static final QualifiedName SHOW_IN_TASKBAR_ICON_PROPERTY = IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY;

	/**
	 * @since 3.9
	 */
	public static IViewPart findViewInActiveWindow(String viewId) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				return page.findView(viewId);
			}
		}
		return null;
	}

	/**
	 * @since 3.9
	 */
	public static void closeViewInActiveWindow(String viewId) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				page.hideView(page.findView(viewId));
			}
		}
	}

	public static IViewPart showViewInActiveWindow(String viewId) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				try {
					return page.showView(viewId);
				} catch (PartInitException e) {
					// ignore
				}
			}
		}
		return null;
	}

	/**
	 * Return the modal shell that is currently open. If there isn't one then return null.
	 * <p>
	 * <b>Note: Applied from patch on bug 99472.</b>
	 *
	 * @param shell
	 *            A shell to exclude from the search. May be <code>null</code>.
	 * @return Shell or <code>null</code>.
	 */
	private static Shell getModalShellExcluding(Shell shell) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Shell[] shells = workbench.getDisplay().getShells();
		int modal = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL | SWT.PRIMARY_MODAL;
		for (Shell shell2 : shells) {
			if (shell2.equals(shell)) {
				break;
			}
			// Do not worry about shells that will not block the user.
			if (shell2.isVisible()) {
				int style = shell2.getStyle();
				if ((style & modal) != 0) {
					return shell2;
				}
			}
		}
		return null;
	}

	/**
	 * Utility method to get the best parenting possible for a dialog. If there is a modal shell create it so as to avoid two modal dialogs.
	 * If not then return the shell of the active workbench window. If neither can be found return null.
	 * <p>
	 * <b>Note: Applied from patch on bug 99472.</b>
	 *
	 * @return Shell or <code>null</code>
	 */
	public static Shell getShell() {
		if (!PlatformUI.isWorkbenchRunning() || PlatformUI.getWorkbench().isClosing()) {
			return null;
		}
		Shell modal = getModalShellExcluding(null);
		if (modal != null) {
			return modal;
		}
		return getNonModalShell();
	}

	/**
	 * Get the active non modal shell. If there isn't one return null.
	 * <p>
	 * <b>Note: Applied from patch on bug 99472.</b>
	 *
	 * @return Shell
	 */
	private static Shell getNonModalShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0) {
				return windows[0].getShell();
			}
		} else {
			return window.getShell();
		}

		return null;
	}

	/**
	 * @return whether the UI is set up to filter contributions (has defined activity categories).
	 */
	public static final boolean isFiltering() {
		return !PlatformUI.getWorkbench().getActivitySupport().getActivityManager().getDefinedActivityIds().isEmpty();
	}

	public static boolean allowUseOf(Object object) {
		if (!isFiltering()) {
			return true;
		}
		if (object instanceof IPluginContribution contribution) {
			if (contribution.getPluginId() != null) {
				IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
				IIdentifier identifier = workbenchActivitySupport.getActivityManager()
						.getIdentifier(createUnifiedId(contribution));
				return identifier.isEnabled();
			}
		}
		if (object instanceof String) {
			IWorkbenchActivitySupport workbenchActivitySupport = PlatformUI.getWorkbench().getActivitySupport();
			IIdentifier identifier = workbenchActivitySupport.getActivityManager().getIdentifier((String) object);
			return identifier.isEnabled();
		}
		return true;
	}

	private static final String createUnifiedId(IPluginContribution contribution) {
		if (contribution.getPluginId() != null) {
			return contribution.getPluginId() + '/' + contribution.getLocalId();
		}
		return contribution.getLocalId();
	}

	public static void addDefaultGroups(IMenuManager menuManager) {
		menuManager.add(new Separator(GROUP_NEW));
		menuManager.add(new Separator(GROUP_OPEN));
		menuManager.add(new Separator(GROUP_EDIT));
		menuManager.add(new Separator(GROUP_FILE));
		menuManager.add(new Separator(GROUP_RUN));
		menuManager.add(new Separator(GROUP_NAVIGATE));
		menuManager.add(new Separator(GROUP_REFRESH));
		menuManager.add(new Separator(GROUP_FILTER));
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.add(new Separator(GROUP_PROPERTIES));
	}

	public static Object openProperties(IServiceLocator serviceLocator) {
		IHandlerService service = serviceLocator.getService(IHandlerService.class);
		if (service != null) {
			try {
				return service.executeCommand(IWorkbenchCommandConstants.FILE_PROPERTIES, null);
			} catch (NotEnabledException e) {
				// ignore
			} catch (Exception e) {
				CommonsWorkbenchPlugin.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN,
								"Opening repository properties failed", e)); //$NON-NLS-1$
			}
		}
		return IStatus.CANCEL;
	}

	public static void busyCursorWhile(final ICoreRunnable runnable) throws OperationCanceledException, CoreException {
		try {
			IRunnableWithProgress runner = monitor -> {
				try {
					runnable.run(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			};
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runner);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else {
				CommonsWorkbenchPlugin.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN, "Unexpected exception", e)); //$NON-NLS-1$
			}
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public static void runInUi(ICoreRunnable runnable, ISchedulingRule rule) throws CoreException {
		WorkbenchUtil.runInUi(PlatformUI.getWorkbench().getProgressService(), runnable, rule);
	}

	public static void runInUi(IRunnableContext context, final ICoreRunnable runnable, ISchedulingRule rule)
			throws CoreException {
		try {
			IRunnableWithProgress runner = monitor -> {
				try {
					runnable.run(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			};
			PlatformUI.getWorkbench().getProgressService().runInUI(context, runner, rule);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				throw (CoreException) e.getCause();
			} else {
				CommonsWorkbenchPlugin.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR, CommonsWorkbenchPlugin.ID_PLUGIN, "Unexpected exception", e)); //$NON-NLS-1$
			}
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
	}

	public static Image getWorkbenchShellImage(int maximumHeight) {
		// always use the launching workbench window
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			IWorkbenchWindow workbenchWindow = windows[0];
			if (workbenchWindow != null && !workbenchWindow.getShell().isDisposed()) {
				Image image = getShell().getImage();
				int diff = Integer.MAX_VALUE;
				if (image != null && image.getBounds().height <= maximumHeight) {
					diff = maximumHeight - image.getBounds().height;
				} else {
					image = null;
				}

				Image[] images = getShell().getImages();
				if (images != null && images.length > 0) {
					// find the icon that is closest in size, but not larger than maximumHeight
					for (Image image2 : images) {
						int newDiff = maximumHeight - image2.getBounds().height;
						if (newDiff >= 0 && newDiff <= diff) {
							diff = newDiff;
							image = image2;
						}
					}
				}

				return image;
			}
		}
		return null;
	}

}
