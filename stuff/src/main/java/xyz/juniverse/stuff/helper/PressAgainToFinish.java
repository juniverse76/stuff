package xyz.juniverse.stuff.helper;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by juniverse on 13/07/2017.
 *
 * back 다시 눌러서 종료하기 기능.
 */

public class PressAgainToFinish {
    private Activity activity;
    private long waitTime;
    private int messageResId;
    private Handler handler;
    private Toast toast;

    // 직접 생성 못하게...
    private PressAgainToFinish() {}

    /**
     * 종료 처리.
     */
    public void finish() {
        if (handler != null) {
            handler.removeMessages(1);
            toast.cancel();
            activity.finish();
            return;
        }

        handler = new Handler(activity.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                handler = null;
                toast.cancel();
                toast = null;
            }
        };

        handler.sendEmptyMessageDelayed(1, waitTime);
        toast = Toast.makeText(activity.getBaseContext(), messageResId, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * PressAgainToFinish 생성.
     * @param activity 담당하는 액티비티
     * @param waitTime 대기 시간 (milliseconds)
     * @param messageResId Toast에 사용할 텍스트 리소스 아이디
     * @return PressAgainToFinish 객체
     */
    public static PressAgainToFinish make(Activity activity, long waitTime, int messageResId) {
        PressAgainToFinish instance = new PressAgainToFinish();

        instance.activity = activity;
        instance.waitTime = waitTime;
        instance.messageResId = messageResId;

        return instance;
    }
}
