package org.eclipse.mylar.tasklist.report.ui;

import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.mylar.tasklist.contribution.DatePicker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ReminderCellEditor extends DialogCellEditor {

	private Date reminderDate;
	public ReminderCellEditor(Composite parent) {
		super(parent, SWT.NONE);
	}
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		ReminderDialog dialog = new ReminderDialog(cellEditorWindow.getShell());
		dialog.open();
		reminderDate = dialog.getDate();
        return reminderDate;
	}
	
	public Date getReminderDate() {
		return reminderDate;
	}
	private class ReminderDialog extends Dialog {

		private Date reminderDate = null;
		protected ReminderDialog(Shell parentShell) {
			super(parentShell);
		}

		protected ReminderDialog(IShellProvider parentShell) {
			super(parentShell);
		}
		
		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			final DatePicker datePicker = new DatePicker(composite, SWT.NULL);	
	        datePicker.setDateText("<reminder>");
			datePicker.addPickerSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent arg0) {
					reminderDate = datePicker.getDate().getTime();
				}

				public void widgetDefaultSelected(SelectionEvent arg0) {
					// ignore
				}
			});
			return composite;
		}
		public Date getDate() {
			return reminderDate;
		}
	}
}
