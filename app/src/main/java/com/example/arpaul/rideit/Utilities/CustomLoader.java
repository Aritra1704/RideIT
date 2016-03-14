package com.example.arpaul.rideit.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by ARPaul on 13-03-2016.
 */
public class CustomLoader {

    Context context;
    public CustomLoader(Context context){
        this.context = context;
    }

    /**
     * Loader Custom
     */
    protected ProgressDialog dialog;
    private Animation rotateXaxis;
    private ImageView ivOutsideImage, ImageView01;
    private boolean isCancelableLoader;

    public void showLoader(String str)
    {
        ((Activity)context).runOnUiThread(new RunShowLoaderCustom(str));
    }

    public void showLoader(String msg, String title)
    {
        ((Activity)context).runOnUiThread(new RunShowLoaderCustom(msg, title));
    }
    /**
     * Name:         RunShowLoader
     Description:  This is to show the loading progress dialog when some other functionality is taking place.**/
    class RunShowLoaderCustom implements Runnable
    {
        private String title;
        private String strMsg;

        public RunShowLoaderCustom(String strMsg)
        {
            this.strMsg = strMsg;
        }
        public RunShowLoaderCustom(String strMsg, String title)
        {
            this.strMsg = strMsg;
            this.title = title;
        }

        @Override
        public void run()
        {
            try
            {
                if(dialog == null)
                    dialog = new ProgressDialog(context);

                if(!isCancelableLoader)
                    dialog.setCancelable(false);
                else
                    dialog.setCancelable(true);
                dialog.show();

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(strMsg))
                {
                    dialog.show(context, title, strMsg, true);
                } else if(!TextUtils.isEmpty(strMsg)) {
                    dialog.show(context, "", strMsg, true);
                }
            }
            catch(Exception e)
            {}
        }
    }

    /**
     * Name:         RunShowLoader
     Description:  For hiding progress dialog (Loader ).**/
    public void  hideLoader()
    {
        ((Activity)context).runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(dialog != null && dialog.isShowing())
                        dialog.dismiss();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
