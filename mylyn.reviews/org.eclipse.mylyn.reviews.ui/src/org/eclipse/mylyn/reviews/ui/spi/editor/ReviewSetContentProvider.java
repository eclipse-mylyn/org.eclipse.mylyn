package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

final class ReviewSetContentProvider implements ITreeContentProvider {

	private final Multimap<ILocation, IComment> threads = LinkedHashMultimap.create();

	private final Multimap<String, ILocation> threadLocationsByFile = LinkedHashMultimap.create();

	public Object[] getElements(Object inputElement) {
		return getReviewItems(inputElement).toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public void dispose() {
		threads.clear();
		threadLocationsByFile.clear();
	}

	@Override
	public boolean hasChildren(Object element) {
		return !getFileComments(element).isEmpty() || element instanceof ILocation;
	}

	@Override
	public Object getParent(Object element) {
		// unsupported
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IFileItem) {
			IFileItem file = (IFileItem) parentElement;
			Multimap<Long, IComment> commentsByLocation = Multimaps.index(getFileComments(file), commentLineNumber());

			for (ILocation location : threadLocationsByFile.get(file.getId())) {
				threads.removeAll(location);
			}
			threadLocationsByFile.removeAll(file.getId());

			for (Long line : commentsByLocation.keySet()) {
				ILocation location = null;
				for (IComment comment : commentsByLocation.get(line)) {
					if (location == null) {
						location = comment.getLocations().iterator().next();
						threadLocationsByFile.put(file.getId(), location);
					}
					if (!threads.containsValue(comment)) {
						threads.put(location, comment);
					}
				}
			}
			Collection<ILocation> relevantThreads = threadLocationsByFile.get(file.getId());
			ILocation[] locations = relevantThreads.toArray(new ILocation[relevantThreads.size()]);
			Arrays.sort(locations, lineNumberComparator());
			return locations;
		} else if (parentElement instanceof ILocation) {
			return threads.get((ILocation) parentElement).toArray();
		}
		return new Object[0];
	}

	private Comparator<ILocation> lineNumberComparator() {
		return new Comparator<ILocation>() {

			@Override
			public int compare(ILocation o1, ILocation o2) {
				return (int) (o1.getIndex() - o2.getIndex());
			}
		};
	}

	private Function<? super IComment, Long> commentLineNumber() {
		return new Function<IComment, Long>() {

			@Override
			public Long apply(IComment comment) {
				return comment.getLocations().iterator().next().getIndex();
			}
		};
	}

	private Predicate<IComment> hasLocation() {
		return new Predicate<IComment>() {

			@Override
			public boolean apply(IComment input) {
				return input.getLocations().iterator().hasNext();
			}
		};
	}

	private List<IFileItem> getReviewItems(Object inputElement) {
		if (inputElement instanceof IReviewItemSet) {
			return ((IReviewItemSet) inputElement).getItems();
		}
		return ImmutableList.of();
	}

	private List<IComment> getFileComments(Object inputElement) {
		if (inputElement instanceof IFileItem) {
			return FluentIterable.from(((IFileItem) inputElement).getAllComments()).filter(hasLocation()).toList();
		}
		return ImmutableList.of();
	}
}