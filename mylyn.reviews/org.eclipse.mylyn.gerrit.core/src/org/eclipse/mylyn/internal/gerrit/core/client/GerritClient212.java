/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritCorePlugin;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.DownloadSchemeX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.GerritConfigX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.ProjectDetailX;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.SchemeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccessSectionInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.AccountInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ChangeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommentInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.CommitInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ConfigInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.DiffContent;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.DiffInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.DiffPreferencesInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.DownloadSchemeInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.FileInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.GitPersonalInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.GroupInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.PermissionInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.PermissionRuleInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectAccessInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ProjectInfo;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Version;

import com.google.gerrit.common.data.AccountInfoCache;
import com.google.gerrit.common.data.CommentDetail;
import com.google.gerrit.common.data.GerritConfig;
import com.google.gerrit.common.data.InheritedRefRight;
import com.google.gerrit.common.data.PatchScript.DisplayMethod;
import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.prettify.common.SparseFileContent;
import com.google.gerrit.reviewdb.Account;
import com.google.gerrit.reviewdb.AccountDiffPreference;
import com.google.gerrit.reviewdb.AccountDiffPreference.Whitespace;
import com.google.gerrit.reviewdb.AccountGroup;
import com.google.gerrit.reviewdb.ApprovalCategory;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.ChangeType;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchLineComment;
import com.google.gerrit.reviewdb.PatchSet;
import com.google.gerrit.reviewdb.PatchSet.Id;
import com.google.gerrit.reviewdb.PatchSetInfo;
import com.google.gerrit.reviewdb.Project;
import com.google.gerrit.reviewdb.Project.NameKey;
import com.google.gerrit.reviewdb.RefRight;
import com.google.gerrit.reviewdb.RefRight.RefPattern;
import com.google.gerrit.reviewdb.RevId;
import com.google.gerrit.reviewdb.UserIdentity;

public class GerritClient212 extends GerritClient29 {

	private final String SELF_ACCOUNT_ID = "self"; //$NON-NLS-1$

	protected GerritClient212(TaskRepository repository, Version version) {
		super(repository, version);
	}

	@Override
	protected GerritConfigX getGerritConfigFromServerInfo(IProgressMonitor monitor) throws GerritException {
		String query = "/config/server/info"; //$NON-NLS-1$/
		org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo serverInfo = getRestClient()
				.executeGetRestRequest(query, org.eclipse.mylyn.internal.gerrit.core.client.rest.ServerInfo.class,
						monitor);

		return convertServerInfoToGerritConfig(serverInfo);
	}

	private GerritConfigX convertServerInfoToGerritConfig(ServerInfo serverInfo) {
		GerritConfigX config = new GerritConfigX();

		// gerrit/all_projects + user -> wildproject
		config.setWildProject(new NameKey(serverInfo.getGerrit().getRootProject()));

		// download/schemes -> getGerritConfig().getDownloadSchemes
		Map<DownloadSchemeX, SchemeInfo> schemes = new HashMap<>();

		serverInfo.getDownload().getSchemes().entrySet().forEach(entry -> {
			DownloadSchemeX scheme = DownloadSchemeX.fromString(entry.getKey());
			if (scheme != null) {
				DownloadSchemeInfo info = entry.getValue();
				SchemeInfo schemeInfo = new SchemeInfo(info.getUrl(), info.isAuthRequired(), info.isAuthSupported(),
						info.getCommands(), info.getCloneCommands());

				schemes.put(scheme, schemeInfo);
			}
		});

		config.setSchemes(schemes);
		config.setDownloadSchemes(
				schemes.keySet().stream().map(DownloadSchemeX::toDownloadScheme).collect(Collectors.toSet()));

		return config;
	}

