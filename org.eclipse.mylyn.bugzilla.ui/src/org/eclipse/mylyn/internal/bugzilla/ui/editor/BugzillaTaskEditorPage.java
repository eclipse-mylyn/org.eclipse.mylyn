/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCustomField;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryResponse;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaStatus;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaUserMatchResponse;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.PersonProposalProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.PersonAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelEvent;
import org.eclipse.mylyn.tasks.core.data.TaskDataModelListener;
import org.eclipse.mylyn.tasks.core.sync.SubmitJobEvent;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.keys.IBindingService;

/**
 * @author Rob Elves
 * @author Frank Becker
 * @since 3.0
 */
public class BugzillaTaskEditorPage extends AbstractTaskEditorPage {

	public static final String ID_PART_BUGZILLA_PLANNING = "org.eclipse.mylyn.bugzilla.ui.editors.part.planning"; //$NON-NLS-1$

	public static final String ID_PART_BUGZILLA_FLAGS = "org.eclipse.mylyn.bugzilla.ui.editors.part.flags"; //$NON-NLS-1$

	public static final String PATH_FLAGS = "flags"; //$NON-NLS-1$

	private final Map<TaskAttribute, AbstractAttributeEditor> attributeEditorMap;

	private TaskDataModelListener productListener;

	private final List<ControlDecoration> errorDecorations = new ArrayList<ControlDecoration>();

	private final List<PersonAttributeEditor> editorsWithError = new ArrayList<PersonAttributeEditor>(3);

	public BugzillaTaskEditorPage(TaskEditor editor) {
		this(editor, BugzillaCorePlugin.CONNECTOR_KIND);
	}

