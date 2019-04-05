import java.sql.*;
import java.util.Scanner;

class PizzaPartyDriver {
    public static void main( String args[] ) {
    OurPizzaParty opp = new OurPizzaParty();
    opp.menu();
    }
}

class OurPizzaParty {
    private String database;
    private String username;
    private String password;
    ResultSet rs; 
    Connection connection; 
    Statement statement; 
    String JDBC_DRIVER = "org.mysql.jdbc.Driver";
    String LOCAL_HOST = "jdbc:mariadb://localhost:3306/";
    public OurPizzaParty() {
    database = "PizzaParty";
    username = "root";
    password = "root";
    }
    
    public OurPizzaParty( String database, String username, String password ) {
    this.database = database;
    this.username = username;
    this.password = password;
    
    try{
        connection = DriverManager.getConnection(LOCAL_HOST + this.database, this.username, this.password);
    }
    catch(SQLException e)
    {
        e.printStackTrace();
    }
    }

    /*
        The method menu will be the entry point into the functionality you provide
    */

    public void menu() {
        String selection;
        Scanner input = new Scanner();
        while(true){
            System.out.println("(1) Execute a complete SQL command\n(2) Build an Order\n(3) Logout / End Program");
            selection = input.nextLine();

            if(selection.equals("1")){
                selection = input.nextLine();
                executeQuery(selection); 
            }
        } 

    // implement your menu
    }

    public void executeQuery(String query){
        ResultSet rs; 
        Connection connection; 
        Statement statement; 

    }

    // implement other methods as needed
}
    // implement other classes as needed