/*********************************************************************
 * Copyright (c) 2010, 2013 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Technologies - improvements
 *      Sascha Scholz (SAP) - improvements
 *      GitHub, Inc. - fixes for bug 354753
 *      Christian Trutz - improvements
 *      Francois Chouinard - Added "LABELS" option on selected queries
 *      Jacques Bouthillier - Bug 414253 Add support for Gerrit Dashboard
 *      Jacques Bouthillier (Ericsson) - Bug 426505 Add Starred functionality
 *      Guy Perron (Ericsson) Bug 423242 Add ability to edit comment from compare navigator popup
 *      Guy Perron/Jacques Bouthillier Bug 437825  support Gerrit 2.9 with API changes
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.ErrorHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeManageService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritSystemAccount;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectAdminService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SubmitRecord;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AbandonInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ActionInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AddReviewerResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo29;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeMessageInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RelatedChangeAndCommitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RelatedChangesInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RestoreInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ReviewerInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.RevisionInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.SubmitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.SubmitInput;
import org.eclipse.mylyn.internal.gerrit.core.remote.GerritRemoteFactoryProvider;
import org.eclipse.mylyn.reviews.core.model.IRepository;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.spi.ReviewsClient;
import org.eclipse.mylyn.reviews.core.spi.remote.emf.AbstractRemoteEmfFactoryProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.common.data.ToggleStarRequest;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Branch;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ChangeMessage;
import com.google.gerrit.reviewdb.ContributorAgreement;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.ChangeType;
import com.google.gerrit.reviewdb.PatchLineComment;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSet.Id;
import com.google.gerrit.reviewdb.PatchSetInfo;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;
import com.google.gerrit.reviewdb.UserIdentity;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.RemoteJsonService;
import com.google.gwtjsonrpc.client.VoidResult;

/**
 * Facade to the Gerrit RPC API.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 * @author Christian Trutz
 * @author Sascha Scholz
 * @author Miles Parker
 * @author Francois Chouinard
 * @author Jacques Bouthillier
 * @author Guy Perron
 */
public class GerritClient extends ReviewsClient {

	private static final Pattern GERRIT_VERSION_PATTERN = Pattern.compile("Powered by Gerrit Code Review (.+)</p>"); //$NON-NLS-1$

	private static final String GET_LABELS_OPTION = "LABELS"; //$NON-NLS-1$

	private final String NOT_SIGNED_IN = "Not Signed In"; //$NON-NLS-1$

	private final String OK = "OK"; //$NON-NLS-1$

	private final String NEED = "NEED"; //$NON-NLS-1$

	private final String REJECT = "REJECT"; //$NON-NLS-1$

	final String MAY = "MAY"; //$NON-NLS-1$

	private abstract class Operation<T> implements AsyncCallback<T> {

		private Throwable exception;

		private T result;

		public abstract void execute(IProgressMonitor monitor) throws GerritException;

		public Throwable getException() {
			return exception;
		}

		public T getResult() {
			return result;
		}

		public void onFailure(Throwable exception) {
			if (isAuthenticationException(exception)) {
				// invalidate login cookie to force re-authentication
				client.setXsrfCookie(null);
			}
			this.exception = exception;
		}

		public void onSuccess(T result) {
			setResult(result);
		}

		protected void setResult(T result) {
			this.result = result;
		}

		public void reset() {
			this.result = null;
			this.exception = null;
		}

	}

