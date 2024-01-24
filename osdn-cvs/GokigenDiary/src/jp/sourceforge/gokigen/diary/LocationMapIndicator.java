package jp.sourceforge.gokigen.diary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author MRSa
 *
 */
public class LocationMapIndicator extends ItemizedOverlay<OverlayItem>
{
    private List<GeoPoint> markers = new ArrayList<GeoPoint>();

    /**
     * 
     * @param defaultMarker
     */
    public LocationMapIndicator(Drawable defaultMarker, Context context)
    {
        super(boundCenterBottom(defaultMarker));
        populate();
    }

    /**
     * 
     * @param defaultMarker
     */
    public LocationMapIndicator(Drawable defaultMarker)
    {
        super(boundCenterBottom(defaultMarker));
        populate();
    }

    /**
     * 
     */    
    @Override
    protected OverlayItem createItem(int i)
    {
        GeoPoint point = markers.get(i);
        return new OverlayItem(point, "", "");
    }

    /**
     * 
     */
    @Override
    public int size()
    {
        return (markers.size());
    }

    /**
     * 
     * @param point
     */
    public void addPoint(GeoPoint point)
    {
        markers.add(point);
        populate();
    }

    /**
     * 
     * @param index
     */
    public void removePoint(int index)
    {
        if (index < size())
        {
           markers.remove(index);
           populate();
        }
    }

    /**
     * 
     */
    public void clearPoint()
    {
        markers.clear();
        populate();
    }
}