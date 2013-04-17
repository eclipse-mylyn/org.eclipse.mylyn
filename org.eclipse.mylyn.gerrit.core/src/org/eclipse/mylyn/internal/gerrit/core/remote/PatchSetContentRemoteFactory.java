/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker, Tasktop Technologies - initial API and implementation
 *     Steffen Pingel, Tasktop Technologies - original GerritUtil implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.ReviewItemCache;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactory;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsPackage;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.CommentDetail;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * Manages retrieval of patch set contents, including file versions and associated comments, from Gerrit API.
 * 
 * @author Miles Parker
 * @author Steffen Pingel
 */
public class PatchSetContentRemoteFactory extends
		AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, PatchSetContent, PatchSetContent, String> {

	private final ReviewItemCache cache;

	private final GerritRemoteFactoryProvider gerritRemoteFactoryProvider;

	public PatchSetContentRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider.getService(), ReviewsPackage.Literals.REVIEW_ITEM_SET__ITEMS,
				ReviewsPackage.Literals.REVIEW_ITEM__ID);
		this.gerritRemoteFactoryProvider = gerritRemoteFactoryProvider;
		cache = new ReviewItemCache();
	}

	@Override
	public PatchSetContent retrieve(PatchSetContent content, IProgressMonitor monitor) throws CoreException {
		try {
			gerritRemoteFactoryProvider.getClient().loadPatchSetContent(content, monitor);
		} catch (GerritException e) {
			throw new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Problem while collecting patch set content", e));
		}
		return content;
	}

	@Override
	public PatchSetContent getRemoteKey(IReviewItemSet parentObject, List<IFileItem> items) {
		PatchSetDetail patchDetail = gerritRemoteFactoryProvider.getReviewItemSetFactory()
				.getRemoteObject(parentObject);
		return new PatchSetContent(null, patchDetail.getPatchSet());
	}

	void addComments(IFileVersion version, List<PatchLineComment> comments, AccountInfoCache accountInfoCache) {
		if (comments == null || comments.isEmpty()) {
			return;
		}
		for (PatchLineComment comment : comments) {
			ILineRange line = IReviewsFactory.INSTANCE.createLineRange();
			line.setStart(comment.getLine());
			line.setEnd(comment.getLine());
			ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
			location.getRanges().add(line);

			IComment topicComment = IReviewsFactory.INSTANCE.createComment();
			IUser author = gerritRemoteFactoryProvider.getUserFactory(accountInfoCache).get(topicComment,
					comment.getAuthor());
			topicComment.setCreationDate(comment.getWrittenOn());
			topicComment.setDescription(comment.getMessage());
			topicComment.setDraft(PatchLineComment.Status.DRAFT == comment.getStatus());
			topicComment.setAuthor(author);

			ITopic topic = IReviewsFactory.INSTANCE.createTopic();
			topic.setId(comment.getKey().get());
			topic.setAuthor(author);
			topic.setCreationDate(comment.getWrittenOn());
			topic.getLocations().add(location);
			topic.setItem(version);
			topic.setDraft(PatchLineComment.Status.DRAFT == comment.getStatus());
			topic.setDescription(comment.getMessage());
			topic.setTitle(GerritUtil.shortenText(comment.getMessage(), 10, 20));
			topic.getComments().add(topicComment);

			version.getTopics().add(topic);
		}
	}

	@Override
	public List<IFileItem> create(IReviewItemSet set, PatchSetContent content) {
		List<IFileItem> items = IReviewsFactory.INSTANCE.createReviewItemSet().getItems();
		for (Patch patch : content.getTargetDetail().getPatches()) {
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
				item = IReviewsFactory.INSTANCE.createFileItem();
				item.setId(id);
				item.setName(patch.getFileName());
				item.setAddedBy(set.getAddedBy());
				item.setCommittedBy(set.getCommittedBy());
				cache.put(item);
			}
			items.add(item);

			PatchScript patchScript = content.getPatchScript(patch.getKey());
			if (patchScript != null) {
				CommentDetail commentDetail = patchScript.getCommentDetail();

				IFileVersion versionA = (IFileVersion) cache.getItem(baseId);
				if (versionA == null) {
					versionA = IReviewsFactory.INSTANCE.createFileVersion();
					versionA.setId(baseId);
					versionA.setContent(patchScript.getA().asString());
					versionA.setPath(patchScript.getA().getPath());
					versionA.setDescription((content.getBase() != null) ? NLS.bind("Patch Set {0}", content.getBase()
							.getPatchSetId()) : "Base");
					versionA.setFile(item);
					versionA.setName(item.getName());
					addComments(versionA, commentDetail.getCommentsA(), commentDetail.getAccounts());
					cache.put(versionA);
				}
				item.setBase(versionA);

				IFileVersion versionB = (IFileVersion) cache.getItem(targetId);
				if (versionB == null) {
					versionB = IReviewsFactory.INSTANCE.createFileVersion();
					versionB.setId(targetId);
					SparseFileContent target = patchScript.getB().apply(patchScript.getA(), patchScript.getEdits());
					versionB.setContent(target.asString());
					versionB.setPath(patchScript.getB().getPath());
					versionB.setDescription(NLS.bind("Patch Set {0}", content.getTargetDetail()
							.getPatchSet()
							.getPatchSetId()));
					versionB.setFile(item);
					versionB.setAddedBy(item.getAddedBy());
					versionB.setCommittedBy(item.getCommittedBy());
					versionB.setName(item.getName());
					addComments(versionB, commentDetail.getCommentsB(), commentDetail.getAccounts());
					cache.put(versionB);
				}
				item.setTarget(versionB);
			}
		}
		return items;
	}

	/**
	 * Patch sets results never change.
	 */
	@Override
	public boolean update(IReviewItemSet set, List<IFileItem> items, PatchSetContent content) {
		return false;
	}
}
