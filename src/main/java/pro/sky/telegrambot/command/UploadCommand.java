package pro.sky.telegrambot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.exception.InvalidExcelFormatException;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;
import pro.sky.telegrambot.service.ExcelProcessingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Команда для загрузки категорий из Excel-файла.
 * <p>
 * Обрабатывает загруженные пользователем Excel-файлы, содержащие структуру категорий,
 * и сохраняет их в базу данных. Поддерживает обработку родительских связей между категориями.
 * <p>
 * Формат команды: {@code /upload} (после чего пользователь должен отправить Excel-файл)
 * <p>
 * Рабочий процесс:
 * <ol>
 *     <li>Пользователь отправляет команду /upload</li>
 *     <li>Бот запрашивает Excel-файл</li>
 *     <li>После получения файла парсит его и сохраняет категории</li>
 * </ol>
 *
 * @see Command Базовый интерфейс команд
 * @see ExcelProcessingService Сервис для обработки Excel-файлов
 * @see InvalidExcelFormatException Исключение при неверном формате файла
 */
@Component
public class UploadCommand implements Command {

    private final ExcelProcessingService excelProcessingService;

    private final TelegramBot telegramBot;

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param workingWithExcelService сервис для работы с Excel
     * @param telegramBot             клиент Telegram бота
     * @param categoryRepository      репозиторий для работы с категориями
     */
    public UploadCommand(ExcelProcessingService workingWithExcelService,
                         TelegramBot telegramBot, CategoryRepository categoryRepository) {
        this.excelProcessingService = workingWithExcelService;
        this.telegramBot = telegramBot;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Возвращает имя команды UPLOAD.
     *
     * @return имя команды (UPLOAD)
     */
    @Override
    public NamesCommand getNameCommand() {
        return NamesCommand.UPLOAD;
    }

    /**
     * Обрабатывает команду /upload, запрашивая у пользователя Excel-файл.
     *
     * @param chatId    идентификатор чата
     * @param arguments аргументы команды
     * @return сообщение с запросом на отправку файла
     */
    @Override
    public SendMessage execute(Long chatId, List<String> arguments) {
        if (arguments.size() > 1) {
            return new SendMessage(chatId,
                    "Неверный формат команды! Используйте: /upload");
        }
        return new SendMessage(chatId,
                "Пожалуйста, отправьте Excel-файл с категориями.");
    }

    /**
     * Обрабатывает полученный Excel-документ с категориями.
     * <p>
     * Выполняет:
     * <ol>
     *     <li>Скачивание файла</li>
     *     <li>Парсинг содержимого</li>
     *     <li>Сохранение категорий в базу данных</li>
     * </ol>
     *
     * @param chatId  идентификатор чата
     * @param message сообщение с прикрепленным файлом
     */
    public void handleDocumentExcel(Long chatId, Message message) {
        try {
            byte[] fileContent = downloadFile(message);
            List<Category> categories = excelProcessingService.parseExcel(fileContent);
            saveCategories(categories);
            telegramBot.execute(new SendMessage(chatId, "Таблица с категориями успешно загружена!"));
        } catch (IOException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Ошибка чтения файла: " + e.getMessage()));
        } catch (InvalidExcelFormatException e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Неверный формат Excel-файла: " + e.getMessage()));
        } catch (Exception e) {
            telegramBot.execute(new SendMessage(chatId,
                    "Ошибка при загрузке категорий: " + e.getMessage()));
        }
    }

    /**
     * Скачивает содержимое файла из Telegram.
     *
     * @param message сообщение с прикрепленным файлом
     * @return содержимое файла в виде byte[]
     * @throws IOException если произошла ошибка при скачивании файла
     */
    private byte[] downloadFile(Message message) throws IOException {
        String fileId = message.document().fileId();
        File file = telegramBot.execute(new GetFile(fileId)).file();
        return telegramBot.getFileContent(file);
    }

    /**
     * Сохраняет список категорий в базу данных с учетом родительских связей.
     * <p>
     * Логика работы:
     * <ul>
     *     <li>Проверяет существующие категории</li>
     *     <li>Избегает дублирования</li>
     *     <li>Устанавливает родительские связи</li>
     *     <li>Сохраняет все изменения</li>
     * </ul>
     *
     * @param newCategories список новых категорий для сохранения
     */
    @Transactional
    public void saveCategories(List<Category> newCategories) {
//    Сначала находим существующие категории по именам
        List<String> categoryNames = newCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        Map<String, Category> existingCategories = categoryRepository.findByNameIn(categoryNames)
                .stream()
                .collect(Collectors.toMap(Category::getName, Function.identity()));
//    Подготовка данных для сохранения
        List<Category> categoriesToSave = new ArrayList<>();
        Map<Long, Category> tempMap = new HashMap<>();
        Map<String, Category> nameToCategoryMap = new HashMap<>();
//    Обработка новых категорий с проверкой дубликатов
        for (Category newCat : newCategories) {
            //Проверяем, существует ли категория с таким именем
            if (existingCategories.containsKey(newCat.getName())) {
                // Если существует, используем существующую
                Category existingCat = existingCategories.get(newCat.getName());
                tempMap.put(newCat.getId(), existingCat);
                nameToCategoryMap.put(existingCat.getName(), existingCat);
                continue;
            }
            // Если не существует, создаем новую
            Category categoryToSave = new Category();
            categoryToSave.setName(newCat.getName());
            categoryToSave.setParent(null); // Временно null
            categoriesToSave.add(categoryToSave);
            tempMap.put(newCat.getId(), categoryToSave);
            nameToCategoryMap.put(newCat.getName(), categoryToSave);
        }
//    Сохраняем новые категории (без дубликатов)
        if (!categoriesToSave.isEmpty()) {
            categoryRepository.saveAll(categoriesToSave);
        }
//    Устанавливаем родительские связи
        for (Category newCat : newCategories) {
            Category savedCat = tempMap.get(newCat.getId());

            if (newCat.getParent() != null) {
                Category parent = tempMap.get(newCat.getParent().getId());
                if (parent != null) {
                    savedCat.setParent(parent);
                }
            }
        }
//    Сохраняем обновленные категории
        categoryRepository.saveAll(nameToCategoryMap.values());
    }
}







