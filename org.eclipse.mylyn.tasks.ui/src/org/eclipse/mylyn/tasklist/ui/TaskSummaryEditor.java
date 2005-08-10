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
/*
 * Created on 19-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.mylar.tasklist.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskActivityListener;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.RelatedLinks;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.internal.RelativePathUtil;
import org.eclipse.mylar.tasklist.ui.views.DateChooserDialog;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.eclipse.ui.part.EditorPart;

/**
 * For details on forms, go to:
 * 	http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/pde-ui-home/working/EclipseForms/EclipseForms.html
 * @author Ken Sueda
 */
public class TaskSummaryEditor extends EditorPart {
	
	private static final String DESCRIPTION_OVERVIEW = "Task Summary";

	/**
	 * TODO: use workbench theme
	 */
	public static final Color HYPERLINK  = new Color(Display.getDefault(), 0, 0, 255);
	
	private ITask task;
	private TaskEditorInput editorInput;
	private Composite editorComposite;
	private TaskEditorCopyAction copyAction;
	private RetargetAction pasteAction;
	private RetargetAction cutAction;
	private static final String cutActionDefId = "org.eclipse.ui.edit.cut";
	private static final String pasteActionDefId = "org.eclipse.ui.edit.paste";
	private Table table;
	private TableViewer tableViewer;
	private RelatedLinks links;
	private RelatedLinksContentProvider contentProvider;
		
	private Button browse;
	private Text pathText;
	private ScrolledForm sform;
	private Action add;
    private Action delete;
    private Text description;
    private Text notes;
    private Text estimated; 
    
    private boolean isDirty = false;
    private TaskEditor parentEditor = null;

