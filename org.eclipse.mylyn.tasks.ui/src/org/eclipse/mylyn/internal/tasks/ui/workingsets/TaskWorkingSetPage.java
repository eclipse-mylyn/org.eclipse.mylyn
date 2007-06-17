/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.workingsets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

/**
 * Adapted from org.eclipse.ui.internal.ide.dialogs.ResourceWorkingSetPage
 * 
 * @author Eugene Kuleshov
 * @author Mik Kersten
 */
public class TaskWorkingSetPage extends WizardPage implements IWorkingSetPage {

    private final static int SIZING_SELECTION_WIDGET_WIDTH = 50;

    private final static int SIZING_SELECTION_WIDGET_HEIGHT = 200;

    private Text text;

    private CheckboxTreeViewer tree;

    private IWorkingSet workingSet;

	private boolean firstCheck = false;

	public TaskWorkingSetPage() {
        super("taskWorkingSetPage", //$NON-NLS-1$ 
				"Task Working Set", null); // the icon
		setDescription("Enter a working set name and select task categories/queries.");
	}

	@SuppressWarnings("unchecked")
	public void finish() {
        // List<AbstractTaskContainer> elements = getCheckedElements((List<AbstractTaskContainer>) tree.getInput());
        Object[] elements = tree.getCheckedElements();
        IAdaptable[] adaptables = new IAdaptable[elements.length];
        Set<AbstractTaskContainer> validElements = new HashSet<AbstractTaskContainer>();
        for (int i = 0; i < adaptables.length; i++) {
        	if (adaptables[i] instanceof AbstractTaskContainer) {
        		validElements.add((AbstractTaskContainer)elements[i]);
        	}
		}
        
        if (workingSet == null) {
            IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
            workingSet = workingSetManager.createWorkingSet(getWorkingSetName(), (IAdaptable[])validElements.toArray(new IAdaptable[validElements.size()]));
        } else {
            workingSet.setName(getWorkingSetName());
			workingSet.setElements((IAdaptable[])validElements.toArray(new IAdaptable[validElements.size()]));
        }
	}

	public IWorkingSet getSelection() {
		return workingSet;
	}

	public void setSelection(IWorkingSet workingSet) {
		this.workingSet = workingSet;
        if (getShell() != null && text != null) {
            firstCheck = true;
            initializeCheckedState();
            text.setText(workingSet.getName());
        }
	}
	
    private String getWorkingSetName() {
        return text.getText();
    }

	public void createControl(Composite parent) {
    	initializeDialogUnits(parent);
    	
        Composite composite = new Composite(parent, SWT.NULL);
        
        GridLayout layout = new GridLayout();
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        setControl(composite);

        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IIDEHelpContextIds.WORKING_SET_RESOURCE_PAGE);
        Label label = new Label(composite, SWT.WRAP);
        label.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_message);
        label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER));

        text = new Text(composite, SWT.SINGLE | SWT.BORDER);
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                validateInput();
            }
        });
        text.setFocus();
        // text.setBackground(FieldAssistColors.getRequiredFieldBackgroundColor(text));

        label = new Label(composite, SWT.WRAP);
        label.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_label_tree);
        label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_CENTER));

        tree = new CheckboxTreeViewer(composite);
        tree.setUseHashlookup(true);

        final ITreeContentProvider treeContentProvider = new ITreeContentProvider() {

    		@SuppressWarnings("unchecked")
			public Object[] getChildren(Object parentElement) {
				if(parentElement instanceof List) {
					List containers = (List) parentElement;
					return containers.toArray(new Object[containers.size()]);
				}
				return new Object[0];
			}

			@SuppressWarnings("unchecked")
			public boolean hasChildren(Object element) {
				return getChildren(element).length > 0;
			}
			
			public Object[] getElements(Object element) {
				return getChildren(element);
			}

			public Object getParent(Object element) {
				return null;
			}
			
			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		};

        tree.setContentProvider(treeContentProvider);
        tree.setLabelProvider(new TaskElementLabelProvider());
        tree.setSorter(new ViewerSorter());
        
		ArrayList<Object> containers = new ArrayList<Object>();
		for (AbstractTaskContainer element : (Set<AbstractTaskContainer>)TasksUiPlugin.getTaskListManager().getTaskList().getRootElements()) {
			if (element instanceof AbstractTaskContainer && !(element instanceof TaskArchive)) {
				containers.add(element);
			}
		}
        tree.setInput(containers);
        
        // tree.setComparator(new ResourceComparator(ResourceComparator.NAME));

        GridData data = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        tree.getControl().setLayoutData(data);

        tree.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                handleCheckStateChange(event);
            }
        });

