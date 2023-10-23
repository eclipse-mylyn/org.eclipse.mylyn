/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.reviews.ui.compare.FileItemCompareEditorInput;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemCompareEditorInput;
import org.eclipse.mylyn.internal.reviews.ui.compare.ReviewItemSetCompareEditorInput;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class GerritCompareUi {

	public static CompareEditorInput getReviewItemSetComparisonEditor(ReviewItemSetCompareEditorInput editorInput,
			IReviewItemSet items, String taskId) {
		return getComparisonEditor(editorInput, getReviewItemSetComparePredicate(items, taskId));
	}

	public static void openCompareEditor(CompareEditorInput input) {
		CompareUI.openCompareEditor(input);
	}

	public static void openFileComparisonEditor(CompareConfiguration configuration, IFileItem item,
			ReviewBehavior behavior) {
		openFileComparisonEditor(configuration, item, behavior, (IComment) null);
	}

	public static void openFileComparisonEditor(CompareConfiguration configuration, IFileItem item,
			ReviewBehavior behavior, IStructuredSelection selection) {
		IComment comment = null;
		if (selection != null && selection.getFirstElement() instanceof IComment) {
			comment = (IComment) selection.getFirstElement();
		}
		openFileComparisonEditor(configuration, item, behavior, comment);
	}

	public static void openFileComparisonEditor(CompareConfiguration configuration, IFileItem item,
			ReviewBehavior behavior, IComment comment) {
		CompareEditorInput editorInput = new FileItemCompareEditorInput(configuration, item, behavior);
		CompareEditorInput newInput = getComparisonEditor(editorInput, getFileComparePredicate(item));
		openCompareEditor(newInput);
		if (comment != null && newInput instanceof ReviewItemCompareEditorInput) {
			((ReviewItemCompareEditorInput) newInput).gotoComment(comment);
		}
	}

	private static CompareEditorInput getComparisonEditor(CompareEditorInput editorInput,
			Predicate<CompareEditorInput> function) {
		IEditorReference[] editorReferences = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage()
				.getEditorReferences();
		return getComparisonEditorInput(editorReferences, editorInput, function);
	}

	static CompareEditorInput getComparisonEditorInput(IEditorReference[] editorReferences,
			CompareEditorInput editorInput, Predicate<CompareEditorInput> predicate) {
//		return FluentIterable.from(Lists.newArrayList(editorReferences)).filter(new Predicate<IEditorReference>() {
//			public boolean apply(IEditorReference ref) {
//				return ref.getId().equals("org.eclipse.compare.CompareEditor"); //$NON-NLS-1$
//			}
//		}).transform(new Function<IEditorReference, CompareEditorInput>() {
//			public CompareEditorInput apply(IEditorReference reference) {
//				try {
//					return (CompareEditorInput) reference.getEditorInput();
//				} catch (PartInitException e) {
//					handleError(e);
//				}
//				return null;
//			}
//		}).firstMatch(predicate).or(editorInput);

		Optional<CompareEditorInput> input = Stream.of(editorReferences)
				.filter(ref -> ref.getId().equals("org.eclipse.compare.CompareEditor")) //$NON-NLS-1$
				.map(new Function<IEditorReference, CompareEditorInput>() {
					public CompareEditorInput apply(IEditorReference reference) {
						try {
							return (CompareEditorInput) reference.getEditorInput();
						} catch (PartInitException e) {
							handleError(e);
						}
						return null;
					}
				})
				.filter(predicate)
				.findFirst();
		if (input.isPresent()) {
			return input.get();
		} else {
			return editorInput;
		}
	}

	static Predicate<CompareEditorInput> getFileComparePredicate(final IFileItem item) {
		return existingEditorInput -> existingEditorInput instanceof FileItemCompareEditorInput
				? (((FileItemCompareEditorInput) existingEditorInput).getFileItemId().equals(item.getId()))
				: false;
	}

	static Predicate<CompareEditorInput> getReviewItemSetComparePredicate(final IReviewItemSet itemSet,
			final String taskId) {
		return existingEditorInput -> existingEditorInput instanceof ReviewItemSetCompareEditorInput
				? (((ReviewItemSetCompareEditorInput) existingEditorInput).getName().equals(itemSet.getName())
						&& taskId.equals(((ReviewItemSetCompareEditorInput) existingEditorInput).getItemTaskId()))
				: false;
	}

	protected static void handleError(PartInitException error) {
		StatusHandler.log(new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID,
				"There was an error restoring the editor input.", error)); //$NON-NLS-1$
	}

}