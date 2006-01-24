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

package org.eclipse.mylar.internal.monitor.monitors;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.monitor.MylarMonitorPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Leah Findlater and Mik Kersten
 */
public class MenuCommandMonitor implements Listener {
	public static final String MENU_ITEM_ID = "item.label.";

	public static final String MENU_ITEM_SELECTED = "menu";

	public static final String TOOLBAR_ITEM_SELECTED = "toolbar";

	public static final String MENU_PATH_DELIM = "/";

	public void handleEvent(Event event) {
		try {
			if (!(event.widget instanceof Item))
				return;
			Item item = (Item) event.widget;
			if (item.getData() == null)
				return;
			Object target = event.widget.getData();
			String id = null;
			String delta = null;
			if (target instanceof IContributionItem)
				id = ((IContributionItem) target).getId();

			if (item instanceof MenuItem) {
				MenuItem menu = (MenuItem) item;
				Menu parentMenu = menu.getParent();
				String location = "";
				if (parentMenu != null) {
					while (parentMenu.getParentItem() != null) {
						location = parentMenu.getParentItem().getText() + MENU_PATH_DELIM + location;
						parentMenu = parentMenu.getParentMenu();
					}
				}
				String simpleId = "";
				if (id == null)
					id = "null";
				String itemText = obfuscateValueIfContainsPath(item.getText());
				id = id + "$" + MENU_ITEM_ID + simpleId + location + itemText;

				delta = MENU_ITEM_SELECTED;
			} else if (item instanceof ToolItem) {
				ToolItem tool = (ToolItem) item;
				String simpleId = "";
				if (id == null)
					id = "null";
				id = id + "$" + MENU_ITEM_ID + simpleId + '.' + tool.getToolTipText();

				delta = TOOLBAR_ITEM_SELECTED;
			}
			InteractionEvent interactionEvent = InteractionEvent.makeCommand(id, delta);
			MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);

		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not log selection", false);
		}
	}

	/**
	 * TODO: generalize this to other resources whose names are private
	 */
	private String obfuscateValueIfContainsPath(String text) {
		if (text.indexOf(".java") != -1 || text.indexOf(".xml") != -1) {
			return MylarMonitorPlugin.OBFUSCATED_LABEL;
		} else {
			return text;
		}
	}
}
