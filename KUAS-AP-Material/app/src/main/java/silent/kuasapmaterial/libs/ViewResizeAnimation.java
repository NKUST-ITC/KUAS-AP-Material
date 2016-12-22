package silent.kuasapmaterial.libs;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * an animation for resizing the view.
 */

public class ViewResizeAnimation extends Animation {

	private View mView;
	private float mToHeight;
	private float mFromHeight;

	public ViewResizeAnimation(View v, float fromHeight, float toHeight) {
		mToHeight = toHeight;
		mFromHeight = fromHeight;
		mView = v;
		setDuration(300);
		setInterpolator(new DecelerateInterpolator());
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
		ViewGroup.LayoutParams p = mView.getLayoutParams();
		p.height = (int) height;
		mView.requestLayout();
	}
}
