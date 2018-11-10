import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;

public class BasicWebCrawler {

    private Logger LOG = Logger.getLogger(BasicWebCrawler.class.getName());

    private HashSet<String> links;

    private BasicWebCrawler() {
        links = new HashSet<String>();
    }

    public static void main(String[] args) {
        new BasicWebCrawler().getPageLinks("http://www.mkyong.com/");
    }

    private void getPageLinks(String URL) {
        if (!links.contains(URL)) {
            try {
                if (links.add(URL)) {
                    LOG.info("URL " + URL + " has been added!");
                }

                Document document = Jsoup.connect(URL).get();

                Elements linksOnPage = document.select("a[href]");
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }


            } catch (IOException e) {
                LOG.warning(e.getMessage());
            }
        }
    }

}
