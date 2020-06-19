import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Configuration.startMaximized;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class EndToEndTest {
    @Test
    public void LoginMaxBetBetLogout() throws InterruptedException {
        startMaximized = true;

//        1.Авторизация
        open("https://ac1.rub90.com/#/");
        $(byText("Войти")).click();
        $(byXpath("//input[@data-field=\"login\"]")).setValue("Pasha94PS");
        $(byXpath("//input[@data-field=\"password\"]")).setValue("Local2000").pressEnter();


//     скролл контентной области
//        SelenideElement scr = $(byXpath("//div[@class='frame frame--lobby scrollarea']/div[@class='scrollarea__inner']"));
//        executeJavaScript("arguments[0].scrollBy(0,100)", scr);


//        # закрываем попап внизу
        $(byXpath("//i[@class='policy-popup__close icon icon-close ']")).click();


//        2.Добавить ставку в купон
        for (int i = 1; i < 100; i++) {
            SelenideElement k = $(byXpath("(//div[@class='widget__content']//span[@class='odd__rate'])[" + (i) + "]"));
            String txt = k.getText();
            boolean regex = Pattern.matches("\\d\\u002E\\d\\d", txt);
            if (regex == true) {
                try {
                    System.out.println(txt);
                    k.click();
                    if ($(byXpath("//input[@class='betslip__input']")).is(Condition.exist) == false) {
                        TimeUnit.SECONDS.sleep(2);
                        k.click();
                    }
                    break;
                } catch (Throwable e) {
                    System.out.println("Обработка исключения. Следующий кэф");
                }
            }
            else {
                System.out.println("Следующий кэф");
            }
        }


//        3. Проверка, что нельзя поставить больше maxbet
        $(byXpath("//input[@class='betslip__input']")).scrollTo().setValue("100000");
        if ($(byXpath("//div[@class='msg msg--warning'] ")).getText() == "Котировки или доступность события изменились!") {
            $(byText("Принять")).click();
            $(byXpath("//input[@class='betslip__input']")).scrollTo().setValue("100000");
        }
        $(byXpath("//div[@class='msg msg--warning']")).shouldHave(Condition.exactText("Сумма ставки превышает лимит. Нажмите \"Принять\", чтобы установить максимально допустимую сумму!"));


//        4.Поставить валидную ставку + проверка на приём
        $(byXpath("//input[@class='betslip__input']")).scrollTo().setValue("100");
        try {
            $(byText("Заключить пари")).click();
        } catch (ElementNotFound e) {
            $(byText("Принять")).click();
            if ($(byXpath("//input[@class='betslip__input']")).is(Condition.exist) == false) {
                for (int i = 1; i < 100; i++) {
                    SelenideElement k = $(byXpath("(//div[@class='widget__content']//span[@class='odd__rate'])[" + i + "]"));
                    String txt = k.getText();
                    boolean regex = Pattern.matches("\\d\\u002E\\d\\d", txt);
                    if (regex == true) {
                        k.click();
                        System.out.println(txt);
                        if ($(byXpath("//input[@class='betslip__input']")).is(Condition.exist) == false) {
                            TimeUnit.SECONDS.sleep(2);
                            k.click();
                        }
                        break;
                    } else {
                        System.out.println("Следующий кэф");
                    }
                }
                $(byXpath("//input[@class='betslip__input']")).scrollTo().setValue("100");
            }
            $(byText("Заключить пари")).click();
        }
        $(byXpath("//div[@class='msg msg--success']")).shouldHave(Condition.exactText("Все ставки приняты."));


//        5. Деавторизация + проверка
        $(byXpath("//div[@class='ellipsis']")).click();
        $(byText("Выход")).click();
        $(byXpath("//div[@class='btn btn--secondary']")).shouldHave(Condition.exactText("Войти"));


//        TimeUnit.SECONDS.sleep(5);
    }
}
