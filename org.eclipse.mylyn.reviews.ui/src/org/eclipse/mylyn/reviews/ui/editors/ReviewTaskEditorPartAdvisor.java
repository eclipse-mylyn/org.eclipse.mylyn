package org.eclipse.mylyn.reviews.ui.editors;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.ReviewDataManager;
import org.eclipse.mylyn.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.ITaskEditorPartDescriptorAdvisor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class ReviewTaskEditorPartAdvisor implements
		ITaskEditorPartDescriptorAdvisor {

	public boolean canCustomize(ITask task) {
		boolean isReview = Boolean.parseBoolean(task.getAttribute(ReviewConstants.ATTR_REVIEW_FLAG));
		return isReview;
	}

	public Set<String> getBlockingIds(ITask task) {
		return Collections.emptySet();
	}

	public Set<String> getBlockingPaths(ITask task) {
		Set<String> blockedPaths = new HashSet<String>();
		blockedPaths.add(AbstractTaskEditorPage.PATH_ATTRIBUTES);
		blockedPaths.add(AbstractTaskEditorPage.PATH_COMMENTS);
		blockedPaths.add(AbstractTaskEditorPage.PATH_ATTACHMENTS);
		blockedPaths.add(AbstractTaskEditorPage.PATH_PLANNING);
		
		return blockedPaths;
	}

	public Set<TaskEditorPartDescriptor> getPartContributions(ITask task) {
		Set<TaskEditorPartDescriptor> parts = new HashSet<TaskEditorPartDescriptor>();
		parts.add(new TaskEditorPartDescriptor(
				ReviewTaskEditorPart.ID_PART_REVIEW) {

			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewTaskEditorPart();
			}
		});
		return parts;
	}

}
