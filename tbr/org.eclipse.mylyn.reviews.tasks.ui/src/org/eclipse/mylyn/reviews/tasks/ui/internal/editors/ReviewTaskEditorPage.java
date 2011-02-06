/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.internal.tasks.ui.editors.ToolBarButtonContribution;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.internal.ITreeNode;
import org.eclipse.mylyn.reviews.tasks.core.internal.ReviewsUtil;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskProperties;
import org.eclipse.mylyn.reviews.tasks.ui.internal.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/*
 * @author Kilian Matt
 */
@SuppressWarnings("restriction")
public class ReviewTaskEditorPage extends AbstractTaskEditorPage {
	private ReviewScope scope;

	public ReviewTaskEditorPage(TaskEditor editor) {
		super(editor, getConnectorId(editor));
	}

	private static String getConnectorId(TaskEditor editor) {
		return ((TaskEditorInput) editor.getEditorInput()).getTaskRepository()
				.getConnectorKind();
	}

	@Override
	public boolean needsSubmitButton() {
		return true;
	}

	@Override
	public void doSubmit() {
		super.doSubmit();
	}

	@Override
	protected void handleTaskSubmitted(SubmitJobEvent event) {
		super.handleTaskSubmitted(event);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> taskDescriptors = new HashSet<TaskEditorPartDescriptor>();
		taskDescriptors.add(new TaskEditorPartDescriptor(
				ReviewTaskEditorPart.ID_PART_REVIEW) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewTaskEditorPart();
			}
		});
		taskDescriptors.add(new TaskEditorPartDescriptor(ReviewSummaryTaskEditorPart.ID_PART_REVIEWSUMMARY) {
			
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewSummaryTaskEditorPart();
			}
		});

		return taskDescriptors;
	}

	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		ToolBarButtonContribution submitButtonContribution = new ToolBarButtonContribution(
				"org.eclipse.mylyn.reviews.tasks.toolbars.submit") {

			@Override
			protected Control createButton(Composite parent) {
				Button submitButton = new Button(parent, SWT.FLAT);
				submitButton.setText("submit review"); //$NON-NLS-1$
				submitButton.setBackground(null);
				submitButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						doSubmit();
					}
				});
				return submitButton;
			}
		};
		submitButtonContribution.marginLeft = 10;
		toolBarManager.add(submitButtonContribution);
	}

	public ReviewScope getReviewScope() throws CoreException {
		if (scope == null) {
			IReviewMapper mapper = ReviewsUiPlugin.getMapper();
			scope = mapper.mapTaskToScope(TaskProperties.fromTaskData(
					TasksUi.getTaskDataManager(), getModel().getTaskData()));

		}
		return scope;
	}

	/* package */ITreeNode getReviewResults(IProgressMonitor monitor) throws CoreException {
		return ReviewsUtil.getReviewSubTasksFor(getModel().getTask(),
				TasksUi.getTaskDataManager(),
				ReviewsUiPlugin.getMapper(),
				monitor);
	}
}
