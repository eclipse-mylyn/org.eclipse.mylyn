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
import org.eclipse.mylyn.internal.gerrit.core.remote.PatchSetContentCompareRemoteFactory;
import org.eclipse.mylyn.internal.gerrit.ui.GerritReviewBehavior;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewItemSetContentRemoteFactory;
import org.eclipse.mylyn.reviews.ui.spi.factories.IUiContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;

import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 * @author Sebastien Dubois
 */
public class CompareWithUiFactory extends AbstractPatchSetUiFactory {

	private final class CompareClient extends ReviewItemSetContentRemoteFactory.Client {
		private final IReviewItemSet compareSet;

		private CompareClient(IReviewItemSet compareSet) {
			this.compareSet = compareSet;
		}

		@Override
		protected void update() {
			CompareConfiguration configuration = new CompareConfiguration();
			CompareUI.openCompareEditor(new ReviewItemSetCompareEditorInput(configuration, compareSet, null,
					new GerritReviewBehavior(getTask(), resolveGitRepository())));
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					dispose();
				}
			});
		}

		@Override
		public void failed(IReviewItemSet parentObject, List<IFileItem> modelObject, IStatus status) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
					"Couldn't load task content for review", status.getException())); //$NON-NLS-1$
		}

		@Override
		protected boolean isClientReady() {
			// ignore
			return true;
		}
	}

	private IReviewItemSet baseSet;

	private IReviewItemSet targetSet;

	public CompareWithUiFactory(IUiContext context, IReviewItemSet set) {
		super("Compare With...", context, set);
	}

	@Override
	public Control createControl(IUiContext context, Composite parent, FormToolkit toolkit) {
		if (isExecutable()) {

			final Composite compareComposite = toolkit.createComposite(parent);
			GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).applyTo(compareComposite);

			Button compareButton = toolkit.createButton(compareComposite, "Compare With Base", SWT.PUSH);
			compareButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					baseSet = null;
					targetSet = getModelObject();
					execute();
				}
			});

			if (getModelObject().getReview().getSets().size() > 1) {
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
						for (final IReviewItemSet otherSet : getModelObject().getReview().getSets()) {
							if (otherSet != getModelObject()) {
								MenuItem item = new MenuItem(menu, SWT.NONE);
								item.setText(NLS.bind("Compare with {0}", otherSet.getName()));
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
			basePatch = getPatchSetDetail(baseSet).getPatchSet();
		}
		final PatchSetContent content = new PatchSetContent(basePatch, getPatchSetDetail(targetSet).getPatchSet());
		final IReviewItemSet compareSet = IReviewsFactory.INSTANCE.createReviewItemSet();
		String basePatchSetLabel = content.getBase() != null ? content.getBase().getPatchSetId() + "" : "Base";
		compareSet.setName(NLS.bind("Compare Patch Set {0} with {1}", content.getTarget().getPatchSetId(),
				basePatchSetLabel));
		PatchSetContentCompareRemoteFactory remoteFactory = new PatchSetContentCompareRemoteFactory(
				getGerritFactoryProvider());
		final RemoteEmfConsumer<IReviewItemSet, List<IFileItem>, String, PatchSetContent, PatchSetContent, Long> consumer = remoteFactory.getConsumerForRemoteObject(
				compareSet, content);
		CompareClient client = new CompareClient(compareSet);
		consumer.addObserver(client);
		client.setConsumer(consumer);
		client.populate();
	}

	@Override
	public boolean isExecutable() {
		return true;
	}
}
