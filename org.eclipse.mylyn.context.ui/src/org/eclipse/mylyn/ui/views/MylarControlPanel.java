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
 * Created on Feb 8, 2005
  */
package org.eclipse.mylar.ui.views;

import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;


/**
 * @author Mik Kersten
 */
public class MylarControlPanel extends Composite {
    private Scale dosScale;
//    private TableViewer tableViewer; 
    private Scale doiScale;
    
    public MylarControlPanel(ViewPart part, Composite parent, int style) {
        super(parent, style);
        final GridLayout gridLayout_3 = new GridLayout();
        gridLayout_3.verticalSpacing = 2;
        gridLayout_3.marginWidth = 0;
        gridLayout_3.marginHeight = 0;
        gridLayout_3.horizontalSpacing = 1;
        setLayout(gridLayout_3);
        
        final Label activeLabel = new Label(this, SWT.NONE);
        activeLabel.setText("Active tasks ");
        activeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
        
//        tableViewer = new TableViewer(this, SWT.BORDER);
//        final GridData gridData = new GridData();
//        gridData.widthHint = 141;
//        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
//        tableViewer.setContentProvider(new ItemContentProvider());
//        tableViewer.setInput(part.getViewSite());
//        tableViewer.setLabelProvider(new TasklistLabelProvider());

        final Composite group = new Composite(this, SWT.NONE);
        final GridLayout gridLayout_2 = new GridLayout();
        gridLayout_2.verticalSpacing = 0;
        gridLayout_2.horizontalSpacing = 0;
        gridLayout_2.marginWidth = 0;
        gridLayout_2.marginHeight = 0;
        group.setLayout(gridLayout_2);
        final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData_1.heightHint = 45;
        group.setLayoutData(gridData_1); 

        final Composite doiComposite = new Composite(group, SWT.NONE);
        final GridData gridData_2 = new GridData(GridData.FILL_BOTH);
        gridData_2.heightHint = 20;
        gridData_2.widthHint = 191;
        doiComposite.setLayoutData(gridData_2);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 1;
        gridLayout.verticalSpacing = 1;
        gridLayout.marginWidth = 1;
        gridLayout.marginHeight = 1;
        gridLayout.numColumns = 2;
        doiComposite.setLayout(gridLayout);

        final Label doiLabel = new Label(doiComposite, SWT.NONE);
        final GridData gridData_4 = new GridData();
        gridData_4.widthHint = 105;
        doiLabel.setLayoutData(gridData_4);
        doiLabel.setText(" Degree of interest");
        doiScale = new Scale(doiComposite, SWT.NONE);
        final GridData gridData_3 = new GridData(GridData.FILL_HORIZONTAL);
        gridData_3.heightHint = 17;
        doiScale.setLayoutData(gridData_3);
        doiScale.setPageIncrement(1);
        doiScale.setSelection(6);
        doiScale.setMinimum(0);
        doiScale.setMaximum(12);
        doiScale.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                MylarContextManager.getScalingFactors().setInteresting(scaleDoiSelection(doiScale.getSelection()));
                MylarPlugin.getContextManager().notifyActivePresentationSettingsChange(IMylarContextListener.UpdateKind.SCALING);
            } 
            public void widgetDefaultSelected(SelectionEvent e) { 
            	// don't care about default selection
            }
        });
        doiScale.addMouseListener(new MouseListener() {
            public void mouseDoubleClick(MouseEvent e) { 
            	// don't care about double click
            }
            public void mouseDown(MouseEvent e) { 
            	// don't care about mouse down
            }
            public void mouseUp(MouseEvent e) {
                MylarContextManager.getScalingFactors().setInteresting(scaleDoiSelection(doiScale.getSelection()));
                MylarPlugin.getContextManager().notifyPostPresentationSettingsChange(IMylarContextListener.UpdateKind.SCALING);
            }
        });

        final Composite dosComposite = new Composite(group, SWT.NONE);
        final GridData gridData_2_1 = new GridData(GridData.FILL_BOTH);
        gridData_2_1.heightHint = 20;
        gridData_2_1.widthHint = 191;
        dosComposite.setLayoutData(gridData_2_1);
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.verticalSpacing = 1;
        gridLayout_1.horizontalSpacing = 1;
        gridLayout_1.marginWidth = 1;
        gridLayout_1.marginHeight = 1;
        gridLayout_1.numColumns = 2;
        dosComposite.setLayout(gridLayout_1);

        final Label dosLabel = new Label(dosComposite, SWT.NONE);
        final GridData gridData_5 = new GridData();
        gridData_5.widthHint = 105;
        dosLabel.setLayoutData(gridData_5);
        dosLabel.setBounds(0, 0, 120, 30);
        dosLabel.setText(" Degree of separation");

        dosScale = new Scale(dosComposite, SWT.NONE);
        final GridData gridData_3_1 = new GridData(GridData.FILL_HORIZONTAL);
        gridData_3_1.heightHint = 17;
        dosScale.setIncrement(1);
        dosScale.setLayoutData(gridData_3_1);
        dosScale.setMinimum(1);
        dosScale.setMaximum(5);
        dosScale.setSelection(2); 
        dosScale.setPageIncrement(1);
        dosScale.setBounds(0, 0, 120, 30);
        dosScale.setToolTipText("landmarks | interesting | projects | dependent projects | workspace");
        dosScale.addMouseListener(new MouseListener() {
            public void mouseDoubleClick(MouseEvent e) { 
            	// don't care about double click
            }
            public void mouseDown(MouseEvent e) { 
            	// don't care about mouse down
            }
            public void mouseUp(MouseEvent e) {
                MylarContextManager.getScalingFactors().setDegreeOfSeparation(
                        dosScale.getSelection());
                MylarPlugin.getContextManager().refreshRelatedElements();
            }
            
        });
       
