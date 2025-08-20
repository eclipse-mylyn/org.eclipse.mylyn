/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.SupportRequest;
import org.eclipse.mylyn.tasks.bugs.IProduct;
import org.eclipse.mylyn.tasks.bugs.ITaskContribution;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Steffen Pingel
 */
public class ReportErrorPage extends WizardPage {

	private final IStatus status;

	private final List<AttributeTaskMapper> contributions;

	private AttributeTaskMapper selectedContribution;

	private Combo contributionCombo;

	public ReportErrorPage(SupportRequest request, IStatus status) {
		super("reportError"); //$NON-NLS-1$
		this.status = status;
		contributions = new ArrayList<>();
		addContributions(request.getContributions());
		setTitle(Messages.ReportErrorPage_Report_as_Bug);
		setMessage(MessageFormat.format(Messages.ReportErrorPage_AN_UNEXPETED_ERROR_HAS_OCCURED_IN_PLUGIN,
				status.getPlugin()));
	}

	private void addContributions(List<ITaskContribution> contributions) {
		for (ITaskContribution contribution : contributions) {
			if (((AttributeTaskMapper) contribution).isMappingComplete()) {
				this.contributions.add((AttributeTaskMapper) contribution);
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

//		Group errorGroup = new Group(composite, SWT.NONE);
//		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(errorGroup);
//		errorGroup.setText("Details");
//		errorGroup.setLayout(new GridLayout(1, true));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.ReportErrorPage_Details);

		Text text = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		text.setText(status.getMessage());
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).applyTo(text);

		// space
		label = new Label(composite, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(label);

		selectedContribution = null;
		if (!contributions.isEmpty()) {
			final Button defaultRepositoryButton = new Button(composite, SWT.RADIO);
			defaultRepositoryButton.setSelection(true);
			selectedContribution = contributions.get(0);
			if (contributions.size() == 1) {
				defaultRepositoryButton
						.setText(NLS.bind(Messages.ReportErrorPage_Report_to, getLabel(selectedContribution)));
				GridDataFactory.fillDefaults().span(2, 1).applyTo(defaultRepositoryButton);
			} else {
				defaultRepositoryButton.setText(Messages.ReportErrorPage_Report_to_Label);
				contributionCombo = new Combo(composite, SWT.READ_ONLY);
				for (AttributeTaskMapper contribution : contributions) {
					contributionCombo.add(getLabel(contribution));
				}
				contributionCombo.select(0);
				contributionCombo.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						int selectionIndex = contributionCombo.getSelectionIndex();
						if (selectionIndex != -1) {
							selectedContribution = contributions.get(selectionIndex);
						}
					}
				});
			}

			final Button selectRepositoryButton = new Button(composite, SWT.RADIO);
			selectRepositoryButton.setText(Messages.ReportErrorPage_Select_repository);
			GridDataFactory.fillDefaults().span(2, 1).applyTo(selectRepositoryButton);

			defaultRepositoryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (defaultRepositoryButton.getSelection()) {
						selectRepositoryButton.setSelection(false);
					}
					if (contributionCombo != null) {
						contributionCombo.setEnabled(true);
						selectedContribution = contributions.get(contributionCombo.getSelectionIndex());
					} else {
						selectedContribution = contributions.get(0);
					}
					getContainer().updateButtons();
				}
			});

			selectRepositoryButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (selectRepositoryButton.getSelection()) {
						defaultRepositoryButton.setSelection(false);
						if (contributionCombo != null) {
							contributionCombo.setEnabled(false);
						}
					}
					selectedContribution = null;
					getContainer().updateButtons();
				}
			});
		}
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private String getLabel(AttributeTaskMapper contribution) {
		IProduct product = contribution.getProduct();
		if (product.getName() != null) {
			return NLS.bind(Messages.ReportErrorPage_Xprovider_Xproduct, product.getProvider().getName(),
					product.getName());
		} else {
			return product.getProvider().getName();
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return selectedContribution == null;
	}

	public AttributeTaskMapper getSelectedContribution() {
		return selectedContribution;
	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

}