    private ITaskActivityListener TASK_LIST_LISTENER = new ITaskActivityListener() {
        public void taskActivated(ITask activeTask) {    
        	if (task != null && !browse.isDisposed() && activeTask.getHandle().equals(task.getHandle())) {
        		browse.setEnabled(false);
        	}
        }

        public void tasksActivated(List<ITask> tasks) {
            for (ITask t : tasks) {
            	taskActivated(t);
            }
        }

        public void taskDeactivated(ITask deactiveTask) {
        	if (task != null && !browse.isDisposed() && deactiveTask.getHandle().equals(task.getHandle())) {
        		browse.setEnabled(true);
        	}
        }

		public void taskPropertyChanged(ITask updatedTask, String property) {
			if (task != null && updatedTask.getHandle().equals(task.getHandle())) {
        		if (property.equals("Description") && !description.isDisposed()) {
        			description.setText(task.getLabel());
        		} else if (property.equals("Path") && !pathText.isDisposed()) {
        			pathText.setText("<Mylar_Dir>/" + task.getPath());
        		}
        	}
		}        
    };    
	/**
	 * 
	 */
	public TaskSummaryEditor() {
		super();

		cutAction = new RetargetAction(ActionFactory.CUT.getId(),
                WorkbenchMessages.Workbench_cut);
		cutAction.setToolTipText(
                WorkbenchMessages.Workbench_cutToolTip);
		cutAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_CUT));
		cutAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_CUT));
		cutAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_CUT_DISABLED));
		cutAction.setAccelerator(SWT.CTRL | 'x');
		cutAction.setActionDefinitionId(cutActionDefId);

		pasteAction = new RetargetAction(ActionFactory.PASTE.getId(),
                WorkbenchMessages.Workbench_paste);
		pasteAction.setToolTipText(
                WorkbenchMessages.Workbench_pasteToolTip);
		pasteAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setAccelerator(SWT.CTRL | 'v');
		pasteAction.setActionDefinitionId(pasteActionDefId);

		copyAction = new TaskEditorCopyAction();
		copyAction.setText(
                WorkbenchMessages.Workbench_copy);
		copyAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_COPY));
		copyAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_COPY));
		copyAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(
			ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setAccelerator(SWT.CTRL | 'c');

		copyAction.setEnabled(false);
		MylarTasklistPlugin.getTaskListManager().addListener(TASK_LIST_LISTENER);
	}
	@Override
	public void doSave(IProgressMonitor monitor) {
		String label = description.getText();
		task.setLabel(label);
		String note = notes.getText();
		task.setNotes(note);		
		String estimate = estimated.getText();
		task.setEstimatedTime(estimate);
		links.clear();		
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getData() instanceof String) {
				links.add((String)items[i].getData());
			}			
		}
		//"<MylarDir>/" + res + ".xml"
		String path = pathText.getText();		
		path = path.substring(path.indexOf('/') + 1, path.lastIndexOf('.'));		
		task.setPath(path);
		refreshTaskListView(task);
		markDirty(false);
	}
	@Override
	public void doSaveAs() {
		// don't support saving
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof TaskEditorInput)) {
			throw new PartInitException("Invalid Input: Must be TaskEditorInput");
		}
		setSite(site);
		setInput(input);
		editorInput = (TaskEditorInput)input;
		setPartName(editorInput.getLabel());
	}
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		sform = toolkit.createScrolledForm(parent);
		sform.getBody().setLayout(new TableWrapLayout());
		editorComposite = sform.getBody();
		
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 10;
		layout.topMargin = 10;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		editorComposite.setLayout(layout);
		//editorComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));				
		
		// Put the info onto the editor
		createContent(editorComposite, toolkit);
		sform.setFocus();
	}

	@Override
	public void setFocus() {
		sform.setFocus();
	}

	public Control getControl() {
		return sform;
	}
	
	public void setTask(ITask task) throws Exception {
		if (task == null)
			throw new Exception("ITask object is null.");
		this.task = task;
	}
	
	private Composite createContent(Composite parent, FormToolkit toolkit) {				
		TaskEditorInput taskEditorInput = (TaskEditorInput)getEditorInput();
		
		task = taskEditorInput.getTask();
		if (task == null) {
			MessageDialog.openError(parent.getShell(), "No such task", "No task exists with this id");
			return null;
		}		
        
		try {
			createOverviewSection(parent, toolkit);	
			createPlanningGameSection(parent, toolkit);
			createNotesSection(parent, toolkit);
	        createRelatedLinksSection(parent, toolkit);
	        createDetailsSection(parent, toolkit);
        } catch (SWTException e) {
        	MylarPlugin.log(e, "content failed");
        }	       
		return null;
	}
	
	private void createOverviewSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(DESCRIPTION_OVERVIEW);
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				sform.reflow(true);
			}
			public void expansionStateChanged(ExpansionEvent e) {
				sform.reflow(true);
			}			
		});
		
		Composite container = toolkit.createComposite(section);
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;						
		container.setLayout(layout);
		container.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
        Label l = toolkit.createLabel(container, "Description:");
        l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));	        
        description = toolkit.createText(container,task.getLabel(), SWT.BORDER);
        TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        description.setLayoutData(td);
        if (!task.canEditDescription()) {
        	description.setEnabled(false);
        } else {
        	description.addModifyListener(new ModifyListener() {
    			public void modifyText(ModifyEvent e) {
    				markDirty(true);
    			}			
    		});
        }
        l = toolkit.createLabel(container, "Reminder:");
        l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));	        
        final Text reminderDate = toolkit.createText(container,task.getReminderDateString(true), SWT.BORDER);        
        reminderDate.setLayoutData(layout);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.grabHorizontal = true;
        td.colspan = 1;
        reminderDate.setLayoutData(td);
        reminderDate.setEnabled(false);
        
        Button dateSelect = toolkit.createButton(container, "Select", SWT.PUSH);
//        td = new TableWrapData(TableWrapData.RIGHT);
//        dateSelect.setLayoutData(td);
        dateSelect.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ITask task = ((TaskEditorInput)getEditorInput()).getTask();
				DateChooserDialog dialog = new DateChooserDialog(Workbench.getInstance().getActiveWorkbenchWindow().getShell());	    		   	
				if (dialog.open() == Dialog.OK && dialog.getReminderDate() != null) {
					task.setReminderDate(dialog.getReminderDate().getTime());
					reminderDate.setText(task.getReminderDateString(true));
				}	
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}        	
        });
	}	

	
	private void createNotesSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText("Notes");			
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				sform.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				sform.reflow(true);
			}			
		});
		Composite container = toolkit.createComposite(section);			
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;					
		container.setLayout(layout);
		
		notes = toolkit.createText(container, task.getNotes(), SWT.BORDER | SWT.MULTI);
		TableWrapData tablewrap = new TableWrapData(TableWrapData.FILL_GRAB);
		tablewrap.heightHint = 100;
		notes.setLayoutData(tablewrap);
		notes.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
			}			
		});
