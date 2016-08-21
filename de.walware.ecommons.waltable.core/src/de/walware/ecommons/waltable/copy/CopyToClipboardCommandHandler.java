/*******************************************************************************
 * Copyright (c) 2012-2016 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package de.walware.ecommons.waltable.copy;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.waltable.command.AbstractLayerCommandHandler;
import de.walware.ecommons.waltable.coordinate.ILValueIterator;
import de.walware.ecommons.waltable.coordinate.LRangeList;
import de.walware.ecommons.waltable.data.ControlData;
import de.walware.ecommons.waltable.data.IDataProvider;
import de.walware.ecommons.waltable.internal.WaLTablePlugin;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.CellDisplayConversionUtils;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.selection.SelectionLayer;
import de.walware.ecommons.waltable.ui.ITableUIContext;


/**
 * Handler class for copying selected data within the {@link SelectionLayer} to the clipboard.
 * This handler is registered by default with the {@link SelectionLayer}, without references
 * to the header regions. You can override the copy data behaviour by registering an instance
 * of this handler to a layer above the {@link SelectionLayer}. This way the registered custom
 * instance will consume a {@link CopyToClipboardCommand} and the registered default handler
 * won't be called.
 */
public class CopyToClipboardCommandHandler extends AbstractLayerCommandHandler<CopyToClipboardCommand> {
	
	
	/**
	 * The SelectionLayer needed to retrieve the selected data to copy to the clipboard.
	 */
	private final SelectionLayer selectionLayer;
	
	/**
	 * The column header layer of the grid, needed to also copy the column header data.
	 */
	private final ILayer columnHeaderDataLayer;
	
	/**
	 * The row header layer of the grid, needed to also copy the row header data.
	 */
	private final ILayer rowHeaderDataLayer;
	
	private final ITableUIContext uiContext;
	
	
	/**
	 * Creates an instance that only checks the {@link SelectionLayer} for data to add to the
	 * clipboard.
	 * @param selectionLayer The {@link SelectionLayer} within the NatTable. Can not be <code>null</code>.
	 */
	public CopyToClipboardCommandHandler(final SelectionLayer selectionLayer,
			final ITableUIContext uiContext) {
		this(selectionLayer, null, null, uiContext);
	}
	
	/**
	 * Creates an instance that checks the {@link SelectionLayer} and the header layers if they are given.
	 * @param selectionLayer The {@link SelectionLayer} within the NatTable. Can not be <code>null</code>.
	 * @param columnHeaderDataLayer The column header data layer within the NatTable grid. Can be <code>null</code>.
	 * @param rowHeaderDataLayer The row header data layer within the NatTable grid. Can be <code>null</code>.
	 */
	public CopyToClipboardCommandHandler(final SelectionLayer selectionLayer,
			final ILayer columnHeaderDataLayer, final ILayer rowHeaderDataLayer,
			final ITableUIContext uiContext) {
		if (selectionLayer == null) {
			throw new NullPointerException("selectionLayer"); //$NON-NLS-1$
		}
		if (uiContext == null) {
			throw new NullPointerException("uiContext"); //$NON-NLS-1$
		}
		
		this.selectionLayer= selectionLayer;
		this.columnHeaderDataLayer= columnHeaderDataLayer;
		this.rowHeaderDataLayer= rowHeaderDataLayer;
		this.uiContext= uiContext;
	}
	
	
	@Override
	public boolean doCommand(final CopyToClipboardCommand command) {
		doCopy(command);
		return true;
	}
	
