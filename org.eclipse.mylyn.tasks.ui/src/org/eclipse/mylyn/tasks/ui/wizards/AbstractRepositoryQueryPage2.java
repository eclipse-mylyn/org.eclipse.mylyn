/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.ui.ProgressContainer;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.mylyn.internal.tasks.ui.wizards.QueryWizardDialog;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Steffen Pingel
 * @author Frank Becker
 * @since 3.7
 */
public abstract class AbstractRepositoryQueryPage2 extends AbstractRepositoryQueryPage {

	private Button cancelButton;

	private final AbstractRepositoryConnector connector;

	private boolean firstTime = true;

	private SectionComposite innerComposite;

	/**
	 * Determines whether a 'Clear Fields' button is shown on the page.
	 */
	private boolean needsClear;

	/**
	 * Determines whether a 'Refresh' button is shown on the page.
	 */
	private boolean needsRefresh = true;

	private ProgressContainer progressContainer;

	private Button refreshButton;

	private Text titleText;

	public AbstractRepositoryQueryPage2(String pageName, TaskRepository repository, IRepositoryQuery query) {
		super(pageName, repository, query);
		this.connector = TasksUi.getRepositoryConnector(getTaskRepository().getConnectorKind());
		setTitle(Messages.AbstractRepositoryQueryPage2_Enter_query_parameters);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(composite);
		GridLayout layout = new GridLayout(2, false);
		if (inSearchContainer()) {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		composite.setLayout(layout);

		createTitleGroup(composite);

		innerComposite = new SectionComposite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).applyTo(innerComposite);
		createPageContent(innerComposite);

		createButtonGroup(composite);

		if (!needsRefresh) {
			setDescription(Messages.AbstractRepositoryQueryPage2_Create_a_Query_Page_Description);
		}

		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	@Override
	public String getQueryTitle() {
		return (titleText != null) ? titleText.getText() : null;
	}

	public boolean handleExtraButtonPressed(int buttonId) {
		if (buttonId == QueryWizardDialog.REFRESH_BUTTON_ID) {
			if (getTaskRepository() != null) {
				refreshConfiguration(true);
			} else {
				MessageDialog.openInformation(
						Display.getCurrent().getActiveShell(),
						Messages.AbstractRepositoryQueryPage2_Update_Attributes_Failed,
						Messages.AbstractRepositoryQueryPage2_No_repository_available_please_add_one_using_the_Task_Repositories_view);
			}
			return true;
		} else if (buttonId == QueryWizardDialog.CLEAR_BUTTON_ID) {
			doClearControls();
			return true;
		}
		return false;
	}

	@Override
	public boolean isPageComplete() {
		if (titleText != null && titleText.getText().length() > 0) {
			return true;
		}
		setMessage(Messages.AbstractRepositoryQueryPage2_Enter_a_title);
		return false;
	}

	public boolean needsClear() {
		return needsClear;
	}

	public boolean needsRefresh() {
		return needsRefresh;
	}

	@Override
	public boolean performSearch() {
		if (inSearchContainer()) {
			saveState();
		}
		return super.performSearch();
	}

	@Override
	public void saveState() {
		if (inSearchContainer()) {
			RepositoryQuery query = new RepositoryQuery(getTaskRepository().getConnectorKind(), "handle"); //$NON-NLS-1$
			applyTo(query);

			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				settings.put(getSavedStateSettingKey(), query.getUrl());
			}
		}
	}

	public void setExtraButtonState(Button button) {
		Integer obj = (Integer) button.getData();
		if (obj == QueryWizardDialog.REFRESH_BUTTON_ID) {
			if (needsRefresh) {
				if (!button.isVisible()) {
					button.setVisible(true);
				}
				button.setEnabled(true);
			} else {
				if (button != null && button.isVisible()) {
					button.setVisible(false);
				}
			}
		} else if (obj == QueryWizardDialog.CLEAR_BUTTON_ID) {
			if (!button.isVisible()) {
				button.setVisible(true);
			}
			button.setEnabled(true);
		}

	}

	public void setNeedsClear(boolean needsClearButton) {
		this.needsClear = needsClearButton;
	}

	public void setNeedsRefresh(boolean needsRefresh) {
		this.needsRefresh = needsRefresh;
	}