//		notes.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e) {								
//			}
//
//			public void keyReleased(KeyEvent e) {
//				if (e.)
//				markDirty(true);
//			}			
//		});
	}
	
	private void createPlanningGameSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText("Planning Game");			
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				sform.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				sform.reflow(true);
			}			
		});
		Composite container = toolkit.createComposite(section);			
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;					
		container.setLayout(layout);
		
		Label l = toolkit.createLabel(container, "Estimated Time:");		
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		estimated = toolkit.createText(container,task.getEstimatedTime(), SWT.BORDER);	        
        estimated.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        
        estimated.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
			}			
		});     
		
		l = toolkit.createLabel(container, "Elapsed Time:");		
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		Text text2 = toolkit.createText(container,task.getElapsedTimeForDisplay(), SWT.BORDER);	        
        text2.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        text2.setEditable(false);
        text2.setEnabled(false);
        //text2.setForeground(background);
	}
	
	private void createRelatedLinksSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Related Links");			
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				sform.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				sform.reflow(true);
			}			
		});
		Composite container = toolkit.createComposite(section);			
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;					
		container.setLayout(layout);			
		
		Label l = toolkit.createLabel(container, "Related Links:");
		l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		toolkit.createLabel(container, "");
		
		createTable(container, toolkit);
		createTableViewer(container, toolkit);		
		toolkit.paintBordersFor(container);
		createAddDeleteButtons(container, toolkit);
	}

	private void createTable(Composite parent, FormToolkit toolkit) {	
		table = toolkit.createTable(parent, SWT.NONE );		
		TableColumn col1 = new TableColumn(table, SWT.NULL);
		TableLayout tlayout = new TableLayout();
		tlayout.addColumnData(new ColumnWeightData(0,0,false));
		table.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 100;
		wd.grabVertical = true;
		table.setLayoutData(wd);
		table.setHeaderVisible(false);
		col1.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new RelatedLinksTableSorter(
						RelatedLinksTableSorter.LABEL));
			}
		});			
		table.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				if(!((RelatedLinksContentProvider)tableViewer.getContentProvider()).isEmpty()) {
					Cursor hyperlinkCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
					Display.getCurrent().getCursorControl().setCursor(hyperlinkCursor);
				}				
			}

			public void mouseExit(MouseEvent e) {
				Cursor pointer = new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW);
				Display.getCurrent().getCursorControl().setCursor(pointer);
			}

			public void mouseHover(MouseEvent e){
				if(!((RelatedLinksContentProvider)tableViewer.getContentProvider()).isEmpty()) {
					Cursor hyperlinkCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
					Display.getCurrent().getCursorControl().setCursor(hyperlinkCursor);
				}
			}
		});		
	}
	
	private void createTableViewer(Composite parent, FormToolkit toolkit) {
		String[] columnNames = {"Links"};	
		tableViewer = new TableViewer(table);
		tableViewer.setColumnProperties(columnNames);
		
		CellEditor[] editors = new CellEditor[columnNames.length];
		
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(50);
		((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		editors[0] = textEditor;		
		
		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new RelatedLinksCellModifier());
		contentProvider = new RelatedLinksContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new RelatedLinksLabelProvider());
		links = task.getRelatedLinks();
		tableViewer.setInput(links);
		defineActions();
		hookContextMenu();
	}	
	private void createAddDeleteButtons(Composite parent, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(parent);
		container.setLayout(new GridLayout(1, true));
		Button addButton = toolkit.createButton(container, "  Add  ", SWT.PUSH | SWT.CENTER);
		//add.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		addButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				addLinkToTable();	
			}
		});

		Button deleteButton = toolkit.createButton(container, "Delete", SWT.PUSH | SWT.CENTER);
		deleteButton.setText("Delete");
		//delete.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		deleteButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeLinkFromTable();				
			}
		});
	}	
	
	private void createDetailsSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText("Details");
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				sform.reflow(true);
			}
			public void expansionStateChanged(ExpansionEvent e) {
				sform.reflow(true);
			}			
		});
		
		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;						
		container.setLayout(layout);
		
		Label l = toolkit.createLabel(container, "Task Handle:");
        l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
        Text handle = toolkit.createText(container, task.getHandle(), SWT.BORDER);
        TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 2;
        handle.setLayoutData(td);
        handle.setEditable(false);
        handle.setEnabled(false);
              		
		
        Label l2 = toolkit.createLabel(container, "Task context path:");
        l2.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
        pathText = toolkit.createText(container, "<Mylar_Dir>/"+task.getPath()+".xml", SWT.BORDER);
        pathText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        pathText.setEditable(false);        
        pathText.setEnabled(false);
        
        browse = toolkit.createButton(container, "Change", SWT.PUSH | SWT.CENTER);
        if (task.isActive()) {
        	browse.setEnabled(false);
        } else {
        	browse.setEnabled(true);
        }		
		browse.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (task.isActive()) {
					MessageDialog.openInformation(
							Display.getDefault().getActiveShell(),
				            "Task Message",
				            "Task can not be active when changing taskscape");
				} else {
					FileDialog dialog = new FileDialog(Display.getDefault()
							.getActiveShell(), SWT.OPEN);
					String[] ext = { "*.xml" };
					dialog.setFilterExtensions(ext);

					String mylarDir = MylarPlugin.getContextManager()
							.getMylarDir()
							+ "/";
					mylarDir = mylarDir.replaceAll("\\\\", "/");
					// mylarDir = formatPath(mylarDir);
					dialog.setFilterPath(mylarDir);

					String res = dialog.open();
					if (res != null) {
						res = res.replaceAll("\\\\", "/");
						res = RelativePathUtil.findRelativePath(mylarDir, res);
						pathText.setText("<MylarDir>/" + res + ".xml");
//						task.setPath(res);
						markDirty(true);
					}
				}
			}
		});
		toolkit.createLabel(container, "");
		l = toolkit.createLabel(container, "Go to Mylar Preferences to change <Mylar_Dir>");
        l.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
	}
	
	private void refreshTaskListView(ITask task) {
		if (TaskListView.getDefault() != null) TaskListView.getDefault().notifyTaskDataChanged(task);
	}
	private class RelatedLinksCellModifier implements ICellModifier, IColorProvider {
		RelatedLinksCellModifier() {
			super();

		}
		public boolean canModify(Object element, String property) {
			return true;
		}
		public Object getValue(Object element, String property) {			
			Object res = null;
			if (element instanceof String) {								
				String url = (String) element;
				try {					
					IWebBrowser b = null;
					int flags = 0;
					if (WorkbenchBrowserSupport.getInstance()
							.isInternalWebBrowserAvailable()) {
						flags = WorkbenchBrowserSupport.AS_EDITOR
								| WorkbenchBrowserSupport.LOCATION_BAR
								| WorkbenchBrowserSupport.NAVIGATION_BAR;

					} else {
						flags = WorkbenchBrowserSupport.AS_EXTERNAL
								| WorkbenchBrowserSupport.LOCATION_BAR
								| WorkbenchBrowserSupport.NAVIGATION_BAR;
					}
					b = WorkbenchBrowserSupport.getInstance().createBrowser(
							flags, "org.eclipse.mylar.tasklist", "Task", "tasktooltip");
					b.openURL(new URL((String) element));					
				} catch (PartInitException e) {
					MessageDialog.openError( Display.getDefault().getActiveShell(), 
							"URL not found", url + " could not be opened");
				} catch (MalformedURLException e) {
					MessageDialog.openError( Display.getDefault().getActiveShell(), 
							"URL not found", url + " could not be opened");
				}
				res = (String) element;
			}			
			return res;
		}
		public void modify(Object element, String property, Object value) {			
			return;
		}
		
		public Color getForeground(Object element) {
			return HYPERLINK;
		}
		
		public Color getBackground(Object element) {
			return null;
		}
	}
	
	private class RelatedLinksLabelProvider extends LabelProvider implements
			ITableLabelProvider, IColorProvider {
		
		public RelatedLinksLabelProvider() {
			// don't have any initialization to do
		}
		public String getColumnText(Object obj, int columnIndex) {
			String result = "";
			if (obj instanceof String) {
				switch (columnIndex) {
				case 0:
					result = (String) obj;
					break;
				default:
					break;
				}
			}
			return result;
		}
		public Image getColumnImage(Object obj, int columnIndex) {			
			return null;
		}
		public Color getForeground(Object element) {
			return HYPERLINK;
		}
		
		public Color getBackground(Object element) {
			return null;
		}
	}

	private class RelatedLinksContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return links.getLinks().toArray();
		}
		public void dispose() {
			// don't care if we are disposed
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// don't care if the input chages
		}
		public boolean isEmpty() {
			return links.getLinks().isEmpty();
		}
	}
	
	private class RelatedLinksTableSorter extends ViewerSorter {

		public final static int LABEL = 1;
		private int criteria;

		public RelatedLinksTableSorter(int criteria) {
			super();
			this.criteria = criteria;
		}
		
		@Override
		public int compare(Viewer viewer, Object o1, Object o2) {
			String s1 = (String) o1;
			String s2 = (String) o2;			
			switch (criteria) {
				case LABEL:
					return compareLabel(s1, s2);
				default:
					return 0;
			}
		}
		protected int compareLabel(String s1, String s2) {
			return s1.compareTo(s2);
		}				
		public int getCriteria() {
			return criteria;
		}
	}
	
	private void addLinkToTable() {
		InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "New related link", 
				"Enter new related link for this task", "", null);
		dialog.open();
		String url = null;
		String link = dialog.getValue();
		if (!(link.startsWith("http://") || link.startsWith("https://"))) {
			url = "http://" + link;					
		} else {
			url = link;
		}		
		tableViewer.add(url);	
		markDirty(true);
	}
	
	private void removeLinkFromTable() {
		String url = (String) ((IStructuredSelection) tableViewer
				.getSelection()).getFirstElement();
		if (url != null) {			
			tableViewer.remove(url);
			markDirty(true);
		}
	}
	private void defineActions() {		  
        delete = new Action() {
			@Override
			public void run() {
				removeLinkFromTable();
			}
		};
        delete.setText("Delete");
        delete.setToolTipText("Delete");
        delete.setImageDescriptor(TaskListImages.REMOVE);
        
        add = new Action() {
			@Override
			public void run() {
				addLinkToTable();
			}
		};
		add.setText("Add");
		add.setToolTipText("Add");
		//add.setImageDescriptor(MylarImages.REMOVE);
	}
	
	private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
            	manager.add(add);
                manager.add(delete);
            }
        });
        Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
        tableViewer.getControl().setMenu(menu);
        //getSite().registerContextMenu(menuMgr, tableViewer);
    }
	
	private void markDirty(boolean dirty) {
		isDirty = dirty;
		if (parentEditor != null) {
			parentEditor.updatePartName();			
		}				
		return;
	}
	public void setParentEditor(TaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}
	

	

	
	// Eric's Old Code...
	//
	
	//private StringBuffer sb;
	//private MenuManager contextMenuManager;
	//private final String VALUE = "VALUE";
	//private final String PROPERTY = "PROPERTY";
	//private final String HEADER = "HEADER";
	//private Color foreground;
	//private StringBuffer commentBuffer;
	//private int index;
	//private ArrayList<StyledText> texts = new ArrayList<StyledText>();
	//private StyledText currentSelectedText;
	//private Font titleFont;
	//private Font textFont;
	//private int scrollIncrement;
	//private int scrollVertPageIncrement;
	//private int scrollHorzPageIncrement;
	//private ScrolledComposite scrolledComposite;
	//private Display display;
	//private CLabel titleLabel;
	//private Composite infoArea;
	//private final int HORZ_INDENT = 0;
	//private final int HORZ_TABLE_SPACING = 10;
	
