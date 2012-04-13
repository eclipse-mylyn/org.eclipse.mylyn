/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
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
 *********************************************************************/
package org.eclipse.mylyn.internal.gerrit.core.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectAdminService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILineRange;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.ChangeManageService;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.common.data.SystemInfoService;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ContributorAgreement;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.Project;
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
 */
public class GerritClient {

	private static final ReviewsFactory FACTORY = ReviewsFactory.eINSTANCE;

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
					&& "Invalid xsrfKey in request".equals(((GerritException) exception).getMessage());
		}
		return false;
	}

	public boolean isNotSignedInException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& "not signed in".equalsIgnoreCase(((GerritException) exception).getMessage());
		}
		return false;
	}

	// XXX belongs in GerritConnector
	public static GerritAuthenticationState authStateFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().fromJson(token, GerritAuthenticationState.class);
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
			return support.getGson().toJson(authState);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	private final GerritHttpClient client;

	private volatile GerritConfiguration config;

	private Account myAcount;

	private AccountDiffPreference myDiffPreference;

//	private GerritConfig createDefaultConfig() {
//		GerritConfig config = new GerritConfig();
//		List<ApprovalType> approvals = new ArrayList<ApprovalType>();
//
//		ApprovalCategory category = new ApprovalCategory(new ApprovalCategory.Id("VRIF"), "Verified");
//		category.setAbbreviatedName("V");
//		category.setPosition((short) 0);
//		List<ApprovalCategoryValue> values = new ArrayList<ApprovalCategoryValue>();
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) -1), "Fails"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 0), "No score"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 1), "Verified"));
//		approvals.add(new ApprovalType(category, values));
//
//		category = new ApprovalCategory(new ApprovalCategory.Id("CRVW"), "Code Review");
//		category.setAbbreviatedName("R");
//		category.setPosition((short) 1);
//		values = new ArrayList<ApprovalCategoryValue>();
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) -2),
//				"Do not submit"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) -1),
//				"I would prefer that you didn\u0027t submit this"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 0), "No score"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 1),
//				"Looks good to me, but someone else must approve"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 2),
//				"Looks good to me, approved"));
//		approvals.add(new ApprovalType(category, values));
//
//		category = new ApprovalCategory(new ApprovalCategory.Id("IPCL"), "IP Clean");
//		category.setAbbreviatedName("I");
//		category.setPosition((short) 2);
//		values = new ArrayList<ApprovalCategoryValue>();
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) -1),
//				"Unclean IP, do not check in"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 0), "No score"));
//		values.add(new ApprovalCategoryValue(new ApprovalCategoryValue.Id(category.getId(), (short) 1),
//				"IP review completed"));
//		approvals.add(new ApprovalType(category, values));
//
//		List<ApprovalType> actions = new ArrayList<ApprovalType>();
//
//		ApprovalTypes approvalTypes = new ApprovalTypes(approvals, actions);
//		config.setApprovalTypes(approvalTypes);
//		return config;
//	}

	private final Map<Class<? extends RemoteJsonService>, RemoteJsonService> serviceByClass;

	private volatile boolean configRefreshed;

	public GerritClient(AbstractWebLocation location) {
		this(location, null, null, null);
	}

	public GerritClient(AbstractWebLocation location, GerritConfiguration config, GerritAuthenticationState authState) {
		this(location, config, authState, null);
	}

	public GerritClient(AbstractWebLocation location, GerritConfiguration config, GerritAuthenticationState authState,
			String xsrfKey) {
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

	public PatchLineComment saveDraft(Patch.Key patchKey, String message, int line, short side, String parentUuid,
			IProgressMonitor monitor) throws GerritException {
		PatchLineComment.Key id = new PatchLineComment.Key(patchKey, null);
		final PatchLineComment comment = new PatchLineComment(id, line, getAccount(monitor).getId(), parentUuid);
		comment.setMessage(message);
		comment.setSide(side);
		return execute(monitor, new Operation<PatchLineComment>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().saveDraft(comment, this);
			}
		});
	}

	public ChangeDetail abondon(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService().abandonChange(id, message, this);
			}
		});
	}

	/**
	 * Returns the details for a specific review.
	 */
	public ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		final Change.Id id = new Change.Id(reviewId);
		return execute(monitor, new Operation<ChangeDetailX>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService().changeDetailX(id, this);
			}
		});
	}

	public GerritPatchSetContent getPatchSetContent(String reviewId, PatchSet base, PatchSetDetail target,
			IProgressMonitor monitor) throws GerritException {
		Map<Patch.Key, PatchScript> patchScriptByPatchKey = new HashMap<Patch.Key, PatchScript>();

		for (Patch patch : target.getPatches()) {
			PatchScript patchScript = getPatchScript(patch.getKey(), (base != null) ? base.getId() : null,
					target.getPatchSet().getId(), monitor);
			if (patchScript != null) {
				patchScriptByPatchKey.put(patch.getKey(), patchScript);
			}
		}

		return new GerritPatchSetContent(base, target, patchScriptByPatchKey);
	}

	public GerritConfigX getGerritConfig() {
		return config == null ? null : config.getGerritConfig();
	}

	public GerritConfiguration getConfiguration() {
		return config;
	}

	public AccountDiffPreference getDiffPreference(IProgressMonitor monitor) throws GerritException {
		synchronized (this) {
			if (myDiffPreference != null) {
				return myDiffPreference;
			}
		}
		AccountDiffPreference diffPreference = execute(monitor, new Operation<AccountDiffPreference>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getAccountService().myDiffPreferences(this);
			}
		});

		synchronized (this) {
			myDiffPreference = diffPreference;
		}
		return myDiffPreference;
	}

	public GerritSystemInfo getInfo(IProgressMonitor monitor) throws GerritException {
		List<ContributorAgreement> contributorAgreements = null;
		Account account = null;
		if (!isAnonymous()) {
//			contributorAgreements = execute(monitor, new GerritOperation<List<ContributorAgreement>>() {
//				@Override
//				public void execute(IProgressMonitor monitor) throws GerritException {
//					getSystemInfoService().contributorAgreements(this);
//				}
//			});
			account = execute(monitor, new Operation<Account>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getAccountService().myAccount(this);
				}
			});
		} else {
			// XXX should run some more meaningful validation as anonymous, for now any call is good to validate the URL etc.
			executeQuery(monitor, "status:open"); //$NON-NLS-1$
		}
		refreshConfigOnce(monitor);
		return new GerritSystemInfo(contributorAgreements, account);
	}

	public PatchScript getPatchScript(final Patch.Key key, final PatchSet.Id leftId, final PatchSet.Id rightId,
			IProgressMonitor monitor) throws GerritException {
		//final AccountDiffPreference diffPrefs = getDiffPreference(monitor);
		//final AccountDiffPreference diffPrefs = new AccountDiffPreference(getAccount(monitor).getId());
		final AccountDiffPreference diffPrefs = createAccountDiffPreference();
		return execute(monitor, new Operation<PatchScript>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().patchScript(key, leftId, rightId, diffPrefs, this);
			}
		});
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

	public PatchSetDetail getPatchSetDetail(final PatchSet.Id idBase, final PatchSet.Id idTarget,
			IProgressMonitor monitor) throws GerritException {
		PatchSetDetail result = null;
		try {
			// Gerrit 2.2
			result = execute(monitor, new Operation<PatchSetDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeDetailService().patchSetDetail2(idBase, idTarget, createAccountDiffPreference(), this);
				}
			});
		} catch (GerritException e) {
			try {
				// fallback for Gerrit < 2.1.7
				String message = e.getMessage();
				if (message != null && message.contains("No such service method")) { //$NON-NLS-1$
					result = execute(monitor, new Operation<PatchSetDetail>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getChangeDetailService().patchSetDetail(idTarget, this);
						}
					});
				} else {
					throw e;
				}
			} catch (GerritException e2) {
				// fallback for Gerrit 2.1.7
				String message = e2.getMessage();
				if (message != null && message.contains("Error parsing request")) { //$NON-NLS-1$
					result = execute(monitor, new Operation<PatchSetDetail>() {
						@Override
						public void execute(IProgressMonitor monitor) throws GerritException {
							getChangeDetailService().patchSetDetail(idBase, idTarget, createAccountDiffPreference(),
									this);
						}
					});
				} else {
					throw e2;
				}
			}
		}
		return result;
	}

	public PatchSetPublishDetailX getPatchSetPublishDetail(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		PatchSetPublishDetailX publishDetail = execute(monitor,
				new Operation<org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getChangeDetailService().patchSetPublishDetailX(id, this);
					}
				});
		return publishDetail;
	}

	public GerritChange getChange(String reviewId, IProgressMonitor monitor) throws GerritException {
		GerritChange change = new GerritChange();
		int id;
		try {
			id = id(reviewId);
		} catch (GerritException e) {
			List<ChangeInfo> result = executeQuery(monitor, reviewId);
			if (result.size() == 1) {
				id = result.get(0).getId().get();
			} else {
				throw e;
			}
		}
		ChangeDetailX changeDetail = getChangeDetail(id, monitor);
		List<PatchSetDetail> patchSets = new ArrayList<PatchSetDetail>(changeDetail.getPatchSets().size());
		Map<PatchSet.Id, PatchSetPublishDetailX> patchSetPublishDetailByPatchSetId = new HashMap<PatchSet.Id, PatchSetPublishDetailX>();
		for (PatchSet patchSet : changeDetail.getPatchSets()) {
			PatchSetDetail patchSetDetail = getPatchSetDetail(null, patchSet.getId(), monitor);
			patchSets.add(patchSetDetail);
			if (!isAnonymous()) {
				PatchSetPublishDetailX patchSetPublishDetail = getPatchSetPublishDetail(patchSet.getId(), monitor);
				patchSetPublishDetailByPatchSetId.put(patchSet.getId(), patchSetPublishDetail);
			}
		}
		change.setChangeDetail(changeDetail);
		change.setPatchSets(patchSets);
		change.setPatchSetPublishDetailByPatchSetId(patchSetPublishDetailByPatchSetId);
		return change;
	}

	public int id(String id) throws GerritException {
		if (id == null) {
			throw new GerritException("Invalid ID (null)");
		}
		try {
			return Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new GerritException(NLS.bind("Invalid ID (''{0}'')", id));
		}
	}

	public void publishComments(String reviewId, int patchSetId, final String message,
			final Set<ApprovalCategoryValue.Id> approvals, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		execute(monitor, new Operation<VoidResult>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().publishComments(id, message, approvals, this);
			}
		});
	}

	public ReviewerResult addReviewers(String reviewId, final List<String> reviewers, IProgressMonitor monitor)
			throws GerritException {
		final Change.Id id = new Change.Id(id(reviewId));
		try {
			return execute(monitor, new Operation<ReviewerResult>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getPatchDetailService().addReviewers(id, reviewers, this);
				}
			});
		} catch (GerritException e) {
			// Gerrit 2.2
			String message = e.getMessage();
			if (message != null && message.contains("Error parsing request")) { //$NON-NLS-1$
				return execute(monitor, new Operation<ReviewerResult>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getPatchDetailService().addReviewers(id, reviewers, false, this);
					}
				});
			} else {
				throw e;
			}
		}
	}

	/**
	 * Returns the latest 25 reviews.
	 */
	public List<ChangeInfo> queryAllReviews(IProgressMonitor monitor) throws GerritException {
		return executeQuery(monitor, "status:open"); //$NON-NLS-1$
	}

	/**
	 * Returns the latest 25 reviews for the given project.
	 */
	public List<ChangeInfo> queryByProject(IProgressMonitor monitor, final String project) throws GerritException {
		return executeQuery(monitor, "status:open project:" + project); //$NON-NLS-1$
	}

	/**
	 * Called to get all gerrit tasks associated with the id of the user. This includes all open, closed and reviewable
	 * reviews for the user.
	 */
	public List<ChangeInfo> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		final Account account = getAccount(monitor);
		AccountDashboardInfo ad = execute(monitor, new Operation<AccountDashboardInfo>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeListService().forAccount(account.getId(), this);
			}
		});

		List<ChangeInfo> allMyChanges = ad.getByOwner();
		allMyChanges.addAll(ad.getForReview());
		allMyChanges.addAll(ad.getClosed());
		return allMyChanges;
	}

	/**
	 * Returns watched changes of the currently logged in user
	 */
	public List<ChangeInfo> queryWatchedReviews(IProgressMonitor monitor) throws GerritException {
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
				throw new GerritException("Failed to obtain Gerrit configuration");
			}
			return gerritConfig;
		} catch (UnknownHostException cause) {
			GerritException e = new GerritException("Unknown host: " + cause.getMessage());
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

	public ChangeDetail restore(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService().restoreChange(id, message, this);
			}
		});
	}

	public ChangeDetail submit(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return execute(monitor, new Operation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeManageService().submit(id, this);
			}
		});
	}

	private void addComments(IFileRevision revision, List<PatchLineComment> comments, AccountInfoCache accountInfoCache) {
		if (comments == null) {
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
			topic.setAuthor(author);
			topic.setCreationDate(comment.getWrittenOn());
			topic.setLocation(location);
			topic.setItem(revision);
			topic.setDescription(comment.getMessage());
			topic.getComments().add(topicComment);

			revision.getTopics().add(topic);
		}
	}

	public List<ChangeInfo> executeQuery(IProgressMonitor monitor, final String queryString) throws GerritException {
		SingleListChangeInfo sl = execute(monitor, new Operation<SingleListChangeInfo>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeListService().allQueryNext(queryString, "z", -1, this); //$NON-NLS-1$
			}
		});
		return sl.getChanges();
	}

	/**
	 * Returns the (possibly cached) account for this client.
	 */
	public Account getAccount(IProgressMonitor monitor) throws GerritException {
//		LoginResult result = execute(monitor, new GerritOperation<LoginResult>() {
//			@Override
//			public void execute(IProgressMonitor monitor) throws GerritException {
//				getService(UserPassAuthService.class).authenticate("steffen.pingel", null, this);
//			}
//		});

		synchronized (this) {
			if (myAcount != null) {
				return myAcount;
			}
		}
		Account account = execute(monitor, new Operation<Account>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getAccountService().myAccount(this);
			}
		});

		synchronized (this) {
			myAcount = account;
		}
		return myAcount;
	}

	private AccountService getAccountService() {
		return getService(AccountService.class);
	}

	private ChangeDetailService getChangeDetailService() {
		return getService(ChangeDetailService.class);
	}

	private ChangeListService getChangeListService() {
		return getService(ChangeListService.class);
	}

	private ChangeManageService getChangeManageService() {
		return getService(ChangeManageService.class);
	}

	private PatchDetailService getPatchDetailService() {
		return getService(PatchDetailService.class);
	}

	private SystemInfoService getSystemInfoService() {
		return getService(SystemInfoService.class);
	}

	private List<Project> getVisibleProjects(IProgressMonitor monitor, GerritConfig gerritConfig)
			throws GerritException {
		List<Project> result = new ArrayList<Project>();
		try {
			List<ProjectDetailX> projectDetails = execute(monitor, new Operation<List<ProjectDetailX>>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getProjectAdminService().visibleProjectDetails(this);
				}
			});
			for (ProjectDetailX projectDetail : projectDetails) {
				if (!GerritUtil.isPermissionOnlyProject(projectDetail, gerritConfig)) {
					result.add(projectDetail.project);
				}
			}
		} catch (GerritException e) {
			// Gerrit <= 2.2.1
			String message = e.getMessage();
			if (message != null && message.contains("No such service method")) { //$NON-NLS-1$
				List<Project> projects = execute(monitor, new Operation<List<Project>>() {
					@Override
					public void execute(IProgressMonitor monitor) throws GerritException {
						getProjectAdminService().visibleProjects(this);
					}
				});
				for (Project project : projects) {
					ProjectDetailX projectDetail = new ProjectDetailX();
					projectDetail.setProject(project);
					if (!GerritUtil.isPermissionOnlyProject(projectDetail, gerritConfig)) {
						result.add(project);
					}
				}
			} else {
				throw e;
			}
		}
		return result;
	}

	private ProjectAdminService getProjectAdminService() {
		return getService(ProjectAdminService.class);
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

	protected synchronized <T extends RemoteJsonService> T getService(Class<T> clazz) {
		RemoteJsonService service = serviceByClass.get(clazz);
		if (service == null) {
			service = GerritService.create(clazz, client);
			serviceByClass.put(clazz, service);
		}
		return clazz.cast(service);
	}

}
