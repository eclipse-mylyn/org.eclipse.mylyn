/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Tomasz Zarna <tzarna@gmail.com> - bug 361536
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Frank Becker
 */
public class BugzillaTaskAttachmentPage extends TaskAttachmentPage {

	private final List<BugzillaFlag> flagAttributes = new ArrayList<BugzillaFlag>();

	private final FormToolkit toolkit;

	private ExpandableComposite flagExpandComposite = null;

	private Composite flagScrollComposite;

	private SectionComposite scrolledComposite;

	private Composite scrolledBodyComposite;

	public BugzillaTaskAttachmentPage(TaskAttachmentModel model) {
		super(model);
		toolkit = new FormToolkit(Display.getCurrent());
	}

	private void updateScrolledCompositeSize() {
		Point p = scrolledBodyComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		scrolledComposite.setMinSize(p);
		Shell shell = getShell();
		shell.pack();
		Point shellSize = shell.getSize();
		shellSize.x++;
		shell.setSize(shellSize);
		shellSize.x--;
		shell.setSize(shellSize);
		shell.pack();
	}

	private void createScrolledComposite(Composite parent) {
		scrolledComposite = new SectionComposite(parent, SWT.NONE) {
			@Override
			public Point computeSize(int hint, int hint2, boolean changed) {
				return new Point(64, 64);
			}
		};
		scrolledBodyComposite = scrolledComposite.getContent();
		scrolledBodyComposite.setLayout(new GridLayout());
		scrolledComposite.setContent(scrolledBodyComposite);
		setControl(scrolledComposite);
		Dialog.applyDialogFont(scrolledBodyComposite);
	}

	private Composite getScrolledBodyComposite() {
		return scrolledBodyComposite;
	}

	@Override
	public void createControl(Composite parent) {
		createScrolledComposite(parent);
		updateScrolledCompositeSize();
		super.createControl(getScrolledBodyComposite());
		BugzillaRepositoryConnector connector = (BugzillaRepositoryConnector) TasksUi.getRepositoryConnector(getModel().getTaskRepository()
				.getConnectorKind());
		RepositoryConfiguration configuration = connector.getRepositoryConfiguration(getModel().getTaskRepository()
				.getRepositoryUrl());
		if (configuration != null) {
			List<BugzillaFlag> flags = configuration.getFlags();
			TaskAttribute productAttribute = getModel().getAttribute()
					.getTaskData()
					.getRoot()
					.getMappedAttribute(BugzillaAttribute.PRODUCT.getKey());
			TaskAttribute componentAttribute = getModel().getAttribute()
					.getTaskData()
					.getRoot()
					.getMappedAttribute(BugzillaAttribute.COMPONENT.getKey());
			Control[] children = getScrolledBodyComposite().getChildren();
			Composite pageComposite = (Composite) children[children.length - 1];
			Composite flagComposite = null;
			for (BugzillaFlag bugzillaFlag : flags) {
				if (bugzillaFlag.getType().equals("bug")) { //$NON-NLS-1$
					continue;
				}
				if (!bugzillaFlag.isUsedIn(productAttribute.getValue(), componentAttribute.getValue())) {
					continue;
				}

				if (flagComposite == null) {
					flagComposite = createFlagSection(pageComposite);
				}
				BugzillaFlagMapper mapper = new BugzillaFlagMapper(connector);
				mapper.setRequestee(""); //$NON-NLS-1$
				mapper.setSetter(""); //$NON-NLS-1$
				mapper.setState(" "); //$NON-NLS-1$
				mapper.setFlagId(bugzillaFlag.getName());
				mapper.setNumber(0);
				final TaskAttribute attribute = getModel().getAttribute().createAttribute(
						BugzillaAttribute.KIND_FLAG_TYPE + bugzillaFlag.getFlagId());
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
					requesteeText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
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
		updateScrolledCompositeSize();
	}

	private Composite createFlagSection(Composite container) {
		flagExpandComposite = toolkit.createExpandableComposite(container, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		flagExpandComposite.setFont(container.getFont());
		flagExpandComposite.setBackground(container.getBackground());
		flagExpandComposite.setText(Messages.BugzillaTaskAttachmentPage_Advanced);
		flagExpandComposite.setLayout(new GridLayout(3, false));
		GridData g = new GridData(SWT.FILL, SWT.TOP, true, false);
		g.horizontalSpan = 3;
		flagExpandComposite.setLayoutData(g);
		flagExpandComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				updateScrolledCompositeSize();
				getControl().getShell().pack();
			}
		});

		flagScrollComposite = new Composite(flagExpandComposite, SWT.NONE);
		flagScrollComposite.setLayout(new GridLayout(3, false));
		flagExpandComposite.setClient(flagScrollComposite);
		return flagScrollComposite;
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