//	
//	private void focusOn(StyledText newText, int caretOffset) {
//		if (newText == null)
//			return;
//		newText.setFocus();
//		newText.setCaretOffset(caretOffset);
//		scrolledComposite.setOrigin(0, newText.getLocation().y);
//	}
//	
//	/**
//	 * Find the next text
//	 */
//	private StyledText nextText(StyledText text) {
//		int index = 0;
//		if (text == null)
//			return texts.get(0);
//		else
//			index = texts.indexOf(text);
//
//		//If we are not at the end....
//		if (index < texts.size() - 1)
//			return texts.get(index + 1);
//		else
//			return texts.get(index);
//	}
//
//	/**
//	 * Find the previous text
//	 */
//	private StyledText previousText(StyledText text) {
//		int index = 0;
//		if (text == null)
//			return texts.get(0);
//		else
//			index = texts.indexOf(text);
//
//		//If we are not at the end....
//		if (index == 0)
//			return texts.get(0);
//		else
//			return texts.get(index - 1);
//	}
//
//	protected StyledText getCurrentText() {
//		return currentSelectedText;
//	}
//
//	protected TaskEditorCopyAction getCopyAction() {
//		return copyAction;
//	}
//
//	private void addTextListeners(StyledText styledText) {
//		styledText.addTraverseListener(new TraverseListener() {
//			public void keyTraversed(TraverseEvent e) {
//				StyledText text = (StyledText) e.widget;
//
//				switch (e.detail) {
//					case SWT.TRAVERSE_ESCAPE :
//						e.doit = true;
//						break;
//					case SWT.TRAVERSE_TAB_NEXT :
//
//						text.setSelection(0);
//						StyledText nextText = nextText(text);
//						focusOn(nextText, 0);
//
//						e.detail = SWT.TRAVERSE_NONE;
//						e.doit = true;
//						break;
//
//					case SWT.TRAVERSE_TAB_PREVIOUS :
//
//						text.setSelection(text.getSelection());
//						StyledText previousText = previousText(text);
//						focusOn(previousText, 0);
//
//						e.detail = SWT.TRAVERSE_NONE;
//						e.doit = true;
//						break;
//
//					default:
//						break;
//				}
//			}
//		});
//
//		styledText.addKeyListener(new KeyListener() {
//			public void keyReleased(KeyEvent e) {
//				//Ignore a key release
//			}
//
//			public void keyPressed(KeyEvent event) {
//				StyledText text = (StyledText) event.widget;
//				if (event.character == ' ' || event.character == SWT.CR) {
//					return;
//				}
//
//				if (event.keyCode == SWT.PAGE_DOWN) {
//
//					scrolledComposite.setOrigin(0,
//						scrolledComposite.getOrigin().y
//							+ scrollVertPageIncrement);
//					return;
//				}
//				if (event.keyCode == SWT.ARROW_DOWN) {
//					scrolledComposite.setOrigin(0,
//						scrolledComposite.getOrigin().y + scrollIncrement);
//				}
//				if (event.keyCode == SWT.PAGE_UP) {
//					int origin = scrolledComposite.getOrigin().y;
//					int scrollAmount = origin - scrollVertPageIncrement;
//					if (scrollAmount <= 0) {
//						scrolledComposite.setOrigin(0, 0);
//					} else {
//						scrolledComposite.setOrigin(0, scrollAmount);
//					}
//					return;
//				}
//				if (event.keyCode == SWT.ARROW_UP) {
//					scrolledComposite.setOrigin(0,
//						scrolledComposite.getOrigin().y - scrollIncrement);
//				}
//			}
//		});
//
//		styledText.addFocusListener(new FocusAdapter() {
//			
//			@Override
//			public void focusLost(FocusEvent e) {
//				StyledText text = (StyledText) e.widget;
//				text.setSelection(text.getSelection().x);
//			}
//		});
//	}
	
