/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 27, 2004
 */
package org.eclipse.mylyn.internal.context.ui.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Mik Kersten
 */
public class ToggleRelationshipProviderAction extends Action implements IMenuCreator {

	private static final String LABEL_DEGREE_OF_SEPARATION = "Degree of Separation:";

	public static final String ID = "org.eclipse.mylyn.ui.actions.active.search.toggle";

//	private AbstractContextStructureBridge structureBridge;

	private Set<AbstractRelationProvider> providers;
	
	private Menu dropDownMenu = null;

	public ToggleRelationshipProviderAction(Set<AbstractRelationProvider> providers, AbstractContextUiBridge uiBridge) {
		super();
		this.providers = providers;
		setImageDescriptor(ContextUiPlugin.getDefault().getActiveSearchIcon(uiBridge));
		setId(ID);
		setText(ContextUiPlugin.getDefault().getActiveSearchLabel(uiBridge));
		setToolTipText(ContextUiPlugin.getDefault().getActiveSearchLabel(uiBridge));
		setMenuCreator(this);

		degreeOfSeparation = getCurrentProvider().getCurrentDegreeOfSeparation();

		if (degreeOfSeparation > 0) {
			run();
		}
	}

	// HACK: this should be specified
	private AbstractRelationProvider getCurrentProvider() {
		return providers.iterator().next();
	}

	@Override
	public void run() {
		ContextUiPlugin.getDefault()
				.updateDegreesOfSeparation(providers, degreeOfSeparation);
	}

	public void dispose() {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	private int degreeOfSeparation = 0;

	public void addActionsToMenu() {
		// HACK: if there are multiple providers, all store the same current DOS
		degreeOfSeparation = getCurrentProvider().getCurrentDegreeOfSeparation();

		MenuItem menuItem = new MenuItem(dropDownMenu, SWT.NONE);
		menuItem.setText(LABEL_DEGREE_OF_SEPARATION);
		// menuItem.setEnabled(false);

		new MenuItem(dropDownMenu, SWT.SEPARATOR);

		for (IDegreeOfSeparation separation : getCurrentProvider().getDegreesOfSeparation()) {

			Action degreeOfSeparationSelectionAction = new Action(
					separation.getDegree() + ": " + separation.getLabel(), AS_CHECK_BOX) {
				@Override
				public void run() {
					try {
						degreeOfSeparation = Integer.parseInt(getId());
						ContextUiPlugin.getDefault().updateDegreesOfSeparation(providers,
								degreeOfSeparation);
					} catch (NumberFormatException e) {
						// ignore this for now
						StatusManager.fail(e, "invalid degree of separation", false);
					}
				}
			};
			degreeOfSeparationSelectionAction.setId("" + separation.getDegree());
			degreeOfSeparationSelectionAction.setEnabled(true);
			degreeOfSeparationSelectionAction.setToolTipText(separation.getLabel());
			ActionContributionItem item = new ActionContributionItem(degreeOfSeparationSelectionAction);
			item.fill(dropDownMenu, -1);

			degreeOfSeparationSelectionAction.setChecked(false);
			if (degreeOfSeparation == 0 && separation.getDegree() == 0) {
				degreeOfSeparationSelectionAction.setChecked(true);
			} else if (degreeOfSeparation != 0 && separation.getDegree() != 0
					&& degreeOfSeparation == separation.getDegree()) {
				degreeOfSeparationSelectionAction.setChecked(true);
			}
		}
	}
}
