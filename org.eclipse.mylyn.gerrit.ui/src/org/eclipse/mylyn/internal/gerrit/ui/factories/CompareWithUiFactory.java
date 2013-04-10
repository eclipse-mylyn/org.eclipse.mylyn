/*******************************************************************************
 * Copyright (c) 2013 Ericsson, Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.factories;

import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
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

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 * @author Sebastien Dubois
 */
public class CompareWithUiFactory extends AbstractPatchSetUiFactory {

	private PatchSet baseSet;

	private PatchSet targetSet;

	final PatchSet currentSet;

	public CompareWithUiFactory(IUiContext context, IReviewItemSet set) {
		super("Compare With...", context, set);
		final PatchSetDetail patchSetDetail = getPatchSetDetail();
		currentSet = patchSetDetail.getPatchSet();
	}

	@Override
	public Control createControl(IUiContext context, Composite parent, FormToolkit toolkit) {
		if (isExecutable()) {
			final ChangeDetailX changeDetail = getChange().getChangeDetail();

			final Composite compareComposite = toolkit.createComposite(parent);
			GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).applyTo(compareComposite);

			Button compareButton = toolkit.createButton(compareComposite, "Compare With Base", SWT.PUSH);
			compareButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					baseSet = null;
					targetSet = currentSet;
					execute();
				}
			});

			if (changeDetail.getPatchSets().size() > 1) {
				Button compareWithButton = toolkit.createButton(compareComposite, "", SWT.PUSH);
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
						for (final PatchSet itemSet : changeDetail.getPatchSets()) {
							if (itemSet.getPatchSetId() != currentSet.getPatchSetId()) {
								MenuItem item = new MenuItem(menu, SWT.NONE);
								item.setText(NLS.bind("Compare with Patch Set {0}", itemSet.getPatchSetId()));
								item.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent e) {
										baseSet = itemSet;
										targetSet = currentSet;
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
		final PatchSetContent content = new PatchSetContent(baseSet, targetSet);
		final IReviewItemSet compareSet = IReviewsFactory.INSTANCE.createReviewItemSet();
		String basePatchSetLabel = content.getBase() != null ? content.getBase().getPatchSetId() + "" : "Base";
		compareSet.setName(NLS.bind("Compare Patch Set {0} and {1}", content.getTarget().getPatchSetId(),
				basePatchSetLabel));
		RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, PatchSetContent, PatchSetContent, String> consumer = getGerritFactoryProvider().getReviewItemSetContentFactory()
				.consume("Compare Items", compareSet, content, "", new RemoteEmfConsumer.IObserver<List<IFileItem>>() {
					public void responded(boolean modified) {
						CompareConfiguration configuration = new CompareConfiguration();
						CompareUI.openCompareEditor(new ReviewItemSetCompareEditorInput(configuration, compareSet,
								null, new GerritReviewBehavior(getTask(), resolveGitRepository())));
					}

					public void failed(IStatus status) {
						StatusHandler.log(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
								"Couldn't load task content for review", status.getException())); //$NON-NLS-1$
					}

					public void created(List<IFileItem> object) {
					}
				});
		consumer.request();
	}

	@Override
	public boolean isExecutable() {
		ChangeDetailX changeDetail = getChange().getChangeDetail();
		return changeDetail != null && changeDetail.getPatchSets().size() > 1;
	}
}
