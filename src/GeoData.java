import org.apache.commons.math3.util.Precision;

public class GeoData {
  //Класс, обеспечивающий хранение основной  информации о географическом объекте
  private double longitude,latitude;
  private String caption;

  public GeoData(String caption,double longitude,double latitude) {
    this.longitude = Precision.round(longitude,5); this.latitude = Precision.round(latitude,5); this.caption = caption;
  }

  public String getCaption(){
    return caption;
  }

  public double getLongitude(){
    return longitude;
  }

  public double getLatitude(){
    return latitude;
  }

}