	@Override
	protected PatchSetDetail getPatchSetDetail(PatchSet.Id idBase, PatchSet.Id idTarget, IProgressMonitor monitor)
			throws GerritException {
		if (!GerritVersion.isVersion2120OrLater(getVersion())) {
			return super.getPatchSetDetail(idBase, idTarget, monitor);
		}
		// Use REST API, RCP was removed in 2.13
		PatchSetDetail patchSetDetail = new PatchSetDetail();
		CommitInfo commitInfo = retrieveCommitInfo(idTarget, monitor);

		AccountInfo accInfo = adaptAccountInfo(commitInfo, monitor);
		patchSetDetail.setInfo(adaptRestPatchSetInfo(commitInfo, accInfo, idTarget, monitor));
		patchSetDetail.setPatchSet(adaptRestPatchSet(commitInfo, accInfo, idTarget, monitor));
		patchSetDetail.setPatches(adaptRestPatches(idTarget, monitor));
		return patchSetDetail;
	}

	private AccountInfo adaptAccountInfo(CommitInfo commitInfo, IProgressMonitor monitor) throws GerritException {
		String accountId = commitInfo.getAuthor().getEmail();
		for (int retries = 0;; retries++) {
			try {
				return retrieveAccountInfo(accountId, monitor);
			} catch (GerritException e) {
				if (retries < 3) {
					accountId = commitInfo.getAuthor().getName();
					continue;
				} else {
					throw e;
				}
			}
		}
	}

	@Override
	protected PatchScriptX getPatchScript(final Patch.Key key, final PatchSet.Id leftId, final PatchSet.Id rightId,
			final IProgressMonitor monitor) throws GerritException {
		if (!GerritVersion.isVersion2120OrLater(getVersion())) {
			return super.getPatchScript(key, leftId, rightId, monitor);
		}

		String fileName = key.getFileName();
		CommitInfo commitInfo = retrieveCommitInfo(rightId, monitor);
		AccountInfo accInfo = adaptAccountInfo(commitInfo, monitor);
		AccountDiffPreference diffPrefs;
		try {
			diffPrefs = adaptAccountDiffPref(accInfo, retrieveDiffPrefInfo(monitor));
		} catch (GerritException e) {
			diffPrefs = null;
		}
		DiffInfo diffInfo;
		if (leftId == null) {
			diffInfo = retrieveDiffInfoAgainstBase(rightId, fileName, monitor);
		} else {
			diffInfo = retrieveDiffInfoNotBase(rightId, leftId, fileName, monitor);
		}

		CommentDetail commentDetail = new CommentDetail(leftId, rightId);
		if (diffInfo != null) {
			if (diffInfo.getContent().size() > 0) {
				if (diffInfo.getContent().get(0).getA() != null || diffInfo.getContent().get(0).getAb() != null
						|| diffInfo.getContent().get(0).getB() != null) {
					commentDetail = setCommentDetails(leftId, rightId, fileName, commentDetail, monitor);
				}
			}

		}

		PatchScriptX patchScriptX = new PatchScriptX();
		patchScriptX.setChangeId(new Change.Key(rightId.getParentKey().toString()));
		patchScriptX.setDiffPrefs(diffPrefs);
		patchScriptX.setComments(commentDetail);
		patchScriptX.setHeader(diffInfo.getDiff_header());
		patchScriptX.setChangeType(diffInfo.getChange_type());
		patchScriptX.setHistory(adaptRestPatches(rightId, monitor));
		patchScriptX.setDisplayMethodA(DisplayMethod.DIFF); // hardcoded to diff.
		patchScriptX.setDisplayMethodB(DisplayMethod.DIFF);

		if (diffInfo.getContent() != null) {
			patchScriptX.setEdits(adaptDiffContent(diffInfo, patchScriptX, monitor));
		}

		if (diffInfo.getMeta_a() != null) {
			patchScriptX.setA(adaptSparseFileContent_A(diffInfo, monitor));
		} else {
			patchScriptX.setA(new SparseFileContent());
		}
		if (diffInfo.getMeta_b() != null) {
			patchScriptX.setB(adaptSparseFileContent_B(diffInfo, monitor));
		} else {
			patchScriptX.setB(new SparseFileContent());
		}

		if (diffInfo.getDiff_header() != null) {
			if (patchScriptX.isBinary()) {
				fetchLeftBinaryContent(patchScriptX, key, leftId, monitor);
				fetchRightBinaryContent(patchScriptX, key, rightId, monitor);
			}
		}
		return patchScriptX;
	}

