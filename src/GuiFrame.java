import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import net.miginfocom.swing.MigLayout;

public class GuiFrame extends JFrame {

  public JTextArea console;

  public GuiFrame(){
    super();
    JPanel root = new JPanel();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("RouteMachine");
    setResizable(false);
    setSize(new Dimension(1500, 900));
    root.setLayout(new MigLayout());
    //Устнаовка 4 текстовых полей
    ArrayList<JTextField> jtf = new ArrayList<>();
    ArrayList<JLabel> jl = new ArrayList<>();
    for (int i= 0; i < 4; ++i){
      jtf.add(new JTextField());
      jtf.get(i).setPreferredSize(new Dimension(100,25));
      jl.add(new JLabel("Coordinate #"+String.valueOf(i) + ":"));
    }
    console = new JTextArea();//Сюда печатаем ошибки и список координат
    //console.setPreferredSize(new Dimension(500,300));
   // console.setMaximumSize(new Dimension(500,10000));
    console.setLineWrap(true);
    console.setWrapStyleWord(true);
    console.setColumns(128);
    JScrollPane sp = new JScrollPane(console);
    sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    sp.setPreferredSize(new Dimension(500,300));
    sp.setMaximumSize(new Dimension(500,10000));
    //Поехали
    root.add(new JLabel("Distance: "));
    root.add(new JLabel("Duration: "),"wrap 15px");
    JButton route = new JButton("Route");
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
  }
  //TO DO: Написать листенеры для кнопок
}
