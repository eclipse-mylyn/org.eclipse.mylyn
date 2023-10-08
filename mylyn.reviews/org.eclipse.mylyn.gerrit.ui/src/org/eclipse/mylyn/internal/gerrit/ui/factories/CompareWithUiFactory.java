/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson, Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetContentCompareRemoteFactory;
import org.eclipse.mylyn.internal.gerrit.ui.GerritCompareUi;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfObserver;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 * @author Sebastien Dubois
 */
public class CompareWithUiFactory extends AbstractPatchSetUiFactory {

	private final class ItemListClient extends RemoteEmfObserver<IReviewItemSet, List<IFileItem>, String, Long> {
		private final IReviewItemSet baseSet;

		private final IReviewItemSet targetSet;

		private final IReviewItemSet compareSet;

		public ItemListClient(IReviewItemSet baseSet, IReviewItemSet targetSet, IReviewItemSet compareSet) {
			this.baseSet = baseSet;
			this.targetSet = targetSet;
			this.compareSet = compareSet;
		}

		@Override
		public void updated(boolean modified) {
			IStatus status = getConsumer().getStatus();
			if (status.isOK()) {
				Set<String> fileNames = new HashSet<>();
				addItems(baseSet, fileNames);
				addItems(targetSet, fileNames);
				if (!fileNames.isEmpty()) {
					for (Iterator<IFileItem> iter = compareSet.getItems().iterator(); iter.hasNext();) {
						IFileItem file = iter.next();
						if (!fileNames.contains(file.getName())) {
							iter.remove();
						}
					}
				}
				GerritCompareUi.openCompareEditor(createCompareEditorInput(compareSet));
				dispose();
			} else {
				StatusManager.getManager()
						.handle(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
								"Couldn't load content for compare editor", //$NON-NLS-1$
								status.getException()), StatusManager.SHOW | StatusManager.LOG);
			}
		}

		private void addItems(IReviewItemSet itemSet, Set<String> fileNames) {
			if (itemSet != null) {
//				fileNames.addAll(Collections2.transform(itemSet.getItems(), new Function<IFileItem, String>() {
//					public String apply(IFileItem f) {
//						return f.getName();
//					}
//				}));

				itemSet.getItems().stream().map(f -> f.getName()).forEachOrdered(fileNames::add);
			}
		}
	}

	private IReviewItemSet baseSet;

	private IReviewItemSet targetSet;

	private IReviewItemSet compareSet;

	public CompareWithUiFactory(IUiContext context, IReviewItemSet set) {
		super(Messages.CompareWithUiFactory_Compare_With, context, set);
	}

	@Override
	public Control createControl(IUiContext context, Composite parent, FormToolkit toolkit) {
		if (isExecutable()) {

			final Composite compareComposite = toolkit.createComposite(parent);
			GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).applyTo(compareComposite);

			Button compareButton = toolkit.createButton(compareComposite,
					Messages.CompareWithUiFactory_Compare_With_Base, SWT.PUSH);
			compareButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					baseSet = null;
					targetSet = getModelObject();
					execute();
				}
			});

			if (getModelObject().getReview().getSets().size() > 1) {
				Button compareWithButton = toolkit.createButton(compareComposite, "", SWT.PUSH); //$NON-NLS-1$
				GridDataFactory.fillDefaults().grab(false, true).applyTo(compareWithButton);
				compareWithButton.setImage(WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU));
				compareWithButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						showCompareMenu(compareComposite);
					}

					private void showCompareMenu(Composite compareComposite) {
						Menu menu = new Menu(compareComposite);
						Point p = compareComposite.getLocation();
						p.y = p.y + compareComposite.getSize().y;
						p = compareComposite.getParent().toDisplay(p);
						for (final IReviewItemSet otherSet : getModelObject().getReview().getSets()) {
							if (otherSet != getModelObject()) {
								MenuItem item = new MenuItem(menu, SWT.NONE);
								item.setText(
										NLS.bind(Messages.CompareWithUiFactory_Compare_with_X, otherSet.getName()));
								item.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										baseSet = otherSet;
										targetSet = getModelObject();
										execute();
									}
								});
							}
						}
						menu.setLocation(p);
						menu.setVisible(true);
					}
				});
			}
			return compareComposite;
		}
		return null;
	}

	@Override
	public void execute() {
		PatchSet basePatch = null;
		if (baseSet != null) {
			PatchSetDetail baseSetDetail = getPatchSetDetail(baseSet);
			if (baseSetDetail == null) {
				handleExecutionStateError();
				return;
			}
			basePatch = baseSetDetail.getPatchSet();
		}
		PatchSetDetail targetSetDetail = getPatchSetDetail(targetSet);
		if (targetSetDetail == null) {
			handleExecutionStateError();
			return;
		}
		final PatchSetContent content = new PatchSetContent(basePatch, targetSetDetail.getPatchSet());
		compareSet = IReviewsFactory.INSTANCE.createReviewItemSet();
		String basePatchSetLabel = content.getBase() != null
				? Integer.toString(content.getBase().getPatchSetId())
				: Messages.CompareWithUiFactory_Base;
		compareSet.setName(NLS.bind(Messages.CompareWithUiFactory_Compare_Patch_Set_X_with_Y,
				content.getTarget().getPatchSetId(), basePatchSetLabel));
		PatchSetContentCompareRemoteFactory remoteFactory = new PatchSetContentCompareRemoteFactory(
				getGerritFactoryProvider());
		final RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, PatchSetContent, PatchSetContent, Long> consumer = remoteFactory
				.getConsumerForRemoteObject(compareSet, content);
		consumer.setUiJob(true);
		consumer.addObserver(new ItemListClient(baseSet, targetSet, compareSet));
		int delimiterIndex = consumer.getLocalKey().indexOf(',');
		String taskId = consumer.getLocalKey().substring(0, delimiterIndex);
		ReviewItemSetCompareEditorInput input = createCompareEditorInput(compareSet);
		CompareEditorInput newInput = GerritCompareUi.getReviewItemSetComparisonEditor(input, compareSet, taskId);
		if (newInput == input) {// no existing compare editor was found
			consumer.retrieve(true);
		} else {
			GerritCompareUi.openCompareEditor(newInput);
		}
	}

	@Override
	protected boolean isExecutableStateKnown() {
		return true;
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

	private ReviewItemSetCompareEditorInput createCompareEditorInput(IReviewItemSet compareSet) {
		CompareConfiguration configuration = new CompareConfiguration();
		GerritReviewBehavior behavior = new GerritReviewBehavior(getTask(), resolveGitRepository());
		return new ReviewItemSetCompareEditorInput(configuration, compareSet, null, behavior);
	}
}
