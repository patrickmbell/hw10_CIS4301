import java.sql.*;
import java.util.Scanner;

class PizzaPartyDriver {
    public static void main( String args[] ) {
        // String passwd; 
        // String username = "root";
        // String db = "PizzaParty";
        // //Scanner in = new Scanner(System.in);
    
        //System.out.println("Password: "); 
       // passwd = in.nextLine(); 
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
    String LOCAL_HOST = "jdbc:mariadb://localhost:3306/";
    
    public OurPizzaParty() {
        database = "pizzaparty";
        username = "root";
        password = "root";
    
        try{
            connection = DriverManager.getConnection(LOCAL_HOST + this.database, this.username, this.password);
            statement = connection.createStatement();
            System.out.println("Connected");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public OurPizzaParty( String database, String username, String password ) {
        this.database = database;
        this.username = username;
        this.password = password;
    
        try{
            connection = DriverManager.getConnection(LOCAL_HOST + this.database, this.username, this.password);
            statement = connection.createStatement();
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
        Scanner in = new Scanner(System.in);
        while(true){
            System.out.println("(1) Execute a complete SQL command\n(2) Build an Order\n(3) Logout / End Program");
            selection = in.nextLine();

            if(selection.equals("3") || selection.equals("q"))
                System.exit(0); //0 indicates successful termination, 1 or -1 is unsuccessful termination. 

            else if(selection.equals("1")){
                System.out.println("Query: ");
                selection = in.nextLine();

                executeQuery(selection); 
            
             
            }
        } 

    // implement your menu
    }

    public void executeQuery(String query){
        try{
            //statement.createStatement();
            rs = statement.executeQuery(query);
            int numColumns = rs.getMetaData().getColumnCount();

            while(rs.next()){
                // String record = rs.getString("Flavor_Name");
                // System.out.println(record);    
                
                for(int i = 1; i < numColumns; i++)
                {
                    String column = rs.getString(i);
                    System.out.print(column + ", ");
                }
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        System.out.println();
        return; 
    }

    // implement other methods as needed
}
    // implement other classes as needed