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
package de.walware.ecommons.waltable.export;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.waltable.Messages;
import de.walware.ecommons.waltable.NatTable;
import de.walware.ecommons.waltable.config.IConfigRegistry;
import de.walware.ecommons.waltable.coordinate.LRectangle;
import de.walware.ecommons.waltable.layer.ILayer;
import de.walware.ecommons.waltable.layer.cell.ILayerCell;
import de.walware.ecommons.waltable.print.PrintEntireGridCommand;
import de.walware.ecommons.waltable.print.TurnViewportOffCommand;
import de.walware.ecommons.waltable.print.TurnViewportOnCommand;
import de.walware.ecommons.waltable.style.DisplayMode;
import de.walware.ecommons.waltable.ui.IClientAreaProvider;

public class NatExporter {
	
	private final Shell shell;

	public NatExporter(final Shell shell) {
		this.shell= shell;
	}
	
	/**
	 * Exports a single ILayer using the ILayerExporter registered in the ConfigRegistry.
	 * @param layer The ILayer to export, usually a NatTable instance.
	 * @param configRegistry The ConfigRegistry of the NatTable instance to export,
	 * 			that contains the necessary export configurations.
	 */
	public void exportSingleLayer(final ILayer layer, final IConfigRegistry configRegistry) {
		final ILayerExporter exporter= configRegistry.getConfigAttribute(ExportConfigAttributes.EXPORTER, DisplayMode.NORMAL);
		
		final OutputStream outputStream= exporter.getOutputStream(this.shell);
		if (outputStream == null) {
			return;
		}
		
		final Runnable exportRunnable= new Runnable() {
			@Override
			public void run() {
				try {
					exporter.exportBegin(outputStream);
					
					exportLayer(exporter, outputStream, "", layer, configRegistry); //$NON-NLS-1$
					
					exporter.exportEnd(outputStream);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to export.", e); //$NON-NLS-1$
				} finally {
					try {
						outputStream.close();
					} catch (final IOException e) {
						e.printStackTrace(System.err);
					}
				}
				
				openExport(exporter);
			}
		};
		
		if (this.shell != null) {
			// Run with the SWT display so that the progress bar can paint
			this.shell.getDisplay().asyncExec(exportRunnable);
		} else {
			exportRunnable.run();
		}
	}
	
	/**
	 * Export multiple NatTable instances to one file by using the given ILayerExporter.
	 * @param exporter The ILayerExporter to use for exporting.
	 * @param natTablesMap The NatTable instances to export. They keys in the map will be
	 * 			used as sheet titles while the values are the instances to export.
	 */
	public void exportMultipleNatTables(final ILayerExporter exporter, final Map<String, NatTable> natTablesMap) {
		final OutputStream outputStream= exporter.getOutputStream(this.shell);
		if (outputStream == null) {
			return;
		}
		
		final Runnable exportRunnable= new Runnable() {
			@Override
			public void run() {
				try {
					exporter.exportBegin(outputStream);
					
					for (final String name : natTablesMap.keySet()) {
						final NatTable natTable= natTablesMap.get(name);
						exportLayer(exporter, outputStream, name, natTable, natTable.getConfigRegistry());
					}
					
					exporter.exportEnd(outputStream);
				} catch (final IOException e) {
					throw new RuntimeException("Failed to export.", e); //$NON-NLS-1$
				} finally {
					try {
						outputStream.close();
					} catch (final IOException e) {
						e.printStackTrace(System.err);
					}
				}
				
				openExport(exporter);
			}
		};
		
		if (this.shell != null) {
			// Run with the SWT display so that the progress bar can paint
			this.shell.getDisplay().asyncExec(exportRunnable);
		} else {
			exportRunnable.run();
		}
	}
	