	private void doCopy(final CopyToClipboardCommand command) {
		final ILayerCell[][] copiedCells= assembleCopiedDataStructure();
		if (copiedCells.length == 0) {
			return;
		}
		final int rowCount= copiedCells.length;
		final int colCount= copiedCells[0].length;
		
		final Object[][] values= new Object[rowCount][colCount];
		try {
			if (rowCount <= 1000 && colCount <= 1000) {
				for (int rowIdx= 0; rowIdx < rowCount; rowIdx++) {
					for (int colIdx= 0; colIdx < colCount; colIdx++) {
						final ILayerCell cell= copiedCells[rowIdx][colIdx];
						if (cell != null) {
							final Object dataValue= cell.getDataValue(0, null);
							if (dataValue instanceof ControlData) {
								if ((((ControlData) dataValue).getCode() & ControlData.ASYNC) != 0) {
									loadAsync(copiedCells, values, rowIdx, colIdx);
									break;
								}
								else if ((((ControlData) dataValue).getCode() & ControlData.ERROR) != 0) {
									throw new CoreException(
											new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID, 0,
													"Failed to load required data.",
													null ));
								}
							}
							values[rowIdx][colIdx]= dataValue;
						}
					}
				}
			}
			else {
				loadAsync(copiedCells, values, 0, 0);
			}
		}
		catch (final CoreException e) {
			if (e.getStatus().getSeverity() != IStatus.ERROR) {
				return;
			}
			final Status status= new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID, 0,
					"Copy table data to clipboard failed.",
					e );
			WaLTablePlugin.log(status);
			this.uiContext.show(status);
			return;
		}
		
		final String textData;
		{	final String cellDelimeter= command.getCellDelimeter();
			final String rowDelimeter= command.getRowDelimeter();
			final StringBuilder textBuilder= new StringBuilder();
			for (int rowIdx= 0; rowIdx < rowCount; ) {
				for (int colIdx= 0; colIdx < colCount; ) {
					final ILayerCell cell= copiedCells[rowIdx][colIdx];
					if (cell != null) {
						textBuilder.append(getTextForCell(command, cell, values[rowIdx][colIdx]));
					}
					if (++colIdx < colCount) {
						textBuilder.append(cellDelimeter);
					}
				}
				if (++rowIdx < rowCount) {
					textBuilder.append(rowDelimeter);
				}
			}
			textData= textBuilder.toString();
		}
		
		if (!textData.isEmpty()) {
			final Clipboard clipboard= new Clipboard(Display.getDefault());
			try {
				clipboard.setContents(
						new Object[]{ textData },
						new Transfer[]{ TextTransfer.getInstance() } );
			}
			finally {
				clipboard.dispose();
			}
		}
	}
	
	private void loadAsync(final ILayerCell[][] copiedCells, final Object[][] values,
			final int startRowIdx, final int startColIdx) throws CoreException {
		try {
			this.uiContext.run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor)
						throws InvocationTargetException {
					try {
						final int rowCount= copiedCells.length;
						final int colCount= copiedCells[0].length;
						
						monitor.beginTask("Collecting data to copy...", rowCount - startRowIdx);
						
						for (int rowIdx= startRowIdx; rowIdx < rowCount; rowIdx++) {
							for (int colIdx= (rowIdx == startRowIdx) ? startColIdx : 0; colIdx < colCount; colIdx++) {
								final ILayerCell cell= copiedCells[rowIdx][colIdx];
								if (cell != null) {
									final Object dataValue= cell.getDataValue(IDataProvider.FORCE_SYNC,
											monitor );
									if (dataValue instanceof ControlData) {
										if ((((ControlData) dataValue).getCode() & ControlData.ERROR) != 0) {
											throw new CoreException((monitor.isCanceled()) ?
													Status.CANCEL_STATUS :
													new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID, 0,
															"Failed to load required data.",
															null ));
										}
									}
									values[rowIdx][colIdx]= dataValue;
								}
							}
							
							if (monitor.isCanceled()) {
								throw new CoreException(Status.CANCEL_STATUS);
							}
							monitor.worked(1);
						}
					}
					catch (final Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		}
		catch (final InvocationTargetException e) {
			final Throwable cause= e.getCause();
			if (cause instanceof CoreException) {
				throw (CoreException) cause;
			}
			throw new CoreException(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID, 0,
					"An error occurred when loading required data.",
					cause ));
		}
		catch (final InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR, WaLTablePlugin.PLUGIN_ID, 0,
					"An error occurred when loading required data.",
					e ));
		}
	}
	
	protected String getTextForCell(final CopyToClipboardCommand command, final ILayerCell cell, final Object value) {
		return CellDisplayConversionUtils.convertDataType(cell, value, command.getConfigRegistry());
	}
	
	
	@Override
	public Class<CopyToClipboardCommand> getCommandClass() {
		return CopyToClipboardCommand.class;
	}
	
	/**
	 * Collects and assembles the selected data that should be copied to the clipboard.
	 * 
	 * Creates the two dimensional array whose dimensions are calculated based on the selection
	 * within the {@link SelectionLayer} and the configured column and row headers.
	 * 
	 * @return A two dimensional array containing the selected cells to copy to the clipboard.
	 * 			The first level of this array represent the row positions of the cells, while the
	 * 			second level contains the cells itself based on the column position.
	 */
	protected ILayerCell[][] assembleCopiedDataStructure() {
		final LRangeList selectedRowPositions= this.selectionLayer.getSelectedRowPositions();
		final LRangeList selectedColumnPositions= this.selectionLayer.getSelectedColumnPositions();
		
		final long rowOffset= (this.columnHeaderDataLayer != null) ? this.columnHeaderDataLayer.getRowCount() : 0;
		final long columnOffset= (this.rowHeaderDataLayer != null) ? this.rowHeaderDataLayer.getColumnCount() : 0;
		if ((selectedRowPositions.values().size() + rowOffset) > Integer.MAX_VALUE
				|| (selectedColumnPositions.values().size() + columnOffset) > Integer.MAX_VALUE ) {
			throw new UnsupportedOperationException("Selected area too large.");
		}
		
		final ILayerCell[][] cells= new ILayerCell[(int) (selectedRowPositions.values().size() + rowOffset)][];
		
		int cellsIdx= 0;
		while (cellsIdx < rowOffset) {
			cells[cellsIdx++]= assembleColumnHeader(selectedColumnPositions, (int) columnOffset, cellsIdx);
		}
		for (final ILValueIterator rowIter= selectedRowPositions.values().iterator(); rowIter.hasNext(); ) {
			final long rowPosition= rowIter.nextValue();
			cells[cellsIdx++]= assembleBody(selectedColumnPositions, (int) columnOffset, rowPosition);
		}
		
		return cells;
	}
	
	/**
	 * Collects and assembles the column header information for the specified column header row.
	 * 
	 * If there is no column header configured an empty array with the matching dimensions will be returned.
	 * 
	 * @param selectedColumnPositions The column positions of which the information should be collected
	 * @param columnOffset The column offset of table body
	 * @param headerRowPosition The row position in the column header of which the information should be collected
	 * @return An array containing the column header information
	 */
	protected ILayerCell[] assembleColumnHeader(final LRangeList selectedColumnPositions, final int columnOffset,
			final long headerRowPosition) {
		final ILayerCell[] headerCells= new ILayerCell[(int) (selectedColumnPositions.values().size() + columnOffset)];
		
		int headerIdx= columnOffset;
		if (this.columnHeaderDataLayer != null) {
			for (final ILValueIterator columnIter= selectedColumnPositions.values().iterator(); columnIter.hasNext(); headerIdx++) {
				final long columnPosition= columnIter.nextValue();
				headerCells[headerIdx]= this.columnHeaderDataLayer.getCellByPosition(columnPosition, headerRowPosition);
			}
		}
		
		return headerCells;
	}
	
	/**
	 * Collects and assembles the selected data per row position that should be copied to the clipboard.
	 * If there is a row header layer configured for this handler, the row header cells of the selected
	 * row position are also added to the resulting array.
	 * 
	 * @param selectedColumnPositions The column positions of which the information should be collected
	 * @param columnOffset The column offset of table body
	 * @param currentRowPosition The row position of which the selected cells should be collected
	 * @return An array containing the specified cells
	 */
	protected ILayerCell[] assembleBody(final LRangeList selectedColumnPositions,  final int columnOffset,
			final long currentRowPosition) {
		final ILayerCell[] bodyCells= new ILayerCell[(int) (selectedColumnPositions.values().size() + columnOffset)];
		
		int bodyIdx= 0;
		if (this.rowHeaderDataLayer != null) {
			for (; bodyIdx < columnOffset; bodyIdx++) {
				bodyCells[bodyIdx]= this.rowHeaderDataLayer.getCellByPosition(bodyIdx, currentRowPosition);
			}
		}
		for (final ILValueIterator columnIter= selectedColumnPositions.values().iterator(); columnIter.hasNext(); bodyIdx++) {
			final long columnPosition= columnIter.nextValue();
			if (this.selectionLayer.isCellPositionSelected(columnPosition, currentRowPosition)) {
				bodyCells[bodyIdx]= this.selectionLayer.getCellByPosition(columnPosition, currentRowPosition);
			}
		}
		
		return bodyCells;
	}
	
}
