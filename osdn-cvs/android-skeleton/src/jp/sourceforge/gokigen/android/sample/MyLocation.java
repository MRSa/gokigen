package jp.sourceforge.gokigen.android.sample;

import android.location.Location;

/**
 *   �ʒu�����L������N���X
 *   (Location�N���X�̒��g���ύX�ł��Ȃ��̂�...)
 * 
 * @author MRSa
 *
 */
public class MyLocation
{
    private double           lastLatitude = 0.0;
    private double           lastLongitude = 0.0;
    private long             lastChangedTime   = 0;
    private long             lastLocationTime  = 0;
    private float            lastAccuracy = 0;
    private double           lastAltitude = 0.0;
    private float            lastSpeed    = 0;
    private float            lastBearing  = 0;
    
    private String            locationInfo = "";
    
    /**
     *  �R���X�g���N�^
     */
    public MyLocation()
    {
    }

    /**
     *   �ʒu�����L�^����
     * @param location
     * @return �ʒu���ύX���ꂽ(true) / �ʒu���O�̂܂�(false)
     */
    public boolean setLocation(Location location)
    {
        boolean result = false;
        double latitude = Math.abs(lastLatitude - location.getLatitude());
        double longitude = Math.abs(lastLongitude - location.getLongitude());

        if ((latitude > 0.0001)||(longitude > 0.0001))
        {
            lastChangedTime = location.getTime();
        }
        
        lastLocationTime = location.getTime();
        // if ((latitude > 0.0001)||(longitude > 0.0001))
        {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            lastAccuracy = location.getAccuracy();
            lastAltitude = location.getAltitude();
            lastSpeed = location.getSpeed();
            lastBearing = location.getBearing();

            result = true;
        }

        locationInfo = GeocoderWrapper.getLocationString(location.getLatitude(), location.getLongitude());
        return (result);
    }

    /**
     *  �ʒu�����X�V����
     * 
     * @param time
     * @param latitude
     * @param longitude
     * @return
     */
    public void setLocation(long time, double latitude, double longitude)
    {
        lastLocationTime = time;
        lastLatitude = latitude;
        lastLongitude = longitude;
    }
    
    /**
     *  �ʒu��񕶎��������������
     * 
     * @param info �ʒu��񕶎���
     */
    public void setLocationInfo(String info)
    {
    	locationInfo = info;
    }    

    /**
     *  �ʒu��񕶎������������
     * 
     * @return  �ʒu��񕶎���
     */
    public String getLocationInfo()
    {
    	return (locationInfo);
    }

    /**
     *  
     * @return
     */
    public long getStayMinites()
    {
        return (((lastChangedTime - lastLocationTime) / 1000) / 60);
    }        
    
    /**
     *  �ʒu��񃁃b�Z�[�W����������
     * @return
     */
    public String getLocationSummaryString()
    {
        // Excel�p�̎���...
        double locTimeForExcel = (lastLocationTime / 86400000.0) + 25569.0 + 0.375;
        long   diffTime = lastLocationTime - lastChangedTime;

        //  �L�^�Ɏc���ʒu�����擾����
        String message = "X,";
        message = message + lastLocationTime  + ",";    // ����
        message = message + lastLatitude + ",";         // �ܓx
        message = message + lastLongitude + ",";        // �o�x
        message = message + lastAltitude + ",";         //
        message = message + lastBearing + ",";
        message = message + lastSpeed + ",";
        message = message + lastAccuracy + ",";
        message = message + lastChangedTime + ",";      // �ω����o����
        message = message + locTimeForExcel + ",";      // Excel�p����
        message = message + diffTime + "\r\n";

        return (message);
    }

    /**
     *   �ܓx���擾����
     * @return  �ܓx
     */
    public double getLatitude()
    {
        return (lastLatitude);
    }

    /**
     *   �o�x���擾����
     * @return  �o�x
     */
    public double getLongitude()
    {
        return (lastLongitude);
    }
    
    /**
     *  ���Ԃ��擾����
     * @return (�ʒu����)����
     */
    public long getTime()
    {
        return (lastLocationTime);
    }

}
