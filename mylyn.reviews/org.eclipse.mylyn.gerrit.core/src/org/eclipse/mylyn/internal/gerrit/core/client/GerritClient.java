/*********************************************************************
 * Copyright (c) 2010, 2015 Sony Ericsson/ST Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.GerritUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.ErrorHandler;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritHttpClient.Request;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritRestClient.Operation;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ChangeDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchDetailService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchSetPublishDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectAdminService;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.data.GerritQueryResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AbandonInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AddReviewerResult;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.BranchInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo28;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CherryPickInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInput;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectInfo;
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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gerrit.common.data.AccountInfo;
import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.ApprovalDetail;
import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.common.data.ReviewerResult;
import com.google.gerrit.common.data.ToggleStarRequest;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.ContributorAgreement;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.ChangeType;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSet.Id;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;
import com.google.gson.reflect.TypeToken;
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
public abstract class GerritClient extends ReviewsClient {

	private final Cache<Project.NameKey, Set<String>> projectBranchMap = Caffeine.newBuilder().build();

	final String NOT_SIGNED_IN = "Not Signed In"; //$NON-NLS-1$

	private static final Pattern GERRIT_VERSION_PATTERN = Pattern.compile("Powered by Gerrit Code Review (.+)</p>"); //$NON-NLS-1$

	private GerritHttpClient client;

	private GerritRestClient restClient;

	private volatile GerritConfiguration config;

	private Account myAccount;

	private final Version version;

	private Map<Class<? extends RemoteJsonService>, RemoteJsonService> serviceByClass;

	private GerritClientStateListener stateListener;

	private volatile boolean configRefreshed;

	public abstract ChangeDetail rebase(String reviewId, int patchSetId, IProgressMonitor monitor)
			throws GerritException;

	protected abstract Account executeAccount(IProgressMonitor monitor) throws GerritException;

	public abstract ChangeDetailX getChangeDetail(int reviewId, IProgressMonitor monitor) throws GerritException;

	public abstract PatchSetPublishDetailX getPatchSetPublishDetail(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException;

	public abstract CommentInput saveDraft(Key patchKey, String message, int line, short side, String parentUuid,
			String uuid, IProgressMonitor monitor) throws GerritException;

	public abstract VoidResult deleteDraft(Key patchkey, String uuid, IProgressMonitor monitor) throws GerritException;

	protected abstract void applyPatchSetInfo(PatchSetDetail patchSetDetail,
			PatchSetPublishDetailX patchSetPublishDetail, IProgressMonitor monitor) throws GerritException;

	public static GerritClient create(TaskRepository repository, AbstractWebLocation location) {
		return create(repository, location, null, null, null, null);
	}

	public static GerritClient create(TaskRepository repository, AbstractWebLocation location,
			GerritConfiguration config, GerritAuthenticationState authState, String xsrfKey,
			GerritClientStateListener stateListener) {
		Version version = Version.emptyVersion;
		GerritClient versionDiscoveryClient = new GerritClient212(repository, version);
		versionDiscoveryClient.initialize(location, config, authState, xsrfKey, stateListener);
		try {
			version = versionDiscoveryClient.getVersion(new NullProgressMonitor());
		} catch (GerritException e) {
			//Ignore, we'll just use the base client.
		}
		GerritClient client = new GerritClient212(repository, version);
		client.initialize(location, config, authState, xsrfKey, stateListener);
		return client;
	}

	protected GerritClient(TaskRepository repository, Version version) {
		super(repository);
		Assert.isNotNull(version);
		this.version = version;
	}

	protected void initialize(AbstractWebLocation location, GerritConfiguration config,
			GerritAuthenticationState authState, String xsrfKey, final GerritClientStateListener stateListener) {
		this.stateListener = stateListener;
		this.client = new GerritHttpClient(location, version) {
			@Override
			protected void sessionChanged(Cookie cookie) {
				GerritAuthenticationState authState = new GerritAuthenticationState();
				authState.setCookie(cookie);
				if (stateListener != null) {
					stateListener.authStateChanged(authState);
				}
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
		this.restClient = new GerritRestClient(client);
	}

	public GerritSystemInfo getInfo(IProgressMonitor monitor) throws GerritException {
		List<ContributorAgreement> contributorAgreements = null;
		Account account = null;
		if (!isAnonymous()) {
			account = getAccount(monitor);
		} else {
			// XXX should run some more meaningful validation as anonymous, for now any call is good to validate the URL etc.
			restClient.executeQuery(monitor, "status:open"); //$NON-NLS-1$
		}
		refreshConfigOnce(monitor);
		return new GerritSystemInfo(getVersion(), contributorAgreements, account);
	}

	public GerritConfiguration refreshConfigOnce(IProgressMonitor monitor) throws GerritException {
		return refreshConfigOnce(null, monitor);
	}

	public GerritConfiguration refreshConfigOnce(Project.NameKey project, IProgressMonitor monitor)
			throws GerritException {
		if (!configRefreshed && config == null) {
			try {
				refreshConfig(monitor);
			} catch (GerritException e) {
				// don't fail validation in case config parsing fails
			}
		}

		GerritConfiguration config = getConfiguration();
		if (project != null && getCachedBranches(project) == null) {
			cacheBranches(project, monitor);
		}
		return config;
	}

	public GerritConfiguration getConfiguration() {
		return config;
	}

	public GerritConfigX getGerritConfig() {
		return config == null ? null : config.getGerritConfig();
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
				if (GerritVersion.isVersion2120OrLater(version)) {
					gerritConfig = getGerritConfigFromServerInfo(monitor);
				} else {
					throw new GerritException("Failed to obtain Gerrit configuration"); //$NON-NLS-1$
				}
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

	protected GerritConfigX getGerritConfigFromServerInfo(IProgressMonitor monitor) throws GerritException {
		return new GerritConfigX();
	}

	public GerritConfiguration refreshConfig(IProgressMonitor monitor) throws GerritException {
		refreshAllCachedProjectBranches(monitor);
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
		if (stateListener != null) {
			stateListener.configurationChanged(config);
		}
		return config;
	}

	public boolean isNotSignedInException(Throwable exception) {
		if (exception instanceof GerritException) {
			return ((GerritException) exception).getCode() == -32603
					&& NOT_SIGNED_IN.equalsIgnoreCase(((GerritException) exception).getMessage());
		}
		return false;
	}

	private List<Project> getVisibleProjects(IProgressMonitor monitor, GerritConfig gerritConfig)
			throws GerritException {
		List<Project> result = new ArrayList<Project>();
		try {
			for (ProjectDetailX projectDetail : getProjectDetails(monitor, gerritConfig, result)) {
				if (!GerritUtil.isPermissionOnlyProject(projectDetail, gerritConfig)) {
					result.add(projectDetail.project);
				}
			}
		} catch (GerritException e) {
			if (isNoSuchServiceError(e)) {
				addProjectsWhenNoSuchService(monitor, gerritConfig, result);
			} else {
				throw e;
			}
		}
		Collections.sort(result, new ProjectByNameComparator());
		return result;
	}

	protected List<ProjectDetailX> getProjectDetails(IProgressMonitor monitor, GerritConfig gerritConfig,
			List<Project> result) throws GerritException {
		List<ProjectDetailX> projectDetails = restClient.execute(monitor, new Operation<List<ProjectDetailX>>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getProjectAdminService(monitor).visibleProjectDetails(this);
			}
		});
		return projectDetails;
	}

	private boolean isNoSuchServiceError(GerritException e) {
		String message = e.getMessage();
		return message != null && message.contains("No such service method"); //$NON-NLS-1$
	}

	public GerritChange getChange(String reviewId, IProgressMonitor monitor) throws GerritException {
		GerritChange gerritChange = new GerritChange();
		int id;
		try {
			id = id(reviewId);
		} catch (GerritException e) {
			List<GerritQueryResult> result = restClient.executeQuery(monitor, reviewId);
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

					applyPatchSetInfo(patchSetDetail, patchSetPublishDetail, monitor);
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

	protected PatchSetDetail getPatchSetDetail(PatchSet.Id idBase, PatchSet.Id idTarget, IProgressMonitor monitor)
			throws GerritException {
		PatchSetDetail patchSetDetail = restClient.execute(monitor, new Operation<PatchSetDetail>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {
				getChangeDetailService(monitor).patchSetDetail2(idBase, idTarget, createAccountDiffPreference(), this);
			}
		});
		return patchSetDetail;
	}

	protected PatchScriptX getPatchScript(final Patch.Key key, final PatchSet.Id leftId, final PatchSet.Id rightId,
			final IProgressMonitor monitor) throws GerritException {
		final AccountDiffPreference diffPrefs = createAccountDiffPreference();
		final PatchScriptX patchScript = restClient.execute(monitor, new Operation<PatchScriptX>(client) {
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

	protected void fetchLeftBinaryContent(final PatchScriptX patchScript, final Patch.Key key, final PatchSet.Id leftId,
			final IProgressMonitor monitor) throws GerritException {
		if (EnumSet.of(ChangeType.DELETED, ChangeType.MODIFIED).contains(patchScript.getChangeType())) {
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
		byte[] bin = restClient.executeGetRestRequest("/cat/" + url, byteArrayType.getType(), monitor); //$NON-NLS-1$
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

	protected AccountDiffPreference createAccountDiffPreference() {
		AccountDiffPreference diffPrefs = new AccountDiffPreference((Account.Id) null);
		diffPrefs.setLineLength(Integer.MAX_VALUE);
		diffPrefs.setTabSize(4);
		diffPrefs.setContext(AccountDiffPreference.WHOLE_FILE_CONTEXT);
		diffPrefs.setIgnoreWhitespace(Whitespace.IGNORE_NONE);
		diffPrefs.setIntralineDifference(false);
		return diffPrefs;
	}

	private ChangeDetailService getChangeDetailService(IProgressMonitor monitor) {
		return getService(ChangeDetailService.class, monitor);
	}

	private PatchDetailService getPatchDetailService(IProgressMonitor monitor) {
		return getService(PatchDetailService.class, monitor);
	}

	private ProjectAdminService getProjectAdminService(IProgressMonitor monitor) {
		return getService(ProjectAdminService.class, monitor);
	}

	private synchronized <T extends RemoteJsonService> T getService(Class<T> clazz, IProgressMonitor monitor) {
		RemoteJsonService service = serviceByClass.get(clazz);
		if (service == null) {
			service = GerritService.create(clazz, client, getVersion());
			serviceByClass.put(clazz, service);
		}
		return clazz.cast(service);
	}

	protected List<ReviewerInfo> listReviewers(final int reviewId, IProgressMonitor monitor) throws GerritException {
		final String uri = "/changes/" + reviewId + "/reviewers/"; //$NON-NLS-1$ //$NON-NLS-2$
		TypeToken<List<ReviewerInfo>> reviewersListType = new TypeToken<List<ReviewerInfo>>() {
		};
		return restClient.executeGetRestRequest(uri, reviewersListType.getType(), monitor);
	}

	protected boolean hasAllReviewers(AccountInfoCache accounts, List<ReviewerInfo> reviewers) {
		for (ReviewerInfo reviewer : reviewers) {
			AccountInfo cachedAccount = accounts.get(new Account.Id(reviewer.getId()));
			if (cachedAccount == null || isAnonymous(cachedAccount)) {
				return false;
			}
		}
		return true;
	}

	public ReviewerResult addReviewers(String reviewId, final List<String> reviewers, IProgressMonitor monitor)
			throws GerritException {
		Assert.isLegal(reviewers != null, "reviewers cannot be null"); //$NON-NLS-1$
		final Change.Id id = new Change.Id(id(reviewId));
		final String uri;
		uri = "/a/changes/" + id.get() + "/reviewers"; //$NON-NLS-1$ //$NON-NLS-2$

		Set<ReviewerInfo> reviewerInfos = new HashSet<ReviewerInfo>(reviewers.size());
		ReviewerResult reviewerResult = new ReviewerResult();
		for (final String reviewerId : reviewers) {
			try {
				AddReviewerResult addReviewerResult = restClient.executePostRestRequest(uri,
						new ReviewerInput(reviewerId), AddReviewerResult.class, null, monitor);
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

	public ReviewerResult removeReviewer(String reviewId, String reviewerId, IProgressMonitor monitor)
			throws GerritException {
		Change.Id id = new Change.Id(id(reviewId));

		String uri = "/a/changes/" + id.get() + "/reviewers/" + reviewerId; //$NON-NLS-1$ //$NON-NLS-2$
		ReviewerResult reviewerResult = new ReviewerResult();

		//Try to remove the reviewer from the change
		try {
			//Currently using a string to return as the only response from the gerrit api will be an http status
			restClient.executeDeleteRestRequest(uri, new ReviewerInput(reviewerId), String.class, null, monitor);
		} catch (GerritHttpException e) {
			if (e.getResponseCode() == HttpStatus.SC_NOT_FOUND) {
				reviewerResult.addError(new ReviewerResult.Error(null, reviewerId));
			}
		} catch (GerritLoginException e) {
			reviewerResult.addError(new ReviewerResult.Error(null, reviewerId));
		}

		//Now that the reviewer has been removed, remove that reviewers approvals on the change
		ChangeDetail changeDetail = getChangeDetail(id.get(), monitor);
		List<ApprovalDetail> approvalDetails = new ArrayList<>();
		for (ApprovalDetail approval : changeDetail.getApprovals()) {
			//If this approval is not made by the user we are removing, add it to the list of approvals
			if (!approval.getAccount().equals(reviewerId)) {
				approvalDetails.add(approval);
			}
		}
		changeDetail.setApprovals(approvalDetails);
		reviewerResult.setChange(changeDetail);
		return reviewerResult;
	}

	protected void merge(AccountInfoCache accounts, List<ReviewerInfo> reviewers) {
		Set<com.google.gerrit.common.data.AccountInfo> accountInfos = new HashSet<com.google.gerrit.common.data.AccountInfo>(
				reviewers.size());
		for (ReviewerInfo reviewer : reviewers) {
			accountInfos.add(reviewer.toAccountInfo());
		}
		AccountInfoCache accountInfoCache = new AccountInfoCache(accountInfos);
		accounts.merge(accountInfoCache);
	}

	public ChangeDetail cherryPick(String reviewId, int patchSetId, final String message, final String destBranch,
			IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		String url = "/changes/" + id.getParentKey() + "/revisions/" + id.get() + "/cherrypick"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		CherryPickInput input = new CherryPickInput(message, destBranch);
		ErrorHandler handler = new ErrorHandler() {
			@Override
			public void handleError(HttpMethodBase method) throws GerritException {
				String errorMsg = getResponseBodyAsString(method);
				if (isNotPermitted(method, errorMsg)) {
					errorMsg = NLS.bind("Cannot cherry pick: {0}", errorMsg); //$NON-NLS-1$
				} else if (isConflict(method)) {
					errorMsg = NLS.bind("Request Conflict: {0}", errorMsg); //$NON-NLS-1$
				} else if (isBadRequest(method)) {
					errorMsg = NLS.bind("Bad Request: {0}", errorMsg); //$NON-NLS-1$
				}
				throw new GerritException(errorMsg);
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
						&& msg.toLowerCase().startsWith("cherry pick not permitted"); //$NON-NLS-1$
			}

			private boolean isConflict(HttpMethodBase method) {
				return method.getStatusCode() == HttpURLConnection.HTTP_CONFLICT;
			}

			private boolean isBadRequest(HttpMethodBase method) {
				return method.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST;
			}
		};
		ChangeInfo result = restClient.executePostRestRequest(url, input, ChangeInfo.class, handler, monitor);

		return getChangeDetail(result.getNumber(), monitor);
	}

	private Map<String, ProjectInfo> listProjects(IProgressMonitor monitor) throws GerritException {
		final String uri = "/projects/"; //$NON-NLS-1$
		TypeToken<Map<String, ProjectInfo>> resultType = new TypeToken<Map<String, ProjectInfo>>() {
		};
		return restClient.executeGetRestRequest(uri, resultType.getType(), monitor);
	}

	private void addProjectsWhenNoSuchService(IProgressMonitor monitor, GerritConfig gerritConfig, List<Project> result)
			throws GerritException {
		Map<String, ProjectInfo> projects = listProjects(monitor);
		for (String projectName : projects.keySet()) {
			result.add(new Project(new NameKey(projectName)));
		}
	}

	public VoidResult setStarred(final String reviewId, final boolean starred, IProgressMonitor monitor)
			throws GerritException {
		final Change.Id id = new Change.Id(id(reviewId));
		final ToggleStarRequest req = new ToggleStarRequest();
		req.toggle(id, starred);
		final String uri = "/a/accounts/self/starred.changes/" + id.get(); //$NON-NLS-1$

		return restClient.execute(monitor, new Operation<VoidResult>(client) {
			@Override
			public void execute(IProgressMonitor monitor) throws GerritException {

				if (starred) {
					restClient.executePutRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(), monitor);
				} else {
					restClient.executeDeleteRestRequest(uri, req, ToggleStarRequest.class, createErrorHandler(),
							monitor);
				}
			}
		});
	}

	protected void setRevisionParentCommit(ChangeInfo changeInfo, ChangeDetailX changeDetail) {
		if (changeInfo.getRevisions() != null) {
			for (Entry<String, RevisionInfo> revisions : changeInfo.getRevisions().entrySet()) {
				RevisionInfo revision = revisions.getValue();
				if (revision.getCommit() != null) {
					CommitInfo commit = revision.getCommit();
					if (commit.getParents().length >= 1) {
						if (changeDetail.getParents() == null) {
							changeDetail.setParents(new HashMap<Integer, CommitInfo[]>());
						}
						changeDetail.getParents().put(revision.getNumber(), commit.getParents());
					}
				}
			}
		}
	}

	/**
	 * Contains different information than the ChangeInfo returned by {@link #getChangeInfo(int, IProgressMonitor)}.
	 * Introduced in 2.8.
	 */
	protected ChangeInfo28 getAdditionalChangeInfo(int reviewId, IProgressMonitor monitor) {
		ChangeInfo28 changeInfo28 = null;
		try {
			changeInfo28 = restClient.executeGetRestRequest("/changes/" + Integer.toString(reviewId) //$NON-NLS-1$
					+ "/?o=ALL_REVISIONS&o=CURRENT_ACTIONS&o=ALL_COMMITS", ChangeInfo28.class, monitor); //$NON-NLS-1$
		} catch (GerritException e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
		}
		return changeInfo28;
	}

	private void refreshAllCachedProjectBranches(IProgressMonitor monitor) throws GerritException {
		Set<Project.NameKey> projects = projectBranchMap.asMap().keySet();
		for (Project.NameKey project : projects) {
			cacheBranches(project, monitor);
		}
	}

	private void cacheBranches(Project.NameKey project, IProgressMonitor monitor) throws GerritException {
		Set<String> branchNames = getBranchNames(project, monitor);
		projectBranchMap.put(project, branchNames);
	}

	private Set<String> getBranchNames(Project.NameKey project, IProgressMonitor monitor) throws GerritException {
		return Arrays.asList(getRemoteProjectBranches(project.get(), monitor))
				.stream()
				.map(b -> b.getRef())
				.collect(Collectors.toUnmodifiableSet());
	}

	public BranchInfo[] getRemoteProjectBranches(String projectName, IProgressMonitor monitor) throws GerritException {
		String url = getProjectBranchesUrl(projectName);
		return restClient.executeGetRestRequest(url, BranchInfo[].class, monitor);
	}

	public void createRemoteBranch(String projectName, String branchName, String revision, IProgressMonitor monitor)
			throws GerritException {
		String url = getProjectBranchesUrl(projectName) + branchName;
		BranchInput input = new BranchInput(branchName, revision);
		restClient.executePutRestRequest(url, input, BranchInput.class, createErrorHandler(), monitor);
	}

	public void deleteRemoteBranch(String projectName, String branchName, String revision, IProgressMonitor monitor)
			throws GerritException {
		String url = getProjectBranchesUrl(projectName) + branchName;
		BranchInput input = new BranchInput(branchName, revision);
		restClient.executeDeleteRestRequest(url, input, BranchInput.class, createErrorHandler(), monitor);
	}

	public Set<String> getCachedBranches(Project.NameKey project) {
		return projectBranchMap.getIfPresent(project);
	}

	private String getProjectBranchesUrl(String projectName) throws GerritException {
		try {
			String encodedProjectName = URLEncoder.encode(projectName, "UTF-8"); //$NON-NLS-1$
			return "/projects/" + encodedProjectName + "/branches/"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (UnsupportedEncodingException e) {
			throw new GerritException(e);
		}
	}

	public void clearCachedBranches(Project.NameKey project) {
		projectBranchMap.invalidate(project);
	}

	public ChangeDetail restore(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		final String uri = "/a/changes/" + id.getParentKey().get() + "/restore"; //$NON-NLS-1$ //$NON-NLS-2$
		try {
			restClient.executePostRestRequest(uri, new RestoreInput(message), ChangeInfo.class, null, monitor);
		} catch (GerritHttpException e) {
			if (e.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
				throw new GerritException("Not Found", e); //$NON-NLS-1$
			}
		}
		return getChangeDetail(id.getParentKey().get(), monitor);
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
		ReviewInput reviewInput = new ReviewInput(message);
		Map<String, CommentInput[]> drafts = listDrafts(id, monitor);
		if (!drafts.isEmpty()) {
			reviewInput.setComments(drafts);
		}
		reviewInput.setApprovals(approvals);
		final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/review"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		restClient.executePostRestRequest(uri, reviewInput, ReviewInfo.class, new ErrorHandler() {

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

	private void handleMissingPatchSet(String desc, GerritException e) {
		GerritCorePlugin.logWarning(
				NLS.bind("Couldn't load {0}. (Perhaps the Patch Set has been removed from repository?)", desc), e); //$NON-NLS-1$
	}

	public ChangeInfo getChangeInfo(final int reviewId, IProgressMonitor monitor) throws GerritException {
		final String uri = "/changes/" + reviewId + "/revisions/current/review"; //$NON-NLS-1$ //$NON-NLS-2$
		return restClient.executeGetRestRequest(uri, ChangeInfo.class, monitor);
	}

	public ChangeDetail abandon(String reviewId, int patchSetId, final String message, IProgressMonitor monitor)
			throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		final String uri = "/a/changes/" + id.getParentKey().get() + "/abandon"; //$NON-NLS-1$ //$NON-NLS-2$
		restClient.executePostRestRequest(uri, new AbandonInput(message), ChangeInfo.class, null, monitor);
		return getChangeDetail(id.getParentKey().get(), monitor);
	}

	private boolean isAnonymous(AccountInfo accountInfo) {
		return accountInfo.getFullName() == null && accountInfo.getPreferredEmail() == null;
	}

	public String toReviewId(String id, IProgressMonitor monitor) throws GerritException {
		try {
			Integer.parseInt(id);
			return id;
		} catch (NumberFormatException e) {
			try {
				List<GerritQueryResult> results = restClient.executeQuery(monitor, id);
				if (results.size() != 1) {
					throw new GerritException(NLS.bind("{0} is not a valid review ID", id)); //$NON-NLS-1$
				}
				return Integer.toString(results.get(0).getNumber());
			} catch (GerritException e2) {
				throw new GerritException(NLS.bind("{0} is not a valid review ID", id), e2); //$NON-NLS-1$
			}
		}
	}

	protected static String encode(String string) throws GerritException {
		try {
			return URLEncoder.encode(string, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new GerritException(e);
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

	public boolean isAnonymous() {
		return client.isAnonymous();
	}

	/**
	 * Returns the (possibly cached) account for this client.
	 */
	public Account getAccount(IProgressMonitor monitor) throws GerritException {

		synchronized (this) {
			if (myAccount != null) {
				return myAccount;
			}
		}
		Account account = executeAccount(monitor);

		synchronized (this) {
			myAccount = account;
		}
		return myAccount;
	}

	public ChangeDetail submit(String reviewId, int patchSetId, IProgressMonitor monitor) throws GerritException {
		final PatchSet.Id id = new PatchSet.Id(new Change.Id(id(reviewId)), patchSetId);
		return submitRest(id, monitor);
	}

	private ChangeDetail submitRest(PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		final String uri = "/a/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/submit"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		restClient.executePostRestRequest(uri, new SubmitInput(true), SubmitInfo.class, new ErrorHandler() {
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

	private Map<String, CommentInput[]> listDrafts(final PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		String uri = "/changes/" + id.getParentKey().get() + "/revisions/" + id.get() + "/drafts/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		TypeToken<Map<String, CommentInput[]>> resultType = new TypeToken<Map<String, CommentInput[]>>() {
		};

		return restClient.executeGetRestRequest(uri, resultType.getType(), monitor);
	}

	public Version getVersion() {
		return version;
	}

	public Version getVersion(IProgressMonitor monitor) throws GerritException {
		return restClient.execute(monitor, new Operation<Version>(client) {
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

	@Override
	public AbstractRemoteEmfFactoryProvider<IRepository, IReview> createFactoryProvider() {
		return new GerritRemoteFactoryProvider(this);
	}

	public GerritRestClient getRestClient() {
		return restClient;
	}
}
