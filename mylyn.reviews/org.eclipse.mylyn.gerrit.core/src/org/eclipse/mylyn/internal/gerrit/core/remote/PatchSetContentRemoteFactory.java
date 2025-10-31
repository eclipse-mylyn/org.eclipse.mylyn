/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewItemSetContentRemoteFactory;
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
public abstract class PatchSetContentRemoteFactory<RemoteKeyType>
		extends ReviewItemSetContentRemoteFactory<PatchSetContent, RemoteKeyType> {

	private final ReviewItemCache cache;

	private final GerritRemoteFactoryProvider gerritFactoryProvider;

	public PatchSetContentRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider);
		gerritFactoryProvider = gerritRemoteFactoryProvider;
		cache = new ReviewItemCache();
	}

	public PatchSetContent pull(IReviewItemSet parentObject, PatchSetContent content, IProgressMonitor monitor)
			throws CoreException {
		try {
			gerritFactoryProvider.getClient().loadPatchSetContent(content, monitor);
		} catch (GerritException e) {
			throw new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Couldn't obtain patch set content for " + content.getId() + ". Check remote connection.", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		for (Patch patch : content.getTargetDetail().getPatches()) {
			PatchScript patchScript = content.getPatchScript(patch.getKey());
			if (patchScript == null) {
				throw new CoreException(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
						"Couldn't obtain patch information for patch set " + patch.getKey() //$NON-NLS-1$
								+ ". Check remote connection.")); //$NON-NLS-1$
			}
			CommentDetail commentDetail = patchScript.getCommentDetail();
			List<PatchLineComment> comments = new ArrayList<>(commentDetail.getCommentsA());
			comments.addAll(commentDetail.getCommentsB());
			for (PatchLineComment comment : comments) {
				gerritFactoryProvider.pullUser(gerritFactoryProvider.getRoot(),
						patchScript.getCommentDetail().getAccounts(), comment.getAuthor(), monitor);
			}
		}
		return content;
	}

	boolean addComments(IReviewItemSet set, IFileVersion version, List<PatchLineComment> comments,
			AccountInfoCache accountInfoCache) {
		if (version == null) {
			return false;
		}
		version.getComments().clear();
		if (comments == null || comments.isEmpty()) {
			return false;
		}
		boolean changed = comments.size() != version.getComments().size();
		int oldDraftCount = version.getDrafts().size();
		int draftCount = 0;
		for (PatchLineComment gerritComment : comments) {
			ILineRange line = IReviewsFactory.INSTANCE.createLineRange();
			line.setStart(gerritComment.getLine());
			line.setEnd(gerritComment.getLine());
			ILineLocation location = IReviewsFactory.INSTANCE.createLineLocation();
			location.getRanges().add(line);

			IComment comment = IReviewsFactory.INSTANCE.createComment();
			IUser author = getGerritProvider().createUser(getGerritProvider().getRoot(), accountInfoCache,
					gerritComment.getAuthor());

			comment.setId(gerritComment.getKey().get());
			comment.setCreationDate(gerritComment.getWrittenOn());
			comment.setTitle(GerritUtil.shortenText(gerritComment.getMessage(), 10, 20));
			comment.setDescription(gerritComment.getMessage());
			comment.setDraft(PatchLineComment.Status.DRAFT == gerritComment.getStatus());
			if (comment.isDraft()) {
				draftCount++;
			}
			comment.setAuthor(author);
			comment.getLocations().add(location);
			version.getComments().add(comment);
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
		if (!items.isEmpty()) {
			return items;
		}
		for (Patch patch : content.getTargetDetail().getPatches()) {
			String targetId = patch.getKey().toString();
			String sourceFileName = patch.getSourceFileName() != null ? patch.getSourceFileName() : patch.getFileName();
			String baseId = content.getBase() != null
					? new Patch.Key(content.getBase().getId(), sourceFileName).toString()
					: "base-" + targetId; //$NON-NLS-1$
			String id = baseId + ":" + targetId; //$NON-NLS-1$
			IFileItem item = (IFileItem) getCache().getItem(id);
			if (item == null) {
				item = IReviewsFactory.INSTANCE.createFileItem();
				item.setId(id);
				item.setName(patch.getFileName());
				item.setAddedBy(set.getAddedBy());
				item.setCommittedBy(set.getCommittedBy());
				item.setReference(patch.getKey().getParentKey() + "," + patch.getFileName()); //$NON-NLS-1$
				getCache().put(item);
			}
			items.add(item);

			PatchScriptX patchScript = content.getPatchScript(patch.getKey());
			if (patchScript != null) {
				IFileVersion baseVersion = (IFileVersion) getCache().getItem(baseId);
				if (baseVersion == null) {
					baseVersion = IReviewsFactory.INSTANCE.createFileVersion();
					baseVersion.setId(baseId);
					if (patchScript.getPatchHeader() != null) {
						if (patchScript.isBinary()) {
							baseVersion.setBinaryContent(patchScript.getBinaryA());
						} else {
							baseVersion.setContent(patchScript.getA().asString());
						}
						baseVersion.setPath(patchScript.getA().getPath());
						baseVersion.setDescription(content.getBase() != null
								? NLS.bind(Messages.PatchSetContentRemoteFactory_Patch_Set,
										content.getBase().getPatchSetId())
								: Messages.PatchSetContentRemoteFactory_Base);
						baseVersion.setFile(item);
						baseVersion.setName(item.getName());
						getCache().put(baseVersion);
					}
				}
				item.setBase(baseVersion);

				IFileVersion targetVersion = (IFileVersion) getCache().getItem(targetId);
				if (targetVersion == null) {
					targetVersion = IReviewsFactory.INSTANCE.createFileVersion();
					targetVersion.setId(targetId);
					SparseFileContent target = patchScript.getB().apply(patchScript.getA(), patchScript.getEdits());
					if (patchScript.getPatchHeader() != null) {
						if (patchScript.isBinary()) {
							targetVersion.setBinaryContent(patchScript.getBinaryB());
						} else {
							targetVersion.setContent(target.asString());
						}
					}
					targetVersion.setPath(patchScript.getB().getPath());
					targetVersion.setDescription(NLS.bind(Messages.PatchSetContentRemoteFactory_Patch_Set,
							content.getTargetDetail().getPatchSet().getPatchSetId()));
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

	@Override
	public boolean isCreateModelNeeded(IReviewItemSet set, List<IFileItem> items) {
		return super.isCreateModelNeeded(set, items) || items == null || items.isEmpty();
	}

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
		if (changed && set.getReview() != null) {
			getGerritProvider().save(set.getReview());
		}
		return changed;
	}

	public GerritRemoteFactoryProvider getGerritProvider() {
		return gerritFactoryProvider;
	}

	@Override
	protected List<IFileItem> open(IReviewItemSet parentObject, String localKey) {
		return parentObject.getItems();
	}

	@Override
	public String getLocalKey(IReviewItemSet parentObject, List<IFileItem> modelObject) {
		return parentObject.getId();
	}

	public ReviewItemCache getCache() {
		return cache;
	}

	@Override
	public String getModelDescription(IReviewItemSet set, List<IFileItem> items, String localKey) {
		if (set.getReview() != null) {
			return NLS.bind(Messages.PatchSetContentRemoteFactory_Model_Description, set.getReview().getId(),
					set.getId());
		}
		return set.getName();
	}
}
