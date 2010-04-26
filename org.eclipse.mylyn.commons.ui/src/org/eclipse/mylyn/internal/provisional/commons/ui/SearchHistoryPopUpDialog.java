/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class SearchHistoryPopUpDialog extends PopupDialog {

	private static final int MAX_HISTORY_NO_FILTER = 3;

	private static final int MAX_HISTORY_FILTER = 10;

	private final int side;

	private Rectangle trimBounds;

	private TextSearchControl textSearchControl;

	private Text textControl;

	private TableViewer historyTable;

	private TableTreePatternFilter patternFilter;

	private Composite additionalControlsComposite;

	public SearchHistoryPopUpDialog(Shell parent, int side) {
		super(parent, PopupDialog.HOVER_SHELLSTYLE, false, false, false, false, false, null, null);
		this.side = side;
		updateBounds();
	}

	@Override
	protected void initializeBounds() {
		Rectangle monitorBounds = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getShell()
				.getMonitor()
				.getClientArea();
		Rectangle bounds = getShell().getBounds();
		int x = 0;
		int y = 0;

		switch (side) {
		case SWT.TOP:
			x = trimBounds.x;
			y = trimBounds.y + trimBounds.height;
			if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
				x = (trimBounds.x + trimBounds.width) - bounds.width;
			}
			break;
		case SWT.BOTTOM:
			x = trimBounds.x;
			y = trimBounds.y - bounds.height;
			if (x + bounds.width > monitorBounds.x + monitorBounds.width) {
				x = (trimBounds.x + trimBounds.width) - bounds.width;
			}
			break;
		case SWT.RIGHT:
			x = (trimBounds.x + trimBounds.width) - bounds.width;
			y = trimBounds.y + trimBounds.height;
			break;
		case SWT.LEFT:
			x = trimBounds.x;
			y = trimBounds.y + trimBounds.height;
			break;
		}
		getShell().setBounds(x, y, Math.max(trimBounds.width, bounds.width), bounds.height);
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		Control createDialogArea = createDialogArea(parent);
		return createDialogArea;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.numColumns = 1;
		composite.setLayout(gl);

		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		createTable(composite);

		additionalControlsComposite = new Composite(composite, SWT.NONE);
		gl = new GridLayout();
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.numColumns = 1;
		additionalControlsComposite.setLayout(gl);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(additionalControlsComposite);

		createAdditionalSearchRegion(additionalControlsComposite);
		if (additionalControlsComposite.getChildren().length == 0) {
			additionalControlsComposite.dispose();
			additionalControlsComposite = null;
		}

		parent.pack();

		return parent;
	}

	private void createTable(Composite composite) {
		historyTable = new TableViewer(composite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.NO_SCROLL);
		TableColumn bindingNameColumn = new TableColumn(historyTable.getTable(), SWT.LEFT);
		// XXX fix this
		bindingNameColumn.setWidth(2000);
		historyTable.setUseHashlookup(true);

		historyTable.setContentProvider(new ITreeContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

			public void dispose() {

			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Collection<?>) {
					Object[] elements = ((Collection<?>) inputElement).toArray();
					return reverseArray(elements);
				}
				return new Object[0];
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public Object getParent(Object element) {
				return null;
			}

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			private Object[] reverseArray(Object[] originalArray) {
				int i = 0;
				int j = originalArray.length - 1;

				while (i < j) {
					Object firstElement = originalArray[i];
					originalArray[i] = originalArray[j];
					originalArray[j] = firstElement;

					i++;
					j--;
				}
				return originalArray;
			}

		});

		if (textSearchControl != null) {
			historyTable.setInput(textSearchControl.getSearchHistory());
		} else {
			historyTable.setInput(Collections.emptyList());
		}
		historyTable.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				String text = getTextFromSelection(event.getSelection());
				if (text != null) {
					textSearchControl.getTextControl().setText(text);
					textControl.setSelection(text.length());
					textSearchControl.addToSearchHistory(text);
				}
				close();
			}

		});

		// XXX CHANGE TO SELECTION LISTENER??
		historyTable.getTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				String text = getTextFromSelection(historyTable.getSelection());
				if (text != null) {
					textSearchControl.getTextControl().setText(text);
					textSearchControl.addToSearchHistory(text);
				}
				textSearchControl.getTextControl().setFocus();
				close();
			}
		});
		patternFilter = new TableTreePatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		patternFilter.setPattern(textSearchControl.getTextControl().getText());
		historyTable.addFilter(patternFilter);
		if (textControl.getText().length() > 0) {
			historyTable.setSorter(new ViewerSorter());
			historyTable.setItemCount(Math.min(historyTable.getTable().getItemCount(), MAX_HISTORY_FILTER));
		} else {
			historyTable.setSorter(null);
			historyTable.setItemCount(Math.min(historyTable.getTable().getItemCount(), MAX_HISTORY_NO_FILTER));
		}
		GridDataFactory.fillDefaults().grab(true, false).hint(trimBounds.width, SWT.DEFAULT).applyTo(
				historyTable.getTable());
		setHistoryTableVisible(historyTable.getTable().getItemCount() > 0);
	}

	private void setHistoryTableVisible(boolean isVisible) {
		GridData layoutData = (GridData) historyTable.getTable().getLayoutData();
		historyTable.getTable().setVisible(isVisible);
		boolean wasVisible = !layoutData.exclude;
		layoutData.exclude = !isVisible;
		if (wasVisible != isVisible || isVisible) {
			getShell().pack();
		}
		initializeBounds();
	}

	private boolean shouldOpen() {
		return (historyTable != null && historyTable.getTable() != null && historyTable.getTable().isVisible())
				|| additionalControlsComposite != null;
	}

	private String getTextFromSelection(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) selection).getFirstElement();
			if (firstElement instanceof String) {
				return (String) firstElement;
			}

		}
		return null;
	}

	private boolean isOpen = false;

	@Override
	public int open() {
		if (!isOpen) {
			isOpen = true;
			int rc = super.open();
			if (!shouldOpen()) {
				close();
			}
			return rc;
		}
		return 0;
	}

	public void asyncClose() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if ((textControl != null && !textControl.isDisposed() && textControl.isFocusControl())
						|| !hasFocus(getShell())) {
					close();
				}
			}
		});
	}

	@Override
	public boolean close() {
		if (patternFilter != null) {
			patternFilter.setPattern(null);
		}
		boolean b = super.close();
		isOpen = !b;
		return b;
	}

	private boolean updateBounds() {
		Rectangle bounds;
		if (textSearchControl == null || textSearchControl.getTextControl() == null
				|| textSearchControl.getTextControl().isDisposed()) {
			bounds = new Rectangle(0, 0, 0, 0);
		} else {
			bounds = textSearchControl.getBounds();
			Point absPosition = textSearchControl.toDisplay(textSearchControl.getLocation());
			bounds.x = absPosition.x - bounds.x;
			bounds.y = absPosition.y - bounds.y;
		}
		if (trimBounds == null || !trimBounds.equals(bounds)) {
			this.trimBounds = bounds;
			return true;
		}
		return false;
	}

	public void attach(final TextSearchControl textSearchControl) {
		Assert.isNotNull(textSearchControl);
		Assert.isNotNull(textSearchControl.getTextControl());
		this.textSearchControl = textSearchControl;
		this.textControl = textSearchControl.getTextControl();
		textSearchControl.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (!hasFocus(textControl)) {
					// user shouldn't be modifying the text if it doesnt have focus
					return;
				}
				if (!isOpen && textControl != null && !textControl.isDisposed() && textControl.getText().length() > 0) {
					updateBounds();
					open();
				}
				if (isOpen && historyTable != null && !historyTable.getTable().isDisposed() && patternFilter != null) {
					patternFilter.setPattern(textControl.getText());
					historyTable.setSelection(null);
					historyTable.refresh();
					if (textControl.getText().length() > 0) {
						historyTable.setSorter(new ViewerSorter());
						historyTable.setItemCount(Math.min(historyTable.getTable().getItemCount(), MAX_HISTORY_FILTER));
					} else {
						historyTable.setSorter(null);
						historyTable.setItemCount(Math.min(historyTable.getTable().getItemCount(),
								MAX_HISTORY_NO_FILTER));
					}
					setHistoryTableVisible(historyTable.getTable().getItemCount() > 0);
					if (!shouldOpen()) {
						asyncClose();
					}
				}
			}
		});

		textControl.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		});

		textControl.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC && isOpen) {
					close();
					e.doit = false;
				} else if (e.stateMask == 0 && e.keyCode == SWT.ARROW_DOWN && isOpen && historyTable.getTable() != null
						&& !historyTable.getTable().isDisposed() && historyTable.getTable().getItemCount() > 0) {
					historyTable.getTable().select(0);
					historyTable.getTable().setFocus();
					e.doit = false;
				} else if ((e.stateMask & SWT.MOD1) != 0 && e.keyCode == SWT.ARROW_DOWN && !isOpen) {
					updateBounds();
					open();
					e.doit = false;
				}
			}
		});

		Listener moveResizeListener = new Listener() {

			public void handleEvent(Event event) {
				if (isOpen) {
					if (updateBounds()) {
						initializeBounds();
					}
				}
			}
		};
		textSearchControl.addListener(SWT.Move, moveResizeListener);
		textSearchControl.addListener(SWT.Resize, moveResizeListener);

		textControl.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent e) {
				asyncClose();
			}

			public void focusGained(FocusEvent e) {
			}
		});
	}

	private boolean hasFocus(Control control) {
		if (control != null && !control.isDisposed() && control.isFocusControl()) {
			return true;
		}
		if (control instanceof Composite) {
			for (Control child : ((Composite) control).getChildren()) {
				if (hasFocus(child)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void createAdditionalSearchRegion(Composite composite) {
	}

}
