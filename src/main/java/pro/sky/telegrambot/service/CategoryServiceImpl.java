package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.exception.CategoryExistsException;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с иерархией категорий.
 * <p>
 * Класс предоставляет функционал для управления древовидной структурой категорий:
 * <ul>
 *     <li>Добавление корневых и дочерних категорий</li>
 *     <li>Удаление категорий (с каскадным удалением дочерних)</li>
 *     <li>Формирование текстового представления дерева категорий</li>
 * </ul>
 * <p>
 * Все операции выполняются в транзакционном контексте ({@code @Transactional}).
 *
 * @see Service Аннотация Spring, обозначающая класс как сервис
 * @see Transactional Аннотация для управления транзакциями
 * @see CategoryService Интерфейс, который реализует данный сервис
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Конструктор с внедрением зависимости CategoryRepository.
     *
     * @param categoryRepository репозиторий для работы с категориями в БД
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализация:
     * <ol>
     *     <li>Проверяет существование категории с таким именем</li>
     *     <li>Создает новую категорию</li>
     *     <li>Сохраняет в базу данных</li>
     * </ol>
     *
     * @throws CategoryExistsException если категория с таким именем уже существует
     */
    @Override
    @Transactional
    public void addRootCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CategoryExistsException("Категория \"" + name + "\" уже существует");
        }
        Category category = new Category(name);
        categoryRepository.save(category);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализация:
     * <ol>
     *     <li>Проверяет существование родительской категории</li>
     *     <li>Проверяет отсутствие дочерней категории с таким именем</li>
     *     <li>Создает новую категорию и устанавливает родительскую связь</li>
     *     <li>Сохраняет изменения</li>
     * </ol>
     *
     * @throws CategoryNotFoundException если родительская категория не найдена
     * @throws CategoryExistsException   если дочерняя категория уже существует
     */
    @Override
    @Transactional
    public void addChildCategory(String parentName, String childName) {
        Optional<Category> verifiable = categoryRepository.findByName(parentName);
        if (verifiable.isEmpty()) {
            throw new CategoryNotFoundException("Родительская категория \"" + parentName + "\" не найдена");
        }
        if (categoryRepository.existsByName(childName)) {
            throw new CategoryExistsException("Дочерняя категория \"" + childName + "\" уже существует");
        }

        Category parent = verifiable.get();
        Category child = new Category(childName);
        child.setParent(parent);
        parent.getChildren().add(child);
        categoryRepository.save(child);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Реализация:
     * <ol>
     *     <li>Проверяет существование категории</li>
     *     <li>Удаляет категорию (каскадное удаление дочерних категорий)</li>
     * </ol>
     *
     * @throws CategoryNotFoundException если категория не найдена
     */
    @Override
    @Transactional
    public void removeCategory(String name) {
        Optional<Category> deleteCategory = categoryRepository.findByName(name);
        if (deleteCategory.isEmpty()) {
            throw new CategoryNotFoundException("Категория \"" + name + "\" не найдена");
        }
        categoryRepository.delete(deleteCategory.get());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Формат вывода:
     * <pre>
     * Дерево категорий:
     * - Родительская категория
     *   - Дочерняя категория 1
     *   - Дочерняя категория 2
     * - Другая родительская категория
     * </pre>
     *
     * @throws CategoryTreeIsEmptyException если в базе нет категорий
     */
    @Override
    @Transactional
    public String viewTree() {
        List<Category> roots = categoryRepository.findAllByParentIsNull();
        if (roots.isEmpty()) {
            throw new CategoryTreeIsEmptyException("Дерево категорий пусто.");
        }
        StringBuilder sb = new StringBuilder("Дерево категорий:\n");
        for (Category parent : roots) {
            buildTreeCategory(parent, 0, sb);
        }
        return sb.toString();
    }

    /**
     * Рекурсивно строит строковое представление дерева категорий.
     *
     * @param category текущая категория для обработки
     * @param indent   уровень вложенности (для отступов)
     * @param sb       StringBuilder для накопления результата
     */
    private void buildTreeCategory(Category category, int indent, StringBuilder sb) {
        sb.append("  ".repeat(indent)).append("- ").append(category.getName()).append("\n");
        List<Category> children = category.getChildren();
        if (children != null) {
            for (Category child : children) {
                buildTreeCategory(child, indent + 1, sb);
            }
        }
    }
}


