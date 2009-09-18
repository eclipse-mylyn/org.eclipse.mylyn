/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui.dialogs;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.commons.ui.CommonsUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * Abstract class for an in-place popup dialog that has a cancel button and sends and ok event when the dialog is closed
 * 
 * @author Shawn Minto
 * @since 3.3
 */
public abstract class AbstractInPlaceDialog extends PopupDialog {

	private final int side;

	private final Rectangle controlBounds;

	private Control control;

	private final Set<IInPlaceDialogCloseListener> listeners = new HashSet<IInPlaceDialogCloseListener>();

	public AbstractInPlaceDialog(Shell parent, int side, Control openControl) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE, false, false, false, false, false, null, null);
		this.side = side;

		Rectangle bounds;
		if (openControl == null || openControl.isDisposed()) {
			bounds = new Rectangle(0, 0, 0, 0);
		} else {
			bounds = openControl.getBounds();
			Point absPosition = openControl.toDisplay(openControl.getLocation());
			bounds.x = absPosition.x - bounds.x;
			bounds.y = absPosition.y - bounds.y;
		}

		this.controlBounds = bounds;
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

		this.control = createControl(composite);

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginBottom = 5;
		gl.marginRight = 5;
		gl.horizontalSpacing = 10;
		gl.verticalSpacing = 0;
		gl.numColumns = 1;
		gl.makeColumnsEqualWidth = false;
		buttonComposite.setLayout(gl);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).applyTo(buttonComposite);
		createButtons(buttonComposite);

		parent.pack();

		Dialog.applyDialogFont(parent);

		return parent;
	}

	protected abstract Control createControl(Composite composite);

	protected void createButtons(Composite composite) {
		createButton(composite, Window.CANCEL, Messages.AbstractInPlacePopupDialog_Cancel);
	}

	protected Control createButton(Composite composite, final int returnCode, String text) {
		ImageHyperlink link = new ImageHyperlink(composite, SWT.NONE);
		link.setText(text);
		link.setUnderlined(true);
		link.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				setReturnCode(returnCode);
				close();

			}
		});
		if (composite.getLayout() instanceof GridLayout) {
			((GridLayout) composite.getLayout()).numColumns = composite.getChildren().length;
		}
		GridDataFactory.fillDefaults().applyTo(link);
		return link;
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
		notifyClosing();
		return super.close();
	}

	public void addCloseListener(IInPlaceDialogCloseListener listener) {
		listeners.add(listener);
	}

	public void removeCloseListener(IInPlaceDialogCloseListener listener) {
		listeners.remove(listener);
	}

	private void notifyClosing() {
		final InPlaceDialogCloseEvent event = new InPlaceDialogCloseEvent(getReturnCode());
		for (final IInPlaceDialogCloseListener listener : listeners) {
			SafeRunnable.run(new ISafeRunnable() {

				public void run() throws Exception {
					listener.dialogClosing(event);
				}

				public void handleException(Throwable exception) {
					CommonsUiPlugin.getDefault().getLog().log(
							new Status(IStatus.ERROR, CommonsUiPlugin.ID_PLUGIN,
									"Error while notifying IInPlaceCloseListener", exception)); //$NON-NLS-1$
				}
			});
		}
	}

	@Override
	protected void initializeBounds() {
		Rectangle monitorBounds = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell()
				.getMonitor()
				.getClientArea();
		Rectangle bounds = getShell().getBounds();
		int x = 0;
		int y = 0;

		switch (side) {
		case SWT.TOP:
			x = controlBounds.x;
			y = controlBounds.y + controlBounds.height;
			if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
				x = (controlBounds.x + controlBounds.width) - bounds.width;
			}
			break;
		case SWT.BOTTOM:
			x = controlBounds.x;
			y = controlBounds.y - bounds.height;
			if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
				x = (controlBounds.x + controlBounds.width) - bounds.width;
			}
			break;
		case SWT.RIGHT:
			x = (controlBounds.x + controlBounds.width) - bounds.width;
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
