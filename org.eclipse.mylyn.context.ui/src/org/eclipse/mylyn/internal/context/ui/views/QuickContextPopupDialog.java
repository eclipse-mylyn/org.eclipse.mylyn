/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.context.ui.editors.ContextEditorFormPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * Derived from {@link QuickOutlinePopupDialog}
 * 
 * @author Mik Kersten
 */
public class QuickContextPopupDialog extends PopupDialog implements
		IInformationControl, IInformationControlExtension,
		IInformationControlExtension2, DisposeListener {

	public static final String ID_VIEWER = "org.eclipse.mylyn.context.ui.navigator.context.quick";
	
	private CommonViewer commonViewer;
	
	private InterestFilter interestFilter = new InterestFilter();
	
	private Text fFilterText;	
	
	private StringMatcher fStringMatcher;
	
	private QuickOutlinePatternAndInterestFilter namePatternFilter;
	
	private ContextNodeOpenListener openListener; 
	
	public QuickContextPopupDialog(Shell parent, int shellStyle) {
		super(parent, shellStyle, true, true, true, true, null, "Task Context");
		create();
	}

	@Override
	public boolean close() {
		// nothing additional to dispose
		return super.close();
	}
	
	protected Control createDialogArea(Composite parent) {
		createViewer(parent);
		createUIListenersTreeViewer();
		addDisposeListener(this);
			
		return commonViewer.getControl();
	}
	
	private void createViewer(Composite parent) {
		commonViewer = createCommonViewer(parent);
		
		openListener = new ContextNodeOpenListener(commonViewer);
		
		commonViewer.addOpenListener(openListener); 
		commonViewer.getTree().addMouseListener(openListener);
		
		commonViewer.addFilter(interestFilter);
		
		namePatternFilter = new QuickOutlinePatternAndInterestFilter();
		commonViewer.addFilter(namePatternFilter);
		
		try {
			commonViewer.getControl().setRedraw(false);
			
			ContextEditorFormPage.forceFlatLayoutOfJavaContent(commonViewer);
			
			commonViewer.setInput(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getInput());
			commonViewer.expandAll();
		} finally {
			commonViewer.getControl().setRedraw(true);
		}
	}

	protected CommonViewer createCommonViewer(Composite parent) {
		CommonViewer viewer = new CommonViewer(ID_VIEWER, parent, SWT.H_SCROLL | SWT.V_SCROLL);
		return viewer;
	}

	protected Point getInitialSize() {
		return new Point(400, 500);
	}
	
	protected void fillDialogMenu(IMenuManager dialogMenu) {
		dialogMenu.add(new Separator());
		super.fillDialogMenu(dialogMenu);
	}

	private void createUIListenersTreeViewer() {
		final Tree tree = commonViewer.getTree();
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e)  {
				if (e.character == 0x1B) { 
					// Dispose on ESC key press
					dispose();
				}
			}
			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});		
		
		tree.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) { 
				handleTreeViewerMouseUp(tree, e);
			}
		});

		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// ignore
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				gotoSelectedElement();
			}
		});		
	}	

	private void handleTreeViewerMouseUp(final Tree tree, MouseEvent e) {
		if ((tree.getSelectionCount() < 1) ||
				(e.button != 1) ||
				(tree.equals(e.getSource()) == false)) {
			return;
		}
		// Selection is made in the selection changed listener
		Object object = tree.getItem(new Point(e.x, e.y));
		TreeItem selection = tree.getSelection()[0];
		if (selection.equals(object)) {
			gotoSelectedElement();
		}
	}	
	
	private Object getSelectedElement() {
		if (commonViewer == null) {
			return null;
		}
		return ((IStructuredSelection) commonViewer.getSelection()).getFirstElement();
	}

	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	public void addFocusListener(FocusListener listener) {
		getShell().addFocusListener(listener);
	}

	public Point computeSizeHint() {
		// Note that it already has the persisted size if persisting is enabled.
		return getShell().getSize();
	}

	public void dispose() {
		close();
	}

	public boolean isFocusControl() {
		if (commonViewer.getControl().isFocusControl() ||
				fFilterText.isFocusControl()) {
			return true;
		}
		return false;
	}

	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		getShell().removeFocusListener(listener);
	}

	public void setBackgroundColor(Color background) {
		applyBackgroundColor(background, getContents());
	}

	public void setFocus() {
		getShell().forceFocus();
		fFilterText.setFocus();
	}

	public void setForegroundColor(Color foreground) {
		applyForegroundColor(foreground, getContents());
	}

	public void setInformation(String information) {
		// See IInformationControlExtension2
	}

	public void setLocation(Point location) {
		/*
		 * If the location is persisted, it gets managed by PopupDialog - fine. Otherwise, the location is
		 * computed in Window#getInitialLocation, which will center it in the parent shell / main
		 * monitor, which is wrong for two reasons:
		 * - we want to center over the editor / subject control, not the parent shell
		 * - the center is computed via the initalSize, which may be also wrong since the size may 
		 *   have been updated since via min/max sizing of AbstractInformationControlManager.
		 * In that case, override the location with the one computed by the manager. Note that
		 * the call to constrainShellSize in PopupDialog.open will still ensure that the shell is
		 * entirely visible.
		 */
		if ((getPersistBounds() == false) || 
				(getDialogSettings() == null)) {
			getShell().setLocation(location);
		}
	}

	public void setSize(int width, int height) {	
		getShell().setSize(width, height);
	}

	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// Ignore
	}

	public void setVisible(boolean visible) {
		if (visible) {
			open();
		} else {
			saveDialogBounds(getShell());
			getShell().setVisible(false);
		}
	}

	public boolean hasContents() {
		if ((commonViewer == null) ||
				(commonViewer.getInput() == null)) {
			return false;
		}
		return true;
	}

	public void setInput(Object input) {
		// Input comes from PDESourceInfoProvider.getInformation2()
		// The input should be a model object of some sort
		// Turn it into a structured selection and set the selection in the tree
		if (input != null) {
			commonViewer.setSelection(new StructuredSelection(input));
		}
	}

	public void widgetDisposed(DisposeEvent e) {
		// Note: We do not reuse the dialog
		commonViewer= null;
		fFilterText= null;
	}


	protected Control createTitleControl(Composite parent) {
		// Applies only to dialog title - not body.  See createDialogArea
		// Create the text widget
		createUIWidgetFilterText(parent);
		// Add listeners to the text widget
		createUIListenersFilterText();
		// Return the text widget
		return fFilterText;
	}

	private void createUIWidgetFilterText(Composite parent) {
		// Create the widget
		fFilterText = new Text(parent, SWT.NONE);
		// Set the font 
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		// Create the layout
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint= Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		fFilterText.setLayoutData(data);
	}	

	/**
	 * 
	 */
	private void gotoSelectedElement() {
		Object selectedElement = getSelectedElement();
		if (selectedElement == null) {
			return;
		}
		dispose();
	}	

	private void createUIListenersFilterText() {
		fFilterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) {
					// Return key was pressed
					gotoSelectedElement();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					// Down key was pressed
					commonViewer.getTree().setFocus();
				} else if (e.keyCode == SWT.ARROW_UP) {
					// Up key was pressed
					commonViewer.getTree().setFocus();
				} else if (e.character == 0x1B) {
					// Escape key was pressed
					dispose();
				}
			}
			public void keyReleased(KeyEvent e) {
				// NO-OP
			}
		});		
		// Handle text modify events
		fFilterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = ((Text)e.widget).getText();
				int length = text.length();
				if (length > 0) { 
					// Append a '*' pattern to the end of the text value if it
					// does not have one already
					if (text.charAt(length - 1) != '*') {
						text = text + '*';
					}
					// Prepend a '*' pattern to the beginning of the text value
					// if it does not have one already
					if (text.charAt(0) != '*') {
						text = '*' + text;
					}
				}
				// Set and update the pattern
				setMatcherString(text, true);
			}
		});		
	}
	
	/**
	 * Sets the patterns to filter out for the receiver.
	 * <p>
	 * The following characters have special meaning:
	 *   ? => any character
	 *   * => any string
	 * </p>
	 *
	 * @param pattern the pattern
	 * @param update <code>true</code> if the viewer should be updated
	 */
	private void setMatcherString(String pattern, boolean update) {
		if (pattern.length() == 0) {
			fStringMatcher = null;
		} else {
			fStringMatcher = new StringMatcher(pattern, true, false);
		}
		// Update the name pattern filter on the tree viewer
		namePatternFilter.setStringMatcher(fStringMatcher);
		// Update the tree viewer according to the pattern
		if (update) {
			stringMatcherUpdated();
		}
	}	
	
	/**
	 * The string matcher has been modified. The default implementation
	 * refreshes the view and selects the first matched element
	 */
	private void stringMatcherUpdated() {
		// Refresh the tree viewer to re-filter
		commonViewer.getControl().setRedraw(false);
		commonViewer.refresh();
		commonViewer.expandAll();
		selectFirstMatch();
		commonViewer.getControl().setRedraw(true);
	}	
	
	/**
	 * Selects the first element in the tree which
	 * matches the current filter pattern.
	 */
	private void selectFirstMatch() {
		Tree tree = commonViewer.getTree();
		Object element = findFirstMatchToPattern(tree.getItems());
		if (element != null) {
			commonViewer.setSelection(new StructuredSelection(element), true);
		} else {
			commonViewer.setSelection(StructuredSelection.EMPTY);
		}
	}

	/**
	 * @param items
	 * @return
	 */
	private Object findFirstMatchToPattern(TreeItem[] items) {
		// Match the string pattern against labels
		ILabelProvider labelProvider = 
			(ILabelProvider)commonViewer.getLabelProvider();
		// Process each item in the tree
		for (int i = 0; i < items.length; i++) {
			Object element = items[i].getData();
			// Return the first element if no pattern is set
			if (fStringMatcher == null) {
				return element;
			}
			// Return the element if it matches the pattern
			if (element != null) {
				String label = labelProvider.getText(element);
				if (fStringMatcher.match(label)) {
					return element;
				}
			}
			// Recursively check the elements children for a match
			element = findFirstMatchToPattern(items[i].getItems());
			// Return the child element match if found
			if (element != null) {
				return element;
			}
		}
		// No match found
		return null;
	}

}
