/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.report.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.util.DateUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Ken Sueda
 */
public class PlanningGameEditorPart extends EditorPart {

	private PlanningGameEditorInput editorInput = null;
	private Table table;
	private TableViewer tableViewer;
	private String[] columnNames = new String[] { "Description", "Priority", "Date Completed", "Duration"};
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInputWithNotify(input);
		editorInput = (PlanningGameEditorInput)input;
		setPartName(editorInput.getName());
		setTitleToolTip(editorInput.getToolTipText());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sform = toolkit.createScrolledForm(parent);
		sform.getBody().setLayout(new TableWrapLayout());
		Composite editorComposite = sform.getBody();
		
		createSummarySection(editorComposite, toolkit);
		createDetailSection(editorComposite, toolkit);
	}

	@Override
	public void setFocus() {		
	}

	private void createSummarySection(Composite parent, FormToolkit toolkit) {
		Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		summarySection.setText("Planning Game Summary");			
		summarySection.setLayout(new TableWrapLayout());
		summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
		Composite summaryContainer = toolkit.createComposite(summarySection);
		summarySection.setClient(summaryContainer);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;						
		summaryContainer.setLayout(layout);
		
		int length = editorInput.getListSize();
		String numComplete = "Number of completed tasks: " + editorInput.getListSize();
		Label label = toolkit.createLabel(summaryContainer, numComplete, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		String avgTime = "Average time spent: ";
		if (length > 0) {
			avgTime =  avgTime + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpent() / editorInput.getListSize());		
		} else {
			avgTime =  avgTime + 0;
		}
		label = toolkit.createLabel(summaryContainer, avgTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		String totalTime = "Total time spent: " + DateUtil.getFormattedDuration(editorInput.getTotalTimeSpent());
		label = toolkit.createLabel(summaryContainer, totalTime, SWT.NULL);
		label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	}
	
	private void createDetailSection(Composite parent, FormToolkit toolkit) {
		Section detailSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		detailSection.setText("Completed Tasks Details");			
		detailSection.setLayout(new TableWrapLayout());
		detailSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
		Composite detailContainer = toolkit.createComposite(detailSection);
		detailSection.setClient(detailContainer);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;						
		detailContainer.setLayout(layout);
		
		createTable(detailContainer, toolkit);
		createTableViewer();
	}
	
	private void createTable(Composite parent, FormToolkit toolkit) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = toolkit.createTable(parent, style );		
		TableLayout tlayout = new TableLayout();
		table.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 300;
		wd.grabVertical = true;
		table.setLayoutData(wd);
				
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(columnNames[0]);
		column.setWidth(300);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new PlanningGameSorter(PlanningGameSorter.DESCRIPTION));

			}
		});

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(columnNames[1]);
		column.setWidth(50);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new PlanningGameSorter(PlanningGameSorter.PRIORITY));
			}
		});

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(columnNames[2]);
		column.setWidth(200);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new PlanningGameSorter(PlanningGameSorter.DATE));
			}
		});

		
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(columnNames[3]);
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new PlanningGameSorter(PlanningGameSorter.DURATION));
			}
		});
	}
	
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);
		
		tableViewer.setContentProvider(new PlanningGameContentProvider(editorInput.getTasks()));
		tableViewer.setLabelProvider(new PlanningGameLabelProvider());
		tableViewer.setInput(editorInput);
	}
}
