package io.github.imsejin.lzcodl.core;

import io.github.imsejin.common.util.FileUtils;
import io.github.imsejin.common.util.FilenameUtils;
import io.github.imsejin.lzcodl.TestUtils;
import io.github.imsejin.lzcodl.common.Loggers;
import io.github.imsejin.lzcodl.common.constant.URIs;
import io.github.imsejin.lzcodl.model.Arguments;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class CrawlerTest {

    private static ChromeDriver driver;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("webdriver.chrome.driver", TestUtils.getDriverPath().getPath());
        driver = new ChromeDriver(new ChromeOptions().addArguments(ChromeBrowser.ChromeOption.getArguments()));
    }

    @AfterAll
    static void afterAll() {
        driver.quit();
    }

    @Test
    void getNumOfImagesInEpisode() {
        // given
        String language = "ja";
        String comicName = "jisoo";
        String episodeName = "1";
        URI episodeUrl = URIs.EPISODE.get(language, comicName, episodeName);

        // when
        driver.get(episodeUrl.toString());

        WebElement scrollList = driver.findElementById("scroll-list");

        // Waits for DOM to complete the rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOfAllElements(scrollList));

        List<WebElement> images = scrollList.findElements(
                By.xpath(".//div[@class='cut' and not(contains(@class, 'cutLicense')) and @data-cut-index and @data-cut-type='cut']"));
        int actual = images.size();

        // then
        assertThat(actual).isEqualTo(11);
    }

    @Test
    @Disabled
    @SneakyThrows
    void downloadDaumWebtoon() {
        // Sets up the arguments.
        final Arguments args = Arguments.builder()
                .language("ko").debugging(true).comicName("NormalLikeThis").build();

        // Activates debug mode.
        if (args.isDebugging()) {
            Loggers.debugging();
            ChromeBrowser.debugging();
        }

        // Goes to login page.
        driver.get("https://accounts.kakao.com/login?continue=https%3A%2F%2Flogins.daum.net");

        // Waits for rendering.
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement emailInput = driver.findElement(By.id("id_email_2"));
        wait.until(ExpectedConditions.visibilityOfAllElements(emailInput));

        // Writes user information into <input>.
        emailInput.sendKeys(args.getUsername());
        WebElement passwordInput = driver.findElement(By.id("id_password_3"));
        passwordInput.sendKeys(args.getPassword());
        driver.findElement(By.xpath("//form[@id='login-form']/fieldset/div[8]/button[1]")).click();

        // Gets links of all episodes.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("http://webtoon.daum.net/webtoon/rss/" + args.getComicName());
        NodeList items = doc.getElementsByTagName("item");
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            Node node = items.item(i);
            NodeList children = node.getChildNodes();

            Node title = children.item(1);
            Node link = children.item(3);

            Map<String, String> map = new HashMap<>();
            map.put("title", title.getTextContent().trim());
            map.put("link", link.getTextContent().trim());
            list.add(map);
        }

        list = list.subList(3, 4);

        for (int i = 0; i < list.size(); i++) {
            Map<String, String> map = list.get(i);
            String link = map.get("link");

            // Goes to each episode page.
            driver.get(link);

            // Waits for rendering.
            WebElement imgView = driver.findElementByXPath("//div[@id='imgView']");
            wait.until(ExpectedConditions.visibilityOfAllElements(imgView));
            TimeUnit.SECONDS.sleep(3);

            // Gets image url in the episode.
            List<WebElement> images = imgView.findElements(By.xpath(".//img[@class='img_webtoon']"));
            List<String> sources = images.stream().map(it -> it.getAttribute("src")).collect(toList());

            // Creates a directory for the episode.
            String title = map.get("title");
            String comicDirName = String.format("D_%s", FilenameUtils.replaceUnallowables(args.getComicName()));
            String episodeName = String.format("%04d - %s", i + 1, title);
            Path dir = Paths.get("/data", comicDirName, episodeName);
            Files.createDirectories(dir);

            Loggers.getLogger().info("path: {}, numOfImages: {}", dir, sources.size());

            // Downloads the images.
            IntStream.range(0, sources.size()).parallel().forEach(j -> {
                String src = sources.get(j);
                String imageName = String.format("%03d.jpg", j + 1);
                try {
                    URL url = new URL(src);
                    File dest = new File(dir.toFile(), imageName);
                    FileUtils.download(url.openStream(), dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