	/**
	 * Exports the given layer to the outputStream using the provided exporter. The exporter.exportBegin() method should be called before this method is invoked, and
	 * exporter.exportEnd() should be called after this method returns. If multiple layers are being exported as part of a single logical export operation, then
	 * exporter.exportBegin() will be called once at the very beginning, followed by n calls to this exportLayer() method, and finally followed by exporter.exportEnd().
	 * 
	 * @param exporter
	 * @param outputStream
	 * @param layerName
	 * @param layer
	 * @param configRegistry
	 */
	protected void exportLayer(final ILayerExporter exporter, final OutputStream outputStream, final String layerName, final ILayer layer, final IConfigRegistry configRegistry) {
		final IClientAreaProvider originalClientAreaProvider= layer.getClientAreaProvider();
		
		// This needs to be done so that the layer can return all the cells
		// not just the ones visible in the viewport
		layer.doCommand(new TurnViewportOffCommand());
		setClientAreaToMaximum(layer);
		
		//if a SummaryRowLayer is in the layer stack, we need to ensure that the values are calculated
//		layer.doCommand(new CalculateSummaryRowValuesCommand());
		
		ProgressBar progressBar= null;
		
		double factor= 1.0;
		if (this.shell != null) {
			final Shell childShell= new Shell(this.shell.getDisplay(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			childShell.setText(Messages.getString("NatExporter.exporting")); //$NON-NLS-1$

			final int startRow= 0;
			final long endRow= layer.getRowCount() - 1;
			factor= (endRow < Integer.MAX_VALUE) ? 1.0 : ((double) 0xfffffff) / endRow;
			
			progressBar= new ProgressBar(childShell, SWT.SMOOTH);
			progressBar.setMinimum(startRow);
			progressBar.setMaximum((int) (factor * endRow));
			progressBar.setBounds(0, 0, 400, 25);
			progressBar.setFocus();

			childShell.pack();
			childShell.open();
		}
		
		try {
			exporter.exportLayerBegin(outputStream, layerName);
			
			for (long rowPosition= 0; rowPosition < layer.getRowCount(); rowPosition++) {
				exporter.exportRowBegin(outputStream, rowPosition);
				if (progressBar != null) {
					progressBar.setSelection((int) (factor * rowPosition));
				}
				
				for (long columnPosition= 0; columnPosition < layer.getColumnCount(); columnPosition++) {
					final ILayerCell cell= layer.getCellByPosition(columnPosition, rowPosition);
					
					final IExportFormatter exportFormatter= configRegistry.getConfigAttribute(ExportConfigAttributes.EXPORT_FORMATTER, cell.getDisplayMode(), cell.getConfigLabels().getLabels());
					final Object exportDisplayValue= exportFormatter.formatForExport(cell, configRegistry);

					exporter.exportCell(outputStream, exportDisplayValue, cell, configRegistry);
				}
				
				exporter.exportRowEnd(outputStream, rowPosition);
			}
			
			exporter.exportLayerEnd(outputStream, layerName);
		} catch (final Exception e) {
			e.printStackTrace(System.err);
		}

		// These must be fired at the end of the thread execution
		layer.setClientAreaProvider(originalClientAreaProvider);
		layer.doCommand(new TurnViewportOnCommand());
		
		if (progressBar != null) {
			final Shell childShell= progressBar.getShell();
			progressBar.dispose();
			childShell.dispose();
		}
	}
	
	private void setClientAreaToMaximum(final ILayer layer) {
		final LRectangle maxClientArea= new LRectangle(0, 0, layer.getWidth(), layer.getHeight());
		
		layer.setClientAreaProvider(new IClientAreaProvider() {
			@Override
			public LRectangle getClientArea() {
				return maxClientArea;
			}
		});
		
		layer.doCommand(new PrintEntireGridCommand());
	}
	
	private void openExport(final ILayerExporter exporter) {
		if (exporter.getResult() != null && exporter.getResult() instanceof File) {
			Program.launch(((File)exporter.getResult()).getAbsolutePath());
		}
	}
	
}
