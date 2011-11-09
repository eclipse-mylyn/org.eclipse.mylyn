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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
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
import com.google.gerrit.common.data.PatchDetailService;
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
	}

	// XXX belongs in GerritConnector
	public static GerritConfig configFromString(String token) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().fromJson(token, GerritConfig.class);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID,
					"Failed to deserialize configration: '" + token + "'", e));
			return null;
		}
	}

	public boolean isAuthenticationException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& "Invalid xsrfKey in request".equals(((GerritException) exception).getMessage());
		}
		return false;
	}

	// XXX belongs in GerritConnector
	public static String configToString(GerritConfig config) {
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().toJson(config);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, "Failed to serialize configration",
					e));
			return null;
		}
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
		try {
			JSonSupport support = new JSonSupport();
			return support.getGson().toJson(authState);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}

	private static String getText(HtmlStreamTokenizer tokenizer) throws IOException, ParseException {
		StringBuilder sb = new StringBuilder();
		for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
			if (token.getType() == Token.TEXT) {
				sb.append(token.toString());
			} else if (token.getType() == Token.COMMENT) {
				// ignore
			} else {
				break;
			}
		}
		return StringEscapeUtils.unescapeHtml(sb.toString());
	}

	private final GerritHttpClient client;

	private volatile GerritConfig config;

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
		this(location, null, null);
	}

	public GerritClient(AbstractWebLocation location, GerritConfig config, GerritAuthenticationState authState) {
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

	public GerritPatchSetContent getPatchSetContent(String reviewId, int patchSetId, IProgressMonitor monitor)
			throws GerritException {
		Map<Patch.Key, PatchScript> patchScriptByPatchKey = new HashMap<Patch.Key, PatchScript>();

		Change.Id changeId = new Change.Id(id(reviewId));
		PatchSetDetail detail = getPatchSetDetail(changeId, patchSetId, monitor);
		for (Patch patch : detail.getPatches()) {
			PatchScript patchScript = getPatchScript(patch.getKey(), null, detail.getPatchSet().getId(), monitor);
			if (patchScript != null) {
				patchScriptByPatchKey.put(patch.getKey(), patchScript);
			}
		}

		GerritPatchSetContent result = new GerritPatchSetContent();
		result.setPatchScriptByPatchKey(patchScriptByPatchKey);
		return result;
	}

	public GerritConfig getConfig() {
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
		final AccountDiffPreference diffPrefs = new AccountDiffPreference((Account.Id) null);
		diffPrefs.setLineLength(Integer.MAX_VALUE);
		diffPrefs.setTabSize(4);
		diffPrefs.setContext(AccountDiffPreference.WHOLE_FILE_CONTEXT);
		diffPrefs.setIgnoreWhitespace(Whitespace.IGNORE_NONE);
		diffPrefs.setIntralineDifference(false);
		return execute(monitor, new Operation<PatchScript>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().patchScript(key, leftId, rightId, diffPrefs, this);
			}
		});
	}

	public PatchSetDetail getPatchSetDetail(Change.Id changeId, int patchSetId, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(changeId, patchSetId);
		return getPatchSetDetail(id, monitor);
	}

	public PatchSetDetail getPatchSetDetail(final PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		PatchSetDetail result = null;
		try {
			// Gerrit 2.2
			result = execute(monitor, new Operation<PatchSetDetail>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getChangeDetailService().patchSetDetail2(null, id, null, this);
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
							getChangeDetailService().patchSetDetail(id, this);
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
							getChangeDetailService().patchSetDetail(null, id, null, this);
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
			PatchSetDetail patchSetDetail = getPatchSetDetail(patchSet.getId(), monitor);
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
		return execute(monitor, new Operation<ReviewerResult>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().addReviewers(id, reviewers, this);
			}
		});
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
	public GerritConfig refreshConfig(IProgressMonitor monitor) throws GerritException {
		configRefreshed = true;
		GerritConfig config = null;
		try {
			GetMethod method = client.getRequest("/", monitor); //$NON-NLS-1$
			try {
				if (method.getStatusCode() == HttpStatus.SC_OK) {
					InputStream in = WebUtil.getResponseBodyAsStream(method, monitor);
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(in,
								method.getResponseCharSet()));
						HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
						try {
							for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
								if (token.getType() == Token.TAG) {
									HtmlTag tag = (HtmlTag) token.getValue();
									if (tag.getTagType() == Tag.SCRIPT) {
										String text = getText(tokenizer);
										text = text.replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
										text = text.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
										config = parseConfig(text);
										break;
									}
								}
							}
						} catch (ParseException e) {
							throw new IOException("Error reading url"); //$NON-NLS-1$
						}
					} finally {
						in.close();
					}
				}

				if (config == null) {
					throw new GerritException("Failed to obtain Gerrit configuration");
				}

				this.config = config;
				configurationChanged(config);
				return config;
			} finally {
				method.releaseConnection();
			}
		} catch (IOException cause) {
			GerritException e = new GerritException();
			e.initCause(cause);
			throw e;
		}
	}

	public GerritConfig refreshConfigOnce(IProgressMonitor monitor) throws GerritException {
		if (!configRefreshed && config == null) {
			try {
				refreshConfig(monitor);
			} catch (GerritException e) {
				// don't fail validation in case config parsing fails
			}
		}
		return getConfig();
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
				getChangeListService().allQueryNext(queryString, "z", 25, this); //$NON-NLS-1$
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

	public boolean isAnonymous() {
		return client.isAnonymous();
	}

	/**
	 * Parses the configuration from <code>text</code>.
	 */
	private GerritConfig parseConfig(String text) {
		String prefix = "var gerrit_hostpagedata={\"config\":"; //$NON-NLS-1$
		String[] tokens = text.split("};"); //$NON-NLS-1$
		for (String token : tokens) {
			if (token.startsWith(prefix)) {
				token = token.substring(prefix.length());
				return configFromString(token);
			}
		}
		return null;
	}

	protected void configurationChanged(GerritConfig config) {
	}

	protected void authStateChanged(GerritAuthenticationState config) {
	}

	protected <T> T execute(IProgressMonitor monitor, Operation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
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
		} finally {
			GerritRequest.setCurrentRequest(null);
		}
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