//        tree.addTreeListener(new ITreeViewerListener() {
//            public void treeCollapsed(TreeExpansionEvent event) {
//            }
//
//            public void treeExpanded(TreeExpansionEvent event) {
//                final Object element = event.getElement();
//                if (tree.getGrayed(element) == false) {
//					BusyIndicator.showWhile(getShell().getDisplay(),
//                            new Runnable() {
//                                public void run() {
//                                    setSubtreeChecked((IContainer) element,
//                                            tree.getChecked(element), false);
//                                }
//                            });
//				}
//            }
//        });

		// Add select / deselect all buttons for bug 46669
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
		selectAllButton.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_selectAll_label);
		selectAllButton.setToolTipText(IDEWorkbenchMessages.ResourceWorkingSetPage_selectAll_toolTip);
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				tree.setCheckedElements(treeContentProvider.getElements(tree.getInput()));
				validateInput();
			}
		});
		setButtonLayoutData(selectAllButton);

		Button deselectAllButton = new Button(buttonComposite, SWT.PUSH);
		deselectAllButton.setText(IDEWorkbenchMessages.ResourceWorkingSetPage_deselectAll_label);
		deselectAllButton.setToolTipText(IDEWorkbenchMessages.ResourceWorkingSetPage_deselectAll_toolTip);
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent selectionEvent) {
				tree.setCheckedElements(new Object[0]);
				validateInput();
			}
		});
		setButtonLayoutData(deselectAllButton);
		
		initializeCheckedState();
        if (workingSet != null) {
            text.setText(workingSet.getName());
        }
        setPageComplete(false);
        
        Dialog.applyDialogFont(composite);
	}

	private void initializeCheckedState() {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				Object[] items = null;
				if (workingSet != null) {
					items = workingSet.getElements();
					if (items != null) {
						// see bug 191342
						tree.setCheckedElements(new Object[] {});
						for (Object item : items) {
							if(item!=null) {
								tree.setChecked(item, true);
							}
						}
					}
				}
			}
		});
	}

	protected void handleCheckStateChange(final CheckStateChangedEvent event) {
        BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
            public void run() {
            	AbstractTaskContainer element = (AbstractTaskContainer) event.getElement();
            	tree.setGrayed(element, false);

                // boolean state = event.getChecked();
                // if (element instanceof AbstractTaskContainer) {
                //     setSubtreeChecked((AbstractTaskContainer) element, state, true);
                // }
                // updateParentState(element);
                validateInput();
            }
        });
	}

	protected void validateInput() {
        String errorMessage = null;
        String infoMessage= null;
        String newText = text.getText();

        if (!newText.equals(newText.trim())) {
            errorMessage = "The name must not have a leading or trailing whitespace."; 
        } else if (firstCheck) {
            firstCheck = false;
            return;
        }
        if ("".equals(newText)) { //$NON-NLS-1$
            errorMessage = "The name must not be empty.";
        }
        if (errorMessage == null
                && (workingSet == null || !newText.equals(workingSet.getName()))) {
            IWorkingSet[] workingSets = PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets();
            for (int i = 0; i < workingSets.length; i++) {
                if (newText.equals(workingSets[i].getName())) {
                    errorMessage = "A working set with the same name already exists.";
                }
            }
        }
        if (infoMessage == null && tree.getCheckedElements().length == 0) {
        	infoMessage = "No categories/queries selected.";
        }
        setMessage(infoMessage, INFORMATION);
        setErrorMessage(errorMessage);
        setPageComplete(errorMessage == null);
	}
	
}

