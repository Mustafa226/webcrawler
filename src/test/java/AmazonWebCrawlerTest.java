import org.junit.Test;

import static org.junit.Assert.*;

public class AmazonWebCrawlerTest {

    private AmazonWebCrawler crawler = new AmazonWebCrawler();

    @Test
    public void parseRobotsTxtFile() {
        crawler.parseRobotsTxtFile();
    }

    @Test
    public void crawl() {
        crawler.crawl();
    }
}