import java.io.BufferedReader; // Буферизуємо читання консолі й текстових файлів.
import java.io.BufferedWriter; // Буферизуємо запис у вихідний файл.
import java.io.IOException; // Обробляємо помилки доступу до файлів і потоків.
import java.io.InputStreamReader; // Перетворюємо байтовий System.in на символьний Reader.
import java.nio.charset.StandardCharsets; // Явно застосовуємо UTF-8 для українського тексту.
import java.nio.file.Files; // Стандартні операції відкриття й перевірки файлів.
import java.nio.file.Path; // Path зберігає шлях відповідно до поточної ОС.
import java.nio.file.StandardOpenOption; // Опції визначають додавання або перезапис файлу.
import java.util.ArrayList; // Зберігаємо всі слова, включно з повтореннями.
import java.util.Collections; // Використовуємо стандартне сортування списку.
import java.util.List; // Інтерфейс для параметрів і результатів зі списками.
import java.util.Locale; // Locale.ROOT забезпечує незалежне від налаштувань приведення регістру.
import java.util.TreeSet; // Перетин слів подаємо як відсортовану множину без дублікатів.
public class Main { // Лабораторна 4: перетин словників і посимвольний запис до EOF.
    private static final BufferedReader CONSOLE = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8)); // Один Reader не губить символи через конкуруючі буфери.
    private static String ask(String prompt) throws IOException { // Допоміжний метод для читання шляхів і відповідей.
        System.out.print(prompt); // Виводимо запрошення в консоль.
        String line = CONSOLE.readLine(); // Читаємо весь рядок шляху, у тому числі пробіли.
        if (line == null) throw new IOException("Введення завершено."); // EOF під час запиту не є коректною відповіддю.
        return line; // Повертаємо введений текст.
    }
    public static List<String> words(Path path) throws IOException { // Читаємо файл послідовно в стандартний контейнер.
        List<String> result = new ArrayList<>(); // Кожне входження слова зберігається окремо.
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) { // try-with-resources закриє файл навіть при помилці.
            String line; // Тимчасова змінна для поточного рядка.
            while ((line = reader.readLine()) != null) { // null позначає фізичний кінець файлу.
                for (String word : line.toLowerCase(Locale.ROOT).split("[\\s.,:;]+")) { // Відкидаємо пробіли та саме перелічені в умові розділові знаки.
                    if (!word.isEmpty()) result.add(word); // Порожній фрагмент на початку рядка не є словом.
                }
            }
        }
        Collections.sort(result); // Природний лексикографічний порядок Java за Unicode.
        return result; // Повертаємо відсортований список із повторами.
    }
    private static List<String> readWords(String prompt) throws IOException { // Запит повторюється, якщо вхідного файлу немає.
        while (true) { // Користувач може виправити шлях без перезапуску.
            String path = ask(prompt); // EOF тут передається верхньому рівню, а не запускає новий запит.
            try { return words(Path.of(path)); } // Відкриваємо та обробляємо введений файл.
            catch (IOException | java.nio.file.InvalidPathException e) { System.out.println("Не вдалося прочитати: " + e.getMessage()); } // Пояснюємо помилку й повертаємось до запиту.
        }
    }
    private static BufferedWriter output() throws IOException { // Вибираємо вихідний шлях і погоджений режим запису.
        while (true) { // Дозволяємо виправляти шлях і відмовлятися від створення.
            String raw = ask("Вихідний файл: "); // Шлях може бути абсолютним або відносним до проєкту.
            try { // Перевіряємо шлях і доступність файлової системи.
                Path path = Path.of(raw); // Створюємо платформозалежне представлення шляху.
                boolean exists = Files.exists(path); // Визначаємо, чи потрібен запит на створення.
                String mode = ask(exists ? "Файл існує: 1 - перезапис, 2 - дописати, 0 - інший шлях: " : "Створити файл? 1 - так, 0 - інший шлях: "); // Вибір явного режиму захищає від випадкового стирання.
                if (!mode.equals("1") && !(exists && mode.equals("2"))) continue; // Відмова або невідома відповідь повертає до вибору шляху.
                StandardOpenOption option = mode.equals("2") ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING; // Додавання зберігає старий вміст, перезапис очищує його.
                return Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, option); // Створюємо UTF-8 writer із вибраними опціями.
            } catch (java.nio.file.InvalidPathException e) { System.out.println("Некоректний шлях: " + e.getMessage()); } // Виправляємо синтаксис шляху.
            catch (IOException e) { // Не всі помилки відкриття можна передбачити через exists.
                if (e.getMessage().equals("Введення завершено.")) throw e; // EOF не можна виправити повторним читанням.
                System.out.println("Не вдалося відкрити вихідний файл: " + e.getMessage()); // Після помилки доступу можна вказати інший файл.
            }
        }
    }
    public static List<String> intersection(List<String> a, List<String> b) { // Визначаємо слова, які присутні в обох файлах.
        TreeSet<String> common = new TreeSet<>(a); // Копія першого списку прибирає дублікати й задає порядок.
        common.retainAll(new TreeSet<>(b)); // Залишаємо тільки елементи, присутні у другій множині.
        return new ArrayList<>(common); // Повертаємо відсортований результат без повторень.
    }
    public static void main(String[] args) { // Запускаємо одну з двох файлових задач.
        try { // Усі файлові винятки перетворюємо на повідомлення користувачу.
            String task = ask("Завдання (1 - спільні слова, 2 - посимвольний запис): "); // Обираємо режим.
            if (task.equals("1")) { // Порівняння слів із двох текстових файлів.
                List<String> a = readWords("Перший вхідний файл: "); // Читаємо й сортуємо слова першого файлу.
                List<String> b = readWords("Другий вхідний файл: "); // Читаємо й сортуємо слова другого файлу.
                System.out.println("Перший список: " + a + "\nДругий список: " + b); // Друкуємо обидва списки з повтореннями за умовою.
                try (BufferedWriter writer = output()) { // Відкриваємо результат у вибраному користувачем режимі.
                    for (String word : intersection(a, b)) { // Записуємо кожне спільне слово один раз.
                        writer.write(word); // Записуємо слово без автоматичного переходу рядка.
                        writer.newLine(); // Кожне слово займає окремий рядок.
                    }
                }
                System.out.println("Спільні слова записано."); // Повідомлення після успішного закриття файлу.
            } else if (task.equals("2")) { // Посимвольний запис із консолі за другим завданням.
                try (BufferedWriter writer = output()) { // Файл відкриваємо до початку текстового введення.
                    System.out.println("Вводьте текст. Завершення: EOF (macOS/IntelliJ: Ctrl+D; Windows CMD: Ctrl+Z, Enter)."); // Ctrl+Z у терміналі macOS призупиняє процес, тому тут потрібен Ctrl+D.
                    int symbol; // int дозволяє відрізнити символьне значення від маркера -1.
                    while ((symbol = CONSOLE.read()) != -1) writer.write(symbol); // Передаємо по одному UTF-16 елементу до writer до фізичного EOF.
                }
                System.out.println("Текст записано."); // Writer закрито, буфер гарантовано виведений.
            } else System.out.println("Оберіть завдання 1 або 2."); // Невідомий номер не запускає запис.
        } catch (IOException e) { System.out.println("Помилка: " + e.getMessage()); } // Обробляємо відмову доступу, помилки читання й EOF у запитах.
    }
}
