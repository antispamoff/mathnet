package krc.etl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.*;

public class LoadMathNet {

	public static void main(String[] agrs) {
		try {
			//Document doc = Jsoup.parse(in, null, "");//"http://www.mathnet.ru/php/person.phtml?option_lang=rus");
			Connection conn = Jsoup.connect("http://www.mathnet.ru/php/person.phtml?option_lang=rus");
			Document doc =	conn
//					.header("Host", "www.mathnet.ru")
//					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//					.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4")
//					.header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
//					.header("Connection", "close")
//					.referrer("http://www.mathnet.ru/php/person.phtml?option_lang=rus")
//					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
//					//.url("http://www.mathnet.ru/php/person.phtml?option_lang=rus")
					.method(Connection.Method.POST)
					.data("lname","%E0","note","","email","","orgname","","slanguage","rus","abroad","null","action","fletter")
					.execute().parse();
			System.out.println(doc.text());
//			Elements links = doc.select("a[href]");
//			for (Element link : links) {
//				if(link.attr("href").contains("personid")) {
//					System.out.println(link.attr("href").split("personid=")[1] + " : " + link.text());
//					System.out.println(link.attr("href"));
//					System.out.println(link);
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}



