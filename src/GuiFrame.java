import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.util.Precision;

public class GuiFrame extends JFrame {

  private class querryThread implements Runnable{
    public void run(){
      ArrayList<GeoData> routePoints = new ArrayList<>();
      for (int i = 0; i < 4; ++i) {
        if (!jtf.get(i).getText().equals(""))
          if (jtf.get(i).getText().matches("\\d{1,2}[.]\\d+[,]-?\\d{1,2}[.]\\d+")) {
            String[] partions = jtf.get(i).getText().split(",");
            routePoints.add(GeoCoder.getByCoordinates(Float.valueOf(partions[0]), Float.valueOf(partions[1]))); //Longitude, Latitude
          } else {
            GeoData tmp = GeoCoder.getByCaption(jtf.get(i).getText());
            if (tmp.getState() != tmp.STATE_NOT_FOUND)
              routePoints.add(tmp);
          }
      }
      if (routePoints.size() != 0){
        if (!console.getText().equals(""))
          console.append("\n\n\n\n");
        console.append("Выполнется построение пути между пунктами: \n");
        for (int i = 0; i < routePoints.size(); ++i){
          console.append(routePoints.get(i).getCaption() + " Координаты: " + String.valueOf( routePoints.get(i).getLatitude()) + "," + String.valueOf( routePoints.get(i).getLongitude())+ "\n");
        }
      }
      RouteData data = GeoCoder.route(routePoints);
      if (data != null){
        for (int i=0; i < data.getRoutePoints().size(); ++i){
          console.append("Точка пути №" +String.valueOf(i+1)+": "+ String.valueOf((data.getRoutePoints().get(i).getLatitude()) + "," +String.valueOf((data.getRoutePoints().get(i).getLongitude()))) + "\n");
        }
        float distance = data.getDistance(), duration = data.getDuration();
        String distanceMeausureUnit = "м.", durationMeasureUnit = "сек.";
        if (duration > 60)
          if (duration > 1800) {
            duration = Precision.round(duration / 3600,1); //Часов
            durationMeasureUnit = "ч.";
          }
          else {
            duration = Precision.round(duration / 60,1); //Минут
            durationMeasureUnit = "мин.";
          }
        if (distance > 500) {
          distance = Precision.round(distance / 1000,1);
          distanceMeausureUnit = "км.";
        }
        console.append("Примерное расстояние: " + String.valueOf(distance) + distanceMeausureUnit + "\n");
        console.append("Примерное время в пути: " + String.valueOf(duration) + durationMeasureUnit +"\n");
      }
      else{
        console.append("Путь не найден. Возможно допущена ошибка в введенных координатах\n");
      }
    }
  }

  public JTextArea console;
  private ArrayList<JTextField> jtf;
  private ArrayList<JLabel> jl;
  public GuiFrame(){
    super();
    JPanel root = new JPanel();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("RouteMachine");
    setResizable(false);
    setSize(new Dimension(1500, 900));
    root.setLayout(new MigLayout());
    //Устнаовка 4 текстовых полей
    jtf = new ArrayList<>();
    jl = new ArrayList<>();
    for (int i= 0; i < 4; ++i){
      jtf.add(new JTextField());
      jtf.get(i).setPreferredSize(new Dimension(130,25));
      jl.add(new JLabel("Точка маршрута №"+String.valueOf(i+1) + ":"));
    }
    console = new JTextArea();//Сюда печатаем ошибки и список координат
    //console.setPreferredSize(new Dimension(500,300));
   // console.setMaximumSize(new Dimension(500,10000));
    console.setLineWrap(true);
    console.setWrapStyleWord(true);
    console.setColumns(128);
    console.setFont(new Font("Consolas", Font.PLAIN,14));
    console.setEditable(false);
    JScrollPane sp = new JScrollPane(console);
    sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    sp.setPreferredSize(new Dimension(500,300));
    sp.setMaximumSize(new Dimension(500,10000));
    //Поехали
    //root.add(new JLabel("Distance: "));
    //root.add(new JLabel("Duration: "),"wrap 15px");
    JButton route = new JButton("Построить маршрут");
    route.setPreferredSize(new Dimension(60,30));
    root.add(sp,"span 2 5");
    root.add(jl.get(0));
    root.add(jtf.get(0),"wrap 15px");
    root.add(jl.get(1));
    root.add(jtf.get(1),"wrap 15px");
    root.add(jl.get(2));
    root.add(jtf.get(2),"wrap 15px");
    root.add(jl.get(3));
    root.add(jtf.get(3),"wrap 15px");
    root.add(route, "span");
    add(root);
    pack();
    setVisible(true);

    route.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        new Thread(new querryThread()).start();
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }
    });

  }
  //TO DO: Написать листенеры для кнопок
}
