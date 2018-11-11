import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class AmazonWebCrawler {

    public static final String HTTPS_WWW_AMAZON_COM = "https://www.amazon.com";
    private static final String SEPARATOR_CHARS = ":";
    private static final String ROBOTS_TXT = "robots.txt";
    private static Logger LOG = Logger.getLogger(AmazonWebCrawler.class.getName());
    private static List<String> CRAWLER_DIRECTIVES = Arrays.asList("USER-AGENT", "ALLOW", "DISALLOW");
    private Map<String, Set<String>> urlMap;

    public AmazonWebCrawler() {
        urlMap = new HashMap<>();
        CRAWLER_DIRECTIVES.forEach(directive -> urlMap.put(directive, new HashSet<>()));
    }

    protected void crawl() {
        this.parseRobotsTxtFile();
        Set<String> allowedUrls = urlMap.get("ALLOW");
        allowedUrls.forEach(url -> {
            try {
                Document document = Jsoup.connect(HTTPS_WWW_AMAZON_COM + url).get();
                if (Objects.nonNull(document)) {
                    LOG.info("Received document for url" + url);
                }
            } catch (IOException e) {
                LOG.warning(e.getMessage());
            }
        });
    }

    protected void parseRobotsTxtFile() {
        // TODO: read & parse robots.txt file directly from URL i.e. https://www.amazon.com/robots.txt
        File robotsTxt = new File(ROBOTS_TXT);
        if (robotsTxt.canRead() && robotsTxt.isFile()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(robotsTxt))) {
                bufferedReader.lines().forEach(this::parseLine);
            } catch (IOException e) {
                LOG.warning(e.getMessage());
            }
        }
    }

    private void parseLine(String line) {
        String[] split = StringUtils.split(line, SEPARATOR_CHARS);
        if (split.length >= 2) {
            String key = split[0];
            String value = StringUtils.deleteWhitespace(split[1]);
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                Optional.ofNullable(urlMap.get(key.toUpperCase()))
                        .ifPresent(strings -> strings.add(value));
            }
        }
    }

}