	private List<Patch> adaptRestPatches(Id id, IProgressMonitor monitor) throws GerritException {
		List<Patch> patches = new ArrayList<>();
		Map<String, FileInfo> infos = retrieveFileInfos(id, monitor);
		for (String fileName : infos.keySet()) {
			FileInfo fileInfo = infos.get(fileName);
			Patch patch = new Patch(new Key(id, fileName));
			patch.setChangeType(ChangeType.forCode(fileInfo.getStatus()));
			patch.setDeletions(fileInfo.getLinesDeleted());
			patch.setInsertions(fileInfo.getLinesInserted());
			patches.add(patch);
		}
		return patches;
	}

	private PatchSet adaptRestPatchSet(CommitInfo commitInfo, AccountInfo accInfo, PatchSet.Id id,
			IProgressMonitor monitor) throws GerritException {
		PatchSet patchSet = new PatchSet(id);
		patchSet.setRevision(new RevId(commitInfo.getCommit()));
		patchSet.setUploader(new Account.Id(accInfo.getId()));
		patchSet.setCreatedOn(parseTimeStamp(commitInfo.getAuthor().getDate()));
		return patchSet;
	}

	private PatchSetInfo adaptRestPatchSetInfo(CommitInfo commitInfo, AccountInfo accInfo, PatchSet.Id id,
			IProgressMonitor monitor) throws GerritException {
		PatchSetInfo info = new PatchSetInfo(id);
		info.setMessage(commitInfo.getMessage());
		info.setSubject(commitInfo.getSubject());
		info.setAuthor(toUserIdentity(commitInfo.getAuthor(), accInfo, monitor));
		info.setCommitter(toUserIdentity(commitInfo.getCommitter(), accInfo, monitor));
		return info;
	}

	private List<Edit> adaptDiffContent(DiffInfo diffInfo, PatchScriptX patchScriptX, IProgressMonitor monitor)
			throws GerritException {
		List<DiffContent> diffContent = diffInfo.getContent(); // content differences in file
		List<Edit> editsInFile = new ArrayList<>();
		int contentSize_A = 0;
		int contentSize_B = 0;
		for (DiffContent diff : diffContent) {
			if (diff.getAb() != null) { // if ab, do nothing; this is not an Edit.
				contentSize_A += diff.getAb().size();
				contentSize_B += diff.getAb().size();
			} else if (diff.getA() != null && diff.getB() != null) {
				Edit edit = new Edit(contentSize_A, contentSize_A + diff.getA().size() - 1, contentSize_B,
						contentSize_B + diff.getB().size() - 1);
				editsInFile.add(edit);
				contentSize_A += diff.getA().size();
				contentSize_B += diff.getB().size();
				patchScriptX.setIntralineDifference(true); // this behavior might not be correct
			} else if (diff.getA() != null) { // content only on side a (deleted in b)
				Edit edit = new Edit(contentSize_A, contentSize_A + diff.getA().size() - 1, contentSize_B,
						contentSize_B);
				editsInFile.add(edit);
				contentSize_A += diff.getA().size();
			} else if (diff.getB() != null) { // content only on side b (added in b)
				Edit edit = new Edit(contentSize_A, contentSize_A, contentSize_B,
						contentSize_B + diff.getB().size() - 1);
				editsInFile.add(edit);
				contentSize_B += diff.getB().size();
			}
		}
		return editsInFile;
	}

	private AccountDiffPreference adaptAccountDiffPref(AccountInfo accInfo, DiffPreferencesInfo diffPrefInfo) {
		AccountDiffPreference accDiffPref = new AccountDiffPreference(new Account.Id(accInfo.getId()));
		accDiffPref.setContext(diffPrefInfo.getContext());
		accDiffPref.setIgnoreWhitespace(diffPrefInfo.getIgnoreWhitespace() != null
				? diffPrefInfo.getIgnoreWhitespace()
				: Whitespace.IGNORE_NONE);
		accDiffPref.setIntralineDifference(diffPrefInfo.isIntralineDifference());
		accDiffPref.setLineLength(diffPrefInfo.getLineLength());
		accDiffPref.setShowTabs(diffPrefInfo.isShowTabs());
		accDiffPref.setShowWhitespaceErrors(diffPrefInfo.isShowWhitespaceErrors());
		accDiffPref.setSyntaxHighlighting(diffPrefInfo.isSyntaxHighlighting());
		accDiffPref.setTabSize(diffPrefInfo.getTabSize());
		return accDiffPref;
	}