//	public void createLayouts(Composite composite, ITask task) {
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		String title = "Task #" + task.getId();
//		newLayout(composite, 4, title, HEADER);
//		titleLabel.setText(title);
//	}
//
//	public void displayArtifact(Composite composite) {
//		TaskEditorInput editorInput = (TaskEditorInput) getEditorInput();
//		background = JFaceColors.getBannerBackground(composite.getParent()
//				.getParent().getDisplay());
//		composite.setBackground(background);
//
//		// Get the background color for the info area
//		composite.setBackground(background);
//
//		// The entire info area is 4 columns in width
//		// all headers take up all 4, values take up 1
//		GridLayout infoLayout = new GridLayout();
//		infoLayout.numColumns = 4;
//		infoLayout.marginHeight = 10;
//		infoLayout.verticalSpacing = 6;
//
//		infoLayout.marginWidth = 5;
//		infoLayout.horizontalSpacing = HORZ_TABLE_SPACING;
//		composite.setLayout(infoLayout);
//		GridData infoData = new GridData(GridData.FILL_BOTH);
//		composite.setLayoutData(infoData);
//
//		// Create the page with the task's contents
//		task = editorInput.getTask();
//		if (task != null) {
//			createLayouts(composite, task);
//		} else {
//			MessageDialog.openError(composite.getShell(), "No such task",
//					"No task exists with this id");
//			return;
//		}
//	}
//
//	/**
//	 * Make sure that a String that is <code>null</code> is changed to a null
//	 * string
//	 * 
//	 * @param text
//	 *            The text to check if it is null or not
//	 * @return
//	 */
//	public String checkText(String text) {
//		if (text == null)
//			return "";
//		else
//			return text;
//	}
//
//	
//	//
////	public void newLayout(Composite composite, int colSpan, String text, String style) {
////		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
////		data.horizontalSpan = colSpan;
////		if (style.equalsIgnoreCase(VALUE)) {
////			StyledText styledText = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY);
////			styledText.setFont(textFont);
////			styledText.setText(checkText(text));
////			styledText.setBackground(background);
////			data.horizontalIndent = HORZ_INDENT;
////
////			styledText.setLayoutData(data);
////			styledText.addSelectionListener(new SelectionAdapter() {
////				
////				@Override
////				public void widgetSelected(SelectionEvent e) {
////					StyledText c = (StyledText) e.widget;
////					if (c != null && c.getSelectionCount() > 0) {
////						if (currentSelectedText != null) {
////							if (!c.equals(currentSelectedText)) {
////								currentSelectedText.setSelectionRange(0, 0);
////							}
////						}
////					}
////					currentSelectedText = c;
////				}
////			});
////
////			styledText.setMenu(contextMenuManager.createContextMenu(styledText));
////
////			styledText.setEditable(false);
////
////			if (styledText.getText().trim().length() > 0) {
////				texts.add(index, styledText);
////				index++;
////				addTextListeners(styledText);
////			}
////		} else if (style.equalsIgnoreCase(PROPERTY)) {
////			StyledText styledText = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY);
////			styledText.setFont(textFont);
////			styledText.setText(checkText(text));
////			styledText.setBackground(background);
////			data.horizontalIndent = HORZ_INDENT;
////			styledText.setLayoutData(data);
////			StyleRange sr = new StyleRange(
////					styledText.getOffsetAtLine(0),
////					text.length(),
////					foreground,
////					background,
////					SWT.BOLD);
////			styledText.setStyleRange(sr);
////			styledText.setEnabled(false);
////			styledText.setMenu(contextMenuManager.createContextMenu(styledText));
////		} else {
////			Composite generalTitleGroup = new Composite(composite, SWT.NONE);
////			generalTitleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			generalTitleGroup.setLayoutData(data);
//			GridLayout generalTitleLayout = new GridLayout();
//			generalTitleLayout.numColumns = 2;
//			generalTitleLayout.marginWidth = 0;
//			generalTitleLayout.marginHeight = 9;
//			generalTitleGroup.setLayout(generalTitleLayout);
//			generalTitleGroup.setBackground(background);
//
//			Label image = new Label(generalTitleGroup, SWT.NONE);
//			image.setBackground(background);
//			image.setImage(
//				WorkbenchImages.getImage(IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM));
//			GridData gd = new GridData(GridData.FILL_VERTICAL);
//			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
//			image.setLayoutData(gd);
//			StyledText generalTitleText = new StyledText(generalTitleGroup, SWT.MULTI | SWT.READ_ONLY);
//			generalTitleText.setText(checkText(text));
//			generalTitleText.setBackground(background);
//			StyleRange sr =	new StyleRange(
//					generalTitleText.getOffsetAtLine(0),
//					text.length(),
//					foreground,
//					background,
//					SWT.BOLD);
//			generalTitleText.setStyleRange(sr);
//			generalTitleText.setEditable(false);
//			
//			generalTitleText.addSelectionListener(new SelectionAdapter() {
//				
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					StyledText c = (StyledText) e.widget;
//					if (c != null && c.getSelectionCount() > 0) {
//						if (currentSelectedText != null) {
//							if (!c.equals(currentSelectedText)) {
//								currentSelectedText.setSelectionRange(0, 0);
//							}
//						}
//					}
//					currentSelectedText = c;
//				}
//			});
//			
//			// create context menu
//			generalTitleGroup.setMenu(contextMenuManager.createContextMenu(generalTitleGroup));
//			generalTitleText.setMenu(contextMenuManager.createContextMenu(generalTitleText));
//			image.setMenu(contextMenuManager.createContextMenu(image));
//		}
//	}
	
