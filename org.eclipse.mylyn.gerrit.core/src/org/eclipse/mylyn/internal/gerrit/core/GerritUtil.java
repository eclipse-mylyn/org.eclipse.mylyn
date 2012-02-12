/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Sascha Scholz (SAP) - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritPatchSetContent;
import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.CommentDetail;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.Account.Id;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.UserIdentity;

/**
 * @author Steffen Pingel
 * @author Sascha Scholz
 */
public class GerritUtil {

	private static final ReviewsFactory FACTORY = ReviewsFactory.eINSTANCE;

	public static GerritChange getChange(TaskData taskData) {
		JSonSupport json = new JSonSupport();
		TaskAttribute attribute = taskData.getRoot().getAttribute(GerritTaskSchema.getDefault().OBJ_REVIEW.getKey());
		if (attribute != null) {
			return json.getGson().fromJson(attribute.getValue(), GerritChange.class);
		}
		return null;
	}

	public static IReview toReview(ChangeDetail detail) throws GerritException {
		IReview review = FACTORY.createReview();
		review.setId(detail.getChange().getId().get() + "");
		List<PatchSet> patchSets = detail.getPatchSets();
		for (PatchSet patchSet : patchSets) {
			IReviewItemSet itemSet = toReviewItemSet(detail, null, patchSet);
			itemSet.setReview(review);
			review.getItems().add(itemSet);
		}
		return review;
	}

	public static IReviewItemSet toReviewItemSet(ChangeDetail detail, PatchSet base, PatchSet patchSet) {
		IReviewItemSet itemSet = FACTORY.createReviewItemSet();
		if (base == null) {
			itemSet.setName(NLS.bind("Patch Set {0}", patchSet.getPatchSetId()));
		} else {
			itemSet.setName(NLS.bind("Compare Patch Set {0} and {1}", patchSet.getPatchSetId(), base.getPatchSetId()));
		}
		itemSet.setId(patchSet.getPatchSetId() + "");
		itemSet.setAddedBy(createUser(patchSet.getUploader(), detail.getAccounts()));
		itemSet.setRevision(patchSet.getRevision().get());
		return itemSet;
	}

	public static List<IReviewItem> toReviewItems(GerritPatchSetContent content, ReviewItemCache cache) {
		List<IReviewItem> items = new ArrayList<IReviewItem>();
		for (Patch patch : content.getTarget().getPatches()) {
			String targetId = patch.getKey().toString();
			String sourceFileName = (patch.getSourceFileName() != null)
					? patch.getSourceFileName()
					: patch.getFileName();
			String baseId = (content.getBase() != null)
					? new Patch.Key(content.getBase().getId(), sourceFileName).toString()
					: "base-" + targetId;
			String id = baseId + ":" + targetId; //$NON-NLS-1$
			IFileItem item = (IFileItem) cache.getItem(id);
			if (item == null) {
				item = FACTORY.createFileItem();
				item.setId(id);
				item.setName(patch.getFileName());
				cache.put(item);
			}
			items.add(item);

			if (content.getPatchScriptByPatchKey() != null) {
				PatchScript patchScript = content.getPatchScriptByPatchKey().get(patch.getKey());
				if (patchScript != null) {
					CommentDetail commentDetail = patchScript.getCommentDetail();

					IFileRevision revisionA = (IFileRevision) cache.getItem(baseId);
					if (revisionA == null) {
						revisionA = FACTORY.createFileRevision();
						revisionA.setId(baseId);
						revisionA.setContent(patchScript.getA().asString());
						revisionA.setPath(patchScript.getA().getPath());
						revisionA.setRevision((content.getBase() != null) ? NLS.bind("Patch Set {0}", content.getBase()
								.getPatchSetId()) : "Base");
						addComments(revisionA, commentDetail.getCommentsA(), commentDetail.getAccounts());
						cache.put(revisionA);
					}
					item.setBase(revisionA);

					IFileRevision revisionB = (IFileRevision) cache.getItem(targetId);
					if (revisionB == null) {
						revisionB = FACTORY.createFileRevision();
						revisionB.setId(targetId);
						SparseFileContent target = patchScript.getB().apply(patchScript.getA(), patchScript.getEdits());
						revisionB.setContent(target.asString());
						revisionB.setPath(patchScript.getB().getPath());
						revisionB.setRevision(NLS.bind("Patch Set {0}", content.getTarget()
								.getPatchSet()
								.getPatchSetId()));
						addComments(revisionB, commentDetail.getCommentsB(), commentDetail.getAccounts());
						cache.put(revisionB);
					}
					item.setTarget(revisionB);
				}
			}
		}
		return items;
	}

