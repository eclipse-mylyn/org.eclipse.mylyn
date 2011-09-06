/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.ui.operations.GerritOperationDialog;
import org.eclipse.mylyn.internal.tasks.ui.actions.SynchronizeEditorAction;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.google.gerrit.common.data.GerritConfig;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractGerritSection extends AbstractTaskEditorSection {

	public Label addTextClient(final FormToolkit toolkit, final Section section, String text) {
		final Label label = new Label(section, SWT.NONE);
		label.setText("  " + text); //$NON-NLS-1$
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setVisible(!section.isExpanded());

		section.setTextClient(label);
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				label.setVisible(!section.isExpanded());
			}
		});

		return label;
	}

	protected Shell getShell() {
		return getTaskEditorPage().getSite().getShell();
	}

	protected ITask getTask() {
		return getTaskEditorPage().getTask();
	}

	protected void openOperationDialog(GerritOperationDialog dialog) {
		if (dialog.open() == Window.OK) {
			SynchronizeEditorAction action = new SynchronizeEditorAction();
			action.selectionChanged(new StructuredSelection(getTaskEditorPage().getEditor()));
			action.run();
		}
	}

	protected GerritConfig getConfig() {
		GerritConnector connector = (GerritConnector) TasksUi.getRepositoryConnector(getTaskData().getConnectorKind());
		GerritClient client = connector.getClient(getTaskEditorPage().getTaskRepository());
		return client.getConfig();
	}

}