	private CommentDetail setCommentDetails(PatchSet.Id leftId, PatchSet.Id rightId, String fileName,
			CommentDetail commentDetail, IProgressMonitor monitor) throws GerritException {
		if (leftId != null) {
			Map<String, List<CommentInfo>> commentInfo_A = retrieveRevisionComments(leftId, monitor);
			adaptCommentDetails(commentInfo_A, leftId, commentDetail.getCommentsA(), fileName, commentDetail);
		}
		Map<String, List<CommentInfo>> commentInfo_B = retrieveRevisionComments(rightId, monitor);
		adaptCommentDetails(commentInfo_B, rightId, commentDetail.getCommentsB(), fileName, commentDetail);

		ChangeInfo changeInfo = getChangeInfo(rightId.getParentKey().get(), monitor);
		List<com.google.gerrit.common.data.AccountInfo> listAccountInfo = new ArrayList<>();

		com.google.gerrit.common.data.AccountInfo accountInfo = setAccountFromChangeInfo(changeInfo);
		listAccountInfo.add(accountInfo);
		AccountInfoCache accCache = new AccountInfoCache(listAccountInfo);
		commentDetail.setAccountInfoCache(accCache);

		return commentDetail;
	}

	private com.google.gerrit.common.data.AccountInfo setAccountFromChangeInfo(ChangeInfo changeInfo) {
		Account account = new Account(new Account.Id(changeInfo.getOwner().getId()));
		account.setFullName(changeInfo.getOwner().getName());
		account.setUserName(changeInfo.getOwner().getUsername());
		account.setPreferredEmail(changeInfo.getOwner().getEmail());
		return new com.google.gerrit.common.data.AccountInfo(account);
	}

	private void adaptCommentDetails(Map<String, List<CommentInfo>> commentInfo, PatchSet.Id id,
			List<PatchLineComment> side, String file, CommentDetail commentDetail) {
		for (String filePath : commentInfo.keySet()) {
			if (!filePath.equals(file)) {
				continue;
			}
			List<CommentInfo> comments = commentInfo.get(filePath);
			for (CommentInfo comment : comments) {
				Patch.Key patchKey = new Patch.Key(id, filePath);
				PatchLineComment.Key patchLineCommentKey = new PatchLineComment.Key(patchKey, comment.getId());
				PatchLineComment lineComment = new PatchLineComment(patchLineCommentKey, comment.getLine(),
						new Account.Id(comment.getAuthor().getId()), null);
				populateLineComment(lineComment, comment);
				side.add(lineComment);
			}
		}
	}

	private void populateLineComment(PatchLineComment lineComment, CommentInfo commentInfo) {
		lineComment.setMessage(commentInfo.getMessage());
	}

	@Override
	protected List<ProjectDetailX> getProjectDetails(IProgressMonitor monitor, GerritConfig gerritConfig,
			List<Project> result) throws GerritException {
		if (!GerritVersion.isVersion2120OrLater(getVersion())) {
			return super.getProjectDetails(monitor, gerritConfig, result);
		}

		List<ProjectDetailX> projectDetails = adaptProjectDetails(gerritConfig, monitor);
		return projectDetails;
	}

