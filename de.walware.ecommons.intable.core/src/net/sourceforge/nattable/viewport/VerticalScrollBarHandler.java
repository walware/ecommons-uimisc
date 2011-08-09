package net.sourceforge.nattable.viewport;

import net.sourceforge.nattable.coordinate.IRelative.Direction;
import net.sourceforge.nattable.layer.LayerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;

/**
 * Listener for the Vertical scroll bar events.
 */
public class VerticalScrollBarHandler extends ScrollBarHandlerTemplate implements Listener {

	public VerticalScrollBarHandler(ViewportLayer viewportLayer, ScrollBar scrollBar) {
		super(viewportLayer, scrollBar);
	}

	/**
	 * In a normal scenario scroll by the height of the viewport. If the row
	 * being scrolled is wider than above, use the row height
	 */
	@Override
	int pageScrollDistance() {
		int heightOfRowBeingScrolled = scrollableLayer.getRowHeightByPosition(getScrollablePosition());
		int viewportHeight = viewportLayer.getClientAreaHeight();
		return (heightOfRowBeingScrolled > viewportHeight) ? heightOfRowBeingScrolled : viewportHeight;
	}

	@Override
	int getSpanByPosition(int scrollablePosition) {
		return scrollableLayer.getRowHeightByPosition(scrollablePosition);
	}

	/**
	 * Convert Viewport 0 pos -> Scrollable 0 pos
	 * 
	 * @return
	 */
	@Override
	int getScrollablePosition() {
		return LayerUtil.convertRowPosition(viewportLayer, 0, scrollableLayer);
//		int scrollablePosition = 
//				LayerUtil.convertRowPosition(viewportLayer, 0, scrollableLayer);
//		int adjustedScrollablePosition =
//				scrollablePosition - viewportLayer.getMinimumOriginRowPosition();
//		return adjustedScrollablePosition;
	}

	@Override
	int getStartPixelOfPosition(int position) {
		return scrollableLayer.getStartYOfRowPosition(position);
	}

	@Override
	int getPositionByPixel(int pixelValue) {
		return scrollableLayer.getRowPositionByY(pixelValue);
	}

	@Override
	void setViewportOrigin(int position) {
		viewportLayer.invalidateVerticalStructure();
//		viewportLayer.setOriginRowPosition(
//				viewportLayer.getMinimumOriginRowPosition() + position );
		viewportLayer.setOriginRowPosition(position);
		scrollBar.setIncrement(viewportLayer.getRowHeightByPosition(0));
	}

	@Override
	Direction scrollDirectionForEventDetail(int eventDetail) {
		return (eventDetail == SWT.PAGE_UP || eventDetail == SWT.ARROW_UP) ?
				Direction.UP : Direction.DOWN;
	}

	@Override
	boolean keepScrolling() {
		return !viewportLayer.isLastRowCompletelyDisplayed();
	}
	
	@Override
	int getViewportWindowSpan() {
//		System.out.println("viewportLayer.getClientAreaHeight: "+viewportLayer.getClientAreaHeight());
//		System.out.println("viewportLayer.getHeight:           "+viewportLayer.getHeight());
//		System.out.println("scrollableLayer.getHeight:         "+scrollableLayer.getHeight());
		return viewportLayer.getClientAreaHeight();
	}

	@Override
	int getScrollableLayerSpan() {
//		System.out.println("viewportLayer.getClientAreaHeight: "+viewportLayer.getClientAreaHeight());
//		System.out.println("viewportLayer.getHeight:           "+viewportLayer.getHeight());
//		System.out.println("scrollableLayer.getHeight:         "+scrollableLayer.getHeight());
		return scrollableLayer.getHeight();
	}
}