	/**
	 * Call this constructor if extending the Bugzilla connector
	 * 
	 * @param editor
	 * @param connectorKind
	 */
	public BugzillaTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		this.attributeEditorMap = new HashMap<TaskAttribute, AbstractAttributeEditor>();
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();
		boolean hasPartComments = false;
		boolean hasPartNewComment = false;
		boolean hasPartDescription = false;
		// remove unnecessary default editor parts
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_PEOPLE)) {
				descriptors.remove(taskEditorPartDescriptor);
				break;
			}
		}
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_COMMENTS)) {
				descriptors.remove(taskEditorPartDescriptor);
				hasPartComments = true;
				break;
			}
		}
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_NEW_COMMENT)) {
				descriptors.remove(taskEditorPartDescriptor);
				hasPartNewComment = true;
				break;
			}
		}
		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_DESCRIPTION)) {
				descriptors.remove(taskEditorPartDescriptor);
				hasPartDescription = true;
				break;
			}
		}

		// Add Bugzilla Planning part
		try {
			TaskData data = TasksUi.getTaskDataManager().getTaskData(getTask());
			if (data != null) {
				// Add Bugzilla Flag part
				Map<String, TaskAttribute> attributes = data.getRoot().getAttributes();
				for (TaskAttribute attribute : attributes.values()) {
					if (BugzillaAttribute.KIND_FLAG.equals(attribute.getMetaData().getKind())) {
						descriptors.add(new TaskEditorPartDescriptor(ID_PART_BUGZILLA_FLAGS) {
							@Override
							public AbstractTaskEditorPart createPart() {
								return new BugzillaFlagPart();
							}
						}.setPath(ID_PART_ATTRIBUTES + "/" + PATH_FLAGS)); //$NON-NLS-1$
						break;
					}
				}

				TaskAttribute attrEstimatedTime = data.getRoot().getMappedAttribute(
						BugzillaAttribute.ESTIMATED_TIME.getKey());
				if (attrEstimatedTime != null) {
					descriptors.add(new TaskEditorPartDescriptor(ID_PART_BUGZILLA_PLANNING) {
						@Override
						public AbstractTaskEditorPart createPart() {
							return new BugzillaPlanningEditorPart();
						}
					}.setPath(ID_PART_ATTRIBUTES + "/" + PATH_PLANNING)); //$NON-NLS-1$
				}
			}
			if (hasPartDescription) {
				descriptors.add(new TaskEditorPartDescriptor(ID_PART_DESCRIPTION) {
					@Override
					public AbstractTaskEditorPart createPart() {
						BugzillaTaskEditorDescriptionPart part = new BugzillaTaskEditorDescriptionPart();
						if (getModel().getTaskData().isNew()) {
							part.setExpandVertically(true);
							part.setSectionStyle(ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
						}
						return part;
					}
				}.setPath(PATH_COMMENTS));

			}
			if (hasPartComments) {
				descriptors.add(new TaskEditorPartDescriptor(ID_PART_COMMENTS) {
					@Override
					public AbstractTaskEditorPart createPart() {
						return new BugzillaTaskEditorCommentPart();
					}
				}.setPath(PATH_COMMENTS));

			}
		} catch (CoreException e) {
			// ignore
		}

		if (hasPartNewComment) {
			// Add the updated Bugzilla new comment part
			descriptors.add(new TaskEditorPartDescriptor(ID_PART_NEW_COMMENT) {
				@Override
				public AbstractTaskEditorPart createPart() {
					return new BugzillaTaskEditorNewCommentPart();
				}
			}.setPath(PATH_COMMENTS));
		}
		// Add the updated Bugzilla people part
		descriptors.add(new TaskEditorPartDescriptor(ID_PART_PEOPLE) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new BugzillaPeoplePart();
			}
		}.setPath(PATH_PEOPLE));

		return descriptors;
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository(), getEditorSite()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, final TaskAttribute taskAttribute) {
				AbstractAttributeEditor editor;
				if (IBugzillaConstants.EDITOR_TYPE_KEYWORDS.equals(type)) {
					editor = new BugzillaKeywordAttributeEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_REMOVECC.equals(type)) {
					editor = new BugzillaCcAttributeEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_VOTES.equals(type)) {
					editor = new BugzillaVotesEditor(getModel(), taskAttribute);
				} else if (IBugzillaConstants.EDITOR_TYPE_FLAG.equals(type)) {
					editor = new FlagAttributeEditor(getModel(), taskAttribute);
				} else {
					editor = super.createEditor(type, taskAttribute);
					if (TaskAttribute.TYPE_BOOLEAN.equals(type)) {
						editor.setDecorationEnabled(false);
					}
				}

				if (editor != null && taskAttribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
					editor.setLayoutHint(new LayoutHint(editor.getLayoutHint()) {

						@Override
						public int getPriority() {
							return super.getPriority() * 10;
						}
					});
				}

				TaskAttributeMetaData properties = taskAttribute.getMetaData();
				if (editor != null && IBugzillaConstants.EDITOR_TYPE_FLAG.equals(properties.getType())) {
					editor.setLayoutHint(new LayoutHint(editor.getLayoutHint()) {

						@Override
						public int getPriority() {
							return super.getPriority() * 5;
						}
					});
				}
				BugzillaTaskEditorPage.this.addToAttributeEditorMap(taskAttribute, editor);
				return editor;
			}
		};
		return factory;
	}

	@Override
	public void doSubmit() {
		TaskAttribute summaryAttribute = getModel().getTaskData().getRoot().getMappedAttribute(TaskAttribute.SUMMARY);
		if (summaryAttribute != null && summaryAttribute.getValue().length() == 0) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_enter_a_short_summary_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart part = getPart(ID_PART_SUMMARY);
			if (part != null) {
				part.setFocus();
			}
			return;
		}

		TaskAttribute componentAttribute = getModel().getTaskData()
				.getRoot()
				.getMappedAttribute(BugzillaAttribute.COMPONENT.getKey());
		if (componentAttribute != null && componentAttribute.getValue().length() == 0) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_select_a_component_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart part = getPart(ID_PART_ATTRIBUTES);
			if (part != null) {
				part.setFocus();
			}
			return;
		}

		TaskAttribute descriptionAttribute = getModel().getTaskData()
				.getRoot()
				.getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (descriptionAttribute != null && descriptionAttribute.getValue().length() == 0
				&& getModel().getTaskData().isNew()) {
			getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Please_enter_a_description_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart descriptionPart = getPart(ID_PART_DESCRIPTION);
			if (descriptionPart != null) {
				descriptionPart.setFocus();
			}
			return;
		}
		TaskAttribute targetMilestoneAttribute = getModel().getTaskData()
				.getRoot()
				.getMappedAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey());
		if (targetMilestoneAttribute != null && targetMilestoneAttribute.getValue().length() == 0
				&& getModel().getTaskData().isNew()) {
			getTaskEditor().setMessage(
					Messages.BugzillaTaskEditorPage_Please_enter_a_target_milestone_before_submitting,
					IMessageProvider.ERROR);
			AbstractTaskEditorPart descriptionPart = getPart(ID_PART_ATTRIBUTES);
			if (descriptionPart != null) {
				descriptionPart.setFocus();
			}
			return;
		}

		TaskAttribute attributeOperation = getModel().getTaskData()
				.getRoot()
				.getMappedAttribute(TaskAttribute.OPERATION);
		if (attributeOperation != null) {
			if ("duplicate".equals(attributeOperation.getValue())) { //$NON-NLS-1$
				TaskAttribute originalOperation = getModel().getTaskData()
						.getRoot()
						.getAttribute(TaskAttribute.PREFIX_OPERATION + attributeOperation.getValue());
				String inputAttributeId = originalOperation.getMetaData().getValue(
						TaskAttribute.META_ASSOCIATED_ATTRIBUTE_ID);
				if (inputAttributeId != null && !inputAttributeId.equals("")) { //$NON-NLS-1$
					TaskAttribute inputAttribute = attributeOperation.getTaskData()
							.getRoot()
							.getAttribute(inputAttributeId);
					if (inputAttribute != null) {
						String dupValue = inputAttribute.getValue();
						if (dupValue == null || dupValue.equals("")) { //$NON-NLS-1$
							getTaskEditor().setMessage(
									Messages.BugzillaTaskEditorPage_Please_enter_a_bugid_for_duplicate_of_before_submitting,
									IMessageProvider.ERROR);
							AbstractTaskEditorPart part = getPart(ID_PART_ACTIONS);
							if (part != null) {
								part.setFocus();
							}
							return;
						}
					}
				}
			}
		}

		if (getModel().getTaskData().isNew()) {
			TaskAttribute productAttribute = getModel().getTaskData()
					.getRoot()
					.getMappedAttribute(TaskAttribute.PRODUCT);
			if (productAttribute != null && productAttribute.getValue().length() > 0) {
				getModel().getTaskRepository().setProperty(IBugzillaConstants.LAST_PRODUCT_SELECTION,
						productAttribute.getValue());
			}
			TaskAttribute componentSelectedAttribute = getModel().getTaskData()
					.getRoot()
					.getMappedAttribute(TaskAttribute.COMPONENT);
			if (componentSelectedAttribute != null && componentSelectedAttribute.getValue().length() > 0) {
				getModel().getTaskRepository().setProperty(IBugzillaConstants.LAST_COMPONENT_SELECTION,
						componentSelectedAttribute.getValue());
			}
		}

		// Force the most recent known good token onto the outgoing task data to ensure submit
		// bug#263318
		TaskAttribute attrToken = getModel().getTaskData().getRoot().getAttribute(BugzillaAttribute.TOKEN.getKey());
		if (attrToken != null) {
			String tokenString = getModel().getTask().getAttribute(BugzillaAttribute.TOKEN.getKey());
			if (tokenString != null) {
				attrToken.setValue(tokenString);
			}
		}
		for (ControlDecoration decoration : errorDecorations) {
			decoration.hide();
			decoration.dispose();
		}
		errorDecorations.clear();
		editorsWithError.clear();
		if (!checkCanSubmit(IMessageProvider.ERROR)) {
			return;
		}
		getTaskEditor().setMessage("", IMessageProvider.NONE); //$NON-NLS-1$
		super.doSubmit();
	}

	@Override
	protected void createParts() {
		attributeEditorMap.clear();
		super.createParts();
		checkCanSubmit(IMessageProvider.INFORMATION);
	}

	@Override
	protected TaskDataModel createModel(TaskEditorInput input) throws CoreException {
		TaskDataModel model = super.createModel(input);
		productListener = new ProductSelectionListener();
		model.addModelListener(productListener);
		return model;
	}

	/**
	 * @since 3.1
	 */
	private void addToAttributeEditorMap(TaskAttribute attribute, AbstractAttributeEditor editor) {
		if (attributeEditorMap.containsKey(attribute)) {
			attributeEditorMap.remove(attribute);
		}
		attributeEditorMap.put(attribute, editor);
	}

	/**
	 * @since 3.1
	 */
	private AbstractAttributeEditor getEditorForAttribute(TaskAttribute attribute) {
		return attributeEditorMap.get(attribute);
	}

	private void refresh(TaskAttribute attributeComponent) {
		AbstractAttributeEditor editor = getEditorForAttribute(attributeComponent);
		if (editor != null) {
			try {
				editor.refresh();
			} catch (UnsupportedOperationException e) {
				// ignore
			}
		}
	}

	private class ProductSelectionListener extends TaskDataModelListener {
		@Override
		public void attributeChanged(TaskDataModelEvent event) {
			TaskAttribute taskAttribute = event.getTaskAttribute();
			if (taskAttribute != null) {
				if (taskAttribute.getId().equals(BugzillaAttribute.PRODUCT.getKey())) {
					RepositoryConfiguration repositoryConfiguration = null;
					try {
						BugzillaRepositoryConnector connector = (BugzillaRepositoryConnector) TasksUi.getRepositoryConnector(getModel().getTaskRepository()
								.getConnectorKind());
						repositoryConfiguration = connector.getRepositoryConfiguration(getModel().getTaskRepository(),
								false, new NullProgressMonitor());
					} catch (CoreException e) {
						StatusHandler.log(new RepositoryStatus(getTaskRepository(), IStatus.ERROR,
								BugzillaUiPlugin.ID_PLUGIN, 0, "Failed to obtain repository configuration", e)); //$NON-NLS-1$
						getTaskEditor().setMessage("Problem occured when updating attributes", IMessageProvider.ERROR); //$NON-NLS-1$
						return;
					}

					TaskAttribute attributeComponent = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.COMPONENT.getKey());
					if (attributeComponent != null) {
						List<String> optionValues = repositoryConfiguration.getProductOptionValues(
								BugzillaAttribute.COMPONENT, taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeComponent.clearOptions();
						for (String option : optionValues) {
							attributeComponent.putOption(option, option);
						}
						if (optionValues.size() > 0) {
							attributeComponent.setValue(optionValues.get(0));
						}
						refresh(attributeComponent);
					}

					TaskAttribute attributeTargetMilestone = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.TARGET_MILESTONE.getKey());
					if (attributeTargetMilestone != null) {
						List<String> optionValues = repositoryConfiguration.getProductOptionValues(
								BugzillaAttribute.TARGET_MILESTONE, taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeTargetMilestone.clearOptions();
						for (String option : optionValues) {
							attributeTargetMilestone.putOption(option, option);
						}
						String defaultMilestones = repositoryConfiguration.getDefaultMilestones(taskAttribute.getValue());
						if (defaultMilestones != null) {
							attributeTargetMilestone.setValue(defaultMilestones);
						} else if (optionValues.size() == 1) {
							attributeTargetMilestone.setValue(optionValues.get(0));
						} else {
							attributeTargetMilestone.setValue("---"); //$NON-NLS-1$
						}
						refresh(attributeTargetMilestone);
					}

					TaskAttribute attributeVersion = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.VERSION.getKey());
					if (attributeVersion != null) {
						List<String> optionValues = repositoryConfiguration.getProductOptionValues(
								BugzillaAttribute.VERSION, taskAttribute.getValue());
						Collections.sort(optionValues);
						attributeVersion.clearOptions();
						for (String option : optionValues) {
							attributeVersion.putOption(option, option);
						}
						if (optionValues.size() == 1) {
							attributeVersion.setValue(optionValues.get(0));
						} else {
							attributeVersion.setValue("unspecified"); //$NON-NLS-1$
						}
						refresh(attributeVersion);
					}

					TaskAttribute attributeDefaultAssignee = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.SET_DEFAULT_ASSIGNEE.getKey());
					if (attributeDefaultAssignee != null) {
						attributeDefaultAssignee.setValue("1"); //$NON-NLS-1$
						refresh(attributeDefaultAssignee);
					}
					if (taskAttribute.getTaskData().isNew()) {
						BugzillaVersion bugzillaVersion = repositoryConfiguration.getInstallVersion();
						if (bugzillaVersion == null) {
							bugzillaVersion = BugzillaVersion.MIN_VERSION;
						}
						if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) >= 0) {
							AbstractTaskEditorPart part = getPart(ID_PART_ACTIONS);
							boolean unconfirmedAllowed = repositoryConfiguration.getUnconfirmedAllowed(taskAttribute.getValue());
							TaskAttribute unconfirmedAttribute = taskAttribute.getTaskData()
									.getRoot()
									.getAttribute(
											TaskAttribute.PREFIX_OPERATION + BugzillaOperation.unconfirmed.toString());
							if (unconfirmedAttribute != null) {
								unconfirmedAttribute.getMetaData().setReadOnly(!unconfirmedAllowed);
							}
							if (part != null) {
								TaskEditorActionPart actionPart = (TaskEditorActionPart) part;
								actionPart.refreshOperations();
							}
						}
					}
