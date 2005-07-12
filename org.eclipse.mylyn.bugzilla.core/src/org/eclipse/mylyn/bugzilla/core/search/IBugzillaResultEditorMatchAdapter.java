package org.eclipse.mylar.bugzilla.core.search;

import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;

public interface IBugzillaResultEditorMatchAdapter extends IEditorMatchAdapter {

	public boolean isShownInEditor(Match match, IEditorPart editor);

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IEditorPart editor);

	public void setResult(BugzillaSearchResult result);
}
