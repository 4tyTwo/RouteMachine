import java.util.ArrayList;

public class RouteData {
  //Все данные получаются с сервера osrm
  //Объекты RouteData не нуждаются в set методах
  private ArrayList<GeoData> routePoints; //Массив данных
  private int distance; //Расстояние в метрах, округлено до целого отсечением ввиду невысокой погрешности
  private int time; //Приблизительное время в секундах, округлено до целого отсечением ввиду невысокой погрешности

  public RouteData(ArrayList<GeoData> routePoints,int distance, int time){
    this.routePoints = new ArrayList<>(routePoints);
    this.distance = distance;
    this.time = time;
  }

  public ArrayList<GeoData> getRoutePoints(){
    return routePoints;
  }
  
}
