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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritConfiguration;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord.Label;
import org.eclipse.mylyn.reviews.core.model.IApprovalType;
import org.eclipse.mylyn.reviews.core.model.IChange;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IRequirementEntry;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IReviewerEntry;
import org.eclipse.mylyn.reviews.core.model.IReviewsFactory;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.core.model.RequirementStatus;
import org.eclipse.mylyn.reviews.core.model.ReviewStatus;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.RemoteEmfConsumer;
import org.eclipse.mylyn.reviews.core.spi.remote.review.ReviewRemoteFactory;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ApprovalType;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.PatchSetApproval;
import com.google.gerrit.reviewdb.UserIdentity;

/**
 * Manages retrieval of Review information from Gerrit API. Also creates, adds, and updates contents of review sets for
 * each patch set, but does not retrieve the patch set details or contents.
 * 
 * @author Miles Parker
 * @author Steffen Pingel
 */
public class GerritReviewRemoteFactory extends ReviewRemoteFactory<GerritChange, String> {

	public GerritReviewRemoteFactory(GerritRemoteFactoryProvider gerritRemoteFactoryProvider) {
		super(gerritRemoteFactoryProvider);
	}

	@Override
	protected IReview open(IRepository parentObject, String localKey) {
		return getGerritProvider().open(localKey);
	}

	@Override
	public boolean isPullNeeded(IRepository parent, IReview review, GerritChange remote) {
		//We don't know if we need a pull until we actually retrieve the data
		return true;
	}

	@Override
	public GerritChange pull(IRepository parent, String remoteKey, IProgressMonitor monitor) throws CoreException {
		try {
			getGerritProvider().getClient().refreshConfigOnce(new NullProgressMonitor());

			GerritChange gerritChange = getGerritProvider().getClient().getChange(remoteKey, monitor);
			final ChangeDetailX detail = gerritChange.getChangeDetail();

			//We need to ensure we have all possible users for review in pull phase, as we can't do any async calls in apply phase
			getGerritProvider().pullUser(parent, detail.getAccounts(), detail.getChange().getOwner(), monitor);
			for (ChangeMessage message : detail.getMessages()) {
				if (message.getAuthor() != null) {
					getGerritProvider().pullUser(parent, detail.getAccounts(), message.getAuthor(), monitor);
				}
			}
			for (PatchSetDetail patchSetDetail : gerritChange.getPatchSetDetails()) {
				getGerritProvider().pullUser(parent, detail.getAccounts(),
						patchSetDetail.getInfo().getAuthor().getAccount(), monitor);
				getGerritProvider().pullUser(parent, detail.getAccounts(),
						patchSetDetail.getInfo().getCommitter().getAccount(), monitor);
			}
			for (ApprovalDetail remoteApproval : detail.getApprovals()) {
				getGerritProvider().pullUser(parent, detail.getAccounts(), remoteApproval.getAccount(), monitor);
			}

			if (detail.getSubmitRecords() != null) {
				for (SubmitRecord record : detail.getSubmitRecords()) {
					for (Label label : record.getLabels()) {
						if (label.getAppliedBy() != null) {
							getGerritProvider().pullUser(parent, detail.getAccounts(), label.getAppliedBy(), monitor);
						}
					}
				}
			}

			pull(parent, detail, detail.getDependsOn(), monitor);
			pull(parent, detail, detail.getNeededBy(), monitor);
			return gerritChange;
		} catch (GerritException e) {
			throw GerritCorePlugin.getDefault()
					.getConnector()
					.toCoreException(parent.getTaskRepository(), "Problem while retrieving Gerrit review.", e);
		}
	}

	protected void pull(IRepository parent, ChangeDetailX detail, List<ChangeInfo> remoteChanges,
			IProgressMonitor monitor) throws CoreException {
		for (ChangeInfo remoteChange : remoteChanges) {
			AccountInfo remoteOwner = detail.getAccounts().get(remoteChange.getOwner());
			getGerritProvider().pullUser(parent, detail.getAccounts(), remoteOwner.getId(), monitor);
		}
	}

