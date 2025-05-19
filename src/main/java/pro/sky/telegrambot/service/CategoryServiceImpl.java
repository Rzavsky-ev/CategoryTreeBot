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
 * Сервис для работы с древовидной структурой категорий.
 * Позволяет добавлять, удалять категории и отображать их в виде дерева.
 *
 * <p>Категории могут быть:
 * <ul>
 *   <li>Корневыми (без родителя)</li>
 *   <li>Дочерними (с указанием родительской категории)</li>
 * </ul>
 * Поддерживается проверка уникальности имён категорий.</p>
 * *
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public void addRootCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CategoryExistsException("Категория с таким названием уже существует " +
                    name);
        }
        Category category = new Category(name);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void addChildCategory(String parentName, String childName) {
        Optional<Category> verifiable = categoryRepository.findByName(parentName);
        if (verifiable.isEmpty()) {
            throw new CategoryNotFoundException("Родительская категория не найдена");
        }
        if (categoryRepository.existsByName(childName)) {
            throw new CategoryExistsException("Дочерняя категория уже существует");
        }

        Category parent = verifiable.get();
        Category child = new Category(childName);
        child.setParent(parent);
        parent.getChildren().add(child);
        categoryRepository.save(child);
    }

    @Override
    @Transactional
    public void removeCategory(String name) {
        Optional<Category> deleteCategory = categoryRepository.findByName(name);
        if (deleteCategory.isEmpty()) {
            throw new CategoryNotFoundException("Категория не найдена");
        }
        categoryRepository.delete(deleteCategory.get());
    }

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
     * Рекурсивно показывает дерево категорий
     *
     * @param category - текущая категория
     * @param indent   - отступ
     * @param sb       - StringBuilder для накопления результата
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


