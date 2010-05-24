/*******************************************************************************
 * Copyright (c) 2006, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Peter Stibrany - fixes for bug 220314
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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
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

	public void init(IViewPart view) {
		this.viewPart = view;
	}

	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) currentSelection).getFirstElement();
			if (object instanceof ITaskAttachment) {
				final ITaskAttachment attachment = (ITaskAttachment) object;

				IWorkbenchPart vp = viewPart;
				if (vp == null) {
					vp = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
				}

				DownloadAndApplyPatch job = new DownloadAndApplyPatch(Messages.ApplyPatchAction_downloadingPatch,
						attachment, vp);
				job.setUser(true);
				job.schedule();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}

	private static class DownloadAndApplyPatch extends Job {
		private final ITaskAttachment attachment;

		private final IWorkbenchPart wbPart;

		public DownloadAndApplyPatch(String jobName, ITaskAttachment attachment, IWorkbenchPart wbPart) {
			super(jobName);

			this.attachment = attachment;
			this.wbPart = wbPart;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
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
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				AttachmentUtil.downloadAttachment(attachment, fos, monitor);
				ok = true;
			} catch (IOException e) {
				return new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
						Messages.ApplyPatchAction_failedToDownloadPatch, e);
			} catch (CoreException e) {
				int s = IStatus.ERROR;
				if (e.getStatus() != null && e.getStatus().getCode() == IStatus.CANCEL) {
					s = IStatus.CANCEL;
				}
				return new Status(s, FocusedTeamUiPlugin.ID_PLUGIN, Messages.ApplyPatchAction_failedToDownloadPatch, e);
			} catch (OperationCanceledException e) {
				return new Status(IStatus.CANCEL, FocusedTeamUiPlugin.ID_PLUGIN,
						Messages.ApplyPatchAction_failedToDownloadPatch, e);
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						if (ok) {
							// report this exception if there was no other problem until now ... we will not work with file
							// which cannot be closed properly, because this indicates some problem
							file.delete();
							return new Status(IStatus.ERROR, FocusedTeamUiPlugin.ID_PLUGIN,
									Messages.ApplyPatchAction_failedToDownloadPatch, e);
						}
					}
				}

				if (!ok) {
					file.delete();
				}
			}

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

			disp.asyncExec(new Runnable() {
				public void run() {
					ApplyPatchOperation op = new ApplyPatchOperation(wbPart, fileStorage, null,
							new CompareConfiguration());

					BusyIndicator.showWhile(disp, op);
				}
			});

			return Status.OK_STATUS;
		}
	}

}
