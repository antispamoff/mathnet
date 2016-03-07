package krc.etl;


import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import krc.util.MySQLUtils;

class Author {
	int id;
	String personid; 
	String name; 
	String surname; 
	String lastname; 
	String altFIO;
	public String toString() {
		return personid + " : " + "lastname=" + lastname + ", name=" + name + ", surname=" + surname + ", FIO=" + altFIO;
	}
}

public class LoadAuthors {
	static PrintWriter f;
	private static String workPath = "D:/Temp";
	final static String host = "www.mathnet.ru";
	final static String[][] letterListcp1251 = new String[][]{
//		{"E0", "а"}, {"E1", "б"}, {"E2", "в"}, {"E3", "г"}, {"E4", "д"}, {"E5", "е"},
//		{"B8", "ё"}, {"E6", "ж"}, {"E7", "з"}, {"E8", "и"}, {"E9", "й"}, {"EA", "к"},
//		{"EB", "л"}, {"EC", "м"}, {"ED", "н"}, {"EE", "о"}, {"EF", "п"}, {"F0", "р"},
//		{"F1", "с"}, {"F2", "т"}, {"F3", "у"}, {"F4", "ф"}, {"F5", "х"}, {"F6", "ц"},
//		{"F7", "ч"}, {"F8", "ш"}, {"F9", "щ"}, {"FA", "ъ"}, 
		{"FB", "ы"}, //{"FC", "ь"},
//		{"FD", "э"}, //{"FE", "ю"}, {"FF", "я"},		  
	};

	private static void loadLetter(String[] letterData) throws IOException {
		System.out.println("Символ: " + letterData[1]);
		ArrayList<Author> authors = getAuthorsByLetter(letterData[0]);
		saveAuthors(authors, letterData[1]);
	}

	private static void saveAuthors(ArrayList<Author> authors, String firstLetter) {
		int ins = 0;
		try {
			Connection conn = MySQLUtils.getMySQLConnection();
			Statement st = conn.createStatement();
			int del = st.executeUpdate("delete from authors where lower(left(lastname,1))=lower('" + firstLetter + "')");
			System.out.println("Удалено авторов на '" + firstLetter + "' - " + del);
			PreparedStatement ps = conn.prepareStatement("insert into authors (personid, name, surname, lastname, altFIO) values (?, ?, ?, ?, ?)");
			for (Author a : authors) {
				ps.clearParameters();
				ps.setString(1, a.personid);
				ps.setString(2, a.name);
				ps.setString(3, a.surname);
				ps.setString(4, a.lastname);
				ps.setString(5, a.altFIO);
				ps.execute();
				ins++;
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		System.out.println("Добавлено авторов на '" + firstLetter + "' - " + ins);
	}

	private static ArrayList<Author> getAuthorsByLetter(String lettercp1251) throws IOException {
		ArrayList<Author> authorList = new ArrayList<Author>();
		try
		{
			Socket s = new Socket();
		    s.connect(new InetSocketAddress(host , 80));
		    System.out.println(host + " connected");
			String params = "option_lang=rus&lname=%" + lettercp1251 + "&note=&email=&orgname=&slanguage=rus&abroad=null&action=fletter";
			String message = "POST /php/person.phtml HTTP/1.1\r\n" + 
					"Host: www.mathnet.ru\r\n" + 
					"User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36\r\n" + 
					"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
					"Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4\r\n" + 
					"Content-Type: application/x-www-form-urlencoded\r\n" +
					"Content-Length: " + params.length() + "\r\n" +
					"Connection: close\r\n\r\n" + 
					params + "\r\n";
		    //writer for socket
		    PrintWriter s_out = new PrintWriter(s.getOutputStream(), true);
			//Send message to server
			s_out.println(message);
			System.out.print("POST data sent...");
			//Get response from server
			String response;
		    //reader for socket
		    BufferedReader s_in = new BufferedReader(new InputStreamReader(s.getInputStream(), "windows-1251"));
		    int cnt = 0;
			System.out.println("Getting data...");
			while ((response = s_in.readLine()) != null) 
			{
				if (response.contains("personid=")) {
					Author author = new Author();
					author.personid = response.substring(response.indexOf("personid=") + "personid=".length(), response.indexOf("'>"));
					String[] FIO = response.substring(response.indexOf("'>") + 2,response.indexOf("</a>")).split(" ");
					author.lastname = FIO.length>0 ? FIO[0]:"";
					author.name = FIO.length>1 ? FIO[1]:"";
					author.surname = FIO.length>2 ? FIO[2]:"";
					//f.println(response);
					if ((response = s_in.readLine()) != null)
						if (response.contains("[") && response.contains("]")) {
							author.altFIO = response.substring(response.indexOf('[')+1, response.indexOf(']'));
							//f.println(response);
						}
					f.println(author);
					authorList.add(author);
					cnt++;
				}
			}
			s.close();
			System.out.println("Загружено с " + host + ": "+ cnt);
		}
		catch (UnknownHostException e) 
		{
			System.err.println("Host not found: " + host);
		    System.exit(1);
		}
		return authorList;
	}

	public static void main(String[] args) throws IOException, InterruptedException, SQLException 
	{
		File out = new File(workPath + "/authors.txt");
		out.createNewFile();
		out.setWritable(true);
		f = new PrintWriter(out);
		       
		for (String[] letterData : letterListcp1251) {
			loadLetter(letterData);
			Thread.sleep(2000);
		}
		f.flush();
		f.close();
	}
	
}
