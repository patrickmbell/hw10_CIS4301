import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

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

class specialtyPizza {
    String pizzaId, flavorName, sauceName, crustStyle;

    public specialtyPizza(String pizzaId, String flavorName, String sauceName, String crustStyle)
    {
        this.pizzaId = pizzaId;
        this.flavorName = flavorName;
        this.sauceName = sauceName;
        this.crustStyle = crustStyle;
    }
}

class OurPizzaParty {
    private String database;
    private String username;
    private String password;
    private ResultSet rs; 
    private Connection connection; 
    private Statement statement;
    private String[] restaurants;  

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
            System.out.println("Connected");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    
    }

    public void getRestaurants(){
        try {
            String query = "SELECT Restaurant_Name FROM restaurant";    
            int count = 0;
            rs = statement.executeQuery(query);
            while(rs.next()){
                count++;
            }
            //System.out.println("numRows "+count);
            restaurants = new String[count];
            count = 0;
            rs = statement.executeQuery(query);
            while(rs.next()){
                restaurants[count] = rs.getString("Restaurant_Name");
                count++;
            }
            
        } 
        catch (SQLException sqle) {
            sqle.printStackTrace();
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

            else if(selection.equals("2")){
                buildOrder();
            }

            else if(selection.equals("1")){
                System.out.println("Query: ");
                selection = in.nextLine();

                executeQuery(selection); 
            }
        } 

    }

    public void executeQuery(String query){
        try{

            rs = statement.executeQuery(query);
            int numColumns = rs.getMetaData().getColumnCount();
            
            while(rs.next()){   
                
                for(int i = 1; i < numColumns; i++)
                {
                    String column = rs.getString(i);
                    if(i != numColumns - 1)
                        System.out.print(column + ", ");
                    else
                        System.out.print(column);
                }
                System.out.println();
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
        }
        System.out.println();
        return; 
    }
    public void buildOrder(){
        getRestaurants();
        Scanner in = new Scanner(System.in);
        String name, restaurant, pizza;
        int index; 
        ArrayList<specialtyPizza> pizzas = new ArrayList<specialtyPizza>();

        System.out.println("Your Name: ");
        name = in.nextLine();
        
        for(int i = 0; i < restaurants.length; i++){
            System.out.println((i+1) + ". " + restaurants[i]);
        }
        System.out.println("Select Restaurant: ");
        index = in.nextInt();
        restaurant = restaurants[index-1];
        try{
            rs = statement.executeQuery("select Flavor_Name, Crust_Style, Sauce_Name, pizza_id from pizza NATURAL JOIN restaurant WHERE Restaurant_Name ='" + restaurant +"'");
            int count = 1;
            System.out.println("\nFlavor Name --- Crust Style --- Sauce Name");
            while(rs.next()){
                specialtyPizza p = new specialtyPizza(rs.getString("pizza_id"), rs.getString("Flavor_Name"), rs.getString("Sauce_Name"), rs.getString("Crust_Style"));
                pizzas.add(p);
                System.out.println(count + ". " + p.flavorName + " --- " + p.crustStyle + " --- " + p.sauceName);
                count++;
            }
            System.out.println();
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