	private List<ProjectDetailX> adaptProjectDetails(GerritConfig gerritConfig, IProgressMonitor monitor)
			throws GerritException {
		ConfigInfo configInfo = retrieveProjectConfigs(gerritConfig.getWildProject().get(), monitor);
		List<ProjectDetailX> projectDetails = new ArrayList<>();
		Map<String, ProjectInfo> listedProjects = listProjects(monitor);
		for (String projectName : listedProjects.keySet()) {
			ProjectDetailX projectDetail = new ProjectDetailX();
			ProjectAccessInfo accessRights = listAccessRights(projectName, monitor);
			projectDetail.setProject(adaptProject(configInfo, projectName, monitor));
			if (accessRights.getGroups() != null) {
				projectDetail.setGroups(adaptGroups(projectName, accessRights, monitor));
				projectDetail.setRights(adaptRights(projectName, accessRights, monitor));
			}
			if (accessRights.is_owner()) {
				projectDetail.setCanModifyDescription(true);
				projectDetail.setCanModifyMergeType(true);
				projectDetail.setCanModifyAgreements(true);
				projectDetail.setCanModifyAccess(true);
			}

			projectDetails.add(projectDetail);
		}
		return projectDetails;
	}

	private Project adaptProject(ConfigInfo configInfo, String projectName, IProgressMonitor monitor)
			throws GerritException {
		Project project = new Project(new Project.NameKey(projectName));
		project.setDescription(configInfo.getDescription());
		project.setUseContributorAgreements(configInfo.getUse_contributor_agreements().isValue());
		project.setUseSignedOffBy(configInfo.getUse_signed_off_by().isValue());
		project.setParent(new Project.NameKey(retrieveParentProject(projectName, monitor)));
		return project;

	}

	private Map<AccountGroup.Id, AccountGroup> adaptGroups(String projectName, ProjectAccessInfo accessRights,
			IProgressMonitor monitor) throws GerritException {
		Map<AccountGroup.Id, AccountGroup> groups = new HashMap<>();
		Map<String, GroupInfo> groupInfos = accessRights.getGroups();
		if (groupInfos != null) {
			for (String groupUuid : groupInfos.keySet()) {
				GroupInfo groupInfo = groupInfos.get(groupUuid);
				AccountGroup.Id groupId = new AccountGroup.Id(groupInfo.getGroup_id());
				String groupName = groupInfo.getName();
				if (groupInfo.getName() == null) {
					groupName = groupUuid;
				}
				groups.put(groupId, setAccountGroup(groupInfo, groupName, groupId, groupUuid));
			}
		}
		return groups;
	}

	private List<InheritedRefRight> adaptRights(String projectName, ProjectAccessInfo accessRights,
			IProgressMonitor monitor) throws GerritException {
		List<InheritedRefRight> inheritedRights = new ArrayList<>();
		Map<String, AccessSectionInfo> localAccessRights = accessRights.getLocal();
		for (String refName : localAccessRights.keySet()) {
			RefPattern refPattern = new RefPattern(refName);
			Map<String, PermissionInfo> accessSectionInfo = localAccessRights.get(refName).getPermissions();
			for (String permissionName : accessSectionInfo.keySet()) {
				ApprovalCategory.Id categoryId = new ApprovalCategory.Id(permissionName);
				Map<String, PermissionRuleInfo> rules = accessSectionInfo.get(permissionName).getRules();
				for (String groupUuid : rules.keySet()) {
					if (getGroupType(groupUuid).equals(AccountGroup.Type.INTERNAL)
							&& accessRights.getGroups().containsKey(groupUuid)) {
						AccountGroup.Id groupId = new AccountGroup.Id(
								accessRights.getGroups().get(groupUuid).getGroup_id());
						RefRight refRight = new RefRight(
								new RefRight.Key(new Project.NameKey(projectName), refPattern, categoryId, groupId)); // non internal groups dont have a group id
						boolean inherited = true;
						if (accessRights.getInherits_from() == null) {
							inherited = false;
						}
						InheritedRefRight inheritedrefRight = new InheritedRefRight(refRight, inherited,
								accessRights.is_owner());
						inheritedRights.add(inheritedrefRight);
					}
				}
			}
		}
		return inheritedRights;
	}

	private AccountGroup setAccountGroup(GroupInfo groupInfo, String groupName, AccountGroup.Id groupId,
			String groupUuid) {
		AccountGroup.NameKey nameKey = new AccountGroup.NameKey(groupName);
		AccountGroup accGroup = new AccountGroup(nameKey, groupId);
		accGroup.setDescription(groupInfo.getDescription());
		accGroup.setExternalNameKey(new AccountGroup.ExternalNameKey(groupName));
		accGroup.setNameKey(nameKey);
		accGroup.setOwnerGroupId(groupId);
		if (groupUuid != null) {
			accGroup.setType(getGroupType(groupUuid));
		}
		return accGroup;
	}

