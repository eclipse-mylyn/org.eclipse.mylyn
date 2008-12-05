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

package org.eclipse.mylyn.internal.tasks.ui.dialogs;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Mik Kersten
 * @author Leo Dos Santos
 */
public class UiLegendDialog extends PopupDialog {

	private FormToolkit toolkit;

	private ScrolledForm form;

	private UiLegendControl content;

	// TODO e3.4 move to new api
	@SuppressWarnings("deprecation")
	public UiLegendDialog(Shell parent) {
		super(parent, PopupDialog.INFOPOPUP_SHELLSTYLE | SWT.ON_TOP, false, false, false, false, null, null);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		return createDialogArea(parent);
	}

	@Override
	public int open() {
		int open = super.open();
//		getShell().setLocation(getShell().getLocation().x, getShell().getLocation().y+20);
		getShell().setFocus();
		return open;
	}

	@Override
	public boolean close() {
		if (form != null && !form.isDisposed()) {
			form.dispose();
		}

		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}

		if (content != null && !content.isDisposed()) {
			content.dispose();
		}

		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText(Messages.UiLegendControl_Tasks_UI_Legend);
		form.getToolBarManager().add(new CloseDialogAction());
		form.getToolBarManager().update(true);
		form.getBody().setLayout(new TableWrapLayout());
		toolkit.decorateFormHeading(form.getForm());

		content = new UiLegendControl(form.getBody(), toolkit);
		content.setWindow(this);

		return parent;
	}

	private class CloseDialogAction extends Action {

		private CloseDialogAction() {
			setImageDescriptor(CommonImages.NOTIFICATION_CLOSE);
			setText(Messages.UiLegendDialog_Close_Dialog);
		}

		@Override
		public void run() {
			close();
		}

	}
}
