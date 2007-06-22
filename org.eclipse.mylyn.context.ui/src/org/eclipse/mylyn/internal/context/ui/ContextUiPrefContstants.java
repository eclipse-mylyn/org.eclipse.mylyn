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

package org.eclipse.mylyn.internal.context.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;

/**
 * @author Mik Kersten
 */
public class ContextUiPrefContstants {

	public static final String NAVIGATORS_AUTO_FILTER_ENABLE = "org.eclipse.mylyn.context.ui.navigators.filter.auto.enable";
	
	public static final String ACTIVE_FOLDING_ENABLED = "org.eclipse.mylyn.context.ui.editor.folding.enabled";
	
	public static final String PREFIX_TASK_TO_PERSPECTIVE = "org.eclipse.mylyn.ui.perspectives.task.";
	
	public static final String PERSPECTIVE_NO_ACTIVE_TASK = "org.eclipse.mylyn.ui.perspectives.task.none";
	
	public static final String MARKER_LANDMARK = "org.eclipse.mylyn.ui.interest.landmark";

	public static final String AUTO_MANAGE_EDITORS_OPEN_NUM = "org.eclipse.mylyn.ide.ui.editors.auto.open.num";

	public static final String AUTO_MANAGE_EDITORS = "org.eclipse.mylyn.ide.ui.editors.auto.manage";
	
	public static final String AUTO_MANAGE_PERSPECTIVES = "org.eclipse.mylyn.ide.ui.perspectives.auto.manage";
	
	public static final String AUTO_MANAGE_EXPANSION = "org.eclipse.mylyn.ide.ui.expansion.auto.manage";
		
	public static final String HIGHLIGHTER_PREFIX = "org.eclipse.mylyn.ui.interest.highlighters";

	public static final String GAMMA_SETTING_DARKENED = "org.eclipse.mylyn.ui.gamma.darkened";

	public static final String GAMMA_SETTING_STANDARD = "org.eclipse.mylyn.ui.gamma.standard";

	public static final String GAMMA_SETTING_LIGHTENED = "org.eclipse.mylyn.ui.gamma.lightened";

	public static final String INTERSECTION_MODE = "org.eclipse.mylyn.ui.interest.intersection";

	public static final String DECORATE_INTEREST_LEVEL = "org.eclipse.mylyn.context.ui.interest.decoration";
	
	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	public static final String TASK_HIGHLIGHTER_PREFIX = "org.eclipse.mylyn.ui.interest.highlighters.task.";

	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

}
