import java.util.ArrayList;

public class RouteData {
  //Все данные получаются с сервера osrm
  //Объекты RouteData не нуждаются в set методах
  private ArrayList<GeoData> routePoints; //Массив данных
  private int distance; //Расстояние в метрах, округлено до целого отсечением ввиду невысокой погрешности
  private int duration; //Приблизительное время в секундах, округлено до целого отсечением ввиду невысокой погрешности

  public RouteData(ArrayList<GeoData> routePoints,int distance, int duration){
    this.routePoints = new ArrayList<>(routePoints);
    this.distance = distance;
    this.duration = duration;
  }

  public ArrayList<GeoData> getRoutePoints(){
    return new ArrayList<>(routePoints);
  }

  public int getDistance(){
    return distance;
  }

  public int getDuration(){
    return duration;
  }
}
