/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * A hyperlink that opens using the Eclipse platform preferences.
 * 
 * @see TasksUiUtil#openUrl(String)
 * 
 * @author David Green
 */
public class PlatformUrlHyperlink extends URLHyperlink {

	public PlatformUrlHyperlink(IRegion region, String urlString) {
		super(region, urlString);
	}

	@Override
	public void open() {
		TasksUiUtil.openUrl(getURLString());
	}
}
