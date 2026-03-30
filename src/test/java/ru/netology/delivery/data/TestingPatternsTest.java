package ru.netology.delivery.data;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;
import static ru.netology.delivery.data.DataGenerator.generateDate;

import java.time.Duration;
import java.util.Locale;

public class TestingPatternsTest {


    @BeforeEach
    void setup() {
        Selenide.open("http://localhost:9999/");
    }

    @Test
    void shouldFillValid () { //валидные данные

        // Генерация пользовательских данных
        DataGenerator.UserInfo userInfo = DataGenerator.Registration.generateUser("ru", 4, "dd.MM.yyyy");
        String city = userInfo.getCity();
        String name = userInfo.getName();
        String phone = userInfo.getPhone();
        String planningDate = generateDate(4);
        String planningDate1 = generateDate(6);// генерируем дату на 4 дня вперёд

        // Заполнение полей формы
        $("[data-test-id='city'] input").setValue(city);
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate);
        $("[name='name']").setValue(name);
        $("[name='phone']").setValue(phone);
        $("[data-test-id='agreement']").click();
        $("button .button__text").shouldHave(Condition.text("Запланировать")).click();

        // Проверка успешного уведомления
        $("[data-test-id='success-notification']").shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + planningDate),
                Duration.ofSeconds(15)).shouldBe(Condition.visible);

        // Заплнение другой даты
        $("[data-test-id='date'] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE)
                .setValue(planningDate1);
        $("button .button__text").shouldHave(Condition.text("Запланировать")).click();

        // Проверка успешного уведомления
        $("[data-test-id='replan-notification']").shouldHave(Condition.text("Необходимо подтверждение У вас уже запланирована встреча на другую дату. Перепланировать? "),
                Duration.ofSeconds(15)).shouldBe(Condition.visible);
        $("[data-test-id='replan-notification'] .button__text").shouldHave(Condition.text("Перепланировать")).click();

        $("[data-test-id='success-notification']").shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + planningDate1),
                Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }
}
