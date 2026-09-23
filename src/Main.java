import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
public class Main {
    // private — поле закрите ззовні; final забороняє переприсвоєння, але не зміну вмісту об’єкта.
    // InputStreamReader декодує байти в символи, BufferedReader додає буфер і readLine.
    private static final BufferedReader CONSOLE = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    // static — виклик без об’єкта; public — доступ ззовні, private — лише в класі; void — без результату.
    // throws оголошує перевірюваний виняток: викликач мусить перехопити його або теж оголосити.
    private static String ask(String prompt) throws IOException {
        System.out.print(prompt);
        // readLine повертає null при EOF; порожній рядок "" означає прочитаний порожній рядок.
        String line = CONSOLE.readLine();
        if (line == null) throw new IOException("Введення завершено.");
        return line;
    }
    public static List<String> words(Path path) throws IOException {
        // ArrayList<T> — змінний список елементів типу T; <> після new виводить тип із контексту.
        List<String> result = new ArrayList<>();
        // try (ресурс) автоматично закриває його після блоку, зокрема при помилці.
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            // Присвоєння в умові: спочатку читаємо значення, потім перевіряємо його.
            while ((line = reader.readLine()) != null) {
                // for (Тип елемент : колекція) — перебір елементів без індексу.
                // String.split приймає regex: "\\s+" — пробіли, "\\." — крапка; -1 зберігає кінцеві порожні частини.
                for (String word : line.toLowerCase(Locale.ROOT).split("[\\s.,:;]+")) {
                    if (!word.isEmpty()) result.add(word);
                }
            }
        }
        // sort без порівнювача викликає Comparable.compareTo; з порівнювачем — Comparator.compare.
        Collections.sort(result);
        return result;
    }
    private static List<String> readWords(String prompt) throws IOException {
        while (true) {
            String path = ask(prompt);
            try { return words(Path.of(path)); }
            // catch (Тип1 | Тип2 e) — один обробник для кількох типів винятків.
            catch (IOException | java.nio.file.InvalidPathException e) { System.out.println("Не вдалося прочитати: " + e.getMessage()); }
        }
    }
    private static BufferedWriter output() throws IOException {
        while (true) {
            String raw = ask("Вихідний файл: ");
            try {
                Path path = Path.of(raw);
                boolean exists = Files.exists(path);
                // умова ? a : b — вибір значення: a, якщо true, інакше b.
                String mode = ask(exists ? "Файл існує: 1 - перезапис, 2 - дописати, 0 - інший шлях: " : "Створити файл? 1 - так, 0 - інший шлях: ");
                // equals порівнює вміст; == для об’єктів Java перевіряє тотожність посилань.
                if (!mode.equals("1") && !(exists && mode.equals("2"))) continue;
                StandardOpenOption option = mode.equals("2") ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;
                return Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, option);
            } catch (java.nio.file.InvalidPathException e) { System.out.println("Некоректний шлях: " + e.getMessage()); }
            catch (IOException e) {
                if (e.getMessage().equals("Введення завершено.")) throw e;
                System.out.println("Не вдалося відкрити вихідний файл: " + e.getMessage());
            }
        }
    }
    public static List<String> intersection(List<String> a, List<String> b) {
        // TreeSet — множина з упорядкованим перебором.
        TreeSet<String> common = new TreeSet<>(a);
        common.retainAll(new TreeSet<>(b));
        return new ArrayList<>(common);
    }
    // main — точка входу; String[] args містить аргументи запуску без назви програми.
    public static void main(String[] args) {
        try {
            String task = ask("Завдання (1 - спільні слова, 2 - посимвольний запис): ");
            if (task.equals("1")) {
                List<String> a = readWords("Перший вхідний файл: ");
                List<String> b = readWords("Другий вхідний файл: ");
                System.out.println("Перший список: " + a + "\nДругий список: " + b);
                try (BufferedWriter writer = output()) {
                    for (String word : intersection(a, b)) {
                        writer.write(word);
                        writer.newLine();
                    }
                }
                System.out.println("Спільні слова записано.");
            } else if (task.equals("2")) {
                try (BufferedWriter writer = output()) {
                    System.out.println("Вводьте текст. Завершення: EOF (macOS/IntelliJ: Ctrl+D; Windows CMD: Ctrl+Z, Enter).");
                    int symbol;
                    // read повертає UTF-16 одиницю як int або -1 при EOF; char не вміщує маркер -1.
                    while ((symbol = CONSOLE.read()) != -1) writer.write(symbol);
                }
                System.out.println("Текст записано.");
            } else System.out.println("Оберіть завдання 1 або 2.");
        } catch (IOException e) { System.out.println("Помилка: " + e.getMessage()); }
    }
}
