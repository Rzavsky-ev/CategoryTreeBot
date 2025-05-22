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
 * Тестовый класс для проверки функциональности {@link CategoryServiceImpl}.
 *
 * <p>Проверяет основные сценарии работы с категориями:</p>
 * <ul>
 *   <li>Добавление корневых и дочерних категорий</li>
 *   <li>Удаление категорий</li>
 *   <li>Просмотр иерархии категорий</li>
 * </ul>
 *
 * <p>Использует Mockito для мокирования зависимостей.</p>
 *
 * @see CategoryService
 * @see CategoryRepository
 */
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepositoryMock;

    @InjectMocks
    private CategoryServiceImpl categoryServiceTest;

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

    @Test
    public void addRootCategoryExisting() {
        String messageException = "Категория с таким названием уже существует ";
        String nameCategory = "Test";

        when(categoryRepositoryMock.existsByName(nameCategory)).thenReturn(true);

        Exception exception = assertThrows(CategoryExistsException.class, () -> {
            categoryServiceTest.addRootCategory(nameCategory);
        });

        verify(categoryRepositoryMock, never()).save(any());
        assertEquals(messageException + nameCategory, exception.getMessage());
    }

    @Test
    public void addNonExistentChildCategoryParentExists() {

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

    @Test
    public void addNonExistentChildCategoryParentNotFound() {
        String childName = "Test";
        String parentName = "Parent";
        String messageException = "Родительская категория не найдена";
        when(categoryRepositoryMock.findByName(parentName)).
                thenReturn(Optional.empty());

        Exception exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryServiceTest.addChildCategory(parentName, childName);
        });

        verify(categoryRepositoryMock, never()).save(any());
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    public void addExistingChildCategoryParentExists() {
        String childName = "Test";
        String parentName = "Parent";
        String messageException = "Дочерняя категория уже существует";
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

    @Test
    public void removeNotFoundCategory() {
        String nameCategory = "Test";
        String messageException = "Категория не найдена";

        when(categoryRepositoryMock.findByName(nameCategory)).
                thenReturn(Optional.empty());

        Exception exception = assertThrows(CategoryNotFoundException.class, () -> {
            categoryServiceTest.removeCategory(nameCategory);
        });

        verify(categoryRepositoryMock, never()).delete(any());
        verify(categoryRepositoryMock).findByName(nameCategory);
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    public void viewNonEmptyTree() {
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

