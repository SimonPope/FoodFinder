import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
	
	public Main() {
		System.out.println("Downloading page");
		List<String> page = getPage("http://www.victoria.ac.nz/events?mode=results&current_result_page=1&results_per_page=1000");
		System.out.println("Getting event links");
		List<String> links = new ArrayList<>();
		boolean eventLink = false;
		for (String line : page) {
			//the line after "showLink" is an event link
			if (eventLink) {
				int start = line.indexOf('"') + 1;
				int finish = line.indexOf('"', start);
				links.add(line.substring(start, finish));
				eventLink = false;
			}
			//when this line occurs, the next will be an event link
			if (line.contains("showLink\">")) {
				eventLink = true;
			}
		}
		System.out.println("Checking events");
		Set<String> found = new HashSet<>();
		for (String link : links) {
			List<String> event = getPage(link);
			if (event == null) {
				continue;
			}
			boolean section = false;
			for (String line : event) {
				//if information section has started
				if (section) {
					//end of information section
					if (line.contains("/section")) {
						break;
					}
					
					String lower = line.toLowerCase();
					if (lower.contains("food") || lower.contains("food") || lower.contains("drink") || lower.contains("snack") || lower.contains("refreshment")) {
						found.add(link);
					}
				}
				else {
					//look for start of information section
					if (line.contains("<section role=\"main\" class=\"primary\">")) {
						section = true;
					}
				}
			}
		}
		System.out.println("Events checked");
		System.out.println("Making page");
		makePage(found);
		System.out.println("Page made");
	}
	
	private static List<String> getPage(String page) {
		List<String> lines = new ArrayList<>();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new URL(page).openStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				lines.add(inputLine);
			}
			in.close();
		}
		catch(@SuppressWarnings("unused") Exception e) {
			return null;
		}
		return lines;
	}
	
	static void makePage(Set<String> links) {
		ArrayList<String> lines = new ArrayList<>();
		lines.add("<html>");
		lines.add("<body>");
		for (String link : links) {
			lines.add("<div>");
			lines.add("<a href=\""+link+"\">");
			lines.add(link);
			lines.add("</a>");
			lines.add("</div>");
		}
		lines.add("</body>");
		lines.add("</html>");
		String[] linesArray = new String[lines.size()];
		linesArray = lines.toArray(linesArray);
		try(PrintWriter writer = new PrintWriter("found.html", "UTF-8")) {
			for (String line : linesArray) {
				writer.println(line);
			}
		}
		catch (@SuppressWarnings("unused") FileNotFoundException e) {}
		catch (@SuppressWarnings("unused") UnsupportedEncodingException e) {}
	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		new Main();
	}
}
