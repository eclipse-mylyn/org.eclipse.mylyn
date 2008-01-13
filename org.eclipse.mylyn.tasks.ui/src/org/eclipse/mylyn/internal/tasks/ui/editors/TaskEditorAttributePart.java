/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.ResetRepositoryConfigurationAction;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class TaskEditorAttributePart extends AbstractTaskEditorPart {

	private boolean supportsRefresh;

	private final Section attributesSection;

	public TaskEditorAttributePart(AbstractTaskEditorPage taskEditorPage, Section attributesSection) {
		super(taskEditorPage);
		this.attributesSection = attributesSection;
	}

//	/**
//	 * Creates the attribute section, which contains most of the basic attributes of the task (some of which are
//	 * editable).
//	 */
//	protected void createAttributeLayout(Composite attributesComposite) {
//		int numColumns = ((GridLayout) attributesComposite.getLayout()).numColumns;
//		int currentCol = 1;
//
//		for (final RepositoryTaskAttribute attribute : taskData.getAttributes()) {
//			if (attribute.isHidden()) {
//				continue;
//			}
//
//			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//			data.horizontalSpan = 1;
//
//			if (attribute.hasOptions() && !attribute.isReadOnly()) {
//				Label label = createLabel(attributesComposite, attribute);
//				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
//				final CCombo attributeCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
//				toolkit.adapt(attributeCombo, true, true);
//				attributeCombo.setFont(TEXT_FONT);
//				attributeCombo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
//				if (hasChanged(attribute)) {
//					attributeCombo.setBackground(colorIncoming);
//				}
//				attributeCombo.setLayoutData(data);
//
//				List<String> values = attribute.getOptions();
//				if (values != null) {
//					for (String val : values) {
//						if (val != null) {
//							attributeCombo.add(val);
//						}
//					}
//				}
//
//				String value = attribute.getValue();
//				if (value == null) {
//					value = "";
//				}
//				if (attributeCombo.indexOf(value) != -1) {
//					attributeCombo.select(attributeCombo.indexOf(value));
//				}
//				attributeCombo.clearSelection();
//				attributeCombo.addSelectionListener(new SelectionAdapter() {
//					@Override
//					public void widgetSelected(SelectionEvent event) {
//						if (attributeCombo.getSelectionIndex() > -1) {
//							String sel = attributeCombo.getItem(attributeCombo.getSelectionIndex());
//							attribute.setValue(sel);
//							attributeChanged(attribute);
//							attributeCombo.clearSelection();
//						}
//					}
//				});
//				currentCol += 2;
//			} else {
//				Label label = createLabel(attributesComposite, attribute);
//				GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
//				Composite textFieldComposite = toolkit.createComposite(attributesComposite);
//				GridLayout textLayout = new GridLayout();
//				textLayout.marginWidth = 1;
//				textLayout.marginHeight = 2;
//				textFieldComposite.setLayout(textLayout);
//				GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//				textData.horizontalSpan = 1;
//				textData.widthHint = 135;
//
//				if (attribute.isReadOnly()) {
//					final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT | SWT.READ_ONLY);
//					text.setLayoutData(textData);
//				} else {
//					final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
//					// text.setFont(COMMENT_FONT);
//					text.setLayoutData(textData);
//					toolkit.paintBordersFor(textFieldComposite);
//					text.setData(attribute);
//
//					if (hasContentAssist(attribute)) {
//						ContentAssistCommandAdapter adapter = applyContentAssist(text,
//								createContentProposalProvider(attribute));
//
//						ILabelProvider propsalLabelProvider = createProposalLabelProvider(attribute);
//						if (propsalLabelProvider != null) {
//							adapter.setLabelProvider(propsalLabelProvider);
//						}
//						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
//					}
//				}
//
//				currentCol += 2;
//			}
//
//			if (currentCol > numColumns) {
//				currentCol -= numColumns;
//			}
//		}
//
//		// make sure that we are in the first column
//		if (currentCol > 1) {
//			while (currentCol <= numColumns) {
//				toolkit.createLabel(attributesComposite, "");
//				currentCol++;
//			}
//		}
//
//		toolkit.paintBordersFor(attributesComposite);
//	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite toolbarComposite = toolkit.createComposite(parent);
		toolbarComposite.setBackground(null);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		toolbarComposite.setLayout(rowLayout);
		ResetRepositoryConfigurationAction repositoryConfigRefresh = new ResetRepositoryConfigurationAction() {
			@Override
			public void performUpdate(TaskRepository repository, AbstractRepositoryConnector connector,
					IProgressMonitor monitor) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						getTaskEditorPage().setGlobalBusy(true);

					}
				});
				try {
					super.performUpdate(repository, connector, monitor);
					if (connector != null) {
						final AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
								TaskEditorAttributePart.this.getTaskRepository().getUrl(), getTaskData().getId());
						if (task != null) {
							TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true,
									new JobChangeAdapter() {

										@Override
										public void done(IJobChangeEvent event) {
											PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

												public void run() {
													getTaskEditorPage().refreshEditor();
												}
											});

										}
									});
						}
					}
				} catch (Exception e) {
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						public void run() {
							getTaskEditorPage().refreshEditor();
						}
					});
				}
			}
		};
		repositoryConfigRefresh.setImageDescriptor(TasksUiImages.REPOSITORY_SYNCHRONIZE);
		repositoryConfigRefresh.selectionChanged(new StructuredSelection(getTaskRepository()));
		repositoryConfigRefresh.setToolTipText("Refresh attributes");

		ToolBarManager barManager = new ToolBarManager(SWT.FLAT);
		barManager.add(repositoryConfigRefresh);
		repositoryConfigRefresh.setEnabled(supportsRefresh());
		barManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		barManager.createControl(toolbarComposite);
		attributesSection.setTextClient(toolbarComposite);

		// Attributes Composite- this holds all the combo fields and text fields
		final Composite attribComp = toolkit.createComposite(parent);
		attribComp.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Control focus = event.display.getFocusControl();
				if (focus instanceof Text && ((Text) focus).getEditable() == false) {
					getManagedForm().getForm().setFocus();
				}
			}
		});

		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 4;
		attribComp.setLayout(attributesLayout);

		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attribComp.setLayoutData(attributesData);

		setControl(attribComp);
	}

	public void setSupportsRefresh(boolean supportsRefresh) {
		this.supportsRefresh = supportsRefresh;
	}

	private boolean supportsRefresh() {
		return supportsRefresh;
	}

}