	public void setQueryTitle(String text) {
		if (titleText != null) {
			titleText.setText(text);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(true);
		}

		if (visible && firstTime) {
			firstTime = false;
			if (!hasRepositoryConfiguration() && needsRefresh) {
				// delay the execution so the dialog's progress bar is visible
				// when the attributes are updated
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (getControl() != null && !getControl().isDisposed()) {
							initializePage();
						}
					}
				});
			} else {
				// no remote connection is needed to get attributes therefore do
				// not use delayed execution to avoid flickering
				initializePage();
			}
		}
	}

	private void createButtonGroup(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(2, 1).applyTo(buttonComposite);
		createButtons(buttonComposite);
		if (buttonComposite.getChildren().length > 0) {
			layout.numColumns = buttonComposite.getChildren().length;
		} else {
			// remove composite to avoid spacing
			buttonComposite.dispose();
		}
	}

	private void createTitleGroup(Composite control) {
		if (inSearchContainer()) {
			return;
		}

		Label titleLabel = new Label(control, SWT.NONE);
		titleLabel.setText(Messages.AbstractRepositoryQueryPage2__Title_);

		titleText = new Text(control, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(titleText);
		titleText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
			}
		});
	}

	private void initializePage() {
		if (needsRefresh) {
			boolean refreshed = refreshConfiguration(false);
			if (!refreshed) {
				// always do a refresh when page is initially shown
				if (!innerComposite.isDisposed()) {
					doRefreshControls();
				}
			}
		}
		boolean restored = false;
		if (getQuery() != null) {
			titleText.setText(getQuery().getSummary());
			restored |= restoreState(getQuery());
		} else if (inSearchContainer()) {
			restored |= restoreSavedState();
		}
		if (!restored) {
			// initialize with default values
			if (!innerComposite.isDisposed()) {
				doClearControls();
			}
		}
	}

	protected boolean refreshConfiguration(final boolean force) {
		if (force || !hasRepositoryConfiguration()) {
			setErrorMessage(null);
			try {
				doRefreshConfiguration();
				if (!innerComposite.isDisposed()) {
					doRefreshControls();
				}
				return true;
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof CoreException) {
					setErrorMessage(((CoreException) e.getCause()).getStatus().getMessage());
				} else {
					setErrorMessage(e.getCause().getMessage());
				}
			} catch (InterruptedException e) {
				// canceled
			}
		}
		return false;
	}

	private void doRefreshConfiguration() throws InvocationTargetException, InterruptedException {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor = SubMonitor.convert(monitor);
				monitor.beginTask(Messages.AbstractRepositoryQueryPage2_Refresh_Configuration_Button_Label,
						IProgressMonitor.UNKNOWN);
				try {
					connector.updateRepositoryConfiguration(getTaskRepository(), monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
					monitor.done();
				}
			}
		};
		if (getContainer() != null) {
			getContainer().run(true, true, runnable);
		} else if (progressContainer != null) {
			progressContainer.run(true, true, runnable);
		} else if (getSearchContainer() != null) {
			getSearchContainer().getRunnableContext().run(true, true, runnable);
		} else {
			IProgressService service = PlatformUI.getWorkbench().getProgressService();
			service.busyCursorWhile(runnable);
		}
	}

	protected void createButtons(final Composite composite) {
		if (getContainer() instanceof QueryWizardDialog) {
			// refresh and clear buttons are provided by the dialog
			return;
		}
		if (needsRefresh) {
			refreshButton = new Button(composite, SWT.PUSH);
			refreshButton.setText(Messages.AbstractRepositoryQueryPage2__Refresh_From_Repository);
			refreshButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (getTaskRepository() != null) {
						refreshConfiguration(true);
					} else {
						MessageDialog.openInformation(
								Display.getCurrent().getActiveShell(),
								Messages.AbstractRepositoryQueryPage2_Update_Attributes_Failed,
								Messages.AbstractRepositoryQueryPage2_No_repository_available_please_add_one_using_the_Task_Repositories_view);
					}
				}
			});
		}
		if (needsClear) {
			Button clearButton = new Button(composite, SWT.PUSH);
			clearButton.setText(Messages.AbstractRepositoryQueryPage2_Clear_Fields);
			clearButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doClearControls();
				}
			});
		}
		final ProgressMonitorPart progressMonitorPart = new ProgressMonitorPart(composite, null);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(progressMonitorPart);
		progressMonitorPart.setVisible(false);
		progressContainer = new ProgressContainer(composite.getShell(), progressMonitorPart) {
			@Override
			protected void restoreUiState(java.util.Map<Object, Object> state) {
				cancelButton.setVisible(false);
				CommonUiUtil.setEnabled(innerComposite, true);
				for (Control control : composite.getChildren()) {
					if (control instanceof ProgressMonitorPart) {
						break;
					}
					control.setEnabled(true);
				}
			}

			@Override
			protected void saveUiState(java.util.Map<Object, Object> savedState) {
				CommonUiUtil.setEnabled(innerComposite, false);
				for (Control control : composite.getChildren()) {
					if (control instanceof ProgressMonitorPart) {
						break;
					}
					control.setEnabled(false);
				}
				cancelButton.setEnabled(true);
				cancelButton.setVisible(true);
			}
		};

		cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText(IDialogConstants.CANCEL_LABEL);
		cancelButton.setVisible(false);
		progressContainer.setCancelButton(cancelButton);
	}

	protected abstract void createPageContent(SectionComposite parent);

	protected void doClearControls() {
	}

	protected abstract void doRefreshControls();

	protected AbstractRepositoryConnector getConnector() {
		return connector;
	}

	protected String getSavedStateSettingKey() {
		return getName() + "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
	}

	protected abstract boolean hasRepositoryConfiguration();

	protected boolean restoreSavedState() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			String queryUrl = settings.get(getSavedStateSettingKey());
			if (queryUrl != null) {
				RepositoryQuery query = new RepositoryQuery(getTaskRepository().getConnectorKind(), "handle"); //$NON-NLS-1$
				query.setUrl(queryUrl);
				return restoreState(query);
			}
		}
		return false;
	}

	protected abstract boolean restoreState(IRepositoryQuery query);

}
