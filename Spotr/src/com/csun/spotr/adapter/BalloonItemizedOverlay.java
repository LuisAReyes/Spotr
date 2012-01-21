package com.csun.spotr.adapter;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.csun.spotr.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * An abstract extension of ItemizedOverlay for displaying an information
 * balloon upon screen-tap of each marker overlay.
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {
	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	private View clickRegion;
	private int viewOffset;
	final private MapController mc;
	private Item currentFocussedItem;
	private int currentFocussedIndex;

	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker
	 *            - A bounded drawable to be drawn on the map for each item in
	 *            the overlay.
	 * @param mapView
	 *            - The view upon which the overlay items are to be drawn.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
	}

	/**
	 * Set the horizontal distance between the marker and the bottom of the
	 * information balloon. The default is 0 which works well for center bounded
	 * markers. If your marker is center-bottom bounded, call this before adding
	 * overlay items to ensure the balloon hovers exactly above the marker.
	 * 
	 * @param pixels
	 *            - The padding between the center point and the bottom of the
	 *            information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}

	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	/**
	 * Override this method to handle a "tap" on a balloon. By default, does
	 * nothing and returns false.
	 * 
	 * @param index
	 *            - The index of the item whose balloon is tapped.
	 * @param item
	 *            - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) {
		return false;
	}

	@Override
	protected final boolean onTap(int index) {

		currentFocussedIndex = index;
		currentFocussedItem = createItem(index);

		createAndDisplayBalloonOverlay();

		mc.animateTo(currentFocussedItem.getPoint());

		return true;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that can
	 * populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}

	/**
	 * Expose map view to subclasses. Helps with creation of balloon views.
	 */
	protected MapView getMapView() {
		return mapView;
	}

	/**
	 * Sets the visibility of this overlay's balloon view to GONE.
	 */
	protected void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}

	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays
	 *            - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {

		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}

	}

	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				View l = ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = { android.R.attr.state_pressed };
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					return true;
				}
				else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					// call overridden method
					onBalloonTap(currentFocussedIndex, currentFocussedItem);
					return true;
				}
				else {
					return false;
				}

			}
		};
	}

	@Override
	public Item getFocus() {
		return currentFocussedItem;
	}

	@Override
	public void setFocus(Item item) {
		currentFocussedItem = item;

		if (currentFocussedItem == null) {
			hideBalloon();
		}
		else {
			createAndDisplayBalloonOverlay();
		}
	}

	/**
	 * Creates and displays the balloon overlay by recycling the current balloon
	 * or by inflating it from xml.
	 * 
	 * @return true if the balloon was recycled false otherwise
	 */
	private boolean createAndDisplayBalloonOverlay() {
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());
			isRecycled = false;
		}
		else {
			isRecycled = true;
		}

		balloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}

		if (currentFocussedItem != null)
			balloonView.setData(currentFocussedItem);

		GeoPoint point = currentFocussedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		}
		else {
			mapView.addView(balloonView, params);
		}
		return isRecycled;
	}
}