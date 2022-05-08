package avic;

import net.bytebuddy.asm.Advice;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test(priority = 0)
    public void checkThatBlockContainsSearchWord()
    {
        String block = driver.findElement(xpath("//div[contains(@class,'vis-text')]/p[3]/span")).getText();
        assertTrue(block.contains("технологии"));
    }

    @Test(priority = 1)
    public void checkThatBlockStartsWithText()
    {
        driver.findElement(xpath("//div[contains(@class,'top-links__left')]//a[@href='/tradein']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        String block = driver.findElement(xpath("//div[@class='content']/p[4]/span")).getText();
        assertTrue(block.startsWith("Наши клиенты"));
    }
    @Test(priority = 2)
    public void checkElementsAmountOnTwicePage()
    {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("samsung");
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(xpath("//div[@class='pagination']//a[contains(@class,'btn-see-more')]")).click();
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(300));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(@class,'active')][@data-paginator='2']")));
        List<WebElement> elementsList = driver.findElements(xpath("//div[@class='item-prod col-lg-3']"));
        int actualElementsSize = elementsList.size();
        assertEquals(actualElementsSize, 24);
    }

    @Test(priority = 3)
    public void checkThatFilterResultsContainsCorrectTag()
    {
        driver.findElement(xpath("//div[@class='category-items--min ']//a[contains(text(),'Samsung')]")).click();
        String resultOfCategorySearch = driver.findElement(xpath("//a[@class='tags__item']")).getText();
        assertEquals(resultOfCategorySearch, "Samsung");
    }

    @Test(priority = 4)
    public void checkMaxPriceLimit()
    {
        int MaxPriceOf = 20000;
        String MaxPriceOfInString = Integer.toString(MaxPriceOf);
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'game-zone')]")).click();
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'ochki')]")).click();
        WebDriverWait wait = new WebDriverWait(driver,  Duration.ofSeconds(240));
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//div[contains(@class,'js_filter_parent')]//input[contains(@class,'max')]")).clear();
        driver.findElement(xpath("//div[contains(@class,'js_filter_parent')]//input[contains(@class,'max')]")).sendKeys(MaxPriceOfInString, Keys.ENTER);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'open-filter-tooltip')]//span[contains(@class,'inner')]")));
        driver.findElement(xpath("//div[contains(@class,'open-filter-tooltip')]//span[contains(@class,'inner')]")).click();
        Select sortObject = new Select(driver.findElement(By.xpath("//div[@class='category-top']//select[@class='js-select sort-select js_sort_select select2-hidden-accessible']")));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        sortObject.selectByVisibleText("От дорогих к дешевым");
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        String priseMaxSearched = (driver.findElement(xpath("//div[@class='item-prod col-lg-3']//div[contains(@class,'prise-new')]")).getText()).split(" ")[0];
        assertTrue(Integer.valueOf(priseMaxSearched) < MaxPriceOf );
    }


    @AfterMethod
    public void tearDown() {
        driver.close();//закрытие драйвера
    }
}