	private AccountGroup.Type getGroupType(String groupUuid) {
		if (groupUuid.matches("^[0-9a-f]{40}$")) { //$NON-NLS-1$
			return AccountGroup.Type.INTERNAL;
		} else if (groupUuid.startsWith("global")) { //$NON-NLS-1$
			return AccountGroup.Type.SYSTEM;
		} else if (groupUuid.startsWith("ldap")) { //$NON-NLS-1$
			return AccountGroup.Type.LDAP;
		} else {
			return AccountGroup.Type.INTERNAL;
		}
	}

	private SparseFileContent adaptSparseFileContent_A(DiffInfo diffInfo, IProgressMonitor monitor) {
		SparseFileContent sparseFileContent = new SparseFileContent();
		sparseFileContent.setSize(diffInfo.getMeta_a().getLines());
		sparseFileContent.setPath(diffInfo.getMeta_a().getName());
		sparseFileContent.setMissingNewlineAtEnd(false); // hardcoded, don't know how to get this from api
		adaptLineContent_A(sparseFileContent, diffInfo);
		return sparseFileContent;
	}

	private SparseFileContent adaptSparseFileContent_B(DiffInfo diffInfo, IProgressMonitor monitor) {
		SparseFileContent sparseFileContent = new SparseFileContent();
		sparseFileContent.setSize(diffInfo.getMeta_b().getLines());
		sparseFileContent.setPath(diffInfo.getMeta_b().getName());
		sparseFileContent.setMissingNewlineAtEnd(false); // hardcoded, don't know how to get this from api
		adaptLineContent_B(sparseFileContent, diffInfo);
		return sparseFileContent;
	}

	private void adaptLineContent_A(SparseFileContent sparseFileContent, DiffInfo diffInfo) {
		List<DiffContent> diffContent = diffInfo.getContent();
		int contentIdx = 0;
		for (DiffContent element : diffContent) {
			if (element.getAb() != null) { // add common content if this is the case
				addLinesAndConvertToStrings(sparseFileContent, element.getAb(), contentIdx);
				contentIdx += element.getAb().size();
			} else if (element.getA() != null) { // content only on side a (deleted in b)
				addLinesAndConvertToStrings(sparseFileContent, element.getA(), contentIdx);
				contentIdx += element.getA().size();
			}
		}
	}

	private void adaptLineContent_B(SparseFileContent sparseFileContent, DiffInfo diffInfo) {
		List<DiffContent> diffContent = diffInfo.getContent();
		int contentIdx = 0;
		for (DiffContent element : diffContent) {
			if (element.getAb() != null) { // add common content if this is the case
				addLinesAndConvertToStrings(sparseFileContent, element.getAb(), contentIdx);
				contentIdx += element.getAb().size();
			} else if (element.getB() != null) { // content only on side b (added in b)
				addLinesAndConvertToStrings(sparseFileContent, element.getB(), contentIdx);
				contentIdx += element.getB().size();
			}
		}
	}

	private void addLinesAndConvertToStrings(SparseFileContent sparseFileContent, ArrayList<String> ab, int idx) {
		for (String string : ab) {
			sparseFileContent.addLine(idx, string);
			idx++;
		}
	}

	private CommitInfo retrieveCommitInfo(PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		String commitQuery = String.format("/changes/%s/revisions/%s/commit", id.getParentKey().get(), id.get()); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(commitQuery, CommitInfo.class, monitor);
	}

	private Map<String, FileInfo> retrieveFileInfos(PatchSet.Id id, IProgressMonitor monitor) throws GerritException {
		String commitQuery = String.format("/changes/%s/revisions/%s/files", id.getParentKey().get(), id.get()); //$NON-NLS-1$
		@SuppressWarnings("serial")
		Type mapTypeToken = new com.google.common.reflect.TypeToken<Map<String, FileInfo>>() {
		}.getType();
		return getRestClient().executeGetRestRequest(commitQuery, mapTypeToken, monitor);
	}

