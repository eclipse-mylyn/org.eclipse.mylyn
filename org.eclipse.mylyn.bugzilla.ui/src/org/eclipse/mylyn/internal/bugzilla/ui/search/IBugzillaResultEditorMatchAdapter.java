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

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import org.eclipse.mylyn.tasks.ui.search.RepositorySearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten (clean-up)
 */
public interface IBugzillaResultEditorMatchAdapter extends IEditorMatchAdapter {

	public boolean isShownInEditor(Match match, IEditorPart editor);

	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor);

	public void setResult(RepositorySearchResult result);
}
