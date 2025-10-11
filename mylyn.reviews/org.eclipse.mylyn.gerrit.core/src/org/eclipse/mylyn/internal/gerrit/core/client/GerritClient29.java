/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.ErrorHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritSystemAccount;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ActionInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo28;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeMessageInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RelatedChangeAndCommitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RelatedChangesInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RevisionInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Branch;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSetInfo;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.UserIdentity;
import com.google.gwtjsonrpc.client.VoidResult;

public class GerritClient29 extends GerritClient {

	private final String OK = "OK"; //$NON-NLS-1$

	private final String NEED = "NEED"; //$NON-NLS-1$

	private final String REJECT = "REJECT"; //$NON-NLS-1$

	final String MAY = "MAY"; //$NON-NLS-1$

	private enum UserType {
		Author, Committer
	}

	protected GerritClient29(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	protected Account executeAccount(IProgressMonitor monitor) throws GerritException {
		if (isAnonymous()) {
			throw new GerritException(NOT_SIGNED_IN, -32603);
		}
		String query = "/accounts/self"; //$NON-NLS-1$/
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = getRestClient()
				.executeGetRestRequest(query, org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo.class,
						monitor);

		Account account = new Account(new Account.Id(accountInfo.getId()));
		account.setFullName(accountInfo.getName());
		account.setUserName(accountInfo.getUsername());
		account.setPreferredEmail(accountInfo.getEmail());
		return account;
	}

	@Override
	public ChangeDetail rebase(String reviewId, int patchSetId, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/rebase"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		getRestClient().executePostRestRequest(uri, new ChangeInfo28(), ChangeInfo28.class, new ErrorHandler() {
			@Override
			public void handleError(HttpMethodBase method) throws GerritException {
				String errorMsg = getResponseBodyAsString(method);
				if (isConflict(method)) {
					throw new GerritException(errorMsg);
				}
			}

			private String getResponseBodyAsString(HttpMethodBase method) {
				try {
					return method.getResponseBodyAsString();
				} catch (IOException e) {
					return null;
				}
			}

			private boolean isConflict(HttpMethodBase method) {
				return method.getStatusCode() == HttpURLConnection.HTTP_CONFLICT;
			}
		}, monitor);
		return getChangeDetail(id.getParentKey().get(), monitor);
	}

	private ChangeMessage convertChangeMessage(int reviewId, ChangeInfo changeInfo,
			ChangeMessageInfo changeMessageInfo) {
		Change.Id changeId = new Change.Id(reviewId);
		ChangeMessage.Key changeMessageKey = new ChangeMessage.Key(changeId, changeInfo.getId());
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo author = changeMessageInfo.getAuthor();
		if (author == null) {
			//if Author is not set, the ChangeMessageInfo was created by the Gerrit system
			author = GerritSystemAccount.GERRIT_SYSTEM;
		}
		Account.Id accountId = new Account.Id(author.getId());
		ChangeMessage changeMessage = new ChangeMessage(changeMessageKey, accountId, changeMessageInfo.getDate());
		changeMessage.setMessage(changeMessageInfo.getMesssage());
		return changeMessage;
	}

	private Branch.NameKey getBranchKey(ChangeInfo changeInfo) {
		Project.NameKey projectKey = new Project.NameKey(changeInfo.getProject());
		Branch.NameKey branchKey = new Branch.NameKey(projectKey, changeInfo.getBranch());
		return branchKey;
	}

	private Change createChange(String keyString, int changeIdValue, AccountInfo accountInfo,
			Branch.NameKey branchKey) {
		Change.Key key = new Change.Key(keyString);
		Change.Id changeId = new Change.Id(changeIdValue);
		Change change = new Change(key, changeId, accountInfo.getId(), branchKey);
		return change;
	}

	private PatchSetInfo getPatchSetInfo(PatchSet.Id patchsetId, String subject) {
		PatchSetInfo patchSetInfo = new PatchSetInfo(patchsetId);
		patchSetInfo.setSubject(subject);
		return patchSetInfo;
	}

	private com.google.gerrit.common.data.ChangeInfo convertToGoogleChangeInfo(RelatedChangeAndCommitInfo info,
			AccountInfo accountInfo, Branch.NameKey branchKey) {

		Change change = createChange(info.getChangeId(), info.getChangeNumber(), accountInfo, branchKey);
		PatchSet.Id patchsetId = new PatchSet.Id(change.getId(), info.getCurrentRevisionNumber());

		PatchSetInfo patchSetInfo = getPatchSetInfo(patchsetId, info.getCommitInfo().getSubject());
		change.setCurrentPatchSet(patchSetInfo);
		com.google.gerrit.common.data.ChangeInfo googleChangeInfo = new com.google.gerrit.common.data.ChangeInfo(
				change);

		return googleChangeInfo;
	}

	private AccountInfo convertAuthorFrom29ToAccountInfo(ChangeInfo changeInfo) {
		AccountInfo accountInfo;
		Account account;
		account = new Account(new Account.Id(changeInfo.getOwner().getId()));
		account.setFullName(changeInfo.getOwner().getName());
		account.setUserName(changeInfo.getOwner().getUsername());
		account.setPreferredEmail(changeInfo.getOwner().getEmail());
		accountInfo = new AccountInfo(account);

		return accountInfo;
	}

	@Override
	public PatchSetPublishDetailX getPatchSetPublishDetail(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		PatchSetPublishDetailX publishDetail = null;
		publishDetail = new PatchSetPublishDetailX();
		ChangeInfo changeInfo = getChangeInfo(id.getParentKey().get(), monitor);

		List<AccountInfo> listAccountInfo = new ArrayList<>();

		AccountInfo accountInfo = convertAuthorFrom29ToAccountInfo(changeInfo);
		listAccountInfo.add(accountInfo);
		AccountInfoCache accountInfoCache = new AccountInfoCache(listAccountInfo);
		publishDetail.setAccounts(accountInfoCache);

		Branch.NameKey branchKey = getBranchKey(changeInfo);
		Change currentChange = createChange(changeInfo.getChangeId(), id.getParentKey().get(), accountInfo, branchKey);
		com.google.gerrit.reviewdb.Change.Status status = changeInfo.getStatus();
		if (status != null) {
			currentChange.setStatus(status);
		}
		publishDetail.setChange(currentChange);

		PatchSetInfo patchSetInfo = getPatchSetInfo(changeInfo.getCurrentPatchSetId(), changeInfo.getSubject());
		publishDetail.setPatchSetInfo(patchSetInfo);
		publishDetail.setLabels(changeInfo.convertToPermissionLabels());

		if (publishDetail.getGiven() == null) {
			publishDetail.setGiven(changeInfo.convertToPatchSetApprovals(id, getAccount(monitor)));
		}
		return publishDetail;
	}

	private List<PatchSet> getPatchSets(final String changeInfoId, int reviewId, IProgressMonitor monitor)
			throws GerritException {

		List<PatchSet> patchSets = new ArrayList<>();
		String query = "/changes/?q=" + changeInfoId + "+change:" + reviewId + "&o=ALL_REVISIONS"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ChangeInfo28[] changeInfo = (ChangeInfo28[]) getRestClient().executeGetRestRequest(query, ChangeInfo28[].class,
				monitor);

		for (ChangeInfo element : changeInfo) {
			for (Entry<String, RevisionInfo> revisionInfo : element.getRevisions().entrySet()) {
				PatchSet.Id patchSetId = new PatchSet.Id(new Change.Id(reviewId), revisionInfo.getValue().getNumber());
				patchSets.add(new PatchSet(patchSetId));
			}

		}
		Collections.sort(patchSets, (p1, p2) -> p1.getPatchSetId() - p2.getPatchSetId());
		return patchSets;
	}

	private List<SubmitRecord> currentSubmitRecord(String uri, IProgressMonitor monitor) throws GerritException {
		List<SubmitRecord> submitRecordList = new ArrayList<>();
		if (isAnonymous()) {
			return submitRecordList;
		}
		SubmitRecord[] submitRecordArray = getRestClient().executePostRestRequest(uri, new SubmitRecord(),
				SubmitRecord[].class, new ErrorHandler() {
					@Override
					public void handleError(HttpMethodBase method) throws GerritException {
						String errorMsg = getResponseBodyAsString(method);
						if (isNotPermitted(method, errorMsg) || isConflict(method)) {
							throw new GerritException(NLS.bind("Cannot get submit change: {0}", errorMsg)); //$NON-NLS-1$
						}
					}

					private String getResponseBodyAsString(HttpMethodBase method) {
						try {
							return method.getResponseBodyAsString();
						} catch (IOException e) {
							return null;
						}
					}

					private boolean isNotPermitted(HttpMethodBase method, String msg) {
						return method.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN
								&& "submit not permitted\n".equals(msg); //$NON-NLS-1$
					}

					private boolean isConflict(HttpMethodBase method) {
						return method.getStatusCode() == HttpURLConnection.HTTP_CONFLICT;
					}
				}, monitor);
		for (SubmitRecord element : submitRecordArray) {
			List<SubmitRecord.Label> list = Collections.emptyList();
			if (element.getStatus().equalsIgnoreCase("OK")) { //$NON-NLS-1$
				list = element.createLabel(element, element.getOkMap(), OK);
			} else if (element.getStatus().equalsIgnoreCase("NOT_READY")) { //$NON-NLS-1$
				list = element.createLabel(element, element.getNeedMap(), NEED);
			} else if (element.getStatus().equalsIgnoreCase("REJECT")) { //$NON-NLS-1$
				list = element.createLabel(element, element.getRejectMap(), REJECT);
			} else if (element.getStatus().equalsIgnoreCase("MAY")) { //$NON-NLS-1$
				list = element.createLabel(element, element.getMayMap(), MAY);
			}
			element.setLabels(list);
			submitRecordList.add(element);
		}

		return submitRecordList;
	}

	@Override
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		String query = "/changes/" + Integer.toString(reviewId) + "/detail/?o=ALL_REVISIONS&o=MESSAGES"; //$NON-NLS-1$//$NON-NLS-2$
		ChangeInfo28 changeInfo = (ChangeInfo28) getRestClient().executeGetRestRequest(query, ChangeInfo28.class,
				monitor);

		List<PatchSet> patchSets = getPatchSets(changeInfo.getChangeId(), reviewId, monitor);

		List<ChangeMessage> listChangeMessage = new ArrayList<>();
		List<ChangeMessageInfo> listChangeMessageInfo = changeInfo.getMessages();

		boolean containsMessageFromGerritSystem = false;
		for (ChangeMessageInfo changeMessageInfo : listChangeMessageInfo) {

			if (changeMessageInfo.getAuthor() == null) {
				//if Author is not set, the ChangeMessageInfo was created by the Gerrit system
				containsMessageFromGerritSystem = true;
			}
			ChangeMessage changeMessage = convertChangeMessage(reviewId, changeInfo, changeMessageInfo);
			listChangeMessage.add(changeMessage);
		}

		List<AccountInfo> listAccountInfo = new ArrayList<>();

		AccountInfo accountInfo = convertAuthorFrom29ToAccountInfo(changeInfo);
		listAccountInfo.add(accountInfo);

		if (containsMessageFromGerritSystem) {
			//add Gerrit system if there was a ChangeMessageInfo that was created by the Gerrit system
			listAccountInfo.add(org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritSystemAccount.GERRIT_SYSTEM
					.getGerritSystemAccountInfo());
		}

		AccountInfoCache accountInfoCache = new AccountInfoCache(listAccountInfo);

		ChangeDetailX changeDetail = new ChangeDetailX();
		changeDetail.setDateCreated(changeInfo.getCreated());
		changeDetail.setLastModified(changeInfo.getUpdated());
		changeDetail.setStarred(changeInfo.getStarred() != null ? true : false);
		changeDetail.setAccounts(accountInfoCache);
		changeDetail.setMessages(listChangeMessage);

		Branch.NameKey branchKey = getBranchKey(changeInfo);

		setChangeDetailDependency(reviewId, changeInfo, changeDetail, accountInfo, monitor);
		PatchSetInfo patchSetInfo = getPatchSetInfo(changeInfo.getCurrentPatchSetId(), changeInfo.getSubject());

		changeDetail.setApprovals(changeInfo.convertToApprovalDetails());
		changeDetail.setApprovalTypes(changeInfo.convertToApprovalTypes());

		//Fill the submit records
		String querysubmit = "/changes/" + Integer.toString(reviewId) //$NON-NLS-1$
				+ "/revisions/current/test.submit_rule?filters=SKIP"; //$NON-NLS-1$
		List<SubmitRecord> submitRecord = currentSubmitRecord(querysubmit, monitor);
		changeDetail.setSubmitRecords(submitRecord);
		if (changeDetail.getApprovalTypes() == null && getGerritConfig() != null) {
			changeDetail.convertSubmitRecordsToApprovalTypes(getGerritConfig().getApprovalTypes());
		}

		changeDetail.setPatchSets(patchSets);
		List<ReviewerInfo> reviewers = listReviewers(reviewId, monitor);
		if (!hasAllReviewers(changeDetail.getAccounts(), reviewers)) {
			merge(changeDetail.getAccounts(), reviewers);
		}

		Change initialChange = createChange(changeInfo.getChangeId(), reviewId, accountInfo, branchKey);

		initialChange.setCurrentPatchSet(patchSetInfo);

		com.google.gerrit.reviewdb.Change.Status status = changeInfo.getStatus();
		if (status != null) {
			initialChange.setStatus(status);
		}
		changeDetail.setChange(initialChange);
		getAdditionalChangeInfo(reviewId, changeDetail, monitor);
		return changeDetail;
	}

	private RelatedChangesInfo getRelatedChanges(final int reviewId, String revisionId, IProgressMonitor monitor)
			throws GerritException {
		String query = "/changes/" + Integer.toString(reviewId) + "/revisions/" + revisionId + "/related"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		RelatedChangesInfo relatedChangesInfo = (RelatedChangesInfo) getRestClient().executeGetRestRequest(query,
				RelatedChangesInfo.class, monitor);
		return relatedChangesInfo;
	}

	private void setChangeDetailDependency(int reviewId, ChangeInfo28 changeInfo28, ChangeDetailX changeDetail,
			AccountInfo accountInfo, IProgressMonitor monitor) throws GerritException {
		List<com.google.gerrit.common.data.ChangeInfo> dependsOn = new ArrayList<>();
		List<com.google.gerrit.common.data.ChangeInfo> neededBy = new ArrayList<>();
		Branch.NameKey branchKey = getBranchKey(changeInfo28);

		RelatedChangesInfo relatedChangesInfo = getRelatedChanges(reviewId, changeInfo28.getCurrentRevision(), monitor);
		List<RelatedChangeAndCommitInfo> listCommitInfo = relatedChangesInfo.getCommitInfo();
		boolean needed = true;
		for (RelatedChangeAndCommitInfo relatedChangeAndCommitInfo : listCommitInfo) {
			if (relatedChangeAndCommitInfo.getCommitInfo()
					.getCommit()
					.equalsIgnoreCase(changeInfo28.getCurrentRevision())) {
				needed = false;
			} else if (relatedChangeAndCommitInfo.getChangeNumber() > 0) {
				com.google.gerrit.common.data.ChangeInfo googleChangeInfo = convertToGoogleChangeInfo(
						relatedChangeAndCommitInfo, accountInfo, branchKey);
				if (needed) {
					neededBy.add(googleChangeInfo);
				} else {
					dependsOn.add(googleChangeInfo);
				}
			}
		}
		changeDetail.setNeededBy(neededBy);
		changeDetail.setDependsOn(dependsOn);
	}

	@Override
	public CommentInput saveDraft(Key patchKey, String message, int line, short side, String parentUuid, String uuid,
			IProgressMonitor monitor) throws GerritException {
		if (uuid == null) {
			uuid = ""; //$NON-NLS-1$
		}
		CommentInput commentInput = new CommentInput();
		commentInput.setLine(line);
		commentInput.setMessage(message);
		commentInput.setPath(patchKey.getFileName());
		if (side == 0) {
			commentInput.setSide("PARENT"); //$NON-NLS-1$
		} else {
			commentInput.setSide("REVISION"); //$NON-NLS-1$
		}

		String uri = "/changes/" + Integer.toString(patchKey.getParentKey().getParentKey().get()) //$NON-NLS-1$
				+ "/revisions/" + patchKey.getParentKey().get() + "/drafts"; //$NON-NLS-1$ //$NON-NLS-2$
		if (!uuid.isEmpty()) {
			uri = uri.concat("/" + uuid); //$NON-NLS-1$
		}

		return getRestClient().executePutRestRequest(uri, commentInput, CommentInput.class, null, monitor);
	}

	private void getAdditionalChangeInfo(int reviewId, ChangeDetailX changeDetail, IProgressMonitor monitor) {
		ChangeInfo28 changeInfo = getAdditionalChangeInfo(reviewId, monitor);
		if (changeInfo != null) {
			setRevisionActions(changeInfo, changeDetail);
			setGlobalActions(changeInfo, changeDetail);
			setRevisionParentCommit(changeInfo, changeDetail);
		}
	}

	private void setRevisionActions(ChangeInfo28 changeInfo, ChangeDetailX changeDetail) {
		if (changeInfo.getRevisions() != null) {
			for (Entry<String, RevisionInfo> revisions : changeInfo.getRevisions().entrySet()) {
				if (revisions.getValue().getActions() != null) {
					for (Entry<String, ActionInfo> actions : revisions.getValue().getActions().entrySet()) {
						if (actions.getKey().equalsIgnoreCase("submit")) { //$NON-NLS-1$
							changeDetail.setCanSubmit(actions.getValue().getEnabled());
						} else if (actions.getKey().equalsIgnoreCase("rebase")) { //$NON-NLS-1$
							changeDetail.setCanRebase(actions.getValue().getEnabled());
						} else if (actions.getKey().equalsIgnoreCase("cherrypick")) { //$NON-NLS-1$
							changeDetail.setCanCherryPick(actions.getValue().getEnabled());
						}
					}
				}
			}
		}
	}

	private void setGlobalActions(ChangeInfo28 changeInfo, ChangeDetailX changeDetail) {
		if (changeInfo.getActions() != null) {
			for (Entry<String, ActionInfo> actions : changeInfo.getActions().entrySet()) {
				if (actions.getKey().equalsIgnoreCase("abandon")) { //$NON-NLS-1$
					changeDetail.setCanAbandon(actions.getValue().getEnabled());
					changeDetail.setCanRestore(!actions.getValue().getEnabled());
				} else if (actions.getKey().equalsIgnoreCase("restore")) { //$NON-NLS-1$
					changeDetail.setCanAbandon(!actions.getValue().getEnabled());
					changeDetail.setCanRestore(actions.getValue().getEnabled());
				}
			}
		}
	}

	@Override
	public VoidResult deleteDraft(Key patchkey, String uuid, IProgressMonitor monitor) throws GerritException {
		CommentInput commentInput = new CommentInput();
		String uri = "/changes/" + Integer.toString(patchkey.getParentKey().getParentKey().get()) //$NON-NLS-1$
				+ "/revisions/" + patchkey.getParentKey().get() + "/drafts/" + uuid; //$NON-NLS-1$ //$NON-NLS-2$

		getRestClient().executeDeleteRestRequest(uri, commentInput, CommentInput.class, null, monitor);
		return null;
	}

	private PatchSetInfo setAccountPatchSetInfo(PatchSetInfo patchSetInfo, IProgressMonitor monitor) {
		if (patchSetInfo.getAuthor().getAccount() == null) {
			patchSetInfo.setAuthor(setUserIdentity(patchSetInfo.getAuthor().getName(), patchSetInfo.getAuthor(),
					UserType.Author, monitor));
		}
		if (patchSetInfo.getCommitter().getAccount() == null) {
			patchSetInfo.setCommitter(setUserIdentity(patchSetInfo.getCommitter().getName(),
					patchSetInfo.getCommitter(), UserType.Committer, monitor));
		}
		return patchSetInfo;
	}

	private org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo getAccountInfo(String account,
			IProgressMonitor monitor) throws GerritException, URIException {
		if (GerritSystemAccount.GERRIT_SYSTEM_NAME.equals(account)) {
			return GerritSystemAccount.GERRIT_SYSTEM;
		}
		String st = URIUtil.encodeQuery(account);
		final String uri = "/accounts/" + st; //$NON-NLS-1$
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = getRestClient()
				.executeGetRestRequest(uri, org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo.class,
						monitor);
		return accountInfo;
	}

	private UserIdentity setUserIdentity(String name, UserIdentity userIdentity, UserType user,
			IProgressMonitor monitor) {
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = null;
		try {
			accountInfo = getAccountInfo(name, monitor);
			Account.Id accountId = new Account.Id(accountInfo.getId());
			userIdentity.setAccount(accountId);
		} catch (GerritException gerritException) {
			if (gerritException.getMessage().indexOf(HttpStatus.SC_NOT_FOUND) != 0) {
				StatusHandler.log(new Status(IStatus.WARNING, GerritCorePlugin.PLUGIN_ID,
						NLS.bind("GerritException {0} not found", user), gerritException)); //$NON-NLS-1$
			}
		} catch (URIException uriException) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					NLS.bind("{0} URIException: ", user), uriException)); //$NON-NLS-1$
		}
		return userIdentity;
	}

	@Override
	protected void applyPatchSetInfo(PatchSetDetail patchSetDetail, PatchSetPublishDetailX patchSetPublishDetail,
			IProgressMonitor monitor) throws GerritException {
		//To add the AccountId if available
		patchSetPublishDetail.setPatchSetInfo(setAccountPatchSetInfo(patchSetDetail.getInfo(), monitor));
	}
}
