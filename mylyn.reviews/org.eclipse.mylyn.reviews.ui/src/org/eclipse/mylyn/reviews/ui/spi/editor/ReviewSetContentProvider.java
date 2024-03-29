package org.eclipse.mylyn.reviews.ui.spi.editor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

final class ReviewSetContentProvider implements ITreeContentProvider {

	private final MultiValuedMap<ILocation, IComment> threads = new ArrayListValuedHashMap<>();

	private final MultiValuedMap<String, ILocation> threadLocationsByFile = new ArrayListValuedHashMap<>();

	@Override
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
		if (parentElement instanceof IFileItem file) {
			MultiValuedMap<Long, IComment> commentsByLocation = new ArrayListValuedHashMap<>();
			for (IComment comment : getFileComments(file)) {
				commentsByLocation.put(comment.getLocations().iterator().next().getIndex(), comment);
			}

			for (ILocation location : threadLocationsByFile.get(file.getId())) {
				threads.remove(location);
			}
			threadLocationsByFile.remove(file.getId());

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
		return (o1, o2) -> (int) (o1.getIndex() - o2.getIndex());
	}

	private List<IFileItem> getReviewItems(Object inputElement) {
		if (inputElement instanceof IReviewItemSet) {
			return ((IReviewItemSet) inputElement).getItems();
		}
		return List.of();
	}

	private List<IComment> getFileComments(Object inputElement) {
		if (inputElement instanceof IFileItem) {
			return ((IFileItem) inputElement).getAllComments()
					.stream()
					.filter(input -> input.getLocations().iterator().hasNext())
					.collect(Collectors.toUnmodifiableList());
		}
		return List.of();
	}
}