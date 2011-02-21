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

import javax.swing.text.html.HTML.Tag;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritService.GerritRequest;
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
import org.eclipse.osgi.util.NLS;

import com.google.gerrit.common.data.AccountDashboardInfo;
import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.AccountService;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.ChangeDetailService;
import com.google.gerrit.common.data.ChangeInfo;
import com.google.gerrit.common.data.ChangeListService;
import com.google.gerrit.common.data.CommentDetail;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchDetailService;
import com.google.gerrit.common.data.PatchScript;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.SingleListChangeInfo;
import com.google.gerrit.common.data.SystemInfoService;
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.Account.Id;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ContributorAgreement;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.PatchLineComment;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtjsonrpc.client.RemoteJsonService;

/**
 * Facade to the Gerrit RPC API.
 * 
 * @author Mikael Kober
 * @author Thomas Westling
 * @author Steffen Pingel
 */
public class GerritClient {

	private static final ReviewsFactory FACTORY = ReviewsFactory.eINSTANCE;

	private abstract class GerritOperation<T> implements AsyncCallback<T> {

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
			this.exception = exception;
		}

		public void onSuccess(T result) {
			setResult(result);
		}

		protected void setResult(T result) {
			this.result = result;
		}
	}

	private final GerritHttpClient client;

	private Account myAcount;

	private final Map<Class<? extends RemoteJsonService>, RemoteJsonService> serviceByClass;

	private AccountDiffPreference myDiffPreference;

	private volatile GerritConfig config;

	public GerritClient(AbstractWebLocation location) {
		this.client = new GerritHttpClient(location);
		this.serviceByClass = new HashMap<Class<? extends RemoteJsonService>, RemoteJsonService>();
	}

	/**
	 * Returns the details for a specific review.
	 */
	public ChangeDetail getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException {
		final Change.Id id = new Change.Id(reviewId);
		return execute(monitor, new GerritOperation<ChangeDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService().changeDetail(id, this);
			}
		});
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
			account = execute(monitor, new GerritOperation<Account>() {
				@Override
				public void execute(IProgressMonitor monitor) throws GerritException {
					getAccountService().myAccount(this);
				}
			});
		} else {
			// XXX should run some more meaningful validation as anonymous, for now any call is good to validate the URL etc.
			executeQuery(monitor, "status:open"); //$NON-NLS-1$
		}
		return new GerritSystemInfo(contributorAgreements, account);
	}

	private boolean isAnonymous() {
		return client.isAnonymous();
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
		return execute(monitor, new GerritOperation<PatchScript>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getPatchDetailService().patchScript(key, leftId, rightId, diffPrefs, this);
			}
		});
	}

	public PatchSetDetail getPatchSetDetail(Change.Id changeId, int patchSetId, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(changeId, patchSetId);
		return execute(monitor, new GerritOperation<PatchSetDetail>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService().patchSetDetail(id, this);
			}
		});
	}

	public int id(String id) throws GerritException {
		if (id == null) {
			throw new GerritException("Invalid ID (null)");
		}
		try {
			return Integer.parseInt(id);
		} catch (NumberFormatException e) {
			throw new GerritException(NLS.bind("Invalid ID ('{0}')", id));
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

	private List<ChangeInfo> executeQuery(IProgressMonitor monitor, final String queryString) throws GerritException {
		SingleListChangeInfo sl = execute(monitor, new GerritOperation<SingleListChangeInfo>() {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeListService().allQueryNext(queryString, "z", 25, this); //$NON-NLS-1$
			}
		});
		return sl.getChanges();
	}

	/**
	 * Called to get all gerrit tasks associated with the id of the user. This includes all open, closed and reviewable
	 * reviews for the user.
	 */
	public List<ChangeInfo> queryMyReviews(IProgressMonitor monitor) throws GerritException {
		final Account account = getAccount(monitor);
		AccountDashboardInfo ad = execute(monitor, new GerritOperation<AccountDashboardInfo>() {
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

	public AccountDiffPreference getDiffPreference(IProgressMonitor monitor) throws GerritException {
		synchronized (this) {
			if (myDiffPreference != null) {
				return myDiffPreference;
			}
		}
		AccountDiffPreference diffPreference = execute(monitor, new GerritOperation<AccountDiffPreference>() {
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

	private Account getAccount(IProgressMonitor monitor) throws GerritException {
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
		Account account = execute(monitor, new GerritOperation<Account>() {
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

	private SystemInfoService getSystemInfoService() {
		return getService(SystemInfoService.class);
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

	private PatchDetailService getPatchDetailService() {
		return getService(PatchDetailService.class);
	}

	protected <T> T execute(IProgressMonitor monitor, GerritOperation<T> operation) throws GerritException {
		try {
			GerritRequest.setCurrentRequest(new GerritRequest(monitor));
			operation.execute(monitor);
			if (operation.getException() instanceof GerritException) {
				throw (GerritException) operation.getException();
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

	public IReview getReview(String reviewId, IProgressMonitor monitor) throws GerritException {
		IReview review = FACTORY.createReview();
		review.setId(reviewId);
		ChangeDetail detail = getChangeDetail(id(reviewId), monitor);
		List<PatchSet> patchSets = detail.getPatchSets();
		for (PatchSet patchSet : patchSets) {
			IReviewItemSet itemSet = FACTORY.createReviewItemSet();
			itemSet.setName(NLS.bind("Patch Set {0}", patchSet.getPatchSetId()));
			itemSet.setId(patchSet.getPatchSetId() + "");
			itemSet.setAddedBy(createUser(patchSet.getUploader(), detail.getAccounts()));
			itemSet.setRevision(patchSet.getRevision().get());
			itemSet.setReview(review);
			// TODO store patchSet.getRefName()
			review.getItems().add(itemSet);
		}
		return review;
	}

	public List<IReviewItem> getChangeSetDetails(IReviewItemSet itemSet, IProgressMonitor monitor)
			throws GerritException {
		List<IReviewItem> result = new ArrayList<IReviewItem>();
		Change.Id changeId = new Change.Id(id(itemSet.getReview().getId()));
		PatchSetDetail detail = getPatchSetDetail(changeId, id(itemSet.getId()), monitor);
		for (Patch patch : detail.getPatches()) {
			IFileItem item = FACTORY.createFileItem();
			PatchScript patchScript = getPatchScript(patch.getKey(), null, detail.getPatchSet().getId(), monitor);
			if (patchScript != null) {
				CommentDetail commentDetail = patchScript.getCommentDetail();

				IFileRevision revisionA = FACTORY.createFileRevision();
				revisionA.setContent(patchScript.getA().asString());
				revisionA.setPath(patchScript.getA().getPath());
				revisionA.setRevision("Base");
				addComments(revisionA, commentDetail.getCommentsA(), commentDetail.getAccounts());
				item.setBase(revisionA);

				IFileRevision revisionB = FACTORY.createFileRevision();
				SparseFileContent target = patchScript.getB().apply(patchScript.getA(), patchScript.getEdits());
				revisionB.setContent(target.asString());
				revisionB.setPath(patchScript.getB().getPath());
				revisionB.setRevision(itemSet.getName());
				addComments(revisionB, commentDetail.getCommentsB(), commentDetail.getAccounts());
				item.setTarget(revisionB);
			}
			item.setName(patch.getFileName());
			result.add(item);
		}
		return result;
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

			IUser author = createUser(comment.getAuthor(), accountInfoCache);

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

	private IUser createUser(Id id, AccountInfoCache accountInfoCache) {
		AccountInfo info = accountInfoCache.get(id);
		IUser user = FACTORY.createUser();
		user.setDisplayName(info.getFullName());
		user.setId(Integer.toString(id.get()));
		return user;
	}

	public GerritConfig getConfig() {
		return config;
	}

	/**
	 * Retrieves the root URL for the Gerrit instance and attempts to parse the configuration from the JavaScript
	 * portion of the page.
	 */
	public GerritConfig refreshConfig(IProgressMonitor monitor) throws GerritException {
		GerritConfig config = null;
		try {
			GetMethod method = client.getRequest("", monitor); //$NON-NLS-1$
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

	/**
	 * Parses the configuration from <code>text</code>.
	 */
	private GerritConfig parseConfig(String text) {
		String prefix = "var gerrit_hostpagedata={\"config\":"; //$NON-NLS-1$
		String[] tokens = text.split("};"); //$NON-NLS-1$
		for (String token : tokens) {
			if (token.startsWith(prefix)) {
				token = token.substring(prefix.length());
				JSonSupport support = new JSonSupport();
				return support.getGson().fromJson(token, GerritConfig.class);
			}
		}
		return null;
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

}
