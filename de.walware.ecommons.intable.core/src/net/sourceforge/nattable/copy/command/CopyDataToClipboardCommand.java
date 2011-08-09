package net.sourceforge.nattable.copy.command;

import net.sourceforge.nattable.command.AbstractContextFreeCommand;

public class CopyDataToClipboardCommand extends AbstractContextFreeCommand {

	private final String cellDelimeter;
	private final String rowDelimeter;
	
	
	public CopyDataToClipboardCommand(String cellDelimeter, String rowDelimeter) {
		this.cellDelimeter = cellDelimeter;
		this.rowDelimeter = rowDelimeter;
	}
	
	
	public String getCellDelimeter() {
		return cellDelimeter;
	}
	
	public String getRowDelimeter() {
		return rowDelimeter;
	}
	
}
