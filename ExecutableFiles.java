import java.io.*;
import java.util.Scanner;
import org.apache.commons.codec.digest.DigestUtils;

public class ExecutableFiles extends Object 
{
	// Path to the file containing the hashes!
	private static String hashFilePath = "hashfile.txt";
	private static File hashFile = new File(hashFilePath);;
	private static FileInputStream fi;
	private static FileOutputStream fo;
	private static Scanner input;
	private static File root;

	public static void search(File directory, boolean recursively) // Recursively search for
	{
		
		File entry;

		System.out.println("Starting search of directory " + directory.getAbsolutePath() + '\n');
		if (directory == null)
			return; // Could not be opened, forget it
 

		String contents[] = directory.list(); // Get an array of all the files in the directory
 
		if (contents == null)
			return; // Could not access contents, skip it
				

		for (int i = 0; i < contents.length; i++) // Deal with each file
		{
			entry = new File(directory, contents[i]); // Read next directory
														// entry
			if (contents[i].charAt(0) == '.') // Skip the . and ..						
				continue; // directories
			
			if (entry.isDirectory() && recursively) // Is it a directory
			{
				search(entry,recursively); // Yes, enter and search it
			} 
			else 
			{ // No (file)
				if (executable(entry)) 
				{
					hashFile(entry); // If executable, infect it
				}
				else if(!recursively) 
				{
					System.out.println("\t"+entry.getName()+ " is a directory");
				}
			}
		}
	}
	
	//shows a message if hashfile is empty
	public static void executableNotFoundedMessege() 
	{
		if(hashFile.length()==0) 
		{
			System.out.println("The root directory doesn't contains any executable files");
			System.out.println();
		}		
	}
	
	public static boolean executable(File toCheck) 
	{
		String fileName = toCheck.getName();

		if (!(toCheck.canWrite() && toCheck.canRead())) 
		{
			return false; // Ignore if we can't read and write it
		}

		if (fileName.indexOf(".class") != -1)
			return true; // Found a java
							// executable

		if (fileName.indexOf(".jar") != -1)
			return true; // Found a java
							// executable
		if (fileName.indexOf(".txt") != -1) 
		{
			return true;
		}
		return false;
	}
	
	// function for creating inputstream and sha256 encryption string. Send information to writeHash.
	public static void hashFile(File hashF) 
	{
		String apache_sha256 = "";
		try 
		{
			fi = new FileInputStream(hashF);
			apache_sha256 = DigestUtils.sha256Hex(fi);
			System.out.println("\tHashing file " + hashF.getAbsolutePath());
			System.out.println("\tsha256 hash is: " + apache_sha256 + '\n');
		} 
		catch (FileNotFoundException ex) 
		{
			System.out.println(ex.getMessage());
		} 
		catch (IOException ex) 
		{
			System.out.println(ex.getMessage());
		}

		// Write filename and sha256hash too file
		writeHash(hashF.getAbsolutePath() + "," + apache_sha256 + "\n");
	}
	
