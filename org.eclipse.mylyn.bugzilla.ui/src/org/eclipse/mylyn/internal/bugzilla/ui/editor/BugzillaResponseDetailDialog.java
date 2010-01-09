/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryResponse;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BugzillaResponseDetailDialog extends Dialog {

	private final BugzillaRepositoryResponse response;

	public BugzillaResponseDetailDialog(Shell parentShell, BugzillaRepositoryResponse response) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.response = response;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText(Messages.BugzillaResponseDetailDialog_Titel);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		gd.heightHint = 120;
		gd.widthHint = 300;
		text.setLayoutData(gd);
		text.setEditable(false);

		String mes = ""; //$NON-NLS-1$
		for (String iterable_map : response.getResponseData().keySet()) {
			if (mes.length() > 0) {
				mes += "\n"; //$NON-NLS-1$
			}
			mes += NLS.bind(Messages.BugzillaResponseDetailDialog_Bug_Line, iterable_map);
			Map<String, List<String>> responseMap = response.getResponseData().get(iterable_map);
			for (String iterable_list : responseMap.keySet()) {
				mes += NLS.bind(Messages.BugzillaResponseDetailDialog_Action_Line, iterable_list);
				List<String> responseList = responseMap.get(iterable_list);
				for (String string : responseList) {
					mes += NLS.bind(Messages.BugzillaResponseDetailDialog_Email_Line, string);
				}
			}

		}
		text.setText(mes);
		parent.pack();
		applyDialogFont(composite);
		return composite;
	}
}
