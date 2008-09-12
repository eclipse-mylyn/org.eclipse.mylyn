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

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Rob Elves
 */
public class BugzillaVotesEditor extends AbstractAttributeEditor {

	// Copy from <code>TaskEditorAttributePart</code>
	private static final int LABEL_WIDTH = 100;

	// Copy from TaskEditorAttributePart
	private static final int COLUMN_GAP = 5;

	public BugzillaVotesEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
	}

	private Hyperlink voteControl;

	private Label hiddenLabel;

	private Hyperlink showVotes;

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		((GridData) getLabelControl().getLayoutData()).exclude = true;
		showVotes = toolkit.createHyperlink(parent, getTaskAttribute().getValue(), SWT.NONE);
		showVotes.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		showVotes.setToolTipText("Show votes");
		showVotes.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				TasksUiUtil.openUrl(getTaskAttribute().getTaskData().getRepositoryUrl()
						+ IBugzillaConstants.URL_SHOW_VOTES + getTaskAttribute().getTaskData().getTaskId());
			}
		});
		setControl(showVotes);
	}

	@Override
	public void createLabelControl(Composite composite, FormToolkit toolkit) {
		voteControl = toolkit.createHyperlink(composite, getLabel(), SWT.NONE);
		voteControl.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		voteControl.setToolTipText("Vote");
		voteControl.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				TasksUiUtil.openUrl(getTaskAttribute().getTaskData().getRepositoryUrl() + IBugzillaConstants.URL_VOTE
						+ getTaskAttribute().getTaskData().getTaskId());
			}
		});

		GridData gd = GridDataFactory.fillDefaults()
				.align(SWT.RIGHT, SWT.CENTER)
				.hint(LABEL_WIDTH, SWT.DEFAULT)
				.create();

		gd.horizontalIndent = COLUMN_GAP;
		gd.widthHint = LABEL_WIDTH + COLUMN_GAP;

		voteControl.setLayoutData(gd);

		hiddenLabel = toolkit.createLabel(composite, "");
		GridData data = new GridData();
		data.exclude = true;
		hiddenLabel.setLayoutData(data);
	}

	@Override
	protected void decorateOutgoing(Color color) {
		// ignore
	}

	@Override
	public Label getLabelControl() {
		return hiddenLabel;
	}

	public String getValue() {
		return getAttributeMapper().getValue(getTaskAttribute());
	}

	public void setValue(String text) {
		getAttributeMapper().setValue(getTaskAttribute(), text);
		attributeChanged();
	}

}
