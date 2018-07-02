import net.miginfocom.swing.MigLayout;
import org.apache.commons.math3.util.Precision;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GuiFrame extends JFrame {

  private class queryThread implements Runnable{
    public void run(){
      ArrayList<GeoData> routePoints = new ArrayList<>();
      for (int i = 0; i < 4; ++i) {
        if (!inputFields.get(i).getText().equals("") && inputFields.get(i).isVisible())
          if (inputFields.get(i).getText().matches("\\d{1,2}[.]\\d+[,]-?\\d{1,2}[.]\\d+")) {
            String[] partions = inputFields.get(i).getText().split(",");
            routePoints.add(GeoCoder.getByCoordinates(Float.valueOf(partions[1]), Float.valueOf(partions[0]))); //Longitude, Latitude
          } else {
            GeoData tmp = GeoCoder.getByCaption(inputFields.get(i).getText());
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
        String distanceMeasureUnit = " м.", durationMeasureUnit = " сек.";
        if (duration > 60)
          if (duration > 1800) {
            duration = Precision.round(duration / 3600,1); //Часов
            durationMeasureUnit = "ч.";
          }
          else {
            duration = Precision.round(duration / 60,1); //Минут
            durationMeasureUnit = " мин.";
          }
        if (distance > 500) {
          distance = Precision.round(distance / 1000,1);
          distanceMeasureUnit = " км.";
        }
        console.append("Примерное расстояние: " + String.valueOf(distance) + distanceMeasureUnit + "\n");
        console.append("Примерное время в пути: " + String.valueOf(duration) + durationMeasureUnit +"\n");
      }
      else{
        console.append("Путь не найден. Возможно допущена ошибка в введенных координатах\n");
      }
    }
  }

  public JTextArea console;
  private ArrayList<JTextField> inputFields; //В массивах удобнее хранить повторяющиеся поля
  private ArrayList<JLabel> routeLabels;
  private int numberOfPoints;
  private Dimension screenSize;

  public GuiFrame(){
    super();
    //В конструкторе полностью собирается форма и создаются все ее элементы
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    numberOfPoints = 2;
    JPanel root = new JPanel();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("RouteMachine");
    setResizable(false);
    setSize(new Dimension(1500, 900));
    root.setLayout(new MigLayout());
    //Устнаовка 4 текстовых полей
    inputFields = new ArrayList<>();
    routeLabels = new ArrayList<>();
    for (int i= 0; i < 4; ++i){
      inputFields.add(new JTextField());
      inputFields.get(i).setPreferredSize(new Dimension(130,25));
      routeLabels.add(new JLabel("Точка маршрута №"+String.valueOf(i+1) + ":"));
      if(i>1){
        inputFields.get(i).setVisible(false);
        routeLabels.get(i).setVisible(false);
      }
    }
    JButton addRoutePoint = new JButton("Добавить точку");
    addRoutePoint.addMouseListener(new addButtonMouseListener());
    JButton removeRoutePoints = new JButton("Удалить точку");
    JButton help = new JButton("Справка");
    help.addMouseListener(new helpButtonMouseListener());
    removeRoutePoints.addMouseListener(new removeButtonMouseListener());
    console = new JTextArea();//Сюда печатаем ошибки и список координат
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
    JButton route = new JButton("Построить маршрут");
    route.setPreferredSize(new Dimension(60,30));
    root.add(sp,"span 2 7");
    for (int i = 0; i < 4; ++i) {
      root.add(routeLabels.get(i));
      root.add(inputFields.get(i),"wrap 15px");
    }
    root.add(addRoutePoint);
    root.add(removeRoutePoints,"wrap 15px");
    root.add(route, "span");
    root.add(help, "span");
    add(root);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);

    route.addMouseListener(new RouteButtonListener());

  }

  private class RouteButtonListener implements MouseListener{
    //Запускает поток с выполнением считывания данных с формы, запросом к серверу и выводу данных на экран
    public void mouseClicked(MouseEvent e) {
      new Thread(new queryThread()).start();
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
  };

  private class addButtonMouseListener implements MouseListener{
    //Добавляет ввод координаты
    public void mouseClicked(MouseEvent e) {
      if (numberOfPoints < 4){
        inputFields.get(numberOfPoints).setVisible(true);
        routeLabels.get(numberOfPoints).setVisible(true);
        ++numberOfPoints;
      }
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
  };

  private class removeButtonMouseListener implements MouseListener{
    //Прячет ввод последней координаты
    public void mouseClicked(MouseEvent e) {
      if (numberOfPoints > 2){
        inputFields.get(numberOfPoints-1).setVisible(false);
        inputFields.get(numberOfPoints-1).setText("");
        routeLabels.get(numberOfPoints-1).setVisible(false);
        --numberOfPoints;
      }
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
  };
  private class helpButtonMouseListener implements MouseListener{
    public void mouseClicked(MouseEvent e) {
      JFrame helpFrame = new JFrame();
      helpFrame.setResizable(false);
      helpFrame.setTitle("Справка");
      helpFrame.setSize(new Dimension(760,350));
      helpFrame.setMinimumSize(new Dimension(760,350));
      JTextArea helper = new JTextArea();
      helper.setEditable(false);
      helper.setLineWrap(true);
      helper.setWrapStyleWord(true);
      String helpText = "Данное приложение реализует построение пути между произвольными точками в количестве не более 4.\nТочки вводятся в поля в правой части окна приложения" +
          " в виде названий на естественном языке или координат в формате широта,долгота.\nМаршрут прокладывается последовательно в соответствии с очередностью введеных точек.\n" +
          "Точки можно добавлять и удалять при помощи соотвествующих кнопок.\n" +
          "Если для ввода точек использовался естественный язык, то сервер сам попытается найти объект по внутренним параметрам поиска.\n" +
          "Для вывода информации на экран используется консоль в левой части окна.\n" +
          "После нажатия на кнопку 'Построить маршрут' ожидайте вывода в консоль.\n" +
          "Время, необходимое на получение результата, зависит от доступности сервера и иногда может составлять более минуты\n" +
          "\nПриложение разработано в рамках выполнения тестового задания для XPO Logistics Топорковым Игорем www.github.com/4tyTwo";
      helper.setText(helpText);
      helper.setFont(new Font("Calibri", Font.PLAIN,16));
      helpFrame.add(helper);
      helpFrame.pack();
      helpFrame.setLocationRelativeTo(null);
      helpFrame.setVisible(true);
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
  };
}