//	private Composite createTitleArea(Composite parent) {
	//
//			// Get the background color for the title area
//			display = parent.getDisplay();
//			Color background = JFaceColors.getBannerBackground(display);
//			Color foreground = JFaceColors.getBannerForeground(display);
//			
//			// Create the title area which will contain
//			// a title, message, and image.
//			Composite titleArea = new Composite(parent, SWT.NO_FOCUS);
//			GridLayout layout = new GridLayout();
//			layout.marginHeight = 0;
//			layout.marginWidth = 0;
//			layout.verticalSpacing = 0;
//			layout.horizontalSpacing = 0;
//			layout.numColumns = 2;
//			titleArea.setLayout(layout);
//			titleArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//			titleArea.setBackground(background);
//			
//			// Message label
//			titleLabel = new CLabel(titleArea, SWT.LEFT);
//			JFaceColors.setColors(titleLabel, foreground, background);
//			titleLabel.setFont(titleFont);
//			final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent event) {
//					if (JFaceResources.HEADER_FONT.equals(event.getProperty())) {
//						titleLabel.setFont(titleFont);
//					}
//				}
//			};
//			titleLabel.addDisposeListener(new DisposeListener() {
//				public void widgetDisposed(DisposeEvent event) {
//					JFaceResources.getFontRegistry().removeListener(fontListener);
//				}
//			});
//			JFaceResources.getFontRegistry().addListener(fontListener);
//			GridData gd = new GridData(GridData.FILL_BOTH);
//			titleLabel.setLayoutData(gd);
	//
