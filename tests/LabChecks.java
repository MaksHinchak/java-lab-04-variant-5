public class LabChecks { // Автономні перевірки без зовнішніх бібліотек тестування.
    private static int count = 0; // Лічильник успішно перевірених умов.
    private static void check(boolean value) { // Допоміжний метод перетворює хибну умову на провал тесту.
        if (!value) throw new AssertionError("Перевірка " + (count + 1) + " не пройшла."); // Зупиняємо тест із ненульовим кодом процесу.
        count++; // Рахуємо лише успішні перевірки.
    }
    private static void near(double actual, double expected) { // Порівнюємо double з допуском на округлення.
        check(Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected))); // Допуск враховує масштаб очікуваного числа.
    }
    private interface Action { void run() throws Exception; } // Лямбда тесту може породжувати і перевірювані винятки.
    private static void expect(Class<? extends Throwable> type, Action action) { // Перевіряємо, що помилкові дані дають саме потрібний вид помилки.
        try { action.run(); } // Виконуємо потенційно помилкову операцію.
        catch (Throwable error) { check(type.isInstance(error)); return; } // Неправильний тип винятку теж провалює тест.
        throw new AssertionError("Очікували " + type.getSimpleName()); // Відсутність потрібного винятку є помилкою реалізації.
    }
    public static void main(String[] args) throws Exception { // Метод запускає усі перевірки цієї лабораторної.
        java.nio.file.Path file = java.nio.file.Files.createTempFile("lab4-check", ".txt"); // Ізолюємо перевірку від користувацьких файлів.
        try { // Тимчасовий файл буде прибрано незалежно від результату тесту.
            java.nio.file.Files.writeString(file, "Java, КОД. java; файл\n"); // Текст перевіряє регістр, повтори та різні роздільники.
            check(Main.words(file).equals(java.util.List.of("java","java","код","файл"))); // Список має зберігати повтори й сортуватися.
            check(Main.intersection(Main.words(file), java.util.List.of("java","код","інше")).equals(java.util.List.of("java","код"))); // Перетин не містить повторного java.
            check(Main.intersection(java.util.List.of(), java.util.List.of("a")).isEmpty()); // Порожня множина не має спільних слів.
        } finally { java.nio.file.Files.deleteIfExists(file); } // Прибираємо лише створений цим тестом файл.
        System.out.println("OK: " + count + " перевірок"); // Видимий підсумок після успішного виконання.
    }
}
