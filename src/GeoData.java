import org.apache.commons.math3.util.Precision;

public final class GeoData {
  //Класс, обеспечивающий хранение основной  информации о географическом объекте
  public final static int STATE_NOT_FOUND = -1;
  public final static int STATE_OK = 0;
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

  public int getState(){
    if (this.getCaption().equals("NOT FOUND"))
      if (Double.valueOf(getLatitude()).equals(Double.valueOf(getLongitude())) && Double.valueOf(getLatitude()).equals(Double.valueOf(0.0))) //Если обе координаты равны нулю, и имя NOT FOUND, то это точно неверная geodata
        return STATE_NOT_FOUND;
    return  STATE_OK;
  }

}
