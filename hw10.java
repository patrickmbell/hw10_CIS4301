import java.sql.*;
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

/*
Price of a pizza is the sum of prices of the flavor and the crust.
Contains entries for individual pizzas of an order.
If an order contains multiple pizzas of same type, each will have different entry in pizza table with 
a different Pizza_ID(other attributes will remain same.)
*/

class PizzaPartyDriver {
    public static void main( String args[] ) {

        OurPizzaParty opp = new OurPizzaParty();
        opp.menu();
    }
}

class Pizza {
    String flavorName, sauceName, crustStyle;
    ArrayList<String> toppings = new ArrayList<String>();
    int crustSize; 
    double price;

    public Pizza(String flavorName, String sauceName, String crustStyle, double price, int crustSize)
    {
        this.flavorName = flavorName;
        this.sauceName = sauceName;
        this.crustStyle = crustStyle;
        this.crustSize = crustSize; 
        this.price = price; 
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
                System.out.println();
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
                
                for(int i = 1; i <= numColumns; i++)
                {
                    String column = rs.getString(i);
                    if(i != numColumns )
                        System.out.print(column + ", ");
                    else
                        System.out.print(column);
                }
                System.out.println();
            }
        }
        catch(SQLException sqle)
        {
            System.out.println("Bad Query!");
        }

        return; 
    }

    public void buildOrder() {
        getRestaurants();
        Scanner in = new Scanner(System.in);
        String name = "";
        String driverName = "";
        String carNumber = "";
        String restaurant, pizza;
        int index;
        Boolean isDelivery;
        Boolean orderComplete = false;
        Boolean foundCustomer = false;
        int menu;

        String choice, crustChoice, sauceChoice, flavorChoice;
        int pizzaIndex, crustSize;
        double pizzaPrice = 0;

        ArrayList<Pizza> orderPizzas = new ArrayList<Pizza>();
        ArrayList<String> pizzaOptions = new ArrayList<String>();
        ArrayList<String> crusts = new ArrayList<String>();
        ArrayList<String> sauces = new ArrayList<String>();
        ArrayList<Integer> crustSizes = new ArrayList<Integer>();
        ArrayList<Integer> pizzaIDs = new ArrayList<Integer>();

        try {

            while (!foundCustomer) {
                System.out.print("Enter Customer Name: ");
                
                name = in.next();

                rs = statement.executeQuery("SELECT DISTINCT * FROM customer WHERE Customer_Name LIKE \"" + '%' + name + '%' + '\"');

                if (!rs.next()) //empty result set
                {
                    System.out.println("Customer does not exist!");
                } else {
                    System.out.print("\n\t* Address: " + rs.getString("Address"));
                    System.out.print("\n\t* Phone: " + rs.getString("Phone_Number"));
                    System.out.print("\nIs this you? (Yes/No): ");
                    choice = in.next();

                    if (choice.equals("Yes") || choice.equals("y") || choice.equals("yes")) {
                        foundCustomer = true;
                        continue;
                    } 
                    else if(choice.equals("No") || choice.equals("no") || choice.equals("n"))
                    {
                        continue;
                    }
                    else 
                        continue;
                        
                }
            }
            System.out.println("\n***** Restaurant Menu *****");

            for (int i = 0; i < restaurants.length; i++) {
                System.out.println("[" + (i + 1) + "]" + " " + restaurants[i]);
            }

            System.out.print("\nEnter Choice: ");
            index = in.nextInt();
            System.out.print("\nIs this order a delivery? (Yes/No): ");

            choice = in.next();

            if (choice.equals("Yes") || choice.equals("yes") || choice.equals("y"))
                isDelivery = true;
            else
                isDelivery = false;

            restaurant = restaurants[index - 1];  //chooses the restaurant from the array of restaurant names.

            while (!orderComplete) {
                System.out.println("\n***** " + restaurant + " Options *****");
                System.out.println("[1]  Add Pizza \n[2]  Finalize Order");
                System.out.print("\nEnter choice: ");
                menu = in.nextInt();

                //Time to finalize this order baby. 
                if(menu == 2)
                {   
                    orderComplete = true;
                    rs = statement.executeQuery("SELECT MAX(Order_ID) as max FROM orders");
                    
                    int maxOrderID = 0;
                    int maxPizzaID = 0;

                    if(rs.next())
                        maxOrderID = rs.getInt("max");    
                    
                    rs = statement.executeQuery("SELECT MAX(Pizza_ID) as max from pizza");

                    if(rs.next())
                        maxPizzaID = rs.getInt("max");


                    System.out.println("***** Order #" + maxOrderID + " *****");
                    System.out.println("***** Customer *****");
                    rs = statement.executeQuery("SELECT DISTINCT * FROM customer WHERE Customer_Name LIKE \"" + '%' + name + '%' + '\"');
                    
                    if(rs.next()){
                    System.out.println("\t* " + rs.getString("Customer_Name"));
                    System.out.println("\t* " + rs.getString("Address"));
                    System.out.println("\t* " + rs.getString("Phone_Number"));
                    System.out.println("***** Restaurant *****");
                    }

                    rs = statement.executeQuery("SELECT * from restaurant WHERE Restaurant_Name = '" + restaurant + "'");
                    
                    if(rs.next()){
                    System.out.println("\t* " + rs.getString("Restaurant_Name"));
                    System.out.println("\t* " + rs.getString("Address"));
                    System.out.println("\t* " + rs.getString("Phone_Number"));
                    }

                    //A successful order will add new entries in Pizza, Order, and Order_Pizza tables.
                    for(int i = 0; i < orderPizzas.size(); i++){ 
                        statement.executeUpdate("INSERT INTO pizza VALUES (" + (maxPizzaID+1) + ", '" + orderPizzas.get(i).flavorName + "', '" + orderPizzas.get(i).crustStyle + "', '" + orderPizzas.get(i).crustSize + "', '" + restaurant + "', '" + orderPizzas.get(i).sauceName + "')");
                        pizzaIDs.add(maxPizzaID+1);
                        maxPizzaID++; 
                    }

                    if(isDelivery)
                    {
                        int numDrivers = 0;
                        int count = 1; 
                        rs = statement.executeQuery("SELECT COUNT(*) as numDrivers from driver where Restaurant_Name ='" + restaurant + "'");

                        if(rs.next()) 
                            numDrivers = rs.getInt("numDrivers");

                        int randDriver = (int)(Math.random() * (numDrivers-1)) + 1;     
                        
                        rs = statement.executeQuery("SELECT Driver_Name, Car_Number as numDrivers from driver where Restaurant_Name ='" + restaurant + "'");

                        while(rs.next()){
                            if(count == randDriver){
                                driverName = rs.getString("Driver_Name");
                                carNumber = rs.getString("Car_Number");
                            }
                        }
                        
                        statement.executeUpdate("INSERT INTO orders VALUES (" + (maxOrderID + 1) + ", '" + name + "', '" + restaurant + "', 'YES', '" + carNumber + "')"); 
                                                
                    }

                    else{
                    statement.executeUpdate("INSERT INTO orders VALUES (" + (maxOrderID + 1) + ", '" + name + "', '" + restaurant + "', 'NO', " + "NULL" + ")");  
                    }

                    //Now to link the orderID with the pizzaIDs in the order_pizza table. 
                    for(int i = 0; i < pizzaIDs.size(); i++)
                    {
                        statement.executeUpdate("INSERT INTO order_pizza VALUES (" + (maxOrderID+1) + ", " + pizzaIDs.get(i) + ")"); 
                    }

                    if(!isDelivery){
                        System.out.println("Scheduled for Pickup");
                    }
                    else{
                        System.out.println("Delivered By: " + driverName);

                    }
                    System.out.println("Pizzas\t\tPizza Description");
                    int count = 1;
                    
                    double orderTotal = 0; 
                    for(int i = 0; i < orderPizzas.size(); i++){

                        System.out.println("\t#" + count + "\t" + "$" + orderPizzas.get(i).price + "\t\t" + orderPizzas.get(i).crustSize + '"' + " " 
                        + orderPizzas.get(i).crustStyle + " " + orderPizzas.get(i).flavorName + " with " + orderPizzas.get(i).sauceName);
                        
                        //if(rs.next())
                        orderTotal += orderPizzas.get(i).price;
                        count++;
                    }

                    System.out.println("Order Total: \t$" + orderTotal); 
                    return; 
                }                        
                System.out.println("***** " + restaurant + " Menu *****");


                rs = statement.executeQuery("select * from flavor WHERE Restaurant_Name ='" + restaurant + "' ORDER BY Restaurant_Name");
                int count = 1;

                while (rs.next()) {
                    Pizza p = new Pizza();
                    p.flavorName = rs.getString("Flavor_Name");
                    p.price = rs.getDouble("Price");
                    System.out.println("[" + count + "] " + p.price + " " + p.flavorName);

                    statement = connection.createStatement();
                    setToppings(restaurant, p.flavorName, p);

                    for(int i = 0; i < p.toppings.size(); i++){
                        System.out.print(p.toppings.get(i));
                        if(i != p.toppings.size()-1)
                            System.out.print(", ");
                        if(i == p.toppings.size()-1)
                            System.out.println();
                    }

                    pizzaOptions.add(p.flavorName);
                    count++;
                    System.out.println();
                }

                System.out.print("Enter Choice: ");
                pizzaIndex = in.nextInt();  //This needs to be index - 1

                flavorChoice = pizzaOptions.get(pizzaIndex-1);

                System.out.println("\n***** " + restaurant + " Crusts Options *****");

                rs = statement.executeQuery("SELECT distinct Crust_Style, Price, Size FROM crusts WHERE Restaurant_Name='"+restaurant+"'");
                count = 1;
                while(rs.next()){
                    crusts.add(rs.getString("Crust_Style"));
                    crustSizes.add(rs.getInt("Size"));
                    System.out.println("\t[" + count + "] " + "+$" + rs.getInt("Price") + " " + rs.getString("Crust_Style") + "(" + rs.getInt("Size") + "\")");
                    count++; 
                }
                
                System.out.print("\nEnter Choice: ");
                index = in.nextInt();
                crustChoice = crusts.get(index-1); 
                crustSize = crustSizes.get(index-1);
                
                System.out.println("***** " + restaurant + " Sauce Options *****");
                rs = statement.executeQuery("SELECT Sauce_Name FROM sauce WHERE Restaurant_Name='" + restaurant + "'");
                count = 1;
                while(rs.next()){
                    sauces.add(rs.getString("Sauce_Name"));
                    System.out.println("\t[" + count + "] " + rs.getString("Sauce_Name"));
                    count++; 
                }

                System.out.print("\nEnter Choice: ");
                index = in.nextInt();
                sauceChoice = sauces.get(index-1);
                
                
                rs = statement.executeQuery("select SUM(flavor.Price + crusts.Price) as Total_Price from flavor join crusts on ( flavor.Restaurant_Name = crusts.Restaurant_Name AND flavor.Restaurant_Name ='" +  restaurant  + "' AND Flavor_Name = '" + flavorChoice + "'  AND Crust_Style = '" + crustChoice + "' AND Size=" + crustSize + ")"); 
                
                if(rs.next())
                    pizzaPrice = rs.getDouble("Total_Price");
                
                Pizza p = new Pizza(flavorChoice, sauceChoice, crustChoice, pizzaPrice, crustSize);

                orderPizzas.add(p);
            }
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

        public void setToppings(String restaurant, String flavorName, Pizza p){
            try
            {
            ResultSet rs = statement.executeQuery("SELECT Topping_Name FROM flavor_toppings WHERE Restaurant_Name='"
                    + restaurant + "'" +  " AND Flavor_Name='" + flavorName + "'");

            while(rs.next())
            {
                p.toppings.add(rs.getString("Topping_Name"));
            }

            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
    }
}
