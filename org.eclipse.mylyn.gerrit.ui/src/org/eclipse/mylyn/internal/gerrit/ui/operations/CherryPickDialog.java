/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.operations.CherryPickRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

public class CherryPickDialog extends GerritOperationDialog {

	private PatchSet patchSet;

	private GerritChange change;

	private RichTextEditor commitMessage;

	private Text destination;

	public CherryPickDialog(Shell parentShell, ITask task) {
		super(parentShell, task);
	}

	public CherryPickDialog(Shell shell, ITask task, PatchSet patchSet, GerritChange gerritChange) {
		super(shell, task);
		this.patchSet = patchSet;
		this.change = gerritChange;
	}

	@Override
	public GerritOperation<?> createOperation() {
		int patchSetId = patchSet.getId().get();
		CherryPickRequest request = new CherryPickRequest(task.getTaskId(), patchSetId, getDestination());
		request.setMessage(commitMessage.getText());
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	private String getDestination() {
		return destination.getText();
	}

	@Override
	protected Control createPageControls(final Composite parent) {
		setTitle(Messages.CherryPickDialog_Cherry_Pick);
		setMessage(NLS.bind(Messages.CherryPickDialog_Change_X_Set_Y, task.getTaskId(), patchSet.getPatchSetId()));

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(composite);

		Label branchlabel = new Label(composite, SWT.NONE);
		branchlabel.setText(NLS.bind(Messages.CherryPickDialog_Cherry_Pick_to_Branch, patchSet.getRefName()));

		destination = new Text(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).applyTo(destination);

		Label commitLabel = new Label(composite, SWT.NONE);
		commitLabel.setText(NLS.bind(Messages.CherryPickDialog_Cherry_Pick_Commit_Message, patchSet.getRefName()));

		commitMessage = createRichTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).hint(400, 100).applyTo(commitMessage.getControl());
		commitMessage.setText(getCommitMessage());

		destination.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});
		commitMessage.getViewer().addTextListener(new ITextListener() {
			@Override
			public void textChanged(TextEvent event) {
				updateButtons();
			}
		});

		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(destination, new TextContentAdapter(),
				createContentProposalProvider(), ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, null, true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		adapter.setAutoActivationCharacters(null);

		destination.setFocus();
		return composite;
	}

	private String getCommitMessage() {
		List<PatchSetDetail> details = change.getPatchSetDetails();
		// If patchSet doesn't exist in details, return commit message of most recent patch set
		PatchSetDetail patchSetDetail = details.get(details.size() - 1);
		for (PatchSetDetail detail : details) {
			if (detail.getPatchSet() == patchSet) {
				patchSetDetail = detail;
			}
		}
		return patchSetDetail.getInfo().getMessage();
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		setOKButtonEnabled(false);
		return control;
	};

	@Override
	protected boolean processOperationResult(GerritOperation<?> operation) {
		if (operation != null) {
			ChangeDetail changeDetail = (ChangeDetail) operation.getOperationResult();
			if (changeDetail != null && changeDetail.getChange() != null) {
				// changeId is deprecated, yet gerrit still uses it as an identifier, as does mylyn reviews
				TasksUiUtil.openTask(getRepository(), changeDetail.getChange().getChangeId() + ""); //$NON-NLS-1$
			}
		}
		return super.processOperationResult(operation);
	}

	private void updateButtons() {
		boolean isSubmittable = !Strings.isNullOrEmpty(destination.getText())
				&& !Strings.isNullOrEmpty(commitMessage.getText());
		setOKButtonEnabled(isSubmittable);
	}

	private void setOKButtonEnabled(boolean enable) {
		getButton(IDialogConstants.OK_ID).setEnabled(enable);
	}

	private IContentProposalProvider createContentProposalProvider() {
		GerritClient client = ((GerritConnector) TasksUi.getRepositoryConnector(task.getConnectorKind()))
				.getClient(getRepository());
		Set<String> allProjectBranches = client.getCachedBranches(change.getChangeDetail().getChange().getProject());
		SortedSet<String> proposals = allProjectBranches != null
				? Sets.newTreeSet(allProjectBranches)
				: Sets.<String> newTreeSet();
		return new BranchProposalProvider(proposals);
	}

	private TaskRepository getRepository() {
		return TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl());
	}
}
