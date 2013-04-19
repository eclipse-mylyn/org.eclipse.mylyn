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

import java.util.ArrayList;
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
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;

/**
 * Manages retrieval of patch set contents, including file versions and associated comments, from Gerrit API.
 * 
 * @author Miles Parker
 * @author Steffen Pingel
 */
public abstract class PatchSetContentRemoteFactory<RemoteKeyType> extends
		AbstractRemoteEmfFactory<IReviewItemSet, List<IFileItem>, PatchSetContent, RemoteKeyType, String> {

	private final ReviewItemCache cache;

	private final GerritRemoteFactoryProvider gerritFactoryProvider;

	public PatchSetContentRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider, ReviewsPackage.Literals.REVIEW_ITEM_SET__ITEMS,
				ReviewsPackage.Literals.REVIEW_ITEM__ID);
		this.gerritFactoryProvider = gerritRemoteFactoryProvider;
		cache = new ReviewItemCache();
	}

	public PatchSetContent pull(IReviewItemSet parentObject, PatchSetContent content, IProgressMonitor monitor)
			throws CoreException {
		try {
			gerritFactoryProvider.getClient().loadPatchSetContent(content, monitor);

		} catch (GerritException e) {
			throw new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Problem while collecting patch set content", e));
		}
		for (Patch patch : content.getTargetDetail().getPatches()) {
			PatchScript patchScript = content.getPatchScript(patch.getKey());
			CommentDetail commentDetail = patchScript.getCommentDetail();
			List<PatchLineComment> comments = new ArrayList<PatchLineComment>();
			comments.addAll(commentDetail.getCommentsA());
			comments.addAll(commentDetail.getCommentsB());
			for (PatchLineComment comment : comments) {
				gerritFactoryProvider.pullUser(getGerritProvider().getRoot(), patchScript.getCommentDetail()
						.getAccounts(), comment.getAuthor(), monitor);
			}
		}
		return content;
	}

	boolean addComments(IReviewItemSet set, IFileVersion version, List<PatchLineComment> comments,
			AccountInfoCache accountInfoCache) {
		version.getTopics().clear();
		if (version == null || comments == null || comments.isEmpty()) {
			return false;
		}
		boolean changed = comments.size() != version.getTopics().size();
		int oldDraftCount = version.getTopics().size();
		for (ITopic topic : version.getTopics()) {
			if (topic.isDraft()) {
				oldDraftCount++;
			}
		}
		int draftCount = 0;
		for (PatchLineComment comment : comments) {
			ILineRange line = IReviewsFactory.INSTANCE.createLineRange();
			line.setStart(comment.getLine());
			line.setEnd(comment.getLine());
			ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
			location.getRanges().add(line);

			IComment topicComment = IReviewsFactory.INSTANCE.createComment();
			IUser author = getGerritProvider().createUser(getGerritProvider().getRoot(), accountInfoCache,
					comment.getAuthor());

			topicComment.setCreationDate(comment.getWrittenOn());
			topicComment.setDescription(comment.getMessage());
			topicComment.setDraft(PatchLineComment.Status.DRAFT == comment.getStatus());
			if (topicComment.isDraft()) {
				draftCount++;
			}
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
		changed |= draftCount != oldDraftCount;
		return changed;
	}

	@Override
	public boolean isPullNeeded(IReviewItemSet parent, List<IFileItem> items, PatchSetContent remote) {
		return true;
	}

	@Override
	public List<IFileItem> createModel(IReviewItemSet set, PatchSetContent content) {
		List<IFileItem> items = set.getItems();
		for (Patch patch : content.getTargetDetail().getPatches()) {
			String targetId = patch.getKey().toString();
			String sourceFileName = (patch.getSourceFileName() != null)
					? patch.getSourceFileName()
					: patch.getFileName();
			String baseId = (content.getBase() != null)
					? new Patch.Key(content.getBase().getId(), sourceFileName).toString()
					: "base-" + targetId;
			String id = baseId + ":" + targetId; //$NON-NLS-1$
			IFileItem item = (IFileItem) getCache().getItem(id);
			if (item == null) {
				item = IReviewsFactory.INSTANCE.createFileItem();
				item.setId(id);
				item.setName(patch.getFileName());
				item.setAddedBy(set.getAddedBy());
				item.setCommittedBy(set.getCommittedBy());
				item.setReference(patch.getKey().getParentKey() + "," + patch.getFileName());
				getCache().put(item);
			}
			items.add(item);

			PatchScript patchScript = content.getPatchScript(patch.getKey());
			if (patchScript != null) {
				IFileVersion baseVersion = (IFileVersion) getCache().getItem(baseId);
				if (baseVersion == null) {
					baseVersion = IReviewsFactory.INSTANCE.createFileVersion();
					baseVersion.setId(baseId);
					baseVersion.setContent(patchScript.getA().asString());
					baseVersion.setPath(patchScript.getA().getPath());
					baseVersion.setDescription((content.getBase() != null) ? NLS.bind("Patch Set {0}",
							content.getBase().getPatchSetId()) : "Base");
					baseVersion.setFile(item);
					baseVersion.setName(item.getName());
					getCache().put(baseVersion);
				}
				item.setBase(baseVersion);

				IFileVersion targetVersion = (IFileVersion) getCache().getItem(targetId);
				if (targetVersion == null) {
					targetVersion = IReviewsFactory.INSTANCE.createFileVersion();
					targetVersion.setId(targetId);
					SparseFileContent target = patchScript.getB().apply(patchScript.getA(), patchScript.getEdits());
					targetVersion.setContent(target.asString());
					targetVersion.setPath(patchScript.getB().getPath());
					targetVersion.setDescription(NLS.bind("Patch Set {0}", content.getTargetDetail()
							.getPatchSet()
							.getPatchSetId()));
					targetVersion.setFile(item);
					targetVersion.setAddedBy(item.getAddedBy());
					targetVersion.setCommittedBy(item.getCommittedBy());
					targetVersion.setName(item.getName());
					getCache().put(targetVersion);
				}
				item.setTarget(targetVersion);
			}
		}
		return items;
	}

	/**
	 * Patch sets results never change.
	 */
	@Override
	public boolean updateModel(IReviewItemSet set, List<IFileItem> items, PatchSetContent content) {
		boolean changed = false;
		for (IFileItem item : items) {
			IFileItem fileItem = item;
			PatchScript patchScript = content.getPatchScript(Patch.Key.parse(item.getReference()));
			if (patchScript != null) {
				CommentDetail commentDetail = patchScript.getCommentDetail();
				changed |= addComments(set, fileItem.getBase(), commentDetail.getCommentsA(),
						commentDetail.getAccounts());
				changed |= addComments(set, fileItem.getTarget(), commentDetail.getCommentsB(),
						commentDetail.getAccounts());
			}
		}
		return changed;
	}

	public GerritRemoteFactoryProvider getGerritProvider() {
		return gerritFactoryProvider;
	}

	public ReviewItemCache getCache() {
		return cache;
	}
}