	private static void addComments(IFileRevision revision, List<PatchLineComment> comments,
			AccountInfoCache accountInfoCache) {
		if (comments == null || comments.isEmpty()) {
			return;
		}
		for (PatchLineComment comment : comments) {
			ILineRange line = FACTORY.createLineRange();
			line.setStart(comment.getLine());
			line.setEnd(comment.getLine());
			ILineLocation location = FACTORY.createLineLocation();
			location.getRanges().add(line);

			IUser author = GerritUtil.createUser(comment.getAuthor(), accountInfoCache);

			IComment topicComment = FACTORY.createComment();
			topicComment.setAuthor(author);
			topicComment.setCreationDate(comment.getWrittenOn());
			topicComment.setDescription(comment.getMessage());

			ITopic topic = FACTORY.createTopic();
			topic.setId(comment.getKey().get());
			topic.setAuthor(author);
			topic.setCreationDate(comment.getWrittenOn());
			topic.setLocation(location);
			topic.setItem(revision);
			topic.setDraft(PatchLineComment.Status.DRAFT == comment.getStatus());
			topic.setDescription(comment.getMessage());
			topic.setTitle(shortenText(comment.getMessage(), 10, 20));
			topic.getComments().add(topicComment);

			revision.getTopics().add(topic);
		}
	}

	public static IUser createUser(Id id, AccountInfoCache accountInfoCache) {
		AccountInfo info = accountInfoCache.get(id);
		IUser user = FACTORY.createUser();
		user.setDisplayName(getUserLabel(info));
		user.setId(Integer.toString(id.get()));
		return user;
	}

	public static String getUserLabel(AccountInfo user) {
		if (user == null) {
			return "Anonymous";
		}
		if (user.getFullName() != null) {
			return user.getFullName();
		}
		if (user.getPreferredEmail() != null) {
			String email = user.getPreferredEmail();
			int i = email.indexOf("@");
			return (i > 0) ? email.substring(0, i) : email;
		}
		return "<Unknown>";
	}

	public static String getUserLabel(UserIdentity user) {
		StringBuilder sb = new StringBuilder();
		sb.append(user.getName());
		if (user.getEmail() != null) {
			sb.append(" <");
			sb.append(user.getEmail());
			sb.append(">");
		}
		return sb.toString();
	}

	public static boolean isPermissionOnlyProject(ProjectDetailX projectDetail, GerritConfig config) {
		if (projectDetail.isPermissionOnly) {
			return true;
		} else if (projectDetail.project.getName().equals(config.getWildProject().get())) {
			return true;
		} else {
			return false;
		}
	}

	public static String shortenText(String t, int minChars, int maxChars) {
		Assert.isTrue(minChars >= 0);
		Assert.isTrue(maxChars >= 0);
		Assert.isTrue(minChars <= maxChars);
		if (t.length() < maxChars) {
			return t;
		}
		for (int i = maxChars - 1; i >= minChars; i--) {
			if (Character.isWhitespace(t.charAt(i))) {
				return NLS.bind("{0}...", t.substring(0, i));
			}
		}
		return NLS.bind("{0}...", t.substring(0, minChars));
	}

	public static IReviewItemSet createInput(ChangeDetail changeDetail, GerritPatchSetContent content,
			ReviewItemCache cache) {
		IReviewItemSet itemSet = GerritUtil.toReviewItemSet(changeDetail, content.getBase(), content.getTarget()
				.getPatchSet());
		List<IReviewItem> items = GerritUtil.toReviewItems(content, cache);
		itemSet.getItems().addAll(items);
		return itemSet;
	}

}
