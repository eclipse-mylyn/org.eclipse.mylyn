/*******************************************************************************
 *  Copyright (c) 2006, 2011 IBM Corporation and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tasktop Technologies - adapted for Mylyn
 *     Frank Becker - adapted for Mylyn Task Editor
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors.outline;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.workbench.DecoratingPatternStyledCellLabelProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineModel;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorOutlineNode;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Mik Kersten
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class QuickOutlineDialog extends PopupDialog implements IInformationControl, IInformationControlExtension,
		IInformationControlExtension2, DisposeListener {

	public final class Filter extends PatternFilter {
		@Override
		protected boolean wordMatches(String text) {
			return super.wordMatches(text);
		}
	}

	public final class TaskEditorOutlineLabelDecorator implements ILabelDecorator {

		public String decorateText(String text, Object element) {
			if (element instanceof TaskEditorOutlineNode) {
				TaskEditorOutlineNode node = (TaskEditorOutlineNode) element;
				if (node.getTaskRelation() != null) {
					return NLS.bind(Messages.QuickOutlineDialog_Node_Label_Decoration, text, node.getTaskRelation()
							.toString());
				}
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
			// ignore
		}

		public void dispose() {
			// ignore
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// ignore
		}

		public Image decorateImage(Image image, Object element) {
			return null;
		}

	}

	private class OpenListener implements IOpenListener, IDoubleClickListener, MouseListener {

		private final Viewer viewer;

		public OpenListener(Viewer viewer) {
			this.viewer = viewer;
		}

		public void mouseDoubleClick(MouseEvent e) {
			setSelection(e);
		}

		public void mouseDown(MouseEvent e) {
			setSelection(e);
		}

		public void mouseUp(MouseEvent e) {
			// ignore

		}

		public void doubleClick(DoubleClickEvent event) {
			open(null);
		}

		public void open(OpenEvent event) {
			AbstractTaskEditorPage taskEditorPage = getTaskEditorPage();
			if (taskEditorPage == null) {
				return;
			}

			StructuredSelection selection = (StructuredSelection) viewer.getSelection();
			Object select = (selection).getFirstElement();
			taskEditorPage.selectReveal(select);
		}

		private void setSelection(MouseEvent event) {
			try {
				Object selection = ((Tree) event.getSource()).getSelection()[0].getData();
				viewer.setSelection(new StructuredSelection(selection));
				open(null);
			} catch (Exception e) {
				// ignore
			}
		}

	}

	public static final String ID_VIEWER = "org.eclipse.mylyn.internal.tasks.ui.taskdata.quick"; //$NON-NLS-1$

	private TreeViewer viewer;

	private Text filterText;

	private Filter namePatternFilter;

	private OpenListener openListener;

	private final IWorkbenchWindow window;

	public QuickOutlineDialog(IWorkbenchWindow window) {
		super(window.getShell(), SWT.RESIZE, true, true, true, true, true, null, null);
		this.window = window;
		setInfoText(Messages.QuickOutlineDialog_Press_Esc_Info_Text);
		create();
	}

	@Override
	public boolean close() {
		// nothing additional to dispose
		return super.close();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createViewer(parent);
		createUIListenersTreeViewer();
		addDisposeListener(this);

		return viewer.getControl();
	}

	private void createViewer(Composite parent) {
		Control composite = super.createDialogArea(parent);
		viewer = createCommonViewer((Composite) composite);
		openListener = new OpenListener(viewer);

		viewer.addOpenListener(openListener);
		viewer.getTree().addMouseListener(openListener);

		namePatternFilter = new Filter();
		namePatternFilter.setIncludeLeadingWildcard(true);
		viewer.addFilter(namePatternFilter);

		AbstractTaskEditorPage taskEditorPage = getTaskEditorPage();
		if (taskEditorPage != null) {
			try {
				viewer.getControl().setRedraw(false);
				TaskEditorOutlineNode root = TaskEditorOutlineNode.parse(taskEditorPage.getModel().getTaskData(), true);
				viewer.setInput(new TaskEditorOutlineModel(root));
				viewer.expandAll();
				TaskEditorOutlineNode attributesNode = root.getChild(TaskEditorOutlineNode.LABEL_ATTRIBUTES);
				if (attributesNode != null) {
					viewer.collapseToLevel(attributesNode, AbstractTreeViewer.ALL_LEVELS);
				}
			} finally {
				viewer.getControl().setRedraw(true);
			}
		}
	}

	protected TreeViewer createCommonViewer(Composite parent) {
		TreeViewer viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setUseHashlookup(true);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new TaskEditorOutlineContentProvider());
		viewer.setLabelProvider(new DecoratingPatternStyledCellLabelProvider(new QuickOutlineLabelProvider(),
				new TaskEditorOutlineLabelDecorator(), null));
		return viewer;
	}

	@Override
	protected void fillDialogMenu(IMenuManager dialogMenu) {
		dialogMenu.add(new Separator());
		super.fillDialogMenu(dialogMenu);
	}

	private void createUIListenersTreeViewer() {
		final Tree tree = viewer.getTree();
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
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
			@Override
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
		if ((tree.getSelectionCount() < 1) || (e.button != 1) || (tree.equals(e.getSource()) == false)) {
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
		if (viewer == null) {
			return null;
		}
		return ((IStructuredSelection) viewer.getSelection()).getFirstElement();
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

	@Override
	protected Point getDefaultSize() {
		return new Point(400, 300);
	}

	public boolean isFocusControl() {
		if (viewer.getControl().isFocusControl() || filterText.isFocusControl()) {
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
		filterText.setFocus();
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
		if (getPersistLocation() == false || getDialogSettings() == null) {
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
		if ((viewer == null) || (viewer.getInput() == null)) {
			return false;
		}
		return true;
	}

	public void setInput(Object input) {
		if (input != null) {
			viewer.setSelection(new StructuredSelection(input));
		}
	}

	public void widgetDisposed(DisposeEvent e) {
		// Note: We do not reuse the dialog
		viewer = null;
		filterText = null;
	}

	@Override
	protected Control createTitleControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		control.setLayout(layout);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Applies only to dialog title - not body.  See createDialogArea
		// Create the text widget
		createUIWidgetFilterText(control);
		// Add listeners to the text widget
		createUIListenersFilterText();
		// Return the text widget
		return control;
	}

	private void createUIWidgetFilterText(Composite parent) {
		// Create the widget
		filterText = new Text(parent, SWT.NONE);
		// Set the font 
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		// Create the layout
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		filterText.setLayoutData(data);
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
		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) {
					// Return key was pressed
					gotoSelectedElement();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					// Down key was pressed
					viewer.getTree().setFocus();
				} else if (e.keyCode == SWT.ARROW_UP) {
					// Up key was pressed
					viewer.getTree().setFocus();
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
		filterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = ((Text) e.widget).getText();
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
	 * The following characters have special meaning: ? => any character * => any string
	 * </p>
	 * 
	 * @param pattern
	 *            the pattern
	 * @param update
	 *            <code>true</code> if the viewer should be updated
	 */
	private void setMatcherString(String pattern, boolean update) {
		namePatternFilter.setPattern(pattern);
		if (update) {
			stringMatcherUpdated();
		}
	}

	/**
	 * The string matcher has been modified. The default implementation refreshes the view and selects the first matched
	 * element
	 */
	private void stringMatcherUpdated() {
		// Refresh the tree viewer to re-filter
		viewer.getControl().setRedraw(false);
		viewer.refresh();
		viewer.expandAll();
		selectFirstMatch();
		viewer.getControl().setRedraw(true);
	}

	protected AbstractTaskEditorPage getTaskEditorPage() {
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage == null) {
			return null;
		}
		IEditorPart editorPart = activePage.getActiveEditor();
		AbstractTaskEditorPage taskEditorPage = null;
		if (editorPart instanceof TaskEditor) {
			TaskEditor taskEditor = (TaskEditor) editorPart;
			IFormPage formPage = taskEditor.getActivePageInstance();
			if (formPage instanceof AbstractTaskEditorPage) {
				taskEditorPage = (AbstractTaskEditorPage) formPage;
			}
		}
		return taskEditorPage;
	}

	/**
	 * Selects the first element in the tree which matches the current filter pattern.
	 */
	private void selectFirstMatch() {
		Tree tree = viewer.getTree();
		Object element = findFirstMatchToPattern(tree.getItems());
		if (element != null) {
			viewer.setSelection(new StructuredSelection(element), true);
		} else {
			viewer.setSelection(StructuredSelection.EMPTY);
		}
	}

	/**
	 * @param items
	 * @return
	 */
	private Object findFirstMatchToPattern(TreeItem[] items) {
		// Match the string pattern against labels
		ILabelProvider labelProvider = (ILabelProvider) viewer.getLabelProvider();
		// Process each item in the tree
		for (TreeItem item : items) {
			Object element = item.getData();
			// Return the element if it matches the pattern
			if (element != null) {
				String label = labelProvider.getText(element);
				if (namePatternFilter.wordMatches(label)) {
					return element;
				}
			}
			// Recursively check the elements children for a match
			element = findFirstMatchToPattern(item.getItems());
			// Return the child element match if found
			if (element != null) {
				return element;
			}
		}
		// No match found
		return null;
	}

}
