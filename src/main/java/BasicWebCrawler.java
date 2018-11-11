import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class BasicWebCrawler {

    private static int MAX_NUMBER_OF_PAGES_TO_VISIT = 10;
    private Logger LOG = Logger.getLogger(BasicWebCrawler.class.getName());
    private Set<String> pagesVisited;
    private List<String> pagesToVisit;
    private List<String> links;
    private Document htmlDocument;

    private BasicWebCrawler() {
        links = new LinkedList<String>();
        pagesVisited = new HashSet<String>();
        pagesToVisit = new LinkedList<String>();
    }

    public static void main(String[] args) {
        BasicWebCrawler crawler = new BasicWebCrawler();
        crawler.search("http://arstechnica.com/", "computer");
    }

    private void search(String url, String searchWord) {
        while (this.pagesVisited.size() < MAX_NUMBER_OF_PAGES_TO_VISIT) {
            String currentUrl;
            BasicWebCrawler crawler = new BasicWebCrawler();
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }
            crawler.crawl(currentUrl);
            boolean success = crawler.searchForWord(searchWord);
            if (success) {
                LOG.info("Found word " + searchWord + " at URL " + currentUrl);
            }
            this.pagesToVisit.addAll(crawler.getLinks());
        }
    }

    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }

    private List<String> getLinks() {
        return this.links;
    }

    private void crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;

            System.out.println("Received web page at " + url);

            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
        } catch (IOException ioe) {
            // We were not successful in our HTTP request
            System.out.println("Error in out HTTP request " + ioe);
        }
    }

    private boolean searchForWord(String searchWord) {
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    private void getPageLinks(String URL) {
        if (!pagesVisited.contains(URL)) {
            try {
                if (pagesVisited.add(URL)) {
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
