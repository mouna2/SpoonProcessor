package Tables;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import spoon.reflect.factory.ClassFactory;

public class TableRequirements {

	public void requirements(Statement st, ClassFactory classFactory) throws FileNotFoundException, SQLException {
		// TODO Auto-generated method stub
		File file = new File("C:\\Users\\mouna\\git\\SpoonProcessor\\Requirements.txt");
		 FileReader fileReader = new FileReader(file);
		 BufferedReader bufferedReader = new BufferedReader(fileReader);
		 StringBuffer stringBuffer = new StringBuffer();

		 
		String line;
		try {
			

			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				
				
				
			
				
				String statement = "INSERT INTO `requirements`(`requirementname`) VALUES ('"+line+"')";		
				st.executeUpdate(statement);
			
				
				
			}




			}
			
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
