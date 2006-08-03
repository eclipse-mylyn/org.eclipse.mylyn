package org.eclipse.mylar.internal.context.ui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.tasks.ui.TaskListPatternFilter;
import org.eclipse.mylar.internal.tasks.ui.views.AdaptiveRefreshPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * Based on
 * 
 * @see{FilteredTree}
 */
public class NavigatorFilterBar extends Composite {

	protected Job refreshJob;

	private InterestFilter suppressedFilter = null;

	private AdaptiveRefreshPolicy refreshPolicy;

	public static class ResourcePatternFilter extends TaskListPatternFilter {
		@Override
		protected boolean isLeafMatch(Viewer viewer, Object element) {
			return super.isLeafMatch(viewer, element);
		}
	}

	public NavigatorFilterBar(TreeViewer treeViewer, Composite parent, ResourcePatternFilter filter) {
		super(parent, SWT.NONE);
		this.treeViewer = treeViewer;
		patternFilter = filter;
		createControl(parent);
		createRefreshJob();
		setInitialText("");
		setFont(parent.getFont());
		refreshPolicy = new AdaptiveRefreshPolicy(refreshJob, filterText);
	}

	protected void textChanged() {
		int textLength = filterText.getText().length();
		InterestFilter interestFilter = getInterestFilter();
		if (textLength > 0 && interestFilter != null && suppressedFilter == null) {
			suppressedFilter = interestFilter;
			getViewer().removeFilter(interestFilter);
		} else if (textLength == 0 && suppressedFilter != null) {
			getViewer().addFilter(suppressedFilter);
			getViewer().expandAll();
			suppressedFilter = null;
		}
		if (refreshPolicy != null) {
			refreshPolicy.textChanged(filterText.getText());
		}
		// super.textChanged();
	}

	private InterestFilter getInterestFilter() {
		if (getViewer() == null) {
			return null;
		}
		ViewerFilter[] filters = getViewer().getFilters();
		for (int i = 0; i < filters.length; i++) {
			ViewerFilter filter = filters[i];
			if (filter instanceof InterestFilter) {
				return (InterestFilter) filter;
			}
		}
		return null;
	}

	// ----------------- below based on FilteredTree ---------------

	protected Text filterText;

	protected ToolBarManager filterToolBar;

	protected TreeViewer treeViewer;

	protected Composite filterComposite;

	private ResourcePatternFilter patternFilter;

	protected String initialText = "";

	private static final String CLEAR_ICON = "org.eclipse.ui.internal.dialogs.CLEAR_ICON"; //$NON-NLS-1$

	private static final String DCLEAR_ICON = "org.eclipse.ui.internal.dialogs.DCLEAR_ICON"; //$NON-NLS-1$

	static {
		ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
				"$nl$/icons/full/etool16/clear_co.gif"); //$NON-NLS-1$
		if (descriptor != null) {
			JFaceResources.getImageRegistry().put(CLEAR_ICON, descriptor);
		}
		descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PlatformUI.PLUGIN_ID,
				"$nl$/icons/full/dtool16/clear_co.gif"); //$NON-NLS-1$
		if (descriptor != null) {
			JFaceResources.getImageRegistry().put(DCLEAR_ICON, descriptor);
		}
	}

	protected void createControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		filterComposite = new Composite(this, SWT.NONE);
		GridLayout filterLayout = new GridLayout(2, false);
		filterLayout.marginHeight = 0;
		filterLayout.marginWidth = 0;
		filterComposite.setLayout(filterLayout);
		filterComposite.setFont(parent.getFont());

		createFilterControls(filterComposite);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	protected Composite createFilterControls(Composite parent) {
		createFilterText(parent);
		createClearText(parent);

		filterToolBar.update(false);
		// initially there is no text to clear
		filterToolBar.getControl().setVisible(false);
		return parent;
	}

