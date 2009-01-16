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
package org.eclipse.mylyn.internal.provisional.tasks.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
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
 * @since 3.1
 */
public abstract class AbstractRepositoryQueryPage2 extends AbstractRepositoryQueryPage {

	private Text titleText;

	private Button updateButton;

	private boolean firstTime = true;

	private final AbstractRepositoryConnector connector;

	private boolean needsRepositoryConfiguration = true;

	public AbstractRepositoryQueryPage2(String pageName, TaskRepository repository, IRepositoryQuery query) {
		super(pageName, repository, query);
		this.connector = TasksUi.getRepositoryConnector(getTaskRepository().getConnectorKind());
		setTitle(Messages.AbstractRepositoryQueryPage2_Enter_query_parameters);
		setDescription(Messages.AbstractRepositoryQueryPage2_If_attributes_are_blank_or_stale_press_the_Update_button);
	}

	public void setNeedsRepositoryConfiguration(boolean needsRepositoryConfiguration) {
		this.needsRepositoryConfiguration = needsRepositoryConfiguration;
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

		Composite innerComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).applyTo(innerComposite);
		innerComposite.setLayout(new FillLayout());
		createPageContent(innerComposite);

		if (needsRepositoryConfiguration) {
			createUpdateButton(composite);
		}

		if (getQuery() != null) {
			titleText.setText(getQuery().getSummary());
			restoreState(getQuery());
		}

		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	protected abstract void createPageContent(Composite parent);

	private void createTitleGroup(Composite control) {
		if (inSearchContainer()) {
			return;
		}

		Label titleLabel = new Label(control, SWT.NONE);
		titleLabel.setText(Messages.AbstractRepositoryQueryPage2__Title_);

		titleText = new Text(control, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(titleText);
		titleText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// ignore
			}

			public void keyReleased(KeyEvent e) {
				getContainer().updateButtons();
			}
		});
	}

	private Control createUpdateButton(final Composite control) {
		Composite composite = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).span(2, 1).applyTo(composite);

		updateButton = new Button(composite, SWT.PUSH);
		updateButton.setText(Messages.AbstractRepositoryQueryPage2__Refresh_From_Repository);
		updateButton.setLayoutData(new GridData());
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getTaskRepository() != null) {
					updateAttributesFromRepository(true);
				} else {
					MessageDialog.openInformation(
							Display.getCurrent().getActiveShell(),
							Messages.AbstractRepositoryQueryPage2_Update_Attributes_Failed,
							Messages.AbstractRepositoryQueryPage2_No_repository_available_please_add_one_using_the_Task_Repositories_view);
				}
			}
		});

		return composite;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(true);
		}

		if (visible && firstTime) {
			firstTime = false;
			if (!hasRepositoryConfiguration() && needsRepositoryConfiguration) {
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

	private void initializePage() {
		if (needsRepositoryConfiguration) {
			updateAttributesFromRepository(false);
		}
		boolean restored = (getQuery() != null);
		if (inSearchContainer()) {
			restored |= restoreState(null);
		}
		if (!restored) {
			// initialize with default values
		}
	}

	protected abstract boolean hasRepositoryConfiguration();

	protected AbstractRepositoryConnector getConnector() {
		return connector;
	}

	private void updateAttributesFromRepository(final boolean force) {
		if (!hasRepositoryConfiguration() || force) {
			setErrorMessage(null);
			try {
				IRunnableWithProgress runnable = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							connector.updateRepositoryConfiguration(getTaskRepository(), monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						} catch (OperationCanceledException e) {
							throw new InterruptedException();
						}
					}
				};

				if (getContainer() != null) {
					getContainer().run(true, true, runnable);
				} else if (getSearchContainer() != null) {
					getSearchContainer().getRunnableContext().run(true, true, runnable);
				} else {
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					service.busyCursorWhile(runnable);
				}
			} catch (InvocationTargetException e) {
				if (e.getCause() instanceof CoreException) {
					setErrorMessage(((CoreException) e.getCause()).getStatus().getMessage());
				} else {
					setErrorMessage(e.getCause().getMessage());
				}
				return;
			} catch (InterruptedException e) {
				return;
			}
		}

		doRefresh();
	}

	protected abstract void doRefresh();

	@Override
	public boolean isPageComplete() {
		if (titleText != null && titleText.getText().length() > 0) {
			return true;
		}
		setMessage(Messages.AbstractRepositoryQueryPage2_Enter_a_title);
		return false;
	}

	@Override
	public boolean performSearch() {
		if (inSearchContainer()) {
			saveState();
		}
		return super.performSearch();
	}

	protected abstract boolean restoreState(IRepositoryQuery query);

	@Override
	public String getQueryTitle() {
		return (titleText != null) ? titleText.getText() : null;
	}

	public void setQueryTitle(String text) {
		if (titleText != null) {
			titleText.setText(text);
		}
	}

}
