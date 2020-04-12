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
		
		deleteUser(statement);
		
		
		input.close();
		conn.close();
	}
	/*
	 * TODO: Check user input is correct
	 * Creates a user and adds it to the the database
	 */
	public static void createUser(Statement statement) throws SQLException, ParseException
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
				
		}
		catch (SQLException e)
		{
	                
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
		
		
		Scanner input = new Scanner(System.in); 
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
		
		try
		{
			//Runs SQL Insert statement
			statement.executeUpdate( insertSQL );
		}
		catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
		
		
		System.out.println("Successful");
		
		input.close();
	}
	
	public static void deleteUser(Statement statement)
	{
		String tableName = "user";
		int uid = 0;
		boolean check = false;
		
		Scanner input = new Scanner(System.in); 
		
		
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
    }
	
	public static void inputMenu(Statement statement) throws SQLException, ParseException
	{
		Scanner input = new Scanner(System.in);
		display_menu();
		
		switch (input.nextInt())
		{
		case 1:
			System.out.println("Creating user account");
			createUser(statement);
			break;
		case 2:
			System.out.println("Deleting user account");
		}
	}
}
