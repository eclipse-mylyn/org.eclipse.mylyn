/**
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 */
package org.eclipse.mylyn.reviews.core.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.reviews.internal.core.model.FileItem;
import org.eclipse.mylyn.reviews.internal.core.model.ReviewsFactory;
import org.junit.Test;

/**
 * <!-- begin-user-doc --> A test case for the model object '<em><b>Review Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 * <li>
 * {@link org.eclipse.mylyn.reviews.core.model.IReviewItem#createComment(org.eclipse.mylyn.reviews.core.model.ILocation, java.lang.String)
 * <em>Create Comment Comment</em>}</li>
 * </ul>
 * </p>
 *
 * @generated NOT
 */
public class CommentContainerTest {

	@Test
	public void testGetAllCommentsFileItems() {
		IFileItem f1 = ReviewsFactory.eINSTANCE.createFileItem();
		IComment comment0 = ReviewsFactory.eINSTANCE.createComment();
		f1.getComments().add(comment0);
		IComment comment1 = ReviewsFactory.eINSTANCE.createComment();
		f1.getComments().add(comment1);
		f1.setBase(ReviewsFactory.eINSTANCE.createFileVersion());
		f1.setTarget(ReviewsFactory.eINSTANCE.createFileVersion());
		IComment comment2 = ReviewsFactory.eINSTANCE.createComment();
		f1.getBase().getComments().add(comment2);
		IComment comment3 = ReviewsFactory.eINSTANCE.createComment();
		f1.getTarget().getComments().add(comment3);
		assertThat(f1.getAllComments().size(), is(4));
		assertThat(f1.getAllComments().get(0), sameInstance(comment0));
		assertThat(f1.getAllComments().get(1), sameInstance(comment1));
		assertThat(f1.getAllComments().get(2), sameInstance(comment2));
		assertThat(f1.getAllComments().get(3), sameInstance(comment3));
	}

	@Test
	public void testGetAllCommentsReviewItems() {
		IReviewItem i1 = ReviewsFactory.eINSTANCE.createFileItem();
		i1.getComments().add(ReviewsFactory.eINSTANCE.createComment());
		i1.getComments().add(ReviewsFactory.eINSTANCE.createComment());
		assertThat(i1.getAllComments().size(), is(2));
	}

	@Test
	public void testGetAllCommentsReviewItemSet() {
		IReviewItemSet itemSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		IFileItem i1 = IReviewsFactory.INSTANCE.createFileItem();
		IComment t0 = ReviewsFactory.eINSTANCE.createComment();
		i1.getComments().add(t0);
		IComment t1 = ReviewsFactory.eINSTANCE.createComment();
		i1.getComments().add(t1);
		itemSet.getItems().add(i1);
		assertThat(itemSet.getAllComments().size(), is(2));

		IFileItem i2 = IReviewsFactory.INSTANCE.createFileItem();
		IComment t2 = ReviewsFactory.eINSTANCE.createComment();
		i2.getComments().add(t2);
		IComment t3 = ReviewsFactory.eINSTANCE.createComment();
		i2.getComments().add(t3);
		itemSet.getItems().add(i2);
		assertThat(itemSet.getAllComments().size(), is(4));
	}

	@Test
	public void testGetAllCommentsReview() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		IComment t0 = ReviewsFactory.eINSTANCE.createComment();
		review.getComments().add(t0);
		assertThat(review.getAllComments().size(), is(1));

		IReviewItemSet itemSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		review.getSets().add(itemSet);
		IFileItem i1 = IReviewsFactory.INSTANCE.createFileItem();
		IComment t1 = ReviewsFactory.eINSTANCE.createComment();
		i1.getComments().add(t1);
		IComment t2 = ReviewsFactory.eINSTANCE.createComment();
		i1.getComments().add(t2);
		itemSet.getItems().add(i1);
		assertThat(review.getAllComments().size(), is(3));

