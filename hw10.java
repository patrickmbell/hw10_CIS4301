import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

/*
Price of a pizza is the sum of prices of the flavor and the crust.
Contains entries for individual pizzas of an order.
If an order contains multiple pizzas of same type, each will have different entry in pizza table with 
a different Pizza_ID(other attributes will remain same.)
*/

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

class Pizza {
    String pizzaId, flavorName, sauceName, crustStyle;
    ArrayList<String> toppings = new ArrayList<String>();

    float price;

    public Pizza(String pizzaId, String flavorName, String sauceName, String crustStyle)
    {
        this.pizzaId = pizzaId;
        this.flavorName = flavorName;
        this.sauceName = sauceName;
        this.crustStyle = crustStyle;
    }
    public Pizza(){

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
            System.out.println("Connected to pizzaparty!");
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
            System.out.println("Connected to pizzaparty!");
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
            System.out.println("**DEBUG** THE COUNT FOR THE RESTAURANTS IS: "  + count); 
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
            System.out.println("\n***** Main Menu *****");
            System.out.println("[1] Execute a complete SQL command\n[2] Build an Order\n[3] Logout / End Program");
            System.out.print("Enter Choice: ");
            selection = in.nextLine();

            if(selection.equals("3") || selection.equals("q")) {
                System.exit(0); //0 indicates successful termination, 1 or -1 is unsuccessful termination. 
                in.close();
            }
            else if(selection.equals("2")){
                buildOrder();
            }

            else if(selection.equals("1")){
                System.out.print("Query: ");
                selection = in.nextLine();

                executeQuery(selection); 
            }
            else
                System.out.println("Invalid Input!");
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
        Boolean isDelivery;
        Boolean orderComplete = false; 
        Boolean foundCustomer = false;  
        String choice; 
        ArrayList<Pizza> pizzas = new ArrayList<Pizza>();
        ArrayList<String> pizzaNames = new ArrayList<String>();
        try{
        
        while(!foundCustomer){
            System.out.print("Enter Customer Name: ");
            name = in.nextLine();
            char c1 = name.toUpperCase().charAt(0);
            
            rs = statement.executeQuery("SELECT DISTINCT * FROM customer WHERE Customer_Name LIKE \""+'%'+name+'%'+'\"');
            
            if(!rs.next()) //empty result set
            {
                System.out.println("Customer does not exist!");
            }
            else
            {
                System.out.print("\n\t* Address: " + rs.getString("Address"));
                System.out.print("\n\t* Phone: " + rs.getString("Phone_Number"));
                System.out.print("\nIs this you? (Yes/No): ");
                choice = in.next();

                if(choice.equals("Yes") || choice.equals("y") || choice.equals("yes")){
                    foundCustomer = true;
                    continue; 
                }
                else
                    continue; 
            }
        }
        System.out.println("\n***** Restaurant Menu *****");

        for(int i = 0; i < restaurants.length; i++){
            System.out.println("[" + (i+1) + "]" + " " + restaurants[i]);
        }
        
        System.out.print("\nEnter Choice: ");
        index = in.nextInt();
        System.out.print("\nIs this order a delivery? (Yes/No): ");

        choice = in.next();
        
        if(choice.equals("Yes") || choice.equals("yes") || choice.equals("y"))
            isDelivery = true; 
        else
            isDelivery = false; 

        restaurant = restaurants[index-1];  //chooses the restaurant from the array of restaurant names.  

        while(!orderComplete){
            System.out.println("\n***** " + restaurant + "Options *****");
            System.out.println("[1]  Add Pizza \n[2]  Finalize Order");
            System.out.print("\nEnter choice: ");
            choice = in.next();

            System.out.println("***** " + restaurant + "Menu *****");
            
            rs = statement.executeQuery("select * from flavor WHERE Restaurant_Name ='" + restaurant +"'");
            int count = 1;
            
            while(rs.next()){
                Pizza p = new Pizza();
                p.flavorName = rs.getString("Flavor_Name");
                p.price = rs.getFloat("Price");
                System.out.println("[" + count + "] " + p.price + " " + p.flavorName);

                ResultSet temp;
                System.out.println("**DEBUG*** " + p.flavorName);
                temp = statement.executeQuery("SELECT Topping_Name FROM flavor_toppings WHERE Restaurant_Name='" 
                + restaurant + "'" +  " AND Flavor_Name='" + p.flavorName + "'");
                
                System.out.println();
                while(temp.next()){
                    String query = temp.getString("Topping_Name");
                    System.out.print(query + " DEBUG");
                    p.toppings.add(query);
                    
                    if(!temp.last())
                    {
                        System.out.print(", ");
                    }

                }
                System.out.println();
            }

            
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
        }
    }
}
