/*******************************************************************************
 * Copyright (c) 2006, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Peter Stibrany - fixes for bug 220314
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentFileStorage;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.team.ui.FocusedTeamUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @author Peter Stibrany
 */
public class ApplyPatchAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	private IViewPart viewPart;

	public ApplyPatchAction() {
		super(Messages.ApplyPatchAction_Apply_Patch);
	}

	protected ApplyPatchAction(String text) {
		super(text);
	}

	private ISelection currentSelection;

	@Override
	public void init(IViewPart view) {
		viewPart = view;
	}

	@Override
	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) currentSelection).getFirstElement();
			if (object instanceof final ITaskAttachment attachment) {
				IWorkbenchPart vp = viewPart;
				if (vp == null) {
					vp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
				}

				DownloadAndApplyPatch job = new DownloadAndApplyPatch(Messages.ApplyPatchAction_downloadingPatch,
						attachment, vp);

				try {
					WorkbenchUtil.busyCursorWhile(job);
				} catch (CoreException e) {
					StatusManager.getManager().handle(e.getStatus());
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		currentSelection = selection;
	}

	private static class DownloadAndApplyPatch implements ICoreRunnable {
		private final ITaskAttachment attachment;

		private final IWorkbenchPart wbPart;

		private final String jobName;

		public DownloadAndApplyPatch(String jobName, ITaskAttachment attachment, IWorkbenchPart wbPart) {
			this.jobName = jobName;
			this.attachment = attachment;
			this.wbPart = wbPart;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
			try {
				IStatus result = execute(new SubProgressMonitor(monitor, 100));
				if (result != null && !result.isOK()) {
					throw new CoreException(result);
				}
			} finally {
				monitor.done();
			}
		}

		protected IStatus execute(IProgressMonitor monitor) {
			String attachmentFilename = AttachmentUtil.getAttachmentFilename(attachment);

			File file = null;
			try {
				file = File.createTempFile("patch-", ".txt"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IOException e) {
				return new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
						Messages.ApplyPatchAction_failedToDownloadPatch, e);
			}
			file.deleteOnExit();

			boolean ok = false;
			try (FileOutputStream fos = new FileOutputStream(file)) {
				AttachmentUtil.downloadAttachment(attachment, fos, monitor);
				ok = true;
				
				IWorkbenchPartSite site = wbPart.getSite();
				if (site == null) {
					return new Status(IStatus.WARNING, FocusedTeamUiPlugin.ID_PLUGIN,
							Messages.ApplyPatchAction_cannotApplyPatch);
				}

				final Display disp = site.getWorkbenchWindow().getWorkbench().getDisplay();
				if (disp.isDisposed()) {
					return new Status(IStatus.WARNING, FocusedTeamUiPlugin.ID_PLUGIN,
							Messages.ApplyPatchAction_cannotApplyPatch);
				}

				final AttachmentFileStorage fileStorage = new AttachmentFileStorage(file, attachmentFilename);

				disp.asyncExec(() -> {
					ApplyPatchOperation op = new ApplyPatchOperation(wbPart, fileStorage, null, new CompareConfiguration());

					BusyIndicator.showWhile(disp, op);
				});

				return Status.OK_STATUS;
			} catch (IOException e) {
				return new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
						Messages.ApplyPatchAction_failedToDownloadPatch, e);
			} catch (CoreException e) {
				int s = IStatus.ERROR;
				if (e.getStatus() != null && e.getStatus().getCode() == IStatus.CANCEL) {
					throw new OperationCanceledException();
				}
				return new Status(s, FocusedTeamUiPlugin.ID_PLUGIN, Messages.ApplyPatchAction_failedToDownloadPatch, e);
			} finally {
				if (!ok) {
					file.delete();
				}
			}
		}
	}

}
