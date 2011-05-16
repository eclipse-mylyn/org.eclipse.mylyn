/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.egit.ui.UIIcons;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.github.core.gist.GistAttribute;
import org.eclipse.mylyn.internal.github.ui.issue.IssueSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Gist task editor page class
 */
public class GistTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * @param editor
	 * @param connectorKind
	 */
	public GistTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage#fillToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void fillToolBar(IToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);

		Action cloneGist = new Action(
				Messages.GistTaskEditorPage_LabelCloneGistAction,
				UIIcons.CLONEGIT) {

			public void run() {
				ICommandService srv = (ICommandService) getSite().getService(
						ICommandService.class);
				IHandlerService hsrv = (IHandlerService) getSite().getService(
						IHandlerService.class);
				Command command = srv.getCommand(CloneGistHandler.ID);

				ExecutionEvent event = hsrv.createExecutionEvent(command, null);
				if (event.getApplicationContext() instanceof IEvaluationContext) {
					IEvaluationContext context = (IEvaluationContext) event
							.getApplicationContext();
					IStructuredSelection selection = new StructuredSelection(
							getModel().getTaskData());
					context.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME,
							selection);
					try {
						command.executeWithChecks(event);
					} catch (ExecutionException ignored) {
						// Ignored
					} catch (NotDefinedException ignored) {
						// Ignored
					} catch (NotEnabledException ignored) {
						// Ignored
					} catch (NotHandledException ignored) {
						// Ignored
					}
				}
			}

		};
		toolBarManager.prependToGroup("open", cloneGist); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage#createPartDescriptors()
	 */
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> partDescriptors = super
				.createPartDescriptors();
		Iterator<TaskEditorPartDescriptor> descriptorIt = partDescriptors
				.iterator();
		while (descriptorIt.hasNext()) {
			TaskEditorPartDescriptor partDescriptor = descriptorIt.next();
			String id = partDescriptor.getId();
			if (id.equals(ID_PART_ATTRIBUTES) || id.equals(ID_PART_SUMMARY)
					|| id.equals(ID_PART_ATTACHMENTS)
					|| id.equals(ID_PART_ACTIONS))
				descriptorIt.remove();
		}
		if (!getModel().getTaskData().isNew()) {
			partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {

				public AbstractTaskEditorPart createPart() {
					return new IssueSummaryPart(GistAttribute.AUTHOR_GRAVATAR
							.getId(), null);
				}
			}.setPath(PATH_HEADER));
			partDescriptors.add(new TaskEditorPartDescriptor(
					ID_PART_ATTACHMENTS) {

				public AbstractTaskEditorPart createPart() {
					return new GistAttachmentPart();
				}
			}.setPath(PATH_ATTACHMENTS));
		}
		partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_ACTIONS) {

			public AbstractTaskEditorPart createPart() {
				return new TaskEditorActionPart() {

					protected void addAttachContextButton(
							Composite buttonComposite, FormToolkit toolkit) {
						// Prohibit context button since Gists don't support
						// binary attachments
					}

				};
			}
		}.setPath(PATH_ACTIONS));

		return partDescriptors;
	}
}