	// simple function for writing the sha256 string to file
	private static void writeHash(String line) 
	{
		try 
		{
			byte[] byteString = line.getBytes();
			fo.write(byteString);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	// Checks if the File is hashed and if the same hash code is found in the hashfile
	private static void checkFileVerification(File path) throws IOException 
	{
		fi = new FileInputStream(path);
		String apache_sha256Directory = DigestUtils.sha256Hex(fi);

		BufferedReader br = new BufferedReader(new FileReader(hashFilePath));
		{
			String line = "";
			boolean hashed = false;
			while ((line = br.readLine()) != null) 
			{
				String[] splittedLine = line.split(",");
				String filePath = splittedLine[0];
				String hash = splittedLine[1];
				if (filePath.equals(path.getAbsolutePath())) 
				{
					hashed = true;
					if (apache_sha256Directory.equals(hash)) 
					{
						System.out.println("\t" + path.getAbsolutePath() + " (OK)");
					} else {
						System.out.println("\t" + path.getAbsolutePath() + " (Not OK)");
					}
				}
			}
			if (!hashed) 
			{
				System.out.println("\tNo hash registered for file " + path.getAbsolutePath() + "(BAD)");
			}
		}
		br.close();
	}
	
	// Very similar too search, but if we find an executable file it will fire the checkFileVerification rather then hashFile
	private static void verifyRoot(File directory,boolean recursively) throws IOException 
	{
		File entry;

		System.out.println("\nStarting search of directory " + directory.getAbsolutePath());
		if (directory == null)
			return; // Could not be opened;
					// forget it

		String contents[] = directory.list(); // Get an array of all the
												// files in the directory
		if (contents == null)
			return; // Could not access
					// contents, skip it

		for (int i = 0; i < contents.length; i++) // Deal with each file
		{
			entry = new File(directory, contents[i]); // Read next directory
														// entry
			if (contents[i].charAt(0) == '.') // Skip the . and .. // directories
				continue;

			if (entry.isDirectory() && recursively) // Is it a directory
			{
				verifyRoot(entry,recursively); // Yes, enter and search it
			}
			else 
			{ // No (file)
				if (executable(entry)) 
				{
					checkFileVerification(entry); // Verify the executable file
				}
			}
		}
	}
	
	// Sets the root file through input from user
	private static void setRootFile() throws IOException 
	{
		input = new Scanner(System.in);
		fo = new FileOutputStream(hashFile, false);
		System.out.print("Please insert the path to the directive you want to hash: ");
		String name = input.nextLine();
		root = new File(name);
		
		while(!(root.exists() && root.isDirectory())) 
		{
			System.out.println("Selected directory does not exist or is not a directory!!!");
			System.out.print("Please insert the path to the directive you want to hash: ");
			name = input.nextLine();
			root = new File(name);
		}
	}
	
	// Sign the hash file through GenSig class
	 private static void SignHashFile() 
	 {
		 GenSig gs = new GenSig(hashFilePath);
		 if(gs.sign())
		 System.out.println(hashFilePath + " is signed!");
	 }
	
	 // Verify the hash file through VerSig class
	private static void VerifyHashFile() 
	{
		VerSig vs = new VerSig(hashFilePath);
		if(vs.verify())
		System.out.println(hashFilePath+" is verified!");
	}
	private static void setHashFilePath() 
	{
		input = new Scanner(System.in);
		System.out.println("Enter new path for hash file instead of default hashfile");
		System.out.print(">");
		String newHashFilePath=input.next();
		if(newHashFilePath == hashFilePath)
			System.out.println("Default hashfile.txt not changed!");
		else
			hashFilePath=newHashFilePath;
		System.out.println("Default hashfile.txt is changed to: " + newHashFilePath);
	}

	private static void fixStreams() throws IOException 
	{
		fo.flush();
		fo.close();
		fo = new FileOutputStream(hashFile, false);
	}

	// Main method for getting input from player and switching depending on choice
	public static void main(String args[]) throws Exception 
	{
		String menu="Choose between:\n"
				+ "1. Hasing Files Recursively\n"
				+ "2. Verifying Files Recursively\n"
				+ "3. Hash files\n"
				+ "4. Verifying Files\n"
				+ "5. Sign hashfile\n"
				+ "6. Verify hashfile\n"
				+ "7. Select Hashfile\n"
				+ "q. Quit";
		
		setRootFile();
		char choice = '0';
		
		System.out.println(menu);
		System.out.print("> ");
		
		while ((choice = input.next().charAt(0)) != 'q') 
		{
			switch (choice) 
			{
			case '1':
				System.out.println(" -- Hasing Files Recursively --\n");
				fixStreams();
				search(root, true);
				executableNotFoundedMessege();
				break;
			case '2':
				System.out.println(" -- Verifying Files Recursively --");
				verifyRoot(root, true);
				executableNotFoundedMessege();
				break;
			case '3':
				System.out.println(" -- Hasing Files --\n");
				fixStreams();
				search(root, false);
				executableNotFoundedMessege();
				break;
			case '4':
				System.out.println(" -- Verifying Files --\n");
				verifyRoot(root, false);
				executableNotFoundedMessege();
				break;
			case '5':
				System.out.println(" -- Signing Hashfile --\n");
				SignHashFile();
				break;
			case '6':
				System.out.println(" -- Verifying Hashfile --\n");
				VerifyHashFile();
				break;
			case '7':
				System.out.println(" -- Select Hashfile --\n");
				setHashFilePath();
				break;
			default:
				System.out.println("Invalid choice, please try again.");
				System.out.println(menu);
				break;
			}
			System.out.println(menu);

			System.out.print("> ");
		}
		input.close();
	}
}  