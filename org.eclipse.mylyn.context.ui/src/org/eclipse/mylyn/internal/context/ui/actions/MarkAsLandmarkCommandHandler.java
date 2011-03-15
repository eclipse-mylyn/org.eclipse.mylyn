package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

public class MarkAsLandmarkCommandHandler extends AbstractHandler {

	private final InterestIncrementAction action = new InterestIncrementAction();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println(HandlerUtil.getCurrentSelection(event));
		action.selectionChanged(null, HandlerUtil.getCurrentSelection(event));
		action.run(null);
		return null;
	}

}