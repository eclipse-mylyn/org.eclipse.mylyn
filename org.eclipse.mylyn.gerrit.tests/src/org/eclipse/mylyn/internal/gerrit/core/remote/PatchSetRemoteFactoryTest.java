/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.eclipse.mylyn.internal.gerrit.core.remote.TestRemoteObserverConsumer.retrieveForLocalKey;
import static org.eclipse.mylyn.internal.gerrit.core.remote.TestRemoteObserverConsumer.retrieveForRemoteKey;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritChange;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.core.client.PatchSetContent;
import org.eclipse.mylyn.internal.gerrit.core.client.compat.PatchScriptX;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.google.gerrit.common.data.PatchSetDetail;
import com.google.gerrit.reviewdb.ApprovalCategoryValue;
import com.google.gerrit.reviewdb.Change;
import com.google.gerrit.reviewdb.Patch;
import com.google.gerrit.reviewdb.Patch.Key;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Miles Parker
 */
public class PatchSetRemoteFactoryTest extends GerritRemoteTest {

	@Test
	public void testPatchSetFiles() throws Exception {
		CommitCommand command2 = reviewHarness.createCommitCommand();
		reviewHarness.addFile("testFile2.txt");
		reviewHarness.addFile("testFile3.txt");
		reviewHarness.commitAndPush(command2);
		CommitCommand command3 = reviewHarness.createCommitCommand();
		reviewHarness.addFile("testFile2.txt", "testmod");
		reviewHarness.addFile("testFile4.txt");
		reviewHarness.addFile("testFile5.txt");
		reviewHarness.commitAndPush(command3);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse();

		assertThat(getReview().getSets().size(), is(3));
		IReviewItemSet testPatchSet = getReview().getSets().get(2);
		PatchSetDetail detail = retrievePatchSetDetail("3");
		assertThat(detail.getInfo().getKey().get(), is(3));

		List<IFileItem> fileItems = testPatchSet.getItems();
		assertThat(fileItems.size(), is(0));
		retrievePatchSetContents(testPatchSet);

		assertThat(fileItems.size(), is(6));
		for (IReviewItem fileItem : fileItems) {
			assertThat(fileItem, instanceOf(IFileItem.class));
			assertThat(fileItem.getAddedBy().getDisplayName(), is("tests"));
			assertThat(fileItem.getCommittedBy().getDisplayName(), is("tests"));
		}
		IFileItem fileItem0 = fileItems.get(0);
		assertThat(fileItem0.getName(), is("/COMMIT_MSG"));

		IFileItem fileItem1 = fileItems.get(1);
		assertThat(fileItem1.getName(), is("testFile1.txt"));

		IFileItem fileItem2 = fileItems.get(2);
		assertThat(fileItem2.getName(), is("testFile2.txt"));

		IFileVersion base2 = fileItem2.getBase();
		assertThat(base2.getAddedBy(), nullValue());
		assertThat(base2.getCommittedBy(), nullValue());
		assertThat(base2.getContent(), is(""));
		assertThat(base2.getId(), is("base-" + reviewHarness.shortId + ",3,testFile2.txt"));
		assertThat(base2.getName(), is("testFile2.txt"));
		assertThat(base2.getPath(), nullValue());
		assertThat(base2.getReference(), nullValue());
		assertThat(base2.getDescription(), is("Base"));

		IFileVersion target2 = fileItem2.getTarget();
		assertThat(target2.getAddedBy().getDisplayName(), is("tests"));
		assertThat(target2.getCommittedBy().getDisplayName(), is("tests"));
		assertThat(target2.getContent(), is("testmod"));
		assertThat(target2.getId(), is(reviewHarness.shortId + ",3,testFile2.txt"));
		assertThat(target2.getName(), is("testFile2.txt"));
		assertThat(target2.getPath(), is("testFile2.txt"));
		assertThat(target2.getReference(), nullValue());
		assertThat(target2.getDescription(), is("Patch Set 3"));
	}

	@Test
	public void testFetchBinaryContent() throws Exception {
		fetchBinaryContent("test.png", "testdata/binary/gerrit.png");
	}

	@Test
	public void testFetchZippedBinaryContent() throws Exception {
		// test servers are configured so that gif files are zipped (the mimetype is not marked safe)
		fetchBinaryContent("test.gif", "testdata/binary/gerrit.gif");
	}

	private void fetchBinaryContent(String fileName, String path) throws Exception {
		byte[] fileContent = commitFile(fileName, path);

		assertThat(getReview().getSets().size(), is(2));

		PatchSetDetail detail = retrievePatchSetDetail(Integer.toString(2));
		assertThat(detail.getInfo().getKey().get(), is(2));

		IReviewItemSet patchSet = getReview().getSets().get(1);
		List<IFileItem> fileItems = patchSet.getItems();
		assertThat(fileItems.size(), is(0));
		retrievePatchSetContents(patchSet);
		assertThat(fileItems.size(), is(3));

		IFileItem fileItem = fileItems.get(1);
		assertThat(fileItem.getName(), is(fileName));
		assertThat(fileItem.getBase().getContent(), nullValue());
		assertThat(fileItem.getBase().getBinaryContent(), nullValue());
		assertThat(fileItem.getTarget().getContent(), nullValue());
		assertThat(fileItem.getTarget().getBinaryContent(), is(fileContent));
	}