	@Override
	public boolean isCreateModelNeeded(IRepository parentObject, IReview modelObject) {
		return super.isCreateModelNeeded(parentObject, modelObject) || modelObject.getModificationDate() == null;
	}

	@Override
	public IReview createModel(IRepository parent, GerritChange gerritChange) {
		final ChangeDetailX detail = gerritChange.getChangeDetail();
		Change change = detail.getChange();

		IReview review = getGerritProvider().open(getLocalKeyForRemoteObject(gerritChange));

		//Immutable Data (?)
		review.setKey(change.getKey().get());
		review.setId(getLocalKeyForRemoteObject(gerritChange));
		AccountInfo remoteOwner = detail.getAccounts().get(change.getOwner());
		IUser owner = getGerritProvider().createUser(parent, detail.getAccounts(), remoteOwner.getId());
		review.setOwner(owner);
		review.setCreationDate(change.getCreatedOn());
		parent.getReviews().add(review);

		return review;
	}

	@Override
	public boolean isUpdateModelNeeded(IRepository parent, IReview review, GerritChange gerritChange) {
		Change change = gerritChange.getChangeDetail().getChange();
		gerritChange.getChangeDetail().getDependsOn();
		isDependenciesDifferent(review.getParents(), gerritChange.getChangeDetail().getDependsOn());
		return review.getModificationDate() == null || !review.getModificationDate().equals(change.getLastUpdatedOn())
				|| isDependenciesDifferent(review.getParents(), gerritChange.getChangeDetail().getDependsOn())
				|| isDependenciesDifferent(review.getChildren(), gerritChange.getChangeDetail().getNeededBy());
	}

	public boolean isDependenciesDifferent(List<IChange> localDependencies, List<ChangeInfo> remoteDependencies) {
		Set<String> localIds = new HashSet<String>();
		for (IChange localChange : localDependencies) {
			localIds.add(localChange.getId());
		}
		Set<String> remoteIds = new HashSet<String>();
		for (ChangeInfo depend : remoteDependencies) {
			remoteIds.add(depend.getId().toString());
		}
		return !localIds.equals(remoteDependencies);
	}

	@Override
	public boolean updateModel(IRepository parent, IReview review, GerritChange gerritChange) {
		ChangeDetailX detail = gerritChange.getChangeDetail();
		Change change = detail.getChange();

		//Mutable Data
		review.setModificationDate(new Date(change.getLastUpdatedOn().getTime())); //Convert from SQL Timestamp
		review.setSubject(change.getSubject());
		review.setMessage(detail.getDescription());

		updateComments(parent, review, detail);
		updateEmptyPatchSets(parent, review, gerritChange);
		updateApprovalsAndRequirements(parent, review, detail);
		updateDependencies(parent, review, detail);
		return true;
	}

	public void updateComments(IRepository parent, IReview review, ChangeDetailX detail) {
		int oldCommentCount = review.getComments().size();
		int commentIndex = 0;
		for (ChangeMessage message : detail.getMessages()) {
			if (commentIndex++ < oldCommentCount) {
				continue;
			}
			IComment comment = review.createComment(null, message.getMessage());
			comment.setDraft(false);
			comment.setCreationDate(message.getWrittenOn());
			if (message.getAuthor() != null) {
				IUser author = getGerritProvider().createUser(parent, detail.getAccounts(), message.getAuthor());
				comment.setAuthor(author);
			}
		}
	}

