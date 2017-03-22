package xyz.juniverse.stuff;

import android.content.Context;

import xyz.juniverse.stuff.comm.RPC;


/**
 * Created by juniverse on 20/03/2017.
 *
 * 그냥 라이브러리로 합치자. 매번 복붙하기 귀찮다.
 */

public class JuniversStuff
{
    /**
     * 이 라이브러리에 관련된 모든 초기화 처리.
     * sample : JuniversStuff.init(getApplicationContext(), BuildConfig.DEBUG);
     * @param debugMode BuildConfig를 사용하는 걸 추천. BuildConfig.DEBUG 넘기면 되겠다.
     */
    public static void init(Context context, boolean debugMode)
    {
        console.setDependancy(debugMode);
    }

    public static void initRPC(String serverUrl)
    {
        RPC.setServerCredential(serverUrl);
    }
}
