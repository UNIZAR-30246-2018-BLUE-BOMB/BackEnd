package bluebomb.urlshortener.services;

import bluebomb.urlshortener.exceptions.DownloadHTMLInternalException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;



@Service
public class HTMLDownloader {

    /**
     * Download the HTML that contains urlToDownload
     *
     * @param urlToDownload url
     * @return HTML that contains urlToDownload
     * @throws DownloadHTMLInternalException if something go wrong in download
     */
    
    @Cacheable("htmlPagesCache")
    public String download(String urlToDownload) throws DownloadHTMLInternalException {
        try {
            // TODO: Resolve relative path (Jsoup)
            Document doc = Jsoup.connect(urlToDownload).get();
            Element link = doc.select("a").first();
            String relHref = link.attr("href"); 
            String absHref = link.attr("abs:href"); 
            return doc.html();
        } catch (IOException e) {
            throw new DownloadHTMLInternalException(e.getMessage());
        }
    }
}
