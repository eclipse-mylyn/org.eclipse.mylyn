/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.views;


import java.text.MessageFormat;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Rob Elves
 * 
 * Cell editor that allows selection of icons. 
 * A replica of ComboBoxCellEditor but makes use of
 * swt Table widget in order to display images for selection.
 */
public class ImageTableCellEditor extends CellEditor {

    /**
     * The list of items to present
     */
    private Image[] items;

    /**
     * The zero-based index of the selected item.
     */
    int selection;

    private Table priorityTable;

    private static final int defaultStyle = SWT.NONE;


    public ImageTableCellEditor() {
        setStyle(defaultStyle);
    }

    public ImageTableCellEditor(Composite parent, Image[] items) {
        this(parent, items, defaultStyle);
    }

    public ImageTableCellEditor(Composite parent, Image[] items, int style) {
        super(parent, style);
        setItems(items);
        
    }

    public Image[] getItems() {
        return this.items;
    }

    public void setItems(Image[] items) {
        Assert.isNotNull(items);
        this.items = items;
        populateTableItems();
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected Control createControl(Composite parent) {

    	priorityTable = new Table(parent, getStyle());

    	priorityTable.setFont(parent.getFont());

    	priorityTable.addKeyListener(new KeyAdapter() {
            // hook key pressed - see PR 14201  
            public void keyPressed(KeyEvent e) {
                keyReleaseOccured(e);
            }
        });

    	priorityTable.addSelectionListener(new SelectionAdapter() {
            public void widgetDefaultSelected(SelectionEvent event) {
                applyEditorValueAndDeactivate();
            }

            public void widgetSelected(SelectionEvent event) {
                selection = priorityTable.getSelectionIndex();
            }
        });

    	priorityTable.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE
                        || e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                }
            }
        });

    	priorityTable.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
            	ImageTableCellEditor.this.focusLost();
            }
        });
        return priorityTable;
    }


    protected Object doGetValue() {
        return new Integer(selection);
    }

    /* (non-Javadoc)
     * Method declared on CellEditor.
     */
    protected void doSetFocus() {
    	priorityTable.setFocus();
    }

    public LayoutData getLayoutData() {
        LayoutData layoutData = super.getLayoutData();      
       	layoutData.minimumWidth = 38;       	
        return layoutData;
    }

    protected void doSetValue(Object value) {
        Assert.isTrue(priorityTable != null && (value instanceof Integer));
        selection = ((Integer) value).intValue();
        priorityTable.select(selection);
    }

    /**
     * Updates the list of choices
     */
    private void populateTableItems() {
        if (priorityTable != null) {
        	priorityTable.removeAll();
            for (int i = 0; i < items.length; i++)   {     	
    			TableItem item = new TableItem(priorityTable, SWT.NONE);
    			item.setImage(items[i]);    			    			
    		}
            setValueValid(true);
            selection = 0;
        }
    }

    /**
     * Applies the currently selected value and deactiavates the cell editor
     */
    void applyEditorValueAndDeactivate() {
        //	must set the selection before getting value
        selection = priorityTable.getSelectionIndex();
        Object newValue = doGetValue();
        markDirty();
        boolean isValid = isCorrect(newValue);
        setValueValid(isValid);
        
        if (!isValid) {
        	// Only format if the 'index' is valid
        	if (priorityTable.getItemCount() > 0 && selection >= 0 && selection < priorityTable.getItemCount()) {
	            // try to insert the current value into the error message.
	            setErrorMessage(MessageFormat.format(getErrorMessage(),
	                    new Object[] { priorityTable.getItem(selection) }));
        	}
        	else {
	            // Since we don't have a valid index, pick a default
	            setErrorMessage(MessageFormat.format(getErrorMessage(),
	                    new Object[] { priorityTable.getItem(0) }));
        	}
        }

        fireApplyEditorValue();
        deactivate();
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#focusLost()
     */
    protected void focusLost() {
        if (isActivated()) {
            applyEditorValueAndDeactivate();
        }
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.jface.viewers.CellEditor#keyReleaseOccured(org.eclipse.swt.events.KeyEvent)
     */
    protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == '\u001b') { // Escape character
            fireCancelEditor();
        } else if (keyEvent.character == '\t') { // tab key
            applyEditorValueAndDeactivate();
        }
    }
    
}