	private AccountInfo retrieveAccountInfo(String accountId, IProgressMonitor monitor) throws GerritException {
		String accQuery = String.format("/accounts/%s", encode(accountId)); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(accQuery, AccountInfo.class, monitor);
	}

	private DiffInfo retrieveDiffInfoNotBase(PatchSet.Id targetId, PatchSet.Id baseId, String fileName,
			IProgressMonitor monitor) throws GerritException {
		String query = String.format("/changes/%s/revisions/%s/files/%s/diff?base=%s", targetId.getParentKey().get(), //$NON-NLS-1$
				targetId.get(), encode(fileName), baseId.get());
		return getRestClient().executeGetRestRequest(query, DiffInfo.class, monitor);
	}

	private DiffInfo retrieveDiffInfoAgainstBase(PatchSet.Id targetId, String fileName, IProgressMonitor monitor)
			throws GerritException {
		String query = String.format("/changes/%s/revisions/%s/files/%s/diff", targetId.getParentKey().get(), //$NON-NLS-1$
				targetId.get(), encode(fileName));
		return getRestClient().executeGetRestRequest(query, DiffInfo.class, monitor);
	}

	private Map<String, List<CommentInfo>> retrieveRevisionComments(PatchSet.Id id, IProgressMonitor monitor)
			throws GerritException {
		String query = String.format("/changes/%s/revisions/%s/comments/", id.getParentKey().get(), id.get()); //$NON-NLS-1$
		@SuppressWarnings("serial")
		Type mapTypeToken = new com.google.common.reflect.TypeToken<Map<String, List<CommentInfo>>>() {
		}.getType();
		return getRestClient().executeGetRestRequest(query, mapTypeToken, monitor);
	}

	private ConfigInfo retrieveProjectConfigs(String projectName, IProgressMonitor monitor) throws GerritException {
		String query = String.format("/projects/%s/config", encode(projectName)); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(query, ConfigInfo.class, monitor);
	}

	private Map<String, ProjectInfo> listProjects(IProgressMonitor monitor) throws GerritException {
		String query = String.format("/projects/?n=25"); //$NON-NLS-1$
		@SuppressWarnings("serial")
		Type mapTypeToken = new com.google.common.reflect.TypeToken<Map<String, ProjectInfo>>() {
		}.getType();
		return getRestClient().executeGetRestRequest(query, mapTypeToken, monitor);
	}

	private String retrieveParentProject(String projectName, IProgressMonitor monitor) throws GerritException {
		String query = String.format("/projects/%s/parent", encode(projectName)); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(query, String.class, monitor);
	}

	private ProjectAccessInfo listAccessRights(String projectName, IProgressMonitor monitor) throws GerritException {
		String query = String.format("/projects/%s/access", encode(projectName)); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(query, ProjectAccessInfo.class, monitor);
	}

	private DiffPreferencesInfo retrieveDiffPrefInfo(IProgressMonitor monitor) throws GerritException {
		String query = String.format("/accounts/self/preferences.diff"); //$NON-NLS-1$
		return getRestClient().executeGetRestRequest(query, DiffPreferencesInfo.class, monitor);
	}

	private UserIdentity toUserIdentity(GitPersonalInfo info, AccountInfo accInfo, IProgressMonitor monitor)
			throws GerritException {
		UserIdentity userIdentity = new UserIdentity();
		userIdentity.setAccount(new Account.Id(accInfo.getId()));
		userIdentity.setEmail(info.getEmail());
		userIdentity.setName(info.getName());
		userIdentity.setTimeZone(info.getTimeZoneOffset());
		userIdentity.setDate(parseTimeStamp(info.getDate()));

		return userIdentity;
	}

	private Timestamp parseTimeStamp(String date) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS"); //$NON-NLS-1$
			Date parsedDate = dateFormat.parse(date);
			return new java.sql.Timestamp(parsedDate.getTime());
		} catch (ParseException e) {
			StatusHandler.log(new Status(IStatus.ERROR, GerritCorePlugin.PLUGIN_ID, e.getMessage(), e));
			return null;
		}
	}
}
