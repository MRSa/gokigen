package jp.sourceforge.gokigen.diary;

/**
 *  "‚²‚«‚°‚ñ“x”" ‚Ì•ÏŠ·
 * 
 * @author MRSa
 *
 */
public class DecideEmotionIcon
{

	public static int decideEmotionIcon(String rating)
    {
		try
		{
            int rate = Integer.parseInt(rating,10);
            return (DecideEmotionIcon.decideEmotionIcon(rate));
		}
		catch (Exception ex)
		{
			return (R.drawable.emo_im_foot_in_mouth);
		}
    }
	
	public static int decideEmotionIcon(int rate)
    {
        int id = 0;
    	if (rate < 5)
        {
            id = R.drawable.emo_im_crying;
        }
        else if (rate <= 10)
        {
            id = R.drawable.emo_im_sad;
        }
        else if (rate <= 15)
        {
            id = R.drawable.emo_im_undecided;
        }
        else if (rate <= 20)
        {
            id = R.drawable.emo_im_yelling;
        }
        else if (rate <= 25)
        {
            id = R.drawable.emo_im_surprised;
        }
        else if (rate <= 30)
        {
            id = R.drawable.emo_im_happy;
        }
        else if (rate <= 35)
        {
            id = R.drawable.emo_im_winking;
        }
        else if (rate <= 40)
        {
            id = R.drawable.emo_im_laughing;
        }
        else if (rate <= 45)
        {
            id = R.drawable.emo_im_angel;
        }
        else
        {
            id = R.drawable.emo_im_cool;
        }
        return (id);
    }

    public static int decideEmotionIconSmall(int rate)
    {
        int id = 0;
    	if (rate < 5)
        {
            id = R.drawable.emo_im_crying_s;
        }
        else if (rate <= 10)
        {
            id = R.drawable.emo_im_sad_s;
        }
        else if (rate <= 15)
        {
            id = R.drawable.emo_im_undecided_s;
        }
        else if (rate <= 20)
        {
            id = R.drawable.emo_im_yelling_s;
        }
        else if (rate <= 25)
        {
            id = R.drawable.emo_im_surprised_s;
        }
        else if (rate <= 30)
        {
            id = R.drawable.emo_im_happy_s;
        }
        else if (rate <= 35)
        {
            id = R.drawable.emo_im_winking_s;
        }
        else if (rate <= 40)
        {
            id = R.drawable.emo_im_laughing_s;
        }
        else if (rate <= 45)
        {
            id = R.drawable.emo_im_angel_s;
        }
        else
        {
            id = R.drawable.emo_im_cool_s;
        }
        return (id);
    }

    public static String decideEmotionString(int rate)
    {
        String data = "";
    	if (rate < 5)
        {
            data = ":'(";    // Crying
        }
        else if (rate <= 10)
        {
            data = ":-(";    // Sad
        }
        else if (rate <= 15)
        {
            data = ":-\\";    // Undecided
        }
        else if (rate <= 20)
        {
            data = ":O";     // Yelling
        }
        else if (rate <= 25)
        {
            data = "=-O";    // Surprised;
        }
        else if (rate <= 30)
        {
            data = ":-)";    // Happy
        }
        else if (rate <= 35)
        {
            data = ";-)";    // Winking
        }
        else if (rate <= 40)
        {
            data = ":-D";    // Laughing
        }
        else if (rate <= 45)
        {
            data = "O:-)";   //  Angel
        }
        else
        {
            data = "B-)";    //  Cool
        }
        return (data);
    }
    
    public static String decideEmotionJapaneseStyleString(int rate)
    {
        String data = "";
    	if (rate < 5)
        {
            data = "BEKE(/„D`)EKEB";    // Crying (LGƒÖG`) i‚sO‚sj
        }
        else if (rate <= 10)
        {
            data = "(LEƒÖE`)";    // Sad
        }
        else if (rate <= 15)
        {
            data = "(LƒwM;)";    // Undecided ((+„t+)) (@_@   (;Ê_Ê)
        }
        else if (rate <= 20)
        {
            data = "(MƒwL#) ";     // Yelling
        }
        else if (rate <= 25)
        {
            data = "(ß0ß)";    // Surprised;  (@„t)@K@K
        }
        else if (rate <= 30)
        {
            data = "iEÍEj";    // Happy
        }
        else if (rate <= 35)
        {
            data = " (^_-)-™";    // Winking 
        }
        else if (rate <= 40)
        {
            data = "R(^¤^)ƒm";    // Laughing  (OÍO)
        }
        else if (rate <= 45)
        {
            data = "R(*L[M)ƒm";   //  Angel ( ^-^)ƒm o(^-^)o 
        }
        else
        {
            data = "ƒÏ(^o^)ô ";    //  Cool o(^-^o)(o^-^)o
        }
        return (data);
    }

