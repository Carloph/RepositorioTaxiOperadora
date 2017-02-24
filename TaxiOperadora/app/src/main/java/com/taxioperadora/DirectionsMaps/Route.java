package com.taxioperadora.DirectionsMaps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
/**
 * Created by carlos on 22/02/17.
 */

public class Route {


        public Distance distance;
        public Duration duration;
        public String endAddress;
        public LatLng endLocation;
        public String startAddress;
        public LatLng startLocation;
        public List<LatLng> points;


}
