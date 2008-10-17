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

	public class CommentGroup {

		private final List<ITaskComment> comments;

		private final String groupName;

		CommentGroup(String groupName, List<ITaskComment> comments) {
			this.groupName = groupName;
			this.comments = comments;
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
		List<CommentGroup> commentGroups = new ArrayList<CommentGroup>();

		// current
		int currentFromIndex = -1;
		List<ITaskComment> current = new ArrayList<ITaskComment>();

		// update task comment and get current group index
		ITaskComment latestComment = null;
		for (int i = 0; i < comments.size(); i++) {
			ITaskComment taskComment = comments.get(i);

			// add all incoming changes
			if (hasIncomingChanges(taskComment)) {
				current.add(taskComment);
			}

			IRepositoryPerson person = taskComment.getAuthor();
			if (person != null && person.getPersonId().equals(currentPersonId)) {
				currentFromIndex = i;
				latestComment = taskComment;
			}
		}

		if (current.size() > 0) {
			comments.removeAll(current);
		}

		// group by last author
		if (currentFromIndex != -1 && currentFromIndex < comments.size()) {
			// bug 238038 comment #58, if the latest comment is generated automatically, lookback one comment
			if (latestComment != null && latestComment.getText().contains(AttachmentUtil.CONTEXT_DESCRIPTION)
					&& currentFromIndex > 0) {
				ITaskComment secondLatestComment = comments.get(currentFromIndex - 1);
				IRepositoryPerson person = secondLatestComment.getAuthor();
				if (person != null && person.getPersonId().equals(currentPersonId)) {
					currentFromIndex--;
				}
			}

			current.addAll(0, new ArrayList<ITaskComment>(comments.subList(currentFromIndex, comments.size())));
			if (current.size() > 0) {
				comments.removeAll(current);
			}
		}

		// recent
		int recentFromIndex = comments.size() - 20 < 0 ? 0 : comments.size() - 20;
		List<ITaskComment> recent = new ArrayList<ITaskComment>(comments.subList(recentFromIndex, comments.size()));
		if (recent.size() > 0) {
			comments.removeAll(recent);
		}

		// ignore groups that have no comment

		// the rest goes to Older
		if (comments.size() > 0) {
			commentGroups.add(new CommentGroup("Older", comments));
		}

		if (recent.size() > 0) {
			commentGroups.add(new CommentGroup("Recent", recent));
		}

		if (current.size() > 0) {
			commentGroups.add(new CommentGroup("Current", current));
		}

		return commentGroups;
	}

	protected boolean hasIncomingChanges(ITaskComment taskComment) {
		return false;
	}

}