//        initDragAndDrop(tableViewer);
//        tableViewer.refresh();
    }

    private float scaleDoiSelection(int selection) {
        int value = selection;
        int scaledValue = (-1)*(value - 6);
        if (scaledValue < 0) scaledValue = 0;
        return scaledValue;
    }
    
//    private static final class ItemContentProvider implements IStructuredContentProvider {
//
//        public Object[] getElements(Object inputElement) {
//            return TaskListPlugin.getTaskListManager().getTaskList().getActiveTasks().toArray();
//        }
//
//        public void dispose() {
//        }
//
//        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//        }
//    }


//    private void initDragAndDrop(final TableViewer tableViewer) {
//        TextTransfer textTransfer = TextTransfer.getInstance();
//        DropTarget target = new DropTarget(tableViewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE);
//        target.setTransfer(new Transfer[] { textTransfer });
//        target.addDropListener(new TaskListDropTargetListener(this, tableViewer, textTransfer, true));
//        
//        DragSource source = new DragSource(tableViewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE);
//        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
//        source.setTransfer(types);
//        source.addDragListener(new DragSourceListener() {
//
//            public void dragStart(DragSourceEvent event) {
//                if (((StructuredSelection)tableViewer.getSelection()).getFirstElement() == null) { 
//                    event.doit = false; 
//                }
//            }
//
//            public void dragSetData(DragSourceEvent event) {
//                StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
//                ITask task = (ITask)selection.getFirstElement();
//                if (task != null) {
//                    event.data = "" + task.getId();
//                } else {
//                    event.data = " ";
//                }
//            }
//
//            public void dragFinished(DragSourceEvent event) {
//                StructuredSelection selection = (StructuredSelection)tableViewer.getSelection();
//                if (selection.isEmpty()) {
//                    return;
//                } else {
//                    ITask task = (ITask) selection.getFirstElement();
//                    tableViewer.remove(task);
//                    tableViewer.refresh();
//                }
//            }
//            
//        });
//    }
    
//    class ViewLabelProvider extends LabelProvider implements IColorProvider {
// 
//        public String getText(Object obj) { 
//            if (obj instanceof BugzillaTask) {
//                String desc = TaskListPlugin.getDefault().getBugzillaProvider().getBugzillaDescription(
//                        ((BugzillaTask)obj));
//                return desc;
//            } else if (obj instanceof Task) {
//                Task task = (Task)obj;
//                return task.toString();// + "  [" + task.getId() + "]"; 
//            } else { 
//                return obj.toString();  
//            }
//        }
//            
//        public Image getImage(Object obj) {
//            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//            if (obj instanceof Category) {
//                return MylarImages.getImage(MylarImages.TASK_CATEGORY);
//            } else if (obj instanceof BugzillaTask) {
//                return MylarImages.getImage(MylarImages.TASK_BUGZILLA);         
//            } else if (obj instanceof Task) {
//                return MylarImages.getImage(MylarImages.TASK);
//            } else {
//                return null;
//            }
//        }
//        public Color getForeground(Object element) {
//            return null;
//        }
//        
//        public Color getBackground(Object element) {
//            if (element instanceof ITask) {
//                ITask task = (ITask)element;
//                if (task.isActive()) {
//                    Highlighter highlighter = MylarUiPlugin.getDefault().getHighlighterForTaskId(((Task)task).getId());
//                    if (highlighter != null) {  
//                        return highlighter.getHighlightColor();
//                    } else {
//                        return null;
//                    }
//                } 
//            }
//            return null;
//        }
//    } 
    
    @Override
    public void dispose() {
        super.dispose();
    }

//    protected void checkSubclass() {
//    	
//    }

//    public TableViewer getTableViewer() {
//        return tableViewer;
//    }
    
}
