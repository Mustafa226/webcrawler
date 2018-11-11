import org.junit.Test;

public class BasicWebCrawlerTest {

    @Test
    public void tesSearch() {
        BasicWebCrawler crawler = new BasicWebCrawler();
        crawler.search("http://arstechnica.com/", "computer");
    }

}