/*
 * 					add confirm_product_change to avoid verification page on submit
 */
					TaskAttribute attributeConfirmeProductChange = taskAttribute.getTaskData()
							.getRoot()
							.getMappedAttribute(BugzillaAttribute.CONFIRM_PRODUCT_CHANGE.getKey());
					if (attributeConfirmeProductChange == null) {
						attributeConfirmeProductChange = BugzillaTaskDataHandler.createAttribute(
								taskAttribute.getTaskData().getRoot(), BugzillaAttribute.CONFIRM_PRODUCT_CHANGE);
					}
					if (attributeConfirmeProductChange != null) {
						attributeConfirmeProductChange.setValue("1"); //$NON-NLS-1$
					}
				}
			}
		}
	}

	@Override
	protected void handleTaskSubmitted(SubmitJobEvent event) {
		if (event.getJob().getStatus() != null) {
			switch (event.getJob().getStatus().getCode()) {
			case BugzillaStatus.ERROR_CONFIRM_MATCH:
			case BugzillaStatus.ERROR_MATCH_FAILED:
				showError((BugzillaStatus) event.getJob().getStatus());
				return;
			}
		}
		if (event.getJob().getResponse() != null && event.getJob().getResponse() instanceof BugzillaRepositoryResponse) {
			final RepositoryResponse response = event.getJob().getResponse();
			if (response instanceof BugzillaRepositoryResponse) {
				final BugzillaRepositoryResponse bugzillaResponse = (BugzillaRepositoryResponse) response;
				if (bugzillaResponse.getResponseData().size() > 0) {
					getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Changes_Submitted_Message,
							IMessageProvider.INFORMATION, new HyperlinkAdapter() {
								@Override
								public void linkActivated(HyperlinkEvent event) {
									showSubmitResponse(bugzillaResponse);
								}

							});
				} else {
					getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Changes_Submitted_Message,
							IMessageProvider.INFORMATION);
				}
			}
		} else {
			super.handleTaskSubmitted(event);
		}
	}

	@Override
	public void refresh() {
		super.refresh();
		checkCanSubmit(IMessageProvider.INFORMATION);
	}

	private boolean checkCanSubmit(final int type) {
		final TaskRepository taskRepository = getModel().getTaskRepository();
		String username = taskRepository.getUserName();
		if (username == null || username.length() == 0) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_Anonymous_can_not_submit_Tasks, type,
							new HyperlinkAdapter() {
								@Override
								public void linkActivated(HyperlinkEvent e) {
									TasksUiUtil.openEditRepositoryWizard(taskRepository);
									refresh();
								}
							});
				}
			});
			return false;
		}
		if (!getModel().getTaskData().isNew()) {
			TaskAttribute exporter = getModel().getTaskData()
					.getRoot()
					.getAttribute(BugzillaAttribute.EXPORTER_NAME.getKey());
			if (exporter == null || exporter.getValue().equals("")) { //$NON-NLS-1$
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getTaskEditor().setMessage(Messages.BugzillaTaskEditorPage_submit_disabled_please_refresh,
								type, new HyperlinkAdapter() {
									@Override
									public void linkActivated(HyperlinkEvent e) {
										TasksUiUtil.openEditRepositoryWizard(taskRepository);
										refresh();
									}
								});
					}
				});
				return false;
			}
		}
		return true;
	}

	private void showError(BugzillaStatus bugzillaStatus) {
		int count = 0;
		BugzillaUserMatchResponse response = bugzillaStatus.getUserMatchResponse();
		FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
		FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		FieldDecoration fieldDecorationWarning = registry.getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		StringBuilder fields = new StringBuilder();
		StringBuilder detail = new StringBuilder();

		count += decorateControlsAndUpdateMessages(response.getAssignedToMsg(), response.getAssignedToProposals(),
				getModel().getTaskData().getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()), fields,
				detail, fieldDecoration, fieldDecorationWarning);
		count += decorateControlsAndUpdateMessages(response.getQaContactMsg(), response.getQaContactProposals(),
				getModel().getTaskData().getRoot().getAttribute(BugzillaAttribute.QA_CONTACT.getKey()), fields, detail,
				fieldDecoration, fieldDecorationWarning);
		count += decorateControlsAndUpdateMessages(response.getNewCCMsg(), response.getNewCCProposals(),
				getModel().getTaskData().getRoot().getAttribute(BugzillaAttribute.NEWCC.getKey()), fields, detail,
				fieldDecoration, fieldDecorationWarning);
		updateTaskEditorPageMessageWithError(bugzillaStatus, detail.toString(), count == 1, fields.toString());
	}

	private int decorateControlsAndUpdateMessages(String message, List<String> proposals, TaskAttribute attribute,
			StringBuilder fields, StringBuilder detail, FieldDecoration fieldDecoration,
			FieldDecoration fieldDecorationWarning) {
		if (attribute == null || (message == null && proposals.size() == 0)) {
			return 0;
		}
		Map<String, String> newPersonProposalMap = new HashMap<String, String>();
		if (fields.length() > 0) {
			fields.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_Error_Label_N, attribute.getMetaData()
					.getLabel()));
		} else {
			fields.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_Error_Label_1, attribute.getMetaData()
					.getLabel()));
		}
		detail.append(attribute.getMetaData().getLabel() + "\n"); //$NON-NLS-1$
		if (message != null && !message.equals("")) { //$NON-NLS-1$
			detail.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_DetailLine, message));
		} else {
			for (String proposalValue : proposals) {
				detail.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_DetailLine, proposalValue));
				newPersonProposalMap.put(proposalValue, proposalValue);
			}
		}
		AbstractAttributeEditor editor = getEditorForAttribute(attribute);
		if (editor != null) {
			decorateEditorWithError(fieldDecoration, fieldDecorationWarning, message, newPersonProposalMap, editor);
		}
		return 1;
	}

	private int decorateControlsAndUpdateMessages(String message, Map<String, List<String>> proposals,
			TaskAttribute attribute, StringBuilder fields, StringBuilder detail, FieldDecoration fieldDecoration,
			FieldDecoration fieldDecorationWarning) {
		if (attribute == null || (message == null && proposals.size() == 0)) {
			return 0;
		}
		Map<String, String> newPersonProposalMap = new HashMap<String, String>();
		if (fields.length() > 0) {
			fields.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_Error_Label_N, attribute.getMetaData()
					.getLabel()));
		} else {
			fields.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_Error_Label_1, attribute.getMetaData()
					.getLabel()));
		}
		detail.append(attribute.getMetaData().getLabel() + "\n"); //$NON-NLS-1$
		if (message != null && !message.equals("")) { //$NON-NLS-1$
			detail.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_DetailLine, message));
		} else {
			for (String key : proposals.keySet()) {
				detail.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_Proposal_Detail, key));

				for (String proposalValue : proposals.get(key)) {
					detail.append(MessageFormat.format(Messages.BugzillaTaskEditorPage_DetailLine, proposalValue));
					newPersonProposalMap.put(proposalValue, proposalValue);
				}
			}
		}
		AbstractAttributeEditor editor = getEditorForAttribute(attribute);
		if (editor != null) {
			decorateEditorWithError(fieldDecoration, fieldDecorationWarning, message, newPersonProposalMap, editor);
		}
		return 1;
	}

	/**
	 * @param bugzillaStatus
	 * @param resultDetail
	 * @param oneError
	 * @param fieldString
	 */
	private void updateTaskEditorPageMessageWithError(BugzillaStatus bugzillaStatus, String resultDetail,
			boolean oneError, String fieldString) {
		String resultString;
		final String titleString;
		switch (bugzillaStatus.getCode()) {
		case BugzillaStatus.ERROR_CONFIRM_MATCH:
			if (oneError) {
				resultString = MessageFormat.format(Messages.BugzillaTaskEditorPage_Message_one,
						Messages.BugzillaTaskEditorPage_Confirm, fieldString);
			} else {
				resultString = MessageFormat.format(Messages.BugzillaTaskEditorPage_Message_more,
						Messages.BugzillaTaskEditorPage_Confirm, fieldString);
			}
			titleString = Messages.BugzillaTaskEditorPage_ConfirmDetailTitle;
			break;
		case BugzillaStatus.ERROR_MATCH_FAILED:
			if (oneError) {
				resultString = MessageFormat.format(Messages.BugzillaTaskEditorPage_Message_one,
						Messages.BugzillaTaskEditorPage_Error, fieldString);
			} else {
				resultString = MessageFormat.format(Messages.BugzillaTaskEditorPage_Message_more,
						Messages.BugzillaTaskEditorPage_Error, fieldString);
			}
			titleString = Messages.BugzillaTaskEditorPage_ErrorDetailTitle;
			break;
		default:
			throw new RuntimeException("unexpected BugzillaStatus: " + bugzillaStatus.getCode()); //$NON-NLS-1$
		}

		final String resultDetailString = resultDetail;
		getTaskEditor().setMessage(resultString, IMessageProvider.ERROR, new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				BugzillaResponseDetailDialog dialog = new BugzillaResponseDetailDialog(WorkbenchUtil.getShell(),
						titleString, resultDetailString);
				dialog.open();
			}
		});
	}

	/**
	 * @param fieldDecoration
	 * @param message
	 * @param newPersonProposalMap
	 * @param editor
	 */
	private void decorateEditorWithError(FieldDecoration fieldDecoration, FieldDecoration fieldDecorationWarning,
			String message, Map<String, String> newPersonProposalMap, AbstractAttributeEditor editor) {
		final Control control = editor.getControl();

		final ControlDecoration decoration = new ControlDecoration(control, SWT.LEFT | SWT.DOWN);
		decoration.setImage(newPersonProposalMap.size() == 1
				? fieldDecorationWarning.getImage()
				: fieldDecoration.getImage());
		IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getService(IBindingService.class);
		if (message != null && !message.equals("")) { //$NON-NLS-1$
			decoration.setDescriptionText(message);
			errorDecorations.add(decoration);
		} else {
			decoration.setDescriptionText(NLS.bind(
					Messages.BugzillaTaskEditorPage_Content_Assist_for_Error_Available,
					bindingService.getBestActiveBindingFormattedFor(ContentAssistCommandAdapter.CONTENT_PROPOSAL_COMMAND)));
			errorDecorations.add(decoration);

			final PersonAttributeEditor personEditor = ((PersonAttributeEditor) editor);
			final PersonProposalProvider personProposalProvider = (PersonProposalProvider) personEditor.getContentAssistCommandAdapter()
					.getContentProposalProvider();
			personProposalProvider.setErrorProposals(newPersonProposalMap);

			editorsWithError.add(personEditor);
			if (newPersonProposalMap.size() == 1) {
				personEditor.setValue(newPersonProposalMap.keySet().iterator().next());

			}
			personEditor.getText().addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					decoration.hide();
					errorDecorations.remove(decoration);
					decoration.dispose();
					personProposalProvider.setErrorProposals(null);
				}
			});
		}
	}

	/**
	 * @param bugzillaResponse
	 */
	private void showSubmitResponse(final BugzillaRepositoryResponse bugzillaResponse) {
		StringBuilder message = new StringBuilder();
		for (String iterable_map : bugzillaResponse.getResponseData().keySet()) {
			if (message.length() > 0) {
				message.append("\n"); //$NON-NLS-1$
			}
			message.append(NLS.bind(Messages.BugzillaTaskEditorPage_Bug_Line, iterable_map));
			Map<String, List<String>> responseMap = bugzillaResponse.getResponseData().get(iterable_map);
			for (String iterable_list : responseMap.keySet()) {
				message.append(NLS.bind(Messages.BugzillaTaskEditorPage_Action_Line, iterable_list));
				List<String> responseList = responseMap.get(iterable_list);
				for (String string : responseList) {
					message.append(NLS.bind(Messages.BugzillaTaskEditorPage_Email_Line, string));
				}
			}
		}
		BugzillaResponseDetailDialog dialog = new BugzillaResponseDetailDialog(WorkbenchUtil.getShell(),
				Messages.BugzillaTaskEditorPage_submitted_Changes_Details, message.toString());
		dialog.open();
	}

}
