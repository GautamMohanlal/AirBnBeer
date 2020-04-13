import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


public class airBnBeerUI {

	public static void main(String args []) throws SQLException, ParseException
	{


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

		Scanner scanner = new Scanner(System.in);
		
		exportCSV(statement);

		//Gets the userID logged on this session
		int uid =checkUID(statement, scanner);
		
		//Displays the main menu
		inputMenu(statement, scanner, uid);
		
		scanner.close();
		conn.close();
	}
	
	/*
	 * Exports results for data visualization
	 */
	public static void exportCSV(Statement statement)
	{
		
		
		// write header line containing column names       
		
		
		String exportSQL = "SELECT MONTH(sdate), COUNT(*), SUM(cost) FROM Events GROUP BY MONTH(sdate)";
		
		try
		{
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter("result.csv"));
			fileWriter.write("Month,Number of Events, Earnings");
			//Execute query and retrieve data
			java.sql.ResultSet rs = statement.executeQuery ( exportSQL ) ;
			
			while(rs.next())
			{
				String month = rs.getString(1);
				String num_events = rs.getString(2);
				String earnings = rs.getString(3);
				
				String line = String.format("\"%s\",%s,%s", month, num_events, earnings);
				
				fileWriter.newLine();
                fileWriter.write(line);
			}
			
			fileWriter.close();
		}
		catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
		catch (IOException e) {
	        System.out.println("File IO error:");
	        e.printStackTrace();
	    }
		
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
		
		input.nextLine();

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
		
