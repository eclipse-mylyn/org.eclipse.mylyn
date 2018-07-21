/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Chris Gross (schtoo@schtoo.com) - patch for bug 16179
 *     Tasktop Technologies - extracted code for Mylyn
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.context;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A helper class for running operations in dialogs. Based on {@link WizardDialog}.
 * 
 * @author Steffen Pingel
 */
class ProgressContainer implements IRunnableContext {

	private static final String FOCUS_CONTROL = "focusControl"; //$NON-NLS-1$

	// The number of long running operation executed from the dialog.
	private long activeRunningOperations = 0;

	private Cursor arrowCursor;

	private Button cancelButton;

	private boolean lockedUI = false;

	// The progress monitor
	private final ProgressMonitorPart progressMonitorPart;

	private final Shell shell;

	private Cursor waitCursor;

	public ProgressContainer(Shell shell, ProgressMonitorPart progressMonitorPart) {
		Assert.isNotNull(shell);
		Assert.isNotNull(progressMonitorPart);
		this.shell = shell;
		this.progressMonitorPart = progressMonitorPart;
	}

	/**
	 * About to start a long running operation triggered through the wizard. Shows the progress monitor and disables the
	 * wizard's buttons and controls.
	 * 
	 * @param enableCancelButton
	 *            <code>true</code> if the Cancel button should be enabled, and <code>false</code> if it should be
	 *            disabled
	 * @return the saved UI state
	 */
	private Object aboutToStart(boolean enableCancelButton) {
		Map<Object, Object> savedState = null;
		if (getShell() != null) {
			// Save focus control
			Control focusControl = getShell().getDisplay().getFocusControl();
			if (focusControl != null && focusControl.getShell() != getShell()) {
				focusControl = null;
			}
			//cancelButton.removeSelectionListener(cancelListener);
			// Set the busy cursor to all shells.
			Display d = getShell().getDisplay();
			waitCursor = new Cursor(d, SWT.CURSOR_WAIT);
			setDisplayCursor(waitCursor);
			// Set the arrow cursor to the cancel component.
			arrowCursor = new Cursor(d, SWT.CURSOR_ARROW);
			cancelButton.setCursor(arrowCursor);
			// Deactivate shell
			savedState = new HashMap<Object, Object>(10);
			saveUiState(savedState);
			if (focusControl != null) {
				savedState.put(FOCUS_CONTROL, focusControl);
			}
			// Attach the progress monitor part to the cancel button
			progressMonitorPart.attachToCancelComponent(cancelButton);
			progressMonitorPart.setVisible(true);
		}
		return savedState;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	private IProgressMonitor getProgressMonitor() {
		return progressMonitorPart;
	}

	public Shell getShell() {
		return shell;
	}

	public boolean isActive() {
		return activeRunningOperations > 0;
	}

	public boolean isLockedUI() {
		return lockedUI;
	}

	protected void restoreUiState(Map<Object, Object> state) {
		// ignore

	}

	/**
	 * This implementation of IRunnableContext#run(boolean, boolean, IRunnableWithProgress) blocks until the runnable
	 * has been run, regardless of the value of <code>fork</code>. It is recommended that <code>fork</code> is set to
	 * true in most cases. If <code>fork</code> is set to <code>false</code>, the runnable will run in the UI thread and
	 * it is the runnable's responsibility to call <code>Display.readAndDispatch()</code> to ensure UI responsiveness.
	 * UI state is saved prior to executing the long-running operation and is restored after the long-running operation
	 * completes executing. Any attempt to change the UI state of the wizard in the long-running operation will be
	 * nullified when original UI state is restored.
	 */
	public void run(boolean fork, boolean cancelable, IRunnableWithProgress runnable) throws InvocationTargetException,
			InterruptedException {
		// The operation can only be canceled if it is executed in a separate
		// thread.
		// Otherwise the UI is blocked anyway.
		Object state = null;
		if (activeRunningOperations == 0) {
			state = aboutToStart(fork && cancelable);
		}
		activeRunningOperations++;
		try {
			if (!fork) {
				lockedUI = true;
			}
			ModalContext.run(runnable, fork, getProgressMonitor(), getShell().getDisplay());
			lockedUI = false;
		} finally {
			activeRunningOperations--;
			// Stop if this is the last one
			if (state != null) {
				stopped(state);
			}
		}
	}

	protected void saveUiState(Map<Object, Object> savedState) {
		// ignore

	}

	public void setCancelButton(Button cancelButton) {
		this.cancelButton = cancelButton;
	}

	/**
	 * Sets the given cursor for all shells currently active for this window's display.
	 * 
	 * @param c
	 *            the cursor
	 */
	private void setDisplayCursor(Cursor c) {
		Shell[] shells = getShell().getDisplay().getShells();
		for (Shell shell2 : shells) {
			shell2.setCursor(c);
		}
	}

	/**
	 * A long running operation triggered through the wizard was stopped either by user input or by normal end. Hides
	 * the progress monitor and restores the enable state wizard's buttons and controls.
	 * 
	 * @param savedState
	 *            the saved UI state as returned by <code>aboutToStart</code>
	 * @see #aboutToStart
	 */
	@SuppressWarnings("unchecked")
	private void stopped(Object savedState) {
		if (getShell() != null && !getShell().isDisposed()) {
			progressMonitorPart.setVisible(false);
			progressMonitorPart.removeFromCancelComponent(cancelButton);

			Map<Object, Object> state = (Map<Object, Object>) savedState;
			restoreUiState(state);
//				cancelButton.addSelectionListener(cancelListener);
			setDisplayCursor(null);
			cancelButton.setCursor(null);
			waitCursor.dispose();
			waitCursor = null;
			arrowCursor.dispose();
			arrowCursor = null;
			Control focusControl = (Control) state.get(FOCUS_CONTROL);
			if (focusControl != null && !focusControl.isDisposed()) {
				focusControl.setFocus();
			}
		}
	}

}