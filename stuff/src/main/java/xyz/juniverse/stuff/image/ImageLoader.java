package xyz.juniverse.stuff.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import xyz.juniverse.stuff.Stuff;
import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 03/02/2017.
 *
 * Image 캐싱하고 로딩해주는 클래스
 * 네트워크에 다운 받은 것은 메모리에 캐싱을 하고 파일 캐시도 할 수 있음.
 */

final public class ImageLoader
{
    private static final String IMG_DIR = "img_cache/";

    private static ImageLoader mInstance = null;
    public static ImageLoader getInstance()
    {
        if (mInstance == null)
            mInstance = new ImageLoader();
        return mInstance;
    }

    private HashMap<String, Bitmap> memoryCache;
    private ImageLoader()
    {
        this.memoryCache = new HashMap<>();
    }

    private File cacheDir = null;
    private File getCacheDir(Context context)
    {
        if (cacheDir == null)
        {
            if (Stuff.debugMode)
                cacheDir = context.getExternalCacheDir();
            else
                cacheDir = context.getCacheDir();

            new File(cacheDir, IMG_DIR).mkdirs();
        }
        return cacheDir;
    }


    /**
     * imageview에 이미지를 로딩한다. 기본으로 캐싱을 한다.
     * @param url
     * @param view
     */
    public void loadImageToView(String url, ImageView view)
    {
        loadImageToView(url, view, true);
    }

    public void loadImageToView(final String url, final ImageView view, final boolean cache)
    {
        final String key = getKey(url);

        // tag reset....
        view.setTag(key);

        Bitmap bm = memoryCache.get(key);
        if (bm != null) {
            view.setImageBitmap(bm);
            return;
        }

        String filePath = null;
        if (cache)
        {
            // look for image from cache folder
//            final File targetFile = new File(getCacheDir(view.getContext()), IMG_DIR + key);
            final File targetFile = getCacheFile(view.getContext(), url);
            if (targetFile.exists())
            {
                bm = BitmapFactory.decodeFile(targetFile.getPath());
                memoryCache.put(key, bm);
                view.setImageBitmap(bm);
                return;
            }

            filePath = targetFile.getPath();
        }

        // url loading
        new ImageLoadTask(new CompleteListener()
        {
            @Override
            public void complete(Bitmap bm) {
                if (bm == null)
                    return;

                memoryCache.put(key, bm);
                if (view.getTag().equals(key))
                    view.setImageBitmap(bm);
            }
        }).execute(url, filePath);
    }

    public void loadAssetImageToView(final String filePath, final ImageView view)
    {
        final String key = getKey(filePath);

        Bitmap bm = memoryCache.get(key);
        if (bm != null) {
            view.setImageBitmap(bm);
            return;
        }

        try {
            bm = BitmapFactory.decodeStream(view.getContext().getAssets().open(filePath));
            memoryCache.put(key, bm);
            view.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preloadAssetImage(final Context context, final String filePath)
    {
        final String key = getKey(filePath);

        Bitmap bm = memoryCache.get(key);
        if (bm != null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bm = BitmapFactory.decodeStream(context.getAssets().open(filePath));
                    memoryCache.put(key, bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                console.i("preloadAssetImage COMPLETE!!!");
            }
        }).start();
    }

    public Bitmap getBitmap(final String url)
    {
        return memoryCache.get(getKey(url));
    }

    public void cacheImageToMemory(Context context, String url, Bitmap bitmap, final CacheWriteListener listener)
    {
        addToCache(context, url, bitmap, false, listener);
    }

    public void cacheImageToFile(Context context, String url, Bitmap bitmap, final CacheWriteListener listener)
    {
        addToCache(context, url, bitmap, true, listener);
    }

    public void removeCache(Context context, String url)
    {
        removeCache(context, url, false);
    }

    public void removeCache(Context context, String url, boolean andFile)
    {
        if (andFile)
        {
            File targetFile = getCacheFile(context, url);
            if (targetFile.exists())
                targetFile.delete();
        }

        String key = getKey(url);
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        memoryCache.remove(key);
    }



    private void addToCache(final Context context, final String url, final Bitmap bitmap, boolean saveFile, final CacheWriteListener listener)
    {
        memoryCache.put(getKey(url), bitmap);

        if (saveFile)
        {
            Runnable writer = new Runnable() {
                @Override
                public void run() {
                    File cacheFile = getCacheFile(context, url);
                    try {
                        FileOutputStream out = new FileOutputStream(cacheFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.close();
                        if (listener != null)
                            listener.complete(cacheFile);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        if (listener != null)
                            listener.complete(null);
                    }
                }
            };

            Thread thread = new Thread(writer);
            thread.start();
        }
    }

    private File getCacheFile(Context context, String url)
    {
        return new File(getCacheDir(context), IMG_DIR + getKey(url));
    }

    private String getKey(String url)
    {
        return url.replace('/', '_').replace(':', '_').replace('.', '_');
    }

    interface CompleteListener
    {
        void complete(Bitmap bm);
    }

    private class ImageLoadTask extends AsyncTask<String, Void, Bitmap>
    {
        CompleteListener listener;

        ImageLoadTask(CompleteListener listener)
        {
            this.listener = listener;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String src = params[0];
            String filePath = params[1];
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                if (filePath != null)
                {
                    try {
                        FileOutputStream out = new FileOutputStream(filePath);
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return myBitmap;
            } catch (IOException e) {
                // Log exception
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (listener != null)
                listener.complete(bitmap);
        }
    }

    public interface CacheWriteListener
    {
        void complete(File file);
    }
}