	public boolean isAuthenticationException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& "Invalid xsrfKey in request".equals(((GerritException) exception).getMessage()); //$NON-NLS-1$
		}
		return false;
	}

	public boolean isNotSignedInException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& NOT_SIGNED_IN.equalsIgnoreCase(((GerritException) exception).getMessage());
		}
		return false;
	}

	// XXX belongs in GerritConnector
	public static GerritAuthenticationState authStateFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.parseResponse(token, GerritAuthenticationState.class);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	// XXX belongs in GerritConnector
	public static String authStateToString(GerritAuthenticationState authState) {
		if (authState == null) {
			return null;
		}
		try {
			JSonSupport support = new JSonSupport();
			return support.toJson(authState);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	private final GerritHttpClient client;

	private volatile GerritConfiguration config;

	private Account myAcount;

	private Version myVersion;

	private final Map<Class<? extends RemoteJsonService>, RemoteJsonService> serviceByClass;

	private volatile boolean configRefreshed;

	/**
	 * The GWT query API was removed in Gerrit 2.5 and replaced with a REST API. If this flag is true, the REST API is
	 * used.
	 */
	private boolean restQueryAPIEnabled;

	public GerritClient(TaskRepository repository, AbstractWebLocation location) {
		this(repository, location, null, null, null);
	}

	public GerritClient(TaskRepository repository, AbstractWebLocation location, GerritConfiguration config,
			GerritAuthenticationState authState) {
		this(repository, location, config, authState, null);
	}

	public GerritClient(TaskRepository repository, AbstractWebLocation location, GerritConfiguration config,
			GerritAuthenticationState authState, String xsrfKey) {
		super(repository);
		this.client = new GerritHttpClient(location) {
			@Override
			protected void sessionChanged(Cookie cookie) {
				GerritAuthenticationState authState = new GerritAuthenticationState();
				authState.setCookie(cookie);
				authStateChanged(authState);
			}
		};
		if (authState != null) {
			client.setXsrfCookie(authState.getCookie());
		}
		if (xsrfKey != null) {
			client.setXsrfKey(xsrfKey);
		}
		this.serviceByClass = new HashMap<Class<? extends RemoteJsonService>, RemoteJsonService>();
		this.config = config;
	}

	public void createRemoteBranch(String projectName, String branchName, String revision, IProgressMonitor monitor)
			throws GerritException {
		Assert.isTrue(isVersion29OrLater(monitor), 
				"Creating branches is only supported in Gerrit versions 2.9 or later"); //$NON-NLS-1$
		String url = getProjectBranchesUrl(projectName) + branchName;
		BranchInput input = new BranchInput(branchName, revision);
		executePutRestRequest(url, input, BranchInput.class, createErrorHandler(), monitor);
	}

	public BranchInfo[] getRemoteProjectBranches(String projectName, IProgressMonitor monitor) throws GerritException {
		Assert.isTrue(isVersion29OrLater(monitor), 
				"Querying branches is only supported in Gerrit versions 2.9 or later"); //$NON-NLS-1$
		String url = getProjectBranchesUrl(projectName);
		return executeGetRestRequest(url, BranchInfo[].class, monitor);
	}

	private String getProjectBranchesUrl(String projectName) {
		return "/projects/" + projectName + "/branches/"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public PatchLineComment saveDraft(Patch.Key patchKey, String message, int line, short side, String parentUuid,
			String uuid, IProgressMonitor monitor) throws GerritException {
		PatchLineComment.Key id = new PatchLineComment.Key(patchKey, uuid);
		final PatchLineComment comment = new PatchLineComment(id, line, getAccount(monitor).getId(), parentUuid);
		comment.setMessage(message);
		comment.setSide(side);
		if (isVersion29OrLater(monitor)) {
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

			executePutRestRequest(uri, commentInput, CommentInput.class, null/*no error handler*/, monitor);

			return comment;
		} else {
			return execute(monitor, new Operation<PatchLineComment>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getPatchDetailService(monitor).saveDraft(comment, this);
				}
			});
		}
	}

	public VoidResult deleteDraft(Patch.Key patchkey, String uuid, IProgressMonitor monitor) throws GerritException {
		final PatchLineComment.Key id = new PatchLineComment.Key(patchkey, uuid);
		if (isVersion29OrLater(monitor)) {
			CommentInput commentInput = new CommentInput();
			String uri = "/changes/" + Integer.toString(patchkey.getParentKey().getParentKey().get()) //$NON-NLS-1$
					+ "/revisions/" + patchkey.getParentKey().get() + "/drafts/" + uuid; //$NON-NLS-1$ //$NON-NLS-2$

			executeDeleteRestRequest(uri, commentInput, CommentInput.class, null/*no error handler*/, monitor);
			return null;
		} else {
			return execute(monitor, new Operation<VoidResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getPatchDetailService(monitor).deleteDraft(id, this);
				}
			});
		}
	}

	public ChangeDetail abandon(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		if (hasJsonRpcApi(monitor)) {
			return execute(monitor, new Operation<ChangeDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeManageService(monitor).abandonChange(id, message, this);
				}
			});
		} else {
			final String uri = "/a/changes/" + id.getParentKey().get() + "/abandon"; //$NON-NLS-1$ //$NON-NLS-2$
			executePostRestRequest(uri, new AbandonInput(message), ChangeInfo.class, null/*no error handler*/, monitor);
			return getChangeDetail(id.getParentKey().get(), monitor);
		}
	}

	public Object getChangeInfo29(String query, Class returnClass, IProgressMonitor monitor) throws GerritException {
		return executeGetRestRequest(query, returnClass, monitor);
	}

	private List<PatchSet> getPatchSets29(final String changeInfoId, int reviewId, IProgressMonitor monitor)
			throws GerritException {

		List<PatchSet> patchSets = new ArrayList<PatchSet>();
		String query = "/changes/?q=" + changeInfoId + "+change:" + reviewId + "&o=ALL_REVISIONS"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ChangeInfo29[] changeInfo = (ChangeInfo29[]) getChangeInfo29(query, ChangeInfo29[].class, monitor);

		for (ChangeInfo element : changeInfo) {
			for (Entry<String, RevisionInfo> revisionInfo : element.getRevisions().entrySet()) {
				PatchSet.Id patchSetId = new PatchSet.Id(new Change.Id(reviewId), revisionInfo.getValue().getNumber());
				patchSets.add(new PatchSet(patchSetId));
			}

		}
		Collections.sort(patchSets, new Comparator<PatchSet>() {

			@Override
			public int compare(PatchSet p1, PatchSet p2) {
				return p1.getPatchSetId() - p2.getPatchSetId();
			}

		});
		return patchSets;
	}

	public RelatedChangesInfo getRelatedChanges(final int reviewId, String revisionId, IProgressMonitor monitor)
			throws GerritException {
		String query = "/changes/" + Integer.toString(reviewId) + "/revisions/" + revisionId + "/related"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		RelatedChangesInfo relatedChangesInfo = (RelatedChangesInfo) getChangeInfo29(query, RelatedChangesInfo.class,
				monitor);
		return relatedChangesInfo;
	}

	private ChangeMessage convertChangeMessage(int reviewId, ChangeInfo changeInfo, ChangeMessageInfo changeMessageInfo) {
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

	private Change createChange(String keyString, int changeIdValue, AccountInfo accountInfo, Branch.NameKey branchKey) {
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
		PatchSet.Id patchsetId = new PatchSet.Id(change.getId(), info.getCurrentRevisionNumbe());

		PatchSetInfo patchSetInfo = getPatchSetInfo(patchsetId, info.getCommitInfo().getSubject());
		change.setCurrentPatchSet(patchSetInfo);
		com.google.gerrit.common.data.ChangeInfo googleChangeInfo = new com.google.gerrit.common.data.ChangeInfo(change);

		return googleChangeInfo;
	}

	public ChangeDetailX getChangeDetail29(int reviewId, IProgressMonitor monitor) throws GerritException {
		ChangeDetailX changeDetail = null;
		String query = "/changes/" + Integer.toString(reviewId) + "/detail/?o=ALL_REVISIONS&o=MESSAGES"; //$NON-NLS-1$//$NON-NLS-2$
		ChangeInfo29 changeInfo = (ChangeInfo29) getChangeInfo29(query, ChangeInfo29.class, monitor);

		List<PatchSet> patchSets = getPatchSets29(changeInfo.getChangeId(), reviewId, monitor);

		List<ChangeMessage> listChangeMessage = new ArrayList<ChangeMessage>();
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

		List<AccountInfo> listAccountInfo = new ArrayList<AccountInfo>();

		AccountInfo accountInfo = convertAuthorFrom29ToAccountInfo(changeInfo);
		listAccountInfo.add(accountInfo);

		if (containsMessageFromGerritSystem) {
			//add Gerrit system if there was a ChangeMessageInfo that was created by the Gerrit system
			listAccountInfo.add(org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritSystemAccount.GERRIT_SYSTEM.getGerritSystemAccountInfo());
		}

		AccountInfoCache accountInfoCache = new AccountInfoCache(listAccountInfo);

		changeDetail = new ChangeDetailX();
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
		String querysubmit = "/changes/" + Integer.toString(reviewId) + "/revisions/current/test.submit_rule?filters=SKIP"; //$NON-NLS-1$//$NON-NLS-2$
		List<SubmitRecord> submitRecord = currentSubmitRecord29(querysubmit, monitor);
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

		initialChange.setStatus(changeInfo.getStatus());
		changeDetail.setChange(initialChange);
		setActions29(reviewId, changeDetail, monitor);
		return changeDetail;
	}

	private List<SubmitRecord> currentSubmitRecord29(String uri, IProgressMonitor monitor) throws GerritException {
		List<SubmitRecord> submitRecordList = new ArrayList<SubmitRecord>();
		SubmitRecord[] submitRecordArray = executePostRestRequest(uri, new SubmitRecord(), SubmitRecord[].class,
				new ErrorHandler() {
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
			List<SubmitRecord.Label> list = null;
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

	private void setChangeDetailDependency(int reviewId, ChangeInfo29 changeInfo29, ChangeDetailX changeDetail,
			AccountInfo accountInfo, IProgressMonitor monitor) throws GerritException {
		List<com.google.gerrit.common.data.ChangeInfo> dependsOn = new ArrayList<com.google.gerrit.common.data.ChangeInfo>();
		List<com.google.gerrit.common.data.ChangeInfo> neededBy = new ArrayList<com.google.gerrit.common.data.ChangeInfo>();
		Branch.NameKey branchKey = getBranchKey(changeInfo29);

		RelatedChangesInfo relatedChangesInfo = getRelatedChanges(reviewId, changeInfo29.getCurrentRevision(), monitor);
		List<RelatedChangeAndCommitInfo> listCommitInfo = relatedChangesInfo.getCommitInfo();
		boolean needed = true;
		for (RelatedChangeAndCommitInfo relatedChangeAndCommitInfo : listCommitInfo) {
			if (relatedChangeAndCommitInfo.getCommitInfo()
					.getCommit()
					.equalsIgnoreCase(changeInfo29.getCurrentRevision())) {
				needed = false;
			} else {
				if (relatedChangeAndCommitInfo.getChangeNumber() > 0) {
					com.google.gerrit.common.data.ChangeInfo googleChangeInfo = convertToGoogleChangeInfo(
							relatedChangeAndCommitInfo, accountInfo, branchKey);
					if (needed) {
						neededBy.add(googleChangeInfo);
					} else {
						dependsOn.add(googleChangeInfo);
					}
				}
			}
		}
		changeDetail.setNeededBy(neededBy);
		changeDetail.setDependsOn(dependsOn);
	}

	/**
	 * Returns the details for a specific review.
	 */
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		final Change.Id changeId = new Change.Id(reviewId);
		ChangeDetailX changeDetail = null;

		if (isVersion29OrLater(monitor)) {
			changeDetail = getChangeDetail29(reviewId, monitor);
		} else {
			changeDetail = execute(monitor, new Operation<ChangeDetailX>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeDetailService(monitor).changeDetailX(changeId, this);
				}
			});
			if (isVersion26OrLater(monitor)) {
				if (changeDetail.getApprovals() == null) {
					ChangeInfo changeInfo = getChangeInfo(reviewId, monitor);
					changeDetail.setApprovals(changeInfo.convertToApprovalDetails());
					changeDetail.setApprovalTypes(changeInfo.convertToApprovalTypes());
				}
				List<ReviewerInfo> reviewers = listReviewers(reviewId, monitor);
				if (!hasAllReviewers(changeDetail.getAccounts(), reviewers)) {
					merge(changeDetail.getAccounts(), reviewers);
				}
			} else if (changeDetail.getApprovalTypes() == null && getGerritConfig() != null) {
				changeDetail.convertSubmitRecordsToApprovalTypes(getGerritConfig().getApprovalTypes());
			}
			changeDetail.setDateCreated(changeDetail.getChange().getCreatedOn());
			changeDetail.setLastModified(changeDetail.getChange().getLastUpdatedOn());

		}
		return changeDetail;
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

	private org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo getAccountInfo(String account,
			IProgressMonitor monitor) throws GerritException, URIException {
		if (GerritSystemAccount.GERRIT_SYSTEM_NAME.equals(account)) {
			return GerritSystemAccount.GERRIT_SYSTEM;
		}
		String st = URIUtil.encodeQuery(account);
		final String uri = "/accounts/" + st; //$NON-NLS-1$
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = executeGetRestRequest(uri,
				org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo.class, monitor);
		return accountInfo;
	}

	private void setActions29(int reviewId, ChangeDetailX changeDetail, IProgressMonitor monitor) {
		ChangeInfo29 changeInfo29 = null;
		try {
			changeInfo29 = executeGetRestRequest("/changes/" + Integer.toString(reviewId) //$NON-NLS-1$
					+ "/?o=CURRENT_REVISION&o=CURRENT_ACTIONS", ChangeInfo29.class, monitor); //$NON-NLS-1$
		} catch (GerritException e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"ChangeDetailX GerritException running rest query", e)); //$NON-NLS-1$
		}

		if (changeInfo29 != null) {
			if (changeInfo29.getRevisions() != null) {
				setRevisionActions29(changeInfo29, changeDetail);
			}

			if (changeInfo29.getActions() != null) {
				setGlobalActions29(changeInfo29, changeDetail);
			}
		}
	}

	private void setRevisionActions29(ChangeInfo29 changeInfo29, ChangeDetailX changeDetail) {
		for (Entry<String, RevisionInfo> mapRevisions : changeInfo29.getRevisions().entrySet()) {
			if (mapRevisions.getValue().getActions() != null) {
				for (Entry<String, ActionInfo> mapActions : mapRevisions.getValue().getActions().entrySet()) {
					if (mapActions.getKey().equalsIgnoreCase("submit")) { //$NON-NLS-1$
						changeDetail.setCanSubmit(mapActions.getValue().getEnabled());
					} else if (mapActions.getKey().equalsIgnoreCase("rebase")) { //$NON-NLS-1$
						changeDetail.setCanRebase(mapActions.getValue().getEnabled());
					}
				}
			}
		}
	}

	private void setGlobalActions29(ChangeInfo29 changeInfo29, ChangeDetailX changeDetail) {
		for (Entry<String, ActionInfo> mapActions : changeInfo29.getActions().entrySet()) {
			if (mapActions.getKey().equalsIgnoreCase("abandon")) { //$NON-NLS-1$
				changeDetail.setCanAbandon(mapActions.getValue().getEnabled());
				changeDetail.setCanRestore(!mapActions.getValue().getEnabled());
			} else if (mapActions.getKey().equalsIgnoreCase("restore")) { //$NON-NLS-1$
				changeDetail.setCanAbandon(!mapActions.getValue().getEnabled());
				changeDetail.setCanRestore(mapActions.getValue().getEnabled());
			}
		}
	}

	private List<ReviewerInfo> listReviewers(final int reviewId, IProgressMonitor monitor) throws GerritException {
		final String uri = "/changes/" + reviewId + "/reviewers/"; //$NON-NLS-1$ //$NON-NLS-2$
		TypeToken<List<ReviewerInfo>> reviewersListType = new TypeToken<List<ReviewerInfo>>() {
		};
		return executeGetRestRequest(uri, reviewersListType.getType(), monitor);
	}

	private boolean hasAllReviewers(AccountInfoCache accounts, List<ReviewerInfo> reviewers) {
		for (ReviewerInfo reviewer : reviewers) {
			AccountInfo cachedAccount = accounts.get(new Account.Id(reviewer.getId()));
			if (cachedAccount == null || isAnonymous(cachedAccount)) {
				return false;
			}
		}
		return true;
	}

	private boolean isAnonymous(AccountInfo accountInfo) {
		return accountInfo.getFullName() == null && accountInfo.getPreferredEmail() == null;
	}

	private void merge(AccountInfoCache accounts, List<ReviewerInfo> reviewers) {
		Set<com.google.gerrit.common.data.AccountInfo> accountInfos = new HashSet<com.google.gerrit.common.data.AccountInfo>(
				reviewers.size());
		for (ReviewerInfo reviewer : reviewers) {
			accountInfos.add(reviewer.toAccountInfo());
		}
		AccountInfoCache accountInfoCache = new AccountInfoCache(accountInfos);
		accounts.merge(accountInfoCache);
	}

	public ChangeInfo getChangeInfo(final int reviewId, IProgressMonitor monitor) throws GerritException {
		final String uri = "/changes/" + reviewId + "/revisions/current/review"; //$NON-NLS-1$ //$NON-NLS-2$
		return executeGetRestRequest(uri, ChangeInfo.class, monitor);
	}

	public void loadPatchSetContent(PatchSetContent patchSetContent, IProgressMonitor monitor) throws GerritException {
		Id baseId = (patchSetContent.getBase() != null) ? patchSetContent.getBase().getId() : null;
		Id targetId = patchSetContent.getTarget().getId();
		if (patchSetContent.getTargetDetail() == null) {
			PatchSetDetail targetDetail = getPatchSetDetail(baseId, targetId, monitor);
			patchSetContent.setTargetDetail(targetDetail);
		}
		for (Patch patch : patchSetContent.getTargetDetail().getPatches()) {
			PatchScriptX patchScript = getPatchScript(patch.getKey(), baseId, targetId, monitor);
			if (patchScript != null) {
				patchSetContent.putPatchScriptByPatchKey(patch.getKey(), patchScript);
			}
		}
	}

	public GerritConfigX getGerritConfig() {
		return config == null ? null : config.getGerritConfig();
	}

	public GerritConfiguration getConfiguration() {
		return config;
	}

	public GerritSystemInfo getInfo(IProgressMonitor monitor) throws GerritException {
		Version version = getCachedVersion(monitor);
		List<ContributorAgreement> contributorAgreements = null;
		Account account = null;
		if (!isAnonymous()) {
			account = getAccount(monitor);
		} else {
			// XXX should run some more meaningful validation as anonymous, for now any call is good to validate the URL etc.
			executeQuery(monitor, "status:open"); //$NON-NLS-1$
		}
		refreshConfigOnce(monitor);
		return new GerritSystemInfo(version, contributorAgreements, account);
	}

	PatchScriptX getPatchScript(final Patch.Key key, final PatchSet.Id leftId, final PatchSet.Id rightId,
			final IProgressMonitor monitor) throws GerritException {
		final AccountDiffPreference diffPrefs = createAccountDiffPreference();
		final PatchScriptX patchScript = execute(monitor, new Operation<PatchScriptX>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService(monitor).patchScriptX(key, leftId, rightId, diffPrefs, this);
			}
		});
		if (patchScript.isBinary()) {
			fetchLeftBinaryContent(patchScript, key, leftId, monitor);
			fetchRightBinaryContent(patchScript, key, rightId, monitor);
		}
		return patchScript;
	}

	protected void fetchLeftBinaryContent(final PatchScriptX patchScript, final Patch.Key key,
			final PatchSet.Id leftId, final IProgressMonitor monitor) throws GerritException {
		if (patchScript.getChangeType() != ChangeType.ADDED) {
			byte[] binaryContent = fetchBinaryContent(getUrlForPatchSetOrBase(key, leftId), monitor);
			patchScript.setBinaryA(binaryContent);
		}
	}

	protected void fetchRightBinaryContent(final PatchScriptX patchScript, final Patch.Key key,
			final PatchSet.Id rightId, final IProgressMonitor monitor) throws GerritException {
		if (patchScript.getChangeType() != ChangeType.DELETED) {
			byte[] binaryContent = fetchBinaryContent(getUrlForPatchSet(key, rightId), monitor);
			patchScript.setBinaryB(binaryContent);
		}
	}

	protected String getUrlForPatchSetOrBase(final Patch.Key key, final PatchSet.Id id) throws GerritException {
		if (id == null) {
			return getUrlForBase(key);
		} else {
			return getUrlForPatchSet(key, id);
		}
	}

	private String getUrlForBase(final Patch.Key key) throws GerritException {
		return encode(key.toString() + "^1"); //$NON-NLS-1$
	}

	protected String getUrlForPatchSet(final Patch.Key key, final PatchSet.Id id) throws GerritException {
		return encode(id + "," + key.getFileName() + "^0"); //$NON-NLS-1$//$NON-NLS-2$
	}

	protected byte[] fetchBinaryContent(String url, IProgressMonitor monitor) throws GerritException {
		final TypeToken<Byte[]> byteArrayType = new TypeToken<Byte[]>() {
		};
		byte[] bin = executeGetRestRequest("/cat/" + url, byteArrayType.getType(), monitor); //$NON-NLS-1$
		if (isZippedContent(bin)) {
			return unzip(bin);
		} else {
			return bin;
		}
	}

	/**
	 * Checks for the 4 byte header that identifies a ZIP file
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static boolean isZippedContent(byte[] bin) {
		return bin != null && bin.length > 4 && bin[0] == 'P' && bin[1] == 'K' && bin[2] == 3 && bin[3] == 4;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static byte[] unzip(byte[] zip) throws GerritException {
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zip));
		try {
			zis.getNextEntry(); // expecting a single entry
			return IOUtils.toByteArray(zis);
		} catch (IOException e) {
			throw new GerritException(e);
		} finally {
			IOUtils.closeQuietly(zis);
		}
	}

	private AccountDiffPreference createAccountDiffPreference() {
		AccountDiffPreference diffPrefs = new AccountDiffPreference((Account.Id) null);
		diffPrefs.setLineLength(Integer.MAX_VALUE);
		diffPrefs.setTabSize(4);
		diffPrefs.setContext(AccountDiffPreference.WHOLE_FILE_CONTEXT);
		diffPrefs.setIgnoreWhitespace(Whitespace.IGNORE_NONE);
		diffPrefs.setIntralineDifference(false);
		return diffPrefs;
	}

	private PatchSetDetail getPatchSetDetail(final PatchSet.Id idBase, final PatchSet.Id idTarget,
			IProgressMonitor monitor) throws GerritException {
		PatchSetDetail patchSetDetail = null;
		try {
			// Gerrit 2.4+
			patchSetDetail = execute(monitor, new Operation<PatchSetDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeDetailService(monitor).patchSetDetail2(idBase, idTarget, createAccountDiffPreference(),
							this);
				}
			});
		} catch (GerritException e) {
			try {
				// fallback for Gerrit < 2.1.7
				if (isNoSuchServiceError(e)) {
					patchSetDetail = execute(monitor, new Operation<PatchSetDetail>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getChangeDetailService(monitor).patchSetDetail(idTarget, this);
						}
					});
				} else {
					throw e;
				}
			} catch (GerritException e2) {
				// fallback for Gerrit 2.1.7
				String message = e2.getMessage();
				if (message != null && message.contains("Error parsing request")) { //$NON-NLS-1$
					patchSetDetail = execute(monitor, new Operation<PatchSetDetail>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getChangeDetailService(monitor).patchSetDetail(idBase, idTarget,
									createAccountDiffPreference(), this);
						}
					});
				} else {
					throw e2;
				}
			}
		}
		return patchSetDetail;
	}

	boolean isNoSuchServiceError(GerritException e) {
		String message = e.getMessage();
		return message != null && message.contains("No such service method"); //$NON-NLS-1$
	}

	public PatchSetPublishDetailX getPatchSetPublishDetail29(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		PatchSetPublishDetailX publishDetail = null;
		publishDetail = new PatchSetPublishDetailX();
		ChangeInfo changeInfo = getChangeInfo(id.getParentKey().get(), monitor);

		List<AccountInfo> listAccountInfo = new ArrayList<AccountInfo>();

		AccountInfo accountInfo = convertAuthorFrom29ToAccountInfo(changeInfo);
		listAccountInfo.add(accountInfo);
		AccountInfoCache accountInfoCache = new AccountInfoCache(listAccountInfo);
		publishDetail.setAccounts(accountInfoCache);

		Branch.NameKey branchKey = getBranchKey(changeInfo);
		Change currentChange = createChange(changeInfo.getChangeId(), id.getParentKey().get(), accountInfo, branchKey);
		currentChange.setStatus(changeInfo.getStatus());

		publishDetail.setChange(currentChange);

		PatchSetInfo patchSetInfo = getPatchSetInfo(changeInfo.getCurrentPatchSetId(), changeInfo.getSubject());
		publishDetail.setPatchSetInfo(patchSetInfo);
		publishDetail.setLabels(changeInfo.convertToPermissionLabels());

		if (publishDetail.getGiven() == null) {
			publishDetail.setGiven(changeInfo.convertToPatchSetApprovals(id, getAccount(monitor)));
		}
		return publishDetail;
	}

	public PatchSetPublishDetailX getPatchSetPublishDetail(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		PatchSetPublishDetailX publishDetail = null;
		if (isVersion29OrLater(monitor)) {
			publishDetail = getPatchSetPublishDetail29(id, monitor);
		} else {
			publishDetail = execute(monitor, new Operation<PatchSetPublishDetailX>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeDetailService(monitor).patchSetPublishDetailX(id, this);
				}
			});
			if (publishDetail.getLabels() == null && isVersion26OrLater(monitor)) {
				ChangeInfo changeInfo = getChangeInfo(id.getParentKey().get(), monitor);
				publishDetail.setLabels(changeInfo.convertToPermissionLabels());
				if (publishDetail.getGiven() == null) {
					publishDetail.setGiven(changeInfo.convertToPatchSetApprovals(id, getAccount(monitor)));
				}
			}

		}
		return publishDetail;
	}

	public GerritChange getChange(String reviewId, IProgressMonitor monitor) throws GerritException {
		GerritChange gerritChange = new GerritChange();
		int id;
		try {
			id = id(reviewId);
		} catch (GerritException e) {
			List<GerritQueryResult> result = executeQuery(monitor, reviewId);
			if (result.size() == 1) {
				id = result.get(0).getNumber();
			} else {
				throw e;
			}
		}
		ChangeDetailX changeDetail = getChangeDetail(id, monitor);
		List<PatchSetDetail> patchSets = new ArrayList<PatchSetDetail>(changeDetail.getPatchSets().size());
		Map<PatchSet.Id, PatchSetPublishDetailX> patchSetPublishDetailByPatchSetId = new HashMap<PatchSet.Id, PatchSetPublishDetailX>();
		for (PatchSet patchSet : changeDetail.getPatchSets()) {
			try {
				PatchSetDetail patchSetDetail = getPatchSetDetail(null, patchSet.getId(), monitor);
				patchSets.add(patchSetDetail);
				if (!isAnonymous()) {
					PatchSetPublishDetailX patchSetPublishDetail = getPatchSetPublishDetail(patchSet.getId(), monitor);

					if (isVersion29OrLater(monitor)) {
						//To add the AccountId if available
						patchSetPublishDetail.setPatchSetInfo(setAccountPatchSetInfo(patchSetDetail.getInfo(), monitor));
					}
					patchSetPublishDetailByPatchSetId.put(patchSet.getId(), patchSetPublishDetail);
					changeDetail.setCurrentPatchSetDetail(patchSetDetail);
				}
			} catch (GerritException e) {
				handleMissingPatchSet(
						NLS.bind("Patch Set {0} items for Review {1}", patchSet.getPatchSetId(), reviewId), e); //$NON-NLS-1$
			}
		}
		gerritChange.setChangeDetail(changeDetail);
		gerritChange.setPatchSets(patchSets);
		gerritChange.setPatchSetPublishDetailByPatchSetId(patchSetPublishDetailByPatchSetId);

		return gerritChange;
	}

	private PatchSetInfo setAccountPatchSetInfo(PatchSetInfo patchSetInfo, IProgressMonitor monitor) {
		if (patchSetInfo.getAuthor().getAccount() == null) {
			patchSetInfo.setAuthor(setUserIdentity(patchSetInfo.getAuthor().getName(), patchSetInfo.getAuthor(),
					"Author", monitor)); //$NON-NLS-1$
		}
		if (patchSetInfo.getCommitter().getAccount() == null) {
			patchSetInfo.setCommitter(setUserIdentity(patchSetInfo.getCommitter().getName(),
					patchSetInfo.getCommitter(), "Committer", monitor)); //$NON-NLS-1$
		}
		return patchSetInfo;
	}

	private UserIdentity setUserIdentity(String name, UserIdentity userIdentity, String user, IProgressMonitor monitor) {
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = null;
		try {
			accountInfo = getAccountInfo(name, monitor);
			Account.Id accountId = new Account.Id(accountInfo.getId());
			userIdentity.setAccount(accountId);
		} catch (GerritException gerritException) {
			if (gerritException.getMessage().indexOf(HttpStatus.SC_NOT_FOUND) != 0) {
				StatusHandler.log(new Status(IStatus.WARNING, GerritCorePlugin.PLUGIN_ID, NLS.bind(
						"GerritException {0} not found", user), gerritException)); //$NON-NLS-1$
			}
		} catch (URIException uriException) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					NLS.bind("{0} URIException: ", user), uriException)); //$NON-NLS-1$
		}
		return userIdentity;
	}

	private void handleMissingPatchSet(String desc, GerritException e) {
		GerritCorePlugin.logWarning(
				NLS.bind("Couldn't load {0}. (Perhaps the Patch Set has been removed from repository?)", desc), e); //$NON-NLS-1$
	}

	public int id(String id) throws GerritException {
		if (id == null) {
			throw new GerritException("Invalid ID (null)"); //$NON-NLS-1$
		}
		try {
			return Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new GerritException(NLS.bind("Invalid ID (''{0}'')", id)); //$NON-NLS-1$
		}
	}

	public void publishComments(String reviewId, int patchSetId, final String message,
			final Set<ApprovalCategoryValue.Id> approvals, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		if (hasJsonRpcApi(monitor)) {
			execute(monitor, new Operation<VoidResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getPatchDetailService(monitor).publishComments(id, message, approvals, this);
				}
			});
		} else {
			ReviewInput reviewInput = new ReviewInput(message);
			Map<String, CommentInfo[]> drafts = listDrafts(id, monitor);
			Map<String, CommentInput[]> comments = convert(drafts);
			if (!comments.isEmpty()) {
				reviewInput.setComments(comments);
			}
			reviewInput.setApprovals(approvals);
			final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/review"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			executePostRestRequest(uri, reviewInput, ReviewInfo.class, new ErrorHandler() {

				@Override
				public void handleError(HttpMethodBase method) throws GerritException {
					if (method.getStatusCode() == HttpURLConnection.HTTP_FORBIDDEN) {
						String msg = getResponseBodyAsString(method);
						if (msg.startsWith("Applying label") && msg.endsWith("is restricted")) { //$NON-NLS-1$ //$NON-NLS-2$
							throw new GerritException(msg);
						}
					}
				}

				private String getResponseBodyAsString(HttpMethodBase method) {
					try {
						String msg = method.getResponseBodyAsString();
						return msg.trim();
					} catch (IOException e) {
						// ignore
					}
					return null;
				}
			}, monitor);
		}
	}

	private Map<String, CommentInfo[]> listDrafts(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		String uri = "/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/drafts/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		TypeToken<Map<String, CommentInfo[]>> resultType = new TypeToken<Map<String, CommentInfo[]>>() {
		};

		return executeGetRestRequest(uri, resultType.getType(), monitor);
	}

	private Map<String, CommentInput[]> convert(Map<String, CommentInfo[]> commentInfos) {
		if (commentInfos == null || commentInfos.isEmpty()) {
			return Collections.<String, CommentInput[]> emptyMap();
		}
		Map<String, CommentInput[]> commentInputs = new HashMap<String, CommentInput[]>(commentInfos.size());
		Set<Entry<String, CommentInfo[]>> entrySet = commentInfos.entrySet();
		for (Entry<String, CommentInfo[]> entry : entrySet) {
			CommentInfo[] infos = entry.getValue();
			List<CommentInput> inputs = new ArrayList<CommentInput>(infos.length);
			for (CommentInfo info : infos) {
				inputs.add(new CommentInput(info));
			}
			commentInputs.put(entry.getKey(), inputs.toArray(new CommentInput[inputs.size()]));
		}
		return commentInputs;
	}

	public ReviewerResult addReviewers(String reviewId, final List<String> reviewers, IProgressMonitor monitor)
			throws GerritException {
		Assert.isLegal(reviewers != null, "reviewers cannot be null"); //$NON-NLS-1$
		final Change.Id id = new Change.Id(id(reviewId));
		if (hasJsonRpcApi(monitor)) {
			try {
				return execute(monitor, new Operation<ReviewerResult>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getPatchDetailService(monitor).addReviewers(id, reviewers, this);
					}
				});
			} catch (GerritException e) {
				// Gerrit 2.2
				String message = e.getMessage();
				if (message != null && message.contains("Error parsing request")) { //$NON-NLS-1$
					return execute(monitor, new Operation<ReviewerResult>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getPatchDetailService(monitor).addReviewers(id, reviewers, false, this);
						}
					});
				} else {
					throw e;
				}
			}
		} else {
			final String uri;
			uri = "/a/changes/" + id.get() + "/reviewers"; //$NON-NLS-1$ //$NON-NLS-2$

			Set<ReviewerInfo> reviewerInfos = new HashSet<ReviewerInfo>(reviewers.size());
			ReviewerResult reviewerResult = new ReviewerResult();
			for (final String reviewerId : reviewers) {
				try {
					AddReviewerResult addReviewerResult = executePostRestRequest(uri, new ReviewerInput(reviewerId),
							AddReviewerResult.class, null /*no error handler*/, monitor);
					reviewerInfos.addAll(addReviewerResult.getReviewers());
				} catch (GerritHttpException e) {
					if (e.getResponseCode() == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
						reviewerResult.addError(new ReviewerResult.Error(null /* no type*/, reviewerId));
					}
				}
			}

			ChangeDetail changeDetail = getChangeDetail(id.get(), monitor);

			List<ApprovalDetail> approvalDetails = new ArrayList<ApprovalDetail>(reviewerInfos.size());
			for (ReviewerInfo reviewerInfo : reviewerInfos) {
				approvalDetails.add(reviewerInfo.toApprovalDetail(changeDetail.getCurrentPatchSet()));
			}
			changeDetail.setApprovals(approvalDetails);
			reviewerResult.setChange(changeDetail);
			return reviewerResult;
		}
	}

	/**
	 * Returns the latest 25 reviews.
	 */
	public List<GerritQueryResult> queryAllReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "status:open"); //$NON-NLS-1$
	}

	/**
	 * Returns the latest 25 reviews for the given project.
	 */
	public List<GerritQueryResult> queryByProject(IProgressMonitor monitor, final String project)
			throws GerritException {
		return executeQuery(monitor, "status:open project:" + project); //$NON-NLS-1$
	}

	/**
	 * Returns changes associated with the logged in user. This includes all open, closed and review requests for the
	 * user. On Gerrit 2.4 and earlier closed reviews are not included.
	 */
	public List<GerritQueryResult> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		if (hasJsonRpcApi(monitor) && !restQueryAPIEnabled) {
			try {
				final Account account = getAccount(monitor);
				AccountDashboardInfo ad = execute(monitor, new Operation<AccountDashboardInfo>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getChangeListService(monitor).forAccount(account.getId(), this);
					}
				});

				List<com.google.gerrit.common.data.ChangeInfo> allMyChanges = ad.getByOwner();
				allMyChanges.addAll(ad.getForReview());
				allMyChanges.addAll(ad.getClosed());
				return convert(allMyChanges);
			} catch (GerritException e) {
				if (isNoSuchServiceError(e)) {
					restQueryAPIEnabled = true;
				} else {
					throw e;
				}
			}
		}
		// the "self" alias is only supported in Gerrit 2.5 and later
		return executeQueryRest(monitor, "owner:self OR reviewer:self", GET_LABELS_OPTION); //$NON-NLS-1$
	}

	private boolean hasJsonRpcApi(IProgressMonitor monitor) throws GerritException {
		return !isVersion26OrLater(monitor);
	}

	private boolean isVersion26OrLater(IProgressMonitor monitor) throws GerritException {
		Version version = getCachedVersion(monitor);
		return GerritVersion.isVersion26OrLater(version);
	}

	private boolean isVersion27OrLater(IProgressMonitor monitor) throws GerritException {
		Version version = getCachedVersion(monitor);
		return GerritVersion.isVersion27OrLater(version);
	}

	private boolean isVersion28OrLater(IProgressMonitor monitor) throws GerritException {
		Version version = getCachedVersion(monitor);
		return GerritVersion.isVersion28OrLater(version);
	}

	public boolean isVersion29OrLater(IProgressMonitor monitor) throws GerritException {
		Version version = getCachedVersion(monitor);
		return GerritVersion.isVersion29OrLater(version);
	}

	/**
	 * Returns watched changes of the currently logged in user
	 */
	public List<GerritQueryResult> queryWatchedReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "is:watched status:open"); //$NON-NLS-1$
	}

	/**
	 * Retrieves the root URL for the Gerrit instance and attempts to parse the configuration from the JavaScript
	 * portion of the page.
	 */
	private GerritConfigX refreshGerritConfig(final IProgressMonitor monitor) throws GerritException {
		try {
			GerritConfigX gerritConfig = client.execute(new Request<GerritConfigX>() {
				@Override
				public HttpMethodBase createMethod() throws IOException {
					return new GetMethod(client.getUrl() + "/"); //$NON-NLS-1$
				}

				@Override
				public GerritConfigX process(HttpMethodBase method) throws IOException {
					InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
					try {
						GerritHtmlProcessor processor = new GerritHtmlProcessor();
						processor.parse(in, method.getResponseCharSet());
						return processor.getConfig();
					} finally {
						in.close();
					}
				}
			}, monitor);

			if (gerritConfig == null) {
				throw new GerritException("Failed to obtain Gerrit configuration"); //$NON-NLS-1$
			}
			return gerritConfig;
		} catch (UnknownHostException cause) {
			GerritException e = new GerritException("Unknown host: " + cause.getMessage()); //$NON-NLS-1$
			e.initCause(cause);
			throw e;
		} catch (IOException cause) {
			GerritException e = new GerritException(cause.getMessage());
			e.initCause(cause);
			throw e;
		}
	}

	public GerritConfiguration refreshConfig(IProgressMonitor monitor) throws GerritException {
		configRefreshed = true;
		GerritConfigX gerritConfig = refreshGerritConfig(monitor);
		List<Project> projects = getVisibleProjects(monitor, gerritConfig);
		Account account = null;
		try {
			account = getAccount(monitor);
		} catch (GerritException e) {
			if (!isNotSignedInException(e)) {
				throw e;
			}
		}
		config = new GerritConfiguration(gerritConfig, projects, account);
		configurationChanged(config);
		return config;
	}

	public GerritConfiguration refreshConfigOnce(IProgressMonitor monitor) throws GerritException {
		if (!configRefreshed && config == null) {
			try {
				refreshConfig(monitor);
			} catch (GerritException e) {
				// don't fail validation in case config parsing fails
			}
		}
		return getConfiguration();
	}

	public ChangeDetail rebase(String reviewId, int patchSetId, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		if (isVersion29OrLater(monitor)) {
			return rebaseRest(id, monitor);
		} else {
			return execute(monitor, new Operation<ChangeDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeManageService(monitor).rebaseChange(id, this);
				}
			});
		}
	}

	private ChangeDetail rebaseRest(final PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/rebase"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		executePostRestRequest(uri, new ChangeInfo29(), ChangeInfo29.class, new ErrorHandler() {
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

	public ChangeDetail restore(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		if (hasJsonRpcApi(monitor)) {
			return execute(monitor, new Operation<ChangeDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeManageService(monitor).restoreChange(id, message, this);
				}
			});
		} else {
			final String uri = "/a/changes/" + id.getParentKey().get() + "/restore"; //$NON-NLS-1$ //$NON-NLS-2$
			try {
				executePostRestRequest(uri, new RestoreInput(message), ChangeInfo.class, null/*no error handler*/,
						monitor);
			} catch (GerritHttpException e) {
				if (e.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
					throw new GerritException("Not Found", e); //$NON-NLS-1$
				}
			}
			return getChangeDetail(id.getParentKey().get(), monitor);
		}
	}

	public ChangeDetail submit(String reviewId, int patchSetId, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		if (hasJsonRpcApi(monitor)) {
			return execute(monitor, new Operation<ChangeDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeManageService(monitor).submit(id, this);
				}
			});
		} else {
			return submitRest(id, monitor);
		}
	}

	private ChangeDetail submitRest(PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/submit"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		executePostRestRequest(uri, new SubmitInput(true), SubmitInfo.class, new ErrorHandler() {
			@Override
			public void handleError(HttpMethodBase method) throws GerritException {
				String errorMsg = getResponseBodyAsString(method);
				if (isNotPermitted(method, errorMsg) || isConflict(method)) {
					throw new GerritException(NLS.bind("Cannot submit change: {0}", errorMsg)); //$NON-NLS-1$
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
		return getChangeDetail(id.getParentKey().get(), monitor);
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server.
	 *
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQuery(IProgressMonitor monitor, final String queryString)
			throws GerritException {
		return executeQuery(monitor, queryString, GET_LABELS_OPTION);
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server with the possibility of adding options
	 * to the query.
	 *
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @param optionString
	 *            Query options ("&o=" parameter). Only applicable for the REST API, ignored otherwise. May be null.
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQuery(IProgressMonitor monitor, final String queryString, String optionString)
			throws GerritException {
		if (hasJsonRpcApi(monitor) && !restQueryAPIEnabled) {
			try {
				SingleListChangeInfo sl = execute(monitor, new Operation<SingleListChangeInfo>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getChangeListService(monitor).allQueryNext(queryString, "z", -1, this); //$NON-NLS-1$
					}
				});
				return convert(sl.getChanges());
			} catch (GerritException e) {
				if (isNoSuchServiceError(e)) {
					restQueryAPIEnabled = true;
				} else {
					throw e;
				}
			}
		}

		return executeQueryRest(monitor, queryString, optionString);
	}

	private List<GerritQueryResult> convert(List<com.google.gerrit.common.data.ChangeInfo> changes) {
		List<GerritQueryResult> results = new ArrayList<GerritQueryResult>(changes.size());
		for (com.google.gerrit.common.data.ChangeInfo changeInfo : changes) {
			GerritQueryResult result = new GerritQueryResult(changeInfo);
			results.add(result);
		}
		return results;
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server. Uses the gerrit REST API.
	 *
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQueryRest(IProgressMonitor monitor, final String queryString)
			throws GerritException {
		return executeQueryRest(monitor, queryString, null);
	}

	/**
	 * Sends a query for the changes visible to the caller to the gerrit server with the possibility of adding options
	 * to the query. Uses the gerrit REST API.
	 *
	 * @param monitor
	 *            A progress monitor
	 * @param queryString
	 *            The specific gerrit change query
	 * @param optionString
	 *            Query options ("&o=" parameter). May be null or empty.
	 * @return a list of GerritQueryResults built from the parsed query result (ChangeInfo:s)
	 * @throws GerritException
	 */
	public List<GerritQueryResult> executeQueryRest(IProgressMonitor monitor, final String queryString,
			String optionString) throws GerritException {
		String uri = "/changes/?q=" + encode(queryString); //$NON-NLS-1$
		if (StringUtils.isNotBlank(optionString)) {
			uri += "&o=" + encode(optionString); //$NON-NLS-1$
		}
		TypeToken<List<GerritQueryResult>> queryResultListType = new TypeToken<List<GerritQueryResult>>() {
		};
		return executeGetRestRequest(uri, queryResultListType.getType(), monitor);
	}

	/**
	 * Returns the (possibly cached) account for this client.
	 */
	public Account getAccount(IProgressMonitor monitor) throws GerritException {

		synchronized (this) {
			if (myAcount != null) {
				return myAcount;
			}
		}
		Account account = executeAccount(monitor);

		synchronized (this) {
			myAcount = account;
		}
		return myAcount;
	}

	private Account executeAccount(IProgressMonitor monitor) throws GerritException {
		if (isVersion29OrLater(monitor)) {
			return getAccount29(monitor);
		} else {
			return execute(monitor, new Operation<Account>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getAccountService(monitor).myAccount(this);
				}
			});
		}
	}

	private Account getAccount29(IProgressMonitor monitor) throws GerritException {
		if (isAnonymous()) {
			throw new GerritException(NOT_SIGNED_IN, -32603);
		}
		String query = "/accounts/self"; //$NON-NLS-1$/
		org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo accountInfo = executeGetRestRequest(query,
				org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo.class, monitor);

		Account account = new Account(new Account.Id(accountInfo.getId()));
		account.setFullName(accountInfo.getName());
		account.setUserName(accountInfo.getUsername());
		account.setPreferredEmail(accountInfo.getEmail());
		return account;
	}

	private AccountService getAccountService(IProgressMonitor monitor) {
		return getService(AccountService.class, monitor);
	}

	private ChangeDetailService getChangeDetailService(IProgressMonitor monitor) {
		return getService(ChangeDetailService.class, monitor);
	}

	private ChangeListService getChangeListService(IProgressMonitor monitor) {
		return getService(ChangeListService.class, monitor);
	}

	private ChangeManageService getChangeManageService(IProgressMonitor monitor) {
		return getService(ChangeManageService.class, monitor);
	}

	private PatchDetailService getPatchDetailService(IProgressMonitor monitor) {
		return getService(PatchDetailService.class, monitor);
	}

	private List<Project> getVisibleProjects(IProgressMonitor monitor, GerritConfig gerritConfig)
			throws GerritException {
		List<Project> result = new ArrayList<Project>();
		try {
			List<ProjectDetailX> projectDetails = execute(monitor, new Operation<List<ProjectDetailX>>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getProjectAdminService(monitor).visibleProjectDetails(this);
				}
			});
			for (ProjectDetailX projectDetail : projectDetails) {
				if (!GerritUtil.isPermissionOnlyProject(projectDetail, gerritConfig)) {
					result.add(projectDetail.project);
				}
			}
		} catch (GerritException e) {
			if (isNoSuchServiceError(e)) {
				if (isVersion27OrLater(monitor)) {
					Map<String, ProjectInfo> projects = listProjects(monitor);
					for (String projectName : projects.keySet()) {
						result.add(new Project(new NameKey(projectName)));
					}
				} else {
					// Gerrit <= 2.2.1
					List<Project> projects = execute(monitor, new Operation<List<Project>>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getProjectAdminService(monitor).visibleProjects(this);
						}
					});
					for (Project project : projects) {
						ProjectDetailX projectDetail = new ProjectDetailX();
						projectDetail.setProject(project);
						if (!GerritUtil.isPermissionOnlyProject(projectDetail, gerritConfig)) {
							result.add(project);
						}
					}
				}
			} else {
				throw e;
			}
		}
		Collections.sort(result, new ProjectByNameComparator());
		return result;
	}

	private Map<String, ProjectInfo> listProjects(IProgressMonitor monitor) throws GerritException {
		final String uri = "/projects/"; //$NON-NLS-1$
		TypeToken<Map<String, ProjectInfo>> resultType = new TypeToken<Map<String, ProjectInfo>>() {
		};
		return executeGetRestRequest(uri, resultType.getType(), monitor);
	}

	private ProjectAdminService getProjectAdminService(IProgressMonitor monitor) {
		return getService(ProjectAdminService.class, monitor);
	}

	public boolean isAnonymous() {
		return client.isAnonymous();
	}

	protected void configurationChanged(GerritConfiguration config) {
	}

	protected void authStateChanged(GerritAuthenticationState config) {
	}

	protected <T> T execute(IProgressMonitor monitor, Operation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
			try {
				return executeOnce(monitor, operation);
			} catch (GerritException e) {
				if (isAuthenticationException(e)) {
					operation.reset();
					return executeOnce(monitor, operation);
				}
				throw e;
			}
		} finally {
			GerritRequest.setCurrentRequest(null);
		}
	}

	private <T> T executePostRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> postRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	private <T> T executeGetRestRequest(final String url, final Type resultType, IProgressMonitor monitor)
			throws GerritException {
		return execute(monitor, new Operation<T>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> getRestRequest(url, resultType, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	private <T> T executePutRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> putRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	private <T> T executeDeleteRestRequest(final String url, final Object input, final Type resultType,
			final ErrorHandler handler, IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<T>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					setResult(client.<T> deleteRestRequest(url, input, resultType, handler, monitor));
				} catch (IOException e) {
					throw new GerritException(e);
				}
			}
		});
	}

	private <T> T executeOnce(IProgressMonitor monitor, Operation<T> operation) throws GerritException {
		operation.execute(monitor);
		if (operation.getException() instanceof GerritException) {
			throw (GerritException) operation.getException();
		} else if (operation.getException() instanceof OperationCanceledException) {
			throw (OperationCanceledException) operation.getException();
		} else if (operation.getException() instanceof RuntimeException) {
			throw (RuntimeException) operation.getException();
		} else if (operation.getException() != null) {
			GerritException e = new GerritException();
			e.initCause(operation.getException());
			throw e;
		}
		return operation.getResult();
	}

	protected synchronized <T extends RemoteJsonService> T getService(Class<T> clazz, IProgressMonitor monitor) {
		Version version = Version.emptyVersion;
		try {
			version = getCachedVersion(monitor);
		} catch (GerritException e) {
			// ignore, continue with emptyVersion
		}
		RemoteJsonService service = serviceByClass.get(clazz);
		if (service == null) {
			service = GerritService.create(clazz, client, version);
			serviceByClass.put(clazz, service);
		}
		return clazz.cast(service);
	}

	@Override
	public AbstractRemoteEmfFactoryProvider<IRepository, IReview> createFactoryProvider() {
		return new GerritRemoteFactoryProvider(this);
	}

	private Version getCachedVersion(IProgressMonitor monitor) throws GerritException {
		synchronized (this) {
			if (myVersion != null) {
				return myVersion;
			}
		}
		Version version = getVersion(monitor);

		synchronized (this) {
			myVersion = version;
		}
		return myVersion;
	}

	public Version getVersion(IProgressMonitor monitor) throws GerritException {
		return execute(monitor, new Operation<Version>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				try {
					Request<String> request = new Request<String>() {
						@Override
						public HttpMethodBase createMethod() throws IOException {
							return new GetMethod(client.getUrl() + "/tools/hooks/"); //$NON-NLS-1$
						}

						@Override
						public String process(HttpMethodBase method) throws IOException {
							String content = method.getResponseBodyAsString();
							Matcher matcher = GERRIT_VERSION_PATTERN.matcher(content);
							if (matcher.find()) {
								return matcher.group(1);
							}
							return null;
						}
					};
					String result = client.execute(request, false, monitor);
					Version version = GerritVersion.parseGerritVersion(result);
					onSuccess(version);
				} catch (Exception e) {
					onFailure(e);
				}
			}
		});
	}

	public String toReviewId(String id, IProgressMonitor monitor) throws GerritException {
		try {
			Integer.parseInt(id);
			return id;
		} catch (NumberFormatException e) {
			try {
				List<GerritQueryResult> results = executeQuery(monitor, id);
				if (results.size() != 1) {
					throw new GerritException(NLS.bind("{0} is not a valid review ID", id)); //$NON-NLS-1$
				}
				return Integer.toString(results.get(0).getNumber());
			} catch (GerritException e2) {
				throw new GerritException(NLS.bind("{0} is not a valid review ID", id), e2); //$NON-NLS-1$
			}
		}
	}

	private static String encode(String string) throws GerritException {
		try {
			return URLEncoder.encode(string, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new GerritException(e);
		}
	}

	public VoidResult setStarred(final String reviewId, final boolean starred, IProgressMonitor monitor)
			throws GerritException {
		final Change.Id id = new Change.Id(id(reviewId));
		final ToggleStarRequest req = new ToggleStarRequest();
		req.toggle(id, starred);
		if (isVersion28OrLater(monitor)) {
			final String uri = "/a/accounts/self/starred.changes/" + id.get(); //$NON-NLS-1$

			return execute(monitor, new Operation<VoidResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {

					if (starred) {
						executePutRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
					} else {
						executeDeleteRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
					}
				}
			});

		} else {
			return execute(monitor, new Operation<VoidResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeListService(monitor).toggleStars(req, this);
				}
			});
		}
	}

	private ErrorHandler createErrorHandler() {
		return new ErrorHandler() {
			@Override
			public void handleError(HttpMethodBase method) throws GerritException {
				throw new GerritException(method.getStatusLine().getReasonPhrase());
			}
		};
	}
}
