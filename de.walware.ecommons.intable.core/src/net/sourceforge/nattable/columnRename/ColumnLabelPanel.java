package net.sourceforge.nattable.columnRename;

import net.sourceforge.nattable.style.editor.AbstractEditorPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ColumnLabelPanel extends AbstractEditorPanel<String> {
	
	private Text textField;
	
	private final String columnLabel;
	private final String newColumnLabel;
	
	
	public ColumnLabelPanel(Composite parent, String columnLabel, String newColumnLabel) {
		super(parent, SWT.NONE);
		this.columnLabel = columnLabel;
		this.newColumnLabel = newColumnLabel;
		init();
	}
	
	
	private void init() {
		GridLayout gridLayout = new GridLayout(2, false);
		setLayout(gridLayout);
		
		// Original label
		Label label = new Label(this, SWT.NONE);
		label.setText("Original");
		
		Label originalLabel = new Label(this, SWT.NONE);
		originalLabel.setText(columnLabel);
		
		// Text field for new label
		Label renameLabel = new Label(this, SWT.NONE);
		renameLabel.setText("Rename");
		
		textField = new Text(this, SWT.BORDER);
		GridData gridData = new GridData(200, 15);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		textField.setLayoutData(gridData);
		
		if (newColumnLabel != null) {
			textField.setText(newColumnLabel);
		}
	}
	
	@Override
	public String getEditorName() {
		return "Column Label";
	}
	
	@Override
	public void edit(String newColumnHeaderLabel) throws Exception {
		if (newColumnHeaderLabel != null) {
			textField.setText(newColumnHeaderLabel);
		}
	}
	
	@Override
	public String getNewValue() {
		if (textField.isEnabled() && textField.getText().length() > 0) {
			return textField.getText();
		}
		return null;
	}
	
}
