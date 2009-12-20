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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaFlag;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaFlagMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.TaskAttachmentPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Frank Becker
 */
public class BugzillaTaskAttachmentPage extends TaskAttachmentPage {

	private final List<BugzillaFlag> flagAttributes = new ArrayList<BugzillaFlag>();

	private final FormToolkit toolkit;

	public BugzillaTaskAttachmentPage(TaskAttachmentModel model) {
		super(model);
		toolkit = new FormToolkit(Display.getCurrent());
	}

	public void createFlagTab(Composite composite) {
		BugzillaRepositoryConnector connector = (BugzillaRepositoryConnector) TasksUi.getRepositoryConnector(getModel().getTaskRepository()
				.getConnectorKind());
		RepositoryConfiguration configuration = connector.getRepositoryConfiguration(getModel().getTaskRepository()
				.getRepositoryUrl());
		if (configuration != null) {
			List<BugzillaFlag> flags = configuration.getFlags();
			TaskAttribute productAttribute = getModel().getAttribute().getTaskData().getRoot().getMappedAttribute(
					BugzillaAttribute.PRODUCT.getKey());
			TaskAttribute componentAttribute = getModel().getAttribute().getTaskData().getRoot().getMappedAttribute(
					BugzillaAttribute.COMPONENT.getKey());
			Composite flagComposite = null;
			for (BugzillaFlag bugzillaFlag : flags) {
				if (bugzillaFlag.getType().equals("bug")) { //$NON-NLS-1$
					continue;
				}
				if (!bugzillaFlag.isUsedIn(productAttribute.getValue(), componentAttribute.getValue())) {
					continue;
				}

				if (flagComposite == null) {
					flagComposite = createFlagSection(composite);
				}
				BugzillaFlagMapper mapper = new BugzillaFlagMapper(connector);
				mapper.setRequestee(""); //$NON-NLS-1$
				mapper.setSetter(""); //$NON-NLS-1$
				mapper.setState(" "); //$NON-NLS-1$
				mapper.setFlagId(bugzillaFlag.getName());
				mapper.setNumber(0);
				final TaskAttribute attribute = getModel().getAttribute().createAttribute(
						"task.common.kind.flag_type" + bugzillaFlag.getFlagId()); //$NON-NLS-1$
				mapper.applyTo(attribute);

				Label flagLiteral = new Label(flagComposite, SWT.NONE);
				flagLiteral.setText("" + bugzillaFlag.getName()); //$NON-NLS-1$
				flagLiteral.setToolTipText(bugzillaFlag.getDescription());
				flagAttributes.add(bugzillaFlag);
				final Combo flagState = new Combo(flagComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
				flagState.add(" "); //$NON-NLS-1$
				if (bugzillaFlag.isRequestable()) {
					flagState.add("?"); //$NON-NLS-1$
				}
				flagState.add("+"); //$NON-NLS-1$
				flagState.add("-"); //$NON-NLS-1$
				if (bugzillaFlag.isRequestable() && bugzillaFlag.isSpecifically_requestable()) {
					flagState.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 1, 1));
					final Text requesteeText = new Text(flagComposite, SWT.BORDER);
					GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
					gd.widthHint = 40;
					requesteeText.setLayoutData(gd);
					requesteeText.setEditable(false);
					requesteeText.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							TaskAttribute requesteeAttribute = attribute.getAttribute("requestee"); //$NON-NLS-1$
							if (requesteeAttribute != null) {
								String value = requesteeText.getText().trim();
								requesteeAttribute.setValue(value);
							}
						}
					});
					flagState.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							// ignore
						}

						public void widgetSelected(SelectionEvent e) {
							TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
							if (state != null) {
								String value = flagState.getItem(flagState.getSelectionIndex());
								state.setValue(value);
								requesteeText.setEditable(value.equals("?")); //$NON-NLS-1$
							}
						}
					});
				} else {
					flagState.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false, 2, 1));
					flagState.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							// ignore
						}

						public void widgetSelected(SelectionEvent e) {
							TaskAttribute state = attribute.getAttribute("state"); //$NON-NLS-1$
							String value = flagState.getItem(flagState.getSelectionIndex());
							if (state != null && value != null) {
								state.setValue(value);
							}
						}
					});
				}
			}
		}
	}

	@Override
	protected void createAdditionalControls(Composite composite) {

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		g.horizontalSpan = 3;
		tabFolder.setLayoutData(g);
		tabFolder.setLayout(layout);

		Composite generalTab = new Composite(tabFolder, SWT.NULL);
		generalTab.setLayout(new GridLayout(3, false));
		generalTab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Dialog.applyDialogFont(generalTab);
		createContentControls(generalTab);
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("General"); //$NON-NLS-1$
		item.setControl(generalTab);

		Composite flagTab = new Composite(tabFolder, SWT.NULL);
		flagTab.setLayout(new GridLayout(3, false));
		flagTab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Dialog.applyDialogFont(flagTab);
		createFlagTab(flagTab);
		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Flags"); //$NON-NLS-1$
		item.setControl(flagTab);
	}

	private Composite createFlagSection(Composite container) {
		ScrolledForm scrollComposite1 = new ScrolledForm(container, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		scrollComposite1.setExpandHorizontal(true);
		scrollComposite1.setExpandVertical(true);
		scrollComposite1.setBackground(container.getBackground());
		scrollComposite1.setForeground(container.getForeground());
		scrollComposite1.setFont(JFaceResources.getHeaderFont());
		GridData g0 = new GridData(GridData.FILL_HORIZONTAL);
		g0.heightHint = 75;
		g0.widthHint = 100;
		g0.verticalIndent = -5;
		scrollComposite1.setLayoutData(g0);

		toolkit.paintBordersFor(scrollComposite1);
		scrollComposite1.getBody().setLayout(new GridLayout());
		Composite scrolledComposite = scrollComposite1.getBody();
		scrolledComposite.setFont(container.getFont());
		scrolledComposite.setBackground(container.getBackground());
		scrolledComposite.setLayout(new GridLayout(3, false));
		GridData g1 = new GridData(GridData.FILL_HORIZONTAL);
		g1.heightHint = 300;
		g1.widthHint = 100;
		scrolledComposite.setLayoutData(g1);
		return scrolledComposite;
	}

	@Override
	public void dispose() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		super.dispose();
	}

}