//			// Title image
//			Label titleImage = new Label(titleArea, SWT.LEFT);
//			titleImage.setBackground(background);
//			titleImage.setImage(
//				WorkbenchImages.getImage(
//						IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_BANNER));
//			gd = new GridData();
//			gd.horizontalAlignment = GridData.END;
//			titleImage.setLayoutData(gd);
//			return titleArea;
//		}
	//
//		private Composite createInfoArea(Composite parent) {
//			// Create the title area which will contain a title, message, and image.
//			scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
//			scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//			infoArea = new Composite(this.scrolledComposite, SWT.NONE);
//			scrolledComposite.setMinSize(infoArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	//
//			contextMenuManager = new MenuManager("#TaskSummaryEditor");
//			contextMenuManager.setRemoveAllWhenShown(true);
//			contextMenuManager.addMenuListener(new IMenuListener() {
//				public void menuAboutToShow(IMenuManager manager) {
//					manager.add(cutAction);
//					manager.add(copyAction);
//					manager.add(pasteAction);
//					manager.add(new Separator());
//					manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
//					if (currentSelectedText == null || 
//							currentSelectedText.getSelectionText().length() == 0) {
//						copyAction.setEnabled(false);
//					}
//					else {
//						copyAction.setEnabled(true);
//					}
//				}
//			});
//			getSite().registerContextMenu(
//				"#TaskSummaryEditor",
//				contextMenuManager,
//				getSite().getSelectionProvider());
	//
