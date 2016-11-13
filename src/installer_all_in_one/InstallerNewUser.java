package installer_all_in_one;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InstallerNewUser {
	private Connection connection;
	
	public void install(){
		if(openConnection()){
			createTables();
			insertMasterPass();
			insertProvinceNames();
			insertCityNames();
			insertSpecNames();
			insertSubSpecNames();
		}
	}

	private boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("Please attach the database library to project");
			return false;
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pezeshkyar_new_user", "root", "dreadlord");

		} catch (SQLException e) {
			
			System.out.println("Error Opening connection: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean createTables(){
		Statement stmt;
		try {
			stmt = connection.createStatement();
			
			String sql = "CREATE TABLE IF NOT EXISTS master " +
	                   "(username VARCHAR(255) not NULL, " +
	                   " password VARCHAR(255))"; 
		    stmt.executeUpdate(sql);
			
		    sql = "CREATE TABLE IF NOT EXISTS province " +
	                   "(id INTEGER not NULL, " +
	                   " name VARCHAR(255), " + 
	                   " PRIMARY KEY ( id ))"; 
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS city " + 
		    		"(id INTEGER not NULL, " + 
		    		" provinceid Integer not NULL, " + 
		    		" name varchar(255), "
		    		+ " primary key(id), "
		    		+ "FOREIGN KEY (provinceid) REFERENCES province(id) ON DELETE CASCADE)";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS spec " + 
		    		"(id integer not NULL, " + 
		    		" spec varchar(255) not NULL, " + 
		    		" primary key(id) )";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS subspec " + 
		    		"(id integer not NULL, " + 
		    		" specid int not NULL, " +
		    		" subspec varchar(255) not NULL, " +
		    		" PRIMARY KEY(id), " +
		    		" FOREIGN KEY(specid) REFERENCES spec(id) ON DELETE CASCADE)";
		    stmt.executeUpdate(sql);

		    
		    sql = "CREATE TABLE IF NOT EXISTS office "
		    		+ " (id integer not NULL, "  
		    		+ " spec integer, "
		    		+ " subspec integer, "
		    		+ " address text, "
		    		+ " phoneno varchar(20), "
		    		+ " cityid integer, "
		    		+ " latitude decimal(9,6), "
		    		+ " longitude decimal(9,6), "
		    		+ " timequantum integer, "
		    		+ " biography text, "
		    		+ " primary key(id), "
		    		+ " FOREIGN KEY(spec) REFERENCES spec(id) ON DELETE CASCADE, "
		    		+ " FOREIGN KEY(subspec) REFERENCES subspec(id) ON DELETE CASCADE)";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS user( " 
		    		+ "id integer not NULL, "
		    		+ "username varchar(255), "  
		    		+ " password varchar(255), "  
		    		+ " mobileno varchar(255), " 
		    		+ " name varchar(255), " 
		    		+ " lastname varchar(255), " 
		    		+ " cityid integer, " 
		    		+ " photo mediumblob, "
		    		+ " email varchar(1023), " 
		    		+ " primary key(id), "
		    		+ " CONSTRAINT uniqueuser UNIQUE(username) "
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "create table IF NOT EXISTS useroffice(userid integer NOT NULL, "
		    		+ "officeid integer NOT NULL, "
		    		+ "primary key (userid, officeid), "
		    		+ "foreign key (userid) references user(id) on delete cascade, "
		    		+ "foreign key (officeid) references office(id) on delete cascade)";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS doctoroffice "
		    		+ " (doctorid integer NOT NULL, "
		    		+ " officeid integer NOT NULL, "
		    		+ " PRIMARY KEY (doctorid, officeid), "
		    		+ " FOREIGN KEY(doctorid) REFERENCES user(id) ON DELETE CASCADE, "
		    		+ " FOREIGN KEY(officeid) REFERENCES office(id) ON DELETE CASCADE) ";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS secretary " +
		    "(officeid integer not NULL, "
		    + " secretaryid int not NULL,"
		    + " PRIMARY KEY (officeid, secretaryid), "
		    + " FOREIGN KEY(officeid) REFERENCES office(id) ON DELETE CASCADE, "
		    + " FOREIGN KEY(secretaryid) REFERENCES user(id) ON DELETE CASCADE )";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS turn " +
		    "( id integer not NULL, "
		    + " date varchar(255) not NULL,"
		    + " starthour integer not NULL, "
		    + " startminute integer not NULL, "
		    + " duration integer not NULL, "
		    + " capacity integer not NULL, "
		    + " reserved integer, "
		    + " officeid integer not NULL, "
		    + " PRIMARY KEY(id), "
		    + " FOREIGN KEY(officeid) REFERENCES office(id) ON DELETE CASCADE ) ";
		    stmt.executeUpdate(sql);

		    sql = "CREATE TABLE IF NOT EXISTS taskgroup " +
				    "( id integer not NULL, "
				    + " name varchar(1023) not NULL,"
				    + " officeid integer not NULL, "
				    + " PRIMARY KEY(id), "
				    + " FOREIGN KEY (officeid) REFERENCES office(id) ON DELETE CASCADE ON UPDATE CASCADE )";
				    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS task " +
		    "( id integer not NULL, "
		    + " name varchar(1023) not NULL,"
		    + " price integer, "
		    + " officeid integer not NULL, "
		    + " taskgroup integer not null, "
		    + " PRIMARY KEY(id), "
		    + " FOREIGN KEY (taskgroup) REFERENCES taskgroup(id) ON DELETE CASCADE ON UPDATE CASCADE, "
		    + " FOREIGN KEY (officeid) REFERENCES office(id) ON DELETE CASCADE ON UPDATE CASCADE )";
		    stmt.executeUpdate(sql);

		    sql = "CREATE TABLE IF NOT EXISTS reserve " +
		    "( id integer not NULL, "
		    + " userid integer not NULL,"
		    + " turnid integer not NULL, "
		    + " taskid integer, "
		    + " numberofturns integer, "
		    + " patientid integer, "
		    + " firstturn integer, "		// baraye barghararie ertebat beine task-haye be ham peivasteh
		    + " payment integer, "
		    + " description text, "
		    + " price integer, "
		    + " PRIMARY KEY(id), "
		    + " FOREIGN KEY (userid) REFERENCES user(id) ON DELETE CASCADE, "
		    + " FOREIGN KEY (patientid) REFERENCES user(id) ON DELETE CASCADE, "
		    + " FOREIGN KEY (turnid) REFERENCES turn(id) ON DELETE CASCADE, "
		    + " FOREIGN KEY (taskid) REFERENCES task(id) ON DELETE CASCADE ) ";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS message "
		    		+ "( id integer not NULL , "
		    		+ "officeid integer not NULL, "
		    		+ "senderid integer not NULL, "
		    		+ "receiverid integer not NULL, "
		    		+ "isread integer, "
		    		+ "subject varchar(1024), "
		    		+ "message text, "
		    		+ "date varchar(255), "
		    		+ "time varchar(255), "
		    		+ "PRIMARY KEY(id), "
		    		+ "FOREIGN KEY (officeid) REFERENCES office(id) ON DELETE CASCADE,"
		    		+ "FOREIGN KEY(senderid) REFERENCES user(id) ON DELETE CASCADE, "
		    		+ "FOREIGN KEY(receiverid) REFERENCES user(id) ON DELETE CASCADE  ) ";
		    stmt.executeUpdate(sql);

		    sql = "CREATE TABLE IF NOT EXISTS gallery( " 
		    		+ "id integer not NULL, "
		    		+ "officeid integer not NULL, "
		    		+ " photo mediumblob,"
		    		+ "description varchar(5000),"
		    		+ "date varchar(50), " 
		    		+ " primary key(id, officeid) "
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS maxgallerypic( " 
		    		+ "officeid integer not NULL, "
		    		+ " maxpic integer, "
		    		+ " maxpicid integer, " 
		    		+ " FOREIGN KEY (officeid) REFERENCES office(id) ON DELETE CASCADE "
		    		+ ")";
		    stmt.executeUpdate(sql);

		    sql = "CREATE TABLE IF NOT EXISTS ticketsubject ("
		    		+ "id integer, subject varchar(200), primary key (id)"
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS ticket("
		    		+ "id integer not NULL, "
		    		+ "userId integer, subjectId integer, "
		    		+ "topic varchar(500), priority integer, "
		    		+ "startDate varchar(50), endDate varchar(50), primary key (id),"
		    		+ "  foreign key (subjectId) references ticketsubject (id)"
		    		+ ")";
		    stmt.executeUpdate(sql);


		    sql = "CREATE TABLE IF NOT EXISTS ticketmessage ("
		    		+ "id integer not Null, userId integer,"
		    		+ " message varchar(5000), dateMessage varchar(50),"
		    		+ " ticketId integer, primary key (id), "
		    		+ " foreign key (ticketId) references ticket (id)"
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS question ("
		    		+ "id integer not Null, label varchar(200),"
		    		+ " replytype integer,"
		    		+ " officeId integer, primary key (id), "
		    		+ " foreign key (officeId) references office(id)"
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS reply ("
		    		+ "id integer not Null,"
		    		+ " userId integer,"
		    		+ " questionId integer, replyType varchar(2000), "
		    		+ " primary key (id),"
		    		+ " foreign key (userId) references user(id), "
		    		+ " foreign key (questionId) references question(id)"
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    sql = "CREATE TABLE IF NOT EXISTS usersupport ("
		    		+ "userid integer NOT NULL, "
		    		+ "description varchar(1024), "
		    		+ "FOREIGN KEY (userid) references user(id), "
		    		+ "CONSTRAINT uniquesupportuserid UNIQUE(userid) "
		    		+ ")";
		    stmt.executeUpdate(sql);
		    
		    stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	      
		return true;
	}
	
	private void insertMasterPass(){
		PreparedStatement stmt = null;
		String line;

		String query = "insert into master(username, password) values(?, ?)";
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new FileInputStream("master.txt"), "UTF8"));
			stmt = connection.prepareStatement(query);
			
			line = br.readLine();
			if(line == null) {br.close(); return;}
			stmt.setString(1, line);
			line = br.readLine();
			if(line == null) {br.close(); return;}
			stmt.setString(2, line);
			
			stmt.executeUpdate();
			stmt.close();
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void insertProvinceNames(){
		String line;
		String[] sep;
		PreparedStatement stmt = null;
		String query = "insert into province(id, name) values(? ,?)";
		
		try {
			BufferedReader br = new BufferedReader(
							new InputStreamReader(
							new FileInputStream("Tbl_Ostan.csv"), "UTF8"));
			stmt = connection.prepareStatement(query);
			
			line = br.readLine();
			while(line != null){
				sep = line.split(",");
				
				int id = G.utf8StrtoInt(sep[0].trim());
				stmt.setInt(1,id);
				stmt.setString(2, sep[1].trim());
				stmt.addBatch();
				
				line = br.readLine();
			}
			
			if(stmt != null){
				stmt.executeBatch();
				stmt.close();
			}
			
			br.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void insertCityNames(){
		String line;
		String[] sep;
		PreparedStatement stmt = null;
		int id, pid;
		String query = "insert into city(id, provinceid, name) values(?, ? ,?)";
		
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new FileInputStream("Tbl_Shahrestan.csv"), "UTF8"));
			stmt = connection.prepareStatement(query);
			
			line = br.readLine();
			while(line != null){
				sep = line.split(",");
				id = G.utf8StrtoInt(sep[0].trim());
				stmt.setInt(1, id);
				pid = G.utf8StrtoInt(sep[1].trim());
				stmt.setInt(2,pid);
				stmt.setString(3, sep[2].trim());
				stmt.addBatch();
				
				line = br.readLine();
			}
			
			if(stmt != null){
				stmt.executeBatch();
				stmt.close();
			}
			
			br.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}


	private void insertSpecNames(){
		String line;
		String[] sep;
		PreparedStatement stmt = null;
		int id;
		String query = "insert into spec(id, spec) values(?, ?)";
		
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new FileInputStream("tbl_spec.csv"), "UTF8"));
			stmt = connection.prepareStatement(query);
			
			line = br.readLine();
			while(line != null){
				sep = line.split(",");
				if(sep.length >= 2) { 
					id = G.utf8StrtoInt(sep[0].trim());
					stmt.setInt(1, id);
					stmt.setString(2, sep[1].trim());
					stmt.addBatch();
				}
				
				line = br.readLine();
			}
			
			if(stmt != null){
				stmt.executeBatch();
				stmt.close();
			}
			
			br.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	
	private void insertSubSpecNames(){
		String line;
		String[] sep;
		PreparedStatement stmt = null;
		int id, sid;
		String query = "insert into subspec(id, specid, subspec) values(?, ? ,?)";
		
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
					new FileInputStream("tbl_subspec.csv"), "UTF8"));
			stmt = connection.prepareStatement(query);
			
			line = br.readLine();
			while(line != null){
				sep = line.split(",");
				id = G.utf8StrtoInt(sep[0].trim());
				stmt.setInt(1, id);
				sid = G.utf8StrtoInt(sep[1].trim());
				stmt.setInt(2,sid);
				stmt.setString(3, sep[2].trim());
				stmt.addBatch();
				
				line = br.readLine();
			}
			
			if(stmt != null){
				stmt.executeBatch();
				stmt.close();
			}
			
			br.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args) {
		InstallerNewUser inst = new InstallerNewUser();
		inst.install();

	}

	
}