	public void updateEmptyPatchSets(IRepository parent, IReview review, GerritChange gerritChange) {
		ChangeDetailX detail = gerritChange.getChangeDetail();
		//Basic Patch Sets
		int oldPatchCount = review.getSets().size();
		int patchIndex = 0;
		PatchSetDetailRemoteFactory itemSetFactory = getGerritProvider().getReviewItemSetFactory();
		for (PatchSetDetail patchSetDetail : gerritChange.getPatchSetDetails()) {
			RemoteEmfConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> consumer = itemSetFactory.getConsumerForRemoteObject(
					review, patchSetDetail);
			try {
				//We force a pull here, which is safe because there isn't any actual client API invocation
				consumer.pull(true, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new RuntimeException("Internal Exception. Unexpected state.", e);
			}
			if (patchIndex++ < oldPatchCount) {
				continue;
			}
			consumer.applyModel(false);
			IReviewItemSet itemSet = consumer.getModelObject();
			IUser author = getGerritProvider().createUser(parent, detail.getAccounts(),
					patchSetDetail.getInfo().getAuthor().getAccount());
			itemSet.setAddedBy(author);
			//User Identity refers to a specific user interaction for the current patch set
			UserIdentity committerIdent = patchSetDetail.getInfo().getCommitter();
			if (committerIdent != null) {
				IUser committer = getGerritProvider().createUser(parent, detail.getAccounts(),
						committerIdent.getAccount());
				itemSet.setCommittedBy(committer);
				itemSet.setCreationDate(committerIdent.getDate());
			}
			UserIdentity authorIdent = patchSetDetail.getInfo().getAuthor();
			if (authorIdent != null) {
				itemSet.setModificationDate(authorIdent.getDate());
			}
			consumer.release();
		}
	}

	public void updateApprovalsAndRequirements(IRepository parent, IReview review, ChangeDetailX detail) {
		Map<String, IApprovalType> typeForKey = new HashMap<String, IApprovalType>();
		Map<String, IApprovalType> typeForName = new HashMap<String, IApprovalType>();
		for (IApprovalType type : parent.getApprovalTypes()) {
			typeForKey.put(type.getKey(), type);
		}
		GerritConfiguration configuration = getGerritProvider().getClient().getConfiguration();
		for (ApprovalType remoteType : configuration.getGerritConfig().getApprovalTypes().getApprovalTypes()) {
			IApprovalType localApprovalType = typeForKey.get(remoteType.getCategory().getId().get());
			if (localApprovalType == null) {
				localApprovalType = IReviewsFactory.INSTANCE.createApprovalType();
				localApprovalType.setKey(remoteType.getCategory().getId().get());
				localApprovalType.setName(remoteType.getCategory().getName());
				parent.getApprovalTypes().add(localApprovalType);
				typeForKey.put(localApprovalType.getKey(), localApprovalType);
			}
			String approvalName = remoteType.getCategory().getName();
			//Special case so we can match different label name for status records. (?!)
			approvalName = approvalName.replace(" ", "-");
			typeForName.put(approvalName, localApprovalType);
		}

		//Approvals
		review.getReviewerApprovals().clear();
		for (ApprovalDetail remoteApproval : detail.getApprovals()) {
			IUser reviewer = getGerritProvider().createUser(parent, detail.getAccounts(), remoteApproval.getAccount());
			if (reviewer == null) {
				throw new RuntimeException("Internal Error, no reviewer found for: " + remoteApproval.getAccount());
			}
			IReviewerEntry reviewerEntry = review.getReviewerApprovals().get(reviewer);
			if (reviewerEntry == null) {
				reviewerEntry = IReviewsFactory.INSTANCE.createReviewerEntry();
				review.getReviewerApprovals().put(reviewer, reviewerEntry);
			}
			for (Entry<com.google.gerrit.reviewdb.ApprovalCategory.Id, PatchSetApproval> remoteMap : remoteApproval.getApprovalMap()
					.entrySet()) {
				String remoteType = remoteMap.getValue().getCategoryId().get();
				IApprovalType approvalType = typeForKey.get(remoteType);
				if (approvalType == null) {
					approvalType = IReviewsFactory.INSTANCE.createApprovalType();
					approvalType.setKey(remoteType);
					approvalType.setName(remoteType);
					parent.getApprovalTypes().add(approvalType);
					typeForKey.put(approvalType.getKey(), approvalType);
				}
				reviewerEntry.getApprovals().put(approvalType, (int) remoteMap.getValue().getValue());
			}
		}

		//Requirements
		review.getRequirements().clear();
		if (detail.getSubmitRecords() != null) {
			for (SubmitRecord record : detail.getSubmitRecords()) {
				for (Label label : record.getLabels()) {
					IApprovalType approvalType = typeForName.get(label.getLabel());
					if (approvalType == null) {
						throw new RuntimeException("Internal Error, no approval type found for: " + label.getLabel());
					}
					IRequirementEntry requirementEntry = IReviewsFactory.INSTANCE.createRequirementEntry();
					if (label.getStatus().equals("OK")) {
						requirementEntry.setStatus(RequirementStatus.SATISFIED);
					} else if (label.getStatus().equals("NEED")) {
						requirementEntry.setStatus(RequirementStatus.NOT_SATISFIED);
					} else if (label.getStatus().equals("REJECT")) {
						requirementEntry.setStatus(RequirementStatus.REJECTED);
					} else if (label.getStatus().equals("MAY")) {
						requirementEntry.setStatus(RequirementStatus.OPTIONAL);
					} else if (label.getStatus().equals("IMPOSSIBLE")) {
						requirementEntry.setStatus(RequirementStatus.ERROR);
					}
					if (label.getAppliedBy() != null) {
						IUser approver = getGerritProvider().createUser(parent, detail.getAccounts(),
								label.getAppliedBy());
						requirementEntry.setBy(approver);
					}
					review.getRequirements().put(approvalType, requirementEntry);
				}
			}
		}

		//State
		switch (detail.getChange().getStatus()) {
		case NEW:
			review.setState(ReviewStatus.NEW);
			break;
		case MERGED:
			review.setState(ReviewStatus.MERGED);
			break;
		case SUBMITTED:
			review.setState(ReviewStatus.SUBMITTED);
			break;
		case ABANDONED:
			review.setState(ReviewStatus.ABANDONED);
			break;
		}
	}

	public void updateDependencies(IRepository parent, IReview review, ChangeDetailX detail) {
		create(parent, review.getParents(), detail, detail.getDependsOn());
		create(parent, review.getChildren(), detail, detail.getNeededBy());
	}

	protected void create(IRepository group, List<IChange> localChanges, ChangeDetailX detail,
			List<ChangeInfo> remoteChanges) {
		localChanges.clear();
		for (ChangeInfo remoteChange : remoteChanges) {
			final IChange localChange = IReviewsFactory.INSTANCE.createChange();
			localChange.setKey(remoteChange.getKey().get());
			localChange.setId(remoteChange.getId().get() + "");
			AccountInfo remoteOwner = detail.getAccounts().get(remoteChange.getOwner());
			IUser owner = getGerritProvider().createUser(group, detail.getAccounts(), remoteOwner.getId());
			localChange.setOwner(owner);
			localChange.setModificationDate(remoteChange.getLastUpdatedOn());
			localChange.setSubject(remoteChange.getSubject());
			switch (remoteChange.getStatus()) {
			case NEW:
				localChange.setState(ReviewStatus.NEW);
				break;
			case MERGED:
				localChange.setState(ReviewStatus.MERGED);
				break;
			case SUBMITTED:
				localChange.setState(ReviewStatus.SUBMITTED);
				break;
			case ABANDONED:
				localChange.setState(ReviewStatus.ABANDONED);
				break;
			}
			localChanges.add(localChange);
		}
	}

	@Override
	public String getRemoteKey(GerritChange remoteObject) {
		return remoteObject.getChangeDetail().getChange().getId().get() + "";
	}

	@Override
	public String getLocalKeyForRemoteObject(GerritChange remoteObject) {
		return getRemoteKey(remoteObject);
	}

	@Override
	public String getLocalKeyForRemoteKey(String remoteKey) {
		return remoteKey;
	}

	@Override
	public String getRemoteKeyForLocalKey(IRepository parentObject, String localKey) {
		return localKey;
	}

	public GerritRemoteFactoryProvider getGerritProvider() {
		return (GerritRemoteFactoryProvider) getFactoryProvider();
	}
}