	@Test
	public void testUnzipBinaryContent() throws Exception {
		File imageFile = CommonTestUtil.getFile(this, "testdata/binary/gerrit.png");
		byte[] zippedBytes = zip(imageFile);
		assertThat(GerritClient.isZippedContent(zippedBytes), is(true));

		byte[] unzippedBytes = GerritClient.unzip(zippedBytes);
		byte[] imageBytes = FileUtils.readFileToByteArray(imageFile);
		assertThat(GerritClient.isZippedContent(imageBytes), is(false));
		assertThat(unzippedBytes, is(imageBytes));
	}

	private byte[] zip(File file) throws IOException, FileNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(bytes);
		out.putNextEntry(new ZipEntry(file.getName()));
		IOUtils.copy(new FileInputStream(file), out);
		out.closeEntry();
		return bytes.toByteArray();
	}

	@Test
	public void testCompareBinaryContent() throws Exception {
		String fileName = "test.png";
		byte[] fileContent2 = commitFile(fileName, "testdata/binary/gerrit.png");
		byte[] fileContent3 = commitFile(fileName, "testdata/binary/gerrit2.png");
		removeFile(fileName);

		PatchSetDetail detail1 = retrievePatchSetDetail("1");
		PatchSetDetail detail2 = retrievePatchSetDetail("2");
		PatchSetDetail detail3 = retrievePatchSetDetail("3");
		PatchSetDetail detail4 = retrievePatchSetDetail("4");

		// compare modified images
		PatchScriptX patchScript = loadPatchSetContent(fileName, detail2, detail3);
		assertPatchContent(patchScript, equalTo(fileContent2), equalTo(fileContent3));

		patchScript = loadPatchSetContent(fileName, detail3, detail2);
		assertPatchContent(patchScript, equalTo(fileContent3), equalTo(fileContent2));

		// compare deleted image
		patchScript = loadPatchSetContent(fileName, detail2, detail4);
		boolean isVersion29OrLater = reviewHarness.client.isVersion29OrLater(new NullProgressMonitor());
		if (isVersion29OrLater) {
			//In Gerrit 2.9, if the file (test.png)is not in the target environment (Detail 4), it returns NULL
			assertThat(patchScript, nullValue());
		} else {
			assertPatchContent(patchScript, equalTo(fileContent2), empty());
		}

		patchScript = loadPatchSetContent(fileName, detail3, detail1);
		if (isVersion29OrLater) {
			//In Gerrit 2.9, if the file (test.png) is not in the target environment (Detail 1), it returns NULL
			assertThat(patchScript, nullValue());
		} else {
			assertPatchContent(patchScript, equalTo(fileContent3), empty());
		}

		// compare added image
		patchScript = loadPatchSetContent(fileName, detail1, detail2);
		assertPatchContent(patchScript, empty(), equalTo(fileContent2));

		patchScript = loadPatchSetContent(fileName, detail4, detail3);
		assertPatchContent(patchScript, empty(), equalTo(fileContent3));
	}

	private PatchScriptX loadPatchSetContent(String fileName, PatchSetDetail base, PatchSetDetail target)
			throws GerritException {
		PatchSetContent content = new PatchSetContent(base.getPatchSet(), target.getPatchSet());
		reviewHarness.client.loadPatchSetContent(content, new NullProgressMonitor());
		return content.getPatchScript(createPatchKey(fileName, target.getInfo().getKey().get()));
	}

	private Key createPatchKey(String fileName, int patchSet) throws GerritException {
		Change.Id changeId = new Change.Id(reviewHarness.client.id(reviewHarness.shortId));
		PatchSet.Id patchSetId = new PatchSet.Id(changeId, patchSet);
		return new Patch.Key(patchSetId, fileName);
	}

	private void assertPatchContent(PatchScriptX patchScript, Matcher<byte[]> base, Matcher<byte[]> target) {
		assertThat(patchScript.getBinaryA(), is(base));
		assertThat(patchScript.getBinaryB(), is(target));
	}

	private static Matcher<byte[]> empty() {
		return nullValue(byte[].class);
	}

	private byte[] commitFile(String fileName, String path) throws Exception {
		File file = CommonTestUtil.getFile(this, path);
		CommitCommand command = reviewHarness.createCommitCommand();
		reviewHarness.addFile(fileName, file);
		reviewHarness.commitAndPush(command);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse();
		return FileUtils.readFileToByteArray(file);
	}

	private void removeFile(String fileName) throws Exception {
		CommitCommand command = reviewHarness.createCommitCommand();
		reviewHarness.removeFile(fileName);
		reviewHarness.commitAndPush(command);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse();
	}

	@Test
	public void testPatchSetComments() throws Exception {
		CommitCommand command2 = reviewHarness.createCommitCommand();
		reviewHarness.addFile("testComments.txt", "line1\nline2\nline3\nline4\nline5\nline6\nline7\n");
		reviewHarness.commitAndPush(command2);
		reviewHarness.consumer.retrieve(false);
		reviewHarness.listener.waitForResponse();
		PatchSetDetail detail = retrievePatchSetDetail("2");
		assertThat(detail.getInfo().getKey().get(), is(2));

		IReviewItemSet testPatchSet = getReview().getSets().get(1);
		TestRemoteObserverConsumer<IReviewItemSet, List<IFileItem>, String, PatchSetContent, String, Long> patchSetObserver //
		= retrievePatchSetContents(testPatchSet);

		IFileItem commentFile = testPatchSet.getItems().get(1);
		assertThat(commentFile.getName(), is("testComments.txt"));
		assertThat(commentFile.getAllComments().size(), is(0));

		String id = commentFile.getReference();
		reviewHarness.client.saveDraft(Patch.Key.parse(id), "Line 2 Comment", 2, (short) 1, null, null,
				new NullProgressMonitor());
		patchSetObserver.retrieve(false);
		patchSetObserver.waitForResponse();

		commentFile = testPatchSet.getItems().get(1);
		List<IComment> allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(1));
		IComment fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(true));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment"));

		reviewHarness.client.deleteDraft(Patch.Key.parse(id), fileComment.getId(), new NullProgressMonitor());

		commentFile = testPatchSet.getItems().get(0);
		allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(0));

		reviewHarness.client.saveDraft(Patch.Key.parse(id), "Line 2 Comment", 2, (short) 1, null, null,
				new NullProgressMonitor());
		patchSetObserver.retrieve(false);
		patchSetObserver.waitForResponse();

		commentFile = testPatchSet.getItems().get(1);
		allComments = commentFile.getAllComments();
		fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(true));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment"));

		reviewHarness.client.saveDraft(Patch.Key.parse(id), "Line 2 Comment modified", 2, (short) 1, null,
				fileComment.getId(), new NullProgressMonitor());
		patchSetObserver.retrieve(false);
		patchSetObserver.waitForResponse();

		commentFile = testPatchSet.getItems().get(1);
		allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(1));
		fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(true));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment modified"));

		reviewHarness.client.publishComments(reviewHarness.shortId, 2, "Submit Comments",
				Collections.<ApprovalCategoryValue.Id> emptySet(), new NullProgressMonitor());
		patchSetObserver.retrieve(false);
		patchSetObserver.waitForResponse();
		allComments = commentFile.getAllComments();
		assertThat(allComments.size(), is(1));
		fileComment = allComments.get(0);
		assertThat(fileComment, notNullValue());
		assertThat(fileComment.isDraft(), is(false));
		assertThat(fileComment.getAuthor().getDisplayName(), is("tests"));
		assertThat(fileComment.getDescription(), is("Line 2 Comment modified"));
	}

	@Test
	public void testLoadPatchSet() throws Exception {
		// given
		GerritChange change = reviewHarness.client.getChange(reviewHarness.shortId, new NullProgressMonitor());
		List<PatchSetDetail> details = change.getPatchSetDetails();
		assertThat(details, notNullValue());
		assertThat(details.size(), is(1));
		PatchSetDetail detail = details.get(0);
		PatchSetContent content = new PatchSetContent((PatchSet) null, detail);

		final Patch.Key commitMsgPatchKey = createPatchKey("/COMMIT_MSG", 1);
		final Patch.Key testFilePatchKey = createPatchKey("testFile1.txt", 1);

		assertThat(content.getTargetDetail(), notNullValue());
		assertThat(content.getPatchScript(commitMsgPatchKey), nullValue());
		assertThat(content.getPatchScript(testFilePatchKey), nullValue());

		// when
		reviewHarness.client.loadPatchSetContent(content, new NullProgressMonitor());

		// then
		assertThat(content.getPatchScript(commitMsgPatchKey), notNullValue());
		assertThat(content.getPatchScript(testFilePatchKey), notNullValue());
	}

	private PatchSetDetail retrievePatchSetDetail(String patchSetId) {
		TestRemoteObserverConsumer<IReview, IReviewItemSet, String, PatchSetDetail, PatchSetDetail, String> itemSetObserver //
		= retrieveForLocalKey(reviewHarness.provider.getReviewItemSetFactory(), getReview(), patchSetId, false);
		PatchSetDetail detail = itemSetObserver.getRemoteObject();
		return detail;
	}

	private TestRemoteObserverConsumer<IReviewItemSet, List<IFileItem>, String, PatchSetContent, String, Long> retrievePatchSetContents(
			IReviewItemSet patchSet) {
		return retrieveForRemoteKey(reviewHarness.provider.getReviewItemSetContentFactory(), patchSet,
				patchSet.getId(), true);
	}
}