    public static int numberOfEmotionIcons()
    {
    	return (decideEmotionIconIndex(R.drawable.emo_im_cool) + 1);
    }
    
    public static int decideEmotionIconFromIndex(int index, boolean isSmall)
    {
    	if (isSmall == true)
    	{
            return (decideEmotionIconSmall((index * 5) + 1));
    	}
        return (decideEmotionIconSmall((index * 5) + 1));
    }

    public static int decideEmotionIconIndex(int iconId)
    {
    	int index = 0;
    	switch (iconId)
    	{
          case R.drawable.emo_im_crying:
    	  case R.drawable.emo_im_crying_s:
            index = 0;
            break;

    	  case R.drawable.emo_im_sad:
    	  case R.drawable.emo_im_sad_s:
            index = 1;
            break;

    	  case R.drawable.emo_im_undecided:
    	  case R.drawable.emo_im_undecided_s:
            index = 2;
            break;

    	  case R.drawable.emo_im_yelling:
    	  case R.drawable.emo_im_yelling_s:
            index = 3;
            break;

    	  case R.drawable.emo_im_surprised:
    	  case R.drawable.emo_im_surprised_s:
            index = 4;
            break;

    	  case R.drawable.emo_im_happy:
    	  case R.drawable.emo_im_happy_s:
            index = 5;
            break;

    	  case R.drawable.emo_im_winking:
    	  case R.drawable.emo_im_winking_s:
            index = 6;
            break;

    	  case R.drawable.emo_im_laughing:
    	  case R.drawable.emo_im_laughing_s:
            index = 7;
            break;
  
    	  case R.drawable.emo_im_angel:
    	  case R.drawable.emo_im_angel_s:
            index = 8;
            break;
          
    	  case R.drawable.emo_im_cool:
    	  case R.drawable.emo_im_cool_s:
            index = 9;
            break;

    	  default:
            index = 0;
            break;    		  
        }
        return (index);
    }

    public static int decideEmotionIconColor(int iconId)
    {
    	int color = 0;
    	switch (iconId)
    	{
          case R.drawable.emo_im_crying:
    	  case R.drawable.emo_im_crying_s:
            color = 0xff00479d;
            break;

    	  case R.drawable.emo_im_sad:
    	  case R.drawable.emo_im_sad_s:
            color = 0xff0086c9;
            break;

    	  case R.drawable.emo_im_undecided:
    	  case R.drawable.emo_im_undecided_s:
            color = 0xff0099cb;
            break;

    	  case R.drawable.emo_im_yelling:
    	  case R.drawable.emo_im_yelling_s:
            color = 0xff80cde3;
            break;

    	  case R.drawable.emo_im_surprised:
    	  case R.drawable.emo_im_surprised_s:
            color = 0xffd4ecf3;
            break;

    	  case R.drawable.emo_im_happy:
    	  case R.drawable.emo_im_happy_s:
            color = 0xffe5e51a;
            break;

    	  case R.drawable.emo_im_winking:
    	  case R.drawable.emo_im_winking_s:
            color = 0xffabcd03;
            break;

    	  case R.drawable.emo_im_laughing:
    	  case R.drawable.emo_im_laughing_s:
            color = 0xffb5aa5f;
            break;
  
    	  case R.drawable.emo_im_angel:
    	  case R.drawable.emo_im_angel_s:
            color = 0xff929d3c;
            break;
          
    	  case R.drawable.emo_im_cool:
    	  case R.drawable.emo_im_cool_s:
            color = 0xff22833a;
            break;

    	  default:
            color = 0xff000000;
            break;    		  
        }
        return (color);
    }
}