		input.nextLine();

	}
	
	public static void updatePayment(Statement statement, int uid, Scanner scanner) throws ParseException
	{
		String tableName = "payment";
		
		System.out.println("Fill in the following information in the same order and space one option from the other in a single sentence");
		System.out.println("");
		System.out.println("1) First_Name");
		System.out.println("2) Last_Name");
		System.out.println("3) Cardnumber");
		System.out.println("4) CCV");
		System.out.println("5) Expiry_Date");
		System.out.println("6) Card_Type(i.e Visa, MasterCard, American Express)");
		
		String userInfo = scanner.nextLine();

		String [] userInput = userInfo.split(" ");
		
		String cardNum = userInput[2];
		String fName = userInput[0];
		String lName = userInput[1];
		String ccv = userInput[3];
		String cardType = userInput[5];
		
		Date expDate = new SimpleDateFormat("MM/dd/yyyy").parse(userInput[4]);
		java.sql.Date expSQLDate = new java.sql.Date(expDate.getTime());
		
		
		String insertSQL = "INSERT INTO " + tableName + " VALUES (" + cardNum + ", " + uid + "," + "\'" + fName + "\'" + ", " + "\'" + lName + "\'" + ", " 
				+ ccv + ", " + "\'" + expSQLDate + "\'," + "\'" + cardType + "\'" + ")";
		
		System.out.println( insertSQL );
		
		try
		{
			statement.executeUpdate( insertSQL );
			System.out.println("Payment succesfully updated");
			
		} 
		catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
		scanner.nextLine();
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
			System.out.println("Owner succesfully created");
		} 
		catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
	}
	
	/*
	 * Creates a property and updates it in the database	
	 */
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

				System.out.println("Succefully created and inserted property");
				check = true;
			}
			catch (SQLException e)
			{

				System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
				System.out.println("INVALID INPUT");
				System.out.println("Input valid entries");
			}
		} while(check == false);
		
		scanner.nextLine();
	}
	
	public static void checkAvailableProp(Statement statement, Scanner scanner) throws ParseException
	{
		
		System.out.println("Enter the following information in the same order and space each option input to view available listings");
		System.out.println("1) City");
		System.out.println("1) Start date(FORMAT-- MM/dd/yyyy)");
		System.out.println("2) End date(FORMAT-- MM/dd/yyyy)");
		
		String userInfo = scanner.nextLine();

		// Tokenize the input string
		String [] userInput = userInfo.split(" ");

		Date startDate = new SimpleDateFormat("MM/dd/yyyy").parse(userInput[1]);
		Date endDate = new SimpleDateFormat("MM/dd/yyyy").parse(userInput[2]);
		java.sql.Date startSQLDate = new java.sql.Date(startDate.getTime());
		java.sql.Date endSQLDate = new java.sql.Date(endDate.getTime());
		
		String city = userInput[0];
		
		String querySQL = "SELECT p.pid, p.snumber, p.city, p.price, p.country, p.postcode, p.ptype, p.features FROM property p WHERE city = " + "\'" + city + "\'" + 
				" AND pid NOT IN (SELECT e.pid FROM events e WHERE NOT (e.edate <= " + "\'" + startSQLDate + "\'" + " OR e.sdate >=" + "\'" + 
				endSQLDate + "\')) ORDER BY p.pid";
		
		System.out.println(querySQL);
		System.out.println("");
		System.out.println("PID   StreetNumber   City   Monthly_Price   Country   PostalCode   Property_Type   4Features");
		System.out.println("______________________________________________________________________________");
	
		try
		{
			java.sql.ResultSet rs = statement.executeQuery ( querySQL ) ;

			while(rs.next())
			{
				int pid = rs.getInt(1);
				int snumber = rs.getInt(2);
				String pCity = rs.getString(3);
				int price = rs.getInt(4);
				String country = rs.getString(5);
				String postcode = rs.getString(6);
				String pType = rs.getString(7);
				String features = rs.getString(8);
				
				System.out.println(pid + "    " + snumber + "    " + pCity + "    " + price + "    " + country + "    " + postcode + "    " + pType + "    " + features);
				
				
			}
		} catch (SQLException e)
		{
			System.out.println("Code: " + e.getErrorCode() + "  sqlState: " + e.getSQLState());
		}
		scanner.nextLine();
		
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
			System.out.println("Enter your user id:");

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
		System.out.println("3) Create property");
		System.out.println("4) Find available properties");
		System.out.println("5) Update payment details");
		System.out.println("6) To Quit press 6");
	}

	public static void question(Statement statement, Scanner scanner, int uid) throws SQLException, ParseException
	{
		System.out.println("Would you like to proceed or quit?");
		System.out.println("To proceed enter 9.");
		System.out.println("If you wish to quit enter 0.");

		switch (scanner.nextInt()) 
		{
		case 0:
			System.out.println ("Thank you and godbye.");
			break;

		case 9:
			System.out.println ("Please proceed.");
			inputMenu(statement, scanner, uid);
			break;
		default:
			System.err.println ( "Unrecognized option" );
			break;
		} 
		
		scanner.nextLine();
	}

	public static void inputMenu(Statement statement, Scanner scanner, int uid) throws SQLException, ParseException
	{
		display_menu();

		switch (scanner.nextInt())
		{
		case 1:
			scanner.nextLine();
			System.out.println("Creating user account");
			createUser(statement, scanner);
			question(statement, scanner, uid);
			break;
		case 2:
			scanner.nextLine();
			System.out.println("Deleting user account");
			deleteUser(statement, scanner);
			question(statement, scanner, uid);
			break;
		case 3:
			scanner.nextLine();
			System.out.println("Creating a property");
			createProperty(statement,uid,scanner);
			question(statement, scanner, uid);
			break;
		case 4:
			scanner.nextLine();
			checkAvailableProp(statement, scanner);
			question(statement, scanner, uid);
			break;
		case 5:
			scanner.nextLine();
			updatePayment(statement,uid, scanner);
			question(statement, scanner, uid);
			break;
		case 6:
			scanner.nextLine();
			System.out.println ("Thank you and godbye.");
			break;
		default:
			System.err.println ( "Unrecognized option" );
			break;	
		}
		scanner.nextLine();
	}
}
