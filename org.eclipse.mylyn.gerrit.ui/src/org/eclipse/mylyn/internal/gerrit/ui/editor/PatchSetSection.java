/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *     Sam Davis - improvements for bug 383592
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.egit.ui.internal.commit.CommitEditor;
import org.eclipse.egit.ui.internal.fetch.FetchGerritChangeWizard;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritTaskSchema;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.ReviewItemCache;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritPatchSetContent;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.egit.GerritToGitMapping;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.gerrit.ui.egit.EGitUiUtil;
import org.eclipse.mylyn.internal.gerrit.ui.operations.AbandonDialog;
import org.eclipse.mylyn.internal.gerrit.ui.operations.PublishDialog;
import org.eclipse.mylyn.internal.gerrit.ui.operations.RebaseDialog;
import org.eclipse.mylyn.internal.gerrit.ui.operations.RestoreDialog;
import org.eclipse.mylyn.internal.gerrit.ui.operations.SubmitDialog;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemCompareEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.PatchSetPublishDetail;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class PatchSetSection extends AbstractGerritSection {

	private class CompareAction extends Action {

		private final PatchSet base;

		private final PatchSet target;

		private final ChangeDetail changeDetail;

		public CompareAction(ChangeDetail changeDetail, PatchSet base, PatchSet target) {
			this.changeDetail = changeDetail;
			this.base = base;
			this.target = target;
		}

		public void fill(Menu menu) {
			MenuItem item = new MenuItem(menu, SWT.NONE);
			item.setText(NLS.bind("Compare with Patch Set {0}", base.getPatchSetId()));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					run();
				}
			});
		}

		@Override
		public void run() {
			doCompareWith(changeDetail, base, target);
		}

	}

	private Composite composite;

	private final List<Job> jobs;

	private FormToolkit toolkit;

	// XXX drafts added after the publish detail was refreshed from server
	private int addedDrafts;

	private final ReviewItemCache cache;

	public PatchSetSection() {
		setPartName("Patch Sets");
		this.jobs = new ArrayList<Job>();
		this.cache = new ReviewItemCache();
	}

	@Override
	public void dispose() {
		for (Job job : jobs) {
			job.cancel();
		}
		super.dispose();
	}

	public void updateTextClient(Section section, final PatchSetDetail patchSetDetail, boolean cachingInProgress) {
		String message;

		String time = DateFormat.getDateTimeInstance().format(patchSetDetail.getPatchSet().getCreatedOn());
		int numComments = getNumComments(patchSetDetail);
		if (numComments > 0) {
			message = NLS.bind("{0}, {1} Comments", time, numComments);
		} else {
			message = NLS.bind("{0}", time);
		}

		if (cachingInProgress) {
			message += " [Caching contents...]";
		}

		final Label textClientLabel = (Label) section.getTextClient();
		textClientLabel.setText("  " + message);
		textClientLabel.getParent().layout(true, true);
		//textClientLabel.setVisible(cachingInProgress || !section.isExpanded());
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
	}

	private Composite createActions(final ChangeDetail changeDetail, final PatchSetDetail patchSetDetail,
			final PatchSetPublishDetail publishDetail, Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.center = true;
		layout.spacing = 10;
		buttonComposite.setLayout(layout);

		boolean canPublish = getTaskData().getAttributeMapper().getBooleanValue(
				getTaskData().getRoot().getAttribute(GerritTaskSchema.getDefault().CAN_PUBLISH.getKey()));
		boolean canSubmit = false;
		boolean canRebase = false;
		if (changeDetail.getCurrentActions() != null) {
			canSubmit = changeDetail.getCurrentActions().contains(ApprovalCategory.SUBMIT);
		} else if (changeDetail instanceof ChangeDetailX) {
			// Gerrit 2.2 and later
			canSubmit = ((ChangeDetailX) changeDetail).canSubmit();
			canRebase = ((ChangeDetailX) changeDetail).canRebase();
		}

		if (canPublish) {
			Button publishButton = toolkit.createButton(buttonComposite, "Publish Comments...", SWT.PUSH);
			publishButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doPublish(publishDetail);
				}
			});
		}

		Button fetchButton = toolkit.createButton(buttonComposite, "Fetch...", SWT.PUSH);
		fetchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFetch(changeDetail, patchSetDetail);
			}
		});

		final Composite compareComposite = toolkit.createComposite(buttonComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).applyTo(compareComposite);

		Button compareButton = toolkit.createButton(compareComposite, "Compare With Base", SWT.PUSH);
		compareButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCompareWith(changeDetail, null, patchSetDetail.getPatchSet());
			}
		});

		if (changeDetail.getPatchSets().size() > 1) {
			Button compareWithButton = toolkit.createButton(compareComposite, "", SWT.PUSH);
			GridDataFactory.fillDefaults().grab(false, true).applyTo(compareWithButton);
			compareWithButton.setImage(WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU));
			compareWithButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					showCompareMenu(compareComposite, changeDetail, patchSetDetail);
				}

				private void showCompareMenu(Composite compareComposite, ChangeDetail changeDetail,
						PatchSetDetail patchSetDetail) {
					Menu menu = new Menu(compareComposite);
					Point p = compareComposite.getLocation();
					p.y = p.y + compareComposite.getSize().y;
					p = compareComposite.getParent().toDisplay(p);
					fillCompareWithMenu(changeDetail, patchSetDetail, menu);
					menu.setLocation(p);
					menu.setVisible(true);
				}
			});
		}

		if (canRebase) {
			Button rebaseButton = toolkit.createButton(buttonComposite, "Rebase", SWT.PUSH);
			rebaseButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doRebase(patchSetDetail.getPatchSet());
				}
			});
		}

		if (canSubmit) {
			Button submitButton = toolkit.createButton(buttonComposite, "Submit", SWT.PUSH);
			submitButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doSubmit(patchSetDetail.getPatchSet());
				}
			});
		}

		if (changeDetail != null && changeDetail.isCurrentPatchSet(patchSetDetail)) {
			if (changeDetail.canAbandon()) {
				Button abondonButton = toolkit.createButton(buttonComposite, "Abandon...", SWT.PUSH);
				abondonButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						doAbandon(patchSetDetail.getPatchSet());
					}
				});
			} else if (changeDetail.canRestore()) {
				Button restoreButton = toolkit.createButton(buttonComposite, "Restore...", SWT.PUSH);
				restoreButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						doRestore(patchSetDetail.getPatchSet());
					}
				});
			}
		}
		return buttonComposite;
	}

	void fillCompareWithMenu(ChangeDetail changeDetail, PatchSetDetail patchSetDetail, Menu menu) {
		for (PatchSet patchSet : changeDetail.getPatchSets()) {
			if (patchSet.getPatchSetId() != patchSetDetail.getPatchSet().getPatchSetId()) {
				CompareAction action = new CompareAction(changeDetail, patchSet, patchSetDetail.getPatchSet());
				action.fill(menu);
			}
		}
	}

	private void createSubSection(final ChangeDetail changeDetail, final PatchSetDetail patchSetDetail,
			final PatchSetPublishDetail publishDetail, Section section) {
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT
				| ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		if (changeDetail.isCurrentPatchSet(patchSetDetail)) {
			style |= ExpandableComposite.EXPANDED;
		}
		final Section subSection = toolkit.createSection(composite, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setText(NLS.bind("Patch Set {0}", patchSetDetail.getPatchSet().getId().get()));
		subSection.setTitleBarForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		addTextClient(toolkit, subSection, "", false); //$NON-NLS-1$
		updateTextClient(subSection, patchSetDetail, false);

		if (subSection.isExpanded()) {
			createSubSectionContents(changeDetail, patchSetDetail, publishDetail, subSection);
		}
		subSection.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (subSection.getClient() == null) {
					createSubSectionContents(changeDetail, patchSetDetail, publishDetail, subSection);
				}
			}
		});
	}

	private int getNumComments(PatchSetDetail patchSetDetail) {
		int numComments = 0;
		for (Patch patch : patchSetDetail.getPatches()) {
			numComments += patch.getCommentCount();
		}
		return numComments;
	}

	private void subSectionExpanded(final ChangeDetail changeDetail, final PatchSetDetail patchSetDetail,
			final Section composite, final Viewer viewer) {
		updateTextClient(composite, patchSetDetail, true);

		final GetPatchSetContentJob job = new GetPatchSetContentJob(getTaskEditorPage().getTaskRepository(),
				patchSetDetail);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (getControl() != null && !getControl().isDisposed()) {
							if (event.getResult().isOK()) {
								GerritPatchSetContent content = job.getPatchSetContent();
								if (content != null && content.getPatchScriptByPatchKey() != null) {
									viewer.setInput(GerritUtil.createInput(changeDetail, content, cache));
								}
							}

							updateTextClient(composite, patchSetDetail, false);
							getTaskEditorPage().reflow();
						}
					}
				});
			}
		});
		jobs.add(job);
		job.schedule();
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;

		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 0, 5).applyTo(composite);

		GerritChange change = GerritUtil.getChange(getTaskData());
		if (change != null) {
			for (PatchSetDetail patchSetDetail : change.getPatchSetDetails()) {
				PatchSet.Id patchSetId = patchSetDetail.getPatchSet().getId();
				PatchSetPublishDetail publishDetail = change.getPublishDetailByPatchSetId().get(patchSetId);
				createSubSection(change.getChangeDetail(), patchSetDetail, publishDetail, getSection());
			}
		}

		return composite;
	}

	protected void doAbandon(PatchSet patchSet) {
		AbandonDialog dialog = new AbandonDialog(getShell(), getTask(), patchSet);
		openOperationDialog(dialog);
	}

	protected void doPublish(PatchSetPublishDetail publishDetail) {
		PublishDialog dialog = new PublishDialog(getShell(), getTask(), publishDetail, addedDrafts);
		openOperationDialog(dialog);
	}

	protected void doFetch(ChangeDetail changeDetail, PatchSetDetail patchSetDetail) {
		GerritToGitMapping mapping = getRepository(changeDetail);
		if (mapping != null) {
			String refName = patchSetDetail.getPatchSet().getRefName();
			FetchGerritChangeWizard wizard = new FetchGerritChangeWizard(mapping.getRepository(), refName);
			WizardDialog wizardDialog = new WizardDialog(getShell(), wizard);
			wizardDialog.setHelpAvailable(false);
			wizardDialog.open();
		}
	}

	private GerritToGitMapping getRepository(ChangeDetail changeDetail) {
		GerritToGitMapping mapper = new GerritToGitMapping(getTaskEditorPage().getTaskRepository(), getConfig(),
				getGerritProject(changeDetail));
		try {
			if (mapper.find() != null) {
				return mapper;
			} else if (mapper.getGerritProject() != null) {
				boolean create = MessageDialog.openQuestion(getShell(), "Clone Git Repository",
						"The referenced Git repository was not found in the workspace. Clone Git repository?");
				if (create) {
					int response = EGitUiUtil.openCloneRepositoryWizard(getShell(),
							getTaskEditorPage().getTaskRepository(), mapper.getGerritProject());
					if (response == Window.OK && mapper.find() != null) {
						return mapper;
					}
				}
			} else {
				String message = NLS.bind("No Git repository found for fetching Gerrit change {0}",
						getTask().getTaskKey());
				String reason = NLS.bind(
						"No remote config found that has fetch URL with host ''{0}'' and path matching ''{1}''",
						mapper.getGerritHost(), mapper.getGerritProjectName());
				GerritCorePlugin.logError(message, null);
				ErrorDialog.openError(getShell(), "Gerrit Fetch Change Error", message, new Status(IStatus.ERROR,
						GerritUiPlugin.PLUGIN_ID, reason));
			}
		} catch (IOException e) {
			Status status = new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Error accessing Git repository", e);
			StatusManager.getManager().handle(status, StatusManager.BLOCK | StatusManager.SHOW | StatusManager.LOG);
		}
		return null;
	}

	protected void doCompareWithInSynchronizeView(ChangeDetail changeDetail, PatchSet base, PatchSet target) {
		GerritToGitMapping mapping = getRepository(changeDetail);
		if (mapping != null) {
			ComparePatchSetJob job = new ComparePatchSetJob(mapping.getRepository(), mapping.getRemote(), base, target);
			job.schedule();
		}
	}

	protected void doCompareWith(ChangeDetail changeDetail, PatchSet base, PatchSet target) {
		OpenPatchSetJob job = new OpenPatchSetJob(getTaskEditorPage().getTaskRepository(), getTask(), changeDetail,
				base, target, cache);
		job.schedule();
	}

	private String getGerritProject(ChangeDetail changeDetail) {
		return changeDetail.getChange().getProject().get();
	}

	protected void doRebase(PatchSet patchSet) {
		RebaseDialog dialog = new RebaseDialog(getShell(), getTask(), patchSet);
		openOperationDialog(dialog);
	}

	protected void doRestore(PatchSet patchSet) {
		RestoreDialog dialog = new RestoreDialog(getShell(), getTask(), patchSet);
		openOperationDialog(dialog);
	}

	protected void doSubmit(PatchSet patchSet) {
		SubmitDialog dialog = new SubmitDialog(getShell(), getTask(), patchSet);
		openOperationDialog(dialog);
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

	void createSubSectionContents(final ChangeDetail changeDetail, final PatchSetDetail patchSetDetail,
			PatchSetPublishDetail publishDetail, Section subSection) {
		Composite composite = toolkit.createComposite(subSection);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(composite);
		subSection.setClient(composite);

		Label authorLabel = new Label(composite, SWT.NONE);
		authorLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		authorLabel.setText("Author");

		Text authorText = new Text(composite, SWT.READ_ONLY);
		authorText.setText(GerritUtil.getUserLabel(patchSetDetail.getInfo().getAuthor()));

		Label committerLabel = new Label(composite, SWT.NONE);
		committerLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		committerLabel.setText("Committer");

		Text committerText = new Text(composite, SWT.READ_ONLY);
		committerText.setText(GerritUtil.getUserLabel(patchSetDetail.getInfo().getCommitter()));

		Label commitLabel = new Label(composite, SWT.NONE);
		commitLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		commitLabel.setText("Commit");

		Hyperlink commitLink = new Hyperlink(composite, SWT.READ_ONLY);
		commitLink.setText(patchSetDetail.getPatchSet().getRevision().get());
		commitLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				GerritToGitMapping mapping = getRepository(changeDetail);
				if (mapping != null) {
					final FetchPatchSetJob job = new FetchPatchSetJob("Opening Commit Viewer", mapping.getRepository(),
							mapping.getRemote(), patchSetDetail.getPatchSet());
					job.schedule();
					job.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(IJobChangeEvent event) {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									CommitEditor.openQuiet(job.getCommit());
								}
							});
						}
					});
				}
			}
		});

		Label refLabel = new Label(composite, SWT.NONE);
		refLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		refLabel.setText("Ref");

		Text refText = new Text(composite, SWT.READ_ONLY);
		refText.setText(patchSetDetail.getPatchSet().getRefName());

		final TableViewer viewer = new TableViewer(composite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).hint(500, SWT.DEFAULT).applyTo(viewer.getControl());
		viewer.setContentProvider(new IStructuredContentProvider() {
			private EContentAdapter modelAdapter;

			public void dispose() {
				// ignore
			}

			public Object[] getElements(Object inputElement) {
				return getReviewItems(inputElement).toArray();
			}

			private List<IReviewItem> getReviewItems(Object inputElement) {
				if (inputElement instanceof IReviewItemSet) {
					return ((IReviewItemSet) inputElement).getItems();
				}
				return Collections.emptyList();
			}

			public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
				if (modelAdapter != null) {
					for (IReviewItem item : getReviewItems(oldInput)) {
						((EObject) item).eAdapters().remove(modelAdapter);
					}
					addedDrafts = 0;
				}

				if (newInput instanceof IReviewItemSet) {
					// monitors any new topics that are added
					modelAdapter = new EContentAdapter() {
						@Override
						public void notifyChanged(Notification notification) {
							super.notifyChanged(notification);
							if (notification.getFeatureID(IReviewItem.class) == ReviewsPackage.REVIEW_ITEM__TOPICS
									&& notification.getEventType() == Notification.ADD) {
								viewer.refresh();
								addedDrafts++;
							}
						}
					};
					for (Object item : getReviewItems(newInput)) {
						((EObject) item).eAdapters().add(modelAdapter);
					}
				}
			}
		});
		viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new ReviewItemLabelProvider()));
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				IFileItem item = (IFileItem) selection.getFirstElement();
				if (item != null) {
					doOpen((IReviewItemSet) viewer.getInput(), item);
				}
			}
		});

		IReviewItemSet itemSet = GerritUtil.createInput(changeDetail, new GerritPatchSetContent(patchSetDetail), cache);
		viewer.setInput(itemSet);

		Composite actionComposite = createActions(changeDetail, patchSetDetail, publishDetail, composite);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(actionComposite);

		subSectionExpanded(changeDetail, patchSetDetail, subSection, viewer);
		EditorUtil.addScrollListener(viewer.getTable());

		getTaskEditorPage().reflow();
	}

	private void doOpen(IReviewItemSet items, IFileItem item) {
		if (item.getBase() == null || item.getTarget() == null) {
			getTaskEditorPage().getEditor().setMessage("The selected file is not available, yet",
					IMessageProvider.WARNING);
			return;
		}

		GerritReviewBehavior behavior = new GerritReviewBehavior(getTask());
		CompareConfiguration configuration = new CompareConfiguration();
		CompareUI.openCompareEditor(new FileItemCompareEditorInput(configuration, item, behavior));
	}

}