//			displayArtifact(infoArea);
//			this.scrolledComposite.setContent(infoArea);
//			Point p = infoArea.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
//			this.scrolledComposite.setMinHeight(p.y);
//			this.scrolledComposite.setMinWidth(p.x);
//			this.scrolledComposite.setExpandHorizontal(true);
//			this.scrolledComposite.setExpandVertical(true);
	//
//			// Add the focus listener to the scrolled composite
//			scrolledComposite.addMouseListener(new MouseAdapter() {
//				
//				@Override
//				public void mouseUp(MouseEvent e) {
//					if (!texts.isEmpty()) {
//						StyledText target = texts.get(0);
//						target.setFocus();
//					} else {
//						scrolledComposite.setFocus();
//					}
//				}
//			});
	//
//			scrolledComposite.addControlListener(new ControlListener() {
//				public void controlMoved(ControlEvent e) {
//					// don't care if a control is moved
//				}
//				public void controlResized(ControlEvent e) {
//					scrolledComposite.getVerticalBar().setIncrement(scrollIncrement);
//					scrolledComposite.getHorizontalBar().setIncrement(scrollIncrement);
//					scrollVertPageIncrement = scrolledComposite.getClientArea().height;
//					scrollHorzPageIncrement = scrolledComposite.getClientArea().width;
//					scrolledComposite.getVerticalBar().setPageIncrement(scrollVertPageIncrement);
//					scrolledComposite.getHorizontalBar().setPageIncrement(scrollHorzPageIncrement);
//				}
//			});
//			return infoArea;
//		}
}
