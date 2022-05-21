import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class main {

    // Configuration Databases and Table
    public static String Table_Name="TABLE_NAME";
    public static String Database_Name="DATABASE_NAME";

    public static void main(String[] args) throws SQLException {


  Startfunction();
}

    // this function is start Menu mode
    static void Startfunction() throws SQLException {
        Scanner sc = new Scanner(System.in);
        String setDate=null;
        if(! isTableExist()) {System.out.println("ERROR");}

        System.out.println("Set the Date , format dd/MM/yyyy");
        setDate=sc.nextLine();
        boolean loop=true;
        while(loop)
        {
         System.out.println("##########___MENU___###########");
         System.out.println("1.Change the Date");
         System.out.println("2.Check Highest Volume in a DAY ");
         System.out.println("3.Check Longest hour in a DAY ");
         System.out.println("4.Check Highest Volume in a WEEK ");
         System.out.println("5.Check Longesh hour in a WEEK ");
         System.out.println("6.EXIT");
         System.out.println(" Date is "+setDate);
         System.out.println("Select appropriate options ");
         int i=0;
         i=sc.nextInt();

         switch (i)
         {
             case 1: {
                 System.out.println("Enter New Date");
                 setDate=sc.next();
                 System.out.println("date successfully changed "+setDate);
                 break;
             }
             case 2: {
                 HighestVolumeinDay(setDate);
             break;}
             case 3:{
                 LongestCallinDay(setDate);
             break; }
             case 4: {
                 HighestHourlyinWeek(setDate);
             break; }
             case 5:{
                 HighestVolumeinWeek(setDate);
             break; }
             case 6: {
                 loop = false;
                 break;
             }
             default: {
                 System.out.println("Invalid Input please try again");
                 break;
             }
         }

        }



    }


    static String NextDate(String D)
    {
     final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
     DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
     Date date;  String output = null;
     try {
         date = dateFormat.parse(D);
        date = new Date(date.getTime() + MILLIS_IN_A_DAY);
        output= dateFormat.format(date);
     }
     catch (ParseException e) {
         e.printStackTrace();
     }


     return output ;
 }
    static void HighestVolumeinWeek(String date) throws SQLException {
       int highest_so_far=0;
       String capDate = null;

       for (int i = 0; i < 7; i++) {

           int cal = LongestDay(date);
           //  System.out.println(date+" highest "+cal);


           if(highest_so_far< cal)
           {
               highest_so_far=cal;
               capDate=date;
           }

           date=NextDate(date);
       }

       System.out.println("Day of the Week "+capDate+" when the call Volume is highest.");


   }
    static int LongestDay(String date) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Database_Name, "root", "root");

        int Duration = 0;
        PreparedStatement p = null;
        ResultSet rs = null;
        if (connection != null) {

            //SELECT * FROM GFG_Call  where Start_time between "13/01/2021 00:00:01" and "13/01/2021 10:00:00";

            StringBuilder sb = new StringBuilder();
            sb.append("Select Count(Duration) as LongestCount from "+Table_Name+" where Start_time BETWEEN");
            sb.append('"');
            sb.append(date + " " + "00:00:01");
            sb.append('"');
            sb.append(" and ");
            sb.append('"');
            sb.append(date + " " + "24:00:00" + '"');


            //System.out.println(sb);

            p = connection.prepareStatement(String.valueOf(sb));
            rs = p.executeQuery();

            rs.next();
            Duration = rs.getInt(1);
            // System.out.println("Sum " + rs.getInt(1));

            rs.close();
            p.close();
            connection.close();

        }
        return Duration;
    }
    static void HighestHourlyinWeek(String date) throws SQLException{

        int highest_so_far=0;
        String capDate = null;

    for (int i = 0; i < 7; i++) {

        int cal = DayDuration(date);
        if(highest_so_far < cal)
        {
            highest_so_far=cal;
            capDate=date;
        }

        date=NextDate(date);
    }
        
    System.out.println("Day of the Week "+capDate+" when the call are longest.");

    }
    static int DayDuration(String date) throws SQLException {
     Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Database_Name, "root", "root");

     int SUM = 0;
     PreparedStatement p = null;
     ResultSet rs = null;
     if (connection != null) {
         // select COUNT(Start_time)  from GFG_Call where Start_time >= "13/01/2021 11:00:00" and End_time < "13/01/2021 10:00:00" ;

         StringBuilder sb = new StringBuilder();
         sb.append("Select SUM(Duration) as LongestCall from "+Table_Name+" where Start_time >=");
         sb.append('"');
         sb.append(date + " " + "00:00:00");
         sb.append('"');
         sb.append(" and End_time <");
         sb.append('"');
         sb.append(date + " " + "24:00:00" + '"');


         //System.out.println(sb);

         p = connection.prepareStatement(String.valueOf(sb));
         rs = p.executeQuery();

         rs.next();
         SUM = rs.getInt(1);

         rs.close();
         p.close();
         connection.close();

     }
     return SUM;
 }





    static void HighestVolumeinDay(String date) throws SQLException {
    int max=0;int time=0, res=0;
    for (int i = 1; i < 24; i++) {
        if(i<10){

            res=CountHourlyVolumes(date,"0"+i+":00:01","0"+(i+1)+":00:00");
            if(res>max) {
                max=res; time=i;
            }
        }else{
            res=CountHourlyVolumes(date,i+":00:01",(i+1)+":00:00");
            if(res>max){
                max=res;
                time=i;
            }
        }
    }

    System.out.println(date+" Highest volume hour is "+time+" to "+(time+1));

}

    static int CountHourlyVolumes(String date, String Start_time, String End_time) throws SQLException
    {

     Connection  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Database_Name, "root", "root");

     int count=0;
         PreparedStatement p = null;
         ResultSet rs = null;
         if(connection!=null)
         {
             // select COUNT(Start_time)  from GFG_Call where Start_time >= "13/01/2021 11:00:00" and End_time < "13/01/2021 10:00:00" ;

             StringBuilder sb = new StringBuilder();
             sb.append("Select COUNT(*) as recordCount from "+Table_Name+" where Start_time >=");
             sb.append('"'); sb.append(date+" "+Start_time); sb.append('"');
             sb.append(" and End_time <");sb.append('"');sb.append(date+" "+End_time+'"');


             //System.out.println(sb);

             p = connection.prepareStatement(String.valueOf(sb));
             rs = p.executeQuery();

             rs.next();
             count=rs.getInt(1);

             rs.close();
             p.close();
             connection.close();

     }
