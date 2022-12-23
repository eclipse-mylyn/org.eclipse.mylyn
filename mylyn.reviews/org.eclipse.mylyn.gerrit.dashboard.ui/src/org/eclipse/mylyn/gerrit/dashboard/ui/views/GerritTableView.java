/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the plug-in
 *   Francois Chouinard - Handle gerrit queries and open reviews in editor
 *   Guy Perron			- Add review counter, Add Gerrit button selection
 *   Jacques Bouthillier - Bug 426580 Add the starred functionality
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.DelayedRefreshJob;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritQueryException;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTaskDataCollector;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.model.ReviewTableData;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.model.UIReviewTable;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.SelectionDialog;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIUtils;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritQuery;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Version;

import com.google.common.base.Strings;

/**
 * This class initiate a new workbench view. The view shows data obtained from Gerrit Dashboard model. The view is
 * connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view.
 *
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */

@SuppressWarnings("restriction")
public class GerritTableView extends ViewPart implements ITaskListChangeListener {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	//Constant to move to GerritQuery.java. Those values need to match the Gerrit request in GerritClient
	public static final String MY_CHANGES_STRING = "owner:self OR reviewer:self"; //$NON-NLS-1$

	public static final String MY_WATCHED_CHANGES_STRING = "is:watched status:open"; //$NON-NLS-1$

	public static final String ALL_OPEN_CHANGES_STRING = "status:open"; //$NON-NLS-1$

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String VIEW_ID = "org.eclipse.mylyn.gerrit.dashboard.ui.views.GerritTableView"; //$NON-NLS-1$

	private static final int REPO_WIDTH = 170;

	private static final int VERSION_WIDTH = 35;

	//Numbers of menu items in the Search pulldown menu; SEARCH_SIZE_MENU_LIST + 1 will be the max
	private static final int SEARCH_SIZE_MENU_LIST = 4;

	private static final String ADJUST_MY_STARRED_COMMAND_ID = "org.eclipse.mylyn.gerrit.dashboard.ui.adjustMyStarred"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
	// Member variables
	// ------------------------------------------------------------------------

	private GerritConnector fConnector = GerritCorePlugin.getDefault().getConnector();

	private TaskRepository fTaskRepository = null;

	private RepositoryQuery fCurrentQuery = null;

	private Label fRepositoryVersionResulLabel;

	private Label fReviewsTotalLabel;

	private Label fReviewsTotalResultLabel;

	private Combo fSearchRequestText;

	private Button fSearchRequestBtn;

	private Set<String> fRequestList = new LinkedHashSet<String>();

	private TableViewer fViewer;

	private ReviewTableData fReviewTable = new ReviewTableData();

	private GerritServerUtility fServerUtil = GerritServerUtility.getInstance();

	private Map<TaskRepository, String> fMapRepoServer = null;

	private Action doubleClickAction;

	private final LinkedHashSet<Job> fJobs = new LinkedHashSet<Job>();

	// ------------------------------------------------------------------------
	// TableRefreshJob
	// ------------------------------------------------------------------------

	private TableRefreshJob fTableRefreshJob;

	// Periodical refreshing job
	private final class TableRefreshJob extends DelayedRefreshJob {

		private TableRefreshJob(TableViewer viewer, String name) {
			super(viewer, name);
		}

