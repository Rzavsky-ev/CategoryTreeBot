package pro.sky.telegrambot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.exception.CategoryExistsException;
import pro.sky.telegrambot.exception.CategoryNotFoundException;
import pro.sky.telegrambot.exception.CategoryTreeIsEmptyException;
import pro.sky.telegrambot.model.Category;
import pro.sky.telegrambot.repository.CategoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для {@link CategoryServiceImpl}, проверяющие логику работы с категориями.
 * <p>
 * Тесты покрывают следующие сценарии:
 * <ul>
 *   <li>Добавление корневых и дочерних категорий</li>
 *   <li>Удаление категорий</li>
 *   <li>Просмотр дерева категорий</li>
 *   <li>Обработку ошибочных ситуаций</li>
 * </ul>
 *
 * <p>Использует Mockito для:
 * <ul>
 *   <li>Мокирования {@link CategoryRepository}</li>
 *   <li>Проверки взаимодействия с репозиторием</li>
 *   <li>Тестирования исключительных ситуаций</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private CategoryServiceImpl categoryServiceTest;

    /**
     * Тестирует добавление новой корневой категории.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Проверку существования категории</li>
     *   <li>Сохранение категории с правильными параметрами</li>
     *   <li>Отсутствие родителя у корневой категории</li>
     * </ul>
     */
    @Test
    public void addRootCategoryNotExisting() {
        String nameCategory = "Test";

        when(categoryRepositoryMock.existsByName(nameCategory)).thenReturn(false);

        categoryServiceTest.addRootCategory(nameCategory);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepositoryMock).save(captor.capture());
        verify(categoryRepositoryMock).existsByName(nameCategory);

        Category saved = captor.getValue();
        assertEquals(nameCategory, saved.getName());
        assertNull(saved.getParent());
    }

    /**
     * Тестирует попытку добавления существующей корневой категории.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Выброс исключения {@link CategoryExistsException}</li>
     *   <li>Корректность сообщения об ошибке</li>
     *   <li>Отсутствие вызова save()</li>
     * </ul>
     */
    @Test
    public void addRootCategoryExisting() {
        String messageException = "Категория \"a\" уже существует";
        String nameCategory = "a";

        when(categoryRepositoryMock.existsByName(nameCategory)).thenReturn(true);

        Exception exception = assertThrows(CategoryExistsException.class, () -> {
            categoryServiceTest.addRootCategory(nameCategory);
        });

        verify(categoryRepositoryMock, never()).save(any());
        assertEquals(messageException, exception.getMessage());
    }

    /**
     * Тестирует добавление дочерней категории к существующему родителю.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Поиск родительской категории</li>
     *   <li>Проверку существования дочерней категории</li>
     *   <li>Корректное сохранение с установленным родителем</li>
     * </ul>
     */
    @Test
    public void addNotExistentChildCategoryParentExists() {

        String childName = "Test";
        String parentName = "Parent";
        Category parent = new Category(parentName);

        when(categoryRepositoryMock.findByName(parentName)).
                thenReturn(Optional.of(parent));
        when(categoryRepositoryMock.existsByName(childName)).thenReturn(false);

        categoryServiceTest.addChildCategory(parentName, childName);
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepositoryMock).save(captor.capture());
        verify(categoryRepositoryMock).existsByName(childName);
        verify(categoryRepositoryMock).findByName(parentName);

        Category savedChild = captor.getValue();
        assertEquals(childName, savedChild.getName());
        assertNotNull(savedChild.getParent());
        assertEquals(parentName, savedChild.getParent().getName());
    }

    /**
     * Тестирует добавление дочерней категории к несуществующему родителю.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Выброс исключения {@link CategoryNotFoundException}</li>
     *   <li>Корректность сообщения об ошибке</li>
     *   <li>Отсутствие вызова save()</li>
     * </ul>
     */
    @Test
    public void addNotExistentChildCategoryParentNotFound() {
        String childName = "Child";
        String parentName = "Parent";
        String messageException = "Родительская категория \"Parent\" не найдена";
        when(categoryRepositoryMock.findByName(parentName)).
                thenReturn(Optional.empty());

        Exception exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryServiceTest.addChildCategory(parentName, childName);
        });

        verify(categoryRepositoryMock, never()).save(any());
        assertEquals(messageException, exception.getMessage());
    }

    /**
     * Тестирует добавление существующей дочерней категории.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Выброс исключения {@link CategoryExistsException}</li>
     *   <li>Корректность сообщения об ошибке</li>
     *   <li>Проверку существования категорий</li>
     *   <li>Отсутствие вызова save()</li>
     * </ul>
     */
    @Test
    public void addExistingChildCategoryParentExists() {
        String childName = "Child";
        String parentName = "Parent";
        String messageException = "Дочерняя категория \"Child\" уже существует";
        Category parent = new Category(parentName);

        when(categoryRepositoryMock.findByName(parentName)).
                thenReturn(Optional.of(parent));
        when(categoryRepositoryMock.existsByName(childName)).thenReturn(true);

        Exception exception = assertThrows(CategoryExistsException.class, () -> {
            categoryServiceTest.addChildCategory(parentName, childName);
        });

        verify(categoryRepositoryMock).findByName(parentName);
        verify(categoryRepositoryMock).existsByName(childName);
        verify(categoryRepositoryMock, never()).save(any());
        assertEquals(messageException, exception.getMessage());
    }

    /**
     * Тестирует удаление существующей категории.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Поиск категории по имени</li>
     *   <li>Корректное удаление категории</li>
     * </ul>
     */
    @Test
    public void removeExistingCategory() {
        String nameCategory = "Test";

        Category category = new Category(nameCategory);

        when(categoryRepositoryMock.findByName(nameCategory)).
                thenReturn(Optional.of(category));

        categoryServiceTest.removeCategory(nameCategory);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepositoryMock).delete(captor.capture());
        verify(categoryRepositoryMock).findByName(nameCategory);

        Category remove = captor.getValue();
        assertEquals(nameCategory, remove.getName());

        verify(categoryRepositoryMock).findByName(nameCategory);
        verify(categoryRepositoryMock).delete(category);
    }

    /**
     * Тестирует удаление несуществующей категории.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Выброс исключения {@link CategoryNotFoundException}</li>
     *   <li>Корректность сообщения об ошибке</li>
     *   <li>Отсутствие вызова delete()</li>
     * </ul>
     */
    @Test
    public void removeNotFoundCategory() {
        String nameCategory = "Test";
        String messageException = "Категория \"Test\" не найдена";

        when(categoryRepositoryMock.findByName(nameCategory)).
                thenReturn(Optional.empty());

        Exception exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryServiceTest.removeCategory(nameCategory);
        });

        verify(categoryRepositoryMock, never()).delete(any());
        verify(categoryRepositoryMock).findByName(nameCategory);
        assertEquals(messageException, exception.getMessage());
    }

    /**
     * Тестирует просмотр непустого дерева категорий.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Поиск корневых категорий</li>
     *   <li>Формирование корректного строкового представления</li>
     *   <li>Иерархию родитель-потомок</li>
     * </ul>
     */
    @Test
    public void viewNotEmptyTree() {
        Category parent = new Category("Parent");
        Category child1 = new Category("Child1");
        Category child2 = new Category("Child2");

        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChildren().addAll(List.of(child1, child2));

        when(categoryRepositoryMock.findAllByParentIsNull()).thenReturn(List.of(parent));

        String result = categoryServiceTest.viewTree();

        String expected = """
                Дерево категорий:
                - Parent
                  - Child1
                  - Child2
                """;

        assertEquals(expected, result);
    }

    /**
     * Тестирует попытку просмотра пустого дерева категорий.
     * <p>
     * Проверяет:
     * <ul>
     *   <li>Выброс исключения {@link CategoryTreeIsEmptyException}</li>
     *   <li>Корректность сообщения об ошибке</li>
     * </ul>
     */
    @Test
    public void viewNotFoundTree() {
        String messageException = "Дерево категорий пусто.";

        when(categoryRepositoryMock.findAllByParentIsNull()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(CategoryTreeIsEmptyException.class, () -> {
            categoryServiceTest.viewTree();
        });

        verify(categoryRepositoryMock).findAllByParentIsNull();
        assertEquals(messageException, exception.getMessage());
    }
}

