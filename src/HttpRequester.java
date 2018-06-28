import java.util.*;


public class HttpRequester {
    public static void main(String[] args){
      Locale.setDefault(new Locale("en", "US"));
      GeoData gd = GeoCoder.getByCaption("ул. Гурьянова, 19к1, Москва, 109548");
      System.out.println("Caption: " + gd.getCaption() +  " latitude: " + String.valueOf(gd.getLatitude()) +" longitude: " + String.valueOf(gd.getLongitude()));
      GeoData gd2 = GeoCoder.getByCaption("Шоссейная ул., 2, Москва, 109548");
      System.out.println("Caption: " + gd2.getCaption() +  " latitude: " + String.valueOf(gd2.getLatitude()) +" longitude: " + String.valueOf(gd2.getLongitude()));
      ArrayList<GeoData> locations = new ArrayList<>(),routePoints;
      locations.add(gd);
      locations.add(gd2);
      routePoints = GeoCoder.route(locations);
      if (routePoints != null){
        for (int i=0; i < routePoints.size(); ++i){
          System.out.println("Coordinates: "+ String.valueOf(routePoints.get(i).getLatitude()) + "," +String.valueOf(routePoints.get(i).getLongitude()));
        }
      }
    }
}
