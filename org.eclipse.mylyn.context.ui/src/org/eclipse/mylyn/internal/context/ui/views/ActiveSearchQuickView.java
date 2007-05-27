/*******************************************************************************
 * Copyright (c) 2004 - 2007 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Geometry;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.context.core.IInteractionElement;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.internal.context.ui.DoiOrderSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class ActiveSearchQuickView {

	/**
	 * Dialog constants telling whether this control can be resized or move.
	 */
	private static final String STORE_DISABLE_RESTORE_SIZE = "DISABLE_RESTORE_SIZE"; //$NON-NLS-1$

	private static final String STORE_DISABLE_RESTORE_LOCATION = "DISABLE_RESTORE_LOCATION"; //$NON-NLS-1$

	/**
	 * Dialog store constant for the location's x-coordinate, location's
	 * y-coordinate and the size's width and height.
	 */
	private static final String STORE_LOCATION_X = "location.x"; //$NON-NLS-1$

	private static final String STORE_LOCATION_Y = "location.y"; //$NON-NLS-1$

	private static final String STORE_SIZE_WIDTH = "size.width"; //$NON-NLS-1$

	private static final String STORE_SIZE_HEIGHT = "size.height"; //$NON-NLS-1$

	/**
	 * The name of the dialog store's section associated with the inplace
	 * XReference view.
	 */
	private static final String sectionName = "org.eclipse.contribution.internal.xref.QuickXRef"; //$NON-NLS-1$

	/**
	 * Fields for text matching and filtering
	 */
	private Text filterText;

	// private StringMatcher stringMatcher;
	private Font statusTextFont;

	/**
	 * Remembers the bounds for this information control.
	 */
	private Rectangle bounds;

	private Rectangle trim;

	/**
	 * Fields for view menu support.
	 */
	private ToolBar toolBar;

	private MenuManager viewMenuManager;

