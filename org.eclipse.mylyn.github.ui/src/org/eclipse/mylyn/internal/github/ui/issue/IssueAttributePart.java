/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.internal.github.core.issue.IssueAttribute;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.UpdateRepositoryConfigurationAction;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.progress.IProgressConstants2;

/**
 * GitHub issue task editor attribute part that display labels and milestone
 * attribute editors.
 */
public class IssueAttributePart extends AbstractTaskEditorSection {

	private List<AbstractAttributeEditor> attributeEditors;

	private boolean hasIncoming;

	private Composite attributesComposite;

	/**
	 * Creates a new {@link IssueAttributePart}.
	 */
	public IssueAttributePart() {
		setPartName(Messages.TaskEditorAttributePart_Attributes);
	}

	@Override
	protected AbstractAttributeEditor createAttributeEditor(
			TaskAttribute attribute) {
		if (IssueAttribute.LABELS.getMetadata().getId()
				.equals(attribute.getId()))
			return new IssueLabelAttributeEditor(getModel(), attribute);
		if (IssueAttribute.MILESTONE.getMetadata().getId()
				.equals(attribute.getId()))
			return super.createAttributeEditor(attribute);
		return null;
	}

	private void createAttributeControls(Composite attributesComposite,
			FormToolkit toolkit) {
		for (AbstractAttributeEditor attributeEditor : attributeEditors) {
			if (attributeEditor.hasLabel())
				attributeEditor
						.createLabelControl(attributesComposite, toolkit);
			attributeEditor.createControl(attributesComposite, toolkit);
			Object data = attributeEditor.getControl().getLayoutData();
			if (data == null) {
				data = GridDataFactory.swtDefaults().create();
				attributeEditor.getControl().setLayoutData(data);
			}
			if (data instanceof GridData)
				((GridData) data).widthHint = 140;
			getTaskEditorPage().getAttributeEditorToolkit().adapt(
					attributeEditor);
		}
	}

	@Override
	public void createControl(Composite parent, final FormToolkit toolkit) {
		initialize();
		super.createControl(parent, toolkit);
	}

	@Override
	protected String getInfoOverlayText() {
		return null;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return getTaskData().isNew() || hasIncoming;
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		attributesComposite = toolkit.createComposite(parent);
		attributesComposite.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Control focus = event.display.getFocusControl();
				if (focus instanceof Text
						&& ((Text) focus).getEditable() == false)
					getManagedForm().getForm().setFocus();
			}
		});

		GridLayout attributesLayout = EditorUtil.createSectionClientLayout();
		attributesLayout.numColumns = 2;
		attributesLayout.horizontalSpacing = 7;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);

		createAttributeControls(attributesComposite, toolkit);
		toolkit.paintBordersFor(attributesComposite);

		return attributesComposite;
	}

	@Override
	protected void fillToolBar(ToolBarManager toolBar) {
		UpdateRepositoryConfigurationAction repositoryConfigRefresh = new UpdateRepositoryConfigurationAction() {
			@Override
			public void run() {
				getTaskEditorPage().showEditorBusy(true);
				final TaskJob job = TasksUiInternal.getJobFactory()
						.createUpdateRepositoryConfigurationJob(
								getTaskEditorPage().getConnector(),
								getTaskEditorPage().getTaskRepository(),
								getTaskEditorPage().getTask());
				job.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						PlatformUI.getWorkbench().getDisplay()
								.asyncExec(() -> {
									getTaskEditorPage().showEditorBusy(false);
									if (job.getStatus() != null) {
										getTaskEditorPage().getTaskEditor()
												.setStatus(
														Messages.TaskEditorAttributePart_Updating_of_repository_configuration_failed,
														Messages.TaskEditorAttributePart_Update_Failed,
														job.getStatus());
									} else {
										getTaskEditorPage().refresh();
									}
								});
					}
				});
				job.setUser(true);
				job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY,
						Boolean.TRUE);
				job.setPriority(Job.INTERACTIVE);
				job.schedule();
			}

		};
		repositoryConfigRefresh
				.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE_SMALL);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(
				getTaskEditorPage().getTaskRepository()));
		repositoryConfigRefresh
				.setToolTipText(Messages.TaskEditorAttributePart_Refresh_Attributes);
		toolBar.add(repositoryConfigRefresh);
	}

	private void initialize() {
		attributeEditors = new ArrayList<>();
		hasIncoming = false;

		TaskAttribute root = getTaskData().getRoot();
		List<TaskAttribute> attributes = new LinkedList<>();
		TaskAttribute milestones = root.getAttribute(IssueAttribute.MILESTONE
				.getMetadata().getId());
		if (milestones != null)
			attributes.add(milestones);

		TaskAttribute labels = root.getAttribute(IssueAttribute.LABELS
				.getMetadata().getId());
		if (labels != null)
			attributes.add(labels);

		for (TaskAttribute attribute : attributes) {
			AbstractAttributeEditor attributeEditor = createAttributeEditor(attribute);
			if (attributeEditor != null) {
				attributeEditors.add(attributeEditor);
				if (getModel().hasIncomingChanges(attribute))
					hasIncoming = true;
			}
		}
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof String) {
			String text = (String) input;
			Map<String, TaskAttribute> attributes = getTaskData().getRoot()
					.getAttributes();
			for (TaskAttribute attribute : attributes.values())
				if (text.equals(attribute.getId())) {
					TaskAttributeMetaData properties = attribute.getMetaData();
					if (TaskAttribute.KIND_DEFAULT.equals(properties.getKind()))
						selectReveal(attribute);
				}
		}
		return super.setFormInput(input);
	}

	/**
	 * Selects and shows the given attribute.
	 *
	 * @param attribute
	 *            to show
	 */
	public void selectReveal(TaskAttribute attribute) {
		if (attribute == null)
			return;

		if (!getSection().isExpanded())
			CommonFormUtil.setExpanded(getSection(), true);

		EditorUtil.reveal(getTaskEditorPage().getManagedForm().getForm(),
				attribute.getId());
	}

}
