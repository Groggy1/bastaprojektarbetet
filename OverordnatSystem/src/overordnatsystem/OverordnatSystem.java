/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package overordnatsystem;

import java.util.Arrays;
import java.util.LinkedList;
//import javax.microedition.io.Connector;
//import javax.microedition.io.StreamConnection;

/**
 *
 * @author oskst764
 */
public class OverordnatSystem {

    DataStore ds;
    DataStore ds3;
    ControlUI cui;

    public DataStore optorderlista(DataStore ds3, OptPlan op) {
        //Massa bra variabler
        LinkedList<Vertex> path;
        DataStore ds4 = new DataStore();
        ds4.orders = ds3.orders;
        int start, stop, diff, mindiff;
        //De orderpar som redan betjänats
        int[] nextnode = new int[20];
        Arrays.fill(nextnode, 50);
        for (int i = 0; i < ds3.orders; i++) {
            boolean notj;
            int[] notja = new int[20];
            Arrays.fill(notja, 50);
            //Undviker att en otillåten ordning skapas då orderlistan optimeras.
            //Ser med andra ord till att endast tomma hyllor används vid lämning
            do {
                notj = false;
                mindiff = 1000000000;
                for (int j = 0; j < ds3.orders; j++) {
                    boolean stopp = true;
                    for (int q = 0; q < nextnode.length; q++) {
                        //Ser till så onödiga beräkningar inte genomförs och på så sätt också onödiga diff räknas ut samt otillåtna kombinationer skapas
                        if (nextnode[q] == j || notja[q] == j) {
                            stopp = false;
                        }
                    }
                    if (stopp) {
                        diff = 0;
                        //Om första förflyttningen, kör från utlämningsplatsen
                        if (i == 0) {
                            //Erhåll start/stop nod
                            start = ds.shelfNode[0];
                            stop = ds.shelfNode[ds3.orderStart[j]];
                            if (start != stop) {
                                //System.out.println("Start " + start + " Stop " + stop);
                                //Planera via dijkstras färdvägen
                                path = op.createPlan(start, stop, 1, false);
                                //beräkna kostnaden för den optimala färdvägen mellan start och stop
                                for (int k = 0; k < path.size() - 1; k++) {
                                    diff = diff + (int) Math.max(Math.abs(ds.nodeY[Integer.parseInt(path.get(k).getId()) - 1] - ds.nodeY[Integer.parseInt(path.get(k + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path.get(k).getId()) - 1] - ds.nodeX[Integer.parseInt(path.get(k + 1).getId()) - 1]));
                                    //System.out.println("Integer.parseInt(path.get(k).getId()) " + Integer.parseInt(path.get(k).getId()));
                                }
                            } else if (start == stop && start == 24) {
                                diff = 0;
                                //System.out.println("Hej diff = 0 start");
                            }
                        } else {
                            //Om inte första gången, använd den senaste slutnoden som planerats
                            //System.out.println("i " + i);
                            //Erhåll start/stop nod
                            start = ds.shelfNode[ds4.orderEnd[i - 1]];
                            stop = ds.shelfNode[ds3.orderStart[j]];
                            if (start != stop) {
                                //System.out.println("Start " + start + " Stop " + stop);
                                //Planera via dijkstras färdvägen
                                path = op.createPlan(start, stop, 1, false);
                                //beräkna kostnaden för den optimala färdvägen mellan start och stop
                                for (int k = 0; k < path.size() - 1; k++) {
                                    diff = diff + (int) Math.max(Math.abs(ds.nodeY[Integer.parseInt(path.get(k).getId()) - 1] - ds.nodeY[Integer.parseInt(path.get(k + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path.get(k).getId()) - 1] - ds.nodeX[Integer.parseInt(path.get(k + 1).getId()) - 1]));
                                    //System.out.println("Integer.parseInt(path.get(k).getId()) " + Integer.parseInt(path.get(k).getId()));
                                }
                            } else if (start == stop && start == 24) {
                                diff = 0;
                                //System.out.println("Hej diff = 0 inte start");
                            }
                        }
                        System.out.println("Diff " + diff + " mindiff " + mindiff);
                        System.out.println("Start " + start + " stop " + stop);
                        System.out.println("i " + i + " j " + j);
                        if (diff < mindiff) {
                            //Kontrolerar att den valda start/stopp noden är ok med avseende på tomma/fulla hyllor
                            for (int k = 0; k < j - 1; k++) {
                                if (ds3.orderEnd[j] == ds3.orderStart[k]) {
                                    notj = true;
                                    notja[k] = j;
                                    //System.out.println("CPCPCPCPCPCP! k " + k + " j " + j);
                                }
                            }
                            //System.out.println("Inne i mindiff");
                            mindiff = diff;
                            nextnode[i] = j;
                            ds4.orderStart[i] = ds3.orderStart[j];
                            ds4.orderEnd[i] = ds3.orderEnd[j];
                            //System.out.println(diff + " next node " + nextnode[i]);
                            //System.out.println("ds4.orderStart[i] " + ds4.orderStart[i]);
                            //System.out.println("ds4.orderEnd[i] " + ds4.orderEnd[i]);
                        }
                    }
                }
            } while (notj);
        }
        return ds4;
    }

    public DataStore onodigaforflytt(DataStore ds2) {
        //Det här är bra skit, tar bort onödiga förflyttningar
        boolean plockatorder = false;
        for (int i = 0; i < ds3.orders; i++) {
            System.out.println("\n\n\n" + "i " + i);
            for (int j = i + 1; j < ds3.orders; j++) {
                System.out.println("ds2.orderStart[j]\t" + ds2.orderStart[j]);
                System.out.println("ds2.orderEnd[j]\t\t" + ds2.orderEnd[j]);
                System.out.println(" j " + j);
                if (ds2.orderStart[j] == ds2.orderEnd[i]) {
                    ds3.orderStart[j] = ds2.orderStart[i];
                    ds3.orderEnd[j] = ds2.orderEnd[j];
                    //Om onödig förflyttning tagits bort är det bra, därför ska man inte returnera ds2 :)
                    plockatorder = true;
                    break;
                } else if (j == ds3.orders - 1) {
                    ds3.orderStart[i] = ds2.orderStart[i];
                    ds3.orderEnd[i] = ds2.orderEnd[i];
                }
            }
        }
        if (!plockatorder) {
            return ds2;
        }
        return ds3;
    }

    OverordnatSystem() {
        double trip = 0;
        double trip1 = 0;
        double trip2 = 0; 
        ds = new DataStore();
        DataStore ds2 = new DataStore();
        DataStore ds4 = new DataStore();
        DataStore ds5 = new DataStore();
        DataStore ds6 = new DataStore();
        ds3 = new DataStore();

        //läser in lagernätet och orderlistan
        ds.setFileName("C:/Users/Groggy/Documents/GitHub/bastaprojektarbetet/OverordnatSystem/Lagernatverk_20130213.csv");
        ds.readNet();
        ds.setFileName("C:/Users/Groggy/Documents/GitHub/bastaprojektarbetet/OverordnatSystem/orders_comb.csv");
        ds.readOrders();
        ds2.setFileName("C:/Users/Groggy/Documents/GitHub/bastaprojektarbetet/OverordnatSystem/orders1.csv");
        ds2.readOrders();
        ds5.setFileName("C:/Users/Groggy/Documents/GitHub/bastaprojektarbetet/OverordnatSystem/orders2.csv");
        ds5.readOrders();

        //startar det grafiska gränssnittet
        cui = new ControlUI(ds);
        cui.setVisible(true);
        cui.showStatus();

        GuiUpdate g1 = new GuiUpdate(ds, cui);
        Thread t2 = new Thread(g1);
        t2.start();

        //Skriver ut den ej optimerade orderlistan
        cui.jTextArea2.append("Ej optimerad orderlista:\n");
        for (int j = 0; j < ds2.orders; j++) {
            if (ds2.orderStart[j] != ds2.orderEnd[j]) {
                cui.jTextArea2.append("" + ds2.orderStart[j]);
                cui.jTextArea2.append(" " + ds2.orderEnd[j] + "\n");
                System.out.println("");
            }
        }


        cui.jTextArea4.append("Ej optimerad orderlista:\n");
        for (int j = 0; j < ds5.orders; j++) {
            if (ds5.orderStart[j] != ds5.orderEnd[j]) {
                cui.jTextArea4.append("" + ds5.orderStart[j]);
                cui.jTextArea4.append(" " + ds5.orderEnd[j] + "\n");
                System.out.println("");
            }
        }

        //Olika nödvändiga variabler tilldelas viktiga värden som de inte klarar sig utan
        ds3 = ds2;
        ds4.orders = ds2.orders;
        ds4.fileName = ds2.fileName;
        ds6 = ds5;

        System.out.println("\n\n\n\n\n");

        System.out.println("\n\n");
        int start1 = 0, start2 = 0;
        int stop1 = 0, stop2 = 0;
        OptPlan op = new OptPlan(ds);

        LinkedList<Vertex> path1 = null;
        LinkedList<Vertex> path2 = null;

        //Optimerar orderlistan genom att ta bort onödiga förflyttningar och ordnar ordrarna på så sätt att avståndet som körs utan låda minimeras
        //ds6 = this.onodigaforflytt(ds5);
        //ds6 = this.optorderlista(ds6, op);
        Arrays.fill(ds.notoknumber, 500);
        //ds3 = this.onodigaforflytt(ds2);
        //ds3 = this.optorderlista(ds3, op);

        //skriv ut den optimerade orderlistan i orderlistafönstret
            /*for(int i = 0; i < ds3.orders; i++) {
         cui.jTextArea2.append(ds3.arcStart[i] + " " + ds3.arcEnd[i]);
         }*/

        //skriv ut den optimerade orderlistan i orderlistafönstret
        cui.jTextArea2.append("Optimerad orderlista:\n");
        for (int j = 0; j < ds3.orders; j++) {
            if (ds3.orderStart[j] != ds3.orderEnd[j]) {
                cui.jTextArea2.append("" + ds3.orderStart[j]);
                cui.jTextArea2.append(" " + ds3.orderEnd[j] + "\n");
                //System.out.println("");
                cui.jTextArea2.setCaretPosition(cui.jTextArea2.getDocument().getLength());
            }
        }

        cui.jTextArea4.append("Optimerad orderlista:\n");
        for (int j = 0; j < ds6.orders; j++) {
            if (ds6.orderStart[j] != ds6.orderEnd[j]) {
                cui.jTextArea4.append("" + ds6.orderStart[j]);
                cui.jTextArea4.append(" " + ds6.orderEnd[j] + "\n");
                //System.out.println("");
                cui.jTextArea4.setCaretPosition(cui.jTextArea4.getDocument().getLength());
            }
        }

        System.out.println("\n\n");

        //Loopa igenom orderlistan för att få fram GPS-koordinater till roboten
        String GPS;
        Arrays.fill(ds.arcColor, 0);
        cui.repaint();
        cui.jTextArea1.append("Optimering klar, tryck \"Start\" för att starta robotarna");
        for (int i = 0; i < Math.max(ds3.orders, ds6.orders) + 1; i++) {
            //stoppa roboten när den precis har lämnat en låda men bibehåll blåtands uppkopplingen
            while (!ds.start) {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.print(e.toString());
                }
            }
            //Förflyttning mellan ordrar
            GPS = "";
            if (i == 0) {
                //Om första, starta i A
                start1 = ds.shelfNode[0];
                stop1 = (int) ds.shelfNode[ds3.orderStart[i]];
                start2 = ds.shelfNode[0];
                stop2 = (int) ds.shelfNode[ds6.orderStart[i]];
                cui.jTextArea3.setText("0 -> " + ds3.orderStart[i] + "\n");
                cui.jTextArea5.setText("0 -> " + ds6.orderStart[i] + "\n");
            } else {
                //Annars starta där föregående flytt slutade
                if (i < ds3.orders) {
                    start1 = (int) ds.shelfNode[ds3.orderEnd[i - 1]];
                    stop1 = (int) ds.shelfNode[ds3.orderStart[i]];
                    cui.jTextArea3.setText(ds3.orderEnd[i - 1] + " -> " + ds3.orderStart[i] + "\n");
                } else {
                    start1 = stop1;
                    cui.jTextArea3.setText("Färdigt");
                }
                if (i < ds6.orders) {
                    start2 = (int) ds.shelfNode[ds6.orderEnd[i - 1]];
                    stop2 = (int) ds.shelfNode[ds6.orderStart[i]];
                    cui.jTextArea5.setText(ds6.orderEnd[i - 1] + " -> " + ds6.orderStart[i] + "\n");
                } else {
                    start2 = stop2;
                    cui.jTextArea5.setText("Färdigt");
                }
            }
            //Nollställer kartan
            Arrays.fill(ds.arcColor, 0);
            //Ser till att roboten står på rätt ställe på kartan som den gör i verkligheten så programlogiken pausas
            ds.robot1X = ds.nodeX[start1 - 1];
            ds.robot1Y = ds.nodeY[start1 - 1];
            ds.robot2X = ds.nodeX[start2 - 1];
            ds.robot2Y = ds.nodeY[start2 - 1];
            //Uppdaterar kartan
            cui.repaint();
            if (start2 != stop2) {
                path2 = op.createPlan(start2, stop2, 2, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start2 == stop2 && start2 == 24) {
                GPS += "J";
            }
            boolean clear = false;
            /*for (int j = 0; j < path1.size(); j++) {
                if (Integer.parseInt(path2.get(i).getId()) == stop1) {
                    clear = true;
                    break;
                }
            }
            if (clear) {
                Arrays.fill(ds.notoknumber, 500);
                Arrays.fill(ds.arcColor, 0);
            }*/

            if (start1 != stop1) {
                path1 = op.createPlan(start1, stop1, 1, true);
                //GPS = this.GPSkoordinater(path2, i, i);
            } else if (start1 == stop1 && start1 == 24) {
                GPS += "J";
            }

            if (start2 != stop2 && clear) {
                path2 = op.createPlan(start2, stop2, 2, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start2 == stop2 && start2 == 24 && clear) {
                GPS += "J";
            }

            if (start1 != stop1 && clear) {
                path1 = op.createPlan(start1, stop1, 1, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start1 == stop1 && start1 == 24 && clear) {
                GPS += "J";
            }
            
            if (path1 != null && i < ds3.orders) {
                for (int r = 0; r < path1.size(); r++) {
                    if (r < path1.size() - 1) {
                        trip1 = trip1 + (Math.max(Math.abs(ds.nodeY[Integer.parseInt(path1.get(r).getId()) - 1] - ds.nodeY[Integer.parseInt(path1.get(r + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path1.get(r).getId()) - 1] - ds.nodeX[Integer.parseInt(path1.get(r + 1).getId()) - 1])));
                    }
                }
            }
            if (path2 != null && i < ds6.orders) {
                for (int r = 0; r < path2.size(); r++) {
                    if (r < path2.size() - 1) {
                        trip2 = trip2 + (Math.max(Math.abs(ds.nodeY[Integer.parseInt(path2.get(r).getId()) - 1] - ds.nodeY[Integer.parseInt(path2.get(r + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path2.get(r).getId()) - 1] - ds.nodeX[Integer.parseInt(path2.get(r + 1).getId()) - 1])));
                    }
                }
            }
            
            trip = trip +  Math.max(trip1,trip2);
            trip1 = 0;
            trip2 = 0; 
            //Skriver ut vad som ska skickas till roboten
            //cui.jTextArea1.append("\nGPS utan låda:\n" + GPS + "\n\n");
            //System.out.println("GPS.längd " + GPS.length() + "\n");
            cui.jTextArea1.setCaretPosition(cui.jTextArea1.getDocument().getLength());

            //Måla om kartan så roboten står på rätt ställe och planerad färdväg är målas röd
            ds.robot1X = ds.nodeX[start1 - 1];
            ds.robot1Y = ds.nodeY[start1 - 1];
            ds.robot2X = ds.nodeX[start2 - 1];
            ds.robot2Y = ds.nodeY[start2 - 1];
            cui.repaint();

            //Simuleringsskit
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println("Fel");
            }
            //}
            //Nollställ kartan
            Arrays.fill(ds.arcColor, 0);
            Arrays.fill(ds.notoknumber, 500);

            //Räkna ut förflyttning av LÅDA!
            GPS = "";
            if (i < ds3.orders) {
                start1 = (int) ds.shelfNode[ds3.orderStart[i]];
                stop1 = (int) ds.shelfNode[ds3.orderEnd[i]];
                cui.jTextArea3.setText(ds3.orderStart[i] + " -> " + ds3.orderEnd[i] + "\n");
            } else {
                start1 = stop1;
                cui.jTextArea3.setText("Färdigt");
            }
            if (i < ds6.orders) {
                start2 = (int) ds.shelfNode[ds6.orderStart[i]];
                stop2 = (int) ds.shelfNode[ds6.orderEnd[i]];
                cui.jTextArea5.setText(ds6.orderStart[i] + " -> " + ds6.orderEnd[i] + "\n");
            } else {
                start2 = stop2;
                cui.jTextArea5.setText("Färdigt");
            }

            //Samma som för förflyttning utan låda
            if (start2 != stop2) {
                path2 = op.createPlan(start2, stop2, 2, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start2 == stop2 && start2 == 24) {
                GPS += "J";
            }
            clear = false;
            /*for (int j = 0; j < path1.size(); j++) {
                if (Integer.parseInt(path2.get(i).getId()) == stop1) {
                    clear = true;
                    break;
                }
            }
            if (clear) {
                Arrays.fill(ds.notoknumber, 500);
                Arrays.fill(ds.arcColor, 0);
            }*/

            if (start1 != stop1) {
                path1 = op.createPlan(start1, stop1, 1, true);
                //GPS = this.GPSkoordinater(path2, i, i);
            } else if (start1 == stop1 && start1 == 24) {
                GPS += "J";
            }

            if (start2 != stop2 && clear) {
                path2 = op.createPlan(start2, stop2, 2, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start2 == stop2 && start2 == 24 && clear) {
                GPS += "J";
            }

            if (start1 != stop1 && clear) {
                path1 = op.createPlan(start1, stop1, 1, true);
                //GPS = this.GPSkoordinater(path1, i, i);
            } else if (start1 == stop1 && start1 == 24 && clear) {
                GPS += "J";
            }
            
            if (path1 != null && i < ds3.orders) {
                for (int r = 0; r < path1.size(); r++) {
                    if (r < path1.size() - 1) {
                        trip1 = trip1 + (Math.max(Math.abs(ds.nodeY[Integer.parseInt(path1.get(r).getId()) - 1] - ds.nodeY[Integer.parseInt(path1.get(r + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path1.get(r).getId()) - 1] - ds.nodeX[Integer.parseInt(path1.get(r + 1).getId()) - 1])));
                    }
                }
            }
            if (path2 != null && i < ds6.orders) {
                for (int r = 0; r < path2.size(); r++) {
                    if (r < path2.size() - 1) {
                        trip2 = trip2 + (Math.max(Math.abs(ds.nodeY[Integer.parseInt(path2.get(r).getId()) - 1] - ds.nodeY[Integer.parseInt(path2.get(r + 1).getId()) - 1]), Math.abs(ds.nodeX[Integer.parseInt(path2.get(r).getId()) - 1] - ds.nodeX[Integer.parseInt(path2.get(r + 1).getId()) - 1])));
                    }
                }
            }
            
            trip = trip +  Math.max(trip1,trip2);
            trip1 = 0;
            trip2 = 0; 

            //Samma som för förflyttning utan låda
            //cui.jTextArea1.append("\nGPS med låda:\n" + GPS + "\n\n");
            //System.out.println("GPS.längd " + GPS.length() + "\n");
            cui.jTextArea1.setCaretPosition(cui.jTextArea1.getDocument().getLength());

            //Samma som för förflyttning utan låda
            ds.robot1X = ds.nodeX[start1 - 1];
            ds.robot1Y = ds.nodeY[start1 - 1];
            ds.robot2X = ds.nodeX[start2 - 1];
            ds.robot2Y = ds.nodeY[start2 - 1];
            cui.repaint();
            //Simuleringstrams
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println("Fel");
            }
            //}
            //Nollställer kartan
            Arrays.fill(ds.arcColor, 0);
            Arrays.fill(ds.notoknumber, 500);
            //Ser till att roboten står på rätt ställe på kartan som den gör i verkligheten så programlogiken pausas
            ds.robot1X = ds.nodeX[stop1 - 1];
            ds.robot1Y = ds.nodeY[stop1 - 1];
            ds.robot2X = ds.nodeX[stop2 - 1];
            ds.robot2Y = ds.nodeY[stop2 - 1];
            //Uppdaterar kartan
            cui.repaint();
        }
        System.out.println("TRIP:" + trip);
    }

    /*
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        OverordnatSystem x = new OverordnatSystem();
    }
}
