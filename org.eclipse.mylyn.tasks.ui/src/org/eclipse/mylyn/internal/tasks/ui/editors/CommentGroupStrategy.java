/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class CommentGroupStrategy {

	private static final int MAX_CURRENT = 12;

	private static final int MAX_RECENT = 20;

	public static class CommentGroup {

		private final List<ITaskComment> comments;

		private final String groupName;

		private final boolean incoming;

		CommentGroup(String groupName, List<ITaskComment> comments, boolean incoming) {
			this.groupName = groupName;
			this.comments = comments;
			this.incoming = incoming;
		}

		public boolean hasIncoming() {
			return incoming;
		}

		public List<TaskAttribute> getCommentAttributes() {
			List<TaskAttribute> commentAttributes = new ArrayList<TaskAttribute>(comments.size());
			for (ITaskComment comment : comments) {
				commentAttributes.add(comment.getTaskAttribute());
			}
			return Collections.unmodifiableList(commentAttributes);
		}

		public List<ITaskComment> getComments() {
			return Collections.unmodifiableList(comments);
		}

		public String getGroupName() {
			return groupName;
		}

	}

	/**
	 * Groups comment according to "Older", "Recent" and "Current".
	 * 
	 * @param taskDataModel
	 *            extracts groups of comment for the model
	 * @return list of comment groups. Groups will be ignored if there are no comments under them.
	 */
	public List<CommentGroup> groupComments(List<ITaskComment> comments, String currentPersonId) {
		if (comments.size() == 0) {
			return Collections.emptyList();
		}

		List<CommentGroup> commentGroups = new ArrayList<CommentGroup>(3);

		// current
		List<ITaskComment> current = new ArrayList<ITaskComment>(MAX_CURRENT);
		if (comments.size() > MAX_CURRENT) {
			for (int i = comments.size() - 1; i >= 0; i--) {
				ITaskComment comment = comments.get(i);
				if (isCurrent(current, comment, currentPersonId)) {
					current.add(comment);
				}
			}
			Collections.reverse(current);
		} else {
			current = comments;
		}
		commentGroups.add(new CommentGroup("Current", current, hasIncomingChanges(current)));

		// recent
		if (comments.size() > current.size()) {
			int recentToIndex = comments.size() - current.size();
			int recentFromIndex = Math.max(recentToIndex - MAX_RECENT, 0);
			List<ITaskComment> recent = new ArrayList<ITaskComment>(comments.subList(recentFromIndex, recentToIndex));
			if (recent.size() > 0) {
				commentGroups.add(new CommentGroup("Recent", recent, hasIncomingChanges(recent)));

				// the rest goes to older
				if (comments.size() > current.size() + recent.size()) {
					int olderToIndex = comments.size() - current.size() - recent.size();
					List<ITaskComment> older = new ArrayList<ITaskComment>(comments.subList(0, olderToIndex));
					commentGroups.add(new CommentGroup("Older", older, hasIncomingChanges(older)));
				}
			}
		}

		Collections.reverse(commentGroups);
		return commentGroups;
	}

	private boolean hasIncomingChanges(List<ITaskComment> comments) {
		for (ITaskComment comment : comments) {
			if (hasIncomingChanges(comment)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCurrent(List<ITaskComment> current, ITaskComment comment, String currentPersonId) {
		if (current.size() >= MAX_CURRENT) {
			return false;
		}

		// add all incoming changes
		if (hasIncomingChanges(comment)) {
			return true;
		}

		if (!current.isEmpty()) {
			// check if last comment was by current user
			ITaskComment lastComment = current.get(current.size() - 1);
			IRepositoryPerson lastPerson = lastComment.getAuthor();
			if (lastPerson != null && lastPerson.getPersonId().equals(currentPersonId)) {
				// bug 238038 comment #58, if the latest comment is generated automatically, look back one comment
				IRepositoryPerson person = comment.getAuthor();
				if (person != null && person.getPersonId().equals(currentPersonId) && lastComment.getText() != null
						&& lastComment.getText().contains(AttachmentUtil.CONTEXT_DESCRIPTION)) {
					return true;
				}

				return false;
			}
		}

		return true;
	}

	protected boolean hasIncomingChanges(ITaskComment taskComment) {
		return false;
	}

}
