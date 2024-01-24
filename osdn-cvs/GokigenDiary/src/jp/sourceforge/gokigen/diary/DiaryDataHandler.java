package jp.sourceforge.gokigen.diary;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

/**
 *  日記データの解析部分 (XMLデータの解析)
 * 
 */
public class DiaryDataHandler extends DefaultHandler
{
    private String temporaryBuffer = "";

    private String savedTimeString = "";
    private String scanedTimeString = "";
    private String locationString = "";
    private String pictureString = null;
    private String attachedPictureString = null;
    private float ratingValue = 0;
    private String messageString = "";
    private String revisedMessageString = "";
    private String wholeMessageString = "";
    private int   latitude = 35000000;
    private int   longitude = 135000000;

    @Override
    public void startDocument() throws SAXException 
    {
        super.startDocument();
        Log.v(Main.APP_IDENTIFIER, "startDocument()");
    }
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException 
    {
        String data = new String(ch, start, length);
        temporaryBuffer = temporaryBuffer + data;
        
        //Log.v(Main.APP_IDENTIFIER, "characters : '" + data +"' (" + start + ", " + length + ")");
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        
        if (localName.matches("place") == true)
        {
            locationString = temporaryBuffer;
        }
        else if (localName.matches("scantime") == true)
        {
            scanedTimeString = temporaryBuffer;
        }
        else if (localName.matches("time") == true)
        {
            savedTimeString = temporaryBuffer;
        }
        else if (localName.matches("rate") == true)
        {
            try
            {
                ratingValue = ((float) Integer.parseInt(temporaryBuffer) / 10);
            }
            catch (Exception ex)
            {
                ratingValue = 0;
            }
        }
        else if (localName.matches("message") == true)
        {
            messageString = temporaryBuffer;
            wholeMessageString = temporaryBuffer;
            wholeMessageString = wholeMessageString + "\n" + "[" + savedTimeString + "]";
        }
        else if (localName.matches("picture") == true)
        {
            if (temporaryBuffer.length() == 0)
            {
                pictureString = null;
            }
            else
            {
                pictureString = temporaryBuffer;
            }  
        }
        else if (localName.matches("latitude") == true)
        {
            latitude = (int) (Double.parseDouble(temporaryBuffer) * 1E6);
            Log.v(Main.APP_IDENTIFIER, "latitude : " + latitude);
        }
        else if (localName.matches("longitude") == true)
        {
            longitude = (int) (Double.parseDouble(temporaryBuffer) * 1E6);
            Log.v(Main.APP_IDENTIFIER, "longitude : " + longitude);
        }
        else if (localName.matches("revisedmessage") == true)
        {
        	// データを上書きする、
        	messageString = temporaryBuffer;
            wholeMessageString = wholeMessageString + "\n" + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-" + "\n" + temporaryBuffer;
        }
        else if (localName.matches("revisedtime") == true)
        {
        	// データを上書きした時刻を取得
            wholeMessageString = wholeMessageString + "\n" + "[" + temporaryBuffer + "]";
        }
        else if (localName.matches("attachedpicture") == true)
        {
        	// データを上書きする、
        	if (temporaryBuffer.length() != 0)
        	{
        	    pictureString = temporaryBuffer;
        	}
        	Log.v(Main.APP_IDENTIFIER, "attached Picture : " + pictureString);
            wholeMessageString = wholeMessageString + "\n" + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-" + "\n" + "(attached picture)";
        }
        //Log.v(Main.APP_IDENTIFIER, "endElement (" + localName + ") : " + temporaryBuffer);
        temporaryBuffer = "";
    }    
    
    public void resetDatas()
    {
        // データの初期化...
        savedTimeString = "";
        scanedTimeString = "";
        locationString = "";
        pictureString = null;
        attachedPictureString = null;
        ratingValue = 0;
        messageString = "";
        revisedMessageString = "";
        System.gc();
    }

    public void setRevisedMessageString(String message)
    {
    	messageString = message;
    	revisedMessageString = message;
    }

    public void setAttachedPictureString(String data)
    {
        attachedPictureString = data;
        pictureString = data;
    }
    
    public String getSavedTimeString()
    {
        return (savedTimeString);
    }

    public String getScanedTimeString()
    {
        return (scanedTimeString);
    }
    
    public String getLocationString()
    {
        return (locationString);
    }

    public float getRatingValue()
    {
        return (ratingValue);
    }
    
    public String getMessageString()
    {
        return (messageString);
    }
    
    public String getPictureString()
    {
        return (pictureString);
    }    
    
    public String getAttachedPictureString()
    {
        return (attachedPictureString);
    }

    public int getLatitude()
    {
    	return (latitude);
    }
    
    public int getLongitude()
    {
    	return (longitude);
    }
    
    public String getRevisedMessageString()
    {
    	return (revisedMessageString);
    }
    
    public String getWholeMessageString()
    {
    	return (wholeMessageString);
    }
}