return count;
     }





    static void LongestCallinDay(String date) throws SQLException {
     int max=-9999;int time=0, res=0;
     for (int i = 1; i < 24; i++) {
         if(i<10){

             res=HourlyDurations(date,"0"+i+":00:00","0"+(i+1)+":00:00");
             if(res>max) {
                 max=res; time=i;
             }
         }else{
             res=HourlyDurations(date,i+":00:00",(i+1)+":00:00");
             if(res>max){
                 max=res;
                 time=i;
             }
         }
     }

     System.out.println(date+" Longest call hour between  "+time+" to "+(time+1));

 }

    static int HourlyDurations(String Date, String Start_time, String End_time) throws SQLException {
     Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Database_Name, "root", "root");

     int SUM = 0;
     PreparedStatement p = null;
     ResultSet rs = null;
     if (connection != null) {
         // select COUNT(Start_time)  from GFG_Call where Start_time >= "13/01/2021 11:00:00" and End_time < "13/01/2021 10:00:00" ;

         StringBuilder sb = new StringBuilder();
         sb.append("Select SUM(Duration) as LongestCall from "+Table_Name+" where Start_time >=");
         sb.append('"');
         sb.append(Date + " " + Start_time);
         sb.append('"');
         sb.append(" and End_time <");
         sb.append('"');
         sb.append(Date + " " + End_time + '"');


         //System.out.println(sb);

         p = connection.prepareStatement(String.valueOf(sb));
         rs = p.executeQuery();

         rs.next();
         SUM = rs.getInt(1);
         rs.close();
         p.close();
         connection.close();

     }
     return SUM;
 }


    public static void PrintTheDataBase(String date)  // this is work find but not in
    {
        PreparedStatement p = null;
        ResultSet rs = null;

        try
        {
            Connection  connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/"+Database_Name, "root", "root"
            );

            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM "+Table_Name+"  where Start_time between ");
            sb.append('"'+date +" 00:00:01"+'"');
            sb.append(" and ");
            sb.append('"'+date+ " 24:00:00"+'"');
                String sql =sb.toString();

               // System.out.println(sql);


            p = connection.prepareStatement(sql);
            rs = p.executeQuery();

            // of the SQL command above
            System.out.println("id\t\tStart_time\t\t\t\tEnd_time\t\t\t\tDuration\t\t");

            while (rs.next()) {

                int id = rs.getInt("Id");
                String Start_time = rs.getString("Start_time");
                String End_time = rs.getString("Start_time");
                int Duration = rs.getInt("Duration");
                System.out.println(id + "\t\t" + Start_time +"\t\t"+ End_time+"\t\t\t"+ Duration);
            }


            connection.close();
        }
        catch(Exception e)
        {
            System.out.println(e+" Errrrorrr");
        }
    }

    public static boolean isTableExist() throws SQLException{
        String sTablename=Table_Name;
        Connection  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Database_Name, "root", "root");
        PreparedStatement p = null;
        if(connection!=null)
        {
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, sTablename.toUpperCase(),null);
            if(rs.next())
            {
                System.out.println("Table "+rs.getString("TABLE_NAME")+"  already exists !!");
                    return true;
            }
            else
            {
                /*
                    // STRUCTURE OF TABLE
                String sql ="create table "+Table_Name+" (id int , "+
                        "From_number int, "+
                        "Start_time CHAR(255) not null, "+
                        "End_time  CHAR(255) not null,"+
                        " Duration int,"+
                        " primary key(id,From_number) );";

                System.out.println(sql);
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
            */

                System.out.println(" Given Table is Not Exists "+sTablename);
                System.out.println(" plz configure properly");
                return false;
            }

        }
        return false;
    }





}

