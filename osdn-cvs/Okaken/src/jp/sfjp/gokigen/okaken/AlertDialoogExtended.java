package jp.sfjp.gokigen.okaken;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

/**
 * 
 * 
 * @author MRSa
 *
 */
public class AlertDialoogExtended  extends AlertDialog
{
	/**
	 * 
	 * @param context
	 */
    public AlertDialoogExtended(Context context)
	{
        super(context);  
    }

	/**
	 * 
	 * @param context
	 */
    public AlertDialoogExtended(Context context, int theme)
	{  
        super(context, theme);  
	}
    
    public Dialog create()
    {
    	return (this);
    }
}
