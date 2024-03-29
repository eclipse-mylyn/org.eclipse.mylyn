/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract class for an in-place popup dialog that has a cancel button and sends and ok event when the dialog is closed
 * 
 * @author Shawn Minto
 * @since 3.7
 */
public abstract class AbstractInPlaceDialog extends PopupDialog {

	public static final int ID_CLEAR = IDialogConstants.CLIENT_ID + 1;

	protected static final int MARGIN_SIZE = 3;

	private final int side;

	private final Rectangle controlBounds;

	private Control control;

	private final Set<IInPlaceDialogListener> listeners = new HashSet<>();

	private final Control openControl;

	DisposeListener disposeListener = e -> dispose();

	public AbstractInPlaceDialog(Shell parent, int side, Control openControl) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, false, false, false, false, false, null, null);
		this.side = side;
		this.openControl = openControl;

		Rectangle bounds;
		if (openControl == null || openControl.isDisposed()) {
			bounds = new Rectangle(0, 0, 0, 0);
		} else {
			bounds = openControl.getBounds();
			Point absPosition = openControl.toDisplay(openControl.getLocation());
			bounds.x = absPosition.x - bounds.x;
			bounds.y = absPosition.y - bounds.y;
		}
		controlBounds = bounds;
		if (openControl != null) {
			openControl.addDisposeListener(disposeListener);
		}

	}

	@Override
	protected Control createContents(Composite parent) {
		return createDialogArea(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.numColumns = 1;
		composite.setLayout(gl);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

		control = createControl(composite);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginBottom = MARGIN_SIZE;
		gl.marginRight = MARGIN_SIZE;
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 0;
		gl.numColumns = 1;
		gl.makeColumnsEqualWidth = false;
		buttonComposite.setLayout(gl);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonComposite);
		createButtons(buttonComposite);
		Dialog.applyDialogFont(parent);

		parent.pack();

		return parent;
	}

	protected abstract Control createControl(Composite composite);

	protected void createButtons(Composite composite) {
		createButton(composite, ID_CLEAR, Messages.DateSelectionDialog_Clear, true);
	}

	protected void dispose() {
		setReturnCode(Window.CANCEL);
		close();
	}

	protected Control createButton(Composite composite, final int returnCode, String text, final boolean shouldClose) {
		Button button = new Button(composite, SWT.NONE);
		button.setText(text);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(returnCode);
				if (shouldClose) {
					close();
				} else {
					notifyButtonPressed(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		if (composite.getLayout() instanceof GridLayout) {
			((GridLayout) composite.getLayout()).numColumns = composite.getChildren().length;
		}
		GridDataFactory.fillDefaults().applyTo(button);
		return button;
	}

	@Override
	public int open() {
		int result = super.open();
		getControl().setFocus();
		setReturnCode(-1);
		return result;
	}

	@Override
	public boolean close() {
		if (getReturnCode() == -1) {
			setReturnCode(Window.OK);
		}
		notifyButtonPressed(true);
		if (openControl != null && !openControl.isDisposed()) {
			openControl.removeDisposeListener(disposeListener);
		}
		return super.close();
	}

	public void addEventListener(IInPlaceDialogListener listener) {
		listeners.add(listener);
	}

	public void removeCloseListener(IInPlaceDialogListener listener) {
		listeners.remove(listener);
	}

	private void notifyButtonPressed(boolean isClosing) {
		final InPlaceDialogEvent event = new InPlaceDialogEvent(getReturnCode(), isClosing);
		for (final IInPlaceDialogListener listener : listeners) {
			SafeRunnable.run(new ISafeRunnable() {
				@Override
				public void run() throws Exception {
					listener.buttonPressed(event);
				}

				@Override
				public void handleException(Throwable exception) {
					// ignore
				}
			});
		}
	}

	@Override
	protected void initializeBounds() {
//		Rectangle monitorBounds = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow()
//				.getShell()
//				.getMonitor()
//				.getClientArea();
		Rectangle monitorBounds = getShell().getMonitor().getClientArea();
		Rectangle bounds = getShell().getBounds();
		int x = 0;
		int y = 0;

		switch (side) {
			case SWT.TOP:
				x = controlBounds.x;
				y = controlBounds.y + controlBounds.height;
				if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
					x = controlBounds.x + controlBounds.width - bounds.width;
				}
				break;
			case SWT.BOTTOM:
				x = controlBounds.x;
				y = controlBounds.y - bounds.height;
				if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
					x = controlBounds.x + controlBounds.width - bounds.width;
				}
				break;
			case SWT.RIGHT:
				x = controlBounds.x + controlBounds.width - bounds.width;
				y = controlBounds.y + controlBounds.height;
				break;
			case SWT.LEFT:
				x = controlBounds.x;
				y = controlBounds.y + controlBounds.height;
				break;
		}
		getShell().setBounds(x, y, bounds.width, bounds.height);
	}

	@Override
	protected void adjustBounds() {
		Point tipSize = getShell().getSize();
		Point location = getShell().getLocation();

		Rectangle bounds;
		Point rightBounds = new Point(tipSize.x + location.x, tipSize.y + location.y);

		Monitor[] ms = getShell().getDisplay().getMonitors();

		if (ms.length > 1) {
			bounds = controlBounds;
			Point p = new Point(location.x, location.y);

			Rectangle tmp;
			for (Monitor element : ms) {
				tmp = element.getBounds();
				if (tmp.contains(p)) {
					bounds = tmp;
					break;
				}
			}

		} else {
			bounds = getControl().getDisplay().getBounds();
		}

		if (!(bounds.contains(location) && bounds.contains(rightBounds))) {
			if (rightBounds.x > bounds.x + bounds.width) {
				location.x -= rightBounds.x - (bounds.x + bounds.width);
			}

			if (rightBounds.y > bounds.y + bounds.height) {
				location.y -= rightBounds.y - (bounds.y + bounds.height);
			}

			if (location.x < bounds.x) {
				location.x = bounds.x;
			}

			if (location.y < bounds.y) {
				location.y = bounds.y;
			}
		}

		getShell().setLocation(location);
	}

	public Control getControl() {
		return control;
	}
}
