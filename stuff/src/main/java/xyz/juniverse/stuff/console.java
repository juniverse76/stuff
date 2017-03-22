package xyz.juniverse.stuff;

import android.util.Log;

/**
 * Created by juniverse on 19/01/2017.
 */

public class console
{
    private static final String TAG = "console";

    private static boolean debugMode = true;
    public static void setDependency(boolean mode)
    {
        debugMode = mode;
    }

    public static void i(Object... args)
    {
        if (!debugMode) return;
        Log.d(TAG, makeLogText(args));
    }

    public static void d(Object... args)
    {
        if (!debugMode) return;
        Log.d(TAG, makeLogText(args));
    }

    public static void e(Object... args)
    {
        if (!debugMode) return;
        Log.e(TAG, makeLogText(args));
    }

    private static String makeLogText(Object... args)
    {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args)
            builder.append(arg).append(' ');

        StackTraceElement trace = Thread.currentThread().getStackTrace()[4];
        String fileName = trace.getFileName();
        int lineNumber = trace.getLineNumber();
        return builder.toString() + String.format("(%s:%d)", fileName, lineNumber);
    }
}
