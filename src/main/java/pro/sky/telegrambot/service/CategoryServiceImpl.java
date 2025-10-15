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
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Добавляет корневую категорию (без родительской категории).
     *
     * @param name название корневой категории
     * @throws CategoryExistsException если категория с указанным именем уже существует
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
     * Добавляет дочернюю категорию к указанной родительской категории.
     *
     * @param parentName имя родительской категории
     * @param childName  имя дочерней категории
     * @throws CategoryNotFoundException если родительская категория не найдена
     * @throws CategoryExistsException   если дочерняя категория с указанным именем уже существует
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
     * Удаляет категорию по имени.
     * При удалении родительской категории также удаляются все её дочерние категории (каскадное удаление).
     *
     * @param name имя категории для удаления
     * @throws CategoryNotFoundException если категория с указанным именем не найдена
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
     * Возвращает строковое представление всего дерева категорий.
     * Дерево отображается в виде иерархической структуры с отступами для вложенных категорий.
     *
     * @return строковое представление дерева категорий
     * @throws CategoryTreeIsEmptyException если дерево категорий пустое
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


