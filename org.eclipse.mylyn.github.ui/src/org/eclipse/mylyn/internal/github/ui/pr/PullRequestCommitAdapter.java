/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.text.DateFormat;
import java.text.MessageFormat;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ui.model.WorkbenchAdapter;

/**
 * Pull request commit adapter
 */
public class PullRequestCommitAdapter extends WorkbenchAdapter {

	private static final DateFormat DATE_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

	private final RepositoryCommit commit;

	/**
	 * @param commit
	 */
	public PullRequestCommitAdapter(RepositoryCommit commit) {
		this.commit = commit;
	}

	/**
	 * Get commit
	 *
	 * @return commit
	 */
	public RepositoryCommit getCommit() {
		return commit;
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return UIIcons.CHANGESET;
	}

	@Override
	public String getLabel(Object object) {
		return commit.getSha().substring(0, 8);
	}

	public StyledString getStyledText(Object object) {
		StyledString styled = new StyledString(getLabel(object));
		String desc = commit.getCommit().getMessage();
		if (desc != null) {
			int delim = desc.indexOf('\n');
			if (delim == -1)
				delim = 80;
			if (delim < desc.length())
				desc = desc.substring(0, delim);
			styled.append(": ").append(desc); //$NON-NLS-1$
		}
		styled.append(' ');
		String name = commit.getCommit().getAuthor().getName();
		String authorWithDate = MessageFormat.format(
				Messages.PullRequestCommitAdapter_AuthorWithDate, name,
				DATE_FORMAT.format(commit.getCommit().getAuthor().getDate()));
		styled.append(authorWithDate, StyledString.QUALIFIER_STYLER);
		return styled;
	}

}
