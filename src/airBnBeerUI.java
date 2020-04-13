import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class airBnBeerUI {
	
	public static void main(String args []) throws SQLException, ParseException
	{
		
		String tableName = "";
		int sqlCode = 0;
		String sqlState = "00000";
		
		if (args.length > 0)
		{
			tableName += args[0];
		}
		else
		{
			tableName += "testJava.tbl";
		}
		//Register DB2 JDBC driver
		try 
		{
			DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ;
		} catch (Exception cnfe)
		{
			System.out.println("Class not found");
		}
		
		//Connect to the database
		Connection conn = DriverManager.getConnection(
				"jdbc:db2://comp421.cs.mcgill.ca:50000/cs421",
				"cs421g22",
				"airbnbeer22"
				);
		Statement statement = conn.createStatement ( ) ;
		
		Scanner input = new Scanner(System.in);
		
		//createUser(statement);
		int uid =checkUID(statement, input);
		createProperty(statement, uid, input);
		//checkUID(statement);
		
		//input.close();
		conn.close();
	}
	/*
	 * TODO: Check user input is correct
	 * Creates a user and adds it to the the database
	 */
	public static void createUser(Statement statement, Scanner input) throws SQLException, ParseException
	{
		String tableName = "user";
		int uid = 0;
		//Gets the maximum uid
		String querySQL = "SELECT MAX(uid) FROM " + tableName;
		
		
		try
		{
			System.out.println(querySQL);
			java.sql.ResultSet rs = statement.executeQuery ( querySQL ) ;
			
			while(rs.next())
			{
				uid = rs.getInt ( 1 ) + 1;
				System.out.println(uid);
			}
			
			//Scanner input = new Scanner(System.in); 
			System.out.println("Fill in the following information in the same order and space one option from the other in a single sentence");
			System.out.println("");
			System.out.println("1) First Name");
			System.out.println("2) Second Name");
			System.out.println("3) Phone number");
			System.out.println("4) Email");
			System.out.println("5) City");
			System.out.println("6) Country");
			System.out.println("7) Date Of Birth(FORMAT-- MM/dd/yyyy");
			System.out.println("8) Gender");
			
			String userInfo = input.nextLine();
			
			// Tokenize the input string
			String [] userInput = userInfo.split(" ");
			
			Date inputDate = new SimpleDateFormat("MM/dd/yyyy").parse(userInput[6]);
			java.sql.Date sqldate = new java.sql.Date(inputDate.getTime());
			
			
			String insertSQL = "INSERT INTO " + tableName + " VALUES (" + uid + "," + "\'" + userInput[0] + "\'," 
					+ "\'" + userInput[1] + "\'," + "\'" + userInput[2] + "\'," + "\'" + userInput[3] + "\'," + "\'" + userInput[4] + "\'," 
					+ "\'" + userInput[5] + "\'," + "\'" + sqldate + "\'," + "\'" + userInput[7] + "\'" + ")";
			
			System.out.println(insertSQL);
			
			// Inserts the record
			statement.executeUpdate( insertSQL );
			
			System.out.println("Successful created account ");
			
		}
		catch (SQLException e)
		{
	                
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
				
	}
	
	public static void deleteUser(Statement statement, Scanner input)
	{
		String tableName = "user";
		int uid = 0;
		boolean check = false;
		
		//Scanner input = new Scanner(System.in); 
		
		
		do
		{
			System.out.println("Enter your user id to delete your account");
			
			//Gets the userID 
			if(input.hasNextInt())
			{
				uid = input.nextInt();
				
				try
				{
					String querySQL = "SELECT * FROM " + tableName + " WHERE uid = " + "\'"+ uid + "\'";
					System.out.println();
					
					String fname = "";
					String lname = "";
					java.sql.ResultSet rs = statement.executeQuery ( querySQL ) ;
					
					while(rs.next())
					{
						int id = rs.getInt(1);
						fname = rs.getString(2);
						lname = rs.getString(3);
						System.out.println("Deleting");
						System.out.println("User: " + fname + " " + lname + "UserID : " + id );
					}
					
					String deleteSQL = "DELETE FROM " + tableName + " WHERE uid = " + uid;
					System.out.println(deleteSQL);
					
					statement.executeUpdate ( deleteSQL ) ;
					
					System.out.println("Successfully deleted");
					check = true;
				}
				catch (SQLException e)
				{
					System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
					System.out.println("Failed connection");
					System.out.println("Input a valid userID");
				}
				
			}
			else
			{
				System.out.println("INVALID USER ID");
				System.out.println("Input a valid userID");
			}
		} while(check == false);
		
	}
	
	/*
	 * Creates an a new owner record
	 */
	public static void createOwner(Statement statement, int uid)
	{
		String tableName = "owner";
		int earnings = 0;
		
		String insertSQL = "INSERT INTO " + tableName + " VALUES (" + uid + ", " + earnings + ")";
		
		System.out.println(insertSQL);
		
		try
		{
			statement.executeUpdate( insertSQL );
		} 
		catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
	}
	
	public static void createProperty(Statement statement, int userID, Scanner scanner) throws SQLException
	{
		String tableName = "property";
		boolean check = false;
		int pid = 0;
		int uid = userID;
		
		//uid = checkUID(statement);
		
		do
		{
			
				try
				{
					
					String querySQL = "SELECT MAX(pid) FROM " + tableName;
					java.sql.ResultSet rs = statement.executeQuery ( querySQL ) ;
					
					while(rs.next())
					{
						pid = rs.getInt ( 1 ) + 1;
						System.out.println("PID: " + pid);
					}
					
					//Scanner scanner = new Scanner(System.in);
					System.out.println("Fill in the following information in the same order and space one option from the other in a single sentence");
					System.out.println("");
					System.out.println("1) Street number");
					System.out.println("2) City");
					System.out.println("3) Country");
					System.out.println("4) Monthly price");
					System.out.println("5) Postal code");
					System.out.println("6) Property type(Condo or Mansion or House or Apartment)");
					System.out.println("7) Features(Lighting, swimming pool)");
					
					String userInfo = scanner.nextLine();
					
					String [] userInput = userInfo.split(" ");
					
					
					String insertSQL = "INSERT INTO " + tableName + " (pid, uid, snumber, city, country, price, postcode, ptype, features)" + " VALUES (" 
							+ pid + ","+ uid + "," + "\'" + userInput[0] + "\'," 
							+ "\'" + userInput[1] + "\'," + "\'" + userInput[2] + "\'," + "\'" + userInput[3] + "\'," + "\'" + userInput[4] + "\'," 
							+ "\'" + userInput[5] + "\'," +  "\'" + userInput[6] + "\'" + ")";
					
					//Creates an owner due to the foreign key constraint
					createOwner(statement, uid);
					System.out.println(insertSQL);
					
					// Inserts the record
					statement.executeUpdate( insertSQL );
					
					check = true;
					scanner.close();
				}
				catch (SQLException e)
				{
			                
					System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
					System.out.println("INVALID INPUT");
					System.out.println("Input valid entries");
				}
		} while(check == false);
		
		
		
	}
	/*
	 * Checks user input uid and 
	 */
	public static int checkUID(Statement statement, Scanner input) throws SQLException
	{
		boolean check = false;
		//Scanner input = new Scanner(System.in);
		int uid = 0;
		
		do
		{
			System.out.println("Enter your user id");
			
			if(input.hasNextInt())
			{
				uid = input.nextInt();
				String queryUID = "SELECT uid FROM user WHERE uid = " + uid;
				
				System.out.println(queryUID);
				try
				{
					java.sql.ResultSet rsUID = statement.executeQuery ( queryUID ) ;
					
					while(rsUID.next())
					{
						uid = rsUID.getInt ( 1 );
					}
					if(uid != 0)
					{
						check = true;
						System.out.println(uid);
					}
					else 
						System.out.println("Invalid USERID");
				} 
				catch (SQLException e)
				{
					System.out.println("INVALID USERID");
					System.out.println("Input a valid user input");
				}
				
			}else
			{
				System.out.println("INVALID USER ID");
				System.out.println("Input a valid userID");
			}
		} while(check == false);
		
		input.nextLine();
		return uid;
		
	}
	
	public static void display_menu()
	{
		System.out.println("Enter the number next to your desired option");
		System.out.println("");
		System.out.println("");
		System.out.println("1) Create user account");
		System.out.println("2) Delete user account");
		System.out.println("3) Create listings");
		System.out.println("4) Find open listings");
		System.out.println("5) To Quit press 5");
	}
	
	public void question(Statement statement) throws SQLException, ParseException
    {
		System.out.println("Would you like to proceed or quit?");
		System.out.println("To proceed enter 9.");
		System.out.println("If you wish to quit enter 0.");
		Scanner q = new Scanner(System.in);
	       
		switch (q.nextInt()) 
		{
		    case 0:
		    System.out.println ("Thank you and godbye.");
		    break;
	  
		    case 9:
		    System.out.println ("Please proceed.");
		    inputMenu(statement);
		    break;
		    default:
		    System.err.println ( "Unrecognized option" );
		    break;
		}
		q.close();    
		
    }
	
	public static void inputMenu(Statement statement) throws SQLException, ParseException
	{
		Scanner input = new Scanner(System.in);
		display_menu();
		
		switch (input.nextInt())
		{
		case 1:
			System.out.println("Creating user account");
			//createUser(statement);
			break;
		case 2:
			System.out.println("Deleting user account");
		}
	}
}
