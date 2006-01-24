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

package org.eclipse.mylar.internal.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;

/**
 * @author Mik Kersten
 */
public class MylarUiPrefContstants {

	public static final String MARKER_LANDMARK = "org.eclipse.mylar.ui.interest.landmark";

	public static final String INTEREST_FILTER_EXCLUSION = "org.eclipse.mylar.ui.interest.filter.exclusion";

	public static final String MANAGE_EDITORS_AUTO_OPEN_NUM = "org.eclipse.mylar.ide.ui.editors.auto.open.num";

	public static final String HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters";

	public static final String GAMMA_SETTING_DARKENED = "org.eclipse.mylar.ui.gamma.darkened";

	public static final String GAMMA_SETTING_STANDARD = "org.eclipse.mylar.ui.gamma.standard";

	public static final String GAMMA_SETTING_LIGHTENED = "org.eclipse.mylar.ui.gamma.lightened";

	public static final String GLOBAL_FILTERING = "org.eclipse.mylar.ui.interest.filter.global";

	public static final String INTERSECTION_MODE = "org.eclipse.mylar.ui.interest.intersection";

	public static final Font ITALIC = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);

	public static final String TASK_HIGHLIGHTER_PREFIX = "org.eclipse.mylar.ui.interest.highlighters.task.";

	public static final Font BOLD = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);

}