		@Override
		protected void doRefresh(Object[] items) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					fViewer.setInput(fReviewTable.getReviews());
					//Refresh the counter
					setReviewsTotalResultLabel(Integer.toString(fReviewTable.getReviews().length));
					fViewer.refresh(false, false);
				}
			});
		}
	}

	public GerritTableView() {
	}

	public boolean setConnector(GerritConnector aConnector) {
		boolean b = true;
		if (aConnector == null) {
			fConnector = GerritCorePlugin.getDefault().getConnector();
		} else {
			fConnector = aConnector;
		}

		if (fConnector == null) {
			b = false; //Not ok
		}
		return b;
	}

	public void setReviewTableData(ReviewTableData ReviewTable) {
		fReviewTable = ReviewTable;
	}

	public void setGerritServerUtility(GerritServerUtility ServerUtil) {
		fServerUtil = ServerUtil;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		TasksUiPlugin.getTaskList().removeChangeListener(this);
		fTableRefreshJob.cancel();
		cleanJobs();
	}

	private void cleanJobs() {
		Iterator<Job> iter = fJobs.iterator();
		while (iter.hasNext()) {
			Job job = iter.next();
			job.sleep();
			job.cancel();
		}
		fJobs.clear();
	}

	/**
	 * Refresh the view content
	 */
	private void refresh() {
		fTableRefreshJob.doRefresh(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite aParent) {
		ScrolledComposite sc = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		Composite c = new Composite(sc, SWT.NONE);
		sc.setContent(c);
		sc.setExpandVertical(true);

		createSearchSection(c);
		UIReviewTable reviewTable = new UIReviewTable();
		fViewer = reviewTable.createTableViewerSection(c);

		// Setup the view layout
		createLayout(c);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();

		// Start the periodic refresh job
		fTableRefreshJob = new TableRefreshJob(fViewer, Messages.GerritTableView_refreshTable);

		// Listen on query results
		TasksUiPlugin.getTaskList().addChangeListener(this);

		sc.setMinSize(c.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createLayout(Composite aParent) {

		//Add a listener when the view is resized
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 1;
		layout.makeColumnsEqualWidth = false;

		aParent.setLayout(layout);
	}

	/**
	 * Create a group to show the search command and a search text
	 *
	 * @param Composite
	 *            aParent
	 */
	private void createSearchSection(Composite aParent) {

		final Group formGroup = new Group(aParent, SWT.SHADOW_ETCHED_IN | SWT.H_SCROLL);

		GridData gridDataGroup = new GridData(GridData.FILL_HORIZONTAL);
		formGroup.setLayoutData(gridDataGroup);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.makeColumnsEqualWidth = false;

		formGroup.setLayout(layout);

		//Left side of the Group
		//Create a form to maintain the search data
		Composite leftSearchForm = UIUtils.createsGeneralComposite(formGroup, SWT.NONE);

		GridLayout leftLayoutForm = new GridLayout();
		leftLayoutForm.numColumns = 3;
		leftLayoutForm.marginHeight = 0;
		leftLayoutForm.makeColumnsEqualWidth = false;
		leftLayoutForm.horizontalSpacing = 0;

		leftSearchForm.setLayout(leftLayoutForm);

		//Label to display the repository and the version
		fRepositoryVersionResulLabel = new Label(leftSearchForm, SWT.NONE);
		fRepositoryVersionResulLabel.setLayoutData(new GridData(REPO_WIDTH, SWT.DEFAULT));

		//Label to display Total reviews
		fReviewsTotalLabel = new Label(leftSearchForm, SWT.NONE);
		fReviewsTotalLabel.setText(Messages.GerritTableView_totalReview);

		fReviewsTotalResultLabel = new Label(leftSearchForm, SWT.NONE);
		fReviewsTotalResultLabel.setLayoutData(new GridData(VERSION_WIDTH, SWT.DEFAULT));

		//Right side of the Group
		Composite rightSearchForm = UIUtils.createsGeneralComposite(formGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(rightSearchForm);

		GridLayout rightLayoutForm = new GridLayout();
		rightLayoutForm.numColumns = 2;
		rightLayoutForm.marginHeight = 0;
		rightLayoutForm.makeColumnsEqualWidth = false;
		rightSearchForm.setLayout(rightLayoutForm);

		//Create a SEARCH text data entry
		fSearchRequestText = new Combo(rightSearchForm, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fSearchRequestText);
		fSearchRequestText.setToolTipText(Messages.GerritTableView_tooltipSearch);
		//Get the last saved commands
		fRequestList = fServerUtil.getListLastCommands();
		setSearchText(""); //$NON-NLS-1$

		//Handle the CR in the search text
		fSearchRequestText.addListener(SWT.DefaultSelection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				//The shorten request is: "is:open" with 7 characters, so need to process the command if text is smaller
				if (fSearchRequestText.getText().trim().length() > 6) {
					processCommands(GerritQuery.CUSTOM);
				}
			}
		});

		//Create a SEARCH button
		fSearchRequestBtn = new Button(rightSearchForm, SWT.NONE);
		fSearchRequestBtn.setText(Messages.GerritTableView_search);
		fSearchRequestBtn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				processCommands(GerritQuery.CUSTOM);
			}
		});

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager(Messages.GerritTableView_popupMenu);
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				GerritTableView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}

	private void fillContextMenu(IMenuManager manager) {
		CommandContributionItem[] contribItems = buildContributions();
		for (CommandContributionItem contribItem : contribItems) {
			manager.add(contribItem);
		}
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			@Override
			public void run() {

				// -------------------------------------------------
				// Open an editor with the detailed task information
				// -------------------------------------------------

				// Retrieve the single table selection ("the" task)
				ISelection selection = fViewer.getSelection();
				if (!(selection instanceof IStructuredSelection)) {
					return;
				}
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (structuredSelection.size() != 1) {
					return;
				}
				Object element = structuredSelection.getFirstElement();
				if (element instanceof ITask) {
					TasksUiUtil.openTask(fTaskRepository, ((ITask) element).getTaskId());
				}
				if (element instanceof GerritTask) {
					//Refresh the table column with the appropriate data, so the "CR" and "V" column gets updated
					fReviewTable.updateReviewItem((GerritTask) element);
					refresh();
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	/**
	 * Create a list for commands to add to the table review list menu
	 *
	 * @return CommandContributionItem[]
	 */
	private CommandContributionItem[] buildContributions() {
		IServiceLocator serviceLocator = getViewSite().getActionBars().getServiceLocator();
		CommandContributionItem[] contributionItems = new CommandContributionItem[1];
		CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(serviceLocator,
				Messages.GerritTableView_starredName, ADJUST_MY_STARRED_COMMAND_ID, CommandContributionItem.STYLE_PUSH);

		contributionParameter.label = Messages.GerritTableView_starredName;
		contributionParameter.visibleEnabled = true;
		contributionItems[0] = new CommandContributionItem(contributionParameter);

		return contributionItems;

	}

	public TableViewer getTableViewer() {
		return fViewer;
	}

	public TaskRepository getTaskRepository() {
		processCommands(""); //To initialize the fTaskRepository  //$NON-NLS-1$
		return fTaskRepository;
	}

	/**
	 * @param create
	 *            whether to create the view if it doesn't already exist
	 */
	public static GerritTableView getActiveView(boolean create) {
		IWorkbench workbench = GerritUi.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			if (page != null) {
				IViewPart viewPart = page.findView(VIEW_ID);
				if (viewPart == null && create) {
					viewPart = createView(page);
				}
				return (GerritTableView) viewPart;
			}
		}
		return null;
	}

	private static IViewPart createView(IWorkbenchPage page) {
		try {
			return page.showView(VIEW_ID, null, org.eclipse.ui.IWorkbenchPage.VIEW_CREATE);
		} catch (PartInitException e) {
			StatusHandler.log(new Status(IStatus.WARNING, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return null;
	}

	/**
	 * bring the Gerrit Dashboard view visible to the current workbench
	 */
	public void openView() {
		IWorkbench workbench = GerritUi.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(VIEW_ID);
		// if the review view is not showed yet,
		if (viewPart == null) {
			try {
				viewPart = page.showView(VIEW_ID);
			} catch (PartInitException e) {
				StatusHandler.log(new Status(IStatus.WARNING, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}
		// if there exists the view, but if not on the top,
		// then brings it to top when the view is already showed.
		else if (!page.isPartVisible(viewPart)) {
			page.bringToTop(viewPart);
		}
	}

	/**
	 * Process the commands based on the Gerrit string
	 *
	 * @param String
	 *            aQuery
	 */
	public void processCommands(String aQuery) {
		GerritUi.Ftracer.traceInfo("Process command :   " + aQuery);
		String lastSaved = fServerUtil.getLastSavedGerritServer();
		if (lastSaved != null) {
			//Already saved a Gerrit server, so use it
			fTaskRepository = fServerUtil.getTaskRepo(lastSaved);
		}

		if (fTaskRepository == null) {
			//If we did not find the task Repository
			fMapRepoServer = GerritServerUtility.getInstance().getGerritMapping();
			//Verify How many gerrit server are defined
			Set<TaskRepository> mapSet = fMapRepoServer.keySet();
			if (fMapRepoServer.size() == 1) {
				for (TaskRepository key : mapSet) {
					fTaskRepository = key;
					//Save it for the next query time
					fServerUtil.saveLastGerritServer(key.getRepositoryUrl());
					break;
				}

			} else if (fMapRepoServer.size() > 1) {
				List<TaskRepository> listTaskRepository = new ArrayList<TaskRepository>();
				for (TaskRepository key : mapSet) {
					listTaskRepository.add(key);
				}
				fTaskRepository = getSelectedRepositoryURL(listTaskRepository);
				if (fTaskRepository != null) {
					//Save it for the next query time
					fServerUtil.saveLastGerritServer(fTaskRepository.getRepositoryUrl());
				}
			}
		}

		//We should have a TaskRepository here, otherwise, the user need to define one
		if (fTaskRepository == null) {
			UIUtils.showErrorDialog(Messages.GerritTableView_defineRepository,
					Messages.GerritTableView_noGerritRepository);
		} else {
			if (aQuery != null && !aQuery.equals("")) { //$NON-NLS-1$
				updateTable(fTaskRepository, aQuery);
			}
		}
	}

	/**
	 * Process the command to set the Starred flag on the Gerrit server String taskId boolean starred
	 *
	 * @param progressMonitor
	 * @return void
	 * @throws CoreException
	 */
	public void setStarred(String taskID, boolean starred, IProgressMonitor progressMonitor) throws CoreException {
		if (fTaskRepository == null) {
			UIUtils.showErrorDialog(Messages.GerritTableView_defineRepository,
					Messages.GerritTableView_noGerritRepository);
		} else {
			fConnector.setStarred(fTaskRepository, taskID, starred, progressMonitor);
		}
	}

	/**
	 * Find the last Gerrit server being used , otherwise consider the Eclipse.org gerrit server version as a default
	 *
	 * @return Version
	 */
	public Version getlastGerritServerVersion() {
		Version version = null;
		String lastSaved = fServerUtil.getLastSavedGerritServer();
		if (lastSaved != null) {
			//Already saved a Gerrit server, so use it
			fTaskRepository = fServerUtil.getTaskRepo(lastSaved);
		}

		if (fTaskRepository == null) {
			//If we did not find the task Repository
			fMapRepoServer = GerritServerUtility.getInstance().getGerritMapping();
			//Verify How many gerrit server are defined
			if (fMapRepoServer.size() == 1) {
				Set<TaskRepository> mapSet = fMapRepoServer.keySet();
				for (TaskRepository key : mapSet) {
					fTaskRepository = key;
					//Save it for the next query time
					fServerUtil.saveLastGerritServer(key.getRepositoryUrl());
					break;
				}

			}
		}

		//We should have a TaskRepository here, otherwise, the user need to define one
		if (fTaskRepository != null) {
			if (setConnector(fConnector)) {
				GerritClient gerritClient = fConnector.getClient(fTaskRepository);
				version = gerritClient.getVersion();
				GerritUi.Ftracer.traceInfo("Selected version: " + version.toString()); //$NON-NLS-1$
			}
		}
		return version;
	}

	/**
	 * Verify if the Gerrit version is before 2.5
	 *
	 * @return boolean
	 */
	public boolean isGerritVersionBefore_2_5() {
		boolean ret = false;

		Version version = getlastGerritServerVersion();
		if (version != null && version.getMajor() >= 2) {
			if (version.getMinor() < 5) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * @param aTaskRepo
	 * @param aQueryType
	 * @return
	 */
	private Object updateTable(final TaskRepository aTaskRepo, final String aQueryType) {

		String cmdMessage = NLS.bind(Messages.GerritTableView_commandMessage, aTaskRepo.getUrl(), aQueryType);
		final Job job = new Job(cmdMessage) {

			@Override
			public boolean belongsTo(Object aFamily) {
				return Messages.GerritTableView_dashboardUiJob.equals(aFamily);
			}

			@Override
			public IStatus run(final IProgressMonitor aMonitor) {
				GerritPlugin.Ftracer.traceInfo("repository:   " + aTaskRepo.getUrl() + "\t query: " + aQueryType); //$NON-NLS-1$ //$NON-NLS-2$

				// If there is only have one Gerrit server, we can proceed as if it was already used before
				IStatus status = null;
				try {
					fReviewTable.createReviewItem(aQueryType, aTaskRepo);
					status = getReviews(aTaskRepo, aQueryType);
					if (status.isOK()) {
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								if (aQueryType != GerritQuery.CUSTOM) {
									if (fCurrentQuery != null) {
										String query = fCurrentQuery.getAttribute(GerritQuery.QUERY_STRING);
										setSearchText(query);
									}
								} else {
									//Record the custom query
									setSearchText(getSearchText());
								}
								boolean ok = setConnector(fConnector);
								GerritClient gerritClient = null;
								if (ok) {
									gerritClient = fConnector.getClient(aTaskRepo);
								}
								if (gerritClient != null) {
									setRepositoryVersionLabel(aTaskRepo.getRepositoryLabel(),
											gerritClient.getVersion().toString());
								}
							}
						});
					}
				} catch (GerritQueryException e) {
					status = e.getStatus();
					StatusHandler
							.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getStatus().getMessage(), e));
				}

				aMonitor.done();
				fJobs.remove(this);
				return status;
			}
		};
		//Clean some Jobs if still running
		cleanJobs();

		fJobs.add(job);
		job.setUser(true);
		job.schedule();

		return null;
	}

	private void setSearchText(String aSt) {
		if (!fSearchRequestText.isDisposed()) {
			if (aSt != null && aSt != "") { //$NON-NLS-1$
				int index = -1;
				String[] ar = fSearchRequestText.getItems();
				for (int i = 0; i < ar.length; i++) {
					if (ar[i].equals(aSt.trim())) {
						index = i;
						break;
					}
				}

				if (index != -1) {
					fRequestList.remove(fRequestList.remove(ar[index]));
				} else {
					//Remove the oldest element from the list
					if (fRequestList.size() > SEARCH_SIZE_MENU_LIST) {
						Object obj = fRequestList.iterator().next(); //Should be the first item in the list
						fRequestList.remove(fRequestList.remove(obj));
					}
				}
				//Add the new text in the combo
				fRequestList.add(aSt.trim());
				//Save the list of commands in file
				fServerUtil.saveLastCommandList(fRequestList);

			}

			fSearchRequestText.setItems(reverseOrder(fRequestList.toArray(new String[0])));
			if (aSt != null && aSt != "") { //$NON-NLS-1$
				fSearchRequestText.select(0);
			} else {
				//Leave the text empty
				fSearchRequestText.setText(""); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Take the list of last save queries and reverse the order to have the latest selection to be the first one on the
	 * pull-down menu
	 *
	 * @param aList
	 *            String[]
	 * @return String[] reverse order
	 */
	private String[] reverseOrder(String[] aList) {
		int size = aList.length;
		int index = size - 1;
		String[] rev = new String[size];
		for (int i = 0; i < size; i++) {
			rev[i] = aList[index--];
		}
		return rev;
	}

	private String getSearchText() {
		if (!fSearchRequestText.isDisposed()) {
			final String[] str = new String[1];
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					str[0] = fSearchRequestText.getText().trim();
					GerritUi.Ftracer.traceInfo("Custom string: " + str[0]); //$NON-NLS-1$
				}
			});
			return str[0];
		}
		return null;
	}

	// ------------------------------------------------------------------------
	// Query handling
	// ------------------------------------------------------------------------

	private void displayWarning(final String st) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				final MessageDialog dialog = new MessageDialog(null, Messages.GerritTableView_warning, null, st,
						MessageDialog.WARNING, new String[] { IDialogConstants.CANCEL_LABEL }, 0);
				dialog.open();
			}
		});
	}

	/**
	 * Perform the requested query and convert the resulting tasks in GerritTask:s
	 *
	 * @param repository
	 *            the tasks repository
	 * @param queryType
	 *            the query
	 * @return IStatus
	 * @throws GerritQueryException
	 */
	private IStatus getReviews(TaskRepository repository, String queryType) throws GerritQueryException {
		if (repository.getUserName() == null || repository.getUserName().isEmpty()) {
			//Test for Anonymous user
			if (queryType.equals(GerritQuery.MY_CHANGES)
					|| queryType.equals(GerritQuery.QUERY_MY_DRAFTS_COMMENTS_CHANGES)) {
				displayWarning(NLS.bind(Messages.GerritTableView_warningAnonymous, queryType));
				return Status.CANCEL_STATUS;
			} else if (queryType == GerritQuery.CUSTOM) {
				int foundSelf = getSearchText().toLowerCase().indexOf("self"); //$NON-NLS-1$
				int foundhasDraft = getSearchText().toLowerCase().indexOf(GerritQuery.QUERY_MY_DRAFTS_COMMENTS_CHANGES);
				if (foundSelf != -1 || foundhasDraft != -1) {
					displayWarning(NLS.bind(Messages.GerritTableView_warningSearchAnonymous, getSearchText()));
					return Status.CANCEL_STATUS;
				}
			}
		}

		// Format the query id
		String queryId = getTitle() + " - " + queryType; //$NON-NLS-1$

		RepositoryQuery query = null;

		IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
		query = (RepositoryQuery) repositoryModel.createRepositoryQuery(repository);
		query.setSummary(queryId);
		query.setAttribute(GerritQuery.TYPE, queryType);
		query.setAttribute(GerritQuery.PROJECT, null);
		if (queryType == GerritQuery.CUSTOM) {
			query.setAttribute(GerritQuery.QUERY_STRING, getSearchText());
		} else {
			String st = matchQueryTypeRequest(queryType);
			query.setAttribute(GerritQuery.QUERY_STRING, st);
		}

		if (query.getAttribute(GerritQuery.QUERY_STRING).isEmpty()) {
			displayWarning(Messages.GerritTableView_warningEmptyValue);
			return Status.CANCEL_STATUS;
		}

		// Save query
		fCurrentQuery = query;

		// Fetch the list of reviews and pre-populate the table
		GerritTask[] reviews = getReviewList(repository, query);

		fReviewTable.init(reviews);
		refresh();

		return Status.OK_STATUS;

	}

	/**
	 * We need to use the define in GerritQuery.java for the missing one
	 *
	 * @param queryType
	 * @return queryString
	 */
	private String matchQueryTypeRequest(String queryType) {
		if (queryType.equals(GerritQuery.ALL_OPEN_CHANGES)) {
			return ALL_OPEN_CHANGES_STRING;
		} else if (queryType.equals(GerritQuery.MY_CHANGES)) {
			return MY_CHANGES_STRING;
		} else if (queryType.equals(GerritQuery.MY_WATCHED_CHANGES)) {
			return MY_WATCHED_CHANGES_STRING;
		}
		return queryType;
	}

	private GerritTask[] getReviewList(TaskRepository repository, RepositoryQuery aQuery) throws GerritQueryException {

		// Execute the query
		GerritTaskDataCollector resultCollector = new GerritTaskDataCollector();
		IStatus status = null;
		boolean ok = setConnector(fConnector);

		if (ok) {
			status = fConnector.performQuery(repository, aQuery, resultCollector, null, new NullProgressMonitor());
		} else {
			status = new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					NLS.bind(Messages.GerritTableView_missingGitConnector, aQuery.getAttribute(GerritQuery.PROJECT)));
		}

		if (!status.isOK()) {
			throw new GerritQueryException(status, Messages.GerritTableView_serverNotRead);
		}

		// Extract the result
		List<GerritTask> reviews = new ArrayList<GerritTask>();
		List<TaskData> tasksData = resultCollector.getResults();
		for (TaskData taskData : tasksData) {
			GerritTask review = new GerritTask(taskData);
			reviews.add(review);
		}
		return reviews.toArray(new GerritTask[0]);
	}

	// ------------------------------------------------------------------------
	// ITaskListChangeListener
	// ------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener#containersChanged(java.util.Set)
	 */
	@Override
	public void containersChanged(final Set<TaskContainerDelta> deltas) {
		for (TaskContainerDelta taskContainerDelta : deltas) {
			IRepositoryElement element = taskContainerDelta.getElement();
			switch (taskContainerDelta.getKind()) {
			case ROOT:
				refresh();
				break;
			case ADDED:
			case CONTENT:
				if (element != null && element instanceof TaskTask) {
					updateReview((TaskTask) element);
				}
				refresh();
				break;
			case DELETED:
			case REMOVED:
				if (element != null && element instanceof TaskTask) {
					deleteReview((TaskTask) element);
				}
				refresh();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Delete a review
	 */
	private synchronized void deleteReview(TaskTask task) {
		fReviewTable.deleteReviewItem(task.getTaskId());
	}

	/**
	 * Add/update a review
	 */
	private synchronized void updateReview(TaskTask task) {
		boolean ourQuery = task.getParentContainers().contains(fCurrentQuery);
		if (ourQuery && !Strings.isNullOrEmpty(task.getSummary())) {
			try {
				TaskData taskData = fConnector.getTaskData(getTaskRepository(), task.getTaskId(),
						new NullProgressMonitor());
				GerritTask gtask = new GerritTask(taskData);
				if (gtask.getAttribute(GerritTask.DATE_COMPLETION) == null) {
					fReviewTable.updateReviewItem(gtask);
				}
			} catch (CoreException e) {
				StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
			}
		}

	}

	private void setRepositoryVersionLabel(String aRepo, String aVersion) {
		if (!fRepositoryVersionResulLabel.isDisposed()) {
			// e.g. "Eclipse.org Reviews - Gerrit 2.6.1"
			fRepositoryVersionResulLabel.setText(NLS.bind(Messages.GerritTableView_gerritLabel, aRepo, aVersion));
		}
	}

	private void setReviewsTotalResultLabel(String aSt) {
		if (!fReviewsTotalResultLabel.isDisposed()) {
			fReviewsTotalResultLabel.setText(aSt);
		}
	}

	private TaskRepository getSelectedRepositoryURL(final List<TaskRepository> listTaskRepository) {
		String selection = null;
		SelectionDialog taskSelection = new SelectionDialog(fViewer.getTable().getShell(), listTaskRepository);
		if (taskSelection.open() == Window.OK) {
			selection = taskSelection.getSelection();
		}
		return fServerUtil.getTaskRepo(selection);
	}

}
