import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class GeoCoder {
  //Класс, хранящий методы для геокодирования - получения объекта по координатам и наооборот
  //Копирование кода - пока расплата за статичность

  public static void main(String[] args){
    Locale.setDefault(new Locale("en", "US"));
    GeoData gd = GeoCoder.getByCaption("ул. Гурьянова, 19к1, Москва, 109548");
    System.out.println("Caption: " + gd.getCaption() +  " latitude: " + String.valueOf(gd.getLatitude()) +" longitude: " + String.valueOf(gd.getLongitude()));
    GeoData gd2 = GeoCoder.getByCaption("Шоссейная ул., 2, Москва, 109548");
    System.out.println("Caption: " + gd2.getCaption() +  " latitude: " + String.valueOf(gd2.getLatitude()) +" longitude: " + String.valueOf(gd2.getLongitude()));
    ArrayList<GeoData> locations = new ArrayList<>();
    locations.add(gd);
    locations.add(gd2);
    RouteData routeData = GeoCoder.route(locations);
    if (routeData.getRoutePoints() != null){
      for (int i=0; i < routeData.getRoutePoints().size(); ++i){
        System.out.println("Coordinate #" +String.valueOf(i)+": "+ String.valueOf((routeData.getRoutePoints().get(i).getLatitude()) + "," +String.valueOf((routeData.getRoutePoints().get(i).getLongitude()))));
      }
    }
    System.out.println("Approximate distance: " + String.valueOf(routeData.getDistance()) + " meters");
    System.out.println("Approximate trip duration: " + String.valueOf(routeData.getDuration()) + " seconds");
  }


  public static GeoData getByCaption(String caption) {
    //Создает http запрос к сервису nominatim openstreetmaps и парсит ответ сервера
    //Возвращает null если ответ не был получем или места с таким названием не было найдено
    try {
      String encodedCaption = URLEncoder.encode(caption, "UTF-8");
      URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + encodedCaption + "&format=xml&limit=1");//Ограничиваемся первым результатом для простоты, они сортируются по важности и первый, вероятно, нужный.
      Document xmlDoc = loadXMLFromString(getServerResponse(url,false));
      XPathFactory xpathFact = XPathFactory.newInstance();
      XPath xpath = xpathFact.newXPath();
      float longitude = Float.parseFloat((String) xpath.evaluate("/searchresults/place[1]/@lon", xmlDoc, XPathConstants.STRING)); //Важно чтобы стояла локаль, в которой дробный разделитель это точка
      float latitude = Float.parseFloat((String) xpath.evaluate("/searchresults/place[1]/@lat", xmlDoc, XPathConstants.STRING));
      return new GeoData(caption, longitude, latitude);
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return null;
  }

  public static GeoData getByCoordinates(float longitude,float latitude){
    //Создает http запрос к сервису nominatim openstreetmaps и парсит ответ сервера
    //Возвращает null если ответ не был получем или места с таким названием не было найдено
    try {
      String encodedCaption = URLEncoder.encode(String.valueOf(latitude)+","+String.valueOf(longitude), "UTF-8");
      URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + encodedCaption + "&format=xml&limit=1");//Ограничиваемся первым результатом для простоты, они сортируются по важности и первый, вероятно, нужный.
        Document xmlDoc = loadXMLFromString(getServerResponse(url,false));
        XPathFactory xpathFact = XPathFactory.newInstance();
        XPath xpath = xpathFact.newXPath();
        String caption = (String) xpath.evaluate("/searchresults/place[1]/@display_name", xmlDoc, XPathConstants.STRING); //Важно чтобы стояла локаль, в которой дробный разделитель это точка
        return new GeoData(caption, longitude, latitude);
      }
      catch (Exception e) {
        e.printStackTrace(System.out);
    }
    return null;
  }

  public static RouteData route(ArrayList<GeoData> locations){
    //Строит точечный маршрут между указанными в locations точками
    String coordintates = "",extractedData = null;
    int time, distance;
    ArrayList<GeoData> routePoints = new ArrayList<>();
    for (int i = 0; i < locations.size(); ++i){
      coordintates += String.valueOf(locations.get(i).getLongitude())+","+String.valueOf(locations.get(i).getLatitude());
      if(i!=locations.size()-1){
        coordintates +=";";
      }
    }
    try {
      String encodedCoordinates = URLEncoder.encode(coordintates, "UTF-8");
      URL url = new URL("http://router.project-osrm.org/route/v1/driving/"+encodedCoordinates+"?geometries=geojson&overview=simplified");
      extractedData = getServerResponse(url,true);
    }
    catch (Exception e){
      e.printStackTrace(System.out);
    }
    if (extractedData != null){
      JSONObject obj = new JSONObject(extractedData);
      JSONArray arr = obj.getJSONArray("routes");
      JSONObject arrElem = arr.getJSONObject(0);
      JSONObject geom = arrElem.getJSONObject("geometry");
      JSONArray coords = geom.getJSONArray("coordinates");
      //JSONObject jsonDuration = arrElem.getJSONObject("duration");
      //JSONObject jsonDistance = arrElem.getJSONObject("distance");
      distance = (int)arrElem.getFloat("distance");
      time = (int) arrElem.getFloat("duration");
      for (int i = 0; i < coords.length(); ++i){
        routePoints.add(new GeoData("",(Double) coords.getJSONArray(i).get(0),(Double) coords.getJSONArray(i).get(1)));
      }
      return  new RouteData(routePoints,distance,time);
    }
    return null;
  }

  private static String getServerResponse(URL url, boolean repeat) {
    //Устанавливает соединение по указанному URL и возрващает ответ от сервета в виде строки
    do {
      try {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setDoOutput(true);
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);
        con.connect();
        String response = con.getResponseMessage();
        if (response.equals("OK")) {
          //Все номрмально
          //Считывание ответа сервера
          BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
          StringBuilder sb = new StringBuilder();
          String output;
          while ((output = br.readLine()) != null) {
            sb.append(output);
          }
          String extractedData = sb.toString(); //На деле это xml, представленный строкой
          con.disconnect(); //Разрываем соединение по первой возможности
          return extractedData;
        }
      } catch (IOException ioe) {
        System.out.println("Сервер не отвечает");
        return null;
      }
    }while (repeat);
    return null;
  }

  private static Document loadXMLFromString(String xml) throws Exception {
    //Превращает строку в xml документ
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(xml));
    return builder.parse(is);
  }

  private GeoCoder(){
    //Запрещаем создание таких объектов
  }

}