		IFileItem i2 = IReviewsFactory.INSTANCE.createFileItem();
		IComment t3 = ReviewsFactory.eINSTANCE.createComment();
		i2.getComments().add(t3);
		IComment t4 = ReviewsFactory.eINSTANCE.createComment();
		i2.getComments().add(t4);
		itemSet.getItems().add(i2);
		assertThat(review.getAllComments().size(), is(5));

		IReviewItemSet reviewSubSet = ReviewsFactory.eINSTANCE.createReviewItemSet();
		IFileItem i4 = IReviewsFactory.INSTANCE.createFileItem();
		IComment t5 = ReviewsFactory.eINSTANCE.createComment();
		i4.getComments().add(t5);
		t5.setId("5");
		reviewSubSet.getItems().add(i4);
		review.getSets().add(reviewSubSet);
		assertThat(review.getAllComments().size(), is(6));
		assertThat(review.getAllComments().get(0), is(t0));
		assertThat(review.getAllComments().get(1), is(t1));
		assertThat(review.getAllComments().get(2), is(t2));
		assertThat(review.getAllComments().get(3), is(t3));
		assertThat(review.getAllComments().get(4), is(t4));
		assertThat(review.getAllComments().get(5), is(t5));

	}

	@Test
	public void testGetAllCommentReplies() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		IComment mainComment = ReviewsFactory.eINSTANCE.createComment();
		review.getComments().add(mainComment);
		List<IComment> allComments = review.getAllComments();
		assertThat(allComments.size(), is(1));

		IComment comment0 = ReviewsFactory.eINSTANCE.createComment();
		review.getComments().add(comment0);
		mainComment.getReplies().add(comment0);

		allComments = review.getAllComments();
		assertThat(allComments.size(), is(2));
		assertThat(allComments.get(0), is(mainComment));
		assertThat(allComments.get(1), is(comment0));
	}

	@Test
	public void testCreateFileItemComment() {
		IRepository repos = ReviewsFactory.eINSTANCE.createRepository();
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		repos.getReviews().add(review);
		IReviewItemSet set = ReviewsFactory.eINSTANCE.createReviewItemSet();
		review.getSets().add(set);
		IFileItem item = ReviewsFactory.eINSTANCE.createFileItem();
		set.getItems().add(item);
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		IUser fileAuthor = ReviewsFactory.eINSTANCE.createUser();
		fileAuthor.setDisplayName("Another User");
		item.setAddedBy(fileAuthor);
		IUser account = ReviewsFactory.eINSTANCE.createUser();
		account.setDisplayName("This User");
		repos.setAccount(account);

		IComment comment = item.createComment(location, "My Comment");
		assertThat(comment.getItem(), instanceOf(IFileItem.class));
		assertThat(comment.getReview(), sameInstance(review));
		assertThat(((FileItem) comment.getItem()).getReview(), sameInstance(review));
		assertThat(((FileItem) comment.getItem()).getReview().getRepository(), sameInstance(repos));
		assertTrue(item.getComments().contains(comment));
		assertSame(comment.getItem(), item);
		assertThat(comment.getDescription(), is("My Comment"));
		assertThat(comment.getAuthor().getDisplayName(), is("This User"));
		assertTrue(new Date().getTime() - 100 < comment.getCreationDate().getTime());
		assertThat(comment.isMine(), is(true));
	}

	@Test
	public void testCreateFileItemCommentNoUser() {
		IFileItem item = ReviewsFactory.eINSTANCE.createFileItem();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		IComment comment = item.createComment(location, "My Comment");
		assertThat(item.getComments().size(), is(1));
		assertThat(comment.getAuthor(), nullValue());
		assertThat(comment.isMine(), is(false));
	}

	@Test
	public void testCreateReviewCommentNoUser() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		ILineLocation location = ReviewsFactory.eINSTANCE.createLineLocation();
		IComment comment = review.createComment(location, "My Comment");
		assertThat(review.getComments().size(), is(1));
		assertNull(comment.getAuthor());
	}

	@Test
	public void testGetReviewFileItemComment() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		IReviewItemSet set = ReviewsFactory.eINSTANCE.createReviewItemSet();
		review.getSets().add(set);
		IFileItem item = ReviewsFactory.eINSTANCE.createFileItem();
		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		set.getItems().add(item);
		item.getComments().add(comment);
		assertThat(comment.getReview(), sameInstance(review));
	}

	@Test
	public void testGetReviewComment() {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		review.getComments().add(comment);
		assertThat(comment.getReview(), sameInstance(review));
	}

	@Test
	public void testIsMineForRepositoryWithoutAccountAndCommentWithoutAuthor() {
		IComment comment = createReviewWithComment(createRepository());

		assertThat(comment.isMine(), is(false));
	}

	@Test
	public void testIsMineForRepositoryAndCommentWithTheSameUser() {
		IUser user = createUser();
		IRepository repo = createRepositoryWithAccount(user);
		IComment comment = createReviewWithCommentAndAuthor(repo, user);

		assertThat(comment.isMine(), is(true));
	}

	//the following tests should be removed when https://bugs.eclipse.org/bugs/show_bug.cgi?id=418878 is fixed.
	@Test
	public void testIsMineForRepositoryAndCommentWithUsersHavingTheSameEmail() {
		IUser user1 = createUser("foo@bar.com");
		IUser user2 = createUser("foo@bar.com");
		IRepository repo = createRepositoryWithAccount(user1);
		IComment comment = createReviewWithCommentAndAuthor(repo, user2);

		assertThat(comment.isMine(), is(true));
	}

	@Test
	public void testIsMineForRepositoryWithAccountAndCommentWithoutAuthor() {
		IUser user = createUser("foo@bar.com");
		IRepository repo = createRepositoryWithAccount(user);
		IComment comment = createReviewWithComment(repo);

		assertThat(comment.isMine(), is(false));
	}

	@Test
	public void testIsMineForRepositoryAndCommentWithUsersHavingTheDifferentEmails() {
		IUser user1 = createUser("foo@bar.com");
		IUser user2 = createUser("baz@bar.com");
		IRepository repo = createRepositoryWithAccount(user1);
		IComment comment = createReviewWithCommentAndAuthor(repo, user2);

		assertThat(comment.isMine(), is(false));
	}

	@Test
	public void testIsMineForRepositoryWithoutAccountAndCommentWithAuthor() {
		IUser user = createUser("foo@bar.com");
		IRepository repo = createRepository();
		IComment comment = createReviewWithCommentAndAuthor(repo, user);

		assertThat(comment.isMine(), is(false));
	}

	@Test
	public void testIsMineForRepositoryWithAccountAndNullEmailAndCommentWithAuthor() {
		IUser user1 = createUser(null);
		IUser user2 = createUser("baz@bar.com");
		IRepository repo = createRepositoryWithAccount(user1);
		IComment comment = createReviewWithCommentAndAuthor(repo, user2);

		assertThat(comment.isMine(), is(false));
	}

	private static IRepository createRepository() {
		return ReviewsFactory.eINSTANCE.createRepository();
	}

	private static IRepository createRepositoryWithAccount(IUser user) {
		IRepository repo = createRepository();
		repo.setAccount(user);
		return repo;
	}

	private static IComment createReviewWithComment(IRepository repos) {
		IReview review = ReviewsFactory.eINSTANCE.createReview();
		repos.getReviews().add(review);
		IReviewItemSet set = ReviewsFactory.eINSTANCE.createReviewItemSet();
		review.getSets().add(set);
		IFileItem item = ReviewsFactory.eINSTANCE.createFileItem();
		IComment comment = ReviewsFactory.eINSTANCE.createComment();
		set.getItems().add(item);
		item.getComments().add(comment);
		return comment;
	}

	private static IComment createReviewWithCommentAndAuthor(IRepository repos, IUser user) {
		IComment comment = createReviewWithComment(repos);
		comment.setAuthor(user);
		return comment;
	}

	private static IUser createUser() {
		return ReviewsFactory.eINSTANCE.createUser();
	}

	private static IUser createUser(String email) {
		IUser user = createUser();
		user.setEmail(email);
		return user;
	}

}
