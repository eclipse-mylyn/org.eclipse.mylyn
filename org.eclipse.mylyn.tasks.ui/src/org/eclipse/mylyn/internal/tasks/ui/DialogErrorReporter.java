package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylyn.commons.core.AbstractErrorReporter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class DialogErrorReporter extends AbstractErrorReporter {

	private static final String ERROR_MESSAGE = "Please report the following error at:\n"
			+ "http://bugs.eclipse.org/bugs/enter_bug.cgi?product=Mylyn\n\n"
			+ "Or via the popup menu in the Error Log view (see Window -> Show View)";

	private boolean errorDialogOpen;

	@Override
	public int getPriority(IStatus status) {
		return AbstractErrorReporter.PRIORITY_DEFAULT;
	}

	@Override
	public void handle(final IStatus status) {
		if (Platform.isRunning()) {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				Display display = workbench.getDisplay();
				if (display != null && !display.isDisposed()) {
					display.asyncExec(new Runnable() {
						public void run() {
							try {
								if (!errorDialogOpen) {
									errorDialogOpen = true;
									Shell shell = Display.getDefault().getActiveShell();
									ErrorDialog.openError(shell, "Mylyn Error", ERROR_MESSAGE, status);
								}
							} finally {
								errorDialogOpen = false;
							}
						}
					});
				}
			}
		}
	}
}
