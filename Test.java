import java.io.*;

class Test {
    public static void main(String args[]) throws Exception{
		
		double lonA = -73.6391;
		double latA = 45.5454;
		double lonB = 39.8579118;
		double latB = 21.3890824;

		double phi = Math.abs(Math.tan( latB / 2 + Math.PI / 4 ) / Math.tan( latA / 2 + Math.PI / 4));
		double delta_phi = Math.log( phi );
		double delta_lon = Math.abs( lonA - lonB );
		double bearing = Math.atan2( delta_lon ,  delta_phi );
		
		double dy = latB - latA;
        double dx = Math.cos(Math.PI / 180 * latA) * (lonB - lonA);
        double angle = Math.atan2(dy, dx);
        
        System.out.println(dy);
        System.out.println(dx);
        System.out.println(angle);
		
		lonA =  -94.581213;
		latA = 39.099912;
		lonB = -90.200203;
		latB = 38.627089;
		
		
		double d = lonA - lonB;
		double x = Math.cos(latB) * Math.sin(d);
		//double y = Math.cos(latA) * Math.sin(latB) - Math.sin(latA) * Math.cos(latB) * Math.cos(d);
		double y = Math.cos(39.099912) * Math.sin(38.627089) - Math.sin(39.099912) * Math.cos(38.627089) * Math.cos(4.38101);
		double b = Math.atan2(x, y);
		
		System.out.println(phi);
		System.out.println(delta_phi);
		System.out.println(delta_lon);
		System.out.println(bearing);
		
		//System.out.println(d);
		//System.out.println(x);
		//System.out.println(y);
		//System.out.println(b);
		System.out.println("======================================");
		System.out.println(getBearing(latA, lonA, latB, lonB));
		System.out.println(getBearing(43.682213, -70.450696, 43.682194, -70.450769));
		System.out.println("======================================");
		System.out.println(_initial(latA, lonA, latB, lonB));
		System.out.println(_initial(43.682213, -70.450696, 43.682194, -70.450769));
		System.out.println("======================================");
		System.out.println(_final(latA, lonA, latB, lonB));
		System.out.println(_final(43.682213, -70.450696, 43.682194, -70.450769));
    }
    
    public static double radians(double n) {
	  return n * (Math.PI / 180);
	}
	public static double degrees(double n) {
	  return n * (180 / Math.PI);
	}

	public static double getBearing(double startLat,double startLong,double endLat,double endLong){
	  startLat = radians(startLat);
	  startLong = radians(startLong);
	  endLat = radians(endLat);
	  endLong = radians(endLong);

	  double dLong = endLong - startLong;

	  double dPhi = Math.log(Math.tan(endLat/2.0+Math.PI/4.0)/Math.tan(startLat/2.0+Math.PI/4.0));
	  if (Math.abs(dLong) > Math.PI){
		if (dLong > 0.0)
		   dLong = -(2.0 * Math.PI - dLong);
		else
		   dLong = (2.0 * Math.PI + dLong);
	  }

	  return (degrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;
	}
	
	static public double _initial (double lat1, double long1, double lat2, double long2)
	{
		return (_bearing(lat1, long1, lat2, long2) + 360.0) % 360;
	}

	static public double _final(double lat1, double long1, double lat2, double long2)
	{
		return (_bearing(lat2, long2, lat1, long1) + 180.0) % 360;
	}

	static public double _bearing(double lat1, double long1, double lat2, double long2)
	{
		double degToRad = Math.PI / 180.0;
		double phi1 = lat1 * degToRad;
		double phi2 = lat2 * degToRad;
		double lam1 = long1 * degToRad;
		double lam2 = long2 * degToRad;

		return Math.atan2(Math.sin(lam2-lam1)*Math.cos(phi2),
			Math.cos(phi1)*Math.sin(phi2) - Math.sin(phi1)*Math.cos(phi2)*Math.cos(lam2-lam1)
		) * 180/Math.PI;
	}
}


