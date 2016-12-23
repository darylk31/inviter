package invite.hfad.com.inviter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

/**
 * Created by Jimmy on 12/22/2016.
 */

public class CustomDatePicker extends DatePicker
{
    public CustomDatePicker(Context context, AttributeSet attrs, int
            defStyle)
    {
        super(context, attrs, defStyle);
    }

    public CustomDatePicker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomDatePicker(Context context)
    {
        super(context);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