//	/**
//	 * Fields which are updated by the IWorkbenchWindowActionDelegate to record
//	 * the selection in the editor
//	 */
//	private ISelection lastSelection;
//
//	private IWorkbenchPart workbenchPart;

	private boolean isDeactivateListenerActive = false;

	private Composite composite, viewMenuButtonComposite;

	private int shellStyle;

	private Listener deactivateListener;

	private Shell parentShell;

	private Shell dialogShell;

	private TreeViewer viewer;

	/**
	 * Constructor which takes the parent shell
	 */
	public ActiveSearchQuickView(Shell parent) {
		parentShell = parent;
		shellStyle = SWT.RESIZE;
	}

	/**
	 * Open the dialog
	 */
	public void open(IInteractionElement focusNode) {
		// If the dialog is already open, dispose the shell and recreate it
		if (dialogShell != null) {
			close();
		}
		createShell();
		createComposites();
		filterText = createFilterText(viewMenuButtonComposite);
		createViewMenu(viewMenuButtonComposite);
		createHorizontalSeparator(composite);
		viewer = createTreeViewer(composite, SWT.V_SCROLL | SWT.H_SCROLL);

		// customFiltersActionGroup = new CustomFiltersActionGroup(
		// "org.eclipse.contribution.xref.QuickXRef", viewer);//$NON-NLS-1$

		addListenersToTree(viewer);
		// set the tab order
		viewMenuButtonComposite.setTabList(new Control[] { filterText });
		composite.setTabList(new Control[] { viewMenuButtonComposite, viewer.getTree() });

		setInfoSystemColor();
		installFilter();
		addListenersToShell();

		viewer.addOpenListener(new ContextNodeOpenListener(viewer));
		viewer.setSorter(new DoiOrderSorter());
		createContents();
		initializeBounds();
		// open the window

		viewer.expandToLevel(focusNode, 3);
		// TreeItem[] items = viewer.getTree().getItems();
		// for (int i = 0; i < items.length; i++) {
		// if (items[i].getData().equals(focusNode)) {
		// items[i].setExpanded(true);
		// TreeItem[] children = items[i].getItems();
		// for (int j = 0; j < items.length; j++) {
		// items[j].setExpanded(true);
		// }
		// }
		// }
		dialogShell.open();
	}

	private void createShell() {
		// Create the shell
		dialogShell = new Shell(parentShell, shellStyle);

		// To handle "ESC" case
		dialogShell.addShellListener(new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent event) {
				event.doit = false; // don't close now
				close();
			}
		});

		Display display = dialogShell.getDisplay();
		dialogShell.setBackground(display.getSystemColor(SWT.COLOR_BLACK));

		int border = ((shellStyle & SWT.NO_TRIM) == 0) ? 0 : 1;
		dialogShell.setLayout(new BorderFillLayout(border));

	}

	private void createComposites() {
		// Composite for filter text and tree
		composite = new Composite(dialogShell, SWT.RESIZE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		viewMenuButtonComposite = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		viewMenuButtonComposite.setLayout(layout);
		viewMenuButtonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private TreeViewer createTreeViewer(Composite parent, int style) {
		viewer = new TreeViewer(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

		// XReferenceContentProvider contentProvider = new
		// XReferenceContentProvider();
		// viewer.setContentProvider(contentProvider);

		viewer.setContentProvider(new ContextContentProvider(dialogShell, true));
		// viewer.setLabelProvider(new TaskscapeNodeLabelProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new DelegatingContextLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator()));
		// viewer.setLabelProvider(new
		// MylarAppearanceAwareLabelProvider(viewer));

		// viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

		// adding these filters which restrict the contents of
		// the view according to what has been typed in the
		// text bar
		viewer.addFilter(new NamePatternFilter());

		// TODO: figure out if this was needed
		// viewer.addFilter(new MemberFilter());

		viewer.setInput(dialogShell);

		// doubleClickAction = new DoubleClickAction(dialogShell, viewer);

		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// if (dialogShell != null && dialogShell.isDisposed()) {
		// dispose();
		// }
		// }
		// });

		return viewer;
	}

	private void createHorizontalSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_DOT);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void setInfoSystemColor() {
		Display display = dialogShell.getDisplay();

		// set the foreground colour
		viewer.getTree().setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		filterText.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		composite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		viewMenuButtonComposite.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		toolBar.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));

		// set the background colour
		viewer.getTree().setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		filterText.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		composite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		viewMenuButtonComposite.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		toolBar.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	}

	// --------------------- adding listeners ---------------------------

	private void addListenersToTree(TreeViewer treeViewer) {
		final Tree tree = treeViewer.getTree();
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == 0x1B) // ESC
					dispose();
			}

			public void keyReleased(KeyEvent e) {
				// do nothing
			}
		});

		// tree.addSelectionListener(new SelectionListener() {
		// public void widgetSelected(SelectionEvent e) {
		// // do nothing
		// }
		//
		// public void widgetDefaultSelected(SelectionEvent e) {
		// gotoSelectedElement();
		// }
		// });

		tree.addMouseMoveListener(new MouseMoveListener() {
			TreeItem fLastItem = null;

			public void mouseMove(MouseEvent e) {
				if (tree.equals(e.getSource())) {
					Object o = tree.getItem(new Point(e.x, e.y));
					if (o instanceof TreeItem) {
						if (!o.equals(fLastItem)) {
							fLastItem = (TreeItem) o;
							tree.setSelection(new TreeItem[] { fLastItem });
						} else if (e.y < tree.getItemHeight() / 4) {
							// Scroll up
							Point p = tree.toDisplay(e.x, e.y);
							Item item = viewer.scrollUp(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem = (TreeItem) item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						} else if (e.y > tree.getBounds().height - tree.getItemHeight() / 4) {
							// Scroll down
							Point p = tree.toDisplay(e.x, e.y);
							Item item = viewer.scrollDown(p.x, p.y);
							if (item instanceof TreeItem) {
								fLastItem = (TreeItem) item;
								tree.setSelection(new TreeItem[] { fLastItem });
							}
						}
					}
				}
			}
		});

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				if (tree.getSelectionCount() < 1)
					return;

				if (e.button != 1)
					return;

				if (tree.equals(e.getSource())) {
					Object o = tree.getItem(new Point(e.x, e.y));
					TreeItem selection = tree.getSelection()[0];
					if (selection.equals(o))
						gotoSelectedElement();
				}
			}
		});

	}

	private void addListenersToShell() {
		dialogShell.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				close();
				if (statusTextFont != null && !statusTextFont.isDisposed())
					statusTextFont.dispose();

				dialogShell = null;
				viewer = null;
				composite = null;
				filterText = null;
				statusTextFont = null;

			}
		});

		deactivateListener = new Listener() {
			/*
			 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
			 */
			public void handleEvent(Event event) {
				if (isDeactivateListenerActive)
					dispose();
			}
		};

		dialogShell.addListener(SWT.Deactivate, deactivateListener);
		isDeactivateListenerActive = true;
		dialogShell.addShellListener(new ShellAdapter() {

			@Override
			public void shellActivated(ShellEvent e) {
				if (e.widget == dialogShell && dialogShell.getShells().length == 0)
					isDeactivateListenerActive = true;
			}
		});

		dialogShell.addControlListener(new ControlAdapter() {

			@Override
			public void controlMoved(ControlEvent e) {
				bounds = dialogShell.getBounds();
				if (trim != null) {
					Point location = composite.getLocation();
					bounds.x = bounds.x - trim.x + location.x;
					bounds.y = bounds.y - trim.y + location.y;
				}

			}

			@Override
			public void controlResized(ControlEvent e) {
				bounds = dialogShell.getBounds();
				if (trim != null) {
					Point location = composite.getLocation();
					bounds.x = bounds.x - trim.x + location.x;
					bounds.y = bounds.y - trim.y + location.y;
				}
			}
		});
	}

	// --------------------- creating and filling the menu
	// ---------------------------

	private void createViewMenu(Composite parent) {
		toolBar = new ToolBar(parent, SWT.FLAT);
		ToolItem viewMenuToolItem = new ToolItem(toolBar, SWT.PUSH, 0);

		GridData data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(data);

		// viewMenuButton.setImage(JavaPluginImages
		// .get(JavaPluginImages.IMG_ELCL_VIEW_MENU));
		// viewMenuButton.setDisabledImage(JavaPluginImages
		// .get(JavaPluginImages.IMG_DLCL_VIEW_MENU));

		// viewMenuButton.setToolTipText(XReferenceUIPlugin.getResourceString("XReferenceInplaceDialog.viewMenu.toolTipText"));
		// //$NON-NLS-1$
		viewMenuToolItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showViewMenu();
			}
		});
	}

	private void showViewMenu() {
		isDeactivateListenerActive = false;

		Menu aMenu = getViewMenuManager().createContextMenu(dialogShell);

		Rectangle toolbarBounds = toolBar.getBounds();
		Point topLeft = new Point(toolbarBounds.x, toolbarBounds.y + toolbarBounds.height);
		topLeft = dialogShell.toDisplay(topLeft);
		aMenu.setLocation(topLeft.x, topLeft.y);

		aMenu.setVisible(true);
	}

	private MenuManager getViewMenuManager() {
		if (viewMenuManager == null) {
			viewMenuManager = new MenuManager();
			fillViewMenu(viewMenuManager);
		}
		return viewMenuManager;
	}

	private void fillViewMenu(IMenuManager viewMenu) {
		viewMenu.add(new GroupMarker("SystemMenuStart")); //$NON-NLS-1$
		viewMenu.add(new MoveAction());
		viewMenu.add(new ResizeAction());
		viewMenu.add(new RememberBoundsAction());
		viewMenu.add(new Separator("SystemMenuEnd")); //$NON-NLS-1$

		// customFiltersActionGroup.fillViewMenu(viewMenu);
	}

	// ----------- all to do with setting the bounds of the dialog -------------

	/**
	 * Initialize the shell's bounds.
	 */
	private void initializeBounds() {
		// if we don't remember the dialog bounds then reset
		// to be the defaults (behaves like inplace outline view)
		Rectangle oldBounds = restoreBounds();
		if (oldBounds != null) {
			dialogShell.setBounds(oldBounds);
			return;
		}
		Point size = dialogShell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Point location = getDefaultLocation(size);
		dialogShell.setBounds(new Rectangle(location.x, location.y, size.x, size.y));
	}

	private Point getDefaultLocation(Point initialSize) {
		Monitor monitor = dialogShell.getDisplay().getPrimaryMonitor();
		if (parentShell != null) {
			monitor = parentShell.getMonitor();
		}

		Rectangle monitorBounds = monitor.getClientArea();
		Point centerPoint;
		if (parentShell != null) {
			centerPoint = Geometry.centerPoint(parentShell.getBounds());
		} else {
			centerPoint = Geometry.centerPoint(monitorBounds);
		}

		return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y, Math.min(centerPoint.y
				- (initialSize.y * 2 / 3), monitorBounds.y + monitorBounds.height - initialSize.y)));
	}

	private IDialogSettings getDialogSettings() {
		IDialogSettings settings = ContextUiPlugin.getDefault().getDialogSettings().getSection(sectionName);
		if (settings == null)
			settings = ContextUiPlugin.getDefault().getDialogSettings().addNewSection(sectionName);

		return settings;
	}

	private void storeBounds() {
		IDialogSettings dialogSettings = getDialogSettings();

		boolean controlRestoresSize = !dialogSettings.getBoolean(STORE_DISABLE_RESTORE_SIZE);
		boolean controlRestoresLocation = !dialogSettings.getBoolean(STORE_DISABLE_RESTORE_LOCATION);

		if (bounds == null)
			return;

		if (controlRestoresSize) {
			dialogSettings.put(STORE_SIZE_WIDTH, bounds.width);
			dialogSettings.put(STORE_SIZE_HEIGHT, bounds.height);
		}
		if (controlRestoresLocation) {
			dialogSettings.put(STORE_LOCATION_X, bounds.x);
			dialogSettings.put(STORE_LOCATION_Y, bounds.y);
		}
	}

	private Rectangle restoreBounds() {

		IDialogSettings dialogSettings = getDialogSettings();

		boolean controlRestoresSize = !dialogSettings.getBoolean(STORE_DISABLE_RESTORE_SIZE);
		boolean controlRestoresLocation = !dialogSettings.getBoolean(STORE_DISABLE_RESTORE_LOCATION);

		if (controlRestoresSize) {
			try {
				bounds.width = dialogSettings.getInt(STORE_SIZE_WIDTH);
				bounds.height = dialogSettings.getInt(STORE_SIZE_HEIGHT);
			} catch (NumberFormatException ex) {
				bounds.width = -1;
				bounds.height = -1;
			}
		}

		if (controlRestoresLocation) {
			try {
				bounds.x = dialogSettings.getInt(STORE_LOCATION_X);
				bounds.y = dialogSettings.getInt(STORE_LOCATION_Y);
			} catch (NumberFormatException ex) {
				bounds.x = -1;
				bounds.y = -1;
			}
		}

		// sanity check
		if (bounds.x == -1 && bounds.y == -1 && bounds.width == -1 && bounds.height == -1)
			return null;

		Rectangle maxBounds = null;
		if (dialogShell != null && !dialogShell.isDisposed())
			maxBounds = dialogShell.getDisplay().getBounds();
		else {
			// fallback
			Display display = Display.getCurrent();
			if (display == null)
				display = Display.getDefault();
			if (display != null && !display.isDisposed())
				maxBounds = display.getBounds();
		}

		if (bounds.width > -1 && bounds.height > -1) {
			if (maxBounds != null) {
				bounds.width = Math.min(bounds.width, maxBounds.width);
				bounds.height = Math.min(bounds.height, maxBounds.height);
			}
			// Enforce an absolute minimal size
			bounds.width = Math.max(bounds.width, 30);
			bounds.height = Math.max(bounds.height, 30);
		}

		if (bounds.x > -1 && bounds.y > -1 && maxBounds != null) {
			bounds.x = Math.max(bounds.x, maxBounds.x);
			bounds.y = Math.max(bounds.y, maxBounds.y);

			if (bounds.width > -1 && bounds.height > -1) {
				bounds.x = Math.min(bounds.x, maxBounds.width - bounds.width);
				bounds.y = Math.min(bounds.y, maxBounds.height - bounds.height);
			}
		}
		return bounds;
	}

	// ----------- all to do with filtering text

	private Text createFilterText(Composite parent) {
		filterText = new Text(parent, SWT.NONE);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();

		data.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		filterText.setLayoutData(data);

		filterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) // return
					gotoSelectedElement();
				if (e.keyCode == SWT.ARROW_DOWN)
					viewer.getTree().setFocus();
				if (e.keyCode == SWT.ARROW_UP)
					viewer.getTree().setFocus();
				if (e.character == 0x1B) // ESC
					dispose();
			}

			public void keyReleased(KeyEvent e) {
				// do nothing
			}
		});

		return filterText;
	}

	private void gotoSelectedElement() {
		Object selectedElement = getSelectedElement();
		if (selectedElement instanceof IStructuredSelection) {
			// Object sel =(IStructuredSelection) selectedElement;
			// Object data = ((TreeObject) sel).getData();
			// if (data != null) {
			// if (data instanceof IXReferenceNode) {
			// XRefUIUtils.revealInEditor(((IXReferenceNode)data).getJavaElement());
			// } else if (data instanceof IJavaElement) {
			// XRefUIUtils.revealInEditor((IJavaElement) data);
			// }
			// dispose();
			// }
		}
	}

	private Object getSelectedElement() {
		if (viewer == null)
			return null;
		return ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}

	private void installFilter() {
		filterText.setText(""); //$NON-NLS-1$

		filterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
//				String text = ((Text) e.widget).getText();
//				int length = text.length();
//				if (length > 0 && text.charAt(length - 1) != '*') {
//					text = text + '*';
//				}
//				 setMatcherString(text);
			}
		});
	}

	protected static class NamePatternFilter extends ViewerFilter {

		public NamePatternFilter() {
			// don't have anything to initialize
		}

		@Override
		public boolean select(Viewer selectedViewer, Object parentElement, Object element) {
			// StringMatcher matcher = getMatcher();
			// if (matcher == null || !(selectedViewer instanceof TreeViewer))
			// return true;
			TreeViewer treeViewer = (TreeViewer) selectedViewer;

			// String matchName = ((ILabelProvider)
			// treeViewer.getLabelProvider())
			// .getText(element);
			// if (matchName != null && matcher.match(matchName))
			// return true;

			return hasUnfilteredChild(treeViewer, element);
		}

		private boolean hasUnfilteredChild(TreeViewer treeViewer, Object element) {
			// if (element instanceof TreeParent) {
			// Object[] children = ((ITreeContentProvider) viewer
			// .getContentProvider()).getChildren(element);
			// for (int i = 0; i < children.length; i++)
			// if (select(viewer, element, children[i]))
			// return true;
			// }
			return false;
		}
	}

	// private StringMatcher getMatcher() {
	// return stringMatcher;
	// }

	/**
	 * Static inner class which sets the layout for the inplace view. Without
	 * this, the inplace view will not be populated.
	 * 
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl
	 */
	private static class BorderFillLayout extends Layout {

		/** The border widths. */
		final int fBorderSize;

		/**
		 * Creates a fill layout with a border.
		 */
		public BorderFillLayout(int borderSize) {
			if (borderSize < 0)
				throw new IllegalArgumentException();
			fBorderSize = borderSize;
		}

		/**
		 * Returns the border size.
		 */
		public int getBorderSize() {
			return fBorderSize;
		}

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {

			Control[] children = composite.getChildren();
			Point minSize = new Point(0, 0);

			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					Point size = children[i].computeSize(wHint, hHint, flushCache);
					minSize.x = Math.max(minSize.x, size.x);
					minSize.y = Math.max(minSize.y, size.y);
				}
			}

			minSize.x += fBorderSize * 2 + 3;
			minSize.y += fBorderSize * 2;

			return minSize;
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {

			Control[] children = composite.getChildren();
			Point minSize = new Point(composite.getClientArea().width, composite.getClientArea().height);

			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					Control child = children[i];
					child.setSize(minSize.x - fBorderSize * 2, minSize.y - fBorderSize * 2);
					child.setLocation(fBorderSize, fBorderSize);
				}
			}
		}
	}

	// ---------- shuts down the dialog ---------------

	/**
	 * Close the dialog
	 */
	public void close() {
		storeBounds();
		toolBar = null;
		viewMenuManager = null;
	}

	public void dispose() {
		filterText = null;
		if (dialogShell != null) {
			if (!dialogShell.isDisposed())
				dialogShell.dispose();
			dialogShell = null;
			parentShell = null;
			viewer = null;
			composite = null;
		}
	}

	// ------------------ moving actions --------------------------

	/**
	 * Move action for the dialog.
	 */
	private class MoveAction extends Action {

		MoveAction() {
			super("move", IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
		}

		@Override
		public void run() {
			performTrackerAction(SWT.NONE);
			isDeactivateListenerActive = true;
		}

	}

	/**
	 * Remember bounds action for the dialog.
	 */
	private class RememberBoundsAction extends Action {

		RememberBoundsAction() {
			super("remember-bounds", IAction.AS_CHECK_BOX); //$NON-NLS-1$
			setChecked(!getDialogSettings().getBoolean(STORE_DISABLE_RESTORE_LOCATION));
		}

		@Override
		public void run() {
			IDialogSettings settings = getDialogSettings();

			boolean newValue = !isChecked();
			// store new value
			settings.put(STORE_DISABLE_RESTORE_LOCATION, newValue);
			settings.put(STORE_DISABLE_RESTORE_SIZE, newValue);

			isDeactivateListenerActive = true;
		}
	}

	/**
	 * Resize action for the dialog.
	 */
	private class ResizeAction extends Action {

		ResizeAction() {
			super("resize", IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
		}

		@Override
		public void run() {
			performTrackerAction(SWT.RESIZE);
			isDeactivateListenerActive = true;
		}

	}

	/**
	 * Perform the requested tracker action (resize or move).
	 * 
	 * @param style
	 *            The track style (resize or move).
	 */
	private void performTrackerAction(int style) {
		Tracker tracker = new Tracker(dialogShell.getDisplay(), style);
		tracker.setStippled(true);
		Rectangle[] r = new Rectangle[] { dialogShell.getBounds() };
		tracker.setRectangles(r);

		if (tracker.open()) {
			dialogShell.setBounds(tracker.getRectangles()[0]);

		}
	}

	// -------------------- all to do with the contents of the view
	// --------------------

	private void createContents() {
//		if (lastSelection != null && workbenchPart != null) {
// IXReferenceAdapter xra =
// XRefUIUtils.getXRefAdapterForSelection(workbenchPart,lastSelection);
// if (xra != null) {
// viewer.setInput(xra);
// }
//		}
	}

	/**
	 * @param lastSelection
	 *            The lastSelection to set.
	 */
	public void setLastSelection(ISelection lastSelection) {
//		this.lastSelection = lastSelection;
	}

	/**
	 * @param workbenchPart
	 *            The workbenchPart to set.
	 */
	public void setWorkbenchPart(IWorkbenchPart workbenchPart) {
//		this.workbenchPart = workbenchPart;
	}

	public boolean isOpen() {
		return dialogShell != null;
	}
}

// private void setMatcherString(String pattern) {
// if (pattern.length() == 0) {
// stringMatcher = null;
// } else {
// boolean ignoreCase = pattern.toLowerCase(Locale.ENGLISH).equals(pattern);
// stringMatcher = new StringMatcher(pattern, ignoreCase, false);
// }
// stringMatcherUpdated();
// }

// private void stringMatcherUpdated() {
// // refresh viewer to refilter
// viewer.getControl().setRedraw(false);
// viewer.refresh();
// viewer.expandAll();
// selectFirstMatch();
// viewer.getControl().setRedraw(true);
// }

// private void selectFirstMatch() {
// Tree tree = viewer.getTree();
// Object element = findElement(tree.getItems());
// if (element != null)
// viewer.setSelection(new StructuredSelection(element), true);
// else
// viewer.setSelection(StructuredSelection.EMPTY);
// }

// private Object findElement(TreeItem[] items) {
// for (int i = 0; i < items.length; i++) {
// Object o = items[i].getData();
// TreeParent treeParent = null;
// TreeObject treeObject = null;
// if (o instanceof TreeParent) {
// treeParent = (TreeParent) o;
// } else if (o instanceof TreeObject) {
// treeObject = (TreeObject) o;
// }
// Object element = null;
// if (treeParent == null) {
// element = treeObject;
// } else {
// element = treeParent;
// }
// if (stringMatcher == null)
// return element;
//
// if (element != null) {
// String label = labelProvider.getText(element);
// if (stringMatcher.match(label))
// return element;
// }
//
// element = findElement(items[i].getItems());
// if (element != null)
// return element;
// }
// return null;
// }