//	private TreeItem getFirstMatchingItem(TreeItem[] items) {
//		for (int i = 0; i < items.length; i++) {
//			if (patternFilter.isLeafMatch(treeViewer, items[i].getData())
//					&& patternFilter.isElementSelectable(items[i].getData())) {
//				return items[i];
//			}
//			return getFirstMatchingItem(items[i].getItems());
//		}
//		return null;
//	}

	private void createRefreshJob() {
		refreshJob = new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (treeViewer.getControl().isDisposed()) {
					return Status.CANCEL_STATUS;
				}

				String text = getFilterString();
				if (text == null) {
					return Status.OK_STATUS;
				}

				boolean initial = initialText != null && initialText.equals(text);
				if (initial) {
					patternFilter.setPattern(null);
				} else if (text != null) {
					patternFilter.setPattern(text);
				}

				try {
					// don't want the user to see updates that will be made to
					// the tree
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh(true);

					if (text.length() > 0 && !initial) {
						/*
						 * Expand elements one at a time. After each is
						 * expanded, check to see if the filter text has been
						 * modified. If it has, then cancel the refresh job so
						 * the user doesn't have to endure expansion of all the
						 * nodes.
						 */
						IStructuredContentProvider provider = (IStructuredContentProvider) treeViewer
								.getContentProvider();
						Object[] elements = provider.getElements(treeViewer.getInput());
						for (int i = 0; i < elements.length; i++) {
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							treeViewer.expandToLevel(elements[i], AbstractTreeViewer.ALL_LEVELS);
						}

						TreeItem[] items = getViewer().getTree().getItems();
						if (items.length > 0) {
							// to prevent scrolling
							treeViewer.getTree().showItem(items[0]);
						}

						// enabled toolbar - there is text to clear
						// and the list is currently being filtered
						updateToolbar(true);
					} else {
						// disabled toolbar - there is no text to clear
						// and the list is currently not filtered
						updateToolbar(false);
					}
				} finally {
					// done updating the tree - set redraw back to true
					treeViewer.getControl().setRedraw(true);
				}
				return Status.OK_STATUS;
			}

		};
		refreshJob.setSystem(true);
	}

	protected void updateToolbar(boolean visible) {
		if (filterToolBar != null) {
			filterToolBar.getControl().setVisible(visible);
		}
	}

	protected void createFilterText(Composite parent) {
		filterText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		filterText.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.accessibility.AccessibleListener#getName(org.eclipse.swt.accessibility.AccessibleEvent)
			 */
			public void getName(AccessibleEvent e) {
				String filterTextString = filterText.getText();
				if (filterTextString.length() == 0) {
					e.result = initialText;
				} else {
					e.result = filterTextString;
				}
			}
		});

		filterText.addFocusListener(new FocusAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
			 */
			public void focusGained(FocusEvent e) {
				/*
				 * Running in an asyncExec because the selectAll() does not
				 * appear to work when using mouse to give focus to text.
				 */
				Display display = filterText.getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						if (!filterText.isDisposed()) {
							if (getInitialText().equals(filterText.getText().trim())) {
								filterText.selectAll();
							}
						}
					}
				});
			}
		});

		filterText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				// on a CR we want to transfer focus to the list
				boolean hasItems = getViewer().getTree().getItemCount() > 0;
				if (hasItems && e.keyCode == SWT.ARROW_DOWN) {
					// treeViewer.getTree().setFocus();
				} else if (e.character == SWT.CR) {
					textChanged();
					// return;
				} else if (e.character == SWT.ESC) {
					setFilterText("");
					textChanged();
				}
			}
		});

		// enter key set focus to tree
		filterText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					if (getViewer().getTree().getItemCount() == 0) {
						Display.getCurrent().beep();
					} else {
						// if the initial filter text hasn't changed, do not try
						// to match
						// boolean hasFocus = getViewer().getTree().setFocus();
						// boolean textChanged =
						// !getInitialText().equals(filterText.getText().trim());
						// if (hasFocus && textChanged &&
						// filterText.getText().trim().length() > 0) {
						// TreeItem item =
						// getFirstMatchingItem(getViewer().getTree().getItems());
						// if (item != null) {
						// getViewer().getTree().setSelection(new TreeItem[] {
						// item });
						// ISelection sel = getViewer().getSelection();
						// getViewer().setSelection(sel, true);
						// }
						// }
					}
				}
			}
		});

		filterText.addModifyListener(new ModifyListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				// textChanged();
			}
		});
		filterText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
	}

	public void setBackground(Color background) {
		super.setBackground(background);
		if (filterComposite != null) {
			filterComposite.setBackground(background);
		}
		if (filterToolBar != null && filterToolBar.getControl() != null) {
			filterToolBar.getControl().setBackground(background);
		}
	}

	private void createClearText(Composite parent) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL);
		filterToolBar = new ToolBarManager(toolBar);

		IAction clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {//$NON-NLS-1$
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			public void run() {
				clearText();
			}
		};

		clearTextAction.setToolTipText(WorkbenchMessages.FilteredTree_ClearToolTip);
		clearTextAction.setImageDescriptor(JFaceResources.getImageRegistry().getDescriptor(CLEAR_ICON));
		clearTextAction.setDisabledImageDescriptor(JFaceResources.getImageRegistry().getDescriptor(DCLEAR_ICON));

		filterToolBar.add(clearTextAction);
	}

	protected void clearText() {
		setFilterText(""); //$NON-NLS-1$
		textChanged();
	}

	protected void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}

	public final PatternFilter getPatternFilter() {
		return patternFilter;
	}

	public TreeViewer getViewer() {
		return treeViewer;
	}

	public Text getFilterControl() {
		return filterText;
	}

	protected String getFilterString() {
		return filterText != null ? filterText.getText() : null;
	}

	public void setInitialText(String text) {
		initialText = text;
		setFilterText(initialText);
		textChanged();
	}

	protected void selectAll() {
		if (filterText != null) {
			filterText.selectAll();
		}
	}

	protected String getInitialText() {
		return initialText;
	}

	public static Font getBoldFont(Object element, NavigatorFilterBar tree, ResourcePatternFilter filter) {
		String filterText = tree.getFilterString();

		if (filterText == null) {
			return null;
		}

		// Do nothing if it's empty string
		String initialText = tree.getInitialText();
		if (!("".equals(filterText) || initialText.equals(filterText))) {//$NON-NLS-1$
			boolean initial = initialText != null && initialText.equals(filterText);
			if (initial) {
				filter.setPattern(null);
			} else if (filterText != null) {
				filter.setPattern(filterText);
			}

			if (filter.isElementVisible(tree.getViewer(), element) && filter.isLeafMatch(tree.getViewer(), element)) {
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
		}
		return null;
	}

	public AdaptiveRefreshPolicy getRefreshPolicy() {
		return refreshPolicy;
	}

}
