/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Description:
 * 	This class implements the Dynamic menu selection to pre-filled the list of gerrit
 * 	project locations.
 * 
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the dynamic menu selection
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.menus;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.gerrit.dashboard.GerritPlugin;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.GerritServerUtility;
import org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils.UIConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class DynamicMenuAddition extends CompoundContributionItem implements IWorkbenchContribution {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	/**
	 * Field SELECT_PICTURE_FILE. (value is ""icons/select.gif"")
	 */
	private static String SELECT_PICTURE_FILE = "icons/select.gif"; //$NON-NLS-1$

//    private static String IMAGE_ID = "imageId";

	/**
	 * Note: An image registry owns all of the image objects registered with it, and automatically disposes of them the
	 * SWT Display is disposed.
	 */
	// For the images
	private static ImageRegistry fImageRegistry = new ImageRegistry();

	static {
		fImageRegistry.put(SELECT_PICTURE_FILE, GerritUi.getImageDescriptor(SELECT_PICTURE_FILE));

		/////////////////////
//        Bundle bundle = Platform.getBundle(GerritUi.PLUGIN_ID);
//        IPath path = new Path("icons/favicon.ico");
//        URL url = FileLocator.find(bundle, path, null);
//        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
//        desc.createImage();
//        fImageRegistry.put(IMAGE_ID, desc);

	}

	// ------------------------------------------------------------------------
	// Variables
	// ------------------------------------------------------------------------

	private IServiceLocator fServiceLocator;

	private GerritServerUtility fServer = null;

	private Map<TaskRepository, String> fMapServer = null;

	private ImageDescriptor fSelectPicture = null;

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	@Override
	protected IContributionItem[] getContributionItems() {

		GerritPlugin.Ftracer.traceInfo("\t\t DynamicMenuAddition .getContributionItems()"); //$NON-NLS-1$
		CommandContributionItem[] contributionItems = new CommandContributionItem[0];
		if (fServer != null) {
			fMapServer = fServer.getGerritMapping();
		}

		if (fMapServer != null && !fMapServer.isEmpty()) {
			Set<TaskRepository> mapSet = fMapServer.keySet();
			String lastSelected = fServer.getLastSavedGerritServer();
			GerritPlugin.Ftracer.traceInfo("-------------------"); //$NON-NLS-1$
			int size = mapSet.size();
			contributionItems = new CommandContributionItem[size];

			int count = 0;
			for (TaskRepository key : mapSet) {
				GerritPlugin.Ftracer.traceInfo("Map Key: " + key.getRepositoryLabel() + "\t URL: " //$NON-NLS-1$//$NON-NLS-2$
						+ fMapServer.get(key));
				CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter(
						fServiceLocator, fMapServer.get(key), UIConstants.ADD_GERRIT_SITE_COMMAND_ID,
						CommandContributionItem.STYLE_PUSH);
				contributionParameter.label = key.getRepositoryLabel();
				contributionParameter.visibleEnabled = true;
				if (lastSelected != null && lastSelected.equals(fMapServer.get(key))) {
					fSelectPicture = fImageRegistry.getDescriptor(SELECT_PICTURE_FILE);
//                    fSelectPicture = fImageRegistry.getDescriptor(IMAGE_ID);

					contributionParameter.icon = fSelectPicture;

				}
				contributionItems[count++] = new CommandContributionItem(contributionParameter);
			}
		}

		return contributionItems;
	}

	@Override
	public void initialize(IServiceLocator aServiceLocator) {
		fServiceLocator = aServiceLocator;

		//Read the Gerrit potential servers
		fServer = GerritServerUtility.getInstance();
	}